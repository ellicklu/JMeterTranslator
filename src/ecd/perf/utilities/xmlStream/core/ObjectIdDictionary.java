package ecd.perf.utilities.xmlStream.core;

import java.util.*;

public class ObjectIdDictionary
{
	class IdWrapper
	{
		private final Object m_obj;

		public int hashCode()
		{
			return System.identityHashCode(m_obj);
		}

		public boolean equals(Object other)
		{
			return m_obj == ((IdWrapper) other).m_obj;
		}

		public String toString()
		{
			return m_obj.toString();
		}

		public IdWrapper(Object obj)
		{
			this.m_obj = obj;
		}
	}

	private Map<IdWrapper, Integer> map;
	private Map<Integer, Object> hashCodeMap;

	public ObjectIdDictionary()
	{
		map = new HashMap<IdWrapper, Integer>();
		hashCodeMap = new HashMap<Integer, Object>();
	}

	public void associate(Object obj)
	{
		IdWrapper wrapper = new IdWrapper(obj);
		map.put(wrapper, wrapper.hashCode());
		hashCodeMap.put(wrapper.hashCode(), obj);
	}

	public void associateWithHashcode(Object obj, Integer hashcode)
	{
		IdWrapper wrapper = new IdWrapper(obj);
		map.put(wrapper, hashcode);
		hashCodeMap.put(hashcode, obj);
	}

	public Integer lookupHashCode(Object obj)
	{
		return map.get(new IdWrapper(obj));
	}

	public Object lookupObject(Integer hashcode)
	{
		return hashCodeMap.get(hashcode);
	}

	public boolean containsId(Object item)
	{
		return map.containsKey(new IdWrapper(item));
	}

	public void removeId(Object item)
	{
		map.remove(new IdWrapper(item));
	}
}
