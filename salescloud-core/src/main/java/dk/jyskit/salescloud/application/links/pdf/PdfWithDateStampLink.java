package dk.jyskit.salescloud.application.links.pdf;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class PdfWithDateStampLink extends ResourceLink<Void> {

	public PdfWithDateStampLink(String id, final String pathRelativeToClassPathRoot, String fileName) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try {
							InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathRelativeToClassPathRoot);
							
							PdfReader pdfReader = new PdfReader(inputStream);
							
							PdfStamper stamper = new PdfStamper(pdfReader, output);
							stamper.setRotateContents(false);

							Font font     = new Font(Font.HELVETICA, 8, Font.NORMAL); 
							Phrase footer = new Phrase(CoreSession.get().getContract().getDocumentFooterText(), font);
							
							for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
								PdfContentByte canvas = stamper.getUnderContent(i);
								ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, footer, 30, 10, 0f);
							}

							stamper.close();
							output.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public String getContentType() {
						return "application/pdf";
					}
				};
			}
		}.setFileName(fileName));
	}

}
