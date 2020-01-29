package dk.jyskit.waf.wicket.components.forms.behaviours;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapJavascriptBehavior;
import de.agilecoders.wicket.jquery.util.Json;

/**
 * TODO: documentation + support for more options.
 *
 * You may want to add something like this to your stylesheet:
 * 
 * .floatlabel-wrapper {
 * 		margin-top: 20px;
 * }
 * .floatlabel-wrapper input.form-control {
 * 		height: 40px;
 * }
 *      
 * @author jan
 * @link http://clubdesign.github.io/floatlabels.js/
 */
public class FloatingLabelBehaviour extends BootstrapJavascriptBehavior {
    private static final ResourceReference JS = new JavaScriptResourceReference(FloatingLabelBehaviour.class, "js/floatlabels.min.js");

    private final Map<String, Object> jsonData;

    public FloatingLabelBehaviour() {
        jsonData = new HashMap<String, Object>();
        jsonData.put("slideInput", true);
        jsonData.put("labelStartTop", "20px");
        jsonData.put("labelEndTop", "2px");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse headerResponse) {
        super.renderHead(component, headerResponse);
        
        headerResponse.render(JavaScriptHeaderItem.forReference(JS, null, "floatlabels", true));
        headerResponse.render(OnDomReadyHeaderItem.forScript(createScript(component)));
    }

    protected CharSequence createScript(Component component) {
        CharSequence script = "$('#" + component.getMarkupId() + "').floatlabel(" +
                Json.stringify(jsonData) + ",function(a){});";
        return script;
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.setOutputMarkupId(true);
        BootstrapBaseBehavior.addTo(component);
    }

    @Override
    public void unbind(Component component) {
        super.unbind(component);
        BootstrapBaseBehavior.removeFrom(component);
    }

	public FloatingLabelBehaviour setLabelStartTop(Component component, String labelStartTop) {
		component.setOutputMarkupId(true);
		jsonData.put("labelStartTop", labelStartTop);
		return this;
	}

	public FloatingLabelBehaviour setLabelEndTop(Component component, String labelEndTop) {
		component.setOutputMarkupId(true);
		jsonData.put("labelEndTop", labelEndTop);
		return this;
	}

	public FloatingLabelBehaviour setLabelEndTop(String labelEndTop) {
		jsonData.put("labelEndTop", labelEndTop);
		return this;
	}
}
