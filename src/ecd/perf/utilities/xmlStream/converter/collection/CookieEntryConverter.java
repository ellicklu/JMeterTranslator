package ecd.perf.utilities.xmlStream.converter.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.model.jmeter.http.CookieEntry;
import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class CookieEntryConverter implements Converter {

	@Override
	public boolean canConvert(Class classType, ConversionContext context) {
		if(classType == CookieEntry.class)
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
		attrs.add(new Attribute("elementType", "Cookie"));
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
		CookieEntry entry = (CookieEntry)obj;
		LinkedHashMap<String,Object> props = new LinkedHashMap<String, Object>();
		props.put("Cookie.name", entry.getName());
		props.put("Cookie.value", entry.getValue());
		props.put("Cookie.domain", entry.getDomain() != null ? entry.getDomain() : "");
		props.put("Cookie.path", entry.getPath() != null ? entry.getPath() : "");
		props.put("Cookie.secure", entry.isSecure());
		props.put("Cookie.expires", entry.getExpires());
		props.put("Cookie.path_specified", entry.isPathSpecified());
		props.put("Cookie.domain_specified", entry.isDomainSpecified());
		return props;
	}

	@Override
	public String marshalText(Object obj, ConversionContext context)
			throws ConversionException {
		return null;
	}
}
