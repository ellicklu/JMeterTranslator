package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class CharConverter extends AbstractSingleValueConverter
{

	public Object fromString(String str)
	{
		if (str == null || str.length() == 0)
			return new Character('\0');
		else
			return new Character(str.charAt(0));
	}

	public boolean canConvert(Class class1, ConversionContext context)
	{
		return class1.equals(char.class) || class1.equals(Character.class);
	}
}
