package dk.jyskit.waf.application.pages;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.ChromeFrameMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.wicket.utils.WicketUtils;

public abstract class JITBasePage extends WebPage {
	protected TransparentWebMarkupContainer body;

	public JITBasePage() {
        HtmlTag htmlTag = new HtmlTag("html", getSession().getLocale(), true);
//        HtmlTag htmlTag = new HtmlTag("html", getSession().getLocale(), false);
		add(htmlTag);
		
		body = new TransparentWebMarkupContainer("bodyTag");
		add(body);
        
        // Make sure theme resources are rendered
        add(new BootstrapBaseBehavior());
        
        // add(new OptimizedMobileViewportMetaTag("viewport"));
        // <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
        MobileViewportMetaTag viewportMetaTag = new MobileViewportMetaTag("viewport");
        viewportMetaTag.setWidth("device-width");
        viewportMetaTag.setInitialScale("1.0");
        viewportMetaTag.setMinimumScale("1.0");
        viewportMetaTag.setMaximumScale("1.0");
        viewportMetaTag.setUserScalable(false);
        add(viewportMetaTag);
        
        add(new ChromeFrameMetaTag("chrome-frame"));
        
        // Placeholder for javascripts near the bottom of the HTML file.
        add(new HeaderResponseContainer("footer-container", "footer-container"));
        
		add(new Behavior() {
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				// Please add icons "manually". 
				// response.render(new FaviconHeaderItem(new PackageResourceReference(getApplication().getClass(), "favicon.ico")));
		        if (hasCss()) {
		        	// Page CSS is added like this (and not using renderHead method of WebPage) to make
		        	// sure the theme contributes its CSS first!
		        	WicketUtils.renderCssWithClassName(response, getPageClass());
		        }
			}
		});
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();

		SEOInfo seoInfo = getSEOInfo();
        
		add(new Label("headTitle", getLocalizedString(seoInfo.getTitle(), seoInfo.getTitle())));
        add(new MetaTag("metaKeywords", Model.of("keywords"), Model.of(seoInfo.getKeywords())));
        add(new MetaTag("metaDescription", Model.of("description"), Model.of(seoInfo.getDescription())));
        add(new MetaTag("metaAuthor", Model.of("author"), Model.of(getAuthor())));
        add(new MetaTag("metaFormatDetection", Model.of("format-detection"), Model.of("telephone=no")));
	}

	/**
	 * @return true if the page has CSS (with same name as class)
	 */
	protected abstract boolean hasCss();

	protected abstract SEOInfo getSEOInfo();
	
	protected abstract String getAuthor();

	public String getLocalizedString(String key) {
		return getLocalizedString(key, null);
	}

	public String getLocalizedString(String key, String defaultValue) {
		return Application.get().getResourceSettings().getLocalizer().getString(key, this, defaultValue);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		Bootstrap.renderHead(response);
//		response.render(JavaScriptHeaderItem.forReference(JqueryUIAllJavaScriptReference.instance()));
	}
	
	@Override
	protected void onConfigure() {
		JITAuthenticatedWicketApplication.get().useNonAdminTheme();
	}
}
