package io.github.rowak.nanoleafapi.schedule;

/**
 * Available types that decide which types
 * of effects should be randomly selected.
 */
public enum RandomType
{
	NONE(-1), ALL(0), COLOR(1), RHYTHM(2);
	
	private int value;
	
	/**
	 * Gets the actual value of the type.
	 * @return  the value of the type
	 */
	public int getValue()
	{
		return value;
	}
	
	private RandomType(int value)
	{
		this.value = value;
	}
}
