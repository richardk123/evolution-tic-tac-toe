package game.data;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kolisek
 */
public class FieldValue
{
	private FieldType type;
	private final int x;
	private final int y;
	private final List<FieldValueChangeListener> listeners = new ArrayList<>();

	public FieldValue(@Nonnull FieldType type, int x, int y)
	{
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public FieldType getType()
	{
		return type;
	}

	public void setType(@Nonnull final FieldType type)
	{
		boolean change = type.equals(this.type);

		this.type = type;

		if (change)
		{
			listeners.forEach(l -> l.valueChange(type));
		}
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void addValueChangeListener(@Nonnull final FieldValueChangeListener listener)
	{
		listeners.add(listener);
	}


}
