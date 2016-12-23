package game;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.data.Coordinate;
import game.data.FieldType;
import game.data.FieldValue;

/**
 * @author Kolisek
 */
public class Board
{

	private List<FieldValue> boardData = new ArrayList<>();
	private final int size;

	public Board(int size)
	{
		this.size = size;

		// empty fields
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				boardData.add(new FieldValue(FieldType.EMPTY, x, y));
			}
		}
	}

	// todo protected
	public void setValue(@Nonnull final Coordinate coordinate,
						 @Nonnull final FieldType fieldType)
	{
		getValue(coordinate).setType(fieldType);
	}

	public FieldType getType(@Nonnull final Coordinate coordinate)
	{
		return getValue(coordinate).getType();
	}

	public FieldValue getValue(@Nonnull final Coordinate coordinate)
	{
		return boardData.get(((coordinate.getX() + 1) * (coordinate.getY() + 1)) - 1);
	}

	public List<FieldValue> getBoardData()
	{
		return Collections.unmodifiableList(this.boardData);
	}

	public int getSize()
	{
		return size;
	}

	public FieldValue[][] getFieldValues(@Nonnull final Coordinate coordinate,
										 int width, int height)
	{
		if (coordinate.getX() + width > size || coordinate.getY() + height > size)
		{
			throw new RuntimeException("out of border");
		}

		FieldValue[][] result = new FieldValue[height][width];

		for (int yp = 0; yp < height; yp++)
		{
			for (int xp = 0; xp < width; xp++)
			{
				Coordinate findCoordinate = new Coordinate(coordinate.getX() + xp, coordinate.getY() + yp);
				result[yp][xp] = getValue(findCoordinate);
			}
		}

		return result;
	}

	private Pattern createPattern(@Nonnull final Coordinate coordinate,
								  int width, int height)
	{
		if (coordinate.getX() + width > size || coordinate.getY() + height > size)
		{
			throw new RuntimeException("out of border");
		}

		String result = "";

		for (int yp = 0; yp < height; yp++)
		{
			for (int xp = 0; xp < width; xp++)
			{
				Coordinate findCoordinate = new Coordinate(coordinate.getX() + xp, coordinate.getY() + yp);
				result += getType(findCoordinate).getValue();
			}

			result += FieldType.ROW_SEPARATOR.getValue();
		}

		return new Pattern(result);
	}

	@Nonnull
	public List<Coordinate> findPattern(@Nonnull final Pattern pattern)
	{
		int patternWidth = pattern.getWidth();
		int patternHeight = pattern.getHeight();

		// patter cannot be bigger than boardData
		if (patternWidth > size || patternHeight > size)
		{
			return new ArrayList<>();
		}

		int maxX = size - patternWidth + 1;
		int maxY = size - patternHeight + 1;
		List<Coordinate> result = new ArrayList<>();

		for (int y = 0; y < maxY; y++)
		{
			for (int x = 0; x < maxX; x ++)
			{
				Pattern boardPattern = createPattern(new Coordinate(x, y), patternWidth, patternHeight);

				if (boardPattern.isSame(pattern))
				{
					result.add(new Coordinate(x, y));
				}
			}
		}

		return result;
	}

	public boolean containsPattern(@Nonnull final Pattern pattern)
	{
		return !findPattern(pattern).isEmpty();

	}

	public boolean isBoardEmpty()
	{
		return boardData.stream()
				.filter((fv) -> !FieldType.EMPTY.equals(fv.getType()))
				.findAny().isPresent();
	}

}
