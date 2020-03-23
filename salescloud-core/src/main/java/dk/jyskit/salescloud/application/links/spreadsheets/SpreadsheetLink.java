package dk.jyskit.salescloud.application.links.spreadsheets;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import com.google.inject.Provider;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class SpreadsheetLink extends ResourceLink<Void> {

	public SpreadsheetLink(String id, String fileName, final Provider<Workbook> workbookProvider) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try {
							workbookProvider.get().write(output);
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					@Override
					public String getContentType() {
						return "application/x-msexcel";
					}
				};
			}
		}.setFileName(fileName));
	}

	public SpreadsheetLink(String id, final WorkbookAndFileName workbookAndFileName) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try {
							workbookAndFileName.getWorkbook().get().write(output);
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					@Override
					public String getContentType() {
						return "application/x-msexcel";
					}
				};
			}

			@Override
			protected void setResponseHeaders(ResourceResponse data, Attributes   attributes) {
				data.setFileName(workbookAndFileName.getFileName().getObject());
				super.setResponseHeaders(data, attributes);
			}
		});
	}
}
