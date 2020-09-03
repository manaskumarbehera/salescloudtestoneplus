package dk.jyskit.waf.wicket.utils;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.iterator.ComponentHierarchyIterator;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import dk.jyskit.waf.wicket.response.JITJavaScriptReferenceHeaderItem;

public class WicketUtils {

	public enum Language {
		ENGLISH(Locale.ENGLISH, "flag-gb", "English"), FRENCH(Locale.FRENCH, "flag-fr", "Fran\u00E7ais");

		private Locale locale;
		private String className;
		private String displayName;

		private Language(Locale locale, String className, String displayName) {
			this.locale = locale;
			this.className = className;
			this.displayName = displayName;
		}

		public String getClassName() {
			return className;
		}

		public Locale getLocale() {
			return locale;
		}

		public String getDisplayName() {
			return displayName;
		}

		public static boolean isLocaleSupported(Locale locale) {
			return locale == null ? false : isLanguageSupported(locale.getLanguage());
		}

		public static boolean isLanguageSupported(String lang) {
			boolean supported = false;
			for (Language l : values()) {
				if (StringUtils.equals(lang, l.getLocale().getLanguage())) {
					supported = true;
					break;
				}
			}
			return supported;
		}
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public static PageParameters buildParams(String key, Object value) {
		PageParameters params = new PageParameters();
		if (value != null) {
			params.add(key, value);
		}
		return params;
	}

	/**
	 * @param response
	 * @param javaScript
	 */
	public void renderJavaScriptByScript(IHeaderResponse response, String javaScript) {
		response.render(JavaScriptReferenceHeaderItem.forScript(javaScript, "JS-" + javaScript.hashCode()));
	}

	/**
	 * @param response
	 * @param klass
	 */
	public void renderJavaScriptByClassName(IHeaderResponse response, Class<?> klass) {
		response.render(JavaScriptReferenceHeaderItem.forReference(new JavaScriptResourceReference(klass, klass.getSimpleName()
				+ ".js")));
	}

	/**
	 * @param response
	 * @param klass
	 * @param map
	 */
	public void renderJavaScriptByClassName(IHeaderResponse response, Class<?> klass, Map<String, Object> map) {
		TextTemplate template = new PackageTextTemplate(klass, klass.getSimpleName() + ".js");
		String script = template.asString(map);
		IOUtils.closeQuietly(template);
		response.render(OnDomReadyHeaderItem.forScript(script));
	}

	/**
	 * Helper to get localized string without component needed to be added to
	 * page.
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param defaultValue
	 *            The default value (optional)
	 * @return
	 */
	public static String getLocalized(String key, String defaultValue) {
		Localizer localizer = Application.get().getResourceSettings().getLocalizer();
		return localizer.getString(key, null, defaultValue);
	}

	/**
	 * Helper to get localized string without component needed to be added to
	 * page.
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param model
	 *            The model to use for property substitutions in the strings (optional)
	 *            Consider using a MapModel.
	 * @param defaultValue
	 *            The default value (optional)
	 * @return
	 */
	public static String getLocalized(String key, final IModel<?> model, String defaultValue) {
		Localizer localizer = Application.get().getResourceSettings().getLocalizer();
		return localizer.getString(key, null, model, defaultValue);
	}
	
	public static Component findParentOfClass(Component component, Class<? extends Component> componentClass) {
		Component targetComponent = component;
		while (targetComponent != null) {
			if (componentClass.isAssignableFrom(targetComponent.getClass())) {
				return targetComponent;
			}
			targetComponent = targetComponent.getParent();
		}
		return targetComponent;
	}

	public static <T extends Component> T findOnPage(Page page, Class<T> componentClass) {
		ComponentHierarchyIterator iter = page.visitChildren();
		iter.filterByClass(componentClass);
		while (iter.hasNext()) {
			@SuppressWarnings("unchecked")
			T child = (T) iter.next();
			return child;
		}
		return null;
	}

	public static AjaxRequestTarget getAjaxTarget() {
		AjaxRequestTarget target = null;
		RequestCycle requestCycle = RequestCycle.get();
		if (requestCycle != null) {
			target = requestCycle.find(AjaxRequestTarget.class);
		}
		return target;
	}

