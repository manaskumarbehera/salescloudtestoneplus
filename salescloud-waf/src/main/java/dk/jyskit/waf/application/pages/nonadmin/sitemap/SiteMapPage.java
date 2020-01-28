package dk.jyskit.waf.application.pages.nonadmin.sitemap;

import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class SiteMapPage extends WebPage {
	private static String xml;
	
	public SiteMapPage() {
		add(new Label("xml", xml).setRenderBodyOnly(true));
	}
	
	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("xml", MarkupType.XML_MIME);
	}

	public static void setXml(String xml) {
		SiteMapPage.xml = xml;
	}
}
