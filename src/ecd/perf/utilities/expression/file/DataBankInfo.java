package ecd.perf.utilities.expression.file;


import java.io.File;

public class DataBankInfo implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private String m_databankFileName;
	private boolean m_useAbsolutePath;
	private String m_alias;
	private String m_repositoryPath;
	//private String m_currentScriptPath;
	//private DataBankLocationInfo m_locationInfo;
	//should be deleted after switching to UsageMode
	private int m_fetchCount;
	private Exception m_exception;
	private boolean m_fromAsset = false;
	private String m_databankSettingsString = null;

	private int m_iReocrdCount ;
	private boolean m_bIterateIndependently = false;
	private String m_charset = null;
	
	private boolean m_bUseSeed = false;

	public String getDatabankFileName()
	{
		if (m_databankFileName == null) return null;
		//in 9.01 scenario. The file name is just  name of the file like fmstocks.csv or its absolute path c:\oracleATS\OFT\Databank\fmstocks.csv
		// since 9.10 . it includes the repository path of the file. //default/databank/fmstock.csv
		File file = new File(m_databankFileName);
		if(file.getParent() == null) {
//			String sRepositoryName = iRepository.getName();
//			String sRepositoryPath = iRepository.getConfig();
//			if(m_repositoryPath != null && sRepositoryPath.equalsIgnoreCase(m_repositoryPath)){
//				String databankDir = DatabankLookupper.getStorageManager().getDatabankLocation(sRepositoryName);
//				m_databankFileName = databankDir + File.separator + m_databankFileName;
//				break;
//			}
		}
		return m_databankFileName;

	}

	public void setDatabankFileName(String fileName)
	{
		m_databankFileName = fileName;
	}


	public boolean isUseAbsolutePath()
	{
		return m_useAbsolutePath;
	}

	public void setRepositoryPath(String repositoryPath)
	{
		m_repositoryPath = repositoryPath;
	}
	
	public void setCharset(String charset) {
		this.m_charset = charset;
	}
	
	public String getCharset() {
		return m_charset;
	}

	public String getAlias()
	{
		return m_alias;
	}

	public void setAlias(String alias)
	{
		m_alias = alias;
	}

}
