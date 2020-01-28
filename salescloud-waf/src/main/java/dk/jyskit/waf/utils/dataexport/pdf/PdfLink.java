package dk.jyskit.waf.utils.dataexport.pdf;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class PdfLink extends ResourceLink<Void> {
	private static final long serialVersionUID = 1L;

	public PdfLink(String id, final PdfLinkCallback callback) {
		super(id, new NonCachedResourceStreamResource() {
			private static final long serialVersionUID = 1L;

//			ByteArrayResource pdfRes = new
//					> ByteArrayResource("application/pdf",myService.getPDF();
//					>
//					> PopupSettings popupSettings = new PopupSettings(FILENAME,
//					> PopupSettings.RESIZABLE|
//					> PopupSettings.SCROLLBARS).setHeight(500).setWidth(700);
//					>
//					> ResourceLink pdfLink = (ResourceLink) new ResourceLink("pdfLink", pdfRes);
//					>
//					> pdfLink.setPopupSettings(popupSettings);
			
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					private static final long serialVersionUID = 1L;

					@Override
					public void write(OutputStream output) {
				        try {
				        	ITextRenderer renderer = new ITextRenderer();
				        	renderer.getSharedContext().setReplacedElementFactory(new MediaReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));
							renderer.setDocumentFromString(callback.getHtml());
				        	renderer.layout();
				        	
				            renderer.createPDF(output);
				        } catch (DocumentException e) {
							e.printStackTrace();
						} finally {
				            if (output != null) {
				                try {
				                	output.close();
				                } catch (IOException e) {
				                }
				            }
				        }
					}

					@Override
					public String getContentType() {
						return "application/pdf";
					}
				};
			}
		}.setFileName(callback.getFileName()));
		
		PopupSettings popupSettings = new PopupSettings(callback.getFileName(), PopupSettings.RESIZABLE| PopupSettings.SCROLLBARS).setHeight(500).setWidth(700);
		setPopupSettings(popupSettings);
	}
}
