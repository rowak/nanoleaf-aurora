package io.github.rowak.nanoleafapi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.rowak.nanoleafapi.StatusCodeException.UnauthorizedException;
import io.github.rowak.nanoleafapi.StatusCodeException.UnprocessableEntityException;

/**
 * A <i>local</i> interface for an Aurora effect.
 * Setter methods in this class will not have an effect on the Aurora.
 */
public class Effect
{
	/**
	 * Names of the available effect properties.
	 */
	private static final String[] PROPERTIES_NAMES =
		{
			"animName", "version", "animData", "palette", "transTime",
			"windowSize", "flowFactor", "delayTime", "loop",
			"colorType", "animType", "pluginType", "pluginUuid",
			"pluginOptions", "explodeFactor", "brightnessRange",
			"direction", "loop"
		};
	/**
	 * Properties for the local effect object.
	 */
	private Map<Object, Object> properties;
	
	/**
	 * The availbale effect types.<br>
	 * <b>Note: The <i>plugin</i> type is for user-made
	 * plugin effect types.</b>
	 */
	public enum Type
	{
		FLOW, EXPLODE, WHEEL, HIGHLIGHT,
		RANDOM, FADE, STATIC, CUSTOM, PLUGIN
	}
	
	/**
	 * The available effect direction types.<br>
	 * <b>Note: If a direction is missing, please
	 * create an issue on <a href =
	 * "https://github.com/rowak/nanoleaf-aurora/issues">Github.</a></b>
	 */
	public enum Direction
	{
		LEFT, RIGHT, UP, DOWN, OUTWARDS
	}
	
	/**
	 * Creates a blank effect.
	 */
	public Effect()
	{
		this.properties = new HashMap<Object, Object>();
	}
	
	/**
	 * Parse the <code>Effect</code> data from the raw <code>JSON</code>
	 * data into a new <code>Effect</code> object.
	 * @param json  the <code>JSON</code> data to convert
	 * @return  a new <code>Effect</code> equivalent
	 * 			to the <code>JSON</code> data
	 */
	public static Effect fromJSON(String json)
	{
		JSONObject data = new JSONObject(json);
		Effect ef = new Effect();
		ef.properties = new HashMap<Object, Object>();
		for (String property : Effect.PROPERTIES_NAMES)
		{
			if (data.has(property))
			{
				ef.properties.put(property, data.get(property));
			}
		}
		final Effect effect = ef;
		return effect;
	}
	
	/**
	 * Properly convert an effect object to <code>JSON</code> format.
	 * This method is most likely used in combination with the
	 * {@link Aurora.Effects#writeEffect(String)} method, to upload a
	 * local <code>Effect</code> to the Aurora.
	 * @param writeCommand  the command prepended to the <code>JSON</code> data to
	 * 						prepare it for the <code>Aurora.Effects.addEffect()</code> method.
	 * 						Set to <code>null</code> to <u>not</u> add a write command
	 * 						to the <code>JSON</code> data
	 * @return  the <code>Effect</code> in <code>JSON</code> format
	 */
	public String toJSON(String writeCommand)
	{
		JSONObject json = new JSONObject();
		if (writeCommand != null && writeCommand != "")
			json.put("command", writeCommand);
		
		for (Object key : properties.keySet())
		{
			Object value = properties.get(key);
			json.put((String)key, value);
		}
		
		return json.toString();
	}
	
	/**
	 * Properly convert an effect object to <code>JSON</code> format.
	 * @return  the <code>Effect</code> in <code>JSON</code> format
	 */
	public String toJSON()
	{
		return toJSON(null);
	}
	
	/**
	 * Converts an effect object to <code>JSON</code> format.
	 * @return  the effect in <code>JSON</code> format
	 */
	@Override
	public String toString()
	{
		return toJSON(null);
	}
	
	/**
	 * Checks if two effects are equal based on their properties.
	 * @param other  the effect to compare this effect to
	 * @return  true, if the effects are equal
	 */
	@Override
	public boolean equals(Object other)
	{
		return this.toString().equals(other.toString());
	}
	
