package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import org.apache.wicket.request.resource.CssResourceReference;

public class BootstrapSelectCssRef extends CssResourceReference {
	private static BootstrapSelectCssRef INSTANCE = new BootstrapSelectCssRef();

	public static BootstrapSelectCssRef get() {
		return INSTANCE;
	}
	
	public BootstrapSelectCssRef() {
		super(BootstrapSelectCssRef.class, "res/bootstrap-select.css");
	}

}
