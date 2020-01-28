package dk.jyskit.waf.utils.dataimport.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

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

	@Override
	public String[] readNext() throws IOException {
		if (!rowIterator.hasNext()) {
			return null;
		}
		
		Row row = rowIterator.next();
		increaseRowNumber();
		
		if (columnCount == -1) {
			columnCount = row.getLastCellNum();
		}

		ArrayList<String> values = new ArrayList<String>();

		for (int cn = 0; cn < columnCount; cn++) {
			Cell cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
			String value = null;

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
							value = Integer.toString((int) numericCellValue);
						} else {
							value = Double.toString(numericCellValue);
						}
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					value = Boolean.toString(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					value = cell.getCellFormula().toString();
					break;
				default:
					value = null;
				}
			}

			values.add(value);
		}

		return values.toArray(new String[0]);
	}
}
