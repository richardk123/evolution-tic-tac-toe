package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * @author Kolisek
 */
public class EvolutionUtils
{
	private static final Properties properties = new Properties();
	private static final EvolutionUtils instance = new EvolutionUtils();

	protected EvolutionUtils()
	{

		try
		{
			InputStream is = getClass().getClassLoader().getResourceAsStream("evolution.properties");
			properties.load(is);
		}
		catch (IOException e)
		{
			throw new RuntimeException("cannot load properties");
		}
	}

	public static EvolutionUtils getInstance()
	{
		return instance;
	}

	public int getNewPlayerGeneCount()
	{
		return Integer.parseInt(properties.getProperty("new.player.gene.count").trim());
	}

	public int getNewGeneMaxPriority()
	{
		return Integer.parseInt(properties.getProperty("new.gene.max.priority").trim());
	}

	public int getNewGeneMaxSize()
	{
		return Integer.parseInt(properties.getProperty("new.gene.max.size").trim());
	}

	public int getChangeChangeGenePattern()
	{
		return Integer.parseInt(properties.getProperty("chance.change.gene.pattern").trim());
	}

	public int getChanceChangeGenePriority()
	{
		return Integer.parseInt(properties.getProperty("chance.change.gene.priority").trim());
	}

	public int getGenePriorityMaxStep()
	{
		return Integer.parseInt(properties.getProperty("gene.priority.max.step").trim());
	}

	public int getChanceRemoveGene()
	{
		return Integer.parseInt(properties.getProperty("chance.remove.gene").trim());
	}

	public int getChanceAddGene()
	{
		return Integer.parseInt(properties.getProperty("chance.add.gene").trim());
	}

	public boolean isChangeAllowed(int percentage)
	{
		return new Random().nextInt(100) <= percentage;
	}

	@SafeVarargs
	public final <T> T chooseRandomFromArray(T... array)
	{
		return array[new Random().nextInt(array.length)];
	}

}
