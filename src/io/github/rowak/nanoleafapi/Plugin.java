package io.github.rowak.nanoleafapi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A <i>local</i> interface for an Aurora plugin.
 * Setter methods in this class will not have an effect on the Aurora.
 */
public class Plugin
{
	/**
	 * Names of the available plugin properties.
	 */
	private static final String[] PROPERTIES_NAMES =
		{
			"uuid", "name", "description", "author",
			"type", "tags", "pluginConfig"
		};
	/**
	 * Properties for this local plugin object.
	 */
	private Map<Object, Object> properties;
	
	/**
	 * Creates a blank plugin.
	 */
	public Plugin()
	{
		this.properties = new HashMap<Object, Object>();
	}
	
	/**
	 * Parse the <code>Plugin</code> data from the raw <code>JSON</code>
	 * data into a new <code>Plugin</code> object.
	 * @param json  the <code>JSON</code> data to convert
	 * @return  a new <code>Plugin</code> equivalent
	 * 			to the <code>JSON</code> data
	 */
	public static Plugin fromJSON(String json)
	{
		JSONObject data = new JSONObject(json);
		Plugin pl = new Plugin();
		pl.properties = new HashMap<Object, Object>();
		for (String property : Plugin.PROPERTIES_NAMES)
		{
			if (data.has(property))
			{
				pl.properties.put(property, data.get(property));
			}
		}
		final Plugin plugin = pl;
		return plugin;
	}
	
	/**
	 * Properly convert a plugin object to <code>JSON</code> format.
	 * @return  the plugin in <code>JSON</code> format
	 */
	public String toJSON()
	{
		JSONObject json = new JSONObject();
		for (Object key : properties.keySet())
		{
			Object value = properties.get(key);
			json.put((String)key, value);
		}
		
		return json.toString();
	}
	
	/**
	 * Converts a plugin object to <code>JSON</code> format.
	 * @return  the plugin in <code>JSON</code> format
	 */
	@Override
	public String toString()
	{
		return toJSON();
	}
	
	/**
	 * Gets a map of properties belonging to this plugin.
	 * @return  the properties of this plugin
	 */
	public Map<Object, Object> getProperties()
	{
		return this.properties;
	}
	
	/**
	 * Gets the uuid of this plugin.
	 * @return  the uuid of this plugin
	 */
	public String getUuid()
	{
		return (String)properties.get("uuid");
	}
	
	/**
	 * Sets the uuid of this plugin.
	 * @param uuid  the uuid of this plugin
	 */
	public void setUuid(String uuid)
	{
		properties.put("uuid", uuid);
	}
	
	/**
	 * Gets the name of this plugin.
	 * @return  the name of this plugin
	 */
	public String getName()
	{
		return (String)properties.get("name");
	}
	
	/**
	 * Sets the name of this plugin.
	 * @param name  the name of this plugin
	 */
	public void setName(String name)
	{
		properties.put("name", name);
	}
	
	/**
	 * Gets the description of this plugin
	 * @return  the description of this plugin
	 */
	public String getDescription()
	{
		return (String)properties.get("description");
	}
	
	/**
	 * Sets the description of this plugin.
	 * @param description  the description of this plugin
	 */
	public void setDescription(String description)
	{
		properties.put("description", description);
	}
	
	/**
	 * Gets the author of this plugin.
	 * @return  the author of this plugin
	 */
	public String getAuthor()
	{
		return (String)properties.get("author");
	}
	
	/**
	 * Sets the author of this plugin.
	 * @param author  the author of this plugin
	 */
	public void setAuthor(String author)
	{
		properties.put("author", author);
	}
	
	/**
	 * Gets the type of this plugin (For example: color, rhythm, etc)
	 * @return  the type of this plugin
	 */
	public String getType()
	{
		return (String)properties.get("type");
	}
	
	/**
	 * Sets the type of this plugin (For example: color, rhythm, etc)
	 * @param type  the type of this plugin
	 */
	public void setType(String type)
	{
		properties.put("type", type);
	}
	
	/**
	 * Gets the tags for this plugin.
	 * @return  the tags for this plugin
	 */
	public String[] getTags()
	{
		JSONArray tagsJson = (JSONArray)properties.get("tags");
		String[] tags = new String[tagsJson.length()];
		for (int i = 0; i < tagsJson.length(); i++)
		{
			tags[i] = tagsJson.getString(i);
		}
		return tags;
	}
	
	/**
	 * Sets the tags for this plugin.
	 * @param tags  the tags for this plugin
	 */
	public void setTags(String[] tags)
	{
		properties.put("tags", new JSONArray(tags));
	}
	
	/**
	 * Gets the plugin configuration for this plugin as a JSONArray.<br>
	 * <b>Note: this uses a more raw type since it is not
	 * fully supported by this api.</b>
	 * @return  the plugin configuration
	 */
	public JSONArray getPluginConfig()
	{
		return (JSONArray)properties.get("pluginConfig");
	}
	
	/**
	 * Sets the plugin configuration for this plugin as a JSONArray.<br>
	 * <b>Note: this uses a more raw type since it is not
	 * fully supported by this api.</b>
	 * @param config  the plugin configuration
	 */
	public void setPluginConfig(JSONArray config)
	{
		properties.put("pluginConfig", new JSONArray(config.toString()));
	}
}
