package ecd.perf.utilities.xmlStream.converter.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class ListConverter implements Converter {

	@Override
	public boolean canConvert(Class classType, ConversionContext context) {
		try {
			classType.asSubclass(List.class);
			return true;
		}
		catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public int getXmlType(ConversionContext context) {
		return Converter.XML_TYPE_ELEMENT;
	}

	@Override
	public List<Attribute> marshalAttributes(Object obj,
			ConversionContext context) throws ConversionException {
		return null;
	}
	
	@Override
	public Element marshalElement(Object obj, ConversionContext context)
			throws ConversionException {
		return context.getDomFactory().element(context.getAliasNameByClass(obj.getClass()));
	}

	@Override
	public List<Object> marshalHashTree(Object obj, ConversionContext context)
			throws ConversionException {
		return null;
	}

	@Override
	public LinkedHashMap<String, Object> marshalProperties(Object obj,
			ConversionContext context) throws ConversionException {
		LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
		List<Object> objList = (List<Object>)obj;
		int itemCount = 0;
		for(Object item : objList) {
			props.put(String.valueOf(++itemCount), item);
		}
		return props;
	}

	@Override
	public String marshalText(Object obj, ConversionContext context)
			throws ConversionException {
		return null;
	}

}
