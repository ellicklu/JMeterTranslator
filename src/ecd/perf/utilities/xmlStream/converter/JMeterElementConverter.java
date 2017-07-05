package ecd.perf.utilities.xmlStream.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;
import ecd.perf.utilities.xmlStream.core.ConversionContext;

public class JMeterElementConverter implements Converter {

	@Override
	public boolean canConvert(Class classType, ConversionContext context) {
		try {
			classType.asSubclass(AbstractModelElement.class);
			return true;
		}
		catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public int getXmlType(ConversionContext context) {
		return XML_TYPE_ELEMENT;
	}

	@Override
	public List<Attribute> marshalAttributes(Object obj,
			ConversionContext context) throws ConversionException {
		List<Attribute> attributes =  new ArrayList<Attribute>();
		AbstractModelElement absobj = (AbstractModelElement)obj;
		if(absobj.getUIClass() != null)
			attributes.add(new Attribute(Converter.XML_GUI_CLASS_NAME_ATTRIBUTE, absobj.getUIClass()));
		if(absobj.getTestclass() != null)
			attributes.add(new Attribute(Converter.XML_TEST_CLASS_NAME_ATTRIBUTE, absobj.getTestclass()));
		AbstractModelElement absObj = (AbstractModelElement)obj;
		Map<String,String> attrs = absObj.getAttributes();
		if(attrs != null) {
			Set<String> keys = attrs.keySet();
			for(String key : keys) {
				String val = attrs.get(key);
				if(val != null)
					attributes.add(new Attribute(key, val));
			}
		}
		return attributes;
	}

	@Override
	public LinkedHashMap<String, Object> marshalProperties(Object obj,
			ConversionContext context) throws ConversionException {
		AbstractModelElement absObj = (AbstractModelElement)obj;
		return absObj.getProperties();
	}

	@Override
	public Element marshalElement(Object obj, ConversionContext context)
			throws ConversionException {
		AbstractModelElement absObj = (AbstractModelElement)obj;
		return context.getDomFactory().element(absObj.getTagName());
	}

	@Override
	public String marshalText(Object obj, ConversionContext context)
			throws ConversionException {
		return null;
	}

	@Override
	public List<Object> marshalHashTree(Object obj,
			ConversionContext context) throws ConversionException {
		AbstractModelElement absObj = (AbstractModelElement)obj;
		List<Object> children =  absObj.getChildren();
		if(children == null && absObj.isPrintEmptyHashTree()){
			children = new ArrayList<Object>();
		}
		return children;
	}

}
