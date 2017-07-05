package ecd.perf.utilities.main.lr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LRParamUtil {
	private static class LRParam {
		private String Name = null;
		private String Delimiter = ",";
		private String ParamName = null;
		private String TableLocation = "Local";
		private String ColumnName = "Col 1";
		private String Table = null;
		private String GenerateNewVal = "Once";
		private String Type = "Table";
		private String value_for_each_vuser = "";
		private String OriginalValue = "";
		private String auto_allocate_block_size = "1";
		private String SelectNextRow = "Sequential";
		private String StartRow = "1";
		private String OutOfRangePolicy = "ContinueWithLast";
	}
	private static String name = "[parameter:";
	private static String delimiter = "Delimiter=\"";
	private static String paramName = "ParamName=\"";
	private static String tableLocation = "TableLocation=\"";
	private static String columnName = "ColumnName=\"";
	private static String table = "Table=\"";
	private static String generateNewVal = "GenerateNewVal=\"";
	private static String type = "Type=\"";
	private static String value_for_each_vuser = "value_for_each_vuser=\"";
	private static String originalValue = "OriginalValue=\"";
	private static String auto_allocate_block_size = "auto_allocate_block_size=\"";
	private static String selectNextRow = "SelectNextRow=\"";
	private static String startRow = "StartRow=\"";
	private static String outOfRangePolicy = "OutOfRangePolicy=\"";

	public static HashMap<String,String> parsePredefinedVariables(File configFile) {
		List<LRParam> paramDefs = readParamConfigs(configFile);
		File folder = configFile.getParentFile();
		return readDataFiles(paramDefs, folder);
	}
	
	private static List<LRParam> readParamConfigs(File configFile) {
		List<LRParam> paramDefs = new ArrayList<LRParam>();
		BufferedReader bufR = null;
		try {
			bufR = new BufferedReader(new FileReader(configFile));
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR: failed to read config file - " + configFile.getAbsolutePath());
			return paramDefs;
		}
		String line = null;
		LRParam tmpParam = null;
		try {
			while((line = bufR.readLine()) != null) {
				if(line.startsWith(name)){
					if(tmpParam != null) {
						paramDefs.add(tmpParam);
						tmpParam = null;
					}
					tmpParam = new LRParam();
					tmpParam.Name = line.substring(name.length(), line.length() - 1);
					
				} else if (line.startsWith(delimiter)) {
					tmpParam.Delimiter = line.substring(delimiter.length(), line.length() - 1);
				} else if (line.startsWith(paramName)) {
					tmpParam.ParamName = line.substring(paramName.length(), line.length() - 1);
				} else if (line.startsWith(tableLocation)) {
					tmpParam.TableLocation = line.substring(tableLocation.length(), line.length() - 1);
				} else if (line.startsWith(columnName)) {
					tmpParam.ColumnName = line.substring(columnName.length(), line.length() - 1);
				} else if (line.startsWith(table)) {
					tmpParam.Table = line.substring(table.length(), line.length() - 1);
				} else if (line.startsWith(generateNewVal)) {
					tmpParam.GenerateNewVal = line.substring(generateNewVal.length(), line.length() - 1);
				} else if (line.startsWith(type)) {
					tmpParam.Type = line.substring(type.length(), line.length() - 1);
				} else if (line.startsWith(value_for_each_vuser)) {
					tmpParam.value_for_each_vuser = line.substring(value_for_each_vuser.length(), line.length() - 1);
				} else if (line.startsWith(originalValue)) {
					tmpParam.OriginalValue = line.substring(originalValue.length(), line.length() - 1);
				} else if (line.startsWith(auto_allocate_block_size)) {
					tmpParam.auto_allocate_block_size = line.substring(auto_allocate_block_size.length(), line.length() - 1);
				} else if (line.startsWith(selectNextRow)) {
					tmpParam.SelectNextRow = line.substring(selectNextRow.length(), line.length() - 1);
				} else if (line.startsWith(startRow)) {
					tmpParam.StartRow = line.substring(startRow.length(), line.length() - 1);
				} else if (line.startsWith(outOfRangePolicy)) {
					tmpParam.OutOfRangePolicy = line.substring(outOfRangePolicy.length(), line.length() - 1);
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR: failed to read config file - " + configFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			try {
				bufR.close();
			} catch (IOException e) {
			}
		}
		
		return paramDefs;
	}
	
	private static HashMap<String,String> readDataFiles(List<LRParam> paramDefs, File folder) {
		HashMap<String, String> vars = new HashMap<String, String>();
		for(LRParam pdef: paramDefs) {
			if("Local".equalsIgnoreCase(pdef.TableLocation)) {
				File tableFile = new File(folder.getAbsolutePath() + File.separator + pdef.Table);
				if(tableFile.exists() && pdef.ParamName != null && pdef.Name != null) {
					BufferedReader bufRd = null;
					try {
						bufRd = new BufferedReader(new FileReader(tableFile));
						String value = readParam(pdef, bufRd);
						if(value == null) {
							System.out.println("WARN: failed to read param value:" + pdef.Name);
						} else {
							vars.put(pdef.Name, value);
						}
					} catch (FileNotFoundException e1) {
						System.out.println("WARN: failed to read param value:" + pdef.Name);
					} finally {
						if(bufRd != null)
							try {
								bufRd.close();
							} catch (IOException e) {
							}
					}
				}
			}
		}
		return vars;
	}
	
	private static String readParam(LRParam pdef, BufferedReader bufRd) {
		String tableHd = null;
		try {
			tableHd = bufRd.readLine();
		} catch (IOException e) {
		}
		if(tableHd != null) {
			String[] heads = tableHd.split(pdef.Delimiter);
			int indexOfCol = -1;
			if(heads != null) {
				for(int i = 0; i < heads.length; i++) {
					if(pdef.ParamName.equalsIgnoreCase(heads[i])){
						indexOfCol = i;
						break;
					}
				}
				
			}
			if(indexOfCol != -1) {
				String firstRow = null;
				try {
					firstRow = bufRd.readLine();
				} catch (IOException e) {
				}
				if(firstRow != null) {
					String[] vals = firstRow.split(pdef.Delimiter);
					if(vals.length > indexOfCol) {
						//BINGLE, get the value
						return vals[indexOfCol];
					}
				}
			}
		}
		return null;
	}
}
