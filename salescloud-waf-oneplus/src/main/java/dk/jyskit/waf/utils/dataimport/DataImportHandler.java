package dk.jyskit.waf.utils.dataimport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import dk.jyskit.waf.utils.dataexport.spreadsheets.SingleSheetExcelLinkCallback;
import dk.jyskit.waf.utils.dataimport.excel.TabularExcelReader;

public interface DataImportHandler extends Serializable {
	void validateColumns(Set<String> columns) throws DataImportException;

	void handleInputRow(int rowNo, Object[] values, Map<String, Integer> columnToPositionMap, int pass) throws DataImportException;

	void beforeStart(int pass);

	void afterFinish(int pass);

	default TabularReader createReader(File file, String sheetName) throws InvalidFormatException, IOException {
		return new TabularExcelReader(file, sheetName);
	}

	boolean getTwoPass();
	
	int getSkipHeaderRows();

	SingleSheetExcelLinkCallback getCallback();
}
