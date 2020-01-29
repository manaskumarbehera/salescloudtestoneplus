package dk.jyskit.waf.utils.dataexport.spreadsheets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class ExcelLink<T> extends ResourceLink<Void> {

	public ExcelLink(String id, final ExcelLinkCallback<T> callback) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try {
							ExcelSpreadsheet<T> spreadsheet = new ExcelSpreadsheet<T>("todo") {
								@Override
								public List<ExcelCol<T>> getCols() {
									return callback.getCols();
								}

								@Override
								public Collection<T> getRows() {
									return callback.getRows();
								}
							};
							
							spreadsheet.getWorkbook().write(output);
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
		}.setFileName(callback.getFileName()));
	}
}		
