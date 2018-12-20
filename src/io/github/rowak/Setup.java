package io.github.rowak;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;

import io.github.rowak.StatusCodeException.InternalServerErrorException;
import io.github.rowak.StatusCodeException.UnauthorizedException;

/**
 * A utility class with useful Aurora management methods.
 */
public class Setup
{
	public static int DEFAULT_PORT = 16021;
	
	/**
	 * Searches for Aurora devices on the local network using SSDP.
	 * @param timeout  the amount of time (in milliseconds) to spend searching for Aurora devices
	 * @return  a collection of type <code>InetSocketAddress</code>,
	 * 			with each element representing an Aurora controller
	 * @throws IOException  unknown IO exception
	 * @throws SocketTimeoutException  (inevitable) once the socket's timeout reaches <code>timeout</code>
	 * @throws UnknownHostException  if the host's local address cannot be found
	 */
	public static List<InetSocketAddress> findAuroras(int timeout)
			throws IOException, SocketTimeoutException, UnknownHostException
	{
		List<InetSocketAddress> auroras = new ArrayList<InetSocketAddress>();
		
		StringBuilder request = new StringBuilder();
		request.append("M-SEARCH * HTTP/1.1\r\n");
		request.append(String.format("HOST: %s:%s\r\n", "239.255.255.250", 1900));
		request.append("ST: nanoleaf_aurora:light\r\n");
		request.append("MAN: \"ssdp:discover\"\r\n");
		request.append("MX: 2");
		request.append("\r\n");
		
		final String IP = "239.255.255.250";
		final int PORT = 1900;
		
		InetSocketAddress host = new InetSocketAddress(InetAddress.getLocalHost(), 1901);
		InetSocketAddress ssdpAddress = new InetSocketAddress(IP, PORT);
		
		byte[] bytes = request.toString().getBytes();
		DatagramPacket requestPacket = new DatagramPacket(bytes, bytes.length, ssdpAddress);
		
		MulticastSocket multicast = null;
		try
		{
			multicast = new MulticastSocket(null);
			multicast.bind(host);
			multicast.setTimeToLive(4);
			multicast.setSoTimeout(timeout);
			multicast.send(requestPacket);
		}
		catch (SocketException se)
		{
			throw new SocketException("Failed to bind to host.");
		}
		catch (IOException ioe)
		{
			throw new IOException("Failed to send request packet.");
		}
		finally
		{
			multicast.disconnect();
			multicast.close();
		}
		
		DatagramSocket socket = null;
		DatagramPacket responsePacket = null;
		try
		{
			socket = new DatagramSocket(1901);
			socket.setSoTimeout(timeout);
			
			while (true)
			{
				try
				{
					responsePacket = new DatagramPacket(new byte[1536], 1536);
					socket.receive(responsePacket);
					String data = new String(responsePacket.getData());
					String[] address = data.substring(data.indexOf("Location:")+17,
							data.indexOf("nl-deviceid:")-2).split(":");
					String ip = address[0];
					int port = Integer.parseInt(address[1]);
					auroras.add(new InetSocketAddress(ip, port));
				}
				catch (NumberFormatException nfe)
				{
					throw new NumberFormatException("Malformed response packet.");
				}
				catch (IOException ioe)
				{
					break;
				}
			}
		}
		catch (SocketException se)
		{
			throw new SocketException("Failed to receive response packet.");
		}
		finally
		{
			if (socket != null)
			{
				socket.disconnect();
				socket.close();
			}
		}
		return auroras;
	}
	
	/**
	 * Creates a unique authentication token that exists until it is destroyed
	 * using the <code>destroyAccessToken()</code> method.
	 * @param host  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: /api/v1/)
	 * @return a unique authentication token
	 * @throws StatusCodeException  if the response status code not 2xx
	 */
	public static String createAccessToken(String host,
			int port, String apiLevel) throws StatusCodeException
	{
		HttpRequest req = HttpRequest.post(String.format("http://%s:%d/api/%s/new",
				host, port, apiLevel));
		String body = req.body();
		if (req.code() == 200)
		{
			JSONObject json = new JSONObject(body);
			return json.getString("auth_token");
		}
		else
		{
			Setup.checkStatusCode(req.code());
			return null;
		}
	}
	
	/**
	 * Permanently destroys an authentication token.
	 * @param host  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: /api/v1/)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws InternalServerErrorException  something went wrong inside the Aurora
	 */
	public static void destroyAccessToken(String host,
			int port, String apiLevel, String accessToken)
					throws StatusCodeException, UnauthorizedException, InternalServerErrorException
	{
		HttpRequest req = HttpRequest.delete(String.format("http://%s:%d/api/%s/%s",
				host, port, apiLevel, accessToken));
		Setup.checkStatusCode(req.code());
	}
	
	/**
	 * Checks for error status codes.
	 * @param code  the response status code
	 * @throws StatusCodeException  if <code>code</code> matches an error status code
	 */
	private static void checkStatusCode(int code)
			throws StatusCodeException
	{
		switch (code)
		{
			case 400:
				throw new StatusCodeException().new BadRequestException();
			case 401:
				throw new StatusCodeException().new UnauthorizedException();
			case 403:
				throw new StatusCodeException().new ForbiddenException();
			case 404:
				throw new StatusCodeException().new ResourceNotFoundException();
			case 422:
				throw new StatusCodeException().new UnprocessableEntityException();
			case 500:
				throw new StatusCodeException().new InternalServerErrorException();
		}
	}
}
