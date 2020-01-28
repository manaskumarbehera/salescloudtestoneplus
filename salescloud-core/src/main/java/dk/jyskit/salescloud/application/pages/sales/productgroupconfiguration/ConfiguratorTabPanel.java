package dk.jyskit.salescloud.application.pages.sales.productgroupconfiguration;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.ProductGroup;

public class ConfiguratorTabPanel extends Panel {
	public ConfiguratorTabPanel(String id) {
		super(id);
		
		List<ITab> tabs = new ArrayList();
		for (final ProductGroup childProductGroup : CoreSession.get().getProductGroup().getChildProductGroups()) {
			tabs.add(new AbstractTab(new Model<String>(childProductGroup.getName())) {
				public Panel getPanel(String panelId) {
					return new FormPanel(panelId, childProductGroup);
				}
			});
		}

		BootstrapTabbedPanel<ITab> tabbedPanel = new BootstrapTabbedPanel<>("tabs", tabs);
		add(tabbedPanel);
	}
}
