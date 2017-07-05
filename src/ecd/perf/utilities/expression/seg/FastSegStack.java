package ecd.perf.utilities.expression.seg;


/**
 * A stack to store segment when parsing the string
 * @author Ellick
 *
 */
public final class FastSegStack
{

	private Segment[] stack;
	private int pointer;

	public FastSegStack(int initialCapacity)
	{
		stack = new Segment[initialCapacity];
	}

	public Segment push(Segment value)
	{
		if (pointer + 1 >= stack.length) {
			resizeStack(stack.length * 2);
		}
		stack[pointer++] = value;
		return value;
	}

	public void popSilently()
	{
		stack[--pointer] = null;
	}

	public Segment pop()
	{
		final Segment result = stack[--pointer];
		stack[pointer] = null;
		return result;
	}

	public Segment peek()
	{
		return pointer == 0 ? null : stack[pointer - 1];
	}

	public int size()
	{
		return pointer;
	}

	public boolean hasStuff()
	{
		return pointer > 0;
	}

	public Segment get(int i)
	{
		return stack[i];
	}

	private void resizeStack(int newCapacity)
	{
		Segment[] newStack = new Segment[newCapacity];
		System.arraycopy(stack, 0, newStack, 0, Math.min(pointer, newCapacity));
		stack = newStack;
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer("["); //$NON-NLS-1$
		for (int i = 0; i < pointer; i++) {
			if (i > 0) {
				result.append(", "); //$NON-NLS-1$
			}
			result.append(stack[i]);
		}
		result.append(']');
		return result.toString();
	}
}