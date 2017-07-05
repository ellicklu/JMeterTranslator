package ecd.perf.utilities.expression.file;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;


public class XmlEncoder 
{
	private static final HashMap<String, Character> m_charMap;
	private static String AMP = "&amp;"; //$NON-NLS-1$
	private static String QUOT = "&quot;"; //$NON-NLS-1$
	private static String APOS = "&apos;"; //$NON-NLS-1$
	private static String LT = "&lt;"; //$NON-NLS-1$
	private static String GT = "&gt;"; //$NON-NLS-1$
	private static String UNICODE = "&#x"; //$NON-NLS-1$
	private static String HTML_UNICODE = "&#"; //$NON-NLS-1$
	static {
		m_charMap = new HashMap<String, Character>();
		m_charMap.put("quot", new Character((char) 34));//$NON-NLS-1$
		m_charMap.put("amp", new Character((char) 38));//$NON-NLS-1$
		m_charMap.put("apos", new Character((char) 39));//$NON-NLS-1$
		m_charMap.put("lt", new Character((char) 60));//$NON-NLS-1$
		m_charMap.put("gt", new Character((char) 62));//$NON-NLS-1$
	}

	private static boolean isLetterOrDigit(char c)
	{
		return isLetter(c) || isDigit(c);
	}

	private static boolean isHexDigit(char c)
	{
		return isHexLetter(c) || isDigit(c);
	}

	private static boolean isLetter(char c)
	{
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	private static boolean isHexLetter(char c)
	{
		return ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
	}

	private static boolean isDigit(char c)
	{
		return (c >= '0') && (c <= '9');
	}

		  
	public static String xmlDecode(String value)
	{
		if (value == null)
			return ""; //$NON-NLS-1$
		String toBeDecode;
		Character ch;
		int tmpPos, curInt;

		int maxPos = value.length();
		StringBuffer sb = new StringBuffer(maxPos);
		int curPos = 0;
		while (curPos < maxPos) {
			char curChar = value.charAt(curPos++);
			if (curChar == '&') {
				tmpPos = curPos;
				if (tmpPos < maxPos) {
					char d = value.charAt(tmpPos++);
					if (d == '#') {
						if (tmpPos < maxPos) {
							d = value.charAt(tmpPos++);
							if ((d == 'x') || (d == 'X')) {
								//handle UNICODE characters
								if (tmpPos < maxPos) {
									d = value.charAt(tmpPos++);
									if (isHexDigit(d)) {
										while (tmpPos < maxPos) {
											d = value.charAt(tmpPos++);
											if (!isHexDigit(d)) {
												if (d == ';') {
													toBeDecode = value.substring(
															curPos + 2,
															tmpPos - 1);
													try {
														curInt = Integer.parseInt(toBeDecode,
																16);
														if ((curInt >= 0)
																&& (curInt < 65536)) {
															curChar = (char) curInt;
															curPos = tmpPos;
														}
													}
													catch (NumberFormatException e) {
													}
												}
												break;
											}
										}
									}
								}
							}
							else if (isDigit(d)) {
								while (tmpPos < maxPos) {
									d = value.charAt(tmpPos++);
									if (!isDigit(d)) {
										if (d == ';') {
											toBeDecode = value.substring(curPos + 1,
													tmpPos - 1);
											try {
												curInt = Integer.parseInt(toBeDecode);
												if ((curInt >= 0) && (curInt < 65536)) {
													curChar = (char) curInt;
													curPos = tmpPos;
												}
											}
											catch (NumberFormatException e) {
											}
										}
										break;
									}
								}
							}
						}
					}
					else if (isLetter(d)) {
						while (tmpPos < maxPos) {
							d = value.charAt(tmpPos++);
							if (!isLetterOrDigit(d)) {
								if (d == ';') {
									toBeDecode = value.substring(curPos, tmpPos - 1);
									ch = (Character) m_charMap.get(toBeDecode);
									if (ch != null) {
										curChar = ch.charValue();
										curPos = tmpPos;
									}
								}
								break;
							}
						}
					}
				}
			}
			sb.append(curChar);
		}
		return sb.toString();
	}
	
	/**
	 * Escape special characters.
	 *
	 * For example, should turn <code>I like "Apples" & bananas</code> into
	 * <code>I like &quot;Apples&quot; &amp; bananas</code>. Doesn't currently
	 * use the entity dictionaries defined in the rest of the class; rather,
	 * it just knows about apos, amp, quot, lt, and gt; and encodes other special
	 * characters numerically.
	 * 
	 * @param value String value which needs to be XML encoded
	 */
	public static String xmlEncode(String value)
	{
		StringWriter writer = new StringWriter();
		try {
			xmlEncodeAndWrite(value, writer);
		} 
		catch (IOException e) {
			// StringWriter uses StringBuffer as backing store and no IOExceptions are thrown ever.
			// it is okay to ignore it
		}
		return writer.toString();
	}
	
	/**
	 * Escape special characters.
	 *
	 * For example, should turn <code>I like "Apples" & bananas</code> into
	 * <code>I like &quot;Apples&quot; &amp; bananas</code>. Doesn't currently
	 * use the entity dictionaries defined in the rest of the class; rather,
	 * it just knows about apos, amp, quot, lt, and gt; and encodes other special
	 * characters numerically.
	 * 
	 * @param value String value which needs to be XML encoded 
	 * @param out XML encoded values will be written out to this Writer
	 * @throws IOException An IOException is thrown if there were any errors when writing 
	 * the XML encoded values to the provided Writer
	 */
	public static void xmlEncodeAndWrite(String value, Writer out) throws IOException
	{
		if (value == null)
			return;
		
		char[] ch = value.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
				case '&':
					out.append(AMP);
					break;
				case '"':
					out.append(QUOT);
					break;
				case '\'':
					out.append(APOS);
					break;
				case '<':
					out.append(LT);
					break;
				case '>':
					out.append(GT);
					break;
				case '\r':
				case '\n':
				case '\t':
					out.append(ch[i]);
					break;
				default:
					if ((ch[i] > (char) 126) || (ch[i] < (char) 32)) {
						out.append(UNICODE); 
						out.append(Integer.toHexString((int) ch[i] & 0xFFFF));
						out.append(';');
					}
					else {
						out.append(ch[i]);
					}
					break;
			}
		}
	}
	
	public static boolean canDecode(String value)
	{
		if (value == null)
			return false;
		if (value.contains(AMP) || value.contains(QUOT) || value.contains(LT) || value.contains(GT) || value.contains(UNICODE) || value.contains(HTML_UNICODE))
			return true;
		return false;
	}
}
