package ecd.perf.utilities.xmlStream.core;

/**
 * Exception that occurs when accessing object methods or object fields
 * @author EllickLu
 *
 */
public class ObjectAccessException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ObjectAccessException(String message)
	{
		super(message);
	}

	public ObjectAccessException(String message, Throwable cause)
	{
		super(message, cause);
	}
}