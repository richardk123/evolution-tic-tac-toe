/**
 * @author Kolisek
 */
public enum FieldValue
{
	P1
			{
				@Override
				public int getValue()
				{
					return 0;
				}
			},
	P2
			{
				@Override
				public int getValue()
				{
					return 1;
				}
			},
	EMPTY
			{
				@Override
				public int getValue()
				{
					return -2;
				}
			};

	public abstract int getValue();
}
