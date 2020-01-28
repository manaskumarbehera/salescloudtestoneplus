package dk.jyskit.salescloud.application.links.spreadsheets;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

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
	
	public void addSheet(String name) {
	    sheet = workbook.createSheet(name);
	    rowNo = -1;
	}
	
	public void resetRowNo() {
	    rowNo = -1;
	}
	
	public Cell addValue(Object value) {
	    Cell cell = row.createCell(colNo++);
	    if (value instanceof Double) {
	    	cell.setCellValue((Double) value);
	    } else if (value instanceof Float) {
	    	cell.setCellValue(Double.valueOf((Float) value));
	    } else if (value instanceof Long) {
	    	cell.setCellValue(Double.valueOf((Long) value));
	    } else if (value instanceof Integer) {
	    	cell.setCellValue(Double.valueOf((Integer) value));
	    } else {
	    	cell.setCellValue(workbook.getCreationHelper().createRichTextString((String) value));
	    }
	    return cell;
	}
	
	public Cell addDouble(double d) {
	    Cell cell = row.createCell(colNo++);
	    cell.setCellValue(d);
	    return cell;
	}
	
	public Cell addDouble(int d) {
	    Cell cell = row.createCell(colNo++);
	    cell.setCellValue(d);
	    return cell;
	}

	public void addCellStyle(Cell cell, IndexedColors color, Integer decimals) {
	    HSSFCellStyle cs = workbook.createCellStyle();
	    if (color != null) {
	    	cs.setFillForegroundColor(color.getIndex());
	    	cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    }
	    if (decimals != null) {
	    	cs.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
	    }
	    cell.setCellStyle(cs);
	}
	
	public Cell addCellColor(Cell cell, IndexedColors color) {
	    HSSFCellStyle cs = workbook.createCellStyle();
	    cs.setFillForegroundColor(color.getIndex());
	    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    cell.setCellStyle(cs);
	    return cell;
	}
	

	public Cell addValueAndColor(Object value, IndexedColors color) {
	    Cell cell = row.createCell(colNo);
	    if (value instanceof Double) {
	    	cell.setCellValue((Double) value);
	    } else {
	    	cell.setCellValue(workbook.getCreationHelper().createRichTextString((String) value));
	    }

	    HSSFCellStyle cs = workbook.createCellStyle();
	    cs.setFillForegroundColor(color.getIndex());
	    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    cell.setCellStyle(cs);
	    
	    colNo++;
	    return cell;
	}

	public void addColoredValue(Object value, IndexedColors color) {
	    Cell cell = row.createCell(colNo);
	    if (value instanceof Double) {
	    	cell.setCellValue((Double) value);
	    } else {
	    	cell.setCellValue(workbook.getCreationHelper().createRichTextString((String) value));
	    }

	    HSSFCellStyle cs = workbook.createCellStyle();
	    
	    Font font = workbook.createFont();
        font.setColor(color.getIndex());
        cs.setFont(font);
        cell.setCellStyle(cs);
	    
	    colNo++;
	}

	public void incRow() {
		incRow(1);
	}
	
	public void incRow(int rows) {
		colNo = 0;
		rowNo += rows;
		createRow();
	}

	public void createRow() {
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
