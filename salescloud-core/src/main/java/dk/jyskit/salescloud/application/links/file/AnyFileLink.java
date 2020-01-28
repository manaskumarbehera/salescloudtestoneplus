package dk.jyskit.salescloud.application.links.file;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class AnyFileLink extends ResourceLink<Void> {

	public AnyFileLink(String id, final String pathRelativeToClassPathRoot, String fileName, final String mimeType) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try {
							InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathRelativeToClassPathRoot);
							IOUtils.copy(inputStream, output);
							inputStream.close();
							output.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public String getContentType() {
						return mimeType;
					}
				};
			}
		}.setFileName(fileName));
	}

}
