package io.github.rowak.effectbuilder;

import io.github.rowak.Color;
import io.github.rowak.Effect;

/**
 * The <code>EffectBuilder</code> model for the
 * six primary Aurora effect types.
 */
public interface EffectBuilder
{
	/**
	 * Creates a new <code>Effect</code> of the current effect type.
	 * @return  a new <code>Effect</code>
	 */
	public abstract Effect build();
	
	/**
	 * Sets the name of the effect.
	 * @param name  the desired name of the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public abstract EffectBuilder setName(String name);
	
	/**
	 * Sets the color type of the effect.
	 * @param colorType  the desired color type of the effect
	 * @return  the current <code>EffectBuilder</code>
	 */
	public abstract EffectBuilder setColorType(String colorType);
	
	/**
	 * Sets the palette of the effect.
	 * @param palette  the desired palette for the effect to use
	 * @return  the current <code>EffectBuilder</code>
	 */
	public abstract EffectBuilder setPalette(Color[] palette);
	
	/**
	 * Sets whether of not the effect should loop.
	 * @param loop  whether or not the effect should loop
	 * @return  the current <code>EffectBuilder</code>
	 */
	public abstract EffectBuilder setLoop(boolean loop);
}
