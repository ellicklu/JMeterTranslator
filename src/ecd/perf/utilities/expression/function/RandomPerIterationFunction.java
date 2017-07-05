package ecd.perf.utilities.expression.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

/**
 * Get the random value based on the three parameters(min,max,index) and iteration. For the same parameter values, Only update this random value once in
 * one iteration. For the different value of index parameter, generate different random value in one Iteration. For the same value of index parameter and
 * different values of min and max parameter, generate different random value in one Iteration. For different iteration, generate different random value.
 * @since 2.1.0
 */
public class RandomPerIterationFunction extends CustomFunction
{
	private final Random m_random = new Random();
	
	private ThreadLocal<Map<String, Integer>> dataLocal = new ThreadLocal<Map<String, Integer>>();
	
	private Map<String, Integer> getStoredData() {
		Map<String, Integer> m_data = dataLocal.get();
		if (m_data == null) {
			m_data = new HashMap<String, Integer>();
			dataLocal.set(m_data);
		}
		return m_data;
	}
	 
	@Override
	public String getId()
	{
		return "randomPerIteration"; //$NON-NLS-1$
	}

	private static final String KEY_ITERATION = "ITERATION_ID"; //$NON-NLS-1$

	@Override
	public byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{ 
		// check parameters
		if (customArguments == null || customArguments.size() == 0)
			return "1".getBytes();

		String bottom_str = null;
		String ceil_str = null;
		String index_str = null;

		if (customArguments.size() == 2) {
			bottom_str = "0";//$NON-NLS-1$
			ceil_str = customArguments.get(0);
			index_str = customArguments.get(1);
		}
		else {
			bottom_str = customArguments.get(0);
			ceil_str = customArguments.get(1);
			index_str = customArguments.get(2);
		}

		// parse and validate parameters
		int ceil = 0;
		int bottom = 0;
		try {
			ceil = Integer.parseInt(ceil_str);
		}
		catch (Exception e) {
			return "1".getBytes();
		}
		try {
			bottom = Integer.parseInt(bottom_str);
		}
		catch (Exception e) {
			return "1".getBytes();
		}
		if (ceil < 0 || bottom < 0 || ceil <= bottom) {
			return "1".getBytes();
		}

		String key = bottom_str  + '_' +  ceil_str + '_' +  index_str;
		int randomValue = -1;
		int latestIterationId = 1;
		int currentIterationId = 10;
		
		Map<String, Integer> m_data = getStoredData();
		
		if(m_data.containsKey(KEY_ITERATION)){
			latestIterationId = m_data.get(KEY_ITERATION);
		}else{
			latestIterationId = currentIterationId;
			m_data.put(KEY_ITERATION, latestIterationId);
		}
		
		if(latestIterationId != currentIterationId){
			dataLocal.remove();
			m_data = getStoredData();
			latestIterationId = currentIterationId;
			// Store latestIterationId
			m_data.put(KEY_ITERATION, latestIterationId);
		}
		
		if(m_data.containsKey(key)){
			randomValue = m_data.get(key);
		}else{
			randomValue = bottom + m_random.nextInt(ceil - bottom);
			//  Store all shared random data by bottom_ceil_index
			m_data.put(key, randomValue);
		}
		return String.valueOf(randomValue).getBytes();
	}
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		String bottom_str = null;
		String ceil_str = null;
		// check parameters
		if (paraValues == null || paraValues.size() == 0){
			bottom_str = "0";
			ceil_str = "9";
		} else if (paraValues.size() == 1) {
			bottom_str = "0";//$NON-NLS-1$
			ceil_str = paraValues.get(0);
		} else {
			bottom_str = paraValues.get(0);
			ceil_str = paraValues.get(1);
		}

		// parse and validate parameters
		int ceil = 0;
		int bottom = 0;
		try {
			ceil = Integer.parseInt(ceil_str);
		}
		catch (Exception e) {
			ceil = 9;
		}
		try {
			bottom = Integer.parseInt(bottom_str);
		}
		catch (Exception e) {
			bottom = 0;
		}
		if (ceil < 0 || bottom < 0 || ceil <= bottom) {
			ceil = bottom + 1;
		}

		return "${__Random("+bottom+","+ceil+")}";
	}
}
