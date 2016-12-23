package game.data;

/**
 * @author Kolisek
 */
public enum FieldType
{
	P1
	{
		@Override
		public String getValue()
		{
			return "O";
		}
	},
	P2
	{
		@Override
		public String getValue()
		{
			return "X";
		}
	},
	EMPTY
	{
		@Override
		public String getValue()
		{
			return ".";
		}
	},
	WILDCARD
	{
		@Override
		public String getValue()
		{
			return "?";
		}
	},
	ROW_SEPARATOR
	{
		@Override
		public String getValue()
		{
			return "\n";
		}
	};

	public abstract String getValue();

	public char getCharValue()
	{
		return new StringBuilder(getValue()).charAt(0);
	}
}
