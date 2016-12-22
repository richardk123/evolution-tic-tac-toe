import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kolisek
 */
public class BoardTest
{

	@Test
	public void testGameEndVertical()
	{
		Board board = new Board(10);

		Referee referee = new Referee(board);

		Assert.assertFalse(referee.isGameEnded());

		for (int i = 0; i < 5; i++)
		{
			board.fill(new Coordinate(0, i), FieldValue.P1);
		}
		Assert.assertTrue(referee.isGameEnded());
	}

	@Test
	public void testGameEndHorizontal()
	{
		Board board = new Board(10);

		Referee referee = new Referee(board);

		Assert.assertFalse(referee.isGameEnded());

		for (int i = 0; i < 5; i++)
		{
			board.fill(new Coordinate(i, 0), FieldValue.P1);
		}
		Assert.assertTrue(referee.isGameEnded());
	}

	@Test
	public void testGameEndDiagonal1()
	{
		Board board = new Board(10);

		Referee referee = new Referee(board);

		Assert.assertFalse(referee.isGameEnded());

		for (int i = 4; i >= 0; i--)
		{
			board.fill(new Coordinate(i, i), FieldValue.P1);
		}
		Assert.assertTrue(referee.isGameEnded());
	}

	@Test
	public void testGameEndDiagonal2()
	{
		Board board = new Board(10);

		Referee referee = new Referee(board);

		Assert.assertFalse(referee.isGameEnded());

		for (int i = 0; i < 5; i++)
		{
			board.fill(new Coordinate(i, i), FieldValue.P1);
		}
		Assert.assertTrue(referee.isGameEnded());
	}

}
