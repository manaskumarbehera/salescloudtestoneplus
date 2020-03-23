package dk.jyskit.salescloud.application.links.spreadsheets;

import com.google.inject.Provider;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.model.IModel;

public interface WorkbookAndFileName {
	Provider<Workbook> getWorkbook();
	IModel<String> getFileName();
}
