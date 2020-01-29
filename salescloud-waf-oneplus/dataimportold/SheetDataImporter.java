package dk.jyskit.waf.utils.dataimport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import dk.jyskit.waf.utils.dataimport.excel.TabularExcelReader;

@Slf4j
public class SheetDataImporter {
	Map<String, Integer> columnToPositionMap;
	private TabularReader reader;
	private DataImportHandler handler;
	private String fileName;
	private int rowNo;
	private String sheetName;
	
	public SheetDataImporter(DataImportHandler handler, String fileName, String sheetName) {
		this.handler 	= handler;
		this.fileName 	= fileName;
		this.sheetName 	= sheetName;
		try {
			reader = new TabularExcelReader(fileName, sheetName);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public void getData() throws DataImportException {
		try {
			for (int pass = 0; pass < (handler.getTwoPass() ? 2 : 1); pass++) {
				rowNo = 0;
				
				reader = new TabularExcelReader(fileName, sheetName);
				
				parseHeaders(handler.getSkipHeaderRows());
				
				if (pass == 0) {
					handler.validateColumns(columnToPositionMap.keySet());
				}

				handler.beforeStart(pass);
				
				String[] values;
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
		columnToPositionMap = new HashMap<String, Integer>();

		for (int i = 0; i < skipHeaderRows; i++) {
			rowNo++;
			reader.readNext();
		}
		rowNo++;
		String[] headerline = reader.readNext();

		int col = 0;
		for (String header : headerline) {
			if (header.length() != 0) { // ignoring nameless columns
				columnToPositionMap.put(header.toLowerCase(), col);
			}
			col++;
		}
	}
}
