package dk.jyskit.waf.wicket.components.spreadsheets;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Spreadsheet {
	private HSSFWorkbook workbook;
    private short rowNo;
    private short colNo;
	private Sheet sheet;
	private Row row;
	
	public Spreadsheet(String name) {
		workbook = new HSSFWorkbook();
	    sheet = workbook.createSheet(name);
	}
	
	public void addText(String text) {
	    row.createCell(colNo++).setCellValue(workbook.getCreationHelper().createRichTextString(text));
	}
	
	public void addTextAndColor(String text, IndexedColors color) {
	    row.createCell(colNo).setCellValue(workbook.getCreationHelper().createRichTextString(text));

	    HSSFCellStyle cs = workbook.createCellStyle();
	    cs.setFillForegroundColor(color.getIndex());
	    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    row.getCell(colNo).setCellStyle(cs);
	    
	    colNo++;
	}

	public void addColoredText(String text, IndexedColors color) {
	    row.createCell(colNo).setCellValue(workbook.getCreationHelper().createRichTextString(text));

	    HSSFCellStyle cs = workbook.createCellStyle();
	    
	    Font font = workbook.createFont();
        font.setColor(color.getIndex());
        cs.setFont(font);
        
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
		return workbook;
	}
}
