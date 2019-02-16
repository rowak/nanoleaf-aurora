package io.github.rowak;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A <i>local</i> interface for the plugin options belonging to a plugin.
 * Setter methods in this class will not have an effect on the Aurora.
 */
public class PluginOptions
{
	/**
	 * Names of the available plugin options properties.
	 */
	private static final String[] PROPERTIES_NAMES =
		{
			"transTime", "loop", "linDirection", "radDirection",
			"rotDirection", "delayTime", "nColorsPerFrame", "mainColorProb"
		};
	/**
	 * Properties for this local plugin options object.
	 */
	private Map<Object, Object> properties;
	
	/**
	 * Creates a blank plugin options object.
	 */
	public PluginOptions()
	{
		this.properties = new HashMap<Object, Object>();
	}
	
	/**
	 * Parse the <code>PluginOptions</code> data from the raw <code>JSON</code>
	 * data into a new <code>PluginOptions</code> object.
	 * @param json  the <code>JSON</code> data to convert
	 * @return  a new <code>PluginOptions</code> object equivalent
	 * 			to the <code>JSON</code> data
	 */
	public static PluginOptions fromJSON(String json)
	{
		JSONArray data = new JSONArray(json);
		PluginOptions plo = new PluginOptions();
		plo.properties = new HashMap<Object, Object>();
		for (int i = 0; i < data.length(); i++)
		{
			JSONObject property = data.getJSONObject(i);
			String name = property.getString("name");
			Object value = property.get("value");
			for (int j = 0; j < PluginOptions.PROPERTIES_NAMES.length; j++)
			{
				String jName = PluginOptions.PROPERTIES_NAMES[j];
				if (jName.equals(name))
				{
					plo.properties.put(name, value);
				}
			}
		}
		final PluginOptions pluginoptions = plo;
		return pluginoptions;
	}
	
	/**
	 * Properly convert a pluginoptions object to <code>JSON</code> format.
	 * @return  the pluginoptions in <code>JSON</code> format
	 */
	public String toJSON()
	{
		JSONArray json = new JSONArray();
		for (Object key : properties.keySet())
		{
			Object value = properties.get(key);
			JSONObject obj = new JSONObject();
			obj.put("name", key);
			obj.put("value", value);
			json.put(obj);
		}
		return json.toString();
	}
	
	/**
	 * Converts a plugin options object to <code>JSON</code> format.
	 * @return  the plugin options in <code>JSON</code> format
	 */
	@Override
	public String toString()
	{
		return toJSON();
	}
	
	/**
	 * Gets a map of properties belonging to
	 * this plugin options object.
	 * @return  the properties of this plugin options object
	 */
	public Map<Object, Object> getProperties()
	{
		return this.properties;
	}
	
	/**
	 * Gets the transition time for this plugin options object.
	 * This option indicates the time it takes to go from one palette
	 * color to another, in tenths of a second.
	 * @return  the transition time
	 */
	public int getTransTime()
	{
		if (properties.containsKey("transTime"))
		{
			return (Integer)properties.get("transTime");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Sets the transition time for this plugin options object.
	 * This option indicates the time it takes to go from one palette
	 * color to another, in tenths of a second.
	 * @param transTime  the transition time
	 */
	public void setTransTime(int transTime)
	{
		properties.put("transTime", transTime);
	}
	
	/**
	 * Checks if loop is enabled for this plugin options object.
	 * @return  true, if loop is enabled and false if loop is disabled or unset
	 */
	public boolean loop()
	{
		if (properties.containsKey("loop"))
		{
			return (Boolean)properties.get("loop");
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Sets if loop is enabled for this plugin options object.
	 * @param loop  whether loop should be enabled or not
	 */
	public void setLoop(boolean loop)
	{
		properties.put("loop", loop);
	}
	
	/**
	 * Gets the linear direction for this plugin options object.
	 * This option will be one of: "left", "right", "up", or "down".
	 * @return  the linear direction
	 */
	public String getLinearDirection()
	{
		return (String)properties.get("linDirection");
	}
	
	/**
	 * Sets the linear direction for this plugin options object.
	 * This option must be one of: "left", "right", "up", or "down".
	 * @param direction  the linear direction
	 */
	public void setLinearDirection(String direction)
	{
		properties.put("linDirection", direction);
	}
	
	/**
	 * Gets the radial direction based on the layout center.
	 * @return  the radial direction
	 */
	public String getRadialDirection()
	{
		return (String)properties.get("radDirection");
	}
	
	/**
	 * Sets the radial direction based on the layout center.
	 * @param direction  the radial direction
	 */
	public void setRadialDirection(String direction)
	{
		properties.put("radDirection", direction);
	}
	
	/**
	 * Gets the rotational/circular direction around the layout center.
	 * This option will be one of: "cw" or "ccw", where "cw" is
	 * clockwise and "ccw" is counterclockwise.
	 * @return  the rotational direction
	 */
	public String getRotationalDirection()
	{
		return (String)properties.get("rotDirection");
	}
	
	/**
	 * Sets the rotational/circular direction around the layout center.
	 * This option must be one of: "cw" or "ccw", where "cw" is
	 * clockwise and "ccw" is counterclockwise.
	 * @param direction  the rotational direction
	 */
	public void setRotationDirection(String direction)
	{
		properties.put("rotDirection", direction);
	}
	
	/**
	 * Gets the delay time for this plugin options object.
	 * This option indicates how long the plugin will dwell
	 * on a palette color, in tenths of a second.
	 * @return  the delay time
	 */
	public int getDelayTime()
	{
		if (properties.containsKey("delayTime"))
		{
			return (Integer)properties.get("delayTime");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Sets the delay time for this plugin options object.
	 * This option indicates how long the plugin will dwell
	 * on a palette color, in tenths of a second.
	 * @param delayTime  the delay time
	 */
	public void setDelayTime(int delayTime)
	{
		properties.put("delayTime", delayTime);
	}
	
	/**
	 * Gets the modifier that indicates how much of a palette
	 * is shown on the layout. The limit for this option is 50.
	 * @return  the number of colors shown per frame
	 */
	public int getNumColorsPerFrame()
	{
		if (properties.containsKey("nColorsPerFrame"))
		{
			return (Integer)properties.get("nColorsPerFrame");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Sets the modifier that indicates how much of a palette
	 * is shown on the layout. The limit for this option is 50.
	 * @param nColorsPerFrame  the number of colors shown per frame
	 */
	public void setNumColorsPerFrame(int nColorsPerFrame)
	{
		properties.put("nColorsPerFrame", nColorsPerFrame);
	}
	
	/**
	 * Gets the probability of the background color being used.
	 * @return  the probability of showing the background color
	 */
	public double getMainColorProbability()
	{
		if (properties.containsKey("mainColorProb"))
		{
			return (Double)properties.get("mainColorProb");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Sets the probability of the background color being used.
	 * @param probability  the probability of showing the background color
	 */
	public void setMainColorProbability(double probability)
	{
		properties.put("mainColorProb", probability);
	}
}