	/**
	 * Creates a new static-type <code>Effect</code>. Each panel can be
	 * individually set to the desired color.
	 * @param effectName  the desired name of the new effect
	 * @param panels  the configured panels of the aurora. Each panel must have
	 * 				  the RGBW values set in order for the change to take effect
	 * @param controller  the desired Aurora controller object
	 * @return  a new static-type <code>Effect</code>
	 * @throws UnauthorizedException  if the access token is invalid
	 * @throws UnprocessableEntityException  if <code>panels</code> is malformed
	 */
	public static Effect createStaticEffect(String effectName,
			Panel[] panels, Aurora controller)
					throws StatusCodeException, UnauthorizedException,
					UnprocessableEntityException
	{
		Effect ef = new Effect();
		ef.setName(effectName);
		ef.setAnimType(Effect.Type.STATIC);
		
		int numPanels = controller.panelLayout().getNumPanels(false);
		StringBuilder animData = new StringBuilder();
		animData.append(numPanels);
		for (int i = 0; i < panels.length; i++)
		{
			Panel panel = panels[i];
			animData.append(" " + panel.getId() +
							" 1 " +
							panel.getRed() + " " +
							panel.getGreen() + " " +
							panel.getBlue() + " " +
							panel.getWhite() + " " +
							0);
		}
		
		ef.setAnimData(animData.toString());
		ef.setLoop(false);
		return ef;
	}
	
	/**
	 * Creates a new static-type <code>Effect</code> using
	 * previously created animation data.
	 * @param effectName  the desired name of the new effect
	 * @param animData  the desired animation data
	 * @return  a new static-type <code>Effect</code>
	 */
	public static Effect createStaticEffect(String effectName,
			String animData)
	{
		Effect ef = new Effect();
		ef.setName(effectName);
		ef.setAnimType(Effect.Type.STATIC);
		ef.setAnimData(animData);
		ef.setLoop(false);
		return ef;
	}
	
	/**
	 * Creates a new custom-type <code>Effect</code>. Animation data must
	 * be created without the API. Refer to the
	 * <a href = "http://forum.nanoleaf.me/docs/openapi#custom_effect_data_format">
	 * OpenAPI documentation (section 3.4.2)</a> for more information.
	 * @param effectName  the desired name of the new effect
	 * @param animData  the animation data for the effect
	 * @param loop  whether the effect should loop or not
	 * @return  a new custom-type <code>Effect</code>
	 */
	public static Effect createCustomEffect(String effectName,
			String animData, boolean loop)
	{
		Effect ef = new Effect();
		ef.setName(effectName);
		ef.setAnimType(Effect.Type.CUSTOM);
		ef.setAnimData(animData);
		ef.setLoop(loop);
		return ef;
	}
	
	/**
	 * Gets a map of properties belonging to this effect.
	 * @return  the properties of this effect
	 */
	public Map<Object, Object> getProperties()
	{
		return this.properties;
	}
	
	/**
	 * Gets the plugin options from this effect. The plugin options can
	 * only be modified using the {@link Effect#setPluginOptions} method.
	 * <br><b>Note: Plugin options are only available for pre-existing version
	 * 2.0 plugin-type effects.</b>
	 * @return  a <code>JSONArray</code> containing the effect pluginOptions
	 * 			in the json form {"name": NAME, "value": VALUE}
	 */
	public PluginOptions getPluginOptions()
	{
		//return (JSONArray)this.properties.get("pluginOptions");
		return PluginOptions.fromJSON(this.properties.get("pluginOptions").toString());
	}
	
	/**
	 * Sets the plugin options for this effect.
	 * <br><b>Note: Plugin options are only available for pre-existing version
	 * 2.0 plugin-type effects.</b>
	 * @param options  the new plugin options
	 */
	public void setPluginOptions(PluginOptions options)
	{
		properties.put("pluginOptions", new JSONArray(options.toJSON()));
	}
	
