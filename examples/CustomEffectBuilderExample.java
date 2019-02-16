import io.github.rowak.Aurora;
import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.Panel;
import io.github.rowak.effectbuilder.CustomEffectBuilder;

/*
 * This example shows how effects can easily be programatically built
 * using the effect builders. This example focuses on the builder for
 * creating custom effects.
 */
public class CustomEffectBuilderExample
{
	public static void main(String[] args) throws Exception
	{
		/*
		 * Remember to replace the YOUR_AURORA_IP and
		 * YOUR_API_KEY strings with your actual aurora ip and api key
		 */
		Aurora aurora = new Aurora("YOUR_AURORA_IP", 16021,
				"v1", "YOUR_API_KEY");
		
		// Get an array of all the panels
		Panel[] panels = aurora.panelLayout().getPanels();
		
		// Initialize the builder using the aurora object
		CustomEffectBuilder ceb = new CustomEffectBuilder(aurora);
		
		/*
		 * Add two arbitrary frames to the first panel in the array.
		 * The first frame sets the panel to red with a transition time of 20.
		 * The second frame sets the panel to green with a transition time of 20
		 */
		ceb.addFrame(panels[0], new Frame(255, 0, 0, 0, 20));
		ceb.addFrame(panels[0], new Frame(0, 255, 0, 0, 20));
		
		/*
		 * Add two arbitrary frames to the second panel in the array.
		 * The first frame sets the panel to green with a transition time of 20.
		 * The second frame sets the panel to blue with a transition time of 20
		 */
		ceb.addFrame(panels[1], new Frame(0, 255, 0, 0, 20));
		ceb.addFrame(panels[1], new Frame(0, 0, 255, 0, 20));
		
		/*
		 * Adds two arbitrary frames to each panel in the array.
		 * The first frame sets each panel to yellow with a transition time of 20.
		 * The second frame sets each panel to blue with a transtition time of 20
		 */
		ceb.addFrameToAllPanels(new Frame(255, 255, 0, 0, 20));
		ceb.addFrameToAllPanels(new Frame(0, 0, 255, 0, 20));
		
		// Build the effect (create the animData) and store it in an effect object
		Effect effect = ceb.build("My Animation", true);
		
		// Display the created effect on the aurora
		aurora.effects().displayEffect(effect);
	}
}
