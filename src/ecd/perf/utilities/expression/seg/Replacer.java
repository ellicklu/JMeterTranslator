package ecd.perf.utilities.expression.seg;

import java.util.SortedSet;

public interface Replacer
{
	/**
	 * For a given input string, specify what segments of the string should be replaced,
	 * and what to replace each segment with.
	 * @param input Input string. This string does NOT contain any escaped transform characters, i.e. you could potentially have abc{{def
	 * @return A SortedSet of replacements. See Replacement class.
	 */
	public SortedSet<Replacement> findReplacements(String input);
}
