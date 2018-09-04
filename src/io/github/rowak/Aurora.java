package io.github.rowak;

import java.net.InetSocketAddress;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;

import io.github.rowak.StatusCodeException.ResourceNotFoundException;
import io.github.rowak.StatusCodeException.UnauthorizedException;
import io.github.rowak.StatusCodeException.UnprocessableEntityException;

/**
 * The primary class in the API. Contains methods and other
 * classes for accessing and manipulating Aurora information.
 */
public class Aurora
{
	private String host, apiLevel, accessToken;
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
	
	/**
	 * Creates a new instance of the Aurora controller.
	 * @param host  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: /api/v1/)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public Aurora(String host, int port,
			String apiLevel, String accessToken)
					throws StatusCodeException, UnauthorizedException
	{
		init(host, port, apiLevel, accessToken);
	}
	
	/**
	 * Creates a new instance of the Aurora controller.
	 * @param host  the <code>InetAddress</code> of the host
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: /api/v1/)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public Aurora(InetSocketAddress host,
			String apiLevel, String accessToken)
					throws StatusCodeException, UnauthorizedException
	{
		init(host.getHostName(), host.getPort(), apiLevel, accessToken);
	}
	
	/**
	 * Initialize the Aurora object and gather initial data.
	 * @param host  the hostname of the Aurora controller
	 * @param port  the port of the Aurora controller (default=16021)
	 * @param apiLevel  the current version of the Aurora OpenAPI (for example: /api/v1/)
	 * @param accessToken  a unique authentication token
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	private void init(String host, int port,
			String apiLevel, String accessToken)
					throws StatusCodeException, UnauthorizedException
	{
		this.host = host;
		this.apiLevel = apiLevel;
		this.port = port;
		this.accessToken = accessToken;
		
		this.state = new State();
		this.effects = new Effects();
		this.panelLayout = new PanelLayout();
		this.rhythm = new Rhythm();
		
		HttpRequest req = HttpRequest.get(getURL(""));
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
	
	public String getHost()
	{
		return this.host;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public String getApiLevel()
	{
		return this.apiLevel;
	}
	
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
		HttpRequest req = HttpRequest.put(getURL("identify"));
		checkStatusCode(req.code());
		return req.code();
	}
	
	/**
	 * Contains methods for accessing and modifying Aurora state information.
	 */
	public class State
	{
		/**
		 * Stores a local copy of the <i>on</i> state of the Aurora.
		 */
		private boolean on;
		
