package dk.jyskit.salescloud.application.pages.standardbundles;

import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import dk.jyskit.salescloud.application.pages.bundles.BundleSelectionPanel;

public class StandardBundlesPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public StandardBundlesPanel(String id, final NotificationPanel notificationPanel) {
		super(id);
		
		add(new BundleSelectionPanel("bundleSelection", notificationPanel, true, null));
	}
}
