package dk.jyskit.waf.utils.dataexport.spreadsheets;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@Slf4j
public abstract class ExcelSpreadsheet<T> implements Serializable {
	private HSSFWorkbook workbook;
    private short rowNo;
    private short colNo;
	private Sheet sheet;
	private Row row;
	
	public ExcelSpreadsheet(String name) {
		workbook = new HSSFWorkbook();
	    sheet = workbook.createSheet(name);
	}
	
	/* High level methods */

	public void addHeaderRow(IndexedColors color, List<ExcelCol<T>> cols) {
	    for (ExcelCol<T> col : cols) {
	    	addTextAndColor(col.getHeader(), true, col.getCellColor());
		}
	    incRow();
	}
	
	private void addValueRow(IndexedColors color, T rec, List<ExcelCol<T>> cols) {
	    for (ExcelCol<T> col : cols) {
    		try {
			    addTextAndColor(col.getValue(rec), false, col.getCellColor());
			} catch (Exception e) {
				log.warn("Problem with record: " + rec, e);
			}
		}
	    incRow();
	}
	
	/* Lower level methods */
	
	public void addTextAndColor(String text, boolean bold, IndexedColors cellColor) {
	    row.createCell(colNo).setCellValue(workbook.getCreationHelper().createRichTextString(text));

	    HSSFCellStyle cs = workbook.createCellStyle();
	    
	    if (cellColor != null) {
		    cs.setFillForegroundColor(cellColor.getIndex());
		    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    }
        if (bold) {
    	    Font font = workbook.createFont();
            font.setBold(true);
            cs.setFont(font);
        }
	    row.getCell(colNo).setCellStyle(cs);
	    
	    colNo++;
	}

	public void incRow() {
		incRow(1);
	}
	
	public void incRow(int rows) {
		colNo = 0;
		rowNo += rows;
		row = sheet.createRow(rowNo);
	}

	public void incCol() {
		incCol(1);
	}
	
	public void incCol(int cols) {
		colNo += cols;
	}
	
	public Workbook getWorkbook() {
		incRow(0);
		
		List<ExcelCol<T>> cols = getCols();
		
		for (T rec : getRows()) {
	    	for (ExcelCol col : cols) {
				col.init(rec);
			}
		}
	    
	    addHeaderRow(IndexedColors.DARK_BLUE, cols);
	    
		for (T rec : getRows()) {
		    addValueRow(null, rec, cols);
		}
	    
		return workbook;
	}

	public abstract List<ExcelCol<T>> getCols();

	public abstract Collection<T> getRows();
}
