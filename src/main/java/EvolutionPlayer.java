import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Kolisek
 */
public class EvolutionPlayer implements Player
{
	private final String name;
	private EvolutionUtils evolutionUtils = new EvolutionUtils();

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
					int priority = gene.getPriority();

					if (coordinatePriority.containsKey(coordinate))
					{
						priority = coordinatePriority.get(coordinate) + gene.getPriority();
					}

					coordinatePriority.put(coordinate, priority);
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

	private class Gene
	{

		private final Pattern pattern;
		private final int relativeX;
		private final int relativeY;
		private final int priority;

		private Gene(Pattern pattern,
					 int relativeX,
					 int relativeY,
					 int priority)
		{
			this.pattern = pattern;
			this.relativeX = relativeX;
			this.relativeY = relativeY;
			this.priority = priority;
		}

		/**
		 * generate new gene
		 */
		private Gene()
		{
			int actualGeneSizeX = 1 + (int) (Math.random() * evolutionUtils.getNewGeneMaxSize() - 1);
			int actualGeneSizeY = 1 + (int) (Math.random() * evolutionUtils.getNewGeneMaxSize() - 1);

			if (actualGeneSizeX == 1 && actualGeneSizeY == 1)
			{
				if (Math.random() < 0.5)
				{
					actualGeneSizeX += 1;
				}
				else
				{
					actualGeneSizeY += 1;
				}
			}

			this.pattern = new Pattern();

			Coordinate coordinates = generatePattern(pattern, actualGeneSizeX, actualGeneSizeY);
			this.relativeX = coordinates.getX();
			this.relativeY = coordinates.getY();
			this.priority = new Random().nextInt(evolutionUtils.getNewGeneMaxPriority());
		}

		private Coordinate generatePattern(Pattern pattern, int actualGeneSizeX, int actualGeneSizeY)
		{
			for (int y = 0; y < actualGeneSizeY; y ++)
			{
				List<Integer> rowData = new ArrayList<>();

				for (int x = 0; x < actualGeneSizeX; x ++)
				{
					rowData.add(evolutionUtils.chooseRandomFromArray(
							FieldValue.EMPTY.getValue(),
							FieldValue.P1.getValue(),
							FieldValue.P2.getValue()));
				}
				pattern.addRow(rowData);
			}

			List<Coordinate> coordinates = pattern.getCoordinatesOf(FieldValue.EMPTY.getValue());

			// do not have empty field to place value
			if (coordinates.isEmpty())
			{
				return generatePattern(pattern, actualGeneSizeX, actualGeneSizeY);
			}

			int random = new Random().nextInt(coordinates.size());
			return coordinates.get(random);
		}

		public Pattern getPattern()
		{
			return pattern;
		}

		public int getRelativeX()
		{
			return relativeX;
		}

		public int getRelativeY()
		{
			return relativeY;
		}

		public int getPriority()
		{
			return priority;
		}

		public Gene mutate()
		{
			Pattern newPattern = pattern.clone();

			for (int i = 0; i < newPattern.getData().size(); i++)
			{
				List<Integer> row = newPattern.getData().get(i);

				for (int j = 0; j < row.size(); j++)
				{
					// chance to change gene
					if (evolutionUtils.isChangeAllowed(evolutionUtils.getChangeChangeGenePattern()))
					{
						row.set(j, evolutionUtils.chooseRandomFromArray(
								FieldValue.EMPTY.getValue(),
								FieldValue.P1.getValue(),
								FieldValue.P2.getValue()));
					}
				}
			}

			int newPriority = this.getPriority();

			if (evolutionUtils.isChangeAllowed(evolutionUtils.getChanceChangeGenePriority()))
			{
				newPriority = new Random().nextBoolean() ?
						newPriority - evolutionUtils.getGenePriorityMaxStep() :
						newPriority + evolutionUtils.getGenePriorityMaxStep();
			}

			return new Gene(newPattern, this.getRelativeX(), this.getRelativeY(), newPriority);
		}

	}

	public int getNumberOfGenes()
	{
		return genes.size();
	}
}
