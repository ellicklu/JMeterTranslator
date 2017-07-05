package ecd.perf.utilities.xmlStream.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities to box or unbox primary types
 * @author EllickLu
 *
 */
public final class Primitives
{
	private final static Map<Class, Class> BOX = new HashMap<Class, Class>();

	private final static Map<Class, Class> UNBOX = new HashMap<Class, Class>();

	static {
		final Class[][] boxing = new Class[][] { { byte.class, Byte.class }, { char.class, Character.class },
				{ short.class, Short.class }, { int.class, Integer.class }, { long.class, Long.class },
				{ float.class, Float.class }, { double.class, Double.class }, { boolean.class, Boolean.class },
				{ void.class, Void.class }, };
		for (int i = 0; i < boxing.length; i++) {
			BOX.put(boxing[i][0], boxing[i][1]);
			UNBOX.put(boxing[i][1], boxing[i][0]);
		}
	}

	static public Class box(final Class type)
	{
		return (Class) BOX.get(type);
	}

	static public Class unbox(final Class type)
	{
		return (Class) UNBOX.get(type);
	}
}
