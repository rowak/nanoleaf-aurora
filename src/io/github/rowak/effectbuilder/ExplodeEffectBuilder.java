package io.github.rowak.effectbuilder;

import io.github.rowak.Effect;
import io.github.rowak.Effect.Color;

/**
 * A small helper class for easily creating new
 * explode-type effects.
 */
public class ExplodeEffectBuilder implements EffectBuilder
{
	private String name;
	private String version;
	private String colorType;
	private Color[] palette;
	private int maxTransTime, minTransTime;
	private int maxDelayTime, minDelayTime;
	private float explodeFactor;
	private Effect.Direction direction;
	private boolean loop;
	
	/**
	 * Creates an instance of the <code>ExplodeEffectBuilder</code>
	 */
	public ExplodeEffectBuilder()
	{
		this.colorType = "HSB";
		this.version = "1.0";
		this.direction = Effect.Direction.OUTWARDS;
		this.maxTransTime = 10;
		this.minTransTime = 10;
		this.maxDelayTime = 10;
		this.minDelayTime = 10;
	}
	
	/**
	 * Creates a new explode-type effect using the data
	 * stored in the <code>EffectBuilder</code>
	 */
	public Effect build()
	{	
		Effect effect = new Effect();
		effect.setName(name);
		effect.setVersion(version);
		effect.setAnimType(Effect.Type.EXPLODE);
		effect.setColorType(colorType);
		effect.setPalette(palette);
		effect.setMaxTransTime(maxTransTime);
		effect.setMinTransTime(minTransTime);
		effect.setMaxDelayTime(maxDelayTime);
		effect.setMinDelayTime(minDelayTime);
		effect.setFlowFactor(explodeFactor);
		effect.setDirection(direction);
		effect.setLoop(loop);
		return effect;
	}

	public ExplodeEffectBuilder setName(String name)
	{
		this.name = name;
		return this;
	}

	public ExplodeEffectBuilder setColorType(String colorType)
	{
		this.colorType = colorType;
		return this;
	}

	public ExplodeEffectBuilder setPalette(Color[] palette)
	{
		this.palette = palette;
		return this;
	}
	
	/**
	 * Sets the maximum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setMaxTransTime(int transTime)
	{
		this.maxTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the minimum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setMinTransTime(int transTime)
	{
		this.minTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the maximum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setMaxDelayTime(int delay)
	{
		this.maxDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the minimum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setMinDelayTime(int delay)
	{
		this.minDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the explosion factor for the effect.
	 * @param explodeFactor  the explosion factor for the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setExplodeFactor(float explodeFactor)
	{
		this.explodeFactor = explodeFactor;
		return this;
	}
	
	/**
	 * Sets the overall direction of movement for the effect.
	 * @param direction  the direction of movement for the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setDirection(Effect.Direction direction)
	{
		this.direction = direction;
		return this;
	}

	public ExplodeEffectBuilder setLoop(boolean loop)
	{
		this.loop = loop;
		return this;
	}
	
	/**
	 * Sets the version of the effect.
	 * <br><b>Note: This value should always be "1.0".</b>
	 * @param version  the version of the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public ExplodeEffectBuilder setVersion(String version)
	{
		this.version = version;
		return this;
	}
}
