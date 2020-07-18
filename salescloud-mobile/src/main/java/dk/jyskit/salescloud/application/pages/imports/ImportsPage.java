package dk.jyskit.salescloud.application.pages.imports;

import dk.jyskit.salescloud.application.MobileSalescloudApplication;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@AuthorizeInstantiation({AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME, UserManagerRole.ROLE_NAME})
@Slf4j
public class ImportsPage extends BasePage {
	FileUploadField fileUploadField;

	public ImportsPage(PageParameters parameters) {
		super(parameters);
		add(new FileUploadForm("form"));
	}

	/**
	 * Form for uploads.
	 */
	private class FileUploadForm extends Form<Void> {
		FileUploadField fileUploadField;

		/**
		 * Construct.
		 *
		 * @param name Component name
		 */
		public FileUploadForm(String name) {
			super(name);

			// set this form to multipart mode (always needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			add(fileUploadField = new FileUploadField("fileInput"));

			setMaxSize(Bytes.kilobytes(200));
			setFileMaxSize(Bytes.kilobytes(200));
		}

		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			final List<FileUpload> uploads = fileUploadField.getFileUploads();
			if (uploads != null) {
				for (FileUpload upload : uploads) {
					handleUpload(upload);
				}
			}
		}

		private void handleUpload(FileUpload upload) {
			if ("contracts.zip".equalsIgnoreCase(upload.getClientFileName())) {
				// Create a new file
				File newFile = new File(new Folder(System.getProperty("java.io.tmpdir")), upload.getClientFileName());

				// Check new file, delete if it already existed
				if (newFile.exists()) {
					// Try to delete the file
					if (!Files.remove(newFile)) {
						throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
					}
				}
				try {
					// Save to new file
					newFile.createNewFile();
					upload.writeTo(newFile);

					ZipFile zipFile = new ZipFile(newFile);
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()){
						ZipEntry entry = entries.nextElement();
						if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".contract")) {
							System.out.println("file : " + entry.getName());
							InputStream inputStream = zipFile.getInputStream(entry);

							// --- Do the actual importing! ---
							MobileContractDao.lookup().saveAndFlush(ImportsUtil.fromJson(IOUtils.toString(inputStream, Charsets.UTF_8)));
						}
					}

					getPage().info("saved file: " + upload.getClientFileName());
				} catch (Exception e) {
					log.error("Failed to import", e);
					throw new IllegalStateException("Unable to write file", e);
				}
			}
		}
	}
}
