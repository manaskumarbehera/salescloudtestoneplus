package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagepreviewanduploadindialog;

import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BlobImageResource;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;

/**
 * Show image and lets user select an image to replace it, using a popup modal.
 * @deprecated Use imagePreviewAndUpload instead.
 * 
 * @author jan
 */
public class ImagePreviewAndUploadInDialog extends FormComponentPanel {
	public ImagePreviewAndUploadInDialog(String id, final PropertyModel propertyModel, final int imageWidth, final int imageHeight) {
		super(id);
		
		add(new Image("image", new BlobImageResource() {
			@Override
			protected Blob getBlob(Attributes attributes) {
				return (SerialBlob) propertyModel.getObject();
			}
		}));

		add(new AjaxLink("upload") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				Jsr303Form jsr303Form = findParent(Jsr303Form.class);
				
				Modal modal = new Modal("dialog");
//				Modal modal = jsr303Form.getModal();
//				modal.setPosition(WindowPosition.TOP);
//				modal.setModal(true);
				modal.setVisible(true);
//				modal.setWidth(630);
				modal.header(Model.of("Vælg billede"));

				WebMarkupContainer dialogContainer = jsr303Form.getDialogContainer();
				dialogContainer.replace(modal);
				
				ImageUploadAndCropWizard wizard = new ImageUploadAndCropWizard("contents", imageWidth, imageHeight);
				wizard.setOutputMarkupId(true);
				modal.replace(wizard);
				
				modal.appendShowDialogJavaScript(target);
				target.add(modal);
			}
		});
	}
}
