package game;

import javax.annotation.Nonnull;

import game.data.Coordinate;
import game.data.FieldType;
import player.Player;

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

	private int numberOfSkippedTurnsP1 = 0;
	private int numberOfSkippedTurnsP2 = 0;

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

		while(!referee.isGameEnded())
		{

			if (numberOfSkippedTurnsP1 > 3 || numberOfSkippedTurnsP2 > 3)
			{
				break;
			}

			referee.setMoveCount(moveCount);

			if (nextTurnPlayer1)
			{
				play(player1, FieldType.P1);
			}
			else
			{
				play(player2, FieldType.P2);
			}

			moveCount++;
			nextTurnPlayer1 = !nextTurnPlayer1;
		}

		if (moveCount > 20)
		{
			render();
			renderGame();
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
				Coordinate coordinate = new Coordinate(x, y);
				switch (board.getType(coordinate))
				{
					case EMPTY : System.out.print("_"); break;
					case P1    : System.out.print("X"); break;
					case P2    : System.out.print("O"); break;
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	private void play(Player player, FieldType value)
	{
		Coordinate coordinate = player.nextTurn(board);

		if (coordinate != null)
		{
			board.setValue(coordinate, value);

			if (player.equals(player1))
			{
				numberOfSkippedTurnsP1 = 0;
			}

			if (player.equals(player2))
			{
				numberOfSkippedTurnsP2 = 0;
			}
		}
		else if (player.equals(player1))
		{
			numberOfSkippedTurnsP1++;
		}
		else if (player.equals(player2))
		{
			numberOfSkippedTurnsP2++;
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
