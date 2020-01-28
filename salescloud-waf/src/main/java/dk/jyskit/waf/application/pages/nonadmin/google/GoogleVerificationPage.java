package dk.jyskit.waf.application.pages.nonadmin.google;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class GoogleVerificationPage extends WebPage {
	private static String siteId;
	
	public GoogleVerificationPage() {
		add(new Label("siteId", siteId).setRenderBodyOnly(true));
	}

	public static void setSiteId(String siteId) {
		GoogleVerificationPage.siteId = siteId;
	}
}
