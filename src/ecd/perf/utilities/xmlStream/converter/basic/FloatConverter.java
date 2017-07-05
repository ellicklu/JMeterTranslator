package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class FloatConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(float.class) || type.equals(Float.class);
	}

	public Object fromString(String str)
	{
		return Float.valueOf(str);
	}
}
