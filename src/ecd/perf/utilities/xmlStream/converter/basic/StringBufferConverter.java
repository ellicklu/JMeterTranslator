package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class StringBufferConverter extends AbstractSingleValueConverter
{

	public Object fromString(String str)
	{
		return new StringBuffer(str);
	}

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(StringBuffer.class);
	}
}
