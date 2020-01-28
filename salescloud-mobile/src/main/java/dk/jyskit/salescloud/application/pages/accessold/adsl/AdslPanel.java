package dk.jyskit.salescloud.application.pages.accessold.adsl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.accessnew.basiclocation.BasicLocationPanel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.value.ValueMap;

@Slf4j
public class AdslPanel extends BasicLocationPanel {
	private static final String KEY_SPEED 		= "sp";

	@Inject
	private OrderLineDao orderLineDao;
	@Inject
	private ContractSaver contractSaver;

	public AdslPanel(String id, Integer tabIndex) {
		super(id, tabIndex);
	}

	@Override
	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
		super.initBundle(contract, values, labelMap, bi);

		contract.getXdslBundle(bi);  // Make sure index exists
		List<XdslBundleData> xdslBundles = contract.getXdslBundles();

		// Init stuff here

		contract.setXdslBundles(xdslBundles);

		List<LocationBundleData> locationBundles = contract.getLocationBundles();

		LocationBundleData locationBundleData	= locationBundles.get(bi);
	}

	protected String getGroupName(MobileContract contract, int bi) {
		if (contract.getBusinessArea().isOnePlus()) {
			return null;
		} else {
			return "xDSL pakke " + (bi+1);
		}
	}

	@Override
	protected void addComponents(final MobileContract contract, Map<String, String> labelMap,
								 ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		super.addComponents(contract, labelMap, formContainer, componentWrappers, bi);

		final LocationBundleData locationBundle = contract.getLocationBundle(bi);
		final XdslBundleData xdslBundle 		= contract.getXdslBundle(bi);

//		{
//			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
//					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
//							xdslBundle.getSpeedEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED,
//							KEY_SPEED + bi, formContainer, "Hastighed", componentWrappers, null,
//							new LocationAccessComponentWrapper.UpdateListener() {
//								@Override
//								public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
//									component.getParent().getParent()
//											.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
//													&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
//													&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
//								}
//							}, null);
//			componentWrappers.add(wrapper);
//		}

//		xdslBundle.setDeviceGroup(null);
//		BusinessArea businessArea = contract.getBusinessArea();
//
//		final Map<MobileProductGroupEnum, DeviceGroupData> deviceGroupData = new HashMap<>();
//		for (MobileProductGroupEnum deviceGroup: new MobileProductGroupEnum[] {MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES}) {
//			for (Product product : getProductGroup(businessArea, deviceGroup).getProducts()) {
//				if (xdslBundle.hasProduct(product)) {
//					xdslBundle.setDeviceGroup(deviceGroup);
//					break;
//				}
//			}
//			deviceGroupData.put(deviceGroup, new DeviceGroupData());
//		}
//
//		for (final MobileProductGroupEnum deviceGroup: new MobileProductGroupEnum[] {MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES}) {
//			final String deviceGroupKey = getFormKey(deviceGroup, bi);
//			labelMap.put(deviceGroupKey, deviceGroup.getDisplayText());
//			values.put(deviceGroupKey, Boolean.valueOf(deviceGroup.equals(xdslBundle.getDeviceGroup())));
//
//			AjaxEventListener listener = new AjaxEventListener() {
//				@Override
//				public void onAjaxEvent(AjaxRequestTarget target) {
//					DeviceGroupData dgd = deviceGroupData.get(deviceGroup);
//					if ((Boolean) values.get(deviceGroupKey)) {
//						for(Component parent : dgd.productParents) {
//							parent.setVisible(true);
//							target.add(parent);
//						}
//						for (Product product : getProductGroup(businessArea, deviceGroup).getProducts()) {
//							if (((MobileProduct) product).hasFlag("default_on")) {
//								values.put(getDeviceProductKey(bi, deviceGroup, product), Boolean.valueOf(true));
//							}
//						}
//
//						for(String otherKey : dgd.otherKeys) {
//							values.put(otherKey, Boolean.valueOf(false));
//						}
//						for(Component otherMainCheckbox : dgd.otherMainCheckboxes) {
//							target.add(otherMainCheckbox.getParent().getParent());
//						}
//						for(Component parent : dgd.otherProductParents) {
//							parent.setVisible(false);
//							target.add(parent);
//						}
//					} else {
//						values.put(deviceGroupKey, Boolean.valueOf(true));
//						target.add(dgd.mainCheckbox.getParent().getParent());
//					}
//				}
//			};
//			DeviceGroupData dgd = deviceGroupData.get(deviceGroup);
//			dgd.mainCheckbox = formContainer.addCheckBox(deviceGroupKey, listener);
//			for (MobileProductGroupEnum g : deviceGroupData.keySet()) {
//				if (!deviceGroup.equals(g)) {
//					DeviceGroupData otherData = deviceGroupData.get(g);
//					otherData.otherMainCheckboxes.add(dgd.mainCheckbox);
//					otherData.otherKeys.add(deviceGroupKey);
//				}
//			}
//
//			for (Product product : getProductGroup(businessArea, deviceGroup).getProducts()) {
//				String key = getDeviceProductKey(bi, deviceGroup, product);
//				if (contract.getXdslBundles().get(bi).getProductEntityIds().contains(product.getId())) {
//					values.put(key, Boolean.TRUE);
//				} else {
//					values.put(key, Boolean.FALSE);
//				}
//				labelMap.put(key, product.getPublicName());
//				CheckBox productCheckbox = formContainer.addCheckBox(key, "style=margin-left:0px");
//				if (((MobileProduct) product).hasFlag("read_only")) {
//					productCheckbox.setEnabled(false);
//				}
//				for (MobileProductGroupEnum g : deviceGroupData.keySet()) {
//					MarkupContainer parent = productCheckbox.getParent().getParent();
//					parent.setOutputMarkupId(true);
//					parent.setOutputMarkupPlaceholderTag(true);
//					parent.setVisible(deviceGroup.equals(xdslBundle.getDeviceGroup()));
//					if (deviceGroup.equals(g)) {
//						deviceGroupData.get(g).productKeys.add(key);
//						deviceGroupData.get(g).productParents.add(parent);
//					} else {
//						deviceGroupData.get(g).otherKeys.add(key);
//						deviceGroupData.get(g).otherProductParents.add(parent);
//					}
//				}
//			}
//		}
	}

	private String getFormKey(final MobileProductGroupEnum deviceGroup, int bundleIndex) {
		return deviceGroup.getKey().replace('.', '_') + '_' + bundleIndex;
	}

	private String getDeviceProductKey(int bundleIndex, MobileProductGroupEnum deviceGroup, Product product) {
		return getFormKey(deviceGroup, bundleIndex) + "_" + product.getProductId();
	}
	
	public MobileProductGroup getProductGroup(BusinessArea businessArea, MobileProductGroupEnum groupValue) {
		for (ProductGroup productGroup : businessArea.getProductGroups()) {
			if (productGroup.getUniqueName().equals(groupValue.getKey())) {
				return (MobileProductGroup) productGroup;
			}
			for (ProductGroup pg : productGroup.getAll()) {
				if (pg.getUniqueName().equals(groupValue.getKey())) {
					return (MobileProductGroup) pg;
				}
			}
		}
		return null;
	}

	@Override
	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page,
								int tabIndex, AjaxRequestTarget target) {
		if (!super.saveAndNavigate(contract, page, tabIndex, target)) {
			return false;
		}

		// Transfer values to contract
		List<XdslBundleData> adslBundles = contract.getXdslBundles();
		for (int i = 0; i < contract.getXdslBundles().size(); i++) {
			XdslBundleData bundle = adslBundles.get(i);
			bundle.setSpeedEntityId((Long) values.get("speed" + i));
			
//			bundle.setProductEntityIds(new ArrayList<Long>());
//			for (MobileProductGroupEnum deviceGroup: new MobileProductGroupEnum[] {MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES}) {
//				for (Product product : getProductGroup(contract.getBusinessArea(), deviceGroup).getProducts()) {
//					String key = getDeviceProductKey(i, deviceGroup, product);
//					Boolean value = (Boolean) values.get(key);
//					if (value != null && value) {
//						bundle.getProductEntityIds().add(product.getId());
//					}
//				}
//			}
			
		}
		contract.setXdslBundles(adslBundles);

		// Adjust order lines
		Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
		Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();
		
		for (int i = 0; i < contract.getXdslBundles().size(); i++) {
			XdslBundleData bundleData = adslBundles.get(i);
			
			if (bundleData.getSpeedEntityId() != null) {
				ProductBundle xdslBundle = null;
				for (ProductBundle bundle : contract.getCampaigns().get(0).getProductBundles()) {
					if (bundle.hasRelationToProduct(bundleData.getSpeedEntityId())) {
						xdslBundle = bundle;
						break;
					}
				}
				if (xdslBundle != null) {
					// XDSL Bundle
//					bundleToCountMap.clear();
					BundleCount bundleCount =  bundleToCountMap.get((MobileProductBundle) xdslBundle);
					if (bundleCount == null) {
						int subIndex = 0;
						bundleCount = new BundleCount((MobileProductBundle) xdslBundle, subIndex, 1, 0);
						bundleToCountMap.put((MobileProductBundle) xdslBundle, bundleCount);
					} else {
						bundleCount.setCountNew(bundleCount.getCountNew() + 1);
					}
					
					// Speed
					// addProduct(productToCountMap, productDao.findById(bundleIds.getSpeedEntityId()));
					
//					// Services
//					for (MobileProductGroupEnum deviceGroup: new MobileProductGroupEnum[] {
//							MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES,
//							MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES}) {
//						for(Product product : getProductGroup(contract.getBusinessArea(), deviceGroup).getProducts()) {
//							if (bundleData.getProductEntityIds().contains(product.getId())) {
//								addProduct(productToCountMap, product);
//							}
//						}
//					}
					
//					// Always included
//					for (Product product : getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED).getProducts()) {
//						addProduct(productToCountMap, product);
//					}
				}
			}
		}
		
		contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.XDSL_BUNDLE);
		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED), productToCountMap, null);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES), productToCountMap);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES), productToCountMap);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED), productToCountMap);
		
		for (OrderLine orderLine : contract.getOrderLines()) {
			orderLineDao.save(orderLine);
		}
		
		contractSaver.save(contract);

		if (page != null) {
			setResponsePage(page);
		}
		return true;
	}

	private void addProduct(Map<Product, List<CountAndInstallation>> productToCountMap, Product product) {
		List<CountAndInstallation> countAndInstallations = productToCountMap.get(product);
		if (countAndInstallations == null) {
			countAndInstallations = new ArrayList<>();
			CountAndInstallation countAndInstallation = new CountAndInstallation();
			countAndInstallation.setCountNew(1);
			countAndInstallations.add(countAndInstallation);
			productToCountMap.put(product, countAndInstallations);
		} else {
			countAndInstallations.get(0).setCountNew(countAndInstallations.get(0).getCountNew() + 1);
		}
	}
	
	class DeviceGroupData implements Serializable {
		public CheckBox mainCheckbox;
		public List<String> productKeys = Lists.newArrayList();
		public List<Component> productParents = Lists.newArrayList();
		public List<String> otherKeys = Lists.newArrayList();
		public List<CheckBox> otherMainCheckboxes = Lists.newArrayList();
		public List<Component> otherProductParents = Lists.newArrayList();
	}
}
