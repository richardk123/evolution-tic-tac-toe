import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kolisek
 */
public class Referee
{

	private final Board board;
	private List<Pattern> endPatterns = new ArrayList<>();
	private int moveCount = 0;

	public Referee(@Nonnull final Board board)
	{
		this.board = board;

		Pattern horizontal = new Pattern();
		horizontal.addRow(1);
		horizontal.addRow(1);
		horizontal.addRow(1);
		horizontal.addRow(1);
		horizontal.addRow(1);

		Pattern vertical = new Pattern();
		vertical.addRow(1, 1, 1, 1, 1);

		Pattern diagonal1 = new Pattern();
		diagonal1.addRow(-1, -1, -1, -1, 1);
		diagonal1.addRow(-1, -1, -1, 1, -1);
		diagonal1.addRow(-1, -1, 1, -1, -1);
		diagonal1.addRow(-1, 1, -1, -1, -1);
		diagonal1.addRow(1, -1, -1, -1, -1);

		Pattern diagonal2 = new Pattern();
		diagonal2.addRow(1, -1, -1, -1, -1);
		diagonal2.addRow(-1, 1, -1, -1, -1);
		diagonal2.addRow(-1, -1, 1, -1, -1);
		diagonal2.addRow(-1, -1, -1, 1, -1);
		diagonal2.addRow(-1, -1, -1, -1, 1);

		endPatterns.add(horizontal);
		endPatterns.add(vertical);
		endPatterns.add(diagonal1);
		endPatterns.add(diagonal2);

	}

	public boolean isGameEnded()
	{
		if (getMoveCount() > (board.getSize() * board.getSize()))
		{
			return true;
		}

		for (Pattern endPattern : endPatterns)
		{
			if (board.containsPattern(endPattern) || board.containsPattern(endPattern.clone().inverse()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isWinnerPlayer1()
	{
		for (Pattern endPattern : endPatterns)
		{
			if (board.containsPattern(endPattern.clone().inverse()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isWinnerPlayer2()
	{
		for (Pattern endPattern : endPatterns)
		{
			if (board.containsPattern(endPattern))
			{
				return true;
			}
		}

		return false;
	}

	public int getMoveCount()
	{
		return moveCount;
	}

	public void setMoveCount(int moveCount)
	{
		this.moveCount = moveCount;
	}
}
