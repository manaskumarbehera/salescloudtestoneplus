package dk.jyskit.salescloud.application.pages.sales.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.PageInfo;

@Slf4j
public class MultiPageTabPanel extends Panel {
	@Inject
	private PageNavigator navigator;
	
	public MultiPageTabPanel(String wicketId, final MultiPageInfoPage parentPage, PageInfoPanelFactory[] childPages) {
		super(wicketId);
		
		final Form<Contract> form = new Form<>("form");
		add(form);
		
		List<ITab> tabs = new ArrayList();
		final Map<Integer, PageInfoPanelFactory> tabIndexToPage = new HashMap<Integer, PageInfoPanelFactory>();
		int index = 0;
		
		for (final PageInfoPanelFactory childPage : childPages) {
			PageInfo pageInfo = childPage.getPageInfo();
			String title = (StringUtils.isEmpty(pageInfo.getSubTitle()) ? pageInfo.getTitle() : pageInfo.getSubTitle());
			tabs.add(new AbstractTab(new Model<String>(title)) {
				public Panel getPanel(String panelId) {
					return childPage.getMainPanel(panelId);
				}
			});
			tabIndexToPage.put(index++, childPage);
		}

		final AjaxBootstrapTabbedPanel<ITab> tabbedPanel = new AjaxBootstrapTabbedPanel<ITab>("tabs", tabs) {
			/**
			 * {@inheritDoc}
			 */
			@Override
			protected WebMarkupContainer newLink(String linkId, final int index) {
				return new AjaxSubmitLink(linkId, form) {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						PageInfoPanelFactory page = tabIndexToPage.get(getSelectedTab());
						if (page.save()) {
							setSelectedTab(index);
							page = tabIndexToPage.get(getSelectedTab());
							parentPage.updateCurrentChildPage(target, page);
							if (target != null) {
								target.add(form);
							}
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						log.error("There is a problem");
					}
				};
			}
		};
		form.add(tabbedPanel);
		
		AjaxButton prevButton = new AjaxButton("prevButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PageInfoPanelFactory page = tabIndexToPage.get(tabbedPanel.getSelectedTab());
				if (page.save()) {
					if (tabbedPanel.getSelectedTab() == 0) {
						navigate(false);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()-1);
						page = tabIndexToPage.get(tabbedPanel.getSelectedTab());
						parentPage.updateCurrentChildPage(target, page);
						target.add(tabbedPanel);
					}
				}
			}
		};
		prevButton.setOutputMarkupId(true);
		form.add(prevButton);
		
		AjaxButton nextButton = new AjaxButton("nextButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PageInfoPanelFactory page = tabIndexToPage.get(tabbedPanel.getSelectedTab());
				if (page.save()) {
					if (tabbedPanel.getSelectedTab() == tabbedPanel.getTabs().size()-1) {
						navigate(true);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()+1);
						page = tabIndexToPage.get(tabbedPanel.getSelectedTab());
						parentPage.updateCurrentChildPage(target, page);
						target.add(tabbedPanel);
					}
				}
			}
		};
		nextButton.setOutputMarkupId(true);
		form.add(nextButton);
	}
	
	private void navigate(boolean goToNext) {
		if (goToNext) {
			setResponsePage(navigator.next(getWebPage()));
		} else {
			setResponsePage(navigator.prev(getWebPage()));
		}
	}
}
