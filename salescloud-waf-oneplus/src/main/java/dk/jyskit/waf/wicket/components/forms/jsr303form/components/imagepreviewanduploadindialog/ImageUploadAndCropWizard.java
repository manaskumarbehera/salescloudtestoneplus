package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagepreviewanduploadindialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.PackageResourceReference;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.images.cropping.CroppableImage;
import dk.jyskit.waf.wicket.components.images.temporaryimage.TemporaryImage;

@Data
@EqualsAndHashCode(callSuper=false)
@Slf4j
public class ImageUploadAndCropWizard extends Wizard implements Serializable {
	@NotNull
	private List<FileUpload> fileUploads;

	private byte[] imageDataSource;
	private final int imageWidthTarget;
	private final int imageHeightTarget;
	private File tempFileSource;

	/**
	 * Construct.
	 *
	 * @param id
	 *            The component id
	 * @param imageWidth
	 * @param imageHeight
	 */
	public ImageUploadAndCropWizard(String id, int imageWidth, int imageHeight) {
		super(id, false);
		this.imageWidthTarget 	= imageWidth;
		this.imageHeightTarget 	= imageHeight;

		init(new DynamicWizardModel(new Step1()));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(new PackageResourceReference(ImageUploadAndCropWizard.class, "ImageUploadAndCropWizard.css")));
	}

	/**
	 * Step 1
	 */
	private final class Step1 extends DynamicWizardStep {
		public Step1() {
			super(null, "Trin 1 af 3", "Upload billede");

			Jsr303Form<ImageUploadAndCropWizard> step1Form = new Jsr303Form<ImageUploadAndCropWizard>("step1Form", ImageUploadAndCropWizard.this);
			add(step1Form);

			step1Form.addFileUploadsField("fileUploads");
			step1Form.addIndicatingSubmitButton("upload", Buttons.Type.Primary, new AjaxSubmitListener() {

				@Override
				public void onSubmit(AjaxRequestTarget target) {
					if (fileUploads == null) {
						error("Der er ikke valgt en fil");
					} else {
						try {
							imageDataSource = fileUploads.get(0).getBytes();
							tempFileSource = File.createTempFile("tmpimg", ".png");
							FileUtils.writeByteArrayToFile(tempFileSource, imageDataSource);

							ImageUploadAndCropWizard.this.getWizardModel().next();
							target.add(ImageUploadAndCropWizard.this);
						} catch (Exception e) {
							error("Filen blev ikke uploadet korrekt (" + e.getMessage() + ")");
						}
					}
				}
			});
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			return new Step2(this);
		}
	}

	/**
	 * Step 2
	 */
	private final class Step2 extends DynamicWizardStep {
		private TemporaryImage image;  // MUST be finalized with the step!!!
		private int x;
		private int y;
		private int w;
		private int h;

		public Step2(DynamicWizardStep prevStep) {
			super(prevStep, "Trin 2 af 3", "Vælg udsnit");

			CroppableImage croppableImage = new CroppableImage("img", (new TemporaryImage(imageDataSource)).getUrl(getRequestCycle()),
					400, imageWidthTarget, imageHeightTarget) {
				@Override
				public void onCrop(AjaxRequestTarget target, int x, int y, int w, int h) {
					Step2.this.x = x;
					Step2.this.y = y;
					Step2.this.w = w;
					Step2.this.h = h;

					ImageUploadAndCropWizard.this.getWizardModel().next();
					target.add(ImageUploadAndCropWizard.this);
				}
			};
			add(croppableImage);
		}

		@Override
		public boolean isLastStep() {
			return false;
		}

		@Override
		public IDynamicWizardStep next() {
			return new Step3(this, x, y, w, h);
		}
	}

	/**
	 * Step 3
	 */
	private final class Step3 extends DynamicWizardStep {
		private TemporaryImage image;  // MUST be finalized with the step!!!

		public Step3(DynamicWizardStep prevStep, int x, int y, int w, int h) {
			super(prevStep, "Trin 3 af 3", "Godkend billede");

			try {
				Jsr303Form<Step3> step3Form = new Jsr303Form<Step3>("step3Form", Step3.this);
				add(step3Form);

				ByteArrayOutputStream baos = new ByteArrayOutputStream(imageDataSource.length * 3 / 4);
				Thumbnails.of(tempFileSource)
					.sourceRegion(new Coordinate(x, y), w, h)
					.size(imageWidthTarget, imageHeightTarget)
					.toOutputStream(baos);
				image = new TemporaryImage(baos.toByteArray());

				step3Form.addImage(image.getUrl(getRequestCycle()), 400);
				step3Form.addSubmitButton("accept", Buttons.Type.Primary, new AjaxSubmitListener() {
					@Override
					public void onSubmit(AjaxRequestTarget target) {
						ImageUploadAndCropWizard.this.getWizardModel().next();
						target.add(ImageUploadAndCropWizard.this);
					}
				});
			} catch (IOException e) {
				log.error("Failed to crop image", e);
			}
		}

		@Override
		public boolean isLastStep() {
			return true;
		}

		@Override
		public IDynamicWizardStep next() {
			return null;
		}
	}

//	@Override
//	protected Component newButtonBar(String id) {
//		return new ButtonBar(id, this);
//	}

	/**
	 * @see org.apache.wicket.extensions.wizard.Wizard#onCancel()
	 */
	@Override
	public void onCancel() {
		// setResponsePage(Index.class);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.Wizard#onFinish()
	 */
	@Override
	public void onFinish() {
		// setResponsePage(Index.class);
	}
}
