package ecd.perf.utilities.expression.file;

import java.io.File;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AssetsXMLSaxHandler extends DefaultHandler{
	public static final String ASSET = "asset"; //$NON-NLS-1$
	public static final String TYPE = "type"; //$NON-NLS-1$
	public static final String DATABANK = "DATABANK"; //$NON-NLS-1$
	public static final String SCRIPT = "SCRIPT"; //$NON-NLS-1$
	public static final String LOCATION = "location"; //$NON-NLS-1$
	public static final String ALIAS = "alias"; //$NON-NLS-1$
	public static final String REPOSITORY = "repository";//compatible for old scripts before 9.01 //$NON-NLS-1$
	public static final String RELATIVE = "relative"; //$NON-NLS-1$
	public static final String ABSOLUTE = "absolute";//new since 9.10 //$NON-NLS-1$
	public static final String PATH = "path"; //$NON-NLS-1$
	public static final String CATEGORY = "category"; //$NON-NLS-1$
	public static final String FILE = "file"; //$NON-NLS-1$
	public static final String SOURCETYPE = "sourceType"; //$NON-NLS-1$
	public static final String DATABASE = "database"; //$NON-NLS-1$
	public static final String DRIVER="driver"; //$NON-NLS-1$
	public static final String URL = "url"; //$NON-NLS-1$
	public static final String USER = "user"; //$NON-NLS-1$
	public static final String PSWD = "pswd"; //$NON-NLS-1$
	public static final String SQL = "sql"; //$NON-NLS-1$
	public static final String ENCRYPTMETHOD="encryptMethod"; //$NON-NLS-1$
	public static final String CHARSET = "charset"; //$NON-NLS-1$
	
	private DataBankInfo currentDatabank = null;
	private final HashMap<String, DataBankInfo> databanks = new HashMap<String, DataBankInfo>();

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (localName.equalsIgnoreCase(ASSET)) {
			String type = attributes.getValue(TYPE);
			if (type.equalsIgnoreCase(DATABANK)) {
				currentDatabank = new DataBankInfo();
				String alias = XmlEncoder.xmlDecode(attributes.getValue(ALIAS));
				String charset = XmlEncoder.xmlDecode(attributes.getValue(CHARSET));
				currentDatabank.setCharset(charset); 
				currentDatabank.setAlias(alias);
			} else if (type.equalsIgnoreCase(SCRIPT)) {
				//ignore script
			}

		} else if (localName.equalsIgnoreCase(LOCATION)) {
			String type = attributes.getValue(TYPE);
			if (type.equalsIgnoreCase(REPOSITORY)) {
				handleAbsoluteRepository(attributes);
			} else if (type.equalsIgnoreCase(RELATIVE)) {
				handleRelativeRepository(attributes);
			} else if (type.equalsIgnoreCase(ABSOLUTE)) {
				handleAbsolutePath(attributes);
			}
		} else if (localName.equalsIgnoreCase(DATABASE)) {
			//ignore database
		}
	}

	private void handleAbsolutePath(Attributes attributes) {
	}

	private void handleAbsoluteRepository(Attributes attributes) {
		String rep = XmlEncoder.xmlDecode(attributes.getValue(REPOSITORY));
		String category = attributes.getValue(CATEGORY);
		String file = attributes.getValue(FILE);
		if (currentDatabank != null) {
			// the databank file name format is repoName/folders/dbFilename
			String databankFileLocation = ""; //$NON-NLS-1$
			if (category == null || category.length() == 0) {
				databankFileLocation = getRepositoryAbsolutePath(rep) + File.separator + file;
			} else {
				databankFileLocation =  getRepositoryAbsolutePath(rep) +  File.separator + category
						+ File.separator + file;
			}
			currentDatabank.setDatabankFileName(databankFileLocation);
		}
	}
	
	private String getRepositoryAbsolutePath(String repo) {
		return "C:\\OracleATS\\OFT";
	}

	private void handleRelativeRepository(Attributes attributes) {
		String relativePath = XmlEncoder.xmlDecode(attributes.getValue(PATH));
		if (currentDatabank != null) {
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase(LOCATION)) {
		}
		if (localName.equalsIgnoreCase(ASSET)) {
			databanks.put(currentDatabank.getAlias(), currentDatabank);
			currentDatabank = null;
		}
	}
	
	public  HashMap<String, DataBankInfo> getParsedDatabanks() {
		return this.databanks;
	}
}