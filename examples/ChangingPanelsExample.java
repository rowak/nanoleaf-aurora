import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.github.rowak.Aurora;
import io.github.rowak.Panel;

/*
 * This example shows how the color of a panel can be easily
 * changed using either the Aurora.Effects.setPanelColor()
 * method or the Aurora.ExternalStreaming.setPanel() method.
 * By default, this code will use the former method to set
 * the panel color.
 */
public class ChangingPanelsExample
{
	public static void main(String[] args) throws Exception
	{
		/*
		 * Remember to replace the YOUR_AURORA_IP and
		 * YOUR_API_KEY strings with your actual aurora ip and api key
		 */
		final Aurora aurora = new Aurora("YOUR_AURORA_IP", 16021,
				"v1", "YOUR_API_KEY");
		
		// Get the available panels
		final Panel[] panels = aurora.panelLayout().getPanels();
		
		// Enable external streaming mode
		//aurora.externalStreaming().enable();
		
		// Create a timer that updates every second
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					Random rand = new Random();
					
					// Randomly select a panel and an rgb color
					Panel panel = panels[rand.nextInt(panels.length)];
					int r = rand.nextInt(255);
					int g = rand.nextInt(255);
					int b = rand.nextInt(255);
					
					// Update the panel with the selected color
					aurora.effects().setPanelColor(panel, r, g, b, 1);
					
					// Alternatively update the panel with external streaming
					// (only if line 21 and line 45 are uncommented and line 41 is commented)
					//aurora.externalStreaming().setPanel(panels[panel], r, g, b, 10);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}, 500, 1000);
	}
}
