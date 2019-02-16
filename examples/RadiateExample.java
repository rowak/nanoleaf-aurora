import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.github.rowak.Aurora;
import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.Panel;
import io.github.rowak.StatusCodeException;
import io.github.rowak.effectbuilder.CustomEffectBuilder;

/*
 * This example shows how a larger effect can be simulated using many smaller
 * custom effects. The "radiate" effect is used as this example.
 * 
 * The radiate effect cycles through each color in the palette every few seconds.
 * The current color is placed at a random location with the brightest color,
 * then the surrounding panels are given a darker color which results in a
 * sort of ripple effect.
 */
public class RadiateExample
{
	// These are the colors that the simulated effect will cycle through
	java.awt.Color[] PALETTE =
		{
			// red
			new java.awt.Color(255, 0, 0),
			// purple
	   		new java.awt.Color(255, 0, 212),
	   		// green
	   		new java.awt.Color(0, 255, 21),
	   		// blue
	   		new java.awt.Color(0, 21, 255),
	   		// cyan
	   		new java.awt.Color(0, 255, 255)
		};
	
	// Represents the index of the current color in the palette
	int paletteIndex = 0;
	// The aurora object being used
	Aurora aurora;
	
	public RadiateExample() throws Exception
	{
		/* Remember to replace the YOUR_AURORA_IP and
		 * YOUR_API_KEY strings with your actual aurora ip and api key */
		aurora = new Aurora("YOUR_AURORA_IP", 16021,
				"v1", "YOUR_API_KEY");
		
		/*
		 * Create a timer to change the effect every 5 seconds
		 */
		new Timer().scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				burst();
			}
		}, 0, 5000);
	}
	
	/*
	 * The burst method creates a burst effect for a single color in the radiate effect.
	 * One panel will be chosen to have the brightest version of the current color, while
	 * the panels surrounding that panel will have darker versions of the current color.
	 */
	public void burst()
	{
		try
		{
			// Get an array of all the connected panels
			Panel[] panels = aurora.panelLayout().getPanels();
			Random random = new Random();
			// Select a random panel to be the "center" of the effect
			Panel panel = panels[random.nextInt(panels.length-1)];
			// Get the next color in the palette
			java.awt.Color color = PALETTE[paletteIndex];
			if (paletteIndex < PALETTE.length-1)
				paletteIndex++;
			else
				paletteIndex = 0;
			// Create a custom effect builder to build the new effect
			CustomEffectBuilder seb = new CustomEffectBuilder(aurora);
			/*
			 * Create a list to keep track of the panels that have been
			 * added to the effect using their panel ids
			 */
			List<Integer> marked = new ArrayList<Integer>();
			/*
			 * Set the initial transition time for the first panel in the
			 * effect (minimum allowed is 1)
			 */
			final int INITIAL_TIME = 1;
			// Set the explode factor (or additional transition time)
			final int EXPLODE_FACTOR = 3;
			// Calculate the darker colors for the neighbors of the chosen panel
			setNeighbors(panel, marked, EXPLODE_FACTOR, 
					panels, seb, color, INITIAL_TIME);
			
			// Build the effect and display it on the aurora
			Effect ef = seb.build("", false);
			aurora.effects().displayEffect(ef);
		}
		catch (StatusCodeException sce)
		{
			sce.printStackTrace();
		}
	}
	
	/*
	 * The setNeighbors method iterates through each panel outwards
	 * from the initial panel, and sets the delay time and color
	 * to a higher value and a darker value for each iteration.
	 */
	public void setNeighbors(Panel panel, final List<Integer> marked,
			final int explodeFactor, Panel[] panels, CustomEffectBuilder seb,
			java.awt.Color lastColor, int time) throws StatusCodeException
	{
		// Create a darker version of the current color
		lastColor = lastColor.darker();
		// Increase the transition time for the next set of neighbors
		time += explodeFactor;
		for (Panel p : panel.getNeighbors(panels))
		{
			if (!marked.contains(p.getId()))
			{
				/*
				 * Add panel p to the effect with the current last color
				 * (which may be darker than the current color)
				 */
				seb.addFrame(p, new Frame(lastColor.getRed(),
							lastColor.getGreen(), lastColor.getBlue(), 0, time));
				// Mark this panel so it can't be added to the effect again
				marked.add(p.getId());
				// Repeat this method for the next set of neighbors
				setNeighbors(p, marked, explodeFactor, panels, seb, lastColor, time);
				
				/*
				 * NOTE: I discovered that returning the above line (setNeighbors(p, ...))
				 * when the return type of this method is not void causes the simulated
				 * effect to behave in a really cool way.
				 */
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new RadiateExample();
	}
}
