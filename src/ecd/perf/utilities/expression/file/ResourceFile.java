package ecd.perf.utilities.expression.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a miscellaneous file in a ScriptPackage
 */
public final class ResourceFile
{
	private String m_fileName; //relative path to file in scriptFolder/resources
	//Changed implementation of internal Resource File API since 12.1
	//private byte[] m_data;
	private File m_resource;

	public ResourceFile(String fileName, File cachedResource)
	{
		m_fileName = fileName;
		m_resource = cachedResource;
	}

	public byte[] getData()
	{
		InputStream is = getDataAsStream();
		try {
			byte[] data = FileUtility.readBytesFromStream(is);
			return data;
		} 
		catch (IOException e) {
			return null;
		}
		finally{
			if(is != null){
				try {
					is.close();
				} 
				catch (IOException ignore) {}			
			}
		}
	}

	public String getFileName()
	{
		return m_fileName;
	}
	
	public InputStream getDataAsStream()
	{
		InputStream is;
		try {
			is = new FileInputStream(m_resource);
			return is;
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public String getDataAsString()
	{
		return getDataAsString(System.getProperty("file.encoding"));//$NON-NLS-1$
	}
	
	public String getDataAsString(String encoding)
	{
		try {
			String res = FileUtility.streamToString(getDataAsStream(), encoding);
			return res;
			//no need to close stream as FileUtility method does it for us
		} 
		catch (IOException e) {
			return null;
		}
	}
}