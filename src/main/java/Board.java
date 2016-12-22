import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kolisek
 */
public class Board
{

	/**
	 * possible values -2, 0, 1
	 * -2 empty field
	 *  0 player1 field
	 *  1 player2 field
	 */
	private int[][] board;
	private final int size;

	public Board(int size)
	{
		this.size = size;
		board = new int[size][size];

		// empty fields
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				board[y][x] = FieldValue.EMPTY.getValue();
			}
		}
	}

	public void fill(@Nonnull final Coordinate coordinate,
					 @Nonnull final FieldValue field)
	{
		board[coordinate.getY()][coordinate.getX()] = field.getValue();
	}

	public int[][] getBoard()
	{
		return this.board;
	}

	public int getSize()
	{
		return size;
	}

	@Nonnull
	public List<Coordinate> findPattern(@Nonnull final Pattern pattern)
	{
		int patternWidth = pattern.getWidth();
		int patternHeight = pattern.getHeight();

		List<BoardPattern> boardPatterns = new ArrayList<>();

		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x ++)
			{
				// skip edges
				if (x < size - patternWidth && y < size - patternHeight)
				{
					BoardPattern boardPattern = new BoardPattern(x, y, patternWidth, patternHeight);
					boardPatterns.add(boardPattern);
				}

				int value = board[y][x];

				for (BoardPattern bp : boardPatterns)
				{
					bp.addValue(x, y, value);
				}
			}
		}

		List<Coordinate> result = new ArrayList<>();

		for (BoardPattern boardPattern : boardPatterns)
		{
			if (boardPattern.toPattern().isSame(pattern))
			{
				result.add(new Coordinate(boardPattern.x, boardPattern.y));
			}
		}

		return result;
	}

	public boolean containsPattern(@Nonnull final Pattern pattern)
	{
		return !findPattern(pattern).isEmpty();

	}

	@Override
	public Board clone()
	{
		Board result = new Board(size);
		result.board = Arrays.copyOf(board, board.length);
		return result;
	}

	public boolean isBoardEmpty()
	{
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				if (board[y][x] != -2)
				{
					return false;
				}
			}
		}

		return true;
	}

	private class BoardPattern
	{
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private int[][] data;

		private BoardPattern(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			data = new int[height][width];
		}

		protected void addValue(int x, int y, int value)
		{
			if (x >= this.x && x < this.x + width && y >= this.y && y < this.y + height)
			{
				data[y - this.y][x - this.x] = value;
			}
		}

		protected Pattern toPattern()
		{
			Pattern pattern = new Pattern();

			for (int y = 0; y < height; y++)
			{
				List<Integer> values = new ArrayList<>();

				for (int x = 0; x < width; x ++)
				{
					values.add(data[y][x]);
				}

				pattern.addRow(values);
			}

			return pattern;
		}
	}

	public int getBoardSum()
	{
		int result = 0;

		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				result += board[y][x];
			}
		}

		return result;
	}

}