	/**
	 * Gets the name of the effect.
	 * @return  the name of the effect (or null if not set)
	 */
	public String getName()
	{
		return (String)this.properties.get("animName");
	}
	
	/**
	 * Sets the name of the effect.
	 * @param name  the name of the effect
	 */
	public void setName(String name)
	{
		this.properties.put("animName", name);
	}
	
	/**
	 * Gets the version of the effect.
	 * @return  the version of the effect (or null if not set)
	 */
	public String getVersion()
	{
		return (String)this.properties.get("version");
	}
	
	/**
	 * Sets the version of the effect.
	 * @param version  the version of the effect
	 */
	public void setVersion(String version)
	{
		this.properties.put("version", version);
	}
	
	/**
	 * Gets whether or not the effect will loop.
	 * @return  true, if the effect will loop (Note: returns false if not set)
	 */
	public boolean getLoop()
	{
		return safeGetProperty("loop", Boolean.TYPE);
	}
	
	/**
	 * Sets whether or not the effect will loop.
	 * @param loop  whether the effect should loop or not
	 */
	public void setLoop(boolean loop)
	{
		safeSetProperty("loop", loop);
	}
	
	/**
	 * Gets the color palette of the effect.
	 * @return  the color palette of the effect (or null if not set)
	 */
	public Color[] getPalette()
	{
		JSONArray json = safeGetProperty("palette", JSONArray.class);
		return json != null ? jsonToPalette(json) : null;
	}
	
	/**
	 * Sets the color palette of the effect.
	 * @param palette  the desired color palette
	 */
	public void setPalette(Color[] palette)
	{
		if (palette != null)
		{
			JSONArray arr = new JSONArray(palette);
			for (Object o : arr)
			{
				JSONObject jobj = (JSONObject)o;
				if (jobj.getDouble("probability") == -1.0)
				{
					jobj.remove("probability");
				}
			}
			this.properties.put("palette", arr);
		}
		else
		{
			throw new NullPointerException("Cannot set null palette");
		}
	}
	
	/**
	 * Sets the total transition time of the effect (maximum and minimum).
	 * @param transTime  the desired transition time
	 */
	public void setTransTime(int transTime)
	{
		setMaxTransTime(transTime);
		setMinTransTime(transTime);
	}
	
	/**
	 * Gets the maximum transition time of the effect.
	 * @return  the maximum transition time (or -1 if not set)
	 */
	public int getMaxTransTime()
	{
		return (Integer)safeGetMaxMinProperty("transTime", "maxValue");
	}
	
	/**
	 * Sets the maximum transition time of the effect.
	 * @param transTime  the desired transition time
	 */
	public void setMaxTransTime(int transTime)
	{
		safeSetMaxMinProperty("transTime", "maxValue", transTime);
	}
	
	/**
	 * Gets the minimum transition time of the effect.
	 * @return the minimum transition time (or -1 if not set)
	 */
	public int getMinTransTime()
	{
		return (Integer)safeGetMaxMinProperty("transTime", "minValue");
	}
	
	/**
	 * Sets the minimum transition time of the effect.
	 * @param transTime  the desired transition time
	 */
	public void setMinTransTime(int transTime)
	{
		safeSetMaxMinProperty("transTime", "minValue", transTime);
	}
	
	/**
	 * Gets the window size of the effect.
	 * @return  the window size of the effect (or -1 if not set)
	 */
	public int getWindowSize()
	{
		return safeGetProperty("windowSize", Integer.class);
	}
	
	/**
	 * Sets the window size of the effect.
	 * @param size  the window size
	 */
	public void setWindowSize(int size)
	{
		this.properties.put("windowSize", size);
	}
	
	/**
	 * Gets the flow factor of the effect (flow effects only).
	 * @return  the flow factor of the effect (or -1.0 if not set)
	 */
	public double getFlowFactor()
	{
		return safeGetProperty("flowFactor", Double.class);
	}
	
