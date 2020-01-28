package dk.jyskit.salescloud.application.pages.bundles;

import java.util.*;

import com.google.common.collect.Lists;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundleEditorPanel;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundlesPage;
import dk.jyskit.salescloud.application.pages.standardbundles.StandardBundlesPage;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.wicket.utils.WicketUtils;
import org.apache.wicket.util.value.ValueMap;

@Slf4j
public class BundleSelectionPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private PageNavigator navigator;

	@Inject
	private ContractSaver contractSaver;
	
	@Inject
	private ProductBundleDao productBundleDao;
	
	@Inject
	private ProductGroupDao productGroupDao;

	@Inject
	private ProductDao productDao;

	@Inject
	private CanOrderFilter canOrderFilter;

	@Inject
	private Dao<OrderLine> orderLineDao;

	private boolean isStandardBundles;

	private MobileProductBundleForm form;
	private ValueMap addOnsValueMap;
	
	public BundleSelectionPanel(String id, final NotificationPanel notificationPanel, boolean standardBundles, MixBundleEditorPanel mixBundleEditorPanel) {
		super(id);
		
		this.isStandardBundles = standardBundles;

		MobileContract contract = MobileSession.get().getContract();

		List<ProductBundle> bundles = new ArrayList<>();
		if (standardBundles) {
			List<ProductBundle> productBundles;
			if (contract.getBusinessArea().hasFeature(FeatureType.USER_PROFILES)) {
				productBundles = contract.getProductBundles();
				productBundles.forEach(productBundle -> log.info(productBundle.getPublicName()) );

//				if (productBundles.get(0).getProducts().size() == 0) {
//					for (ProductBundle targetBundle: contract.getProductBundles()) {
//						for (ProductBundle sourceBundle :
//								((MobileCampaign) contract.getCampaigns().get(0)).getProductBundles()) {
//							if (Objects.equals(sourceBundle, targetBundle)) {
//								sourceBundle.getProducts().forEach(bundleProductRelation -> {
//									targetBundle.addProductRelation(bundleProductRelation.clone());
//								});
//							}
//						}
//						ProductBundleDao.lookup().save(targetBundle);
//					}
//				}
			} else {
				productBundles = contract.getCampaigns().get(0).getProductBundles();
			}

			for (ProductBundle pb : productBundles) {
				if (!canOrderFilter.accept(pb)) {
					continue;
				}
				MobileProductBundle productBundle = (MobileProductBundle) pb;
				log.info(productBundle.toString() + " - " + productBundle.getProducts().size());
				if (productBundle.getProducts().size() > 0) {
					log.info(productBundle.getProducts().get(0).getProduct().getPublicName() + " - " +
							((MobileProduct) productBundle.getProducts().get(0).getProduct()).isPoolModeBundle() + "/" +
							((MobileProduct) productBundle.getProducts().get(0).getProduct()).isNonPoolModeBundle());
				}

				if (getRelevantBundleType().equals(((MobileProductBundle) productBundle).getBundleType())) {
					if (productBundle.isActive()) {
						if (contract.getBusinessArea().hasFeature(FeatureType.POOLS)) {
							if (contract.isPoolsMode() && productBundle.useInPoolsMode()) {
								bundles.add(productBundle);
							} else if (!contract.isPoolsMode() && productBundle.useInNonPoolsMode()) {
								bundles.add(productBundle);
							} else {
								log.info(productBundle.toString() + " ignored 3 - " + productBundle.useInPoolsMode() + "/" + productBundle.useInNonPoolsMode());
							}
						} else {
							bundles.add(productBundle);
						}
					} else {
						log.info(productBundle.toString() + " ignored 2");
					}
				} else {
					log.info(productBundle.toString() + " ignored 1");
				}
			}
		} else {
			for (ProductBundle productBundle : contract.getProductBundles()) {
//			for (ProductBundle productBundle : contract.getBundles()) {
				bundles.add(productBundle);
			}
		}
		
		ArrayList<BundleCount> bundleCountBundleCountList = new ArrayList<>();
		for (ProductBundle productBundle : bundles) {
			int countNew = 0;
			int countExisting = 0;
			for (OrderLine line : contract.getOrderLines()) {
				if (productBundle.equals(line.getBundle())) {
					countNew = line.getCountNew();
					countExisting = line.getCountExisting();
					break;
				}
			}
			int subIndex = 0;
			bundleCountBundleCountList.add(new BundleCount((MobileProductBundle) productBundle, subIndex, countNew, countExisting));
		}
		
//		for (OrderLine line : contract.getOrderLines()) {
//			if (line.getBundle() != null) {
//				if (line.getBundle().isActive()) {
//					if ((standardBundles == ((MobileProductBundle) line.getBundle()).isStandardBundle())) {
//						bundleCountBundleCountList.add(new BundleCount((MobileProductBundle) line.getBundle(), line.getCount()));
//					}
//				}
//			}
//		}
		Collections.sort(bundleCountBundleCountList, new Comparator<BundleCount>() {
			@Override
			public int compare(BundleCount o1, BundleCount o2) {
				return Long.valueOf(o1.getBundle().getSortIndex()).compareTo(o2.getBundle().getSortIndex());
			}
		});
		setDefaultModel(new Model(bundleCountBundleCountList));

		setOutputMarkupId(true);

		addOnsValueMap = new ValueMap();

		form = new MobileProductBundleForm("form", (IModel<ArrayList<BundleCount>>) getDefaultModel(), isStandardBundles, mixBundleEditorPanel, this, addOnsValueMap);
		add(form);

		for (String buttonId: new String[] {"prevButton", "prevButtonTop"}) {
			AjaxButton prevButton = new AjaxButton(buttonId, form) {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.add(notificationPanel);
				}
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					saveAndNavigate(target, true, false, false, false);
				}
			};
			prevButton.setDefaultFormProcessing(true).setOutputMarkupId(true);
			if (BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) {
				if (buttonId.equals("prevButtonTop")) {
					prevButton.setVisible(false);
				}
			}
			form.add(prevButton);
		}

		String[] positions = new String[] {"nextButton", "nextButtonTop"};
		for (String buttonId: positions) {
			AjaxButton nextButton = new AjaxButton(buttonId, form) {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.add(notificationPanel);
				}
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					saveAndNavigate(target, false, true, false, false);
				}
			};
			nextButton.setDefaultFormProcessing(true).setOutputMarkupId(true);
			if (BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) {
				if (buttonId.equals("nextButtonTop")) {
					nextButton.setVisible(false);
				}
			}
			form.add(nextButton);
		}

		for (String buttonId: new String[] {"otherBundlesButton", "otherBundlesButtonTop"}) {
			AjaxButton otherBundlesButton = new AjaxButton(buttonId, form) {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.add(notificationPanel);
				}
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					saveAndNavigate(target, false, false, true, false);
				}
			};
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.MOBILE_VOICE) {
				otherBundlesButton.add(new Label("label", (isStandardBundles) ? "TDC Erhverv Mobil Mix" : "TDC Erhverv Mobil Pakker"));
				otherBundlesButton.setDefaultFormProcessing(true).setOutputMarkupId(true);
			} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.SWITCHBOARD) {
				otherBundlesButton.add(new Label("label", (isStandardBundles) ? "TDC MobilMix" : "TDC Mobilpakker"));
				otherBundlesButton.setDefaultFormProcessing(true).setOutputMarkupId(true);
//			} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
//				otherBundlesButton.add(new Label("label", "TDC Office pakker"));
//				otherBundlesButton.setDefaultFormProcessing(true).setOutputMarkupId(true);
			} else {
				otherBundlesButton.setVisible(false);
			}
			if (isStandardBundles && 
					(MobileSession.get().getContract().getCampaigns().size() > 0) && 
					!((MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0)).isAllowMixBundles()) {
				otherBundlesButton.setEnabled(false);
			}
			form.add(otherBundlesButton);
		}
	}

	private MobileProductBundleEnum getRelevantBundleType() {
		BusinessArea businessArea = MobileSession.get().getBusinessArea();
		if (businessArea.getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
			return MobileProductBundleEnum.OFFICE_BUNDLE;
		}
		return MobileProductBundleEnum.MOBILE_BUNDLE;
	}

	public void saveAndNavigate(AjaxRequestTarget target, boolean goToPrev, boolean goToNext, boolean goToOtherBundlePage, boolean switchMixBundle) {
		ArrayList<BundleCount> bundleCountList = (ArrayList<BundleCount>) getDefaultModelObject();
		Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();
		for (BundleCount bundleCount : bundleCountList) {
			bundleToCountMap.put(bundleCount.bundle, bundleCount);
		}
		MobileContract contract = (MobileContract) CoreSession.get().getContract();

		if (contract.adjustSubscriptions(bundleToCountMap, isStandardBundles)) {
			contract.adjustOrderLinesForBundles(bundleToCountMap, getRelevantBundleType());
//			contract.adjustOrderLinesForBundles(bundleToCountMap, isStandardBundles, getRelevantBundleType());
			for (OrderLine orderLine : contract.getOrderLines()) {
				orderLineDao.save(orderLine);
			}
			contractSaver.save(contract);
			
			if (goToOtherBundlePage) {
				if (isStandardBundles) {
					setResponsePage(MixBundlesPage.class);
				} else {
					setResponsePage(StandardBundlesPage.class);
				}
			} else if (goToNext) {
				setResponsePage(navigator.next(getWebPage()));
			} else if (goToPrev) {
				setResponsePage(navigator.prev(getWebPage()));
			} else if (switchMixBundle) {
				setResponsePage(MixBundlesPage.class);
			}

			//		ArrayList<MobileProductCount> bundleCountList = (ArrayList<BundleCount>) getDefaultModelObject();

			BusinessArea businessArea = MobileSession.get().getBusinessArea();
			for (MobileProductGroupEnum groupType : new MobileProductGroupEnum[] {
					MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_FUNCTIONS,
//					MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING,
					MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING_ILD}) {
				ProductGroup group 		= businessArea.getProductGroupByUniqueName(groupType.getKey());
				Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();

				for (String subIndexAndProductId: addOnsValueMap.keySet()) {
					String[] subIndexAndProductIdArr = subIndexAndProductId.split("¤");
					MobileProduct product = (MobileProduct) businessArea.getProductById(Long.valueOf(subIndexAndProductIdArr[1]));
					if (product == null) {
						form.error("Ukendt produkt");
					} else {
						if (Objects.equals(product.getProductGroup(), group)) {
							List<CountAndInstallation> countList = productToCountMap.get(product);
							if (countList == null) {
								countList = Lists.newArrayList();
								productToCountMap.put(product, countList);
							}
							ProductCountAndInstallation productRow = (ProductCountAndInstallation) addOnsValueMap.get(subIndexAndProductId);
							CountAndInstallation countAndInstallation = new CountAndInstallation();
							countAndInstallation.setCountNew(productRow.getCountNew());
							countAndInstallation.setCountExisting(productRow.getCountExisting());
							countAndInstallation.setSubIndex(Integer.valueOf(subIndexAndProductIdArr[0]));
							countList.add(countAndInstallation);
						}
					}
				}
				contract.adjustOrderLinesForProducts(group, productToCountMap, null);
			}

			{
				ProductGroup group = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_SERVICE.getKey());
				Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
				if (contract.getServiceLevelEntityId() != null) {
					Product product = productDao.findById(contract.getServiceLevelEntityId());
					((MobileProduct) product).addToProductToCountsMap(productToCountMap, contract.getNoOfUsers(false), 0, null);
				}
				contract.adjustOrderLinesForProducts(group, productToCountMap, null);
			}
		} else {
			form.error("Der er konfigureret flere licenser end du prøver at vælge. Nulstil først konfiguration af uønskede licenser.");
			target.add(WicketUtils.findOnPage(getPage(), NotificationPanel.class));
		}

		contract.adjustOrderLinesForRemoteInstallation();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		WicketUtils.renderCssByFileName(response, BundleSelectionPanel.class, "bundles.css");
		WicketUtils.renderJQueryOnDomReady(response, "$('.bundle-product-primary-container').each(function() { if ($(this).children().length == 0) { $(this).parent().addClass('empty'); } });");
	}

}

// $('div.bundle').each(function() {if (!$(this).find('.bundle-product-primary')) {$(this).children('bundle-header').addClass('empty');}});