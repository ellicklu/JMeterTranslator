package ecd.perf.utilities.expression.seg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.ats.SegmentParserOptions;

public class SegmentList extends Segment
{
	private List<Segment> m_sublist;

	public SegmentList()
	{
		m_sublist = new ArrayList<Segment>();
	}

	public SegmentList(List<Segment> list)
	{
		m_sublist = list;
	}

	@Override
	public void addSubSegment(Segment sub)
	{
		m_sublist.add(sub);
	}
	
	public void replaceSubSegment(Segment origSegment, Segment newSegment)
	{
		int pos = m_sublist.indexOf(origSegment);
		if (pos == -1)
			return;
		
		m_sublist.set(pos, newSegment);
	}

	@Override
	public byte[] evaluateBytes(Variables variables, SegmentParserOptions options)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (Segment seg : m_sublist) {
			try {
				bos.write(seg.evaluateBytes(variables, options));
			}
			catch (IOException e) {
				// will never happen
			}
		}
		return bos.toByteArray();
	}

	@Override
	public String evaluateString(Variables variables, SegmentParserOptions options)
	{
		StringBuffer value = new StringBuffer();
		for (Segment seg : m_sublist) {
			value.append(seg.evaluateString(variables, options));
		}
		return value.toString();
	}

	@Override
	public String getRecordedValue()
	{
		StringBuffer value = null;
		
		for (Segment seg : m_sublist) {
			String subValue = seg.getRecordedValue();
			if (subValue == null)
				continue;
			
			if (value == null)
				value = new StringBuffer();
			
			value.append(subValue);
		}
		
		return value != null ? value.toString() : null;
	}

	@Override
	public String getDisplayValue()
	{
		StringBuffer value = new StringBuffer();
		
		for (Segment seg : m_sublist) 
			value.append(seg.getDisplayValue());
		
		return value.toString();
	}
	
	@Override
	public String getCurrentValue()
	{
		StringBuffer value = new StringBuffer();
		
		for (Segment seg : m_sublist) 
			value.append(seg.getCurrentValue());
		
		return value.toString();
	}

	public int size()
	{
		return m_sublist.size();
	}
	
	public List<Segment> getSegments()
	{
		return m_sublist;
	}

	public Segment get(int index)
	{
		return m_sublist.get(index);
	}

	@Override
	public String translate(Map<String,String> encodeOptions) {
		StringBuffer value = new StringBuffer();
		for (Segment seg : m_sublist) {
			value.append(seg.translate(encodeOptions));
		}
		return value.toString();
	}
}
