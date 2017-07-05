package ecd.perf.utilities.xmlStream.converter.basic;

import java.math.BigInteger;
import java.util.Map;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.core.ConversionContext;


public class BigIntegerConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(BigInteger.class);
	}

	public Object fromString(String str)
	{
		return new BigInteger(str);
	}
}
