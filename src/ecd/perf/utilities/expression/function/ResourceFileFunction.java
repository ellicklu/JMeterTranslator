package ecd.perf.utilities.expression.function;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.CustomFunction;
import ecd.perf.utilities.expression.TransformsUtil;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.file.ResourceFile;
import ecd.perf.utilities.expression.seg.Variables;
import ecd.perf.utilities.main.ASTDisplayer;


public class ResourceFileFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "resourceFile"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters,
			SegmentParserOptions options) {
		boolean shouldTransform = getTransformParam(parameters);
		if (shouldTransform) {
			String charset = getCharsetParam(parameters);
			String transformedContent = computeString(variables, parameters, options);
			byte[] returnBytes;
			try {
				returnBytes = transformedContent != null ? (charset != null ? transformedContent.getBytes(charset) : transformedContent.getBytes()) : new byte[0];
				return returnBytes;
			} catch (UnsupportedEncodingException e) {
				return transformedContent.getBytes();
			}
		} else {
			ResourceFile rf = getResourceFile(parameters);
			return rf != null ? rf.getData() : new byte[0];
		}
	}
	
	private ResourceFile getResourceFile(List<String> parameters)
	{
		if (isValidParameters(parameters)) {
			String resourceFileName = parameters.get(0);
//			String scriptPath = ASTDisplayer.getInstance().getScriptPath();
//			File scriptFile = new File(scriptPath);
//			if(scriptFile.isFile()) {
//				String scriptFolder = scriptFile.getParent();
//				ResourceFile rf = new ResourceFile(resourceFileName, new File(scriptFolder+File.separator+"resources" + File.separator + resourceFileName));
//				return rf;
//			}
		}
		return null;
	}
	
	private boolean isValidParameters(List<String> parameters)
	{
		if (parameters == null || parameters.size() == 0 || parameters.get(0) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean getTransformParam(List<String> parameters)
	{
		String transformParam = parameters.size() >= 3 ? parameters.get(2) : null;
		return transformParam != null ? Boolean.parseBoolean(transformParam.trim()) : false;
	}
	
	private String getCharsetParam(List<String> parameters)
	{
		String charset = parameters.size() >= 2 ? parameters.get(1) : null;
		return charset != null ? charset.trim() : null;
	}
	
	@Override
	public String computeString(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		ResourceFile rf = getResourceFile(parameters);
		if (rf != null) {
			String charset = getCharsetParam(parameters);
			boolean shouldTransform = getTransformParam(parameters);
			String content = charset != null ? rf.getDataAsString(charset) : rf.getDataAsString();
			if (shouldTransform) {
				return TransformsUtil.transform(content, variables, options.getUseRecordedValue(), options.isUseDatabank(), options.getUseUrlEncode(), options.getCharset());
			} else {
				return content;
			}
		}
		return null;
	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		ResourceFile rf = getResourceFile(paraValues);
		if (rf != null) {
			String charset = getCharsetParam(paraValues);
			boolean shouldTransform = getTransformParam(paraValues);
			String content = charset != null ? rf.getDataAsString(charset) : rf.getDataAsString();
			if (shouldTransform) {
				return TransformsUtil.translate(content, encodeOptions, null);
			} else {
				return content;
			}
		}
		return null;
	}
}
