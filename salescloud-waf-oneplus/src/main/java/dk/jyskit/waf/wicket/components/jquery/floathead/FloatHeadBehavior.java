package dk.jyskit.waf.wicket.components.jquery.floathead;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;

/**
 * Locks a table header in place while scrolling - without breaking styles or events bound to the header.
 * Add with a selector to an html table tag and the header will be shown even scrolling out of view.
 * Wicket behavior for  <a href="http://mkoryak.github.io/floatThead/">http://mkoryak.github.io/floatThead/ - Copyright (c) 2012 - 2014 Misha Koryak</a>
 * @author palfred
 *
 */
public class FloatHeadBehavior extends JQueryBehavior {
	private static final long serialVersionUID = 1L;
	private static final String METHOD = "floatThead";

	public FloatHeadBehavior(String selector) {
		this(selector, new FloatHeadOptions());
	}

	public FloatHeadBehavior(String selector, Options options) {
		super(selector, METHOD, options);
		this.initReferences();
	}

	private void initReferences() {
		add(FloatHeadCssRef.get());
		add(FloatHeadScriptRef.get());
	}
}