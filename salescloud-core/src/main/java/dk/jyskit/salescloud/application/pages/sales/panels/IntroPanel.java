package dk.jyskit.salescloud.application.pages.sales.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import dk.jyskit.salescloud.application.model.PageInfo;

public class IntroPanel extends Panel {
	public IntroPanel(String id, PageInfo pageInfo) {
		super(id);
		
		setOutputMarkupId(true);
		
//		add(new Image("businessAreaLogo", TdcTheme.MOBILE_LOGO_REFERENCE));
		
		add(new Label("title", Model.of(pageInfo.getTitle())));

		addContent(pageInfo);
	}

	protected void addContent(PageInfo pageInfo) {
		add(new Label("content", Model.of(pageInfo.getIntroHtml())).setEscapeModelStrings(false));
	}
}
