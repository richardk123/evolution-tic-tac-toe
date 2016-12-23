package player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import game.Board;
import game.data.Coordinate;
import game.data.FieldType;
import game.Pattern;
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

	public class Gene
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
			int maxSize = evolutionUtils.getNewGeneMaxSize();
			int minSize = 1;

			int actualGeneSizeX = evolutionUtils.getLinearBiasedRandomNumber(minSize, maxSize);
			int actualGeneSizeY = evolutionUtils.getLinearBiasedRandomNumber(minSize, maxSize);

			if (actualGeneSizeX == 1 && actualGeneSizeY == 1)
			{
				if (new Random().nextBoolean())
				{
					actualGeneSizeX += 1;
				}
				else
				{
					actualGeneSizeY += 1;
				}
			}

			this.pattern = generatePattern(actualGeneSizeX, actualGeneSizeY);
			Coordinate coordinates = findCoordinateToPlaceValue(pattern);
			this.relativeX = coordinates.getX();
			this.relativeY = coordinates.getY();
			this.priority = new Random().nextInt(evolutionUtils.getNewGeneMaxPriority());
		}

		private Pattern generatePattern(int actualGeneSizeX, int actualGeneSizeY)
		{
			String data = "";

			for (int y = 0; y < actualGeneSizeY; y ++)
			{
				for (int x = 0; x < actualGeneSizeX; x ++)
				{
					data += evolutionUtils.chooseRandomFromArray(
									FieldType.EMPTY.getValue(),
									FieldType.P1.getValue(),
									FieldType.P2.getValue());
				}
				data += FieldType.ROW_SEPARATOR.getValue();
			}

			Pattern pattern = new Pattern(data);

			if (pattern.getCoordinatesOf(FieldType.EMPTY).isEmpty())
			{
				return generatePattern(actualGeneSizeX, actualGeneSizeY);
			}

			return pattern;
		}

		private Coordinate findCoordinateToPlaceValue(Pattern pattern)
		{
			List<Coordinate> coordinates = pattern.getCoordinatesOf(FieldType.EMPTY);
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
			StringBuilder newPatternData = new StringBuilder(pattern.getData());

			for (int i = 0; i < newPatternData.length(); i++)
			{
				char currentVal = newPatternData.charAt(i);

				// chance to change gene
				if (currentVal != FieldType.ROW_SEPARATOR.getCharValue() &&
						evolutionUtils.isChangeAllowed(evolutionUtils.getChangeChangeGenePattern()))
				{
					char val = evolutionUtils.chooseRandomFromArray(
							FieldType.EMPTY.getValue(),
							FieldType.P1.getValue(),
							FieldType.P2.getValue()).charAt(0);

					newPatternData.setCharAt(i, val);
				}
			}

			int newPriority = this.getPriority();

			if (evolutionUtils.isChangeAllowed(evolutionUtils.getChanceChangeGenePriority()))
			{
				newPriority = new Random().nextBoolean() ?
						newPriority - evolutionUtils.getGenePriorityMaxStep() :
						newPriority + evolutionUtils.getGenePriorityMaxStep();
			}

			Pattern pattern = new Pattern(newPatternData.toString());

			return new Gene(pattern, getRelativeX(), getRelativeY(), newPriority);
		}

	}

	public int getNumberOfGenes()
	{
		return genes.size();
	}
}