	/**
	 * Sets the flow factor of the effect (flow effects only).
	 * @param factor  the flow factor
	 */
	public void setFlowFactor(double factor)
	{
		this.properties.put("flowFactor", factor);
	}
	
	/**
	 * Sets the total delay time of the effect (maximum and minimum).
	 * @param delay  the desired delay time between transitions
	 */
	public void setDelayTime(int delay)
	{
		setMaxDelayTime(delay);
		setMinDelayTime(delay);
	}
	
	/**
	 * Gets the maximum delay time of the effect.
	 * @return  the maximum delay time of the effect (or -1 if not set)
	 */
	public int getMaxDelayTime()
	{
		return (Integer)safeGetMaxMinProperty("delayTime", "maxValue");
	}
	
	/**
	 * Sets the maximum delay time of the effect.
	 * @param delay  the maximum delay time between transitions
	 */
	public void setMaxDelayTime(int delay)
	{
		safeSetMaxMinProperty("delayTime", "maxValue", delay);
	}
	
	/**
	 * Gets the minimum delay time of the effect.
	 * @return  the minimum delay time of the effect (or -1 if not set)
	 */
	public int getMinDelayTime()
	{
		return (Integer)safeGetMaxMinProperty("delayTime", "minValue");
	}
	
	/**
	 * Sets the minimum delay time of the effect.
	 * @param delay  the minimum delay time between transitions
	 */
	public void setMinDelayTime(int delay)
	{
		safeSetMaxMinProperty("delayTime", "minValue", delay);
	}
	
	/**
	 * Gets the color type of the effect.
	 * @return  the color type of the effect (or null if not set)
	 */
	public String getColorType()
	{
		//return (String)safeGetProperty("colorType", "");
		return safeGetProperty("colorType", String.class);
	}
	
	/**
	 * Sets the color type of the effect.
	 * @param type  the color type of the effect
	 */
	public void setColorType(String type)
	{
		this.properties.put("colorType", type);
	}
	
	/**
	 * Gets the animation type of the effect.
	 * @return  the animation type of the effect (or null if not set)
	 */
	public Effect.Type getAnimType()
	{
		String type = (String)this.properties.get("animType");
		Effect.Type validType = null;
		for (Effect.Type t : Effect.Type.values())
		{
			if (t.toString().toLowerCase().equals(type))
			{
				validType = t;
			}
		}
		return validType;
	}
	
	/**
	 * Sets the animation type of the effect.
	 * @param type  the animation type
	 */
	public void setAnimType(Effect.Type type)
	{
		if (type != null)
		{
			this.properties.put("animType", type.toString().toLowerCase());
		}
		else
		{
			throw new NullPointerException("Cannot set null effect type");
		}
	}
	
	/**
	 * Gets the plugin type of the effect.
	 * @return  the plugin type of the effect (or null if not set)
	 */
	public String getPluginType()
	{
		return safeGetProperty("pluginType", String.class);
	}
	
	/**
	 * Sets the plugin type of the effect.
	 * @param type  the plugin type
	 */
	public void setPluginType(String type)
	{
		this.properties.put("pluginType", type);
	}
	
	/**
	 * Gets the explosion factor of the effect.
	 * @return  the explosion factor of the effect (or -1.0 if not set)
	 */
	public double getExplodeFactor()
	{
		return safeGetProperty("explodeFactor", Double.class);
	}
	
	/**
	 * Sets the explosion factor of the effect.
	 * @param factor  the explosion factor
	 */
	public void setExplodeFactor(double factor)
	{
		this.properties.put("explodeFactor", factor);
	}
	
	/**
	 * Sets the total brightness of the effect (maximum and minimum).
	 * @param brightness  the brightness level of the effect
	 */
	public void setBrightness(int brightness)
	{
		setMaxBrightness(brightness);
		setMinBrightness(brightness);
	}
	
