package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class ByteConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(byte.class) || type.equals(Byte.class);
	}

	public Object fromString(String str)
	{
		int value = Integer.decode(str).intValue();
		if (value < Byte.MIN_VALUE || value > 0xFF) {
			throw new NumberFormatException("For input string: \"" + str + '"'); //$NON-NLS-1$
		}
		return new Byte((byte) value);
	}
}