	/**
	 * @param response
	 * @param klass
	 * @param javaScriptFileName
	 */
	public static void renderJavaScriptByFileName(IHeaderResponse response, Class<?> klass, String javaScriptFileName) {
		response.render(JavaScriptReferenceHeaderItem.forReference(new JavaScriptResourceReference(klass, javaScriptFileName)));
	}

	/**
	 * @param response
	 * @param clazz
	 * @param paths
	 */
	public static void renderJavaScriptsInBody(IHeaderResponse response, Class clazz, String ... paths) {
		for (String path : paths) {
			String fileName = path; 
			if (!fileName.endsWith(".js")) {
				fileName += ".js";
			}
			String id = fileName.replace("/", "").replace(".", "");
			response.render(new JITJavaScriptReferenceHeaderItem(
					new PackageResourceReference(clazz, fileName), null, id, false, null, null, false));
		}
	}

	/**
	 * @param response
	 * @param script
	 */
	public static void renderJavaScriptInBody(IHeaderResponse response, Class clazz, String fileName) {
		String fName = fileName; 
		if (!fName.endsWith(".js")) {
			fName += ".js";
		}
		response.render(new JITJavaScriptReferenceHeaderItem(new PackageResourceReference(clazz, fName), null, fName, false, null, null, false));
	}

	/**
	 * @param response
	 * @param script
	 */
	public static void renderJavaScriptInBody(IHeaderResponse response, Class clazz, String fileName, IModel<Map<String, Object>> variablesModel) {
		String fName = fileName; 
		if (!fName.endsWith(".js")) {
			fName += ".js";
		}
		response.render(new JITJavaScriptReferenceHeaderItem(
				new TextTemplateResourceReference(clazz, fName, "application/javascript", PackageTextTemplate.DEFAULT_ENCODING,
				variablesModel, (Locale) null, (String) null, (String) null), 
				null, fName, false, null, null, false));
	}
	
	/**
	 * Look up specific file paths here: http://www.webjars.org/.
	 * 
	 * @param response
	 * @param filePath
	 */
	public static void renderJavaScriptFromWebJar(IHeaderResponse response, String filePath) {
		response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference(filePath)));
	}

	/**
	 * @param response
	 * @param klass
	 */
	public static void renderCssWithClassName(IHeaderResponse response, Class<?> klass) {
		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(klass, klass.getSimpleName() + ".css")));
	}

	/**
	 * @param response
	 * @param klass
	 * @param fileName
	 */
	public static void renderCssByFileName(IHeaderResponse response, Class<?> klass, String fileName) {
		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(klass, fileName)));
	}

	/**
	 * Look up specific file paths here: http://www.webjars.org/.
	 * 
	 * @param response
	 * @param filePath
	 */
	public static void renderCssFromWebJar(IHeaderResponse response, String filePath) {
		response.render(CssReferenceHeaderItem.forReference(new WebjarsCssResourceReference(filePath)));
	}

	/**
	 * @param response
	 * @param script
	 */
	public static void renderJQueryOnDomReady(IHeaderResponse response, String script) {
		// Make sure JQuery is added first
		response.render(JavaScriptReferenceHeaderItem.forReference(JQueryResourceReference.get()));
		response.render(JavaScriptReferenceHeaderItem.forScript("$(function(){ " + script + " });", "JQUERY-" + script.hashCode()));
	}

	/**
	 * Copy properties using wicket property expressions.
	 * Uses {@link PropertyModel} to copy properties, which cause to function on on wicket models and/or pojos.
	 * Can be used to make copies of model objects if used in a form with ajax updating behavior.
	 * @param src The object to copy properties from
	 * @param dest The dest object the properties to
	 * @param propNames The names or expressions of the properties to copy.
	 */
	public static void copyProperties(Object src, Object dest, String... propNames) {
		if (src != null) {
			for (String name : propNames) {
				new PropertyModel<Object>(dest, name).setObject(new PropertyModel<Object>(src, name).getObject());
			}
		}
	}

	/**
	 * Check whether the component or any child have any feedback message.
	 * 
	 * @param root
	 * @return
	 */
	public static boolean hasFeedbackMessageInHierarchy(Component root) {
		if (root != null) {
			if (root.hasFeedbackMessage()) {
				return true;
			}
			if (root instanceof MarkupContainer) {
				MarkupContainer container = (MarkupContainer) root;
				for (Component component : container) {
					if (hasFeedbackMessageInHierarchy(component)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
