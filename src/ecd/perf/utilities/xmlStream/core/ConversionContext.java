package ecd.perf.utilities.xmlStream.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom.input.DefaultJDOMFactory;
import org.jdom.input.JDOMFactory;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import ecd.perf.utilities.model.jmeter.ElementEntry;
import ecd.perf.utilities.model.jmeter.http.CookieEntry;
import ecd.perf.utilities.model.jmeter.http.HeaderEntry;
import ecd.perf.utilities.model.jmeter.http.ParamEntry;
import ecd.perf.utilities.xmlStream.IO.XmlIOUtils;
import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.converter.JMeterElementConverter;
import ecd.perf.utilities.xmlStream.converter.basic.*;
import ecd.perf.utilities.xmlStream.converter.collection.CookieEntryConverter;
import ecd.perf.utilities.xmlStream.converter.collection.ElementEntryConverter;
import ecd.perf.utilities.xmlStream.converter.collection.HeaderEntryConverter;
import ecd.perf.utilities.xmlStream.converter.collection.ListConverter;
import ecd.perf.utilities.xmlStream.converter.collection.ParamEntryConverter;


/**
 * the conversion context that includes class aliases, converter map and document factory
 * @author EllickLu
 *
 */
public class ConversionContext
{
	//map from class to element name
	private Map<Object, String> m_classAliasMap;
	//map from element name to class
	private Map<String, Object> m_aliasClassMap;
	//convert lookup that lookups the convert 
	private ConverterLookup m_converterLookup;
	//document factory
	private JDOMFactory m_domFactory = new DefaultJDOMFactory();

	/**
	 * set alias name to class
	 *
	 */
	protected void setupAliases()
	{
		m_classAliasMap = new HashMap<Object, String>();
		m_aliasClassMap = new HashMap<String, Object>();
		aliasClass("class", Class.class); //$NON-NLS-1$
		aliasClass("stringProp", String.class); //$NON-NLS-1$
		aliasClass("boolProp", Boolean.class); //$NON-NLS-1$
		aliasClass("longProp", Long.class); //$NON-NLS-1$
		aliasClass("shortProp", Short.class); //$NON-NLS-1$
		aliasClass("doubleProp", Double.class); //$NON-NLS-1$
		aliasClass("intProp", Integer.class); //$NON-NLS-1$
		aliasClass("floatProp", Float.class); //$NON-NLS-1$
		aliasClass("charProp", Character.class); //$NON-NLS-1$
		aliasClass("bytes", byte[].class); //$NON-NLS-1$
		aliasClass("collectionProp", List.class);
		aliasClass("collectionProp", ArrayList.class);
		aliasClass("elementProp", ElementEntry.class);
		aliasClass("elementProp", CookieEntry.class);
		aliasClass("elementProp", HeaderEntry.class);
		aliasClass("elementProp", ParamEntry.class);
	}

	/**
	 * register converters
	 *
	 */
	protected void setupConverters()
	{
		m_converterLookup = new ConverterLookup();

		//SingleValueConverter
		registerConverter(new IntConverter());
		registerConverter(new FloatConverter());
		registerConverter(new DoubleConverter());
		registerConverter(new LongConverter());
		registerConverter(new ShortConverter());
		registerConverter(new CharConverter());
		registerConverter(new BooleanConverter());
		registerConverter(new ByteConverter());
		registerConverter(new StringConverter());
		registerConverter(new StringBufferConverter());
		registerConverter(new BigIntegerConverter());
		registerConverter(new ByteArrayConverter());
		
		//Collections
		registerConverter(new ListConverter());
		registerConverter(new ElementEntryConverter());
		registerConverter(new CookieEntryConverter());
		registerConverter(new HeaderEntryConverter());
		registerConverter(new ParamEntryConverter());

		//JMeterElement Converter
		registerConverter(new JMeterElementConverter());
	}

	/**
	 * register converter
	 * @param converter
	 */
	public void registerConverter(Converter converter)
	{
		m_converterLookup.registerConverter(converter);
	}
	
	public void registerConverterToFirst(Converter converter)
	{
		m_converterLookup.registerConverterToFirst(converter);
	}

	/**
	 * return converter lookup
	 * @return
	 */
	public ConverterLookup getConverterLookup()
	{
		return m_converterLookup;
	}

	/**
	 * alias class with attribute name
	 * @param name alias name
	 * @param type class type
	 */
	public void aliasClass(String name, Class type)
	{
		m_classAliasMap.put(type, name);
		m_aliasClassMap.put(name, type);
	}

	/**
	 * get alias name by class
	 * @param type
	 * @return
	 */
	public String getAliasNameByClass(Class type)
	{
		if (m_classAliasMap.containsKey(type)) {
			return XmlIOUtils.getEscapedName(m_classAliasMap.get(type));
		}
		else {
			return XmlIOUtils.getEscapedName(type.getName());
		}
	}

	/**
	 * get class by alas attribute name
	 * @param aliasName
	 * @return
	 */
	public Class getClassByAliasName(String aliasName)
	{
		aliasName = XmlIOUtils.unescapeXmlName(aliasName);
		if (m_aliasClassMap.containsKey(aliasName)) {
			Object obj = m_aliasClassMap.get(aliasName);
			if (obj instanceof Class)
				return (Class) obj;
		}
		try {
			Class class_obj = Class.forName(aliasName);
			aliasClass(aliasName, class_obj);
			return class_obj;
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * get document factory
	 * @return
	 */
	public JDOMFactory getDomFactory()
	{
		return m_domFactory;
	}

	/**
	 * get single value converter by class
	 * @param fieldType
	 * @return
	 */
	public SingleValueConverter getSingleValueConverterFromItemType(Class fieldType)
	{
		return m_converterLookup.lookupSingleValueConverter(fieldType, this);
	}

	/**
	 * get converter by class
	 * @param type
	 * @param context
	 * @return
	 */
	public Converter lookupConverterForType(Class type, ConversionContext context)
	{
		return m_converterLookup.lookupConverterForType(type, this);
	}

	/**
	 * get converter by object
	 * @param obj
	 * @param context
	 * @return
	 */
	public Converter lookupConverterForType(Object obj, ConversionContext context)
	{
		if (obj != null) {
			return m_converterLookup.lookupConverterForType(obj.getClass(), context);
		}
		else {
			return m_converterLookup.lookupConverterForType(null, context);
		}
	}
}
