package io.github.rowak.effectbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rowak.Aurora;
import io.github.rowak.Panel;
import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.StatusCodeException;
import io.github.rowak.StatusCodeException.UnauthorizedException;

/**
 * A small helper class for creating and managing
 * complex <code>custom</code>-type effects.
 */
public class CustomEffectBuilder
{
	private Panel[] panels;
	private Map<Integer, List<Frame>> frames;
	
	/**
	 * Creates a new <code>CustomEffectBuilder</code> object.
	 * @param controller  the desired Aurora controller
	 * @throws UnauthorizedException  if the Aurora access token is invalid
	 */
	public CustomEffectBuilder(Aurora controller)
			throws StatusCodeException, UnauthorizedException
	{
		panels = controller.panelLayout().getPanels();
		frames = new HashMap<Integer, List<Frame>>();
		for (Panel panel : panels)
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
		int numPanels = 0;
		for (Panel p : panels)
		{
			if (frames.get(p.getId()).size() > 0)
			{
				numPanels++;
			}
		}
		StringBuilder data = new StringBuilder();
		data.append(numPanels);
		for (int i = 0; i < panels.length; i++)
		{
			Panel panel = panels[i];
			int numFrames = frames.get(panel.getId()).size();
			if (numFrames > 0)
			{
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
		}
		
		return Effect.createCustomEffect(effectName, data.toString(), loop);
	}
	
	/**
	 * Adds a frame to all panels in the effect.
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder addFrameToAllPanels(Frame frame)
	{
		for (Panel p : panels)
		{
			frames.get(p.getId()).add(frame);
		}
		return this;
	}
	
	/**
	 * Adds a new frame (RGBW color and transition time) to the effect.
	 * @param panel  the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder addFrame(Panel panel, Frame frame)
	{
		return addFrame(panel.getId(), frame);
	}
	
	/**
	 * Adds a new frame (RGBW color and transition time) to the effect.
	 * @param panelId  the panelId of the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder addFrame(int panelId, Frame frame)
	{
		if (panelIdIsValid(panelId))
		{
			frames.get(panelId).add(frame);
		}
		else
		{
			throw new IllegalArgumentException("Panel with id " +
					panelId + " does not exist.");
		}
		return this;
	}
	
	/**
	 * Removes a frame (RGBW color and transition time) from the effect.
	 * @param panel  the panel to add to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder removeFrame(Panel panel, Frame frame)
	{
		return removeFrame(panel.getId(), frame);
	}
	
	/**
	 * Removes a frame (RGBW color and transition time) from the effect.
	 * @param panelId  the panelId of the panel to add to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>CustomEffectBuilder</code>
	 */
	public CustomEffectBuilder removeFrame(int panelId, Frame frame)
	{
		if (panelIdIsValid(panelId))
		{
			frames.get(panelId).remove(frame);
		}
		else
		{
			throw new IllegalArgumentException("Panel with id " +
					panelId + " does not exist.");
		}
		return this;
	}
	
	private boolean panelIdIsValid(int panelId)
	{
		for (Panel p : panels)
		{
			if (p.getId() == panelId)
			{
				return true;
			}
		}
		return false;
	}
}
