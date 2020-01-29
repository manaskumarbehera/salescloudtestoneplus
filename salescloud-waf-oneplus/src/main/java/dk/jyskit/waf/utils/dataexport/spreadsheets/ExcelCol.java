
package dk.jyskit.waf.utils.dataexport.spreadsheets;

import org.apache.poi.ss.usermodel.IndexedColors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ExcelCol<T> {
	public String header;
	
	public abstract String getValue(T obj);
	
	void init(T rowObject) {
	}
	
	public IndexedColors getCellColor() {
		return null;
	}
}
