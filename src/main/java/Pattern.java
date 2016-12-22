import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kolisek
 */
public class Pattern
{
	public static final Integer ANY_VALUE = -1;

	private List<List<Integer>> data = new ArrayList<>();

	public Pattern addRow(@Nonnull final List<Integer> rowData)
	{
		for (Integer integer : rowData)
		{
			if (integer == null || (!integer.equals(-2) && !integer.equals(ANY_VALUE) && !integer.equals(0) && !integer.equals(1)))
			{
				throw new RuntimeException("unknown value");
			}
		}

		data.add(rowData);
		return this;
	}

	public Pattern addRow(@Nonnull final Integer... rowData)
	{
		return addRow(Arrays.asList(rowData));
	}

	public Pattern inverse()
	{
		for (List<Integer> rows : data)
		{
			for (int i = 0; i < rows.size(); i++)
			{
				Integer value = rows.get(i);

				if (value.equals(FieldValue.P1.getValue()))
				{
					rows.set(i, FieldValue.P2.getValue());
				}
				else if (value.equals(FieldValue.P2.getValue()))
				{
					rows.set(i, FieldValue.P1.getValue());
				}

			}
		}
		return this;
	}

	@Nonnull
	public Pattern clone()
	{
		Pattern result = new Pattern();

		for (List<Integer> row : data)
		{
			List<Integer> newRow = new ArrayList<>();
			result.data.add(newRow);

			for (Integer value : row)
			{
				newRow.add(value);
			}
		}

		return result;
	}

	public boolean isSame(@Nonnull final Pattern pattern)
	{
		List<Integer> data1 = pattern.data.stream().flatMap(Collection::stream).collect(Collectors.toList());
		List<Integer> data2 = this.data.stream().flatMap(Collection::stream).collect(Collectors.toList());

		int size1 = data1.size();
		int size2 = data2.size();

		if (size1 != size2)
		{
			return false;
		}

		for (int i = 0; i < data1.size(); i++)
		{
			Integer val1 = data1.get(i);
			Integer val2 = data2.get(i);

			if (Objects.equals(val1, ANY_VALUE) || Objects.equals(val2, ANY_VALUE))
			{
				continue;
			}

			if (!val1.equals(val2))
			{
				return false;
			}
		}

		return true;
	}

	public void clear()
	{
		this.data.clear();
	}

	public int getWidth()
	{
		return data.stream().mapToInt(List::size).max().orElse(0);
	}

	public int getHeight()
	{
		return data.size();
	}

	@Nonnull
	public List<Coordinate> getCoordinatesOf(int type)
	{

		List<Coordinate> result = new ArrayList<>();

		for (int y = 0; y < data.size(); y ++)
		{
			 List<Integer> row = data.get(y);

			for (int x = 0; x < row.size(); x ++)
			{
				if (row.get(x).equals(type))
				{
					result.add(new Coordinate(x, y));
				}
			}
		}

		return result;
	}

	/**
	 * possible values -2, -1, 0, 1
	 * -2 empty field
	 * -1 any value
	 * 0 player1
	 * 1 player2
	 */
	public List<List<Integer>> getData()
	{
		return data;
	}
}
