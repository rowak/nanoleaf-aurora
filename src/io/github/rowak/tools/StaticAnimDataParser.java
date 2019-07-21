package io.github.rowak.tools;

import java.util.HashMap;
import java.util.Map;

import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.Panel;

/**
 * A helper class to assist with the parsing of
 * STATIC-type effects. This class allows you to get
 * the color of any panel in the static effect.
 */
public class StaticAnimDataParser
{
	private String animData;
	private Map<Integer, Frame> frames;
	
	/**
	 * Create a data parser using an effect.
	 * @param effect  the effect to parse
	 */
	public StaticAnimDataParser(Effect effect)
	{
		this.animData = effect.getAnimData();
		parse();
	}
	
	/**
	 * Create a data parser using the animation data.
	 * @param animData  the animation data to parse
	 */
	public StaticAnimDataParser(String animData)
	{
		this.animData = animData;
		parse();
	}
	
	/**
	 * Gets a map containing the frames for each panel id.
	 * @return  the frames for each panel id
	 */
	public Map<Integer, Frame> getFrames()
	{
		return frames;
	}
	
	/**
	 * Gets a frame at a panel using a panel id.
	 * @param panelId  the id of the panel
	 * @return  the corresponding frame for the panel
	 */
	public Frame getFrame(int panelId)
	{
		return frames.get(panelId);
	}
	
	/**
	 * Gets a frame at a panel using a panel object
	 * @param panel  the panel object
	 * @return  the corresponding frame for the panel
	 */
	public Frame getFrame(Panel panel)
	{
		return getFrame(panel.getId());
	}
	
	private void parse()
	{
		frames = new HashMap<Integer, Frame>();
		String[] dataTemp = animData.split(" ");
		int[] data = new int[dataTemp.length-1];
		for (int i = 1; i < dataTemp.length; i++)
		{
			data[i-1] = Integer.parseInt(dataTemp[i]);
		}
		
		for (int i = 0; i < data.length; i+=7)
		{
			int panelId = data[i];
			int r = data[i+2];
			int g = data[i+3];
			int b = data[i+4];
			int w = data[i+5];
			int t = data[i+6];
			
			frames.put(panelId, new Frame(r, g, b, w, t));
		}
	}
}
