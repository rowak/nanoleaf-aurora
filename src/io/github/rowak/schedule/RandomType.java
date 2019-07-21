package io.github.rowak.schedule;

public enum RandomType
{
	NONE(-1), ALL(0), COLOR(1), RHYTHM(2);
	
	private int value;
	
	public int getValue()
	{
		return value;
	}
	
	private RandomType(int value)
	{
		this.value = value;
	}
}
