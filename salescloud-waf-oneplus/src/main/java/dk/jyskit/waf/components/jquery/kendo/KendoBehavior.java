package dk.jyskit.waf.components.jquery.kendo;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.less.LessResourceReference;

public class KendoBehavior extends Behavior {
	public static ResourceReference THEME_CSS = createRef("default", false);
	public static ResourceReference COMMON_CSS = createRef("common", false);

	public static ResourceReference createRef(String kendoPart, boolean useLess) {
		if (useLess) {
			return new LessResourceReference(KendoBehavior.class, "css/kendo." + kendoPart + ".less") {};
		} else {
			return new CssResourceReference(KendoBehavior.class, "css/kendo." + kendoPart + ".css");
		}

	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(CssHeaderItem.forReference(KendoBehavior.COMMON_CSS));
		response.render(CssHeaderItem.forReference(KendoBehavior.THEME_CSS));
		String localeAvailable = getLocaleAvailable();
		if (localeAvailable != null) {
			response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(KendoBehavior.class, getResourcePath(localeAvailable))));
			response.render(JavaScriptHeaderItem.forScript("kendo.culture(\"" + localeAvailable + "\");",	"kendocult"));
		}
	}

	private String getLocaleAvailable() {
		String result = null;
		if (WebSession.get() != null && WebSession.get().getLocale() != null) {
			Locale locale = WebSession.get().getLocale();
			if (result == null && !Strings.isEmpty(locale.getCountry())) {
				result = checkLocaleResource(locale.getLanguage() + "-" + locale.getCountry());
			}
			if (result == null && !Strings.isEmpty(locale.getLanguage())) {
				result = checkLocaleResource(locale.getLanguage());
			}
		}
		return result;
	}

	public String checkLocaleResource(String local) {
		String name = getResourcePath(local);
		if (getClass().getResource(name) != null) {
			return local;
		} else {
			return null;
		}
	}

	public String getResourcePath(String local) {
		//TODO rename to use wicket default locale form
		return "cultures/kendo.culture." + local + ".js";
	}

}
