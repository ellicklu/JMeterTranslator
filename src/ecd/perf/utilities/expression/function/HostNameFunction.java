/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression.function;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

/**
 * Gets the fully qualified domain name for local IP address
 * @since 2.1.0
 */
public class HostNameFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "hostname"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e) {
			return null;
		}
		
		return localhost.getCanonicalHostName().getBytes();
	}
	
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__P(hostname,www.dummy.org)}";
	}
}
