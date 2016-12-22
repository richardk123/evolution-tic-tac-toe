import javax.annotation.Nonnull;

/**
 * @author Kolisek
 */
public class Game
{
	public static final int BOARD_SIZE = 20;

	private final Player player1;
	private final Player player2;
	private final Board board;
	private final Referee referee;

	private boolean nextTurnPlayer1 = true;

	public Game(@Nonnull final Player player1,
				@Nonnull final Player player2)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.board = new Board(BOARD_SIZE);
		this.referee = new Referee(board);
	}

	public void start()
	{

		int moveCount = 0;

		int boardSum = board.getBoardSum();

		while(!referee.isGameEnded())
		{
			referee.setMoveCount(moveCount);

			if (nextTurnPlayer1)
			{
				play(player1, FieldValue.P1);
			}
			else
			{
				play(player2, FieldValue.P2);
			}

			// when board was not changed, end the match
			if (board.getBoardSum() == boardSum)
			{
				break;
			}

			boardSum = board.getBoardSum();
			moveCount++;
			nextTurnPlayer1 = !nextTurnPlayer1;
		}

		if (moveCount > 20)
		{
//			render();
//			renderGame();
		}
	}

	private void render()
	{
		String winner = "none";

		if (referee.isWinnerPlayer1())
		{
			winner = player1.getName();
		}

		if (referee.isWinnerPlayer2())
		{
			winner = player2.getName();
		}

		System.out.println(String.format("player win: %s move count: %s", winner, referee.getMoveCount()));
	}

	private void renderGame()
	{
		for (int y = 0; y < board.getSize(); y++)
		{
			for (int x = 0; x < board.getSize(); x++)
			{
				switch (board.getBoard()[y][x])
				{
					case -2 : System.out.print("_"); break;
					case 1 :  System.out.print("X"); break;
					case 0 :  System.out.print("O"); break;
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	private void play(Player player, FieldValue value)
	{
		Coordinate coordinate = player.nextTurn(board);

		if (coordinate != null)
		{
			board.fill(coordinate, value);
		}
	}

	public Board getBoard()
	{
		return board;
	}

	public Referee getReferee()
	{
		return referee;
	}
}
