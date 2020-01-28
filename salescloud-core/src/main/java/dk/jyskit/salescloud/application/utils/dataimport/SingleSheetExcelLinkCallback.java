package dk.jyskit.salescloud.application.utils.dataimport;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface SingleSheetExcelLinkCallback<T> extends Serializable {
	String getSheetName();
	String getFileName();
	List<ExcelCol<T>> getCols();
	Collection<T> getRows();
}
