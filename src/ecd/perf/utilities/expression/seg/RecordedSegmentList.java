package ecd.perf.utilities.expression.seg;

public class RecordedSegmentList extends SegmentList{
	@Override
	public String getRecordedValue()
	{
		StringBuffer value = null;
		
		for (Segment seg : getSegments()) {
			String subValue = seg.getRecordedValue();
			if (subValue == null)
				subValue = seg.getCurrentValue();
			
			if (value == null)
				value = new StringBuffer();
			
			value.append(subValue);
		}
		
		return value != null ? value.toString() : null;
	}
}
