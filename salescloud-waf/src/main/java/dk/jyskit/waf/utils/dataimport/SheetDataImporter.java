package dk.jyskit.waf.utils.dataimport;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import dk.jyskit.waf.utils.dataimport.excel.TabularExcelReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SheetDataImporter {
	Map<String, Integer> columnToPositionMap;
	private TabularReader reader;
	private DataImportHandler handler;
	private File file;
	private int rowNo;
	private String sheetName;
//	private String fileName;
	
//	public SheetDataImporter(DataImportHandler handler, String fileName, String sheetName) {
//		this.handler 	= handler;
//		this.fileName 	= fileName;
//		this.file 		= new File(fileName);
//		this.sheetName 	= sheetName;
//		try {
//			reader = handler.createReader(fileName, sheetName);
//		} catch (Exception e) {
//			log.error("", e);
//		}
//	}
	
	public SheetDataImporter(DataImportHandler handler, File file, String sheetName) {
		this.handler 	= handler;
		this.file		= file;
		this.sheetName 	= sheetName;
		try {
//			reader = new TabularExcelReader(file, sheetName);
			reader = handler.createReader(file, sheetName);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public void getData() throws DataImportException {
		try {
			for (int pass = 0; pass < (handler.getTwoPass() ? 2 : 1); pass++) {
				rowNo = 0;
				
				reader = handler.createReader(file, sheetName);
				
				parseHeaders(handler.getSkipHeaderRows());
				
				if (pass == 0) {
					handler.validateColumns(columnToPositionMap.keySet());
				}

				handler.beforeStart(pass);
				
				Object[] values;
				int emptyLinesInARow = 0;
				boolean done = false;
				while ((rowNo < getMaxRows()) && !done) {
					values = reader.readNext();
					rowNo++;
					if (values == null) {
						emptyLinesInARow++;
						if (emptyLinesInARow > 10) {
							done = true;
						}
					} else {
						emptyLinesInARow = 0;
						handler.handleInputRow(rowNo, values, columnToPositionMap, pass);
					}
				}
				
				handler.afterFinish(pass);
			}
		} catch (Exception e) {
			if (e instanceof DataImportException) {
				throw (DataImportException) e;
			} else {
				if (e instanceof PersistenceException) {
					PersistenceException persistenceException = (PersistenceException) e;
					ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceException.getCause();
					
					for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
						log.error(constraintViolation.getMessage());
						log.error("I'm guessing this is the problem: \n"
								+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
								+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
								+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
					}
				} else if (e instanceof ConstraintViolationException) {
					ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
					
					for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
						log.error(constraintViolation.getMessage());
						log.error("I'm guessing this is the problem: \n"
								+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
								+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
								+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
					}
				} else {
					log.error("A problem occured during import of spreadsheet", e);
				}

			}
		}
	}

	/**
	 * @return maximum number of rows 
	 */
	protected int getMaxRows() {
		return Integer.MAX_VALUE;
	}

	void parseHeaders(int skipHeaderRows) throws IOException, DataImportException {
		columnToPositionMap = new HashMap<String, Integer>();

		Object[] headerline;
		for (int i = 0; i < skipHeaderRows; i++) {
			rowNo++;
			headerline = reader.readNext();
			System.out.println(headerline);
		}
		rowNo++;
		headerline = reader.readNext();

		int col = 0;
		for (Object header : headerline) {
			if (header instanceof String) {
				String s = (String) header;
				if (s.length() != 0) { // ignoring nameless columns
					columnToPositionMap.put(s.toLowerCase(), col);
				}
			}
			col++;
		}
	}
}
