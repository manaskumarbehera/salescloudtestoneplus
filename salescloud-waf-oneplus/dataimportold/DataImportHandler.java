package dk.jyskit.waf.utils.dataimport;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import dk.jyskit.waf.utils.dataexport.spreadsheets.ExcelLinkCallback;

public interface DataImportHandler extends Serializable {
	void validateColumns(Set<String> columns) throws DataImportException;

	void handleInputRow(int rowNo, String[] values, Map<String, Integer> columnToPositionMap, int pass) throws DataImportException;

	void beforeStart(int pass);

	void afterFinish(int pass);

	boolean getTwoPass();

	int getSkipHeaderRows();

	ExcelLinkCallback getCallback();
}
