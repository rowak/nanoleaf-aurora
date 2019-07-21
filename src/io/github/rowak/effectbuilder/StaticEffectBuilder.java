package io.github.rowak.effectbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rowak.Aurora;
import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.StatusCodeException;
import io.github.rowak.Panel;
import io.github.rowak.StatusCodeException.UnauthorizedException;

/**
 * A small helper class for creating simple
 * <code>static</code>-type effects.
 */
public class StaticEffectBuilder
{
	private Panel[] panels;
	private Map<Integer, Frame> frames;
	
	/**
	 * Creates a new instance of an <code>StaticEffectBuilder</code>.
	 * @param controller  the desired Aurora controller
	 * @throws UnauthorizedException  if the Aurora access token is invalid
	 */
	public StaticEffectBuilder(Aurora controller)
			throws StatusCodeException, UnauthorizedException
	{
		panels = controller.panelLayout().getPanels();
		frames = new HashMap<Integer, Frame>();
	}
	
	/**
	 * Gets a map of the frames for this effect. The key represents the
	 * panel and the value represents the frame for the corresponding panel.
	 * @return  a map of the frames for this effect
	 */
	public Map<Integer, Frame> getFrames()
	{
		return frames;
	}
	
	/**
	 * Creates a new <code>static</code>-type effect
	 * using the animation data from the <code>StaticEffectBuilder</code>.
	 * @param effectName  the desired effect name
	 * @return  a new <code>custom</code>-type effect
	 * @throws UnauthorizedException  if the access token is invalid
	 */
	public Effect build(String effectName)
			throws StatusCodeException, UnauthorizedException
	{
		StringBuilder data = new StringBuilder();
		data.append(frames.size());
		List<Integer> ids = new ArrayList<Integer>(frames.keySet());
		for (int i = 0; i < panels.length; i++)
		{
			if (ids.contains(panels[i].getId()))
			{
				Panel panel = panels[i];
				Frame frame = frames.get(panel.getId());
				data.append(" " + panel.getId() + " 1");
				data.append(" " +
							frame.getRed() + " " +
							frame.getGreen() + " " +
							frame.getBlue() + " " +
							frame.getWhite() + " " +
							frame.getTransitionTime());
			}
		}
		return Effect.createStaticEffect(effectName, data.toString());
	}
	
	/**
	 * Adds a new frame (RGBW color and transition time) to the effect.
	 * @param panel  the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>StaticEffectBuilder</code>
	 */
	public StaticEffectBuilder setPanel(Panel panel, Frame frame)
	{
		return setPanel(panel.getId(), frame);
	}
	
	/**
	 * Adds a new frame (RGBW color and transition time) to the effect.
	 * @param panelId  the panelId of the panel to add the frame to
	 * @param frame  the RGBW color and transition time
	 * @return the current <code>StaticEffectBuilder</code>
	 */
	public StaticEffectBuilder setPanel(int panelId, Frame frame)
	{
		if (panelIdIsValid(panelId))
		{
			this.frames.put(panelId, frame);
		}
		else
		{
			throw new IllegalArgumentException("Panel with id " +
					panelId + " does not exist.");
		}
		return this;
	}
	
	/**
	 * Adds a frame to all panels in the effect.
	 * @param frame  the RGBW color and transition time
	 * @return  the current <code>StaticEffectBuilder</code>
	 */
	public StaticEffectBuilder setAllPanels(Frame frame)
	{
		for (Panel panel : this.panels)
		{
			this.frames.put(panel.getId(), frame);
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