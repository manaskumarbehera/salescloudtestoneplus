package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.utils.dataexport.common.NonCachedResourceStreamResource;

public class ZipFileLink extends ResourceLink<Void> {
	public ZipFileLink(String id, final String zipFileName, final String contentFileName, final String template, final MobileContract contract) {
		super(id, new NonCachedResourceStreamResource() {
			@Override
			protected IResourceStream getResourceStream() {
				return new AbstractResourceStreamWriter() {
					@Override
					public void write(OutputStream output) {
						List<List<KeyValue<String, String>>> values = new LinkedList<>();
						int i = 0;
						for (Subscription subscription: MobileSession.get().getContract().getSubscriptions()) {
							List<KeyValue<String, String>> keyValues = new LinkedList<>();
							values.add(keyValues);
							boolean yearly = true;
							for (Product product : subscription.getProducts()) {
								if (product.getPublicName().indexOf("åned") > -1) {
									yearly = false;
									break;
								}
							}
							if (yearly) {
								keyValues.add(new UnmodifiableMapEntry("abonnement", "årsabonnement"));
							} else {
								keyValues.add(new UnmodifiableMapEntry("abonnement", "månedsabonnement"));
							}
							keyValues.add(new UnmodifiableMapEntry("navn", subscription.getFirstName() + " " + subscription.getLastName()));
							keyValues.add(new UnmodifiableMapEntry("email", subscription.getEmail()));
//							keyValues.add(new UnmodifiableMapEntry("filename", "cloudacademy-bekræftelse-" + (++i)));
						}
						try (ZipOutputStream zos = new ZipOutputStream(output)) {
							for (List<KeyValue<String, String>> list : values) {
								String s = template;
								String f = contentFileName;
								for (KeyValue<String, String> keyValue : list) {
									s = s.replace("${" + keyValue.getKey() + "}", keyValue.getValue());
									f = f.replace("${" + keyValue.getKey() + "}", keyValue.getValue());
								}
								ZipEntry entry = new ZipEntry(f);
								zos.putNextEntry(entry);
								zos.write(s.getBytes());
								zos.closeEntry();
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
		}.setFileName(zipFileName));
	}

}
