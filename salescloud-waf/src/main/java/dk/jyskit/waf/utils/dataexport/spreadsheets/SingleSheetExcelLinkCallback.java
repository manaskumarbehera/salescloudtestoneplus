package dk.jyskit.waf.utils.dataexport.spreadsheets;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface SingleSheetExcelLinkCallback<T> extends Serializable {
	String getSheetName();
	String getFileName();
	List<ExcelCol<T>> getCols();
	Collection<T> getRows();
}
