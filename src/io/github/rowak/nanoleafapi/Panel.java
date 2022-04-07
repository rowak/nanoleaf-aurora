package io.github.rowak.nanoleafapi;

import java.util.ArrayList;
import java.util.List;

import io.github.rowak.nanoleafapi.StatusCodeException.UnauthorizedException;

/**
 * Represents a single Aurora light panel. Used to
 * store <code>JSON</code>-parsed data.
 */
public class Panel extends Position
{
	private int id, r, g, b, w;
	private int shapeType;
	
	/**
	 * Creates a new instance of a <code>Panel</code>.
	 * @param id  the id of the panel
	 * @param x  the x-value of the panel location
	 * @param y  the y-value of the panel location
	 * @param orientation  the panel's orientation on the Aurora grid
	 */
	public Panel(int id, int x, int y, int orientation)
	{
		super(x, y, orientation);
		this.id = id;
	}
	
	/**
	 * Creates a new instance of a <code>Panel</code>.
	 * @param id  the id of the panel
	 * @param x  the x-value of the panel location
	 * @param y  the y-value of the panel location
	 * @param orientation  the panel's orientation on the Aurora grid
	 * @param shapeType  the panel's shape type
	 */
	public Panel(int id, int x, int y, int orientation, int shapeType)
	{
		this(id, x, y, orientation);
		this.shapeType = shapeType;
	}

	/**
	 * Gets the unique ID for the panel.
	 * @return  the panel's unique ID
	 */
	public int getId()
	{
		return this.id;
	}
	
	/**
	 * Gets the color of the panels.
	 * @return  the color of the panels
	 */
	public Color getColor()
	{
		return Color.fromRGB(r, g, b);
	}

	/**
	 * Gets the panel's shape type
	 * @return  the panel's shape type
	 */
	public int getShapeType()
	{
		return this.shapeType;
	}
	
	/**
	 * Gets the red RGBW value of the panel's color.
	 * @return  the panel's red value
	 */
	public int getRed()
	{
		return this.r;
	}
	
	/**
	 * Sets the red RGBW value of the panel's color.
	 * @param value  the red RGBW value
	 */
	public void setRed(int value)
	{
		this.r = value;
	}
	
	/**
	 * Gets the green RGBW value of the panel's color.
	 * @return  the panel's green value
	 */
	public int getGreen()
	{
		return this.g;
	}
	
	/**
	 * Sets the green RGBW value of the panel's color.
	 * @param value  the green RGBW value
	 */
	public void setGreen(int value)
	{
		this.g = value;
	}
	
	/**
	 * Gets the blue RGBW value of the panel's color.
	 * @return  the panel's blue value
	 */
	public int getBlue()
	{
		return this.b;
	}
	
	/**
	 * Sets the blue RGBW value of the panel's color.
	 * @param value  the blue RGBW value
	 */
	public void setBlue(int value)
	{
		this.b = value;
	}
	
	/**
	 * Gets the white RGBW value of the panel's color.
	 * @return  the panel's white value
	 */
	public int getWhite()
	{
		return this.w;
	}
	
	/**
	 * Sets the white RGBW value of the panel's color.
	 * @param value  the white RGBW value
	 */
	public void setWhite(int value)
	{
		this.w = value;
	}
	
	/**
	 * Sets the RGB values of this panel's color.
	 * @param red  the red RGBW value
	 * @param green  the green RGBW value
	 * @param blue  the blue RGBW value
	 */
	public void setRGB(int red, int green, int blue)
	{
		this.r = red;
		this.g = green;
		this.b = blue;
	}
	
	/**
	 * Sets the RGBW values of the panel's color.
	 * @param red  the red RGBW value
	 * @param green  the green RGBW value
	 * @param blue  the blue RGBW value
	 * @param white  the white RGBW value
	 */
	public void setRGBW(int red, int green,
			int blue, int white)
	{
		setRGB(red, green, blue);
		this.w = white;
	}
	
	/**
	 * Gets the direct neighbors of this panel (maximum is 3, minimum is 1).
	 * @param panels  all connected panels in the Aurora.
	 * @return  an array of type <code>Panel</code> containing the
	 * 			direct neighbors of this panel
	 */
	public Panel[] getNeighbors(Panel[] panels)
	{
		// Distance constant represents the vertical/horizontal/diagonal distance
		// that all neighboring panels are within
		final int DISTANCE_CONST = 86;
		List<Panel> neighbors = new ArrayList<Panel>();
		int p1x = this.getX();
		int p1y = this.getY();
		for (Panel p2 : panels)
		{
			int p2x = p2.getX();
			int p2y = p2.getY();
			if (Math.floor(Math.sqrt(Math.pow((p1x - p2x), 2) +
					Math.pow((p1y - p2y), 2))) == DISTANCE_CONST)
			{
				neighbors.add(p2);
			}
		}
		return neighbors.toArray(new Panel[]{});
	}
	
	/**
	 * Gets the direct neighbors of this panel (maximum is 3, minimum is 0).
	 * @param aurora  the Aurora to get the panels from
	 * @return  an array of type <code>Panel</code> containing the
	 * 			direct neighbors of this panel
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public Panel[] getNeighbors(Aurora aurora)
			throws StatusCodeException, UnauthorizedException
	{
		return this.getNeighbors(aurora.panelLayout().getPanels());
	}
}