		/**
		 * Initializes this <code>State</code> object by
		 * requesting the Aurora's <code>on</code> value.
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public State() throws StatusCodeException
		{
			getOn();
		}
		
		/**
		 * Gets the on state of the Aurora (true = on, false = off).
		 * @return true, if the Aurora is on
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean getOn() throws StatusCodeException
		{
			on = Boolean.parseBoolean(HttpRequest.get(getURL("state/on/value")).body());
			return on;
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
			HttpRequest req = HttpRequest.put(getURL("state")).send(body);
			if (req.code() == 200)
				this.on = on;
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
			return setOn(!on);
		}
		
		/**
		 * Gets the master brightness of the Aurora.
		 * @return  the brightness of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/brightness/value")).body());
		}
		
		/**
		 * Sets the master brightness of the Aurora.
		 * @param brightness  the desired brightness
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>brightness</code> is not within the
		 * 								 		 maximum and minimum restrictions
		 */
		public int setBrightness(int brightness)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"brightness\": {\"value\": %d}}", brightness);
			HttpRequest req = HttpRequest.put(getURL("state")).send(body);
			checkStatusCode(req.code());
			return req.code();
		}
		
		/**
		 * Gets the maximum brightness of the Aurora.
		 * @return  the maximum brightness
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMaxBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/brightness/max")).body());
		}
		
		/**
		 * Gets the minimum brightness of the Aurora.
		 * @return  the minimum brightness
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinBrightness() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/brightness/min")).body());
		}
		
		/**
		 * Gets the hue of the Aurora (static/custom effects only).
		 * @return  the hue of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getHue() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/hue/value")).body());
		}
		
		/**
		 * Sets the hue of the Aurora (static/custom effects only).
		 * @param hue  the desired hue.
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>hue</code> is not within the
		 * 										 maximum and minimum restrictions
		 */
		public int setHue(int hue)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"hue\": {\"value\": %d}}", hue);
			HttpRequest req = HttpRequest.put(getURL("state")).send(body);
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
			return Integer.parseInt(HttpRequest.get(getURL("state/hue/max")).body());
		}
		
		/**
		 * Gets the minimum hue of the Aurora.
		 * @return  the minimum hue
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinHue() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/hue/min")).body());
		}
		
		/**
		 * Gets the saturation of the Aurora (static/custom effects only).
		 * @return  tue saturation of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getSaturation() throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/sat/value")).body());
		}
		
		/**
		 * Sets the saturation of the Aurora (static/custom effects only).
		 * @param saturation  the desired saturation
		 * @return  (204 No Content, 401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>saturation</code> is not within
		 * 										 the maximum and minimum restrictions
		 */
		public int setSaturation(int saturation)
				throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
		{
			String body = String.format("{\"sat\": {\"value\": %d}}", saturation);
			HttpRequest req = HttpRequest.put(getURL("state")).send(body);
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
			return Integer.parseInt(HttpRequest.get(getURL("state/sat/max")).body());
		}
		
		/**
		 * Gets the minimum saturation of the Aurora.
		 * @return  the minimum saturation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinSaturation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/sat/min")).body());
		}
		
		/**
		 * Gets the color temperature of the Aurora (color temperature effect only).
		 * @return  the color temperature of the Aurora
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getColorTemperature()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/ct/value")).body());
		}
		
		/**
		 * 
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
			HttpRequest req = HttpRequest.put(getURL("state")).send(body);
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
			return Integer.parseInt(HttpRequest.get(getURL("state/ct/max")).body());
		}
		
		/**
		 * Gets the minimum color temperature of the Aurora.
		 * @return  the minimum color temperature
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinColorTemperature()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("state/ct/min")).body());
		}
		
		/**
		 * Gets the color mode of the Aurora.
		 * @return  the color mode
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getColorMode()
				throws StatusCodeException, UnauthorizedException
		{
			return HttpRequest.get(getURL("state/colorMode")).body().replace("\"", "");
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
			return HttpRequest.get(getURL("effects/select")).body().replace("\"", "");
		}
		
		/**
		 * Sets the selected effect on the Aurora to the effect
		 * specified by <code>effectName</code>.
		 * @param effectName  the name of the desired effect
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
			HttpRequest req = HttpRequest.put(getURL("effects")).send(body);
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
			JSONObject json = new JSONObject(HttpRequest.get(getURL("effects")).body());
			JSONArray arr = json.getJSONArray("effectsList");
			String[] effects = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++)
				effects[i] = arr.getString(i);
			return effects;
		}
		
		/**
		 * Creates an <code>Effect</code> object from the <code>JSON</code> data
		 * for the effect <code>effectName</code>.
		 * @param effectName  the name of the desired effect
		 * @return  a new <code>Effect</code> object based on the effect <code>effectName</code>
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not present on the Aurora controller
		 */
		public Effect getEffect(String effectName) throws StatusCodeException,
				UnauthorizedException, ResourceNotFoundException
		{
			String body = String.format("{\"write\": {\"command\": \"request\", \"animName\": \"%s\"}}", effectName);
			HttpRequest req = HttpRequest.put(getURL("effects")).send(body);
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
		 * @param effect  the <code>Effect</code> object of the desired effect
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
		 * @param effectName  the name of the desired effect
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
		 * @param effectName  the name of the desired effect
		 * @param newName  the new name of the desired effect
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
		 * @param effect  the <code>Effect</code> object of the desired effect
		 * @return  (200 OK, 204 No Content,
		 * 			401 Unauthorized, 422 Unprocessable Entity)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws UnprocessableEntityException  if <code>effect</code> contains an
		 * 										 invalid number of instance variables, or has
		 * 										 one or more invalid instance variables (causing
		 * 										 the <code>JSON</code> output to be invalid)
		 */
		public int previewEffect(Effect effect) throws StatusCodeException,
				UnauthorizedException, UnprocessableEntityException
		{
			return writeEffect(String.format(effect.toJSON("display")));
		}
		
		/**
		 * Uploads and previews the local effect <code>effect</code> on
		 * the Aurora controller for a given duration without installing it.
		 * @param effectName   the name of the desired effect
		 * @param duration  the desired duration for the effect to be displayed
		 * @return  200 OK, 204 No Content,
		 * 			401 Unauthorized, 404 Resource Not Found)
		 * @throws UnauthorizedException  if the access token is invalid
		 * @throws ResourceNotFoundException  if the effect <code>effectName</code>
		 * 									  is not found on the Aurora controller
		 */
		public int previewEffectFor(String effectName, int duration)
				throws StatusCodeException, UnauthorizedException
		{
			return writeEffect(String.format("{\"command\": \"displayTemp\", \"duration\": %d, \"animName\": \"%s\"}",
					duration, effectName));
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
			HttpRequest req = HttpRequest.put(getURL("effects")).send(body);
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
			int numPanels = Integer.parseInt(HttpRequest.get(getURL("panelLayout/layout/numPanels")).body());
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
			return Integer.parseInt(HttpRequest.get(getURL("panelLayout/layout/sideLength")).body());
		}
		
		/**
		 * Gets an array of <code>Panel</code>s connected to the Aurora. Each <code>Panel</code> contains
		 * position data for that <code>Panel</code>.
		 * @return  an array of <code>Panel</code>s
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Panel[] getPositionData()
				throws StatusCodeException, UnauthorizedException
		{
			JSONObject json = new JSONObject(HttpRequest.get(getURL("panelLayout/layout")).body());
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
		 * Gets the global orientation for the Aurora.
		 * @return  the global orientation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getGlobalOrientation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("panelLayout/globalOrientation/value")).body());
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
			HttpRequest req = HttpRequest.put(getURL("panelLayout")).send(body);
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
			return Integer.parseInt(HttpRequest.get(getURL("panelLayout/globalOrientation/max")).body());
		}
		
		/**
		 * Gets the minimum global orientation for the Aurora.
		 * @return  the mimum global orientation
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getMinGlobalOrientation()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("panelLayout/globalOrientation/min")).body());
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
			return Boolean.parseBoolean(HttpRequest.get(getURL("rhythm/rhythmConnected")).body());
		}
		
		/**
		 * Indicates if the Rhythm's microphone is currently active or not.
		 * @return  true, if the Rhythm is active
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean getActive()
				throws StatusCodeException, UnauthorizedException
		{
			return Boolean.parseBoolean(HttpRequest.get(getURL("rhythm/rhythmActive")).body());
		}
		
		/**
		 * Indicates the Rhythm's Id in the Light Panel system.
		 * @return  the Rhythm's Id as an integer
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public int getId()
				throws StatusCodeException, UnauthorizedException
		{
			return Integer.parseInt(HttpRequest.get(getURL("rhythm/rhythmId")).body());
		}
		
		/**
		 * Indicates the Rhythm's hardware version.
		 * @return  the Rhythm's hardware version
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getHardwareVersion()
				throws StatusCodeException, UnauthorizedException
		{
			return HttpRequest.get(getURL("rhythm/hardwareVersion")).body();
		}
		
		/**
		 * Indicates the Rhythm's firmware version.
		 * @return  the Rhythm's firmware version
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public String getFirmwareVersion()
				throws StatusCodeException, UnauthorizedException
		{
			return HttpRequest.get(getURL("rhythm/firmwareVersion")).body();
		}
		
		/**
		 * Indicates if an aux cable (3.5mm) is currently connected to the Rhythm.
		 * @return  true, if an aux cable is connected
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public boolean auxAvailable()
				throws StatusCodeException, UnauthorizedException
		{
			return Boolean.parseBoolean(HttpRequest.get(getURL("rhythm/auxAvailable")).body());
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
			return Integer.parseInt(HttpRequest.get(getURL("rhythm/rhythmMode")).body());
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
			HttpRequest req = HttpRequest.put(getURL("rhythm")).send(body);
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
			JSONObject json = new JSONObject(HttpRequest.get(getURL("rhythm/rhythmPos")).body());
			int x = json.getInt("x");
			int y = json.getInt("y");
			int o = json.getInt("o");
			return new Position(x, y, o);
		}
	}
	
	/**
	 * Represents a position on the Aurora panel grid.
	 * Used to store <code>JSON</code>-parsed data.
	 */
	public class Position
	{
		private int x, y, orientation;
		
		public Position(int x, int y, int orientation)
		{
			this.x = x;
			this.y = y;
			this.orientation = orientation;
		}
		
		public int getX()
		{
			return this.x;
		}
		
		public int getY()
		{
			return this.y;
		}
		
		public int getOrientation()
		{
			return this.orientation;
		}
	}
	
	/**
	 * Represents a single Aurora light panel. Used to
	 * store <code>JSON</code>-parsed data.
	 */
	public class Panel extends Position
	{
		private int id, r, g, b, w;
		
		/**
		 * Creates a new instance of a <code>Panel</code>.
		 * @param id  the id of the panel
		 * @param x  the x-value of the panel location
		 * @param y  the y-value of the panel location
		 * @param orientation  the panel's orientation on the Aurora grid
		 */
		public Panel(int id, int x, int y, int orientation)
		{
			super(x, y, orientation);
			this.id = id;
		}
		
		/**
		 * Gets the unique ID for the panel.
		 * @return  the panel's unique ID
		 */
		public int getId()
		{
			return this.id;
		}
		
		/**
		 * Gets the red RGBW value of the panel's color.
		 * @return  the panel's red value
		 */
		public int getRed()
		{
			return this.r;
		}
		
		/**
		 * Sets the red RGBW value of the panel's color.
		 * @param value  the red RGBW value
		 */
		public void setRed(int value)
		{
			this.r = value;
		}
		
		/**
		 * Gets the green RGBW value of the panel's color.
		 * @return  the panel's green value
		 */
		public int getGreen()
		{
			return this.g;
		}
		
		/**
		 * Sets the green RGBW value of the panel's color.
		 * @param value  the green RGBW value
		 */
		public void setGreen(int value)
		{
			this.g = value;
		}
		
		/**
		 * Gets the blue RGBW value of the panel's color.
		 * @return  the panel's blue value
		 */
		public int getBlue()
		{
			return this.b;
		}
		
		/**
		 * Sets the blue RGBW value of the panel's color.
		 * @param value  the blue RGBW value
		 */
		public void setBlue(int value)
		{
			this.b = value;
		}
		
		/**
		 * Gets the white RGBW value of the panel's color.
		 * @return  the panel's white value
		 */
		public int getWhite()
		{
			return this.w;
		}
		
		/**
		 * Sets the white RGBW value of the panel's color.
		 * @param value  the white RGBW value
		 */
		public void setWhite(int value)
		{
			this.w = value;
		}
		
		/**
		 * Sets the RGBW values for the panel's color.
		 * @param red  the red RGBW value
		 * @param green  the green RGBW value
		 * @param blue  the blue RGBW value
		 * @param white  the white RGBW value
		 */
		public void setRGBW(int red, int green,
				int blue, int white)
		{
			this.r = red;
			this.g = green;
			this.b = blue;
			this.w = white;
		}
	}
	
	/**
	 * Constructs a full URL to make an Aurora API call.
	 * @param endpoint  the final location in the API call (used to navigate <code>JSON</code>)
	 * @return  a completed URL (ready to be sent)
	 */
	private String getURL(String endpoint)
	{
		return String.format("http://%s:%d/api/%s/%s/%s",
				host, port, apiLevel, accessToken, endpoint);
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
