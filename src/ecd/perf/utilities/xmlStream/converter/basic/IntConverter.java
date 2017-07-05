package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class IntConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(int.class) || type.equals(Integer.class);
	}

	public Object fromString(String str)
	{
		long value = Long.decode(str).longValue();
		if (value < Integer.MIN_VALUE || value > 0xFFFFFFFFl) {
			throw new NumberFormatException("For input string: \"" + str + '"'); //$NON-NLS-1$
		}
		return new Integer((int) value);
	}

}
