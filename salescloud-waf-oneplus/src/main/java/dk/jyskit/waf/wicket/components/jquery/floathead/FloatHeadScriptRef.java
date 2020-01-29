package dk.jyskit.waf.wicket.components.jquery.floathead;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class FloatHeadScriptRef extends JavaScriptResourceReference {
	private static FloatHeadScriptRef INSTANCE = new FloatHeadScriptRef();

	public static FloatHeadScriptRef get() {
		return INSTANCE;
	}

	public FloatHeadScriptRef() {
		super(FloatHeadScriptRef.class, "res/jquery.floatThead.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = new ArrayList<>();
		dependencies.add((JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())));
		dependencies.add((JavaScriptHeaderItem.forReference(UnderschoreScriptRef.get())));
		return dependencies;
	}
}
