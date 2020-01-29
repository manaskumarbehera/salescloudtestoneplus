package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagepreviewanduploadindialog;

import java.util.Map;

import org.apache.wicket.markup.html.form.FormComponentPanel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

/**
 * Show image and lets user select an image to replace it, using a popup dialog.
 * @deprecated Use ImagePreviewAndUploadPanel instead.
 * 
 * @author jan
 */
public class ImagePreviewAndUploadInDialogPanel extends ComponentWithLabelAndValidationPanel<FormComponentPanel> {
	public ImagePreviewAndUploadInDialogPanel(ComponentContainerPanel container, String fieldName, 
			int imageWidth, int imageHeight, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new ImagePreviewAndUploadInDialog("editor", propertyModel, imageWidth, imageHeight), attributesMap);
	}
}
