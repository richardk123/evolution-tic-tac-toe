import game.Board;
import game.data.Coordinate;
import game.data.FieldType;
import game.Referee;
import game.data.FieldValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kolisek
 */
public class BoardTest
{

	@Test
	public void coordinateTest()
	{
		Board board = new Board(5);

		board.setValue(new Coordinate(0, 0), FieldType.P1);
		board.setValue(new Coordinate(1, 0), FieldType.P1);
		board.setValue(new Coordinate(2, 0), FieldType.P1);

		FieldValue[][] val = board.getFieldValues(new Coordinate(0, 0), 3, 1);

		Assert.assertTrue(val[0][0].getType().equals(FieldType.P1));
		Assert.assertTrue(val[0][1].getType().equals(FieldType.P1));
		Assert.assertTrue(val[0][2].getType().equals(FieldType.P1));
	}

	@Test
	public void testGameEndVertical()
	{
		Board board = new Board(5);

		Referee referee = new Referee(board);

		Assert.assertFalse(referee.isGameEnded());

		for (int i = 0; i < 5; i++)
		{
			board.setValue(new Coordinate(0, i), FieldType.P1);
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
			board.setValue(new Coordinate(i, 0), FieldType.P1);
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
			board.setValue(new Coordinate(i, i), FieldType.P1);
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
			board.setValue(new Coordinate(i, i), FieldType.P1);
		}
		Assert.assertTrue(referee.isGameEnded());
	}

	@Test
	public void testBorders()
	{
		Board board = new Board(20);
		Referee referee = new Referee(board);

		for (int i = 16; i < 20; i++)
		{
			board.setValue(new Coordinate(i, 0), FieldType.P1);
		}

		Assert.assertFalse(referee.isGameEnded());
	}

}
