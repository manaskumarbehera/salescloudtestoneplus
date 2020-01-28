package dk.jyskit.waf.wicket.components.jquery.floathead;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class UnderschoreScriptRef extends JavaScriptResourceReference {
	private static UnderschoreScriptRef INSTANCE = new UnderschoreScriptRef();

	public static UnderschoreScriptRef get() {
		return INSTANCE;
	}

	public UnderschoreScriptRef() {
		super(UnderschoreScriptRef.class, "res/underscore.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = new ArrayList<>();
		dependencies.add((JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())));
		return dependencies;
	}
}
