package io.github.rowak.nanoleafapi;

/**
 * Stores a frame's RGBW color and transition time.
 */
public class Frame
{
	private int r, g, b, w, t;
	
	/**
	 * Creates a new instance of a <code>Frame</code> from an RGBW color.
	 * @param red  the red RGBW value of the frame's color
	 * @param green  the green RGBW value of the frame's color
	 * @param blue  the blue RGBW value of the frame's color
	 * @param white  the white RGBW value of the frame's color
	 * 				 (note: this is currently ignored by the api)
	 * @param transitionTime  the duration of transition between
	 * 						  the previous frame and this frame
	 */
	public Frame(int red, int green,
			int blue, int white, int transitionTime)
	{
		this.r = red;
		this.g = green;
		this.b = blue;
		this.w = white;
		this.t = transitionTime;
	}
	
	/**
	 * Creates a new instance of a <code>Frame</code> from a <code>Color</code>.
	 * @param color  the color of the frame
	 * @param transitionTime  the duration of transition between
	 * 						  the previous frame and this frame
	 */
	public Frame(Color color, int transitionTime)
	{
		this.r = color.getRed();
		this.g = color.getGreen();
		this.b = color.getBlue();
		this.w = 0;
		this.t = transitionTime;
	}
	
	/**
	 * Gets the red RGBW value of the frame's color.
	 * @return  the frame's red value
	 */
	public int getRed()
	{
		return this.r;
	}
	
	/**
	 * Gets the green RGBW value of the frame's color.
	 * @return  the frame's green value
	 */
	public int getGreen()
	{
		return this.g;
	}
	
	/**
	 * Gets the blue RGBW value of the frame's color.
	 * @return  the frame's blue value
	 */
	public int getBlue()
	{
		return this.b;
	}
	
	/**
	 * Gets the white RGBW value of the frame's color.
	 * @return  the frame's white value
	 */
	public int getWhite()
	{
		return this.w;
	}
	
	/**
	 * Gets the transition time of this frame (the duration of
	 * transition between the previous frame and this frame).
	 * @return  the frame's transition time
	 */
	public int getTransitionTime()
	{
		return this.t;
	}
	
	@Override
	public String toString()
	{
		return getClass().getName() + "[r=" + this.r +
				", g=" + this.g + ", b=" + this.b +
				", w=" + this.w + ", t=" + this.t + "]";
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Frame)
		{
			return this.r == ((Frame)other).r &&
					this.g == ((Frame)other).g &&
					this.b == ((Frame)other).b &&
					this.w == ((Frame)other).w &&
					this.t == ((Frame)other).t;
		}
		return false;
	}
}
