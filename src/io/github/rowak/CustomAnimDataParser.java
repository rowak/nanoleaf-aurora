package io.github.rowak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class to assist with the parsing of
 * CUSTOM-type effects. This class allows you to get
 * the frames for any panel in a custom effect.
 */
public class CustomAnimDataParser
{
	private String animData;
	private Map<Integer, List<Frame>> framesByNum;
	private Map<Integer, List<Frame>> framesByPanel;
	
	/**
	 * Creates a data parser using an effect.
	 * @param effect  the effect to parse
	 */
	public CustomAnimDataParser(Effect effect)
	{
		this.animData = effect.getAnimData();
		parse();
	}
	
	/**
	 * Creates a data parser using the animation data.
	 * @param animData  the animation data to parse
	 */
	public CustomAnimDataParser(String animData)
	{
		this.animData = animData;
		parse();
	}
	
	/**
	 * Gets a map containing the frames for frame number
	 * The KEY is the frame number (starting at 0) and the
	 * VALUE is the list of frames for that frame number
	 * (this includes all panels).
	 * @return  the frames for each frame number
	 */
	public Map<Integer, List<Frame>> getFramesByNum()
	{
		return framesByNum;
	}
	
	/**
	 * Gets a map containing the frames for each panel id.
	 * The KEY is the panel id and the VALUE is the list
	 * of frames for that panel.
	 * @return  the frames for each panel id
	 */
	public Map<Integer, List<Frame>> getFramesByPanel()
	{
		return framesByPanel;
	}
	
	/**
	 * Gets the frames for a panel using a panel id.
	 * @param panelId  the id of the panel
	 * @return  the corresponding frames for the panel
	 */
	public List<Frame> getFrames(int panelId)
	{
		return framesByPanel.get(panelId);
	}
	
	/**
	 * Gets the frames for a panel using a panel object.
	 * @param panel  the panel object
	 * @return  the corresponding frames for the panel
	 */
	public List<Frame> getFrames(Panel panel)
	{
		return getFrames(panel.getId());
	}
	
	private void parse()
	{
		framesByNum = new HashMap<Integer, List<Frame>>();
		framesByPanel = new HashMap<Integer, List<Frame>>();
		
		final String[] dataTemp = animData.split(" ");
		final int[] data = new int[dataTemp.length-1];
		for (int i = 1; i < dataTemp.length; i++)
		{
			data[i-1] = Integer.parseInt(dataTemp[i]);
		}
		
		int maxFrames = 0;
		int x = 0;
		while (x < data.length)
		{
			int panelId = data[x];
			int numFrames = data[x+1];
			if (numFrames > maxFrames)
			{
				maxFrames = numFrames;
			}
			for (int i = 0; i < numFrames; i++)
			{
				int r = data[x + 2 + i*5];
				int g = data[x + 3 + i*5];
				int b = data[x + 4 + i*5];
				int w = data[x + 5 + i*5];
				int t = data[x + 6 + i*5];
				
				Frame frame = new Frame(r, g, b, w, t);
				if (!framesByNum.containsKey(i))
				{
					framesByNum.put(i, new ArrayList<Frame>());
				}
				if (!framesByPanel.containsKey(panelId))
				{
					framesByPanel.put(panelId, new ArrayList<Frame>());
				}
				framesByNum.get(i).add(frame);
				framesByPanel.get(panelId).add(frame);
			}
			x += 2 + 5*numFrames;
		}
	}
}
