package io.github.rowak.schedule.builder;

import io.github.rowak.schedule.ScheduleAction;
import io.github.rowak.schedule.ScheduleEffects;

/**
 * A small helper class for creating
 * schedule actions from scratch.
 */
public class ScheduleActionBuilder
{
	private ScheduleAction action;
	
	/**
	 * Creates an empty schedule action builder.
	 */
	public ScheduleActionBuilder()
	{
		action = new ScheduleAction();
	}
	
	/**
	 * Creates a new schedule action object using
	 * the information from this builder.
	 * @return  a schedule action object
	 */
	public ScheduleAction build()
	{
		return action;
	}
	
	/**
	 * Sets if the Aurora will be enabled
	 * when the schedule is run.
	 * @param on  if the Aurora should be enabled
	 * @return  this schedule action builder
	 */
	public ScheduleActionBuilder setOn(boolean on)
	{
		action.setOn(on);
		return this;
	}
	
	/**
	 * Sets the brightness value and brightness duration.
	 * @param brightness  the brightness
	 * @param duration  the duration/transition between the previous
	 * 					brightness and the new brightness
	 * @return  this schedule action builder
	 */
	public ScheduleActionBuilder setBrightness(
			int brightness, int duration)
	{
		action.setBrightness(brightness, duration);
		return this;
	}
	
	/**
	 * Sets the <b>optional</b> effects object containing
	 * information about the effect being set. This only
	 * applies if an effect is actually being set.
	 * @param effects  the effects object
	 * @return  this schedule action builder
	 */
	public ScheduleActionBuilder setEffects(ScheduleEffects effects)
	{
		action.setEffects(effects);
		return this;
	}
}
