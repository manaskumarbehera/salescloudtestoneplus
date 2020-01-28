package dk.jyskit.salescloud.application.links.file;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.KeyValue;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class ZipFileLink extends ResourceLink<Void> {

	public ZipFileLink(String id, final String template, final List<List<KeyValue<String, String>>>values,  final String fileName) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try (ZipOutputStream zos = new ZipOutputStream(output)) {
							for (List<KeyValue<String, String>> list : values) {
								String s = template;
								String f = fileName;
								for (KeyValue<String, String> keyValue : list) {
									s = s.replace(keyValue.getKey(), keyValue.getValue());
									f = f.replace(keyValue.getKey(), keyValue.getValue());
								}
								ZipEntry entry = new ZipEntry(f);
								zos.putNextEntry(entry);
								zos.write(s.getBytes());
								zos.closeEntry();
							}
							zos.close();
							output.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public String getContentType() {
						return "application/zip";
					}
				};
			}
		}.setFileName(fileName));
	}

}
