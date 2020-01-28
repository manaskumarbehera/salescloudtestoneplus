package dk.jyskit.salescloud.application.services.importdata;

import java.io.File;

import dk.jyskit.waf.utils.dataimport.SheetDataImporter;

public class DataImporter extends SheetDataImporter {
	private int maxRows = Integer.MAX_VALUE;

	public DataImporter(ProductImportHandler importHandler, File file) {
		super(importHandler, file, null);  // null: Just pick the first sheet
	}

	@Override
	protected int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows; 
	}
}
