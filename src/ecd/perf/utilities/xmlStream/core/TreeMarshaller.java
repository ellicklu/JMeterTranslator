package ecd.perf.utilities.xmlStream.core;

import java.util.*;

import org.jdom.Element;
import org.jdom.Attribute;

import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.xmlStream.IO.Object2XmlWriter;
import ecd.perf.utilities.xmlStream.converter.ConversionException;
import ecd.perf.utilities.xmlStream.converter.Converter;


/**
 * parse the Object data structure as tree model and convert it into XML document tree model
 * @author EllickLu
 *
 */
public class TreeMarshaller
{
	//object to xml writer
	protected Object2XmlWriter m_writer;

	//conversion context
	private ConversionContext m_conversionContext;

	/**
	 * construct TreeMarshaller
	 * @param writer Object2XmlWriter
	 * @param context conversion context
	 */
	public TreeMarshaller(Object2XmlWriter writer, ConversionContext context)
	{
		m_writer = writer;
		m_conversionContext = context;
	}

	/**
	 * recursively convert object to xml document
	 * @param item Object to be covnertered
	 * @param fieldNameDefinedIn field name of this object declared in its parent object class
	 * @throws ConversionException
	 */
	public void convertToXml(Object item, String fieldNameDefinedIn) throws ConversionException
	{
		//lookup converter by the object's class
		Converter converter = m_conversionContext.lookupConverterForType(item, m_conversionContext);
		Element element = converter.marshalElement(item, m_conversionContext);
		//Attributes
		expandAttributes(converter, item, element, fieldNameDefinedIn);

		boolean isTagStarted = false;
		//Text
		if(expandTextBody(converter, item, element)){
			isTagStarted = true;
		}
		//Properties
		if(expandProperties(converter, item, element)){
			isTagStarted = true;
		}
		//Empty Tag Or Root Tag
		if(!isTagStarted){//convert empty body tag
			if(item instanceof JmeterTestPlan) {
				m_writer.startTag(element, true);
				m_writer.changeLine();
				expandHashTree(converter, item);
				m_writer.endTag(element);
				m_writer.changeLine();
				return;//DO NOT remove this line!!!
			} else {
				//start tag
				m_writer.startTag(element, false);
				m_writer.changeLine();
			}
		}
		
		//HashTree
		expandHashTree(converter, item);
		
		return;
	}
	
	private boolean expandTextBody(Converter converter, Object item, Element element) throws ConversionException {
		String body_text = converter.marshalText(item, m_conversionContext);
		//Body Text
		if (body_text != null && body_text.length() != 0) {//convert tag with text body
			//start tag
			m_writer.startTag(element, true);
			//do tag body
			m_writer.printBodyText(body_text);
			//end tag
			m_writer.endTag(element);
			m_writer.changeLine();
			return true;
		} else {
			return false;
		}
	}
	
	private boolean expandProperties(Converter converter, Object item, Element element) throws ConversionException{
		//get children elements in tag body
		Map<String, Object> properties = converter.marshalProperties(item, m_conversionContext);

		//Properties
		if (properties != null && properties.size() != 0) {//convert tag with children tag
			//start tag
			m_writer.startTag(element, true);
			m_writer.changeLine();
			//do tag body
			Set<String> children_keyset = properties.keySet();
			for (String child_key : children_keyset) {
				convertToXml(properties.get(child_key), child_key);
			}
			m_writer.changeLine();
			//end tag
			m_writer.endTag(element);
			m_writer.changeLine();
			return true;
		} else {
			return false;
		}
	}
	
	private void expandAttributes(Converter converter, Object item, Element element, String fieldNameDefinedIn) throws ConversionException {
		//get attributes in tag
		List<Attribute> attr_list = converter.marshalAttributes(item, m_conversionContext);

		if (fieldNameDefinedIn != null) {
			if (attr_list == null)
				attr_list = new ArrayList<Attribute>();
			attr_list.add(m_conversionContext.getDomFactory().attribute(Converter.XML_NAME_ATTRIBUTE, fieldNameDefinedIn));
		}
		element.setAttributes(attr_list);
	}
	
	private boolean expandHashTree(Converter converter, Object item) throws ConversionException{
		List<Object> hashtree = converter.marshalHashTree(item, m_conversionContext);
		//HashTree
		if(hashtree != null) {
			Element hashTreeElement = m_conversionContext.getDomFactory().element(Converter.XML_HASHTREE_TAG_NAME);
			//start tag
			m_writer.startTag(hashTreeElement, true);
			m_writer.changeLine();
			//do tag content
			for (Object childElement : hashtree) {
				convertToXml(childElement, null);
			}
			//end tag
			m_writer.endTag(hashTreeElement);
			m_writer.changeLine();
			return true;
		} else {
			return false;
		}
	}
}
