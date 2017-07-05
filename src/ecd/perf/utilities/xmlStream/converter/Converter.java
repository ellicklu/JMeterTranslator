package ecd.perf.utilities.xmlStream.converter;

import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import ecd.perf.utilities.xmlStream.core.ConversionContext;


/**
 * interface of converting between object and xml document
 * @author EllickLu
 *
 */
public interface Converter
{

	//converter type
	public static final int XML_TYPE_ELEMENT = 1;
	public static final int XML_TYPE_TEXT = 2;
	public static final int XML_TYPE_ATTRIBUTE = 3;

	//class name attribute name
	public static final String XML_GUI_CLASS_NAME_ATTRIBUTE = "guiclass"; //$NON-NLS-1$
	//field name attribute name
	public static final String XML_TEST_CLASS_NAME_ATTRIBUTE = "testclass"; //$NON-NLS-1$
	
	public static final String XML_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	
	public static final String XML_HASHTREE_TAG_NAME = "hashTree"; //$NON-NLS-1$


	/**
	 * determine whether the converter fits the class type
	 * @param classType class type
	 * @param context covnersion context
	 * @return TRUE:if can convert, FALSE:if doesn't fit
	 */
	public abstract boolean canConvert(Class classType, ConversionContext context);

	/**
	 * get converter type
	 * @param context conversion context
	 * @return XML_TYPE_ELEMENT:1, XML_TYPE_TEXT:2, XML_TYPE_ATTRIBUTE:3
	 */
	public abstract int getXmlType(ConversionContext context);

	/**
	 * create xml element with the object class type
	 * @param obj object to be converted
	 * @param context covnersion context
	 * @return Created Element
	 * @throws ConversionException
	 */
	public abstract Element marshalElement(Object obj, ConversionContext context) throws ConversionException;

	/**
	 * explore each single valued field of the object and generate attributes
	 * @param obj Object to be converted
	 * @param context conversion context
	 * @return attribute list
	 * @throws ConversionException
	 */
	public abstract List<Attribute> marshalAttributes(Object obj, ConversionContext context) throws ConversionException;

	/**
	 * explore each none single properties of the object and generate children elements
	 * @param obj Object to be converted
	 * @param context conversion context
	 * @return children elements' map
	 * @throws ConversionException
	 */
	public abstract LinkedHashMap<String, Object> marshalProperties(Object obj, ConversionContext context) throws ConversionException;

	/**
	 * explore each none single value field of the object and generate children elements
	 * @param obj Object to be converted
	 * @param context conversion context
	 * @return children elements' map
	 * @throws ConversionException
	 */
	public abstract List<Object> marshalHashTree(Object obj, ConversionContext context) throws ConversionException;
	
	/**
	 * 
	 * @param obj
	 * @param context
	 * @return
	 * @throws ConversionException
	 */
	public abstract String marshalText(Object obj, ConversionContext context) throws ConversionException;

}