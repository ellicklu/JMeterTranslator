package ecd.perf.utilities.expression.lr;

import java.util.HashMap;
import java.util.Map;

import ecd.perf.utilities.expression.seg.Segment;
import ecd.perf.utilities.model.jmeter.CSVDataSet;

/**
 * This helper class is supposed to be called by playback api for the purpose of performance issue.
 * The max map size is 1000, if the map's keyset exceed this number, then the cached parsed segment must be cleared to avoid memory leak.
 * @author ellick
 */
public class LRSegmentParserHelper {
   //bug 11689483 Change consurent map for locked on atomic operations hash map
   //concurrent hash map causes NPE when a lot of VUSErs run a lot of iterations
	private static final int MAX_MAP_SIZE = 1000;//Max map size to avoid memory leak.
	private final Map<String, Segment> m_parsedSegmentMap = new HashMap<String, Segment>();
	private final static  LRSegmentParserHelper g_instance = new LRSegmentParserHelper();;
	private final Object mapLock = new Object();

	private LRSegmentParserHelper()
	{
	}
	
	public static LRSegmentParserHelper getInstance()
	{		
		return g_instance;
	}
	
	public Segment parse(String input, HashMap<String, CSVDataSet> databanks) throws Exception
	{
		if(input == null)
			return null;
		synchronized(mapLock){
		  if(m_parsedSegmentMap.containsKey(input)) {
		     return m_parsedSegmentMap.get(input);
		  } else {
			  if(m_parsedSegmentMap.size() > MAX_MAP_SIZE)
				  clear();
		  }
		  Segment parsedSegment = LRSegmentParser.parse(input, databanks);			
		  m_parsedSegmentMap.put(input, parsedSegment);
			
		  return parsedSegment;
		}
	}
	
	private void clear()
	{
		m_parsedSegmentMap.clear();
	}
}
