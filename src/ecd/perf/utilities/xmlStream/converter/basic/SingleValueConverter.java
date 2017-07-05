package ecd.perf.utilities.xmlStream.converter.basic;

import ecd.perf.utilities.xmlStream.converter.Converter;

public interface SingleValueConverter extends Converter
{
	/**
	 * Marshalls an Object into a single value representation.
	 * @param obj the Object to be converted
	 * @return a String with the single value of the Object or <code>null</code>
	 */
	public String toString(Object obj);

	/**
	 * Unmarshalls an Object from its single value representation.
	 * @param str the String with the single value of the Object
	 * @return the Object
	 */
	public Object fromString(String str);

}
