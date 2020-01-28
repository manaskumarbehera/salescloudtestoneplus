package dk.jyskit.waf.wicket.components.forms.jsr303form.components.slider;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * This component seems a little too complex with both a "value" and a "hidden" field. For some reason, a simpler
 * approach does not work.
 * Another note: we may want to use Kendo slider instead - because of "tooltips"
 * 
 * @author jan
 */
public class SliderField extends FormComponentPanel {
	private final ResourceReference CSS	= new PackageResourceReference(SliderField.class, "css/jquery.nouislider.min.css");
	private final ResourceReference JS 	= new PackageResourceReference(SliderField.class, "js/jquery.nouislider.min.js");
	private String jsInit;
	
	public SliderField(String wicketId, PropertyModel model, Integer initialValue, int min, int max, int step) {
		super(wicketId, new Model());
		HiddenField<Integer> hiddenField = new HiddenField<>("hidden", model);
		add(hiddenField);
		WebMarkupContainer value = new WebMarkupContainer("value");
		add(value);
		jsInit = 
	    	"$(function() {" +
				"$(\"#" + value.getMarkupId() + "\").noUiSlider(" + 
					"{"
					+ "range: [" + min + ", " + max + "], "
					+ "start: " + initialValue 
					+ ", handles: 1, step: 1, behaviour: 'extend-tap', " 
					+ "serialization: {"
					+ 		"to: $(\"#" + hiddenField.getMarkupId() + "\"), "
					+ 		"resolution: 1}"
					+ "}"	
				+ ");" +
			"})";
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(CSS, "screen"));
		response.render(JavaScriptReferenceHeaderItem.forReference(JS));
		response.render(OnDomReadyHeaderItem.forScript(jsInit));
	}

}

