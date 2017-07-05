package ecd.perf.utilities.model.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.opencsv.CSVReader;

public class CSVDataSet extends AbstractModelElement{

	private static final long serialVersionUID = 1L;
	
	private final ArrayList<String> fields = new ArrayList<String>();

	public CSVDataSet(String testname, String filepath, String fileEncoding, 
			String delimiter, boolean quoteData, boolean recycle,
			boolean stopThread, String shareMode) {
		super(testname);
		this.put("filename", filepath);
		this.put("fileEncoding", fileEncoding);
		//this.put("variableNames", variableNames);
		this.put("delimiter", delimiter);
		this.put("quoteData", quoteData);
		this.put("recycle", recycle);
		this.put("stopThread", stopThread);
		this.put("shareMode", shareMode);
	}
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> properties = super.getProperties();
		this.put("variableNames", getDataFieldsWithDelima());
		return properties;
	}
	
	private String getDataFieldsWithDelima(){
		StringBuilder sbld = new StringBuilder(100);
		File csvFile = new File((String) this.get("filename"));
		if(csvFile.exists()) {
			try {
				CSVReader csvReader = new CSVReader(new FileReader(csvFile));
				String[] headers = csvReader.readNext();
				fields.clear();
				for(String headerItem : headers){
					fields.add(headerItem);
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}

		} else {
			//use the fields parsed in the script. But the order of the fields might be incorrect
		}
		if(fields != null) {
			int count = 0;
			for(String fld : fields) {
				if(count++ > 0) {
					sbld.append(",");
				}
				sbld.append(fld);
			}
		}
		
		return sbld.toString();
	}
	
	public void addDataField(String fieldName){
		if(!fields.contains(fieldName))
			fields.add(fieldName);
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "CSVDataSet";
	}

	@Override
	public String getTestclass() {
		return "CSVDataSet";
	}

	@Override
	public String getUIClass() {
		return "TestBeanGUI";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
