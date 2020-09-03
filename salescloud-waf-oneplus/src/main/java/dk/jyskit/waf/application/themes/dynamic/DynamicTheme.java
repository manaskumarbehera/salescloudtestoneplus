package dk.jyskit.waf.application.themes.dynamic;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
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

public abstract class DynamicTheme implements ITheme {

    private final String name;
    private final Map<String, List<ReferenceAndCondition>> idToResourceReferences = new HashMap<String, List<ReferenceAndCondition>>();

    /**
     * Construct.
     *
     * @param name               Unique theme name
     */
    public DynamicTheme() {
        this.name = "dynamic";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return name;
    }
    
    public void addCss(String themeId, CssResourceReference reference) {
    	addCss(themeId, reference, null);
    }

    public void addCss(String themeId, CssResourceReference reference, String condition) {
    	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
    	if (resourceReferences == null) {
    		resourceReferences = Lists.newArrayList();
    		idToResourceReferences.put(themeId, resourceReferences);
    	}
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.isCss 		= true;
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	resourceReferences.add(rac);
    }

    public void addCss(String themeId, UrlResourceReference reference) {
    	addCss(themeId, reference, null);
    }

    public void addCss(String themeId, UrlResourceReference reference, String condition) {
    	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
    	if (resourceReferences == null) {
    		resourceReferences = Lists.newArrayList();
    		idToResourceReferences.put(themeId, resourceReferences);
    	}
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.isCss 		= true;
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptInHead(String themeId, JavaScriptResourceReference reference) {
    	addJavaScriptInHead(themeId, reference, null);
    }

    public void addJavaScriptInHead(String themeId, JavaScriptResourceReference reference, String condition) {
    	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
    	if (resourceReferences == null) {
    		resourceReferences = Lists.newArrayList();
    		idToResourceReferences.put(themeId, resourceReferences);
    	}
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.reference 	= reference;
    	rac.condition 	= condition;
    	rac.inHead		= true;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptInBody(String themeId, ResourceReference reference) {
    	addJavaScriptInBody(themeId, reference, null);
    }

    public void addJavaScriptInBody(String themeId, ResourceReference reference, String condition) {
    	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
    	if (resourceReferences == null) {
    		resourceReferences = Lists.newArrayList();
    		idToResourceReferences.put(themeId, resourceReferences);
    	}
    	ReferenceAndCondition rac = new ReferenceAndCondition();
    	rac.reference	= reference;
    	rac.condition	= condition;
    	rac.inHead		= false;
    	resourceReferences.add(rac);
    }

    public void addJavaScriptOnLoad(String themeId, Class baseClass, String relativeFileName) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();

		InputStream inputStream = null;
    	try {
        	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
        	if (resourceReferences == null) {
        		resourceReferences = Lists.newArrayList();
        		idToResourceReferences.put(themeId, resourceReferences);
        	}
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

    public void addJavaScriptOnDomReady(String themeId, Class baseClass, String relativeFileName) {
    	ReferenceAndCondition rac = new ReferenceAndCondition();

		InputStream inputStream = null;
    	try {
        	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
        	if (resourceReferences == null) {
        		resourceReferences = Lists.newArrayList();
        		idToResourceReferences.put(themeId, resourceReferences);
        	}
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
    	String themeId = getThemeId();
    	
    	List<ReferenceAndCondition> resourceReferences = idToResourceReferences.get(themeId);
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
    
    public abstract String getThemeId();
    
    private static class ReferenceAndCondition {
		public CharSequence javaScript;
		private ResourceReference reference;
    	private String condition;
    	private boolean isCss;
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
	
	@Override
	public List<HeaderItem> getDependencies() {
		return null;
	}
}
