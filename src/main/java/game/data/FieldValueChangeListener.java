package game.data;

import javax.annotation.Nonnull;

/**
 * @author Kolisek
 */
public interface FieldValueChangeListener
{
	/**
	 *
	 * @param fieldType changed type
	 */
	void valueChange(@Nonnull FieldType fieldType);

}
