package io.github.rowak;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import io.github.rowak.StatusCodeException.ResourceNotFoundException;
import io.github.rowak.StatusCodeException.UnauthorizedException;
import io.github.rowak.StatusCodeException.UnprocessableEntityException;
import io.github.rowak.effectbuilder.CustomEffectBuilder;
import io.github.rowak.schedule.Schedule;

/**
 * The primary class in the API. Contains methods and other
 * classes for accessing and manipulating the Aurora.
 */
public class Aurora
{
	private String hostName, apiLevel, accessToken;
	private int port;
	
	private String name;
	private String serialNumber;
	private String manufacturer;
	private String firmwareVersion;
	private String model;
	
	private State state;
	private Effects effects;
	private PanelLayout panelLayout;
	private Rhythm rhythm;
	private ExternalStreaming externalStreaming;
	private Schedules schedules;
	
	/**
	 * Creates a new instance of the Aurora controller.
	 * @param hostName  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: v1)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws HttpRequestException  if the connection to the Aurora times out
	 */
	public Aurora(String hostName, int port,
			String apiLevel, String accessToken) throws StatusCodeException, 
			UnauthorizedException, HttpRequestException
	{
		init(hostName, port, apiLevel, accessToken);
	}
	
	/**
	 * Creates a new instance of the Aurora controller.
	 * @param host  the <code>InetAddress</code> of the host
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: v1)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws HttpRequestException  if the connection to the Aurora times out
	 */
	public Aurora(InetSocketAddress host,
			String apiLevel, String accessToken) throws StatusCodeException,
			UnauthorizedException, HttpRequestException
	{
		init(host.getHostName(), host.getPort(), apiLevel, accessToken);
	}
	
	/**
	 * Creates a new instance of the Aurora controller.
	 * @param metadata  the <code>AuroraMetadata</code> associated with the Aurora controller
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: v1)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws HttpRequestException  if the connection to the Aurora times out
	 */
	public Aurora(AuroraMetadata metadata,
			String apiLevel, String accessToken) throws StatusCodeException,
			UnauthorizedException, HttpRequestException
	{
		init(metadata.getHostName(), metadata.getPort(), apiLevel, accessToken);
	}
	
	/**
	 * Initialize the Aurora object and gather initial data.
	 * @param host  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI
	 * 					(for example: "v1" or "beta")
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws HttpRequestException  if the connection to the Aurora times out
	 */
	private void init(String hostName, int port,
			String apiLevel, String accessToken) throws StatusCodeException,
			UnauthorizedException, HttpRequestException
	{
		this.hostName = hostName;
		this.apiLevel = apiLevel;
		this.port = port;
		this.accessToken = accessToken;
		
		this.state = new State();
		this.effects = new Effects();
		this.panelLayout = new PanelLayout();
		this.rhythm = new Rhythm();
		this.externalStreaming = new ExternalStreaming();
		this.schedules = new Schedules();
		
		HttpRequest req = get(getURL(""));
		int code = req.code();
		checkStatusCode(code);
		String body = req.body();
		JSONObject controllerInfo = new JSONObject(body);
		this.name = controllerInfo.getString("name");
		this.serialNumber = controllerInfo.getString("serialNo");
		this.manufacturer = controllerInfo.getString("manufacturer");
		this.firmwareVersion = controllerInfo.getString("firmwareVersion");
		this.model = controllerInfo.getString("model");
	}
	
	/**
	 * Returns the Aurora's host name (IP address).
	 * @return  the host name for this Aurora
	 */
	public String getHostName()
	{
		return this.hostName;
	}
	
	/**
	 * Returns the port that the Aurora is running on.
	 * @return  the port for this Aurora
	 */
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * Returns the current API level assigned to this Aurora by the user.
	 * @return  the API level for this Aurora
	 */
	public String getApiLevel()
	{
		return this.apiLevel;
	}
	
	/**
	 * Returns the access token generated by the user for this Aurora.
	 * @return  the access token for this Aurora
	 */
	public String getAccessToken()
	{
		return this.accessToken;
	}
	
	/**
	 * Returns the Aurora's <code>State</code> object which contains
	 * methods for accessing and modifying state information.
	 * @return  the Aurora's <code>State</code> object
	 */
	public State state()
	{
		return this.state;
	}
	
	/**
	 * Returns the Aurora's <code>Effects</code> object which contains
	 * methods for accessing and modifying effects information.
	 * @return  the Aurora's <code>Effects</code> object
	 */
	public Effects effects()
	{
		return this.effects;
	}
	
	/**
	 * Returns the Aurora's <code>PanelLayout</code> object which contains
	 * methods for accessing and modifying panel layout information.
	 * @return  the Aurora's <code>PanelLayout</code> object
	 */
	public PanelLayout panelLayout()
	{
		return this.panelLayout;
	}
	
	/**
	 * Returns the Aurora's <code>Rhythm</code> object which contains
	 * methods for accessing and modifying Rhythm information.
	 * @return  the Aurora's <code>Rhythm</code> object
	 */
	public Rhythm rhythm()
	{
		return this.rhythm;
	}
	
	/**
	 * Returns the Aurora's <code>ExternalStreaming</code> object which contains
	 * methods for controlling the external streaming feature of the Aurora.
	 * @return  the Aurora's <code>ExternalStreaming</code> object
	 */
	public ExternalStreaming externalStreaming()
	{
		return this.externalStreaming;
	}
	
