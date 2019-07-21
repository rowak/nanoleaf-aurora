package io.github.rowak.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

/**
 * A <i>local</i> interface for an Aurora schedule.
 * Setter methods in this class will not have an effect on the Aurora.
 */
public class Schedule
{
	private int id;
	private String setId;
	private boolean enabled;
	private Date startTime;
	private ScheduleAction action;
	private ScheduleRepeat repeat;
	
	/**
	 * Create a new schedule containing all its data upfront.
	 * @param id  a unique integer identifier
	 * @param setId  a special identifier for the schedule made up of two
	 * 				 random UUIDs separated by the delimiter "--" (formatted as: UUID--UUID).
	 * @param enabled  if the schedule is enabled or not
	 * @param startTime  the time when the schedule will start running
	 * @param action  the action to perform when the schedule is run
	 * @param repeat  the time and frequency for the schedule to repeat
	 */
	public Schedule(int id, String setId, boolean enabled,
			Date startTime, ScheduleAction action, ScheduleRepeat repeat)
	{
		this.id = id;
		this.setId = setId;
		this.enabled = enabled;
		this.startTime = startTime;
		this.action = action;
		this.repeat = repeat;
		if (setId == null)
		{
			this.setId = getRandomSetId();
		}
	}
	
	/**
	 * Converts a JSON string schedule into its corresponding schedule object.
	 * @param json  the JSON string to convert
	 * @return  a schedule object
	 */
	public static Schedule fromJSON(String json)
	{
		JSONObject obj = new JSONObject(json);
		Schedule schedule = new Schedule(0, null, false, null, null, null);
		schedule.id = obj.getInt("id");
		schedule.setId = obj.getString("set_id");
		schedule.enabled = obj.getBoolean("enabled");
		schedule.startTime = getDateFromJson(obj.getJSONObject("start_time").toString());
		schedule.action = ScheduleAction.fromJSON(obj.getJSONObject("action").toString());
		schedule.repeat = ScheduleRepeat.fromJSON(obj.getJSONObject("repeat").toString());
		return schedule;
	}
	
	/**
	 * Gets the schedule's unique integer identifier.
	 * @return  unique integer identifier
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Sets the schedule's unique integer identifier.
	 * @param id  unique integer identifier
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the schedule's set identifier. This is a special identifier
	 * made up of two random UUIDs separated by the delimiter "--".<br>
	 * For example: "9b6a491f-c290-452c-b9f4-9a3001278bc5--031f382b-497b-4823-995a-be4791d761c3".
	 * @return  the set identifier
	 */
	public String getSetId()
	{
		return setId;
	}
	
	/**
	 * Sets the schedule's set identifier. This field is <b>NOT</b> required,
	 * and will be automatically generated if left unset. This is a special identifier
	 * made up of two random UUIDs separated by the delimiter "--".<br>
	 * For example: "9b6a491f-c290-452c-b9f4-9a3001278bc5--031f382b-497b-4823-995a-be4791d761c3".
	 * @param setId  the set identifier
	 */
	public void setSetId(String setId)
	{
		this.setId = setId;
	}
	
	/**
	 * Checks if the schedule is enabled or not.
	 * @return  true, if the schedule is enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * Sets if the schedule is enabled or not.
	 * @param enabled  if the schedule should be enabled
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	/**
	 * Gets the time when the schedule will start running.
	 * @return  the starting time
	 */
	public Date getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Sets the time when the schedule will start running.
	 * @param startTime  the starting time
	 */
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}
	
	/**
	 * Gets the action that performs when the schedule is run.
	 * @return  the schedule action
	 */
	public ScheduleAction getAction()
	{
		return action;
	}
	
	/**
	 * Sets the action that performs when the schedule is run.
	 * @param action  the schedule action
	 */
	public void setAction(ScheduleAction action)
	{
		this.action = action;
	}
	
	/**
	 * Gets the time and frequency for the schedule to repeat.
	 * @return  the schedule repeat object
	 */
	public ScheduleRepeat getRepeat()
	{
		return repeat;
	}
	
	/**
	 * Sets the time and frequency for the schedule to repeat.
	 * @param repeat  the schedule repeat object
	 */
	public void setRepeat(ScheduleRepeat repeat)
	{
		this.repeat = repeat;
	}
	
	private String getRandomSetId()
	{
		return UUID.randomUUID() + "--" + UUID.randomUUID();
	}
	
	/**
	 * Converts this schedule object into its corresponding JSON string.
	 * @return  this schedule in JSON string format
	 */
	public String toJSON()
	{
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("set_id", setId);
		json.put("enabled", enabled);
		json.put("start_time", new JSONObject(getDateAsJson(startTime)));
		json.put("action", new JSONObject(action.toJSON()));
		json.put("repeat", new JSONObject(repeat.toJSON()));
		return json.toString();
	}
	
	/**
	 * Converts a date object into its corresponding JSON string,
	 * while conforming to a JSON structure readable by the Aurora.
	 * This method is the inverse of {@link #getDateFromJson(String)}.
	 * @param date  a date object
	 * @return  the date in JSON string format
	 */
	public static String getDateAsJson(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		JSONObject json = new JSONObject();
		json.put("year", c.get(Calendar.YEAR));
		json.put("month", c.get(Calendar.MONTH)+1);
		json.put("day", c.get(Calendar.DAY_OF_MONTH));
		json.put("hour", c.get(Calendar.HOUR_OF_DAY));
		json.put("minute", c.get(Calendar.MINUTE));
		json.put("time_zone", 0);
		return json.toString();
	}
	
	/**
	 * Converts a JSON formatted date into its corresponding date object.
	 * This method is the inverse of {@link #getDateAsJson(String)}.
	 * @param json
	 * @return
	 */
	public static Date getDateFromJson(String json)
	{
		JSONObject obj = new JSONObject(json);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, obj.getInt("year"));
		c.set(Calendar.MONTH, obj.getInt("month")-1);
		c.set(Calendar.DAY_OF_MONTH, obj.getInt("day"));
		c.set(Calendar.HOUR_OF_DAY, obj.getInt("hour"));
		c.set(Calendar.MINUTE, obj.getInt("minute"));
		return c.getTime();
	}
	
	@Override
	public String toString()
	{
		return toJSON();
	}
}
