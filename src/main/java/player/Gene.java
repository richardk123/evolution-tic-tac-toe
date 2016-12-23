package player;

import java.util.List;
import java.util.Random;

import game.Pattern;
import game.data.Coordinate;
import game.data.FieldType;
import util.EvolutionUtils;

/**
 * @author Kolisek
 */
public class Gene
{

	private final Pattern pattern;
	private final int relativeX;
	private final int relativeY;
	private final int priority;
	private final EvolutionUtils evolutionUtils = EvolutionUtils.getInstance();

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
	protected Gene()
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

		for (int y = 0; y < actualGeneSizeY; y++)
		{
			for (int x = 0; x < actualGeneSizeX; x++)
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
