package io.github.rowak.schedule;

public enum RepeatType
{
	ONCE(-1), MINUTE(0), HOURLY(1),
	DAILY(2), WEEKLY(3), MONTHLY(4);
	
	private int value;
	
	public int getValue()
	{
		return value;
	}
	
	private RepeatType(int value)
	{
		this.value = value;
	}
}
