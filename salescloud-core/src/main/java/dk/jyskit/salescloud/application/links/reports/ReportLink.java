package dk.jyskit.salescloud.application.links.reports;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.google.inject.Provider;
import com.lowagie.text.DocumentException;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class ReportLink extends ResourceLink<Void> {
	private static final long serialVersionUID = 1L;

	public ReportLink(String id, String fileName, final Provider<String> reportProvider) {
		super(id, new NonCachedResourceStreamResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					private static final long serialVersionUID = 1L;

					@Override
					public void write(OutputStream output) {
				        try {
				        	ITextRenderer renderer = new ITextRenderer();
				        	renderer.getSharedContext().setReplacedElementFactory(new MediaReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));
							renderer.setDocumentFromString(reportProvider.get());
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
		}.setFileName(fileName));
		setOutputMarkupId(true);
	}
}
