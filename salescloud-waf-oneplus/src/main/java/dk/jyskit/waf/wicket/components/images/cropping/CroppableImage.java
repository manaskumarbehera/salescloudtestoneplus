package dk.jyskit.waf.wicket.components.images.cropping;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;

/**
 * We are dealing with 3 image sizes here:
 * - target size, i.e. the size we want the resulting image be.
 * - original image size
 * - box size, i.e. the size of the original image as it appears on screen. This size may be smaller
 * 	 than original size. The JCrop plugin takes care of scaling things properly.
 *
 * @author jan
 */
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class CroppableImage extends Panel {
	private static final long serialVersionUID = 1L;

	private String x;
	private String y;
	private String w;
	private String h;

	private final int maxBoxWidth;

	private final Integer targetWidth;

	private final Integer targetHeight;

	/**
	 * If only one target with is set then there is no aspect ration set on the selection, and the resulting image is scaled the same amount in width and height.
	 * @param id
	 * @param imageUrl
	 * @param maxBoxWidth
	 * @param targetWidth
	 * @param targetHeight
	 */
	public CroppableImage(String id, final String imageUrl, int maxBoxWidth, Integer targetWidth, Integer targetHeight) {
		super(id);
		this.maxBoxWidth = maxBoxWidth;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;

		Jsr303Form<CroppableImage> form = new Jsr303Form<CroppableImage>("croppableImageForm", this);
		add(form);

		form.addHiddenField("x");
		form.addHiddenField("y");
		form.addHiddenField("w");
		form.addHiddenField("h");

		WebMarkupContainer image = form.addImage(imageUrl, maxBoxWidth);
		image.setOutputMarkupId(true);
		image.setMarkupId("jcrop_target");

		form.addIndicatingSubmitButton("crop", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				onCrop(target, Float.valueOf(x).intValue(), Float.valueOf(y).intValue(), Float.valueOf(w).intValue(), Float.valueOf(h).intValue());
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CroppableImage.class, "js/jquery.Jcrop.min.js")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CroppableImage.class, "js/jquery.color.js")));

 		String template = (targetWidth == null || targetHeight == null) ?  "CroppableImage_NoLimit.js" : "CroppableImage.js";
		TextTemplate js = new PackageTextTemplate(CroppableImage.class, template);
 		Map<String, Object> variables = new HashMap<String, Object>();
 		variables.put("component.targetHeight", targetHeight);
 		variables.put("component.targetWidth", targetWidth);
 		js.interpolate(variables);
 		response.render(OnDomReadyHeaderItem.forScript(js.asString()));

		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(CroppableImage.class, "jquery.Jcrop.css")));
	}

	public abstract void onCrop(AjaxRequestTarget target, int x, int y, int w, int h);
}
