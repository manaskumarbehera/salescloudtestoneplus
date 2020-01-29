package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagefield;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BlobImageResource;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.waf.utils.images.ImageUtils;
import dk.jyskit.waf.wicket.components.fancybox.Fancybox;
import dk.jyskit.waf.wicket.components.images.cropping.CroppableImage;
import dk.jyskit.waf.wicket.components.images.imagereference.ImageReferenceFactory;
import dk.jyskit.waf.wicket.components.images.temporaryimage.TemporaryImage;

/**
 * Show image and lets user select an image to replace it. The new image can be cropped by the user.
 * Everything happens "inline", using Ajax.
 *
 * @author jan
 */
@Slf4j
public class ImageField extends FormComponentPanel {
    private FileUploadField fileUploadField;
    private byte[] uploadedData;
    private byte[] currentData;
    private String extension;
	private WebMarkupContainer viewModeContainer;
	private WebMarkupContainer uploadModeContainer;
	private WebMarkupContainer cropModeContainer;
	private WebMarkupContainer saveModeContainer;
	private SerialBlob originalImage;
    private enum Mode {MODE_VIEW, MODE_UPLOAD, MODE_CROP, MODE_SAVE};

    /**
     * The propertyModel wraps a SerialBlob.
     *
     * @param id
     */
    public ImageField(String id, final PropertyModel propertyModel, final Integer imageWidth, final Integer imageHeight) {
        super(id, propertyModel);

        viewModeContainer = new WebMarkupContainer("modeView");
        add(viewModeContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));

