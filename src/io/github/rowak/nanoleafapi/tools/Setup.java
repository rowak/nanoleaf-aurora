package io.github.rowak.nanoleafapi.tools;

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

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import io.github.rowak.nanoleafapi.AuroraMetadata;
import io.github.rowak.nanoleafapi.StatusCodeException;
import io.github.rowak.nanoleafapi.StatusCodeException.BadRequestException;
import io.github.rowak.nanoleafapi.StatusCodeException.ForbiddenException;
import io.github.rowak.nanoleafapi.StatusCodeException.InternalServerErrorException;
import io.github.rowak.nanoleafapi.StatusCodeException.ResourceNotFoundException;
import io.github.rowak.nanoleafapi.StatusCodeException.UnauthorizedException;
import io.github.rowak.nanoleafapi.StatusCodeException.UnprocessableEntityException;

/**
 * A utility class with useful Aurora management methods.
 */
public class Setup
{
	public static int DEFAULT_PORT = 16021;
	
	/**
	 * Searches for Aurora devices on the local network using SSDP.
	 * @param timeout  the amount of time (in milliseconds) to spend searching for Aurora devices
	 * @return  a collection of type <code>AuroraMetadata</code>,
	 * 			with each element containing the metadata of an Aurora controller
	 * @throws IOException  unknown IO exception
	 * @throws SocketTimeoutException  (inevitable) once the socket's timeout reaches <code>timeout</code>
	 * @throws UnknownHostException  if the host's local address cannot be found
	 */
	public static List<AuroraMetadata> findAuroras(int timeout)
			throws IOException, SocketTimeoutException, UnknownHostException
	{
		List<AuroraMetadata> auroras = new ArrayList<AuroraMetadata>();
		
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
					AuroraMetadata metadata = AuroraMetadata.fromPacketData(data);
					auroras.add(metadata);
				}
				catch (NumberFormatException nfe)
				{
					throw new NumberFormatException("Malformed response packet. Bad port.");
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
	 * Gets Aurora devices on the local network using the nanoleaf internal api.<br>
	 * <b>Warning: This method of finding auroras uses the Nanoleaf backend api and
	 * is not officially supported or documented by Nanoleaf.</b>
	 * @return  a collection of type <code>InetSocketAddress</code>,
	 * 			with each element representing an Aurora controller
	 */
	public static List<InetSocketAddress> quickFindAuroras()
	{
		List<InetSocketAddress> auroras = new ArrayList<InetSocketAddress>();
		
		final String DEVICES_ENDPOINT = "https://my.nanoleaf.me/api/v1/devices/discover";
		HttpRequest request = HttpRequest.get(DEVICES_ENDPOINT);
		request.connectTimeout(10000);
		if (request.ok())
		{
			JSONArray json = new JSONArray(request.body());
			for (int i = 0; i < json.length(); i++)
			{
				JSONObject obj = json.getJSONObject(i);
				String ip = obj.getString("private_ipv4");
				int port = DEFAULT_PORT;
				InetSocketAddress addr = new InetSocketAddress(ip, port);
				auroras.add(addr);
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
	 * @throws HttpRequestException  if aurora cannot be reached/ 
	 * 								 connection times out (timeout=5000ms)
	 */
	public static String createAccessToken(String host,
			int port, String apiLevel) throws StatusCodeException
	{
		HttpRequest req = HttpRequest.post(String.format("http://%s:%d/api/%s/new",
				host, port, apiLevel));
		req.connectTimeout(5000);
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
		req.connectTimeout(5000);
		Setup.checkStatusCode(req.code());
	}
	
	/**
	 * Checks for error status codes.
	 * @param code  the response status code
	 * @throws StatusCodeException  if <code>code</code> matches an error status code
	 * @throws HttpRequestException  if aurora cannot be reached/ 
	 * 								 connection times out (timeout=5000ms)
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
