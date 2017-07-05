package ecd.perf.utilities.xmlStream.IO;

import java.util.regex.Matcher;

/**
 * XML IO utilities of filtering the input stream or output stream to fit for XML format
 * @author EllickLu
 *
 */
public class XmlIOUtils
{
	public static final String ARRAY_NAME_REPLACEMENT = "__array__"; //$NON-NLS-1$

	/**
	 * escape special char
	 * @param s
	 * @return
	 */
	public static String getEscaped(String s)
	{
		StringBuffer result = new StringBuffer(s.length() + 10);
		for (int i = 0; i < s.length(); ++i) {
			appendEscapedChar(result, s.charAt(i));
		}
		return result.toString();
	}

	/**
	 * change class name to xml name, reaplace '$'
	 * @param s
	 * @return
	 */
	public static String getEscapedName(String s)
	{
		String str = s.replaceAll("\\$", "___"); //$NON-NLS-1$ //$NON-NLS-2$
		if (str.indexOf("[L") == 0 && str.indexOf(";") == str.length() - 1) { //$NON-NLS-1$ //$NON-NLS-2$
			str = str.replaceFirst("\\[L", Matcher.quoteReplacement(ARRAY_NAME_REPLACEMENT)); //$NON-NLS-1$
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * change xml name to class name
	 * @param s
	 * @return
	 */
	public static String unescapeXmlName(String s)
	{
		String str = s.replaceAll("___", Matcher.quoteReplacement("\\.")); //$NON-NLS-1$ //$NON-NLS-2$
		if (str.indexOf(ARRAY_NAME_REPLACEMENT) == 0) {
			str = str.replaceFirst(ARRAY_NAME_REPLACEMENT, Matcher.quoteReplacement("[L")) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return str;
	}

	public static String unescapeXmlText(String text)
	{
		return (text != null) ? text.trim() : text;
	}

	private static String getReplacement(char c)
	{
		switch (c) {
			case '<':
				return "lt"; //$NON-NLS-1$
			case '>':
				return "gt"; //$NON-NLS-1$
			case '"':
				return "quot"; //$NON-NLS-1$
			case '\'':
				return "apos"; //$NON-NLS-1$
			case '&':
				return "amp"; //$NON-NLS-1$
			case '\r':
				return "#x0D"; //$NON-NLS-1$
			case '\n':
				return "#x0A"; //$NON-NLS-1$
			case '\u0009':
				return "#x09"; //$NON-NLS-1$
		}
		return null;
	}

	private static void appendEscapedChar(StringBuffer buffer, char c)
	{
		String replacement = getReplacement(c);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		}
		else {
			buffer.append(c);
		}
	}

	public static void main(String args[])
	{
		String str = "[Loracle.oats.scripting.models.store.util.xml.test.SimpleClass"; //$NON-NLS-1$
		System.out.println(getEscapedName(str));
		System.out.println(unescapeXmlName(getEscapedName(str)));
	}
}