        uploadModeContainer = new WebMarkupContainer("modeUpload");
        add(uploadModeContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));

        cropModeContainer = new WebMarkupContainer("modeCrop");
        add(cropModeContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));

        saveModeContainer = new WebMarkupContainer("modeSave");
        add(saveModeContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));

        // View

		originalImage = (SerialBlob) propertyModel.getObject();
		useOriginalImage();

		TemporaryImage currentImage = new TemporaryImage(new PropertyModel(ImageField.this, "currentData"), extension);
		final Image image = new Image("image", currentImage.getResourceReference());
		viewModeContainer.add(image.setOutputMarkupId(true));

		addShowFullSize();

        final AjaxLink showUploadLink = new AjaxLink("showUploadLink") {
        	@Override
        	public void onClick(AjaxRequestTarget target) {
        		setMode(Mode.MODE_UPLOAD, target);
        	}
        };
        viewModeContainer.add(showUploadLink.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));

        // Upload

        final Form uploadSubForm = new Form("uploadSubForm");
        uploadModeContainer.add(uploadSubForm);

        uploadSubForm.add(fileUploadField = new FileUploadField("file", new Model((Serializable) new ArrayList<FileUpload>())));

		final FeedbackPanel uploadFeedbackPanel = new FeedbackPanel("feedback", new ComponentFeedbackMessageFilter(uploadSubForm));
		uploadSubForm.add(uploadFeedbackPanel.setOutputMarkupId(true));

		uploadSubForm.add(new AjaxButton("goToCropping") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					FileUpload fileUpload = fileUploadField.getFileUpload();
					if (fileUpload == null) {
						uploadSubForm.error("Ingen fil valgt");
					} else {
						FileUpload fu = fileUploadField.getFileUpload();
						extension = StringUtils.substringAfterLast(fu.getClientFileName(), ".");
						uploadedData = ImageUtils.getDataFromInputStream(fu.getInputStream());

		        		setMode(Mode.MODE_CROP, target);

		        		TemporaryImage tmpImage = new TemporaryImage(new PropertyModel(ImageField.this, "uploadedData"), extension);

		        		final int boxWidth = 500;

		        		CroppableImage croppableImage = new CroppableImage("cropImage", tmpImage.getUrl(getRequestCycle()), boxWidth, imageWidth, imageHeight) {
		        			@Override
		        			public void onCrop(AjaxRequestTarget target, int x, int y, int w, int h) {
		        				try {
		        					BufferedImage uploadedImage = ImageUtils.byteArrayToBufferedImage(uploadedData);
		        					float scale = ((float) uploadedImage.getWidth()) / boxWidth;

		        					ByteArrayOutputStream baos = new ByteArrayOutputStream(uploadedData.length);

		        					// if only one of the aspects is given calculate the other.
		        					int wantedWidth = imageWidth != null ? imageWidth.intValue() : 10;
		        					int wantedHeight = imageHeight != null ? imageHeight.intValue() : 10;
		        					if (imageWidth == null && imageHeight != null) {
		        						wantedWidth = imageHeight * w / h;
		        					}
		        					if (imageWidth != null && imageHeight == null) {
		        						wantedHeight = imageWidth * h/ w;
		        					}

		        					Thumbnails.of(new ByteArrayInputStream(uploadedData))
		        						.sourceRegion(Math.round(x * scale), Math.round(y * scale), Math.round(w * scale), Math.round(h * scale))
		        						.size(wantedWidth, wantedHeight)
		        						.toOutputStream(baos);
		        					currentData = baos.toByteArray();
		        	        		setMode(Mode.MODE_SAVE, target);
		        				} catch (IOException e) {
		        					log.error("Failed to crop image", e);
		        				}
		        			}
		        		};
		        		cropModeContainer.addOrReplace(croppableImage);
					}
				} catch (IOException e) {
					log.error("Could not read image", e);
				}

				target.add(uploadFeedbackPanel);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(uploadFeedbackPanel);
			}
		});

		AjaxLink cancelUploadLink = new AjaxLink("cancelUpload") {
			@Override
			public void onClick(AjaxRequestTarget target) {
        		setMode(Mode.MODE_VIEW, target);
			}
		};
		uploadSubForm.add(cancelUploadLink);

		// Save

        Form saveSubForm = new Form("uploadSubForm");
        saveModeContainer.add(saveSubForm);

        final FeedbackPanel saveFeedbackPanel = new FeedbackPanel("feedback", new ComponentFeedbackMessageFilter(saveSubForm));
		saveSubForm.add(saveFeedbackPanel.setOutputMarkupId(true));

		final Image finalImage = new Image("finalImage", new BlobImageResource() {
			@Override
			public SerialBlob getBlob(Attributes attributes) {
				try {
					return new SerialBlob(currentData);
				} catch (Exception e) {
					log.error("Failed to convert", e);
				}
				return null;
			}
		});
		saveSubForm.add(finalImage.setOutputMarkupId(true));

		saveSubForm.add(new AjaxButton("acceptCropping") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				addShowFullSize();

				target.add(saveFeedbackPanel);

        		setMode(Mode.MODE_VIEW, target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(saveFeedbackPanel);
			}
		});

		AjaxLink cancelCroppingLink = new AjaxLink("cancelCropping") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				useOriginalImage();
				setMode(Mode.MODE_VIEW, target);
			}
		};
		saveSubForm.add(cancelCroppingLink);
    }

	private void useOriginalImage() {
		currentData = null;
		if (originalImage != null) {
			try {
				currentData = originalImage.getBytes(1l, (int) originalImage.length());
			} catch (SerialException e) {
				log.error("Failed to get data", e);
			}
		}
	}

	private void addShowFullSize() {
		TemporaryImage tmpImage = null;
		if (currentData != null) {
			tmpImage = new TemporaryImage(new PropertyModel(ImageField.this, "currentData"), extension);
		}
		if (tmpImage == null) {
			EmptyPanel emptyPanel = new EmptyPanel("fullSizeImage");
			emptyPanel.setVisible(false);
			viewModeContainer.addOrReplace(emptyPanel);
		} else {
			WebMarkupContainer fancybox = new Fancybox("fullSizeImage",
					ImageReferenceFactory.fromURL(""+urlFor(tmpImage.getResourceReference(), new PageParameters())));
			viewModeContainer.addOrReplace(fancybox);
		}
	}

    @Override
    protected void convertInput() {
        /**
         * Build up a new SerialBlob instance.
         */
    	if (currentData != null) {
    		try {
    			BufferedImage croppedImage = ImageUtils.byteArrayToBufferedImage(currentData);
    			log.info("cropped: w=" + croppedImage.getWidth() + ", h=" + croppedImage.getHeight());

    		    setConvertedInput(ImageUtils.getImageBlobFromInputStream(new ByteArrayInputStream(currentData)));
    		} catch (IOException e) {
    			log.error("Could not read image", e);
    		}
    	}
    }

	private void setMode(Mode mode, AjaxRequestTarget target) {
		viewModeContainer.setVisible(mode == Mode.MODE_VIEW);	 	target.add(viewModeContainer);
		uploadModeContainer.setVisible(mode == Mode.MODE_UPLOAD);	target.add(uploadModeContainer);
		cropModeContainer.setVisible(mode == Mode.MODE_CROP);		target.add(cropModeContainer);
		saveModeContainer.setVisible(mode == Mode.MODE_SAVE);		target.add(saveModeContainer);
	}
}