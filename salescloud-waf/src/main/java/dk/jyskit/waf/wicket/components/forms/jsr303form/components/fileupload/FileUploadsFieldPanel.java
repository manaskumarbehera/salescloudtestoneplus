package dk.jyskit.waf.wicket.components.forms.jsr303form.components.fileupload;

import java.util.Map;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class FileUploadsFieldPanel extends ComponentWithLabelAndValidationPanel<FileUploadField> {
	public FileUploadsFieldPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		FileUploadField fileUploadField = new FileUploadField("editor", propertyModel);
		
		init(fileUploadField, attributesMap);
	}
}
