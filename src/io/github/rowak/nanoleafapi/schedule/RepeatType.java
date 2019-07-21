package io.github.rowak.nanoleafapi.schedule;

/**
 * Available interval types that decide how
 * often a schedule should repeat.
 */
public enum RepeatType
{
	ONCE(-1), MINUTE(0), HOURLY(1),
	DAILY(2), WEEKLY(3), MONTHLY(4);
	
	private int value;
	
	/**
	 * Gets the actual value of the type.
	 * @return  the value of the type
	 */
	public int getValue()
	{
		return value;
	}
	
	private RepeatType(int value)
	{
		this.value = value;
	}
}
