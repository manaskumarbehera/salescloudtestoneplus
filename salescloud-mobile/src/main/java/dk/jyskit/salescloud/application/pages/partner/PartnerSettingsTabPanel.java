package dk.jyskit.salescloud.application.pages.partner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;

import static dk.jyskit.salescloud.application.model.MobileProductBundleEnum.HARDWARE_BUNDLE;
import static dk.jyskit.salescloud.application.pages.partner.PartnerSettingsFormPanel.*;

@Slf4j
public class PartnerSettingsTabPanel extends Panel {
	@Inject
	private PageNavigator navigator;

	@Inject
	private MobileContractDao contractDao;
	@Inject
	private ContractSaver contractSaver;
	
	public Tab0Bean tab0 		= new Tab0Bean();
	public ValueMap valueMap1 	= new ValueMap();
	public ValueMap valueMap2 	= new ValueMap();
	public ValueMap valueMap3 	= new ValueMap();

	private AjaxBootstrapTabbedPanel<ITab> tabbedPanel;

	private Form<Contract> form;

	public PartnerSettingsTabPanel(String id) {
		super(id);
		
		form = new Form<>("form");
		add(form);
		
		form.add(new FeedbackPanel("feedback"));
		
		List<ITab> tabs = new ArrayList();
		
		String[] titles = new String[] {"Indstillinger", "Hardware til rate", "Sammenfatning partner"};
//		String[] titles = new String[] {"Indstillinger", "Øvrige installationsydelser", "Hardware til rate", "Sammenfatning partner"};

		for (int i = 0; i < titles.length; i++) {
			final int tabIndex = i;
			tabs.add(new AbstractTab(new Model<String>(titles[i])) {
				public Panel getPanel(String panelId) {
					return new PartnerSettingsFormPanel(panelId, PartnerSettingsTabPanel.this, tabIndex);
				}
			});
		}
		
		tabbedPanel = new AjaxBootstrapTabbedPanel<ITab>("tabs", tabs) {
			/**
			 * {@inheritDoc}
			 */
			@Override
			protected WebMarkupContainer newLink(String linkId, final int index) {
				return new AjaxSubmitLink(linkId, form) {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						if (save()) {
							setSelectedTab(index);
							//	((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(getSelectedTab()));
							if (target != null) {
								target.add(form);
							}
						} else {
							if (target != null) {
								target.add(tabbedPanel);
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
				if (save(																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							)) {
					if (tabbedPanel.getSelectedTab() == 0) {
						navigate(false);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()-1);
//						((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
						target.add(tabbedPanel);
					}
				} else {
					target.add(tabbedPanel);
				}
			}
		};
		prevButton.setOutputMarkupId(true);
		form.add(prevButton);
		
		AjaxButton nextButton = new AjaxButton("nextButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (save()) {
					if (tabbedPanel.getSelectedTab() == tabbedPanel.getTabs().size()-1) {
						navigate(true);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()+1);
//						((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
						target.add(tabbedPanel);
					}
				} else {
					target.add(tabbedPanel);
				}
			}
		};
		nextButton.setOutputMarkupId(true);
		form.add(nextButton);
	}
	
	private boolean save() {
		MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		if (tabbedPanel.getSelectedTab() == TAB_INDSTILLINGER) {
			contract.setSupportPricePrUser(tab0.getSupportPricePrUser() * 100);
			contract.setSupportRecurringFee(tab0.getSupportRecurringFee() * 100);
//			contract.setInstallationFeeDiscount(tab0.getInstallationFeeDiscount() * 100);
//			contract.setOneTimeFeeDiscount(tab0.getOneTimeFeeDiscount() * 100);
//			contract.setRateNonRecurringFee(tab0.isRateAgreement() ? 299 : 0);
			contract.setRateNonRecurringFee(tab0.getRateNonRecurringFee() * 100);
			contract.setRateMonths(tab0.getRateMonths());
			contract.setUpFrontPayment(tab0.isUpFrontPayment() ? 150000 : 0);
			contract.setPbs(tab0.getPayment().toLowerCase().contains("pbs"));
			contract.setSegment(tab0.getSegment());
			contract.setSupportMonths(tab0.getSupportMonths());
		}
		
//		if (tabbedPanel.getSelectedTab() == 1) {
//			Map<Long, Integer> idToPriceMap = MapUtils.stringToLongIntMap(contract.getVariableInstallationFees());
//			Map<Long, String> idToProductNameMap 	= MapUtils.stringToLongStringMap(contract.getVariableProductNames());
//			Map<Product, List<CountAndInstallation>> productToCountsMap = new HashMap<>();
//			ProductGroup productGroup = contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION.getKey());
//			for (String productId : valueMap1.keySet()) {
//				for(Product product : productGroup.getProducts()) {
//					if (productId.equals(product.getProductId())) {
//						Object value = valueMap1.get(productId);
//						CountAndInstallation countAndInstallation = new CountAndInstallation();
//
//						if (product.getPublicName().contains("Diverse")) {
//							ProductNameAndPriceRow productRow = (ProductNameAndPriceRow) valueMap1.get(productId);
//							idToProductNameMap.put(product.getId(), productRow.getName());
//							idToPriceMap.put(product.getId(), productRow.getPrice());
//							countAndInstallation.setCountNew(productRow.getPrice() == 0 ? 0 : 1);
//						} else {
//							if (((MobileProduct) product).isVariableInstallationFee()) {
//								Integer price = (Integer) value;
//								if (product.getInternalName().contains("Rabat")) {
//									idToPriceMap.put(product.getId(), -price);
//								} else {
//									idToPriceMap.put(product.getId(), price);
//								}
//								countAndInstallation.setCountNew(price == 0 ? 0 : 1);
//							} else {
//								countAndInstallation.setCountNew(((Boolean) value) ? 1 : 0);
//							}
//						}
//
//						List<CountAndInstallation> countAndInstallations = new ArrayList<>();
//						countAndInstallations.add(countAndInstallation);
//						productToCountsMap.put(product, countAndInstallations);
//						break;
//					}
//				}
//			}
//			contract.setVariableProductNames(MapUtils.longStringMapToString(idToProductNameMap));
//			contract.setVariableInstallationFees(MapUtils.longIntMapToString(idToPriceMap));
//			contract.adjustOrderLinesForProducts(productGroup, productToCountsMap, null);
//		}
		
		if (tabbedPanel.getSelectedTab() == TAB_HARDWARE_TIL_RATE) {
			Map<Long, Integer> idToPriceMap 		= MapUtils.stringToLongIntMap(contract.getVariableRecurringFees());
			Map<Long, String> idToCategoryMap 		= MapUtils.stringToLongStringMap(contract.getVariableCategories());
			Map<Long, String> idToProductNameMap 	= MapUtils.stringToLongStringMap(contract.getVariableProductNames());
			ProductGroup productGroup = contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE.getKey());
			Map<Product, List<CountAndInstallation>> productToCountsMap = new HashMap<>();
			Map<MobileProductBundle, BundleCount> bundleToCountsMap = new HashMap<>();

			for (String productId : valueMap2.keySet()) {
				for(Product product : productGroup.getProducts()) {
					if (productId.equals(product.getProductId())) {
						ProductPriceAndCountRow productRow = (ProductPriceAndCountRow) valueMap2.get(productId);
						CountAndInstallation countAndInstallation = new CountAndInstallation();
						countAndInstallation.setCountNew(productRow.getCount());
						List<CountAndInstallation> countAndInstallations = new ArrayList<>();
						countAndInstallations.add(countAndInstallation);
						productToCountsMap.put(product, countAndInstallations);
						idToPriceMap.put(product.getId(), productRow.getPrice());
						break;
					}
				}
				for (ProductBundle bundle: contract.getCampaigns().get(0).getProductBundles()) {
					MobileProductBundle b = (MobileProductBundle) bundle;
					if (HARDWARE_BUNDLE.equals(b.getBundleType())) {
						if (contract.getVariableRecurringFees() == null) {
							log.warn("contract.getVariableRecurringFees() is null for contract: " + contract.getId());
						} else {
							if (productId.equals(b.getProductId())) {
								ProductPriceAndCountRow row = (ProductPriceAndCountRow) valueMap2.get(productId);
								BundleCount bundleCount = new BundleCount(b, 0, row.getCount(), 0);
								bundleToCountsMap.put(b, bundleCount);
								idToPriceMap.put(b.getId(), row.getPrice());
								break;
							}
						}
					}
				}
			}

			contract.setVariableRecurringFees(MapUtils.longIntMapToString(idToPriceMap));
			contract.setVariableCategories(MapUtils.longStringMapToString(idToCategoryMap));
			contract.setVariableProductNames(MapUtils.longStringMapToString(idToProductNameMap));
			contract.adjustOrderLinesForProducts(productGroup, productToCountsMap, null);
			contract.adjustOrderLinesForBundles(bundleToCountsMap, HARDWARE_BUNDLE);
		}
		
		if (tabbedPanel.getSelectedTab() == TAB_OPGØRELSE) {
			try {
				contract.setInstallationFeeDiscount(Long.valueOf(valueMap3.getString("installationFeeDiscount")) * 100);
			} catch (Exception e) {
			}
			if (contract.getSegment() != null) {
				String tmNumber = valueMap3.getString("TM-nummer");
				if (!StringUtils.isEmpty(tmNumber) && ((tmNumber.length() != 10) || (!tmNumber.startsWith("90000")))) {
					form.error("Fejl i TM nummer, skal bestå af 10 tal, hvor af de fem første er: 90000");
					return false;
				}
				contract.setTmNumber(valueMap3.getString("TM-nummer"));
				if (contract.getTmNumberDate() == null) {
					contract.setTmNumberDate(new Date());
				}
			} 
		}
//		contractDao.save(contract);
		contractSaver.save(contract);
		return true;
	}
	
	private void navigate(boolean goToNext) {
		if (goToNext) {
			setResponsePage(navigator.next(getWebPage()));
		} else {
			setResponsePage(navigator.prev(getWebPage()));
		}
	}
}
