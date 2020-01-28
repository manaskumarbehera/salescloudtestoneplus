package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;

public class BootstrapSelectBehavior extends JQueryBehavior {
	private static final long serialVersionUID = 1L;
	private static final String METHOD = "selectpicker";

	public BootstrapSelectBehavior(String selector) {
		this(selector, new BootstrapSelectOptions());
	}

	public BootstrapSelectBehavior(String selector, Options options) {
		super(selector, METHOD, options);
		this.initReferences();
	}

	private void initReferences() {
		add(BootstrapSelectCssRef.get());
		add(BootstrapSelectScriptRef.get());
	}
}