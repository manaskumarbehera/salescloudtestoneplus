package dk.jyskit.salescloud.application.pages.productselection;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Alert;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAll;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfiguratorTabPanel extends Panel {
	@Inject
	private PageNavigator navigator;
	private Map<ProductGroup,ValueMap> groupToValueMap;
	
	public ConfiguratorTabPanel(String id, List<Long> productGroupIds) {
		super(id);
		
		final Form<Contract> form = new Form<>("form");
		add(form);
		
		MobileContract contract = (MobileContract) CoreSession.get().getContract();
		add(new Alert("count", Model.of("Du har ialt valgt " + contract.getSubscriptions().size() + " nye pakker i løsningen")));
		
		groupToValueMap = new HashMap<>();
		
		List<ITab> tabs = new ArrayList();
		final Map<Integer, Long> tabIndexToEntityId = new HashMap<Integer, Long>();
		int index = 0;
		
		ProductGroup addonGroup;
		if (CoreSession.get().getBusinessArea().hasFeature(FeatureType.TEM5_PRODUCTS)) {
			addonGroup = CoreSession.get().getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON.getKey());
		} else {
			addonGroup = CoreSession.get().getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_ADDON.getKey());
		}
		
		for (final ProductGroup productGroup : addonGroup.getChildProductGroups()) {
			final ValueMap valueMap = new ValueMap();
			groupToValueMap.put(productGroup, valueMap);
			
			for (Product product: productGroup.getProducts()) {
				valueMap.put(product.getPublicName(), new ProductCountOrAll(product, contract.getCountExistingForProduct(product), contract.getCountNewForProduct(product)));
			}
			
			tabs.add(new AbstractTab(new Model<String>(productGroup.getName())) {
				public Panel getPanel(String panelId) {
					return new FormPanel(panelId, productGroup, valueMap);
				}
			});
			tabIndexToEntityId.put(index++, productGroup.getId());
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
						save();
						setSelectedTab(index);
						((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(getSelectedTab()));
						if (target != null) {
							target.add(form);
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
				save();
				if (tabbedPanel.getSelectedTab() == 0) {
					navigate(false);
				} else {
					tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()-1);
					((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
					target.add(tabbedPanel);
				}
			}
		};
		prevButton.setOutputMarkupId(true);
		form.add(prevButton);
		
		AjaxButton nextButton = new AjaxButton("nextButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				save();
				if (tabbedPanel.getSelectedTab() == tabbedPanel.size()-1) {
					navigate(true);
				} else {
					tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()+1);
					((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
					target.add(tabbedPanel);
				}
			}
		};
		nextButton.setOutputMarkupId(true);
		form.add(nextButton);
	}
	
	private void save() {
		for (ProductGroup productGroup : groupToValueMap.keySet()) {
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			ValueMap valueMap = groupToValueMap.get(productGroup);
			for (String productName : valueMap.keySet()) {
				for(Product product : productGroup.getProducts()) {
					if (productName.equals(product.getPublicName())) {
						ProductCountOrAll productRow = (ProductCountOrAll) valueMap.get(productName);
						CountAndInstallation countAndInstallation = new CountAndInstallation();
						countAndInstallation.setCountNew(productRow.getCountNew());
						countAndInstallation.setCountExisting(productRow.getCountExisting());
						List<CountAndInstallation> countAndInstallations = new ArrayList<>();
						countAndInstallations.add(countAndInstallation);
						productToCountMap.put(product, countAndInstallations);
						break;
					}
				}
			}
			((MobileContract) CoreSession.get().getContract()).adjustOrderLinesForProducts(productGroup, productToCountMap, null);
		}
	}
	
	private void navigate(boolean goToNext) {
		if (goToNext) {
			setResponsePage(navigator.next(getWebPage()));
		} else {
			setResponsePage(navigator.prev(getWebPage()));
		}
	}
}
