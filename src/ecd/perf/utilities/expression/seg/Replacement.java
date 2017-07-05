package ecd.perf.utilities.expression.seg;

import ecd.perf.utilities.expression.ats.SegmentParser;

/**
 * Represents a replacement to be made to a String of characters.
 * When added to a Set, no two Replacement objects may overlap.
 * For example, two replacements cannot replace the same
 * (or part of the same) value.
 */
public class Replacement implements Comparable<Replacement>
{
	public final int m_startOffset;
	public final int m_endOffset;
	public final Segment m_replacement;

	public Replacement(int startOffset, int endOffset, Segment replacement)
	{
		m_startOffset = startOffset;
		m_endOffset = endOffset;
		m_replacement = replacement;
	}
	
	public Replacement(int startOffset, int endOffset, String replace)
	{
		m_startOffset = startOffset;
		m_endOffset = endOffset;
		Segment replacement = null;
		try {
			replacement = SegmentParser.parse(replace, null);
		} catch (Exception e) {
			replacement = new LiteralSegment("err_literal_replacement");
		}
		m_replacement = replacement;
	}
	
	public int getStartOffset() {
		return m_startOffset;
	}
	
	public int getEndOffset() {
		return m_endOffset;
	}
	
	public Segment getReplacement() {
		return m_replacement;
	}
	

	@Override
	/**
	 * 
	 */
	public int compareTo(Replacement that) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    
	    if ( this == that ) return EQUAL;

	    if ( this.getEndOffset() <= that.getStartOffset() )
	    		return BEFORE;
	    
	    if ( this.getStartOffset() >= that.getEndOffset() )
	    		return AFTER;
	    
	    return EQUAL;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( !(o instanceof Replacement) ) return false;
		
		Replacement that = (Replacement)o;
		return this.compareTo(that) == 0;
	}
}