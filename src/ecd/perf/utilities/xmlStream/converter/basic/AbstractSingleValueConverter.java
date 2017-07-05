package ecd.perf.utilities.xmlStream.converter.basic;

import java.util.*;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.core.ConversionContext;



/**
 * abstract converter to convert single valued object to Xml
 * 
 * @author EllickLu
 * 
 */
public abstract class AbstractSingleValueConverter implements SingleValueConverter
{

	public String toString(Object obj)
	{
		return obj == null ? null : obj.toString();
	}

	public abstract Object fromString(String str);

	public int getXmlType(ConversionContext context)
	{
		return Converter.XML_TYPE_ATTRIBUTE;
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
		return context.getDomFactory().element(context.getAliasNameByClass(obj.getClass()));
	}

	public String marshalText(Object obj, ConversionContext context) throws ConversionException
	{
		return String.valueOf(obj);
	}
	
	@Override
	public List<Object> marshalHashTree(Object obj,
			ConversionContext context) throws ConversionException {
		return null;
	}
}
