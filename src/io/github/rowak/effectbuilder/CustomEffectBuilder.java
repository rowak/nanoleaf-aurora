package io.github.rowak.effectbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rowak.Aurora;
import io.github.rowak.Effect;
import io.github.rowak.StatusCodeException;
import io.github.rowak.Effect.Frame;
import io.github.rowak.StatusCodeException.UnauthorizedException;

/**
 * A small helper class for creating and managing
 * complex <code>custom</code>-type effects.
 */
public class CustomEffectBuilder
{
	private Aurora.Panel[] panels;
	private Map<Integer, List<Frame>> frames;
	private Aurora controller;
	
	/**
	 * Creates a new <code>CustomEffectBuilder</code> object.
	 * @param controller  the desired Aurora controller
	 * @throws UnauthorizedException  if the Aurora access token is invalid
	 */
	public CustomEffectBuilder(Aurora controller)
			throws StatusCodeException, UnauthorizedException
	{
		this.controller = controller;
		panels = controller.panelLayout().getPanels();
		frames = new HashMap<Integer, List<Frame>>();
		for (Aurora.Panel panel : panels)
			frames.put(panel.getId(), new ArrayList<Frame>());
	}
	
	/**
	 * Creates a new <code>custom</code>-type effect
	 * using the animation data from the effect builder.
	 * @param effectName  the desired effect name
	 * @param loop  whether or not the effect will loop
	 * @return  a new <code>custom</code>-type effect
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public Effect build(String effectName, boolean loop)
			throws StatusCodeException, UnauthorizedException
	{
		int numPanels = this.controller.panelLayout().getNumPanels(false);
		StringBuilder data = new StringBuilder();
		data.append(numPanels);
		for (int i = 0; i < panels.length; i++)
		{
			Aurora.Panel panel = panels[i];
			int numFrames = frames.get(panel.getId()).size();
			data.append(" " + panel.getId() + " " + numFrames);
			
			for (int j = 0; j < numFrames; j++)
			{
				Frame frame = frames.get(panel.getId()).get(j);
				data.append(" " +
							frame.getRed() + " " +
							frame.getGreen() + " " +
							frame.getBlue() + " " +
							frame.getWhite() + " " +
							frame.getTransitionTime());
			}
		}
		
		return Effect.createCustomEffect(effectName, data.toString(), loop);
	}
	
	/**
	 * Add a new frame (RGBW color and transition time) to the effect.
	 * @param panel  the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder addFrame(Aurora.Panel panel, Frame frame)
	{
		this.frames.get(panel.getId()).add(frame);
		return this;
	}
	
	/**
	 * Add a new frame (RGBW color and transition time) to the effect.
	 * @param panelId  the panelId of the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder addFrame(int panelId, Frame frame)
	{
		this.frames.get(panelId).add(frame);
		return this;
	}
	
	/**
	 * Removes a frame (RGBW color and transition time) from the effect.
	 * @param panel  the panel to add to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder removeFrame(Aurora.Panel panel, Frame frame)
	{
		this.frames.get(panel.getId()).remove(frame);
		return this;
	}
	
	/**
	 * Removes a frame (RGBW color and transition time) from the effect.
	 * @param panelId  the panelId of the panel to add to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder removeFrame(int panelId, Frame frame)
	{
		this.frames.get(panelId).remove(frame);
		return this;
	}
}
