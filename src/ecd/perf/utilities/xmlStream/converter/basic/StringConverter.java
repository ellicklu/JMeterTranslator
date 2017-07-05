package ecd.perf.utilities.xmlStream.converter.basic;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import ecd.perf.utilities.xmlStream.core.ConversionContext;


public class StringConverter extends AbstractSingleValueConverter
{

	/**
	 * A Map to store strings as long as needed to map similar strings onto the same
	 * instance and conserve memory. The map can be set from the outside during
	 * construction, so it can be a lru map or a weak map, sychronised or not.
	 */
	private final Map<String, String> m_cache;

	public StringConverter(Map<String, String> map)
	{
		m_cache = map;
	}

	public StringConverter()
	{
		this(Collections.synchronizedMap(new WeakHashMap<String, String>()));
	}

	public boolean canConvert(Class type, ConversionContext context)
	{
		return type.equals(String.class);
	}

	public Object fromString(String str)
	{
		String s = m_cache.get(str);

		if (s == null) {
			// fill cache
			m_cache.put(str, str);

			s = str;
		}

		return s;
	}

}
