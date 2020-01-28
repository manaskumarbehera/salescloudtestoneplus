package dk.jyskit.salescloud.application.utils.dataimport;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface DataImportHandler extends Serializable {
	void validateColumns(Set<String> columns) throws DataImportException;

	void handleInputRow(int rowNo, Object[] values, Map<String, Integer> columnToPositionMap, int pass) throws DataImportException;

	void beforeStart(int pass);

	void afterFinish(int pass);

	boolean getTwoPass();

	int getSkipHeaderRows();

	SingleSheetExcelLinkCallback getCallback();
}
