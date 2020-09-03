package dk.jyskit.waf.application.pages.nonadmin.robots;

import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;

public class RobotsAllowNonAdminPage extends WebPage {
	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("txt", "text/plain");
	}
}
