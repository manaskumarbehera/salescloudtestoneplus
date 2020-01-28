package dk.jyskit.salescloud.application.pages.mixbundles;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.pages.bundles.BundleSelectionPanel;

public class MixBundlesPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private MixBundleEditorPanel mixBundleEditorPanel;
	private BundleSelectionPanel bundleSelectionPanel;

	public MixBundlesPanel(String id, final NotificationPanel notificationPanel, IModel<MobileProductBundle> selectedBundleModel, 
			final Map<Long, Product> oldSpeechTimes, final Map<Long, Product> oldDataAmounts) {
		super(id);
		
		mixBundleEditorPanel = new MixBundleEditorPanel("editor", selectedBundleModel, oldSpeechTimes, oldDataAmounts) {
			@Override
			public void onChange(AjaxRequestTarget target) {
				target.add(bundleSelectionPanel);
			}
		};
		mixBundleEditorPanel.setOutputMarkupId(true);
		add(mixBundleEditorPanel);
		
		bundleSelectionPanel = new BundleSelectionPanel("bundleSelection", notificationPanel, false, mixBundleEditorPanel);
		add(bundleSelectionPanel);
	}
}
