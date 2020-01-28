package dk.jyskit.waf.utils.dataimport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dk.jyskit.waf.utils.dataimport.csv.TabularCSVReader;
import dk.jyskit.waf.utils.dataimport.excel.TabularExcelReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataImporter {
	
//	Map<Integer, String> positionToColumnMap;
	Map<String, Integer> columnToPositionMap;
	private TabularReader reader;
	private DataImportHandler handler;
	private String localFileName;
	private String origFileName;
	private int rowNo;
	
	public DataImporter(DataImportHandler handler, String localFileName, String origFileName) {
		this.handler = handler;
		this.localFileName = localFileName;
		this.origFileName = origFileName;
		readFile(localFileName, origFileName);
	}

	private void readFile(String currentFileName, String origFileName) {
		try {
			if (origFileName.endsWith(".xlsx") || origFileName.endsWith(".xls")) {
				reader = new TabularExcelReader(currentFileName);
			} else {
				reader = new TabularCSVReader(currentFileName, "UTF-8", ';');
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public void getData() throws DataImportException {
		try {
			for (int pass = 0; pass < (handler.getTwoPass() ? 2 : 1); pass++) {
				rowNo = 0;
				
				readFile(localFileName, origFileName);
				
				parseHeaders(handler.getSkipHeaderRows());
				
				if (pass == 0) {
					handler.validateColumns(columnToPositionMap.keySet());
				}

				handler.beforeStart(pass);
				
				Object[] values;
				while ((values = reader.readNext()) != null) {
					rowNo++;
					handler.handleInputRow(rowNo, values, columnToPositionMap, pass);
				}
				
				handler.afterFinish(pass);
			}
		} catch (Exception e) {
			if (e instanceof DataImportException) {
				throw (DataImportException) e;
			} else {
				log.error("", e);
			}
		}
	}

	void parseHeaders(int skipHeaderRows) throws IOException, DataImportException {
//		positionToColumnMap = new HashMap<Integer, String>();
		columnToPositionMap = new HashMap<String, Integer>();

		for (int i = 0; i < skipHeaderRows; i++) {
			rowNo++;
			reader.readNext();
		}
		rowNo++;
		Object[] headerline = reader.readNext();

		int col = 0;
		for (Object header : headerline) {
			if (header instanceof String) {
				String s = (String) header;
				if (s.length() != 0) { // ignoring nameless columns
//				if (positionToColumnMap.containsValue(header)) {
//					log.warn("Duplicate column: " + header);
//				}
//				positionToColumnMap.put(col, s.toLowerCase());
					columnToPositionMap.put(s.toLowerCase(), col);
				}
			}
			col++;
		}
	}
}
