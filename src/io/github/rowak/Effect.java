package io.github.rowak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.rowak.StatusCodeException.UnauthorizedException;
import io.github.rowak.StatusCodeException.UnprocessableEntityException;

/**
 * A <i>local</i> interface for an <code>Effect</code>. Setter methods in this class
 * will not have an effect on the Aurora.
 */
public class Effect
{
	private String name;
	private String version;
	private boolean loop;
	private Color[] palette;
	private int transTime = -1, 
			maxTransTime = -1, minTransTime = -1;
	private int windowSize = -1;
	private double flowFactor = -1;
	private int delayTime = -1,
			maxDelayTime = -1, minDelayTime = -1;
	private String colorType;
	private Effect.Type animType;
	private String pluginType;
	private String pluginUuid;
	private String animData;
	private double explodeFactor = -1;
	private int maxBrightness = -1,
			minBrightness = -1;
	private String direction;
	
	/**
	 * The availbale animation types.<br>
	 * <b>Note: The <i>plugin</i> type is for user-made
	 * plugin effect types.</b>
	 */
	public enum Type
	{
		FLOW, EXPLODE, WHEEL, HIGHLIGHT,
		RANDOM, FADE, STATIC, CUSTOM, PLUGIN
	}
	
	/**
	 * Parse the <code>Effect</code> data from
	 * the raw <code>JSON</code> data into
	 * a new <code>Effect</code> object.
	 * @param json  the <code>JSON</code> data to convert
	 * @return  a new <code>Effect</code> equivalent
	 * 			to the <code>JSON</code> data
	 */
	public static Effect fromJSON(String json)
	{
		JSONObject data = new JSONObject(json);
		Effect ef = new Effect();
		ef.name = data.getString("animName");
		ef.version = data.getString("version");
		ef.animType = Effect.Type.valueOf(data.getString("animType").toUpperCase());
		ef.colorType = data.getString("colorType");
		
		try
		{
			ef.animData = data.getString("animData");
		}
		catch (JSONException je)
		{
			ef.animData = null;
		}
		
		JSONArray arr = data.getJSONArray("palette");
		ef.palette = new Color[arr.length()];
		for (int i = 0; i < arr.length(); i++)
		{
			JSONObject colors = arr.getJSONObject(i);
			int hue = colors.getInt("hue");
			int sat = colors.getInt("saturation");
			int brightness = colors.getInt("brightness");
			ef.palette[i] = new Effect().new Color(hue,
					sat, brightness);
			try
			{
				double probability = colors.getDouble("probability");
				ef.palette[i].setProbability(probability);
			}
			catch (JSONException je)
			{
				ef.palette[i].setProbability(-1);
			}
		}
		
		if (ef.version.equals("2.0") && ef.animType.equals(Effect.Type.PLUGIN))
		{
			JSONArray options = data.getJSONArray("pluginOptions");
			for (int i = 0; i < options.length(); i++)
			{
				JSONObject plugin = options.getJSONObject(i);
				String pluginName = plugin.getString("name");
				if (pluginName.equals("loop"))
					ef.loop = plugin.getBoolean("value");
				else if (pluginName.equals("transTime"))
					ef.transTime = plugin.getInt("value");
				else if (pluginName.equals("delayTime"))
					ef.delayTime = plugin.getInt("value");
			}
			ef.pluginType = data.getString("pluginType");
			ef.pluginUuid = data.getString("pluginUuid");
		}
		else
		{
			try
			{
				JSONObject transTime = data.getJSONObject("transTime");
				ef.maxTransTime = transTime.getInt("maxValue");
				ef.minTransTime = transTime.getInt("minValue");
			}
			catch (JSONException je)
			{
				ef.maxTransTime = -1;
				ef.minTransTime = -1;
			}
			if (ef.animType.equals(Effect.Type.WHEEL))
				ef.windowSize = data.getInt("windowSize");
			else
				ef.windowSize = -1;
			if (ef.animType.equals(Effect.Type.FLOW))
				ef.flowFactor = data.getInt("flowFactor");
			else
				ef.flowFactor = -1.0;
			try
			{
				JSONObject delayTime = data.getJSONObject("delayTime");
				ef.maxDelayTime = delayTime.getInt("maxValue");
				ef.minDelayTime = delayTime.getInt("minValue");
			}
			catch (JSONException je)
			{
				ef.maxDelayTime = -1;
				ef.minDelayTime = -1;
			}
			if (ef.animType.equals(Effect.Type.EXPLODE))
				ef.explodeFactor = data.getDouble("explodeFactor");
			else
				ef.explodeFactor = -1;
			try
			{
				JSONObject brightnessRange = data.getJSONObject("brightnessRange");
				ef.maxBrightness = brightnessRange.getInt("maxValue");
				ef.minBrightness = brightnessRange.getInt("minValue");
			}
			catch (JSONException je)
			{
				ef.maxBrightness = -1;
				ef.minBrightness = -1;
			}
			if (ef.animType.equals(Effect.Type.FLOW) ||
				ef.animType.equals(Effect.Type.EXPLODE) ||
				ef.animType.equals(Effect.Type.WHEEL))
			{
				ef.direction = data.getString("direction");
			}
			try
			{
				ef.loop = data.getBoolean("loop");
			}
			catch (JSONException je)
			{
				
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
	 * 						prepare it for the <code>Aurora.addEffect()</code> method.
	 * 						Set to <code>null</code> to <u>not</u> add a write command
	 * 						to the <code>JSON</code> data
	 * @return  the <code>Effect</code> in <code>JSON</code> format
	 */
	public String toJSON(String writeCommand)
	{
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (writeCommand != null && writeCommand != "")
			json.append("\"command\":\"" + writeCommand + "\",");
		if (this.animType.equals(Effect.Type.WHEEL))
			json.append("\"version\":\"" + this.version + "\",");
		json.append("\"animName\":\"" + this.name + "\",");
		
		String animType = this.animType.name().toLowerCase();
		
		if (!this.animType.equals(Effect.Type.CUSTOM) &&
			!this.animType.equals(Effect.Type.STATIC) &&
			!this.animType.equals(Effect.Type.PLUGIN))
		{
			json.append("\"animType\":\"" + animType + "\",");
			json.append("\"colorType\":\"" + this.colorType + "\",");
			if (!this.animType.equals(Effect.Type.FLOW) &&
				!this.animType.equals(Effect.Type.EXPLODE))
			{
				json.append("\"animData\":" + this.animData + ",");
			}
			json.append("\"palette\":" + paletteToJSON(this.palette) + ",");
			
			if (this.animType.equals(Effect.Type.FLOW))
			{
				json.append("\"transTime\":{\"maxValue\":" +
						this.maxTransTime + ",\"minValue\":" + this.minTransTime + "},");
				json.append("\"delayTime\":{\"maxValue\":" +
						this.maxDelayTime + ",\"minValue\":" + this.minDelayTime + "},");
				json.append("\"flowFactor\":" + this.flowFactor + ",");
				json.append("\"direction\":\"" + this.direction + "\",");
				json.append("\"loop\":" + this.loop + "}");
			}
			else if (this.animType.equals(Effect.Type.EXPLODE))
			{
				json.append("\"transTime\":{\"maxValue\":" +
						this.maxTransTime + ",\"minValue\":" + this.minTransTime + "},");
				json.append("\"delayTime\":{\"maxValue\":" +
						this.maxDelayTime + ",\"minValue\":" + this.minDelayTime + "},");
				json.append("\"explodeFactor\":" + this.explodeFactor + ",");
				json.append("\"direction\":\"" + this.direction + "\",");
				json.append("\"loop\":" + this.loop + "}");
			}
			else if (this.animType.equals(Effect.Type.WHEEL))
			{
				json.append("\"transTime\":{\"maxValue\":" +
						this.maxTransTime + ",\"minValue\":" + this.minTransTime + "},");
				json.append("\"windowSize\":\"" + this.windowSize + "\",");
				json.append("\"direction\":\"" + this.direction + "\",");
				json.append("\"loop\":" + this.loop + "}");
			}
			else if (this.animType.equals(Effect.Type.HIGHLIGHT) ||
					 this.animType.equals(Effect.Type.RANDOM) ||
					 this.animType.equals(Effect.Type.FADE))
			{
				json.append("\"brightnessRange\":{\"maxValue\":" +
						this.maxBrightness + ",\"minValue\":" + this.minBrightness + "},");
				json.append("\"transTime\":{\"maxValue\":" +
						this.maxTransTime + ",\"minValue\":" + this.minTransTime + "},");
				json.append("\"delayTime\":{\"maxValue\":" +
						this.maxDelayTime + ",\"minValue\":" + this.minDelayTime + "},");
				json.append("\"loop\":" + this.loop + "}");
			}
		}
		else if (this.animType.equals(Effect.Type.CUSTOM) ||
				this.animType.equals(Effect.Type.STATIC))
		{
			json.append("\"animType\":\"" + animType + "\",");
			json.append("\"animData\":\"" + this.animData + "\",");
			json.append("\"loop\":" + this.loop + "}");
		}
		else
		{
			json.append("\"palette\":" + paletteToJSON(this.palette) + ",");
			json.append("\"version\":\"" + this.version + "\",");
			json.append("\"colorType\":\"" + this.colorType + "\",");
			json.append("\"pluginUuid\":\"" + this.pluginUuid + "\",");
			json.append("\"animType\":\"" + animType + "\",");
			json.append("\"pluginOptions\":[{\"name\":\"loop\", \"value\":" + this.loop + "}," +
						"{\"name\":\"transTime\",\"value\":" + this.transTime + "}," +
						"{\"name\":\"delayTime\",\"value\":" + this.delayTime + "}],");
			json.append("\"pluginType\":\"" + this.pluginType + "\"}");
		}
		return json.toString();
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
			Aurora.Panel[] panels, Aurora controller)
					throws StatusCodeException, UnauthorizedException, UnprocessableEntityException
	{
		Effect ef = new Effect();
		ef.setName(effectName);
		ef.setAnimType(Effect.Type.STATIC);
		
		int numPanels = controller.panelLayout().getNumPanels(false);
		StringBuilder animData = new StringBuilder();
		animData.append(numPanels);
		for (int i = 0; i < panels.length; i++)
		{
			Aurora.Panel panel = panels[i];
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
	 * Gets the name of the effect.
	 * @return  the name of the effect (or null if not set)
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Sets the name of the effect.
	 * @param name  the desired name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the version of the effect.
	 * @return  the version of the effect
	 */
	public String getVersion()
	{
		return this.version;
	}
	
	/**
	 * Sets the version of the effect.
	 * @param version  the version of the effect
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	/**
	 * Gets whether or not the effect will loop.
	 * @return  true, if the effect will loop
	 */
	public boolean getLoop()
	{
		return this.loop;
	}
	
	/**
	 * Sets whether or not the effect will loop.
	 * @param loop  whether the effect should loop or not
	 */
	public void setLoop(boolean loop)
	{
		this.loop = loop;
	}
	
	/**
	 * Gets the color palette of the effect.
	 * @return  the color palette of the effect (or null if not set)
	 */
	public Color[] getPalette()
	{
		return this.palette;
	}
	
	/**
	 * Sets the color palette of the effect.
	 * @param palette  the desired color palette
	 */
	public void setPalette(Color[] palette)
	{
		this.palette = palette;
	}
	
	/**
	 * Gets the transition time of the effect.
	 * @return  the transition time (or -1 if not set)
	 */
	public int getTransTime()
	{
		return this.transTime;
	}
	
	/**
	 * Sets the transition time of the effect.
	 * @param transTime  the desired transition time
	 */
	public void setTransTime(int transTime)
	{
		this.transTime = transTime;
	}
	
	/**
	 * Gets the maximum transition time of the effect.
	 * @return  the maximum transition time (or -1 if not set)
	 */
	public int getMaxTransTime()
	{
		return this.maxTransTime;
	}
	
	/**
	 * Sets the maximum transition time of the effect.
	 * @param transTime  the desired transition time
	 */
	public void setMaxTransTime(int transTime)
	{
		this.maxTransTime = transTime;
	}
	
	/**
	 * Gets the minimum transition time of the effect.
	 * @return the minimum transition time (or -1 if not set)
	 */
	public int getMinTransTime()
	{
		return this.minTransTime;
	}
	
	/**
	 * Sets the minimum transition time of the effect.
	 * @param transTime  the desired transition time
	 */
	public void setMinTransTime(int transTime)
	{
		this.minTransTime = transTime;
	}
	
	/**
	 * Gets the window size of the effect.
	 * @return  the window size of the effect (or -1 if not set)
	 */
	public int getWindowSize()
	{
		return this.windowSize;
	}
	
	/**
	 * Sets the window size of the effect.
	 * @param size  the window size
	 */
	public void setWindowSize(int size)
	{
		this.windowSize = size;
	}
	
	/**
	 * Gets the flow factor of the effect (flow effects only).
	 * @return  the flow factor of the effect (or -1.0 if not set)
	 */
	public double getFlowFactor()
	{
		return this.flowFactor;
	}
	
	/**
	 * Sets the flow factor of the effect (flow effects only).
	 * @param factor  the desired flow factor
	 */
	public void setFlowFactor(double factor)
	{
		this.flowFactor = factor;
	}
	
	/**
	 * Gets the delay time of the effect.
	 * @return  the dely time of the effect (or -1 if not set)
	 */
	public int getDelayTime()
	{
		return this.delayTime;
	}
	
	/**
	 * Sets the delay time of the effect.
	 * @param delay  the desired delay time
	 */
	public void setDelayTime(int delay)
	{
		this.delayTime = delay;
	}
	
	/**
	 * Gets the maximum delay time of the effect.
	 * @return  the maximum delay time of the effect (or -1 if not set)
	 */
	public int getMaxDelayTime()
	{
		return this.maxDelayTime;
	}
	
	/**
	 * Sets the maximum delay time of the effect.
	 * @param delay  the desired maximum delay time
	 */
	public void setMaxDelayTime(int delay)
	{
		this.maxDelayTime = delay;
	}
	
	/**
	 * Gets the minimum delay time of the effect.
	 * @return  the minimum delay time of the effect (or -1 if not set)
	 */
	public int getMinDelayTime()
	{
		return this.minDelayTime;
	}
	
	/**
	 * Sets the minimum delay time of the effect.
	 * @param delay  the desired minimum delay time
	 */
	public void setMinDelayTime(int delay)
	{
		this.minDelayTime = delay;
	}
	
	/**
	 * Gets the color type of the effect.
	 * @return  the color type of the effect (or null if not set)
	 */
	public String getColorType()
	{
		return this.colorType;
	}
	
	/**
	 * Sets the color type of the effect.
	 * @param type  the desired color type
	 */
	public void setColorType(String type)
	{
		this.colorType = type;
	}
	
	/**
	 * Gets the animation type of the effect.
	 * @return  the animation type of the effect (or null if not set)
	 */
	public Effect.Type getAnimType()
	{
		return this.animType;
	}
	
	/**
	 * Sets the animation type of the effect.
	 * @param type  the desired animation type
	 */
	public void setAnimType(Effect.Type type)
	{
		this.animType = type;
	}
	
	/**
	 * Gets the plugin type of the effect.
	 * @return  the plugin type of the effect (or null if not set)
	 */
	public String getPluginType()
	{
		return this.pluginType;
	}
	
	/**
	 * Sets the plugin type of the effect.
	 * @param type  the desired plugin type
	 */
	public void setPluginType(String type)
	{
		this.pluginType = type;
	}
	
	/**
	 * Gets the explosion factor of the effect.
	 * @return  the explosion factor of the effect (or -1.0 if not set)
	 */
	public double getExplodeFactor()
	{
		return this.explodeFactor;
	}
	
	/**
	 * Sets the explosion factor of the effect.
	 * @param factor  the desired explosion factor
	 */
	public void setExplodeFactor(double factor)
	{
		this.explodeFactor = factor;
	}
	
	/**
	 * Gets the maximum brightness of the effect.
	 * @return  the maximum brightness of the effect (or -1 if not set)
	 */
	public int getMaxBrightness()
	{
		return this.maxBrightness;
	}
	
	/**
	 * Sets the maximum brightness of the effect.
	 * @param brightness  the desired maximum brightness
	 */
	public void setMaxBrightness(int brightness)
	{
		this.maxBrightness = brightness;
	}
	
	/**
	 * Gets the minimum brightness of the effect.
	 * @return  the minimum brightness of the effect (or -1 if not set)
	 */
	public int getMinBrightness()
	{
		return this.minBrightness;
	}
	
	/**
	 * Sets the minimum brightness of the effect.
	 * @param brightness  the desired minimum brightness
	 */
	public void setMinBrightness(int brightness)
	{
		this.minBrightness = brightness;
	}
	
	/**
	 * Gets the moving direction of the effect.
	 * @return  the direction of the effect (or null if not set)
	 */
	public String getDirection()
	{
		return this.direction;
	}
	
	/**
	 * Sets the moving direction of the effect.
	 * @param direction  the desired direction
	 */
	public void setDirection(String direction)
	{
		this.direction = direction;
	}
	
	/**
	 * Gets the pluginUUID of the effect.
	 * @return  the pluginUUID of the effect (or null if not set)
	 */
	public String getPluginUuid()
	{
		return this.pluginUuid;
	}
	
	/**
	 * Sets the pluginUUID of the effect.
	 * @param uuid  the desired UUID
	 */
	public void setPluginUuid(String uuid)
	{
		this.pluginUuid = uuid;
	}
	
	/**
	 * Gets the animation data of the effect (static/custom effects only).
	 * @return  the animation data of the effect
	 */
	public String getAnimData()
	{
		return this.animData;
	}
	
	/**
	 * Sets the animation data of the effect (static/custom effects only).
	 * @param data  the animation data of the effect
	 */
	public void setAnimData(String data)
	{
		this.animData = data;
	}
	
	private String paletteToJSON(Color[] palette)
	{
		StringBuilder json = new StringBuilder();
		json.append("[");
		for (int i = 0; i < palette.length; i++)
		{
			Color color = palette[i];
			json.append("{\"hue\":" + color.hue + "," +
						"\"saturation\":" + color.saturation + "," +
						"\"brightness\":" + color.brightness);
			if (color.probability != -1)
				json.append(",\"probability\":" + color.probability + "}");
			else
				json.append("}");
			if (i < palette.length-1)
				json.append(",");
			else
				json.append("]");
		}
		return json.toString();
	}
	
	/**
	 * Represents a single color in an effect's palette.
	 * Used to store <code>JSON</code>-parsed data.
	 */
	public class Color
	{
		private int hue, saturation, brightness;
		private double probability;
		
		/**
		 * Creates a simple instance of <code>Color</code> <u>without</u> probability.
		 * @param hue  the hue of the color
		 * @param saturation  the saturation of the color
		 * @param brightness  the brightness of the color
		 */
		public Color(int hue, int saturation, int brightness)
		{
			this.hue = hue;
			this.saturation = saturation;
			this.brightness = brightness;
			this.probability = -1;
		}
		
		/**
		 * Creates an instance of <code>Color</code> with probability.
		 * @param hue  the hue of the color
		 * @param saturation  the saturation of the color
		 * @param brightness  the brightness of the color
		 * @param probability  the probability of the color appearing in the effect
		 */
		public Color(int hue, int saturation, int brightness, double probability)
		{
			this.hue = hue;
			this.saturation = saturation;
			this.brightness = brightness;
			this.probability = probability;
		}
		
		/**
		 * Get the hue of the color.
		 * @return  the hue
		 */
		public int getHue()
		{
			return this.hue;
		}
		
		/**
		 * Set the hue of the color.
		 * @param hue  the desired hue
		 */
		public void setHue(int hue)
		{
			this.hue = hue;
		}
		
		/**
		 * Get the saturation of the color.
		 * @return  the saturation
		 */
		public int getSaturation()
		{
			return this.saturation;
		}
		
		/**
		 * Set the saturation of the color.
		 * @param saturation  the desired saturation
		 */
		public void setSaturation(int saturation)
		{
			this.saturation = saturation;
		}
		
		/**
		 * Get the brightness of the color.
		 * @return  the brightness
		 */
		public int getBrightness()
		{
			return this.brightness;
		}
		
		/**
		 * Set the brightness of the color.
		 * @param brightness  the desired brightness
		 */
		public void setBrightness(int brightness)
		{
			this.brightness = brightness;
		}
		
		/**
		 * Get the probability of the color.
		 * @return  the probability
		 */
		public double getProbability()
		{
			return this.probability;
		}
		
		/**
		 * Set the probability of the color.
		 * @param probability  the desired probability
		 */
		public void setProbability(double probability)
		{
			this.probability = probability;
		}
	}
	
	/**
	 * A small helper class for creating and managing
	 * complex <code>custom</code>-type effects.
	 */
	public static class Animation
	{
		private Aurora.Panel[] panels;
		private Map<Integer, List<Frame>> frames;
		private Aurora controller;
		
		/**
		 * Creates a new instance of an <code>Animation</code>.
		 * @param controller  the desired Aurora controller
		 * @throws UnauthorizedException  if the Aurora access token is invalid
		 */
		public Animation(Aurora controller)
				throws StatusCodeException, UnauthorizedException
		{
			this.controller = controller;
			panels = controller.panelLayout().getPositionData();
			frames = new HashMap<Integer, List<Frame>>();
			for (Aurora.Panel panel : panels)
				frames.put(panel.getId(), new ArrayList<Frame>());
		}
		
		/**
		 * Creates a new <code>custom</code>-type effect
		 * using the animation data from the <code>Animation</code>.
		 * @param effectName  the desired effect name
		 * @param loop  whether or not the effect will loop
		 * @return  a new <code>custom</code>-type effect
		 * @throws UnauthorizedException  if the access token is invalid
		 */
		public Effect createAnimation(String effectName, boolean loop)
				throws StatusCodeException, UnauthorizedException
		{
			int numPanels = this.controller.panelLayout().getNumPanels(false);
			StringBuilder data = new StringBuilder();
			data.append(numPanels);
			for (int i = 0; i < panels.length; i++)
			{
				Aurora.Panel panel = panels[i];
				int numFrames = frames.get(panel.getId()).size();
				data.append(" " + panel.getId() + " " + numFrames);
				
				for (int j = 0; j < numFrames; j++)
				{
					Frame frame = frames.get(panel.getId()).get(j);
					data.append(" " +
								frame.getRed() + " " +
								frame.getGreen() + " " +
								frame.getBlue() + " " +
								frame.getWhite() + " " +
								frame.getTransitionTime());
				}
			}
			
			return Effect.createCustomEffect(effectName, data.toString(), loop);
		}
		
		/**
		 * Add a new frame (RGBW color and transition time) to the animation.
		 * @param panel  the panel to add the frame to
		 * @param frame  the RGBW color and transition time
		 */
		public void addFrame(Aurora.Panel panel, Frame frame)
		{
			this.frames.get(panel.getId()).add(frame);
		}
		
		/**
		 * Removes a frame (RGBW color and transition time) from the animation.
		 * @param panel  the panel to add to add the frame to
		 * @param frame  the RGBW color and transition time
		 */
		public void removeFrame(Aurora.Panel panel, Frame frame)
		{
			this.frames.get(panel).remove(frame);
		}
		
		/**
		 * Stores an frame's RGBW color and transition time.
		 */
		public static class Frame
		{
			private int r, g, b, w, t;
			
			/**
			 * Creates a new instance of a <code>Frame</code>.
			 * @param red  the red RGBW value of the frame's color
			 * @param green  the green RGBW value of the frame's color
			 * @param blue  the blue RGBW value of the frame's color
			 * @param white  the white RGBW value of the frame's color
			 * @param transitionTime  the duration of transition between
			 * 						  the previous frame and this frame
			 */
			public Frame(int red, int green,
					int blue, int white, int transitionTime)
			{
				this.r = red;
				this.g = green;
				this.b = blue;
				this.w = white;
				this.t = transitionTime;
			}
			
			/**
			 * Gets the red RGBW value of the frame's color.
			 * @return  the frame's red value
			 */
			public int getRed()
			{
				return this.r;
			}
			
			/**
			 * Gets the green RGBW value of the frame's color.
			 * @return  the frame's green value
			 */
			public int getGreen()
			{
				return this.g;
			}
			
			/**
			 * Gets the blue RGBW value of the frame's color.
			 * @return  the frame's blue value
			 */
			public int getBlue()
			{
				return this.b;
			}
			
			/**
			 * Gets the white RGBW value of the frame's color.
			 * @return  the frame's white value
			 */
			public int getWhite()
			{
				return this.w;
			}
			
			/**
			 * Gets the transition time of this frame (the duration of
			 * transition between the previous frame and this frame).
			 * @return  the frame's transition time
			 */
			public int getTransitionTime()
			{
				return this.t;
			}
		}
	}
}
