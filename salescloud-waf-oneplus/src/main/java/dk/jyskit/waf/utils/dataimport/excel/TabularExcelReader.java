package dk.jyskit.waf.utils.dataimport.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import dk.jyskit.waf.utils.dataimport.TabularReader;

public class TabularExcelReader extends TabularReader {
	Iterator<Row> rowIterator;
	int columnCount;

	public TabularExcelReader(String filename) throws IOException, InvalidFormatException {
		this(filename, null);
	}

	public TabularExcelReader(String filename, String sheetName) throws IOException, InvalidFormatException {
		InputStream inp = new FileInputStream(filename);

		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet;
		if (sheetName == null) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}

		rowIterator = sheet.rowIterator();

		columnCount = -1; // #cells not yet determined
	}

	public TabularExcelReader(File file) throws IOException, InvalidFormatException {
		this(file, null);
	}

	public TabularExcelReader(File file, String sheetName) throws IOException, InvalidFormatException {
		InputStream inp = new FileInputStream(file);

		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet;
		if (sheetName == null) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}

		rowIterator = sheet.rowIterator();

		columnCount = -1; // #cells not yet determined
	}

	@Override
	public Object[] readNext() throws IOException {
		if (!rowIterator.hasNext()) {
			return null;
		}
		
		Row row = rowIterator.next();
		increaseRowNumber();
		
		int columnsInRow = row.getLastCellNum();
		
		if (columnCount < columnsInRow) {
			columnCount = columnsInRow;
		}

		ArrayList<Object> values = new ArrayList<Object>();

		for (int cn = 0; cn < columnsInRow; cn++) {
			Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			Object value = null;

			if (cell == null) {
				value = "";
			} else {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					value = cell.getRichStringCellValue().getString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						value = cell.getDateCellValue().toString();
					} else {
						double numericCellValue = cell.getNumericCellValue();
						if (numericCellValue == Math.floor(numericCellValue) && !Double.isInfinite(numericCellValue)) {
							value = Integer.valueOf((int) numericCellValue);
						} else {
							value = new Double(numericCellValue);
						}
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = Boolean.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
//					value = cell.getCellFormula().toString();
					switch (cell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_NUMERIC:
						double numericCellValue = cell.getNumericCellValue();
						if (numericCellValue == Math.floor(numericCellValue) && !Double.isInfinite(numericCellValue)) {
							value = Integer.valueOf((int) numericCellValue);
						} else {
							value = new Double(numericCellValue);
						}
						break;
					case Cell.CELL_TYPE_STRING:
						value = cell.getRichStringCellValue().getString();
						break;
					}
					break;
				default:
					value = null;
				}
			}

			values.add(value);
		}

		// Return array if not all empty values
		for (Object value : values) {
			if (value instanceof String) {
				if (!StringUtils.isEmpty((String) value)) {
					return values.toArray(new Object[0]);
				}
			}
		}
		return null;
	}
} 