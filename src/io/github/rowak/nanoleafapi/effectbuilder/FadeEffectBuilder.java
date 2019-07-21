package io.github.rowak.nanoleafapi.effectbuilder;

import io.github.rowak.nanoleafapi.Color;
import io.github.rowak.nanoleafapi.Effect;

/**
 * A small helper class for easily creating new
 * explosion-type effects.
 */
public class FadeEffectBuilder implements EffectBuilder
{
	private String name;
	private String version;
	private String colorType;
	private String animData;
	private Color[] palette;
	private int maxTransTime, minTransTime;
	private int maxDelayTime, minDelayTime;
	private int maxBrightness, minBrightness;
	private boolean loop;
	
	/**
	 * Creates an instance of the <code>FadeEffectBuilder</code>
	 */
	public FadeEffectBuilder()
	{
		this.colorType = "HSB";
		this.version = "1.0";
		this.maxBrightness = 0;
		this.minBrightness = 0;
		this.maxTransTime = 10;
		this.minTransTime = 10;
	}
	
	/**
	 * Creates a new fade-type effect using the data
	 * stored in the <code>EffectBuilder</code>
	 */
	public Effect build()
	{	
		Effect effect = new Effect();
		effect.setName(name);
		effect.setVersion(version);
		effect.setAnimType(Effect.Type.FADE);
		effect.setColorType(colorType);
		effect.setAnimData(animData);
		effect.setPalette(palette);
		effect.setMaxTransTime(maxTransTime);
		effect.setMinTransTime(minTransTime);
		effect.setMaxDelayTime(maxDelayTime);
		effect.setMinDelayTime(minDelayTime);
		effect.setMaxBrightness(maxBrightness);
		effect.setMinBrightness(minBrightness);
		effect.setLoop(loop);
		return effect;
	}

	public FadeEffectBuilder setName(String name)
	{
		this.name = name;
		return this;
	}

	public FadeEffectBuilder setColorType(String colorType)
	{
		this.colorType = colorType;
		return this;
	}
	
	/**
	 * Sets the animation data for the effect
	 * @param animData  the desired animation data
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setAnimData(String animData)
	{
		this.animData = animData;
		return this;
	}

	public FadeEffectBuilder setPalette(Color[] palette)
	{
		this.palette = palette;
		return this;
	}
	
	/**
	 * Sets the maximum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMaxTransTime(int transTime)
	{
		this.maxTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the minimum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMinTransTime(int transTime)
	{
		this.minTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the maximum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMaxDelayTime(int delay)
	{
		this.maxDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the minimum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMinDelayTime(int delay)
	{
		this.minDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the maximum brightness for the effect.
	 * @param brightness  the desired brightness time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMaxBrightness(int brightness)
	{
		this.maxDelayTime = brightness;
		return this;
	}
	
	/**
	 * Sets the minimum brightness for the effect.
	 * @param brightness  the desired brightness time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public FadeEffectBuilder setMinBrightness(int brightness)
	{
		this.minDelayTime = brightness;
		return this;
	}
	
	public FadeEffectBuilder setLoop(boolean loop)
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
	public FadeEffectBuilder setVersion(String version)
	{
		this.version = version;
		return this;
	}
}
