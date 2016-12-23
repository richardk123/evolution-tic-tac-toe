package player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import game.Board;
import game.data.Coordinate;
import util.EvolutionUtils;

/**
 * @author Kolisek
 */
public class EvolutionPlayer implements Player
{
	private final String name;
	private EvolutionUtils evolutionUtils = EvolutionUtils.getInstance();

	private Boolean isEmpty;
	private List<Gene> genes = new ArrayList<>();

	public EvolutionPlayer(String name)
	{
		this(name, true);
	}

	public EvolutionPlayer(String name, boolean generateGenes)
	{
		this.name = name;

		if (generateGenes)
		{
			for (int i = 0; i < evolutionUtils.getNewPlayerGeneCount(); i++)
			{
				genes.add(new Gene());
			}
		}
	}

	@Override
	public Coordinate nextTurn(@Nonnull Board board)
	{
		if (isEmpty == null)
		{
			isEmpty = board.isBoardEmpty();
		}

		// if this is first turn
		if (isEmpty)
		{
			isEmpty = false;
			int middle = board.getSize() / 2;
			return new Coordinate(middle, middle);
		}
		else
		{
			Map<Coordinate, Integer> coordinatePriority = new HashMap<>();

			for (Gene gene : genes)
			{
				List<Coordinate> coordinates = board.findPattern(gene.getPattern());

				for (Coordinate coordinate : coordinates)
				{
					Coordinate alteredCoordinate = new Coordinate(
							coordinate.getX() + gene.getRelativeX(),
							coordinate.getY() + gene.getRelativeY());

					int priority = gene.getPriority();

					if (coordinatePriority.containsKey(alteredCoordinate))
					{
						priority = coordinatePriority.get(alteredCoordinate) + gene.getPriority();
					}

					coordinatePriority.put(alteredCoordinate, priority);
				}
			}

			int bestPriority = 0;
			Coordinate winCoordinate = null;

			// find win coordinate
			for (Coordinate coordinate : coordinatePriority.keySet())
			{
				int priority = coordinatePriority.get(coordinate);

				if (priority > bestPriority)
				{
					bestPriority = priority;
					winCoordinate = coordinate;
				}
			}

			return winCoordinate;
		}
	}

	public EvolutionPlayer mutate(String name)
	{
		EvolutionPlayer result = new EvolutionPlayer(name, false);

		result.genes.addAll(
				genes.stream()
						.filter((g) -> !evolutionUtils.isChangeAllowed(evolutionUtils.getChanceRemoveGene()))
						.map(Gene::mutate)
						.collect(Collectors.toList()));

		if (evolutionUtils.isChangeAllowed(evolutionUtils.getChanceAddGene()))
		{
			result.genes.add(new Gene());
		}

		return result;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public List<Gene> getGenes()
	{
		return genes;
	}

	public int getNumberOfGenes()
	{
		return genes.size();
	}
}
