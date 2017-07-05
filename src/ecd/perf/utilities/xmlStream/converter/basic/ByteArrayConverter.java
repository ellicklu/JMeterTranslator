package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class ByteArrayConverter extends AbstractSingleValueConverter
{

	@Override
	public Object fromString(String str)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canConvert(Class classType, ConversionContext context)
	{
		if (classType == byte[].class)
			return true;
		else
			return false;
	}
}
