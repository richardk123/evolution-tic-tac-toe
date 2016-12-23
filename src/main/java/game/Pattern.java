package game;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import game.data.Coordinate;
import game.data.FieldType;

/**
 * @author Kolisek
 */
public class Pattern
{
	private final String data;

	public Pattern(@Nonnull final String data)
	{
		this.data = data;
	}

	public Pattern inverse()
	{
		return new Pattern(data.replaceAll(FieldType.P1.getValue(), FieldType.P2.getValue() + "R")
				.replaceAll(FieldType.P2.getValue(), FieldType.P1.getValue() + "R")
				.replaceAll("R", ""));
	}

	@Override
	@Nonnull
	public Pattern clone()
	{
		return new Pattern(data);
	}

	public boolean isSame(@Nonnull final Pattern pattern)
	{
		int size1 = pattern.data.length();
		int size2 = this.data.length();

		if (size1 != size2)
		{
			return false;
		}

		for (int i = 0; i < size1; i++)
		{
			String val1 = String.valueOf(pattern.data.charAt(i));
			String val2 = String.valueOf(this.data.charAt(i));

			String wildCard = FieldType.WILDCARD.getValue();

			if (Objects.equals(val1, wildCard) || Objects.equals(val2, wildCard))
			{
				continue;
			}

			if (!Objects.equals(val1, val2))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Pattern))
		{
			return false;
		}

		return ((Pattern) object).data.equals(data);
	}


	public int getWidth()
	{
		return data.split(FieldType.ROW_SEPARATOR.getValue())[0].length();
	}

	public int getHeight()
	{
		return data.split(FieldType.ROW_SEPARATOR.getValue()).length;
	}

	@Nonnull
	public List<Coordinate> getCoordinatesOf(@Nonnull final FieldType fieldType)
	{
		List<Coordinate> result = new ArrayList<>();

		String[] rows = data.split(FieldType.ROW_SEPARATOR.getValue());

		for (int y = 0; y < rows.length; y++)
		{
			for (int x = 0; x < rows[y].length(); x++)
			{
				String value = String.valueOf(rows[y].charAt(x));

				if (fieldType.getValue().equals(value))
				{
					result.add(new Coordinate(x, y));
				}
			}
		}

		return result;
	}

	public String getData()
	{
		return data;
	}
}
