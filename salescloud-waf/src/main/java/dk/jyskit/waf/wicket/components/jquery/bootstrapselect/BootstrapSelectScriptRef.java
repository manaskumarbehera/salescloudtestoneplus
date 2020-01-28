package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class BootstrapSelectScriptRef extends JavaScriptResourceReference {
	private static BootstrapSelectScriptRef INSTANCE = new BootstrapSelectScriptRef();

	public static BootstrapSelectScriptRef get() {
		return INSTANCE;
	}

	public BootstrapSelectScriptRef() {
		super(BootstrapSelectScriptRef.class, "res/bootstrap-select.js");
	}

	@Override
	public List<HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = new ArrayList<>();
		dependencies.add((JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())));
		return dependencies;
	}
}
