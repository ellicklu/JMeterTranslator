package ecd.perf.utilities.xmlStream.converter;

import java.lang.reflect.Field;
import java.util.*;

import ecd.perf.utilities.xmlStream.core.ObjectAccessException;



/**
 * Map field name with class, defined in class
 * @author EllickLu
 *
 */
public class FieldDictionary
{
	private final Map<String, Map> keyedByFieldNameCache = Collections.synchronizedMap(new HashMap<String, Map>());
	private final Map<String, Map> keyedByFieldKeyCache = Collections.synchronizedMap(new HashMap<String, Map>());

	class FieldKey
	{
		private String m_name;
		private Class m_definedInClass;
		private int m_index;

		public FieldKey(String name, Class definedIn, int index)
		{
			m_name = name;
			m_definedInClass = definedIn;
			m_index = index;
		}

		public Class getDefinedInClass()
		{
			return m_definedInClass;
		}

		public void setDefinedInClass(Class definedInClass)
		{
			this.m_definedInClass = definedInClass;
		}

		public int getIndex()
		{
			return m_index;
		}

		public void setIndex(int index)
		{
			this.m_index = index;
		}

		public String getName()
		{
			return m_name;
		}

		public void setName(String name)
		{
			this.m_name = name;
		}

	}

	public FieldDictionary()
	{
	}

	public Iterator serializableFieldsFor(Class cls)
	{
		return buildMap(cls, true).values().iterator();
	}

	public Field field(Class cls, String name, Class definedIn) throws ObjectAccessException
	{
		Map fields = buildMap(cls, definedIn != null);
		Field field = (Field) fields.get(definedIn == null ? ((Object) (name))
				: ((Object) (new FieldKey(name, definedIn, 0))));
		if (field == null)
			throw new ObjectAccessException("No such field " + cls.getName() + "." + name); //$NON-NLS-1$ //$NON-NLS-2$
		else
			return field;
	}

	private Map buildMap(Class cls, boolean tupleKeyed)
	{
		String clsName = cls.getName();
		if (!keyedByFieldNameCache.containsKey(clsName))
			synchronized (keyedByFieldKeyCache) {
				if (!keyedByFieldNameCache.containsKey(clsName)) {
					Map<String, Field> keyedByFieldName = new HashMap<String, Field>();
					Map<FieldKey, Field> keyedByFieldKey = new HashMap<FieldKey, Field>();
					for (; !(java.lang.Object.class).equals(cls); cls = cls.getSuperclass()) {
						Field fields[] = cls.getDeclaredFields();
						for (int i = 0; i < fields.length; i++) {
							Field field = fields[i];
							field.setAccessible(true);
							if (!keyedByFieldName.containsKey(field.getName()))
								keyedByFieldName.put(field.getName(), field);
							keyedByFieldKey.put(new FieldKey(field.getName(), field.getDeclaringClass(), i), field);
						}

					}

					keyedByFieldNameCache.put(clsName, keyedByFieldName);
					keyedByFieldKeyCache.put(clsName, keyedByFieldKey);
				}
			}
		return (Map) (tupleKeyed ? keyedByFieldKeyCache.get(clsName) : keyedByFieldNameCache.get(clsName));
	}

}
