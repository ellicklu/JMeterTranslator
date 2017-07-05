package ecd.perf.utilities.xmlStream.core;

import java.util.*;

import ecd.perf.utilities.xmlStream.converter.Converter;
import ecd.perf.utilities.xmlStream.converter.basic.NullConverter;
import ecd.perf.utilities.xmlStream.converter.basic.SingleValueConverter;


public class ConverterLookup
{
	//converters' list
	private final List<Converter> m_converters = new ArrayList<Converter>();
	//null converter
	private final NullConverter m_nullConverter = new NullConverter();

	//map from class type to converter
	private final Map<Class, Converter> m_typeToConverterMap = Collections.synchronizedMap(new HashMap<Class, Converter>());

	/**
	 * lookup converter by class type
	 * @param type class type
	 * @param context conversion context
	 * @return converter
	 */
	public Converter lookupConverterForType(Class type, ConversionContext context)
	{
		if (type == null)
			return m_nullConverter;
		Converter cachedConverter = m_typeToConverterMap.get(type);
		if (cachedConverter != null)
			return cachedConverter;
		for (Iterator iterator = m_converters.iterator(); iterator.hasNext();) {
			Converter converter = (Converter) iterator.next();
			if (converter.canConvert(type, context)) {
				m_typeToConverterMap.put(type, converter);
				return converter;
			}
		}
		return null;
	}

	/**
	 * register converter
	 * @param converter
	 */
	public void registerConverter(Converter converter)
	{
		m_converters.add(converter);
	}
	
	public void registerConverterToFirst(Converter converter)
	{
		m_converters.add(0, converter);
	}

	/**
	 * lookup single value converter by class
	 * @param type class type
	 * @param context covnersion context
	 * @return single value converter
	 */
	public SingleValueConverter lookupSingleValueConverter(Class type, ConversionContext context)
	{
		if (type == null)
			return m_nullConverter;
		Converter cachedConverter = m_typeToConverterMap.get(type);
		if (cachedConverter != null && cachedConverter instanceof SingleValueConverter)
			return (SingleValueConverter) cachedConverter;
		for (Iterator iterator = m_converters.iterator(); iterator.hasNext();) {
			Converter converter = (Converter) iterator.next();
			if (converter.canConvert(type, context) && converter instanceof SingleValueConverter) {
				m_typeToConverterMap.put(type, converter);
				return (SingleValueConverter) converter;
			}
		}
		return m_nullConverter;
	}
}
