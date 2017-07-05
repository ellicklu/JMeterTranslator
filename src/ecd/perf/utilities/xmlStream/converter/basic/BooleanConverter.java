package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class BooleanConverter extends AbstractSingleValueConverter
{

	public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true); //$NON-NLS-1$ //$NON-NLS-2$

	private final String positive;
	private final String negative;
	private final boolean caseSensitive;

	public BooleanConverter(final String positive, final String negative, final boolean caseSensitive)
	{
		this.positive = positive;
		this.negative = negative;
		this.caseSensitive = caseSensitive;
	}

	public BooleanConverter()
	{
		this("true", "false", false); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean shouldConvert(final Class type, final Object value)
	{
		return true;
	}

	public boolean canConvert(final Class type, ConversionContext context)
	{
		return type.equals(boolean.class) || type.equals(Boolean.class);
	}

	public Object fromString(final String str)
	{
		if (caseSensitive) {
			return positive.equals(str) ? Boolean.TRUE : Boolean.FALSE;
		}
		else {
			return positive.equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public String toString(final Object obj)
	{
		final Boolean value = (Boolean) obj;
		return obj == null ? null : value.booleanValue() ? positive : negative;
	}
}
