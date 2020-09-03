package dk.jyskit.waf.wicket.components.forms.jsr303form.components.fileupload;

import java.util.List;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import dk.jyskit.waf.wicket.response.JITJavaScriptReferenceHeaderItem;

/**
 * FileUploadField from Wicket Bootstrap is NOT used because it uses Jasny which is "blacklisted" in WAF
 * because it overrides Bootstrap colors.
 */
public class FileUploadField extends org.apache.wicket.markup.html.form.upload.FileUploadField {
	private final static ResourceReference jsRef = new JavaScriptResourceReference(FileUploadsFieldPanel.class, "jasny-fileinput.js");

	public FileUploadField(String id) {
		super(id);
	}

	public FileUploadField(String id, IModel<List<FileUpload>> model) {
		super(id, model);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(new PackageResourceReference(FileUploadField.class, "jasny-fileinput.css")));
		response.render(new JITJavaScriptReferenceHeaderItem(jsRef, null, "jasny-fileinput-js", false, null, null, false));
	}
}
