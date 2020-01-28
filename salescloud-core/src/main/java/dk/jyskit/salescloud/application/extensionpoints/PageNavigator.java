package dk.jyskit.salescloud.application.extensionpoints;

import org.apache.wicket.markup.html.WebPage;


public interface PageNavigator {
	Class<? extends WebPage> first();
	Class<? extends WebPage> prev(WebPage currentPage);
	Class<? extends WebPage> next(WebPage currentPage);
}
