package ecd.perf.utilities.xmlStream.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Exception that occurs when converting between object and xml document
 * 
 * @author EllickLu
 * 
 */
public class ConversionException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ConversionException(String msg, Exception cause)
	{
		super(msg);
		stuff = new HashMap<String, String>();
		if (msg != null)
			add("message", msg); //$NON-NLS-1$
		if (cause != null) {
			add("cause-exception", cause.getClass().getName()); //$NON-NLS-1$
			add("cause-message", cause.getMessage()); //$NON-NLS-1$
			this.cause = cause;
		}
	}

	public ConversionException(String msg)
	{
		super(msg);
		stuff = new HashMap<String, String>();
	}

	public ConversionException(Exception cause)
	{
		this(cause.getMessage(), cause);
	}

	public String get(String errorKey)
	{
		return (String) stuff.get(errorKey);
	}

	public void add(String name, String information)
	{
		stuff.put(name, information);
	}

	public Iterator keys()
	{
		return stuff.keySet().iterator();
	}

	public String getMessage()
	{
		StringBuffer result = new StringBuffer();
		if (super.getMessage() != null)
			result.append(super.getMessage());
		result.append("\n---- Debugging information ----"); //$NON-NLS-1$
		String v;
		for (Iterator iterator = keys(); iterator.hasNext(); result.append(": " + v + " ")) { //$NON-NLS-1$ //$NON-NLS-2$
			String k = (String) iterator.next();
			v = get(k);
			result.append("\n" + k); //$NON-NLS-1$
			int padding = 20 - k.length();
			for (int i = 0; i < padding; i++)
				result.append(' ');

		}

		result.append("\n-------------------------------"); //$NON-NLS-1$
		return result.toString();
	}

	public Throwable getCause()
	{
		return cause;
	}

	public String getShortMessage()
	{
		return super.getMessage();
	}

	private Map<String, String> stuff;

	protected Exception cause;
}