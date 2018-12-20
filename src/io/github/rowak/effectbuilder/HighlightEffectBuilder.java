package io.github.rowak.effectbuilder;

import io.github.rowak.Color;
import io.github.rowak.Effect;

/**
 * A small helper class for easily creating new
 * highlight-type effects.
 */
public class HighlightEffectBuilder implements EffectBuilder
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
	 * Creates an instance of the <code>HighlightEffectBuilder</code>
	 */
	public HighlightEffectBuilder()
	{
		this.colorType = "HSB";
		this.version = "1.0";
		this.maxBrightness = 100;
		this.minBrightness = 100;
		this.maxTransTime = 10;
		this.minTransTime = 10;
		this.maxDelayTime = 10;
		this.minDelayTime = 10;
	}
	
	/**
	 * Creates a new highlight-type effect using the data
	 * stored in the <code>EffectBuilder</code>
	 */
	public Effect build()
	{	
		Effect effect = new Effect();
		effect.setName(name);
		effect.setVersion(version);
		effect.setAnimType(Effect.Type.HIGHLIGHT);
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

	public HighlightEffectBuilder setName(String name)
	{
		this.name = name;
		return this;
	}

	public HighlightEffectBuilder setColorType(String colorType)
	{
		this.colorType = colorType;
		return this;
	}
	
	/**
	 * Sets the animation data for the effect.
	 * @param animData  the desired animation data
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setAnimData(String animData)
	{
		this.animData = animData;
		return this;
	}

	public HighlightEffectBuilder setPalette(Color[] palette)
	{
		this.palette = palette;
		return this;
	}
	
	/**
	 * Sets the maximum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMaxTransTime(int transTime)
	{
		this.maxTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the minimum transition time for the effect.
	 * @param transTime  the desired transition time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMinTransTime(int transTime)
	{
		this.minTransTime = transTime;
		return this;
	}
	
	/**
	 * Sets the maximum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMaxDelayTime(int delay)
	{
		this.maxDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the minimum delay time for the effect.
	 * @param delay  the desired delay time
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMinDelayTime(int delay)
	{
		this.minDelayTime = delay;
		return this;
	}
	
	/**
	 * Sets the maximum brightness for the effect.
	 * @param brightness  the desired brightness
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMaxBrightness(int brightness)
	{
		this.maxDelayTime = brightness;
		return this;
	}
	
	/**
	 * Sets the minimum brightness for the effect.
	 * @param brightness  the desired brightness
	 * @return  the current <code>EffectBuilder</code>
	 */
	public HighlightEffectBuilder setMinBrightness(int brightness)
	{
		this.minDelayTime = brightness;
		return this;
	}

	public HighlightEffectBuilder setLoop(boolean loop)
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
	public HighlightEffectBuilder setVersion(String version)
	{
		this.version = version;
		return this;
	}
}
