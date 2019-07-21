package io.github.rowak.nanoleafapi;

/**
 * Represents a position on the Aurora panel grid.
 */
public class Position
{
	private int x, y, orientation;
	
	/**
	 * Creates a new instance of Position with an x, y
	 * position and orientation.
	 * @param x  the x-value on the grid
	 * @param y  the y-value on the grid
	 * @param orientation  the orientation on the grid in degrees
	 */
	public Position(int x, int y, int orientation)
	{
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}
	
	/**
	 * Gets the x-value of this position.
	 * @return  the x-value
	 */
	public int getX()
	{
		return this.x;
	}
	
	/**
	 * Sets the x-value of this position.
	 * @param x  the x-value
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 * Gets the y-value of this position.
	 * @return  the y-value
	 */
	public int getY()
	{
		return this.y;
	}
	
	/**
	 * Sets the y-value of this position.
	 * @param y  the y-value
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 * Gets the orientation of this position in degrees.
	 * @return  the orientation in degrees
	 */
	public int getOrientation()
	{
		return this.orientation;
	}
	
	/**
	 * Sets the orientation of this position in degrees.
	 * @param orientation  the orientation in degrees
	 */
	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
	}
}
