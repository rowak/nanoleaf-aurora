package io.github.rowak.nanoleafapi.schedule.builder;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.github.rowak.nanoleafapi.schedule.Schedule;
import io.github.rowak.nanoleafapi.schedule.ScheduleAction;
import io.github.rowak.nanoleafapi.schedule.ScheduleRepeat;

/**
 * A small helper class for creating
 * schedules from scratch.
 */
public class ScheduleBuilder
{
	private Schedule schedule;
	
	/**
	 * Creates an empty schedule builder.
	 */
	public ScheduleBuilder()
	{
		schedule = new Schedule(0, null, false,
				null, null, null);
	}
	
	/**
	 * Creates a new schedule object using
	 * the information from this builder.
	 * @return  a schedule object
	 */
	public Schedule build()
	{
		if (schedule.getSetId() == null)
		{
			schedule.setSetId(getRandomSetId());
		}
		if (schedule.getStartTime() == null)
		{
			schedule.setStartTime(Calendar.getInstance().getTime());
		}
		if (schedule.getAction() == null)
		{
			throw new NullPointerException("Schedule action is null");
		}
		if (schedule.getRepeat() == null)
		{
			throw new NullPointerException("Schedule repeat is null");
		}
		return schedule;
	}
	
	/**
	 * Sets the unique integer identifier for the schedule.
	 * @param id  the integer identifier
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setId(int id)
	{
		schedule.setId(id);
		return this;
	}
	
	/**
	 * Sets the set identifier for the schedule. This field is <b>NOT</b> required,
	 * and will be automatically generated if left unset. This is a special identifier
	 * made up of two random UUIDs separated by the delimiter "--".<br>
	 * For example: "9b6a491f-c290-452c-b9f4-9a3001278bc5--031f382b-497b-4823-995a-be4791d761c3".
	 * @param setId  the set identifier
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setSetId(String setId)
	{
		schedule.setSetId(setId);
		return this;
	}
	
	/**
	 * Sets if the schedule will be enabled or not.
	 * @param enabled  if the schedule should be enabled
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setEnabled(boolean enabled)
	{
		schedule.setEnabled(enabled);
		return this;
	}
	
	/**
	 * Sets the time when the schedule will start running.
	 * @param startTime  the start time
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setStartTime(Date startTime)
	{
		schedule.setStartTime(startTime);
		return this;
	}
	
	/**
	 * Sets the action that performs when the schedule is run.
	 * @param action  the schedule action
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setAction(ScheduleAction action)
	{
		schedule.setAction(action);
		return this;
	}
	
	/**
	 * Sets the time and frequency for the schedule to repeat.
	 * @param repeat  the schedule repeat object
	 * @return  this schedule builder
	 */
	public ScheduleBuilder setRepeat(ScheduleRepeat repeat)
	{
		schedule.setRepeat(repeat);
		return this;
	}
	
	private String getRandomSetId()
	{
		return UUID.randomUUID() + "--" + UUID.randomUUID();
	}
}
