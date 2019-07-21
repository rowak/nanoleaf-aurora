package io.github.rowak.schedule;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

/**
 * A <b>local</b> interface for a schedule repeat. Schedule repeats
 * define the how often and how long a schedule will repeat.
 * The setter methods in this class will not have an effect on the Aurora.
 */
public class ScheduleRepeat
{
	private RepeatType intervalType;
	private int intervalValue;
	private Date endTime;
	
	/**
	 * Creates a new schedule repeat object that defines
	 * how often and how long a schedule repeats.
	 * @param intervalType  the type of interval
	 * @param intervalValue  the value of the interval
	 * 						 (for example, <b>3</b> days)
	 * @param endTime  the time after which the
	 * 				   schedule will no longer be run
	 */
	public ScheduleRepeat(RepeatType intervalType,
			int intervalValue, Date endTime)
	{
		this.intervalType = intervalType;
		this.intervalValue = intervalValue;
		this.endTime = endTime;
		if (endTime == null)
		{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2999);
			this.endTime = c.getTime();
		}
	}
	
	/**
	 * Converts a JSON string schedule repeat into
	 * its corresponding schedule repeat object.
	 * @param json  the JSON string to convert
	 * @return  a schedule repeat object
	 */
	public static ScheduleRepeat fromJSON(String json)
	{
		JSONObject obj = new JSONObject(json);
		ScheduleRepeat repeat = new ScheduleRepeat(null, 0, null);
		repeat.intervalType = getIntervalTypeFromInt(obj.getInt("interval_type"));
		repeat.intervalValue = obj.getInt("interval_value");
		repeat.endTime = Schedule.getDateFromJson(
				obj.getJSONObject("end_time").toString());
		return repeat;
	}
	
	/**
	 * Gets the type of interval at which
	 * the schedule will repeat.
	 * @return  the repeat interval type
	 */
	public RepeatType getIntervalType()
	{
		return intervalType;
	}
	
	/**
	 * Sets the interval at which the schedule will repeat.
	 * @param type  the repeat interval type
	 */
	public void setIntervalType(RepeatType type)
	{
		this.intervalType = type;
	}
	
	/**
	 * Gets the interval value at which
	 * the schedule will repeat.
	 * @return  the repeat interval value
	 */
	public int getIntervalValue()
	{
		return intervalValue;
	}
	
	/**
	 * Sets the interval value at which
	 * the schedule will repeat.
	 * @param value  the repeat interval value
	 */
	public void setIntervalValue(int value)
	{
		this.intervalValue = value;
	}
	
	/**
	 * Gets the time at which the schedule
	 * will no longer repeat.
	 * @return  the end time
	 */
	public Date getEndTime()
	{
		return endTime;
	}
	
	/**
	 * Sets the time at which the schedule
	 * will no longer repeat.
	 * @param time  the end time
	 */
	public void setEndTime(Date time)
	{
		this.endTime = time;
	}
	
	private static RepeatType getIntervalTypeFromInt(int type)
	{
		for (RepeatType rt : RepeatType.values())
		{
			if (type == rt.getValue())
			{
				return rt;
			}
		}
		return null;
	}
	
	private String getDefaultEndTimeJson()
	{
		return "{\"year\":3000,\"month\":0,\"day\":0," +
				"\"hour\":0,\"minute\":0,\"time_zone\":0}";
	}
	
	/**
	 * Converts this schedule repeat object to
	 * its corresponding JSON string.
	 * @return  this schedule in JSON string format
	 */
	public String toJSON()
	{
		JSONObject json = new JSONObject();
		json.put("interval_type", intervalType);
		json.put("interval_value", intervalValue);
		Calendar c = Calendar.getInstance();
		c.setTime(endTime);
		if (c.get(Calendar.YEAR) != 2999)
		{
			json.put("end_time", new JSONObject(
					Schedule.getDateAsJson(endTime)));
		}
		else
		{
			json.put("end_time", new JSONObject(
					getDefaultEndTimeJson()));
		}
		return json.toString();
	}
	
	@Override
	public String toString()
	{
		return toJSON();
	}
}
