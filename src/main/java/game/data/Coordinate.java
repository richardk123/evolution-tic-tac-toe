package game.data;

/**
 * @author Kolisek
 */
public class Coordinate
{
	private final int x;
	private final int y;

	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public boolean equals(Object coordinate)
	{
		if (!(coordinate instanceof Coordinate))
		{
			return false;
		}

		return this.x == ((Coordinate) coordinate).getX() && this.y == ((Coordinate) coordinate).getY();
	}
}
