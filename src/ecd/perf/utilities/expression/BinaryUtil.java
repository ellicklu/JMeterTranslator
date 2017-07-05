/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression;

/**
 * BinaryUtil is a utility class that provides functions of conversion between binary data and hexadecimal string.
 * For example,
 * The hexadecimal string of 'abc' is "\97\98\99".
 * @since 2.1.0
 */
public final class BinaryUtil
{
	public static final String BINARY_CHARSET = "OracleBinary"; //$NON-NLS-1$
	
	private static char[] ALLDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Convert binary content to hexadecimal string.
	 * @param content Binary data.
	 * @param printable If the returned hexadecimal string is printable.
	 * @return Returns null when input bytes are null.
	 * @throws Exception
	 */
	public static String binary2HexString(byte[] bytes, boolean printable)
	{
		//		 Convert \ to \\
		// Convert any non-printable chars to \##
		// Allowable printable chars:
		// 9   (tab)
		// 10  (line feed)
		// 13  (carriage return)
		// 32 through 126
		if (bytes == null)
			return null;
		StringBuffer buf = new StringBuffer(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == '\\') {
				buf.append("\\\\"); //$NON-NLS-1$
			}
			// always encode { and } characters so we don't potentially
			// break custom transform parsing
			else if (bytes[i] == '{' || bytes[i] == '}') {
				buf.append('\\');
				buf.append(ALLDIGITS[(bytes[i] >> 4) & 0x0F]);
				buf.append(ALLDIGITS[bytes[i] & 0x0F]);
			}
			else if (bytes[i] < 32 || bytes[i] >= 127) {
				if (bytes[i] != '\t' && bytes[i] != '\r' && bytes[i] != '\n') {
					buf.append('\\');
					buf.append(ALLDIGITS[(bytes[i] >> 4) & 0x0F]);
					buf.append(ALLDIGITS[bytes[i] & 0x0F]);
				}
				else {
					if (printable) {
						buf.append((char) bytes[i]);
					}
					else {
						buf.append('\\');
						buf.append(ALLDIGITS[(bytes[i] >> 4) & 0x0F]);
						buf.append(ALLDIGITS[bytes[i] & 0x0F]);
					}
				}
			}
			else {
				buf.append((char) bytes[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * Convert hexadecimal string to binary content.
	 * @param content Hexadecimal string to decode into a byte array.
	 * @return Decoded byte array for the given hexadecimal string. Return null when input bytes are null.
	 * @throws BinaryDecodingException if a character in the string cannot be decoded into a byte array.
	 */
	public static byte[] hexString2Binary(String encodedBytes)
	{
		if (encodedBytes == null)
			return null;
		int data = 0;
		int bufferlength = 128;
		byte[] datas = new byte[bufferlength];
		int length = 0;
		int state = 0;
		char curChar = 0;

		//state 0, normal
		//state 1, saw slash
		//state 2, saw slash and a letter
		for (int i = 0; i < encodedBytes.length(); i++) {
			curChar = encodedBytes.charAt(i);
			switch (state) {
				case 0://normal
					if (curChar == '\\') {
						state = 1;
						data = 0;
					}
					else {
						datas[length++] = (byte) curChar;
					}
					break;
				case 1://just saw slash
					if ('0' <= curChar && '9' >= curChar) {
						data = 16 * data + (curChar - '0');
						state = 2;
					}
					else if ('A' <= curChar && 'F' >= curChar) {
						data = 16 * data + (curChar - 'A') + 10;
						state = 2;
					}
					else if ('a' <= curChar && 'f' >= curChar) {
						data = 16 * data + (curChar - 'a') + 10;
						state = 2;
					}
					else if (curChar == '\\') {
						datas[length++] = (byte) '\\';
						state = 0;
					}
					else if (curChar == 't' || curChar == 'T') {
						datas[length++] = (byte) '\t';
						state = 0;
					}
					else if (curChar == 'r' || curChar == 'R') {
						datas[length++] = (byte) '\r';
						state = 0;
					}
					else if (curChar == 'n' || curChar == 'N') {
						datas[length++] = (byte) '\n';
						state = 0;
					}
					else {
						return null;
					}
					break;
				case 2: //saw slash and a letter
					if ('0' <= curChar && '9' >= curChar) {
						data = 16 * data + (curChar - '0');
						datas[length++] = (byte) data;
						state = 0;
					}
					else if ('A' <= curChar && 'F' >= curChar) {
						data = 16 * data + (curChar - 'A') + 10;
						datas[length++] = (byte) data;
						state = 0;
					}
					else if ('a' <= curChar && 'f' >= curChar) {
						data = 16 * data + (curChar - 'a') + 10;
						datas[length++] = (byte) data;
						state = 0;
					}
					else {
						return null;
					}
			}
			if (length == bufferlength) {
				bufferlength *= 2;
				byte[] temp = new byte[bufferlength];
				System.arraycopy(datas, 0, temp, 0, length);
				datas = temp;
			}
		}
		if (length == 0) {
			datas = new byte[0];
		}
		else if (length != bufferlength) {
			bufferlength = length;
			byte[] temp = new byte[bufferlength];
			System.arraycopy(datas, 0, temp, 0, length);
			datas = temp;
		}
		return datas;
	}
}
