package dk.jyskit.waf.wicket.themes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.util.io.IOUtils;

import com.google.common.collect.Lists;

import de.agilecoders.wicket.core.settings.ITheme;
import dk.jyskit.waf.wicket.response.JITJavaScriptReferenceHeaderItem;

/**
 * {@link ITheme} implementation with more control of placement of javascript and also easier to use.
 *
 * @author jan
 */
public abstract class JITTheme implements ITheme {

    private final String name;
    private final List<ReferenceAndCondition> resourceReferences;

    /**
     * Construct.
     *
     * @param name               Unique theme name
     */
    public JITTheme(final String name) {
        this.name = name;
        this.resourceReferences = Lists.newArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return name;
    }
    
    public void addCss(CssResourceReference reference) {
    	addCss(reference, null);
    }

    public void addCss(CssResourceReference reference, String condition) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.isCss 		= true;
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	resourceReferences.add(rac);
    }

    public void addCss(UrlResourceReference reference) {
    	addCss(reference, null);
    }

    public void addCss(UrlResourceReference reference, String condition) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.isCss 		= true;
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptInHead(JavaScriptResourceReference reference) {
    	addJavaScriptInHead(reference, null);
    }

    public void addJavaScriptInHead(JavaScriptResourceReference reference, String condition) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	rac.inHead		= true;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptInBody(JavaScriptResourceReference reference) {
    	addJavaScriptInBody(reference, null);
    }

    public void addJavaScriptInBody(JavaScriptResourceReference reference, String condition) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.reference	= reference;
    	rac.condition	= condition;
    	rac.inHead		= false;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptOnLoad(Class baseClass, String relativeFileName) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();

		InputStream inputStream = null;
    	try {
    		String path = baseClass.getCanonicalName();
    		path = path.substring(0, path.lastIndexOf('.'));
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.replace('.', '/') + '/' + relativeFileName);
			rac.javaScript = IOUtils.toString(inputStream);
	    	rac.onLoad		= true;
	    	resourceReferences.add(rac);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

    public void addJavaScriptOnDomReady(Class baseClass, String relativeFileName) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();

		InputStream inputStream = null;
    	try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(baseClass.getCanonicalName().replace('.', '/') + '/' + relativeFileName);
			rac.javaScript	= IOUtils.toString(inputStream);
	    	rac.onDomReady	= true;
	    	resourceReferences.add(rac);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        for (ReferenceAndCondition rac : resourceReferences) {
            if (rac.isCss) {
        		response.render(CssHeaderItem.forReference(rac.reference, null, null, rac.condition));
            } else {
            	if (rac.onLoad) {
               		response.render(OnLoadHeaderItem.forScript(rac.javaScript));
            	} else if (rac.onDomReady) {
               		response.render(OnDomReadyHeaderItem.forScript(rac.javaScript));
            	} else {
               		response.render(new JITJavaScriptReferenceHeaderItem(rac.reference, null, null, false, null, rac.condition, rac.inHead));
            	}
            }
        }
    }
    
    private static class ReferenceAndCondition {
		public CharSequence javaScript;
		private boolean isCss;
		private ResourceReference reference;
    	private String condition;
    	private boolean inHead;
    	public boolean onLoad;
    	public boolean onDomReady;
    }

	/* (non-Javadoc)
	 * 
	 * This is another way of achieving the same as my support for UrlResourceReference.
	 * 
	 * @see de.agilecoders.wicket.core.settings.ITheme#getCdnUrls()
	 */
	@Override
	public Iterable<String> getCdnUrls() {
		return null;
	}
}
