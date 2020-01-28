package dk.jyskit.waf.wicket.components.forms.jsr303form.components.progressbar;

import java.util.Map;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.fileupload.FileUploadField;

public class UploadProgressBarPanel extends ComponentWithLabelAndValidationPanel<UploadProgressBarField> {
	public UploadProgressBarPanel(ComponentContainerPanel container, final String fieldName, Form form, FileUploadField fileUploadField, Map<String, String> attributesMap) {
		super(container, fieldName);
		UploadProgressBarField progressBarField = new UploadProgressBarField("editor", form, fileUploadField);
		
		init(progressBarField, Model.of(" "), attributesMap);
	}
}
