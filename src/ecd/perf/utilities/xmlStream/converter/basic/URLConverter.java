package ecd.perf.utilities.xmlStream.converter.basic;

import java.net.MalformedURLException;
import java.net.URL;

import ecd.perf.utilities.xmlStream.core.ConversionContext;


/**
 * Converts a java.net.URL to a string.
 *
 * @author J. Matthew Pryor
 */
public class URLConverter extends AbstractSingleValueConverter
{

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(URL.class);
	}

	public Object fromString(String str)
	{
		try {
			return new URL(str);
		}
		catch (MalformedURLException e) {
			return null;
		}
	}

}
