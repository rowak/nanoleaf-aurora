package io.github.rowak.effectbuilder;

import io.github.rowak.Effect;
import io.github.rowak.Effect.Color;

/**
 * A small helper class for easily creating new
 * wheel-type effects.
 */
public class WheelEffectBuilder implements EffectBuilder
{
	private String name;
	private String version;
	private String colorType;
	private String animData;
	private Color[] palette;
	private int maxTransTime, minTransTime;
	private float windowSize;
	private Effect.Direction direction;
	private boolean loop;
	
	/**
	 * Creates an instance of the <code>WheelEffectBuilder</code>
	 */
	public WheelEffectBuilder()
	{
		this.colorType = "HSB";
	}
	
	/**
	 * Creates a new wheel-type effect using the data
	 * stored in the <code>EffectBuilder</code>
	 */
	public Effect build()
	{	
		Effect effect = new Effect();
		effect.setName(name);
		effect.setAnimType(Effect.Type.WHEEL);
		effect.setVersion(version);
		effect.setColorType(colorType);
		effect.setAnimData(animData);
		effect.setPalette(palette);
		effect.setMaxTransTime(maxTransTime);
		effect.setMinTransTime(minTransTime);
		effect.setFlowFactor(windowSize);
		effect.setDirection(direction.name().toLowerCase());
		effect.setLoop(loop);
		return effect;
	}

	public WheelEffectBuilder setName(String name)
	{
		this.name = name;
		return this;
	}
	
	/**
	 * Sets the version of the effect.<br>
	 * <b>Note: Currently only supports version 1.0</b>
	 * @param version  the version of the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setVersion(String version)
	{
		this.version = version;
		return this;
	}

	public WheelEffectBuilder setColorType(String colorType)
	{
		this.colorType = colorType;
		return this;
	}
	
	/**
	 * Sets the animation data for the effect.
	 * @param animData  the desired animation data
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setAnimData(String animData)
	{
		this.animData = animData;
		return this;
	}

	public WheelEffectBuilder setPalette(Color[] palette)
	{
		this.palette = palette;
		return this;
	}
	
	/**
	 * Sets the maximum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setMaxTransTime(int transTime)
	{
		this.maxTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the minimum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setMinTransTime(int transTime)
	{
		this.minTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the window size for the effect. This is what
	 * determines the number of colors shown in the effect.
	 * @param windowSize  the desired window size
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setWindowSize(float windowSize)
	{
		this.windowSize = windowSize;
		return this;
	}
	
	/**
	 * Sets the overall direction of movement for the effect.
	 * @param direction  the direction of movement for the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public WheelEffectBuilder setDirection(Effect.Direction direction)
	{
		this.direction = direction;
		return this;
	}

	public WheelEffectBuilder setLoop(boolean loop)
	{
		this.loop = loop;
		return this;
	}
}
