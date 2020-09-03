package dk.jyskit.waf.utils.dataimport.csv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import au.com.bytecode.opencsv.CSVReader;
import dk.jyskit.waf.utils.dataimport.TabularReader;

public class TabularCSVReader extends TabularReader {
	
	CSVReader csvReader; 
	
	public TabularCSVReader(String filename, String charsetName, char divider) throws UnsupportedEncodingException, FileNotFoundException {
		Reader reader = new InputStreamReader(new FileInputStream(filename), charsetName);
		csvReader = new CSVReader(reader, divider);
	}
	
	@Override
	public String[] readNext() throws IOException {
		increaseRowNumber();
		return csvReader.readNext();
	}

}
