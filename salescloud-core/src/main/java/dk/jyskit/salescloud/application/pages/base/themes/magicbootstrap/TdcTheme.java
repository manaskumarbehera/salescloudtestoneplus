package dk.jyskit.salescloud.application.pages.base.themes.magicbootstrap;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeCssReference;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.waf.wicket.themes.JITTheme;

/**
 * This theme is based on Bootstrap 3 CSS which has been tweaked using http://pikock.github.io/bootstrap-magic/.
 * To edit: import less file on http://pikock.github.io/bootstrap-magic/
 * 
 * @author jan
 */
public class TdcTheme extends JITTheme {
	public final static ResourceReference LOGO_REFERENCE 				= new PackageResourceReference(TdcTheme.class, "images/tdc_logo_42x36.png");
	public final static ResourceReference LOGO_STAGING_REFERENCE		= new PackageResourceReference(TdcTheme.class, "images/tdc_logo_staging.png");
	public final static ResourceReference LOGO_BIG_REFERENCE 			= new PackageResourceReference(TdcTheme.class, "images/tdc_logo_stor.png");
	public final static ResourceReference MOBILE_LOGO_REFERENCE			= new PackageResourceReference(TdcTheme.class, "images/TDC_erhverv_mobile_voice_logo.png");
	
	public final static JavaScriptResourceReference JS_HTML5SHIV		= new JavaScriptResourceReference(TdcTheme.class, "js/html5shiv.js");
	public final static JavaScriptResourceReference JS_RESPOND 			= new JavaScriptResourceReference(TdcTheme.class, "js/respond.min.js");
	
    /**
     * Construct.
     */
    public TdcTheme() {
        super("tdc");
        
        addCss(FontAwesomeCssReference.instance());
        
        if ("true".equals(CoreApplication.get().getSetting("waf.debug.css"))) {
            addCss(new CssResourceReference(TdcTheme.class, "css/magic-bootstrap-min.css"));
        } else {
            addCss(new CssResourceReference(TdcTheme.class, "css/magic-bootstrap.css"));
        }
        addCss(new CssResourceReference(TdcTheme.class, "css/custom.css"));
//        addCss(new CssResourceReference(TdcTheme.class, "css/prettyCheckable.css"));
        
        addCss(new UrlResourceReference(Url.parse("//fonts.googleapis.com/css?family=Open+Sans%7CPT+Serif%7CMonaco")));

        // Must have these AFTER the .css files
        addJavaScriptInHead(JS_HTML5SHIV, "lt IE 9");
        addJavaScriptInHead(JS_RESPOND, "lt IE 9");
    }
    
    @Override
    public List<HeaderItem> getDependencies() {
    	return null;
    }
}
