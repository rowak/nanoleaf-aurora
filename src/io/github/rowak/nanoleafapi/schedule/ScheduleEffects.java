package io.github.rowak.nanoleafapi.schedule;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A <b>local</b> interface for a schedule's effects. Schedule effects define whether a
 * schedule will change to another effect when run. Schedule effects are only included
 * in schedule objects if the schedule wants to change the effect on the Aurora.
 * The setter methods in this class will not have an effect on the Aurora.
 */
public class ScheduleEffects
{
	private boolean randomOn;
	private RandomType randomType;
	private String effectName;
	
	/**
	 * Create a new schedule effects object that defines whether
	 * the effect will be randomly chosen or specified.
	 * @param randomOn  if the effect should be randomly chosen
	 * @param randomType  the type of effects that should be randomly chosen
	 * @param effectName  the name of a specific effect that should be chosen
	 * 					  (must be <code>null</code> if randomOn is true)
	 */
	public ScheduleEffects(boolean randomOn,
			RandomType randomType, String effectName)
	{
		this.randomOn = randomOn;
		this.randomType = randomType;
		this.effectName = effectName;
	}
	
	/**
	 * Converts a JSON string schedule effects object into
	 * its corresponding schedule effects object.
	 * @param json  the JSON string to convert
	 * @return  a schedule effects object
	 */
	public static ScheduleEffects fromJSON(String json)
	{
		JSONObject obj = new JSONObject(json);
		ScheduleEffects effects = new ScheduleEffects(false, null, null);
		effects.randomOn = obj.getBoolean("random_on");
		effects.randomType = getRandomTypeFromInt(obj.getInt("random_type"));
		JSONObject effectJson = null;
		try
		{
			effectJson = obj.getJSONObject("effect");
		}
		catch (JSONException je) {};
		if (effectJson != null)
		{
			effects.effectName = effectJson.getString("animName");
		}
		return effects;
	}
	
	/**
	 * Checks if the effect will be randomly chosen or not.
	 * @return  true, if the random is on
	 */
	public boolean isRandomOn()
	{
		return randomOn;
	}
	
	/**
	 * Sets if the effect will be randomly chosen or not.
	 * @param on  if random should be on
	 */
	public void setRandomOn(boolean on)
	{
		this.randomOn = on;
	}
	
	/**
	 * Gets the type of effects that
	 * will be randomly chosen from.
	 * @return  the random type
	 */
	public RandomType getRandomType()
	{
		return randomType;
	}
	
	/**
	 * Sets the type of effects that
	 * will be randomly chosen from.
	 * @param type  the random type
	 */
	public void setRandomType(RandomType type)
	{
		this.randomType = type;
	}
	
	/**
	 * Gets the name of the effect that will be set.
	 * This field will be <code>null</code> if random is on.
	 * @return  the name of the effect
	 */
	public String getEffectName()
	{
		return effectName;
	}
	
	/**
	 * Sets the name of the effect that will be set.
	 * This field must be <code>null</code> if random is on.
	 * @param effect  the name of the effect
	 */
	public void setEffect(String effect)
	{
		this.effectName = effect;
	}
	
	private static RandomType getRandomTypeFromInt(int type)
	{
		for (RandomType rt : RandomType.values())
		{
			if (type == rt.getValue())
			{
				return rt;
			}
		}
		return null;
	}
	
	/**
	 * Converts this schedule effects object to
	 * its corresponding JSON string.
	 * @return  this schedule effects object
	 * 			in JSON string format
	 */
	public String toJSON()
	{
		JSONObject json = new JSONObject();
		json.put("random_on", randomOn);
		json.put("random_type", randomType);
		JSONObject effectJson = new JSONObject();
		effectJson.put("animName", effectName);
		if (effectName != null)
		{
			json.put("effect", effectJson);
		}
		else
		{
			effectJson = null;
			json.put("effect", JSONObject.NULL);
		}
		return json.toString();
	}
	
	@Override
	public String toString()
	{
		return toJSON();
	}
}
