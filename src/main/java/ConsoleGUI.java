import simulator.EvolutionSimulator;

/**
 * @author Kolisek
 */
public class ConsoleGUI
{

	public static void main(String[] args)
	{
		Thread thread = new Thread(new EvolutionSimulator());
		thread.start();
	}




}