	/**
	 * Returns the Aurora's <code>Schedules</code> object which contains
	 * methods for managing schedules on the Aurora device.
	 * @return  the Aurora's <code>Schedules</code> object
	 */
	public Schedules schedules()
	{
		return this.schedules;
	}
	
	/**
	 * Returns the unique name of the Aurora controller.
	 * @return  the name of the Aurora controller
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the unique serial number of the Aurora controller.
	 * @return  the serial number of the Aurora controller
	 */
	public String getSerialNumber()
	{
		return this.serialNumber;
	}
	
	/**
	 * Returns the name of the manufacturer of the Aurora controller.
	 * @return  the name of the manufacturer
	 */
	public String getManufacturer()
	{
		return this.manufacturer;
	}
	
	/**
	 * Returns the firmware version of the Aurora controller.
	 * @return  the firsmware version
	 */
	public String getFirmwareVersion()
	{
		return this.firmwareVersion;
	}
	
	/**
	 * Returns the model of the Aurora controller.
	 * @return  the model of the Aurora controller
	 */
	public String getModel()
	{
		return this.model;
	}
	
	/**
	 * Causes the panels to flash in unison.
	 * This is typically used to help users differentiate between multiple panels.
	 * @return  (204 No Content, 401 Unauthorized)
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public int identify() throws StatusCodeException, UnauthorizedException
	{
		HttpRequest req = put(getURL("identify"), null);
		checkStatusCode(req.code());
		return req.code();
	}
	
	/**
	 * Contains methods for accessing and modifying Aurora state information.
	 */
	public class State
	{
		/**
		 * Gets the on state of the Aurora (true = on, false = off).
		 * @return true, if the Aurora is on
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean getOn() throws StatusCodeException
		{
			return Boolean.parseBoolean(get(getURL("state/on/value")).body());
		}
		
		/**
		 * Sets the on state of the Aurora (true = on, false = off).
		 * @param on  whether the Aurora should be turned on or off
		 * @return  (200 OK, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int setOn(boolean on)
				throws StatusCodeException, UnauthorizedException
		{
			String body = String.format("{\"on\": {\"value\": %b}}", on);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Toggles the on state of the Aurora (on = off, off = on).
		 * @return  (200 OK, 401 Unauthorized)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int toggleOn() throws StatusCodeException, UnauthorizedException
		{
			return setOn(!this.getOn());
		}
		
		/**
		 * Gets the master brightness of the Aurora.
		 * @return  the brightness of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/brightness/value")).body());
		}
		
		/**
		 * Sets the master brightness of the Aurora.
		 * @param brightness  the new brightness level as a percent
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>brightness</code> is not within the
		 * 								 		 maximum (100) and minimum (0) restrictions
		 */
		public int setBrightness(int brightness)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"brightness\": {\"value\": %d}}", brightness);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Fades the master brightness of the Aurora over a perdiod of time.
		 * @param brightness  the new brightness level as a percent
		 * @param duration  the fade time <i>in seconds</i>
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>brightness</code> is not within the
		 * 								 		 maximum (100) and minimum (0) restrictions
		 */
		public int fadeToBrightness(int brightness, int duration)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"brightness\": {\"value\": %d, \"duration\": %d}}",
					brightness, duration);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Increases the brightness by an amount as a percent.
		 * @param amount  the amount to increase by
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int increaseBrightness(int amount)
				throws StatusCodeException, UnauthorizedException
		{
			String body = String.format("{\"brightness\": {\"increment\": %d}}", amount);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Decreases the brightness by an amount as a percent.
		 * @param amount  the amount to decrease by
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int decreaseBrightness(int amount)
				throws StatusCodeException, UnauthorizedException
		{
			return increaseBrightness(-amount);
		}
		
		/**
		 * Gets the maximum brightness of the Aurora.
		 * @return  the maximum brightness
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/brightness/max")).body());
		}
		
		/**
		 * Gets the minimum brightness of the Aurora.
		 * @return  the minimum brightness
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/brightness/min")).body());
		}
		
		/**
		 * Gets the hue of the Aurora (static/custom effects only).
		 * @return  the hue of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getHue() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/hue/value")).body());
		}
		
		/**
		 * Sets the hue of the Aurora (static/custom effects only).
		 * @param hue  the new hue
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>hue</code> is not within the
		 * 										 maximum and minimum restrictions
		 */
		public int setHue(int hue)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"hue\": {\"value\": %d}}", hue);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Gets the maximum hue of the Aurora.
		 * @return  the maximum hue
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxHue() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/hue/max")).body());
		}
		
		/**
		 * Gets the minimum hue of the Aurora.
		 * @return  the minimum hue
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinHue() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/hue/min")).body());
		}
		
		/**
		 * Gets the saturation of the Aurora (static/custom effects only).
		 * @return  tue saturation of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getSaturation() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/sat/value")).body());
		}
		
		/**
		 * Sets the saturation of the Aurora (static/custom effects only).
		 * @param saturation  the new saturation
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>saturation</code> is not within
		 * 										 the maximum and minimum restrictions
		 */
		public int setSaturation(int saturation)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"sat\": {\"value\": %d}}", saturation);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Gets the maximum saturation of the Aurora.
		 * @return  the maximum saturation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxSaturation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/sat/max")).body());
		}
		
		/**
		 * Gets the minimum saturation of the Aurora.
		 * @return  the minimum saturation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinSaturation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/sat/min")).body());
		}
		
		/**
		 * Gets the color temperature of the Aurora (color temperature effect only).
		 * @return  the color temperature of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getColorTemperature()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/ct/value")).body());
		}
		
		/**
		 * Sets the color temperature of the Aurora in Kelvins.
		 * @param colorTemperature  color temperature in Kelvins
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>colorTemperature</code> is not
		 * 										 within the maximum and minimum values
		 */
		public int setColorTemperature(int colorTemperature)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"ct\": {\"value\": %d}}", colorTemperature);
			HttpRequest req = put(getURL("state"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Gets the maximum color temperature of the Aurora.
		 * @return  the maximum color temperature
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxColorTemperature()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/ct/max")).body());
		}
		
		/**
		 * Gets the minimum color temperature of the Aurora.
		 * @return  the minimum color temperature
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinColorTemperature()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("state/ct/min")).body());
		}
		
		/**
		 * Gets the color mode of the Aurora.
		 * @return  the color mode
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getColorMode()
				throws StatusCodeException, UnauthorizedException
		{
			return get(getURL("state/colorMode")).body().replace("\"", "");
		}
		
		/**
		 * Gets the current color (HSB/RGB) of the Aurora.<br>
		 * <b>Note: This only works if the Aurora is displaying a solid color.</b>
		 * @return  the color of the Aurora
		 */
		public Color getColor()
				throws StatusCodeException, UnauthorizedException
		{
			return Color.fromHSB(getHue(), getSaturation(), getBrightness());
		}
		
		/**
		 * Sets the color (HSB/RGB) of the Aurora.
		 * @param color  the new color
		 */
		public void setColor(Color color)
				throws StatusCodeException, UnauthorizedException
		{
			setHue(color.getHue());
			setSaturation(color.getSaturation());
			setBrightness(color.getBrightness());
		}
	}
	
	/**
	 * Contains methods for accessing and modifying Aurora effects information.
	 */
	public class Effects
	{
		/**
		 * Gets the name of the currently selected effect on the Aurora controller.
		 * @return  the name of the effect
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getCurrentEffectName() throws UnauthorizedException
		{
			return get(getURL("effects/select")).body().replace("\"", "");
		}
		
		/**
		 * Gets the currently selected effect as an <code>Effect</code> object.
		 * @return  the effect object
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect does not exist
		 */
		public Effect getCurrentEffect() throws StatusCodeException,
			UnauthorizedException, ResourceNotFoundException
		{
			return getEffect(getCurrentEffectName());
		}
		
		/**
		 * Sets the selected effect on the Aurora to the effect
		 * specified by <code>effectName</code>.
		 * @param effectName  the name of the effect
		 * @return  (200 OK, 204 No Content, 401 Unauthorized,
		 * 			404 Resource Not Found, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not present on the Aurora controller
		 * @throws UnprocessableEntityException  if <code>effectName</code> is malformed
		 */
		public int setEffect(String effectName) throws StatusCodeException,
			UnauthorizedException, ResourceNotFoundException, UnprocessableEntityException
		{
			String body = String.format("{\"select\": \"%s\"}", effectName);
			HttpRequest req = put(getURL("effects"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Sets a random effect based on the effects installed on the
		 * Aurora controller. This includes dynamic as well as Rhythm effects.
		 * @return  (200 OK, 204 No Content, 401 Unauthorized, 404 Resource Not Found)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int setRandomEffect() throws StatusCodeException, UnauthorizedException
		{
			String[] effects = getEffectsList();
			String currentEffect = getCurrentEffectName();
			String effect = currentEffect;
			while (effect.equals(currentEffect))
			{
				int i = new Random().nextInt(effects.length);
				effect = effects[i];
			}
			return setEffect(effect);
		}
		
		/**
		 * Gets a string array of all the effects installed on the Aurora controller.
		 * This includes static, dynamic, and Rhythm effects.
		 * @return  a string array of all the effects
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String[] getEffectsList() throws StatusCodeException, UnauthorizedException
		{
			JSONObject json = new JSONObject(get(getURL("effects")).body());
			JSONArray arr = json.getJSONArray("effectsList");
			String[] effects = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++)
				effects[i] = arr.getString(i);
			return effects;
		}
		
		/**
		 * Creates an <code>Effect</code> object from the <code>JSON</code> data
		 * for the effect <code>effectName</code>.
		 * @param effectName  the name of the effect
		 * @return  a new <code>Effect</code> object based on the effect <code>effectName</code>
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not present on the Aurora controller
		 */
		public Effect getEffect(String effectName) throws StatusCodeException,
				UnauthorizedException, ResourceNotFoundException
		{
			String body = String.format("{\"write\": {\"command\": \"request\", \"animName\": \"%s\"}}", effectName);
			HttpRequest req = put(getURL("effects"), body);
			checkStatusCode(req.code());
			return Effect.fromJSON(req.body());
		}
		
		/**
		 * Gets an array of type <code>Effect</code> containing all of
		 * the effects installed on the Aurora controller.
		 * @return  an array of the effects installed on the Aurora controller
		 * @throws UnauthorizedException   if the access token is invalid
		 */
		public Effect[] getAllEffects() throws StatusCodeException, UnauthorizedException
		{
			String[] list = getEffectsList();
			Effect[] effects = new Effect[list.length];
			for (int i = 0; i < list.length; i++)
				effects[i] = getEffect(list[i]);
			return effects;
		}
		
		/**
		 * Uploads and installs the local effect <code>effect</code> to the Aurora controller.
		 * If the effect does not exist on the Aurora it will be created. If the effect exists
		 * it will be overwritten.
		 * @param effect  the effect to be uploaded
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>effect</code> contains an
		 * 										 invalid number of instance variables, or has
		 * 										 one or more invalid instance variables (causing
		 * 										 the <code>JSON</code> output to be invalid)
		 */
		public int addEffect(Effect effect) throws StatusCodeException,
				UnauthorizedException, UnprocessableEntityException
		{
			return writeEffect(effect.toJSON("add"));
		}
		
		/**
		 * Deletes an effect from the Aurora controller.
		 * @param effectName  the name of the effect
		 * @return  (200 OK, 204 No Content, 401 Unauthorized,
		 * 				404 Resource Not Found, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not present on the Aurora controller
		 * @throws UnprocessableEntityException  if <code>effectName</code> is malformed
		 */
		public int deleteEffect(String effectName) throws StatusCodeException,
				UnauthorizedException, ResourceNotFoundException, UnprocessableEntityException
		{
			return writeEffect(String.format("{\"command\": \"delete\", \"animName\": \"%s\"}", effectName));
		}
		
		/**
		 * Renames an effect on the Aurora controller.
		 * @param effectName  the name of the effect
		 * @param newName  the new name of the effect
		 * @return  (200 OK, 204 No Content, 401 Unauthorized,
		 * 			404 Resource Not Found, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not present on the Aurora controller
		 * @throws UnprocessableEntityException  if <code>effectName</code> or
		 * 										 <code>newName</code> are malformed
		 */
		public int renameEffect(String effectName, String newName) throws StatusCodeException,
				UnauthorizedException, ResourceNotFoundException, UnprocessableEntityException
		{
			return writeEffect(String.format("{\"command\": \"rename\", \"animName\": \"%s\", \"newName\": \"%s\"}",
					effectName, newName));
		}
		
		/**
		 * Uploads and previews the local effect <code>effect</code> on
		 * the Aurora controller without installing it.
		 * @param effect  the effect to be previewed
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>effect</code> contains an
		 * 										 invalid number of instance variables, or has
		 * 										 one or more invalid instance variables (causing
		 * 										 the <code>JSON</code> output to be invalid)
		 */
		public int displayEffect(Effect effect) throws StatusCodeException,
				UnauthorizedException, UnprocessableEntityException
		{
			return writeEffect(String.format(effect.toJSON("display")));
		}
		
		/**
		 * Uploads and previews the local effect <code>effect</code> on
		 * the Aurora controller for a given duration without installing it.
		 * @param effectName   the name of the effect to be previewed
		 * @param duration  the duration for the effect to be displayed
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 404 Resource Not Found)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not found on the Aurora controller
		 */
		public int displayEffectFor(String effectName, int duration)
				throws StatusCodeException, UnauthorizedException
		{
			return writeEffect(String.format("{\"command\": \"displayTemp\", \"duration\": %d, \"animName\": \"%s\"}",
					duration, effectName));
		}
		
		/**
		 * Sets the color of a single panel on the Aurora.
		 * @param panel  the target panel
		 * @param red  the red RGB value
		 * @param green  the green RGB value
		 * @param blue  the blue RGB value
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the <code>panel</code> is not found on the Aurora
		 * 										 or if the <code>red</code>, <code>green</code>,
		 * 										 or <code>blue</code> values are invalid (must be
		 * 										 0 &#60; x &#60; 255)
		 */
		public int setPanelColor(Panel panel, int red,
				int green, int blue, int transitionTime) throws StatusCodeException,
					UnauthorizedException, UnprocessableEntityException
		{
			return setPanelColor(panel.getId(), red, green, blue, transitionTime);
		}
		
		/**
		 * Sets the color of a single panel on the Aurora.
		 * @param panel  the target panel
		 * @param hexColor  the new hex color
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the <code>panel</code> is not found on the Aurora
		 * 										 or if the <code>red</code>, <code>green</code>,
		 * 										 or <code>blue</code> values are invalid (must be
		 * 										 0 &#60; x &#60; 255)
		 */
		public int setPanelColor(Panel panel, String hexColor,
				int transitionTime) throws StatusCodeException,
					UnauthorizedException, UnprocessableEntityException
		{
			java.awt.Color color = java.awt.Color.decode(hexColor);
			return setPanelColor(panel, color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		/**
		 * Sets the color of a single panel on the Aurora.
		 * @param panelId  the target panel id
		 * @param red  the red RGB value
		 * @param green  the green RGB value
		 * @param blue  the blue RGB value
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the <code>panelId</code> is not valid
		 * 										 or if the <code>red</code>, <code>green</code>,
		 * 										 or <code>blue</code> values are invalid (must be
		 * 										 0 &#60; x &#60; 255)
		 */
		public int setPanelColor(int panelId, int red,
				int green, int blue, int transitionTime) throws StatusCodeException,
					UnauthorizedException, UnprocessableEntityException
		{
			Effect custom = new Effect();
			custom.setVersion("1.0");
			custom.setAnimType(Effect.Type.CUSTOM);
			custom.setAnimData("1 " + panelId + " 1 " +
					red + " " + green + " " + blue + " 0 " + transitionTime);
			custom.setLoop(false);
			return displayEffect(custom);
		}
		
		/**
		 * Sets the color of a single panel on the Aurora.
		 * @param panelId  the target panel id
		 * @param hexColor  the new hex color
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the <code>panelId</code> is not valid
		 * 										 or if the <code>red</code>, <code>green</code>,
		 * 										 or <code>blue</code> values are invalid (must be
		 * 										 0 &#60; x &#60; 255)
		 */
		public int setPanelColor(int panelId, String hexColor,
				int transitionTime) throws StatusCodeException,
					UnauthorizedException, UnprocessableEntityException
		{
			java.awt.Color color = java.awt.Color.decode(hexColor);
			return setPanelColor(panelId, color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		/**
		 * Fades all of the panels to an RGB color over a perdiod of time.
		 * @param red  the red RGB value
		 * @param green  the green RGB value
		 * @param blue  the blue RGB value
		 * @param duration  the fade time <i>in hertz (10Hz = 1sec)</i>
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the RGB values are outside of
		 * 										 the range 0-255 or if the duration
		 * 										 is negative
		 */
		public int fadeToColor(int red, int green, int blue, int duration)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			CustomEffectBuilder ceb = new CustomEffectBuilder(Aurora.this);
			ceb.addFrameToAllPanels(new Frame(red, green, blue, 0, duration));
			return displayEffect(ceb.build("", false));
		}
		
		/**
		 * Fades all of the panels to a hex color over a perdiod of time.
		 * @param hexColor the new hex color
		 * @param duration  the fade time <i>in hertz (frames per second)</i>
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 UnprocessableEntityException)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if the hex color is invalid
		 * 										 or if the duration is negative
		 */
		public int fadeToColor(String hexColor, int duration)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			java.awt.Color color = java.awt.Color.decode(hexColor);
			return fadeToColor(color.getRed(),
					color.getGreen(), color.getBlue(), duration);
		}
		
		/**
		 * Gets <i>all</i> the plugins/motions from the Aurora.
		 * <br><b>Note: This method is slow.</b>
		 * @return  an array of plugins from the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Plugin[] getPlugins() throws UnauthorizedException, StatusCodeException
		{
			String body = String.format("{\"write\": {\"command\": \"requestPlugins\"}}");
			HttpRequest req = put(getURL("effects"), body);
			checkStatusCode(req.code());
			JSONObject json = new JSONObject(req.body());
			JSONArray arr = json.getJSONArray("plugins");
			Plugin[] plugins = new Plugin[arr.length()];
			for (int i = 0; i < arr.length(); i++)
			{
				plugins[i] = Plugin.fromJSON(arr.getJSONObject(i).toString());
			}
			return plugins;
		}
		
		/**
		 * <b>(This method works with JSON data)</b><br>
		 * Uploads a <code>JSON</code> string to the Aurora controller.<br>
		 * Calls the <code>write</code> effect command from the 
		 * <a href = "http://forum.nanoleaf.me/docs/openapi#write">OpenAPI</a>. Refer to it
		 * for more information about the commands.
		 * <h1>Commands:</h1>
		 * - add  -  Installs an effect on the Aurora controller or updates
		 * 			 the effect if it already exists.<br>
		 * - delete  -  Permanently removes an effect from the Aurora controller.
		 * - request  -  Requests a single effect by name.<br>
		 * - requestAll  -  Requests all the installed effects from the Aurora controller.
		 * 					Note: this takes a long time, but returns a <code>JSON</code> string.
		 * 					If efficiency is important, use the {@link #getAllEffects()} method.<br>
		 * - display  -  Sets a color mode on the Aurora (used for previewing effects).<br>
		 * - displayTemp  -  Temporarily sets a color mode on the Aurora (typically used for
		 * 					 notifications of visual indicators).<br>
		 * - rename  -  Changes the name of an effect on the Aurora controller.<br><br>
		 * @param command  the operation to perform the write with
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>command</code> is malformed
		 * 										 or contains invalid effect options
		 */
		public int writeEffect(String command) throws StatusCodeException,
				UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"write\": %s}", command);
			HttpRequest req = put(getURL("effects"), body);
			checkStatusCode(req.code());
			return req.code();
		}
	}
	
	/**
	 * Contains methods for accessing and modifying Aurora panel layout information.
	 */
	public class PanelLayout
	{
		/**
		 * Gets the number of panels connected to the Aurora controller.
		 * @param includeRhythm  whether or not to include the Rhythm as a panel
		 * 		   (inluded by default in the OpenAPI)
		 * @return  the number of panels
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getNumPanels(boolean includeRhythm)
				throws StatusCodeException, UnauthorizedException
		{
			int numPanels = Integer.parseInt(get(getURL("panelLayout/layout/numPanels")).body());
			if (!includeRhythm || !Aurora.this.rhythm.getConnected())
				numPanels--;
			return numPanels;
		}
		
		/**
		 * Gets the side length of each panel connected to the Aurora.
		 * @return  the side length of each panel
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getSideLength() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("panelLayout/layout/sideLength")).body());
		}
		
		/**
		 * Gets an array of the connected panels.
		 * Each <code>Panel</code> contains the <b>original position data.</b>
		 * @return  an array of panels
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Panel[] getPanels()
				throws StatusCodeException, UnauthorizedException
		{
			JSONObject json = new JSONObject(get(getURL("panelLayout/layout")).body());
			JSONArray arr = json.getJSONArray("positionData");
			Panel[] pd = new Panel[arr.length()];
			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject data = arr.getJSONObject(i);
				int panelId = data.getInt("panelId");
				int x = data.getInt("x");
				int y = data.getInt("y");
				int o = data.getInt("o");
				pd[i] = new Panel(panelId, x, y, o);
			}
			return pd;
		}
		
		/**
		 * Gets an array of the connected panels that are
		 * rotated to match the global orientation.
		 * Each <code>Panel</code> contains <b>modified position data.</b>
		 * @return an array of rotated panels
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Panel[] getPanelsRotated()
				throws StatusCodeException, UnauthorizedException
		{
			Panel[] panels = getPanels();
			Point origin = getCentroid(panels);
			int globalOrientation = getGlobalOrientation();
			globalOrientation = globalOrientation == 360 ? 0 : globalOrientation;
			double radAngle = Math.toRadians(globalOrientation);
			for (Panel p : panels)
			{
				int x = p.getX() - origin.x;
				int y = p.getY() - origin.y;
				
				double newX = x * Math.cos(radAngle) - y * Math.sin(radAngle);
				double newY = x * Math.sin(radAngle) + y * Math.cos(radAngle);
				
				x = (int)(newX + origin.x);
				y = (int)(newY + origin.y);
				p.setX(x);
				p.setY(y);
			}
			return panels;
		}
		
		/**
		 * Finds a <code>Panel</code> object if you have a panel
		 * id but not the panel object.
		 * @param id  the panel id for the panel
		 * @return  a <code>Panel</code> with the same id, or null if no panel is found
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Panel getPanel(int id)
				throws StatusCodeException, UnauthorizedException
		{
			for (Panel panel : getPanels())
			{
				if (panel.getId() == id)
				{
					return panel;
				}
			}
			return null;
		}
		
		/**
		 * Gets the global orientation for the Aurora.
		 * @return  the global orientation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getGlobalOrientation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("panelLayout/globalOrientation/value")).body());
		}
		
		/**
		 * Sets the global orientation for the Aurora.
		 * @param orientation  the global orientation
		 * @return  (204 No Content, 401 Unauthorized)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int setGlobalOrientation(int orientation)
				throws StatusCodeException, UnauthorizedException
		{
			String body = String.format("{\"globalOrientation\": {\"value\": %d}}", orientation);
			HttpRequest req = put(getURL("panelLayout"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Gets the maximum global orientation for the Aurora.
		 * @return  the maximum global orientation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxGlobalOrientation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("panelLayout/globalOrientation/max")).body());
		}
		
		/**
		 * Gets the minimum global orientation for the Aurora.
		 * @return  the mimum global orientation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinGlobalOrientation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("panelLayout/globalOrientation/min")).body());
		}
		
		private Point getCentroid(Panel[] panels)
		{
			int centroidX = 0, centroidY = 0;
			int numXPoints = 0, numYPoints = 0;
			List<Integer> xpoints = new ArrayList<Integer>();
			List<Integer> ypoints = new ArrayList<Integer>();
			
			for (Panel p : panels)
			{
				int x = p.getX();
				int y = p.getY();
				if (!xpoints.contains(x))
				{
					centroidX += x;
					xpoints.add(x);
					numXPoints++;
				}
				if (!ypoints.contains(y))
				{
					centroidY += y;
					ypoints.add(y);
					numYPoints++;
				}
			}
			centroidX /= numXPoints;
			centroidY /= numYPoints;
			return new Point(centroidX, centroidY);
		}
	}
	
	/**
	 * Contains methods for accessing and modifying Aurora Rhythm information.
	 */
	public class Rhythm
	{
		/**
		 * Indicates if the Rhythm is connected to the Light Panels or not.
		 * @return  true, if the Rhythm is connected
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean getConnected()
				throws StatusCodeException, UnauthorizedException
		{
			return Boolean.parseBoolean(get(getURL("rhythm/rhythmConnected")).body());
		}
		
		/**
		 * Indicates if the Rhythm's microphone is currently active or not.
		 * @return  true, if the Rhythm is active
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean getActive()
				throws StatusCodeException, UnauthorizedException
		{
			return Boolean.parseBoolean(get(getURL("rhythm/rhythmActive")).body());
		}
		
		/**
		 * Indicates the Rhythm's Id in the Light Panel system.
		 * @return  the Rhythm's Id as an integer
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getId()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("rhythm/rhythmId")).body());
		}
		
		/**
		 * Indicates the Rhythm's hardware version.
		 * @return  the Rhythm's hardware version
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getHardwareVersion()
				throws StatusCodeException, UnauthorizedException
		{
			return get(getURL("rhythm/hardwareVersion")).body();
		}
		
		/**
		 * Indicates the Rhythm's firmware version.
		 * @return  the Rhythm's firmware version
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getFirmwareVersion()
				throws StatusCodeException, UnauthorizedException
		{
			return get(getURL("rhythm/firmwareVersion")).body();
		}
		
		/**
		 * Indicates if an aux cable (3.5mm) is currently connected to the Rhythm.
		 * @return  true, if an aux cable is connected
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean auxAvailable()
				throws StatusCodeException, UnauthorizedException
		{
			return Boolean.parseBoolean(get(getURL("rhythm/auxAvailable")).body());
		}
		
		/**
		 * Allows the user to control the sound source for the Rhythm.
		 * Mode 0 is the microphone. Mode 1 is the aux (3.5mm) cable.
		 * @return  the Rhythm mode (0 or 1)
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMode()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(get(getURL("rhythm/rhythmMode")).body());
		}
		
		/**
		 * Writing 0 to this field sets the Rhythm's sound source to the microphone,
		 * and writing 1 to the field sets the sound source to the aux cable.
		 * @param mode  the Rhythm mode (0 or 1 only)
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>mode</code> is not either 0 or 1
		 */
		public int setMode(int mode)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"rhythmMode\": %d}", mode);
			HttpRequest req = put(getURL("rhythm"), body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Indicates the position and orientation of the Rhythm in the Light Panels' layout.
		 * @return  the <code>Position</code> of the Rhythm
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Position getPosition()
				throws StatusCodeException, UnauthorizedException
		{
			JSONObject json = new JSONObject(get(getURL("rhythm/rhythmPos")).body());
			int x = json.getInt("x");
			int y = json.getInt("y");
			int o = json.getInt("o");
			return new Position(x, y, o);
		}
	}
	
	/**
	 * Contains methods for sending panel data to the
	 * Aurora <b>very quickly</b>.
	 */
	public class ExternalStreaming
	{
		/**
		 * The address of the aurora controller <i>for streaming mode only</i>.
		 */
		InetSocketAddress address;
		
		/**
		 * Gets the <code>SocketAddress</code> containing
		 * the host name and port of the external streaming controller.
		 * @return the <code>SocketAddress</code> of the streaming controller
		 */
		public InetSocketAddress getAddress()
		{
			return this.address;
		}
		
		/**
		 * Sets the external streaming address.
		 * @param address  the <code>SocketAddress</code>
		 * of the streaming controller
		 */
		public void setAddress(InetSocketAddress address)
		{
			this.address = address;
		}
		
		/**
		 * Enables external streaming mode over UDP.
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public void enable() throws StatusCodeException
		{
			String body = "{\"write\": {\"command\": \"display\", \"animType\": \"extControl\"}}";
			HttpRequest req = put(getURL("effects"), body);
			checkStatusCode(req.code());
			JSONObject response = new JSONObject(req.body());
			String host = response.getString("streamControlIpAddr");
			int port = response.getInt("streamControlPort");
			this.address = new InetSocketAddress(host, port);
		}
		
		/**
		 * Sends a series of frames to the target Aurora.
		 * <b>Requires external streaming to be enabled. Enable it
		 * using the {@link #enable()} method.</b>
		 * @param effect  the custom effect to be sent to the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void sendStaticEffect(Effect effect) throws StatusCodeException,
					UnauthorizedException, SocketException, IOException
		{
			sendAnimData(effect.getAnimData());
		}
		
		/**
		 * Sends a static animation data string to the target Aurora.<br>
		 * <b>Note: Requires external streaming to be enabled. Enable it
		 * using the {@link #enable()} method.</b>
		 * @param animData  the static animation data to be sent to the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void sendAnimData(String animData) throws StatusCodeException,
					UnauthorizedException, SocketException, IOException
		{
			byte[] data = animDataToBytes(animData);
			
			DatagramPacket packet = new DatagramPacket(data,
					data.length, this.address.getAddress(), this.address.getPort());
			
			try
			{
				DatagramSocket socket = new DatagramSocket();
				socket.send(packet);
				socket.close();
			}
			catch (SocketException se)
			{
				throw new SocketException("Failed to connect to target Aurora.");
			}
			catch (IOException ioe)
			{
				throw new IOException("I/O error.");
			}
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panelId  the id of the panel to update
		 * @param red  the red RGB value
		 * @param green  the green RGB value
		 * @param blue  the blue RGB value
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(int panelId, int red,
				int green, int blue, int transitionTime) throws StatusCodeException,
					UnauthorizedException, SocketException, IOException
		{
			String frame = String.format("1 %d 1 %d %d %d 0 %d",
					panelId, red, green, blue, transitionTime);
			sendAnimData(frame);
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panel  the panel to update
		 * @param red  the red RGB value
		 * @param green  the green RGB value
		 * @param blue  the blue RGB value
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(Panel panel, int red,
				int green, int blue, int transitionTime) throws StatusCodeException,
					UnauthorizedException, SocketException, IOException
		{
			setPanel(panel.getId(), red, green, blue, transitionTime);
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panelId  the id of the panel to update
		 * @param color  the color object
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(int panelId, Color color, int transitionTime)
				throws StatusCodeException, UnauthorizedException,
				SocketException, IOException
		{
			setPanel(panelId, color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panel  the panel to update
		 * @param color  the color object
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(Panel panel, Color color, int transitionTime)
				throws StatusCodeException, UnauthorizedException,
				SocketException, IOException
		{
			setPanel(panel.getId(), color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panel  the id of the panel to update
		 * @param color  the hex color code
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(int panelId, String hexColor, int transitionTime)
				throws StatusCodeException, UnauthorizedException,
				SocketException, IOException
		{
			java.awt.Color color = java.awt.Color.decode(hexColor);
			setPanel(panelId, color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		/**
		 * Updates the color of a single panel.
		 * @param panel  the panel to update
		 * @param color  the hex color code
		 * @param transitionTime  the time to transition to this frame from
		 * 						  the previous frame (must be 1 or greater)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws SocketException  if the target Aurora cannot be found or connected to
		 * @throws IOException  if an I/O error occurs
		 */
		public void setPanel(Panel panel, String hexColor, int transitionTime)
				throws StatusCodeException, UnauthorizedException,
				SocketException, IOException
		{
			java.awt.Color color = java.awt.Color.decode(hexColor);
			setPanel(panel.getId(), color.getRed(),
					color.getGreen(), color.getBlue(), transitionTime);
		}
		
		private byte[] animDataToBytes(String animData)
		{
			String[] dataStr = animData.split(" ");
			byte[] dataBytes = new byte[dataStr.length];
			for (int i = 0; i < dataStr.length; i++)
				dataBytes[i] = (byte)Integer.parseInt(dataStr[i]);
			return dataBytes;
		}
	}
	
	/**
	 * Contains methods for managing schedules on the Aurora.
	 */
	public class Schedules
	{
		/**
		 * Gets an array of schedules stored on the Aurora.
		 * @return  an array of schedules
		 */
		public Schedule[] getSchedules()
		{
			HttpRequest req = HttpRequest.get(getURL("schedules"));
			JSONObject obj = new JSONObject(req.body());
			JSONArray arr = obj.getJSONArray("schedules");
			Schedule[] schedules = new Schedule[arr.length()];
			for (int i = 0; i < arr.length(); i++)
			{
				schedules[i] = Schedule.fromJSON(
						arr.getJSONObject(i).toString());
			}
			return schedules;
		}
		
		/**
		 * Uploads an array of schedules to the Aurora.
		 * @param schedules  an array of schedules
		 * @throws UnprocessableEntityException  if one of the schedules are invalid
		 */
		public void addSchedules(Schedule[] schedules)
				throws UnprocessableEntityException, StatusCodeException
		{
			String schedulesStr = "\"schedules\":[";
			for (int i = 0; i < schedules.length; i++)
			{
				schedulesStr += schedules[i];
				if (i < schedules.length-1)
				{
					schedulesStr += ",";
				}
				else
				{
					schedulesStr += "]";
				}
			}
			String body = String.format("{\"write\":{\"command\":" +
					"\"addSchedules\",%s}}", schedulesStr);
			HttpRequest req = HttpRequest.put(getURL("effects"));
			req.send(body);
			req.code();
		}
		
		/**
		 * Uploads a schedule to the Aurora.
		 * @param schedule  the schedule
		 * @throws UnprocessableEntityException  if the schedule is invalid
		 */
		public void addSchedule(Schedule schedule)
				throws UnprocessableEntityException, StatusCodeException
		{
			addSchedules(new Schedule[]{schedule});
		}
		
		/**
		 * Deletes an array of schedules from the Aurora.
		 * @param schedules  an array of schedules
		 * @throws UnprocessableEntityException  if the Aurora does not contain
		 * 										 one of the schedules
		 */
		public void removeSchedules(Schedule[] schedules)
				throws UnprocessableEntityException, StatusCodeException
		{
			int[] ids = new int[schedules.length];
			for (int i = 0; i < schedules.length; i++)
			{
				ids[i] = schedules[i].getId();
			}
			removeSchedulesById(ids);
		}
		
		/**
		 * Deletes a schedule from the Aurora.
		 * @param schedule  the schedule
		 * @throws UnprocessableEntityException  if the Aurora does not
		 * 										 contain the schedule
		 */
		public void removeSchedule(Schedule schedule)
				throws UnprocessableEntityException, StatusCodeException
		{
			removeSchedules(new Schedule[]{schedule});
		}
		
		/**
		 * Deletes an array of schedules from the Aurora
		 * using their unique schedule IDs.
		 * @param scheduleIds  an array of schedule IDs
		 * @throws UnprocessableEntityException  if the Aurora does not contain one
		 * 										 of the schedule IDs
		 */
		public void removeSchedulesById(int[] scheduleIds)
				throws UnprocessableEntityException, StatusCodeException
		{
			String schedulesStr = "\"schedules\":[";
			for (int i = 0; i < scheduleIds.length; i++)
			{
				schedulesStr += String.format("{\"id\":%d}",
						scheduleIds[i]);
				if (i < scheduleIds.length-1)
				{
					schedulesStr += ",";
				}
				else
				{
					schedulesStr += "]";
				}
			}
			String body = String.format("{\"write\":{\"command\":" +
					"\"removeSchedules\",%s}}", schedulesStr);
			HttpRequest req = HttpRequest.put(getURL("effects"));
			req.send(body);
			checkStatusCode(req.code());
		}
		
		/**
		 * Deletes a schedule from the Aurora using
		 * its unique schedule ID.
		 * @param scheduleId  the schedule ID
		 * @throws UnprocessableEntityException  if the Aurora does not contain
		 * 										 the schedule ID
		 */
		public void removeScheduleById(int scheduleId)
				throws UnprocessableEntityException, StatusCodeException
		{
			removeSchedulesById(new int[]{scheduleId});
		}
	}
	
	private HttpRequest get(String url)
	{
		HttpRequest req = HttpRequest.get(url);
		req.connectTimeout(2000);
		return req;
	}
	
	private HttpRequest post(String url, String json)
	{
		HttpRequest req = HttpRequest.post(url);
		req.connectTimeout(2000);
		if (json != null)
			req.send(json);
		return req;
	}
	
	private HttpRequest put(String url, String json)
	{
		HttpRequest req = HttpRequest.put(url);
		req.connectTimeout(2000);
		if (json != null)
			req.send(json);
		return req;
	}
	
	/**
	 * Constructs a full URL to make an Aurora API call.
	 * @param endpoint  the final location in the API call (used to navigate <code>JSON</code>)
	 * @return  a completed URL (ready to be sent)
	 */
	private String getURL(String endpoint)
	{
		return String.format("http://%s:%d/api/%s/%s/%s",
				hostName, port, apiLevel, accessToken, endpoint);
	}
	
	/**
	 * Checks for error status codes.
	 * @param code  the response status code
	 * @throws StatusCodeException  if <code>code</code> matches an error status code
	 */
	private void checkStatusCode(int code) throws StatusCodeException
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
