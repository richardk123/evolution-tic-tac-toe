package player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import game.Board;
import game.data.Coordinate;

/**
 * @author Kolisek
 */
public interface Player
{

	@Nullable
	Coordinate nextTurn(@Nonnull Board board);

	@Nonnull
	String getName();

}
