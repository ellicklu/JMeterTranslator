package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class DoubleConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(double.class) || type.equals(Double.class);
	}

	public Object fromString(String str)
	{
		return Double.valueOf(str);
	}
}
