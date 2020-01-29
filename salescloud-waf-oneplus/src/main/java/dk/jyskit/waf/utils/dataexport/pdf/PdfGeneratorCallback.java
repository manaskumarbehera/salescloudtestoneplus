package dk.jyskit.waf.utils.dataexport.pdf;

import java.io.OutputStream;
import java.io.Serializable;

public interface PdfGeneratorCallback extends Serializable {
	public void generateOutputStream(OutputStream outputStream);
	public String getFilename();
}
