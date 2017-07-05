package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class ShortConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(short.class) || type.equals(Short.class);
	}

	public Object fromString(String str)
	{
		int value = Integer.decode(str).intValue();
		if (value < Short.MIN_VALUE || value > 0xFFFF) {
			throw new NumberFormatException("For input string: \"" + str + '"'); //$NON-NLS-1$
		}
		return new Short((short) value);
	}

}