	/**
	 * Gets the maximum brightness of the effect.
	 * @return  the maximum brightness of the effect (or -1 if not set)
	 */
	public int getMaxBrightness()
	{
		return (Integer)safeGetMaxMinProperty("brightnessRange", "maxValue");
	}
	
	/**
	 * Sets the maximum brightness of the effect.
	 * @param brightness  the maximum brightness level
	 */
	public void setMaxBrightness(int brightness)
	{
		safeSetMaxMinProperty("brightnessRange", "maxValue", brightness);
	}
	
	/**
	 * Gets the minimum brightness of the effect.
	 * @return  the minimum brightness of the effect (or -1 if not set)
	 */
	public int getMinBrightness()
	{
		return (Integer)safeGetMaxMinProperty("brightnessRange", "minValue");
	}
	
	/**
	 * Sets the minimum brightness of the effect.
	 * @param brightness  the minimum brightness level
	 */
	public void setMinBrightness(int brightness)
	{
		safeSetMaxMinProperty("brightnessRange", "minValue", brightness);
	}
	
	/**
	 * Gets the moving direction of the effect.
	 * @return  the direction of the effect (or null if not set)
	 */
	public Effect.Direction getDirection()
	{
		String direction = (String)this.properties.get("direction");
		Effect.Direction validDirection = null;
		for (Effect.Direction d : Effect.Direction.values())
		{
			if (d.toString().toLowerCase().equals(direction))
				validDirection = d;
		}
		return validDirection;
	}
	
	/**
	 * Sets the moving direction of the effect.
	 * @param direction  the direction of motion of the effect
	 */
	public void setDirection(Effect.Direction direction)
	{
		if (direction != null)
		{
			this.properties.put("direction", direction.toString().toLowerCase());
		}
		else
		{
			throw new NullPointerException("Cannot set null direction");
		}
	}
	
	/**
	 * Gets the pluginUUID of the effect.
	 * @return  the pluginUUID of the effect (or null if not set)
	 */
	public String getPluginUuid()
	{
		return safeGetProperty("pluginUuid", String.class);
	}
	
	/**
	 * Sets the pluginUUID of the effect.
	 * @param uuid  the UUID for the plugin
	 */
	public void setPluginUuid(String uuid)
	{
		this.properties.put("pluginUuid", uuid);
	}
	
	/**
	 * Gets the animation data of the effect (static/custom effects only).
	 * @return  the animation data of the effect
	 */
	public String getAnimData()
	{
		return safeGetProperty("animData", String.class);
	}
	
	/**
	 * Sets the animation data of the effect (static/custom effects only).
	 * @param data  the animation data of the effect
	 */
	public void setAnimData(String data)
	{
		this.properties.put("animData", data);
	}
	
	/**
	 * Sets a plugin option to a specified value in the effect's plugin options.
	 * <br><b>This only works for PLUGIN-type effects.</b>
	 * @param option  the plugin option
	 * @param value  the option's value
	 */
	private void setPluginOption(String option, Object value)
	{
		JSONArray pluginOptions = (JSONArray)this.properties.get("pluginOptions");
		JSONObject optionObject = null;
		for (Object o : pluginOptions)
		{
			JSONObject jo = (JSONObject)o;
			if (jo.get("name").equals(option))
			{
				optionObject = jo;
			}
		}
		if (optionObject != null)
			optionObject.put("value", value);
		this.properties.put("pluginOptions", pluginOptions);
	}
	
	/**
	 * Gets a plugin option from the effect's plugin options array.
	 * @param option  the plugin option to get
	 * @return  a <code>JSONObject</code> containing the plugin option (name and value)
	 */
	private JSONObject getPluginOption(String option)
	{
		JSONArray pluginOptions = (JSONArray)this.properties.get("pluginOptions");
		if (pluginOptions != null)
		{
			for (Object o : pluginOptions)
			{
				JSONObject jo = (JSONObject)o;
				if (jo.get("name").equals(option))
				{
					return jo;
				}
			}
		}
		return null;
	}
	
