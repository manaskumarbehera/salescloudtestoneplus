package dk.jyskit.salescloud.application.services.importdata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.MicroMap;

import dk.jyskit.waf.utils.dataexport.spreadsheets.SingleSheetExcelLinkCallback;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.dataimport.DataImportHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractImportHandler implements DataImportHandler {
	
	protected List<Col> cols;

	public AbstractImportHandler() {
		cols = createCols();
	}

	protected abstract List<Col> createCols();

	@Override
	public void validateColumns(Set<String> columns) throws DataImportException {
		for(String colName : columns) {
			if (!StringUtils.isEmpty(colName)) {
				for(Col col : cols) {
					if (colName.equalsIgnoreCase(col.getKey())) {
						col.setFoundInHeader(true);
						break;
					}
				}
			}
		}
		
		String missing = null;
		for(Col col : cols) {
			if (!col.isFoundInHeader() && col.isMandatory()) {
				if (missing == null) {
					missing = "";
				} else {
					missing += ", ";
				}
				missing += "'" + col.getKey() + "'";
			}
		}
		if (missing != null) {
			Localizer localizer = Application.get().getResourceSettings().getLocalizer();
			String err = localizer.getString("import.err.missing_columns", null, Model.ofMap(new MicroMap("names", missing)), "");
			log.error("Missing column: " + missing);
			throw new DataImportException(err);
		}  	
	}

	@Override
	public void beforeStart(int pass) {
	}

	@Override
	public void afterFinish(int pass) {
//		try {
//			Map<String, Object> context = new HashMap<>();
//			NameAndEmail sender 	= new NameAndEmail(account.getEmailFrom());
//			NameAndEmail recipient 	= new NameAndEmail(ApplicationSession.get().getUser().getEmail());
//			sendEmailService.sendEmail("Import færdig", "Den import du satte igang i medlemskartoteket er nu færdig", context , sender , recipient);
//		} catch (Exception e) {
//			log.error("Import done, but there was an exception", e);
//			// Running an import during initialization will cause this to fail. It is safe to ignore this.
//		}
	}

	@Override
	public boolean getTwoPass() {
		return false;
	}

	@Override
	public int getSkipHeaderRows() {
		return 0;
	}

	@Override
	public SingleSheetExcelLinkCallback getCallback() {
		return null;
	}

	protected Object getValue(Object[] values, Map<String, Integer> columnToPositionMap, String colName) {
		Integer index = columnToPositionMap.get(colName.toLowerCase());
		if (index == null) {
			return null;
		}
		if (values[index] == null) {
			return null;
		}
		if (values[index] instanceof String) {
			return ((String) values[index]).trim();
		}
		return values[index];
	}

	protected boolean isEmptyRow(Object[] values) {
		for (Object value : values) {
			if (value != null) {
				if (value instanceof String) {
					String s = (String) value;
					if (!StringUtils.isEmpty(s)) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

} 