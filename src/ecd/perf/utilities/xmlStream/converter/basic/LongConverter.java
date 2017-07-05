package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class LongConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(long.class) || type.equals(Long.class);
	}

	public Object fromString(String str)
	{
		return Long.decode(str);
	}

}
