package dk.jyskit.salescloud.application.pages.systemutils;

import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import java.io.*;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ContractsZipFileLink extends ResourceLink<Void> {
	public ContractsZipFileLink(String id) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						try (ZipOutputStream zos = new ZipOutputStream(output)) {
							Iterator<File> fileIterator = FileUtils.iterateFiles(FileUtils.getTempDirectory(), new String[] {"contract"}, false);
							while (fileIterator.hasNext()) {
								File file = fileIterator.next();
								FileInputStream fis = new FileInputStream(file);
								ZipEntry zipEntry = new ZipEntry(file.getName());
								zos.putNextEntry(zipEntry);

								byte[] bytes = new byte[1024];
								int length;
								while ((length = fis.read(bytes)) >= 0) {
									zos.write(bytes, 0, length);
								}
								zos.closeEntry();
								fis.close();
								file.delete();
							}
							zos.close();
							output.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public String getContentType() {
						return "application/zip";
					}
				};
			}
		}.setFileName("contracts.zip"));
	}
}
