package ecd.perf.utilities.xmlStream.converter.basic;

import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.core.ConversionContext;


public class NullConverter implements SingleValueConverter
{
	public static final String XML_ELEMENT_NULL = "nullElement"; //$NON-NLS-1$

	public boolean canConvert(Class classType, ConversionContext context)
	{
		return true;
	}

	public int getXmlType(ConversionContext context)
	{
		return Converter.XML_TYPE_ELEMENT;
	}

	public List<Attribute> marshalAttributes(Object obj, ConversionContext context) throws ConversionException
	{
		return null;
	}

	public LinkedHashMap<String, Object> marshalProperties(Object obj, ConversionContext context) throws ConversionException
	{
		return null;
	}

	public Element marshalElement(Object obj, ConversionContext context) throws ConversionException
	{
		return context.getDomFactory().element(XML_ELEMENT_NULL);
	}

	public String marshalText(Object obj, ConversionContext context) throws ConversionException
	{
		return null;
	}

	public Object fromString(String str)
	{
		return null;
	}

	public String toString(Object obj)
	{
		return null;
	}

	@Override
	public List<Object> marshalHashTree(Object obj,
			ConversionContext context) throws ConversionException {
		return null;
	}
}