	/**
	 * Attempts to locate then return the specified property as type <code>T</code>.
	 * @param property  the property to get
	 * @param type  the type of the property (for return and parsing type)
	 * @return  the value of the specified property as type <code>T</code>
	 * 			or <code>"null"/-1/-1.0/false</code> if the property cannot be located
	 */
	private <T> T safeGetProperty(String property, Class<T> type)
	{
		JSONObject option = getPluginOption(property);
		if (this.properties.containsKey(property))
		{
			T value = (T)this.properties.get(property);
			// Special handling for double types (bug? double json values are formatted as ints)
			if (type.equals(Double.class) && value instanceof Integer)
				value = (T)new Double((Integer)value);
			// Special handling for null strings
			if ((value == null || value.equals(null)) && type.equals(String.class))
				return (T)"null";
			return value;
		}
		else if (option != null)
		{
			T value = (T)option.get("value");
			// Special handling for double types (bug? double json values are formatted as ints)
			if (type.equals(Double.class) && value instanceof Integer)
				value = (T)new Double((Integer)value);
			// Special handling for null strings
			if ((value.equals(null) || value.equals(null)) && type.equals(String.class))
				return (T)"null";
			return value;
		}
		else if (type.equals(Integer.class))
		{
			return (T)new Integer(-1);
		}
		else if (type.equals(Double.class))
		{
			return (T)new Double(-1.0);
		}
		else if (type.equals(boolean.class))
		{
			return (T)new Boolean(false);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Properly sets a property's value, accounting for all effect versions.
	 * @param property  the name of the property being updated
	 * @param value  the new value of the property
	 */
	private void safeSetProperty(String property, Object value)
	{
		if (this.getAnimType() != null &&
				this.getAnimType().equals(Effect.Type.PLUGIN) &&
				this.getVersion().equals("2.0"))
		{
			setPluginOption(property, value);
		}
		else
		{
			this.properties.put(property, value);
		}
	}
	
	/**
	 * Properly gets a max/min effect property value.
	 * @param property  the max/min property to get
	 * @param maxMin  either "max" or "min" for the max/min values respectively
	 * @return  the value of the max/min property
	 */
	private Object safeGetMaxMinProperty(String property, String maxMin)
	{
		JSONObject propertyRange = (JSONObject)this.properties.get(property);
		if (propertyRange != null)
		{
			return propertyRange.opt(maxMin);
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Properly sets a max/min effect property value.
	 * @param property  the max/min property to set
	 * @param maxMin  either "max" or "min" for the max/min values respectively
	 * @param value  the new max/min value
	 */
	private void safeSetMaxMinProperty(String property, String maxMin, int value)
	{
		JSONObject propertyRange = (JSONObject)this.properties.get(property);
		if (propertyRange != null)
		{
			propertyRange.put(maxMin, value);
		}
		else
		{
			propertyRange = new JSONObject();
			propertyRange.put("maxValue", 0);
			propertyRange.put("minValue", 0);
			propertyRange.put(maxMin, value);
			this.properties.put(property, propertyRange);
		}
	}
	
	/**
	 * Converts a json-formatted <code>Color[]</code> array
	 * into a <code>Color[]</code> array object.
	 * @param arr  the <code>JSONArray</code> object
	 * 			   containing the <code>Color[]</code> array
	 * @return  a <code>Color[]</code> array object
	 */
	private Color[] jsonToPalette(JSONArray arr)
	{
		Color[] palette = new Color[arr.length()];
		for (int i = 0; i < arr.length(); i++)
		{
			JSONObject colors = arr.getJSONObject(i);
			int hue = colors.getInt("hue");
			int sat = colors.getInt("saturation");
			int brightness = colors.getInt("brightness");
			palette[i] = Color.fromHSB(hue,
					sat, brightness);
			try
			{
				double probability = colors.getDouble("probability");
				palette[i].setProbability(probability);
			}
			catch (JSONException je)
			{
				palette[i].setProbability(-1);
			}
		}
		return palette;
	}
}
