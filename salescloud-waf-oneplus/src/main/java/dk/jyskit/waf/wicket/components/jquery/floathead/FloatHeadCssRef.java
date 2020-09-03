package dk.jyskit.waf.wicket.components.jquery.floathead;

import org.apache.wicket.request.resource.CssResourceReference;

public class FloatHeadCssRef extends CssResourceReference {
	private static FloatHeadCssRef INSTANCE = new FloatHeadCssRef();

	public static FloatHeadCssRef get() {
		return INSTANCE;
	}

	public FloatHeadCssRef() {
		super(FloatHeadCssRef.class, "res/jquery.floatThead.css");
	}

}
