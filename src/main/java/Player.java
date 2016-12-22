import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
