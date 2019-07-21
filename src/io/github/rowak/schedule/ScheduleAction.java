package io.github.rowak.schedule;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A <b>local</b> interface for a schedule action. Schedule actions define
 * the event that occurs when the parent schedule is triggered.
 * Setter methods in this class will not have an effect on the Aurora.
 */
public class ScheduleAction
{
	private boolean on;
	private int brightness;
	private int brightnessDuration;
	private ScheduleEffects effects;
	
	/**
	 * Converts a JSON string action into its corresponding action object.
	 * @param json  the JSON string to convert
	 * @return  a schedule action object
	 */
	public static ScheduleAction fromJSON(String json)
	{
		JSONObject obj = new JSONObject(json);
		ScheduleAction action = new ScheduleAction();
		action.on = obj.getInt("on") == 1 ? true : false;
		JSONObject brightnessJson = obj.getJSONObject("brightness");
		action.brightness = brightnessJson.getInt("value");
		action.brightnessDuration = brightnessJson.getInt("duration");
		try
		{
			action.effects = ScheduleEffects.fromJSON(
					obj.getJSONObject("effects").toString());
		}
		catch (JSONException je) {}
		return action;
	}
	
	/**
	 * Checks if the Aurora will be enabled
	 * when the schedule is run.
	 * @return  true, if the Aurora will be enabled
	 */
	public boolean getOn()
	{
		return on;
	}
	
	/**
	 * Sets if the Aurora will be enabled
	 * when the schedule is run.
	 * @param on  if the Aurora should be enabled
	 */
	public void setOn(boolean on)
	{
		this.on = on;
	}
	
	/**
	 * Gets the brightness that will be applied
	 * to the Aurora when the schedule is run.
	 * @return  the brightness
	 */
	public int getBrightness()
	{
		return brightness;
	}
	
	/**
	 * Sets the brightness that will be applied
	 * to the Aurora when the schedule is run.
	 * @param brightness  the brightness
	 */
	public void setBrightness(int brightness)
	{
		this.brightness = brightness;
	}
	
	/**
	 * Gets the time in seconds that it will take
	 * to transition to the set brightness.
	 * @return  the brightness duration
	 */
	public int getBrightnessDuration()
	{
		return brightnessDuration;
	}
	
	/**
	 * Sets the time in seconds that it will take
	 * to transition to the set brightness.
	 * @param duration  the brightness duration
	 */
	public void setBrightnessDuration(int duration)
	{
		this.brightnessDuration = duration;
	}
	
	/**
	 * Sets the brightness value and duration at the same time.
	 * @param brightness  the brightness
	 * @param duration  the duration/transition between the previous
	 * 					brightness and the new brightness
	 */
	public void setBrightness(int brightness, int duration)
	{
		this.brightness = brightness;
		this.brightnessDuration = duration;
	}
	
	/**
	 * Gets the <b>optional</b> effects object containing
	 * information about the effect being set. This only
	 * applies if an effect is actually being set.
	 * @return  the effects object
	 */
	public ScheduleEffects getEffects()
	{
		return effects;
	}
	
	/**
	 * Sets the <b>optional</b> effects object containing
	 * information about the effect being set. This only
	 * applies if an effect is actually being set.
	 * @param effects  the effects object
	 */
	public void setEffects(ScheduleEffects effects)
	{
		this.effects = effects;
	}
	
	/**
	 * Converts this action object into its corresponding JSON string.
	 * @return  this schedule action in JSON string format
	 */
	public String toJSON()
	{
		JSONObject json = new JSONObject();
		json.put("on", on ? 1 : 0);
		JSONObject brightnessJson = new JSONObject();
		brightnessJson.put("value", brightness);
		brightnessJson.put("duration", brightnessDuration);
		json.put("brightness", brightnessJson);
		if (effects != null)
		{
			json.put("effects", new JSONObject(effects.toJSON()));
		}
		return json.toString();
	}
	
	@Override
	public String toString()
	{
		return toJSON();
	}
}
