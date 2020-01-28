package dk.jyskit.waf.wicket.response;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.head.filter.AbstractHeaderResponseFilter;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;
import org.apache.wicket.markup.head.filter.OppositeHeaderResponseFilter;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.util.lang.Args;

import com.google.common.collect.Lists;

import de.agilecoders.wicket.core.Bootstrap;

/**
 * @author jan
 */
public class JITJavaScriptHeaderResponseDecorator implements IHeaderResponseDecorator {

    final List<FilteringHeaderResponse.IHeaderResponseFilter> filters;

    /**
     * Construct. Uses {@link de.agilecoders.wicket.core.settings.IBootstrapSettings#getJsResourceFilterName()}
     * as filter name.
     */
    public JITJavaScriptHeaderResponseDecorator() {
        this(Bootstrap.getSettings().getJsResourceFilterName());
    }

    /**
     * Construct.
     *
     * @param filterName The name of the footer container
     */
    public JITJavaScriptHeaderResponseDecorator(final String filterName) {
        Args.notEmpty(filterName, "filterName");

        filters = Lists.newArrayList();

		final AbstractHeaderResponseFilter jsAcceptingFilter = new AbstractHeaderResponseFilter(filterName) {
			public boolean accepts(HeaderItem item) {

				if (item instanceof OnDomReadyHeaderItem || item instanceof OnLoadHeaderItem) {
					return true;
				} else if (item instanceof JITJavaScriptReferenceHeaderItem) {
					if (((JITJavaScriptReferenceHeaderItem) item).isInHead()) {
						return false;
					} else {
						return true;
					}
				} else if (item instanceof JavaScriptHeaderItem) {
					return true;
				}
				return false;
			}
		};

        filters.add(jsAcceptingFilter);
        filters.add(new OppositeHeaderResponseFilter("headBucket", jsAcceptingFilter));
    }

    /**
     * decorates the original {@link IHeaderResponse}
     *
     * @param response original {@link IHeaderResponse}
     * @return decorated {@link IHeaderResponse}
     */
    public IHeaderResponse decorate(final IHeaderResponse response) {
        return new FilteringHeaderResponse(response, "headBucket", filters);
    }
}





