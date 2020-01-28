package dk.jyskit.salescloud.application.pages.sales.content;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.MiscPanel;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public abstract class ContentPage extends BasePage {
	protected Panel helpPanel;
	protected Panel introPanel;
	protected Panel miscPanel;
	protected PageInfo pageInfo;

	public ContentPage(PageParameters parameters, PageInfo pageInfo) {
		super(parameters);
		handleParameters(parameters);
		
		this.pageInfo = pageInfo;
		
	}
	
	@Override
	protected void onInitialize() {
		initPage();
		
		WebMarkupContainer withHelp = new WebMarkupContainer("withHelp") {
			@Override
			protected void onConfigure() {
				setVisible(withHelp());
			}
		};
		add(withHelp);
		
		WebMarkupContainer withoutHelp = new WebMarkupContainer("withoutHelp") {
			@Override
			protected void onConfigure() {
				setVisible(!withHelp());
			}
		};
		add(withoutHelp);
		
		introPanel = getIntroPanel("introPanel");
		
		miscPanel = getMiscPanel("miscPanel");
		
		if (withHelp()) {
			withHelp.add(introPanel);
			withHelp.add(miscPanel);
			withHelp.add(getMainPanel("mainPanel", getPageParameters(), pageInfo));
			
			helpPanel = getHelpPanel("helpPanel");
			withHelp.add(helpPanel);
		} else {
			withoutHelp.add(introPanel);
			withoutHelp.add(miscPanel);
			withoutHelp.add(getMainPanel("mainPanel", getPageParameters(), pageInfo));
		}
		super.onInitialize();
	}

	protected void handleParameters(PageParameters parameters) {
	}

	protected void initPage() {
	}

	protected Panel getIntroPanel(String wicketId) {
		return new IntroPanel(wicketId, pageInfo);
	}

	protected Panel getMiscPanel(String wicketId) {
		return new MiscPanel(wicketId, Model.of(pageInfo.getMiscHtml()));
	}
	
	protected Panel getHelpPanel(String wicketId) {
		return new HelpPanel(wicketId, Model.of(pageInfo.getHelpHtml()));
	}

	protected Panel getIntroPanel() {
		return introPanel;
	}

	protected Panel getMiscPanel() {
		return miscPanel;
	}

	protected Panel getHelpPanel() {
		return helpPanel;
	}
	
	/**
	 * Override to drop help and use all space for content
	 * 
	 * @return
	 */
	protected boolean withHelp() {
		return true;
	}
	
	public void replaceIntroPanel(IntroPanel panel) {
		introPanel.replaceWith(panel);
		introPanel = panel;
	}

	public void replaceMiscPanel(MiscPanel panel) {
		miscPanel.replaceWith(panel);
		miscPanel = panel;
	}

	public void replaceHelpPanel(HelpPanel panel) {
		helpPanel.replaceWith(panel);
		helpPanel = panel;
	}

	protected abstract Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo);
}
