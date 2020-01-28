package dk.jyskit.waf.utils.dataexport.spreadsheets;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface ExcelLinkCallback<T> extends Serializable {
	String getTitleKey();
	String getFileName();
	List<ExcelCol<T>> getCols();
	Collection<T> getRows();
}
