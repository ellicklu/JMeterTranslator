package ecd.perf.utilities.xmlStream.converter.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.model.jmeter.ElementEntry;
import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class ElementEntryConverter implements Converter {

	@Override
	public boolean canConvert(Class classType, ConversionContext context) {
		if(classType == ElementEntry.class)
			return true;
		else
			return false;
	}

	@Override
	public int getXmlType(ConversionContext context) {
		return Converter.XML_TYPE_ELEMENT;
	}

	@Override
	public List<Attribute> marshalAttributes(Object obj,
			ConversionContext context) throws ConversionException {
		List<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(new Attribute("elementType", "Argument"));
		return attrs;
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
		ElementEntry entry = (ElementEntry)obj;
		LinkedHashMap<String,Object> props = new LinkedHashMap<String, Object>();
		props.put("Argument.name", entry.getName());
		props.put("Argument.value", entry.getValue());
		props.put("Argument.metadata", entry.getMetadata());
		return props;
	}

	@Override
	public String marshalText(Object obj, ConversionContext context)
			throws ConversionException {
		return null;
	}

}
