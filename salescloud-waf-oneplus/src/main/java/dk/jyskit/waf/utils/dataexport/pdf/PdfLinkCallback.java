package dk.jyskit.waf.utils.dataexport.pdf;

import java.io.Serializable;

public interface PdfLinkCallback extends Serializable {
	String getHtml();
	String getTitleKey();
	String getFileName();
}
