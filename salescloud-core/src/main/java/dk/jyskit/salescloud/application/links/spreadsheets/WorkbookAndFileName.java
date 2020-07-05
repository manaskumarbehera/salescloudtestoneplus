package dk.jyskit.salescloud.application.links.spreadsheets;

import com.google.inject.Provider;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface WorkbookAndFileName extends Serializable {
	Provider<Workbook> getWorkbook();
	IModel<String> getFileName();
}
