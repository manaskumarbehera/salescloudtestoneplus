package dk.jyskit.salescloud.application.pages.accessnew.fiber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.accessnew.basiclocation.BasicLocationPanel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.UpdateLevel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.VisibleAndEnabled;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.NumberTextField;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.utils.ExceptionUtils;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.value.ValueMap;

import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper.makeKey;

@Slf4j
public class FiberErhvervPlusPanel extends BasicLocationPanel {
	private static final String KEY_PRODUCT 				= "pr";
	private static final String KEY_FIBER_SPEED 			= "fs";
	private static final String KEY_IN_SOLUTION 			= "is";
	private static final String KEY_TDC_ROUTER 				= "tr";
	private static final String KEY_FIREWALL 				= "fw";
	private static final String KEY_ACCESSPOINT				= "ap";
	private static final String KEY_ACCESSPOINT_PS			= "aq";
	private static final String KEY_WIFI 					= "wf";
	private static final String KEY_SUPERVISION 			= "su";
	private static final String KEY_IP 						= "ip";
	private static final String KEY_FLOORS 					= "fl";
	private static final String KEY_ZONEOPRETTELSE 			= "zo";
	private static final String KEY_ZONEDRIFT 				= "zd";
	private static final String KEY_LEVERANCEKOORDINERING	= "lk";
	private static final String KEY_CAMPAIGN 				= "ca";
	private static final String KEY_GUEST_NETWORK   		= "gn";

	@Inject
	private PageNavigator navigator;
	@Inject
	private OrderLineDao orderLineDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private ContractSaver contractSaver;

	private BootstrapSelectSingle fiberSpeedDropDownChoice;
	private DropDownChoice inSolutionDropDownChoice;
	private DropDownChoice tdcRouterDropDownChoice;
	private BootstrapSelectSingle firewallDropDownChoice;
	private NumberTextField accesspointField;
	private NumberTextField accesspointPsuField;
	private NumberTextField floorsField;
	private DropDownChoice wiFiDropDownChoice;
	private DropDownChoice guestNetworkDropDownChoice;
	private DropDownChoice supervisionDropDownChoice;
	private BootstrapSelectSingle ipAddressDropDownChoice;
	private BootstrapSelectSingle zoneOprettelseDropDownChoice;
	private BootstrapSelectSingle zoneDriftDropDownChoice;
	private DropDownChoice coordinationDropDownChoice;
	private String prevCampaign;

	public FiberErhvervPlusPanel(String id, Integer tabIndex) {
		super(id, tabIndex);
	}

	@Override
	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
		super.initBundle(contract, values, labelMap, bi);

		contract.getFiberErhvervPlusBundle(bi);  // Make sure index exists
		List<FiberErhvervPlusBundleData> fiberPlusBundles = contract.getFiberErhvervPlusBundles();

		// Init stuff here

		contract.setFiberErhvervPlusBundles(fiberPlusBundles);

		List<LocationBundleData> locationBundles = contract.getLocationBundles();

		LocationBundleData locationBundleData 			= locationBundles.get(bi);
		FiberErhvervPlusBundleData fiberErhvervPlusBundleData 	= fiberPlusBundles.get(bi);

		if (StringUtils.isEmpty(locationBundleData.getAddress()) && !StringUtils.isEmpty(fiberPlusBundles.get(bi).getAddress())) {
			log.info("Transfer address from fiber bundle to location bundle");
			locationBundleData.setAddressRoad(fiberErhvervPlusBundleData.getAddressRoad());
			locationBundleData.setAddressCity(fiberErhvervPlusBundleData.getAddressCity());
			locationBundleData.setAddressZipCode(fiberErhvervPlusBundleData.getAddressZipCode());
			contract.setLocationBundles(locationBundles);
		}
	}

	@Override
	protected void addComponents(final MobileContract contract,
								 Map<String, String> labelMap, ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		final LocationBundleData locationBundle 		= contract.getLocationBundle(bi);
		final FiberErhvervPlusBundleData fiberBundle	= contract.getFiberErhvervPlusBundle(bi);

//		FormGroup<FiberPanelValues> group = formContainer.createGroup(Model.of("Lokation " + (bi + 1)));
//
//		{
//			addTextField(contract, values, labelMap, fiberBundle.getAddressRoad(), KEY_ADDRESS_ROAD , bi, formContainer,
//					"Adresse", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
//						@Override
//						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
//						}
//					});
//		}
//
//		{
//			addTextField(contract, values, labelMap, fiberBundle.getAddressZipCode(), KEY_ADDRESS_ZIPCODE + bi,
//					group, "Postnr.", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
//						@Override
//						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
//						}
//					});
//		}
//
//		{
//			addTextField(contract, values, labelMap, fiberBundle.getAddressCity(), KEY_ADDRESS_CITY , bi, formContainer,
//					"By", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
//						@Override
//						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
//						}
//					});
//		}

		{
			String productName = "Fiber plus";
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, productName, Lists.newArrayList(productName),
							KEY_PRODUCT , bi, formContainer, "Produkt", componentWrappers);
			componentWrappers.add(wrapper);
			DropDownChoice field = wrapper.getComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent()
							.setVisible(!StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_CITY, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getCampaign(), FiberErhvervPlusBundleData.CAMPAIGN_STANDARD),
					Lists.newArrayList(FiberErhvervPlusBundleData.CAMPAIGN_STANDARD, FiberErhvervPlusBundleData.CAMPAIGN_PRICE_A,
							FiberErhvervPlusBundleData.CAMPAIGN_PRICE_B),
					KEY_CAMPAIGN , bi, formContainer, "Kampagne", componentWrappers);
			componentWrappers.add(wrapper);
			DropDownChoice field = wrapper.getComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent()
							.setVisible(!StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_CITY, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getFiberSpeedEntityId(), getProductGroupBasedOnCampaign(fiberBundle.getCampaign()),
					KEY_FIBER_SPEED , bi, formContainer, "Hastighed", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			fiberSpeedDropDownChoice = wrapper.getFormComponent();
			sortOptions(fiberSpeedDropDownChoice.getChoices());
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent()
							.setVisible(!StringUtils.isEmpty((String) values.get(makeKey(KEY_CAMPAIGN, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)))
									&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_CITY, bi))));
					if (!prevCampaign.equals(values.get(makeKey(KEY_CAMPAIGN, bi)))) {
						prevCampaign = (String) values.get(makeKey(KEY_CAMPAIGN, bi));
						fiberSpeedDropDownChoice.getChoices().clear();
						MobileProductGroupEnum productGroup = getProductGroupBasedOnCampaign(prevCampaign);
						BusinessArea businessArea = contract.getBusinessArea();
						MobileProductGroup pg = getProductGroup(businessArea, productGroup);
						List<Product> products = pg.getProducts();
						for (Product product : products) {
							fiberSpeedDropDownChoice.getChoices().add(product.getId());
						}
						sortOptions(fiberSpeedDropDownChoice.getChoices());
						if (target != null) {
							target.add(fiberSpeedDropDownChoice);
						}
						values.put(LocationAccessComponentWrapper.makeKey(KEY_FIBER_SPEED, wrapper.getIndex()), null);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getInSolution()), Lists.newArrayList(YES, NO),
					KEY_IN_SOLUTION , bi, formContainer, "Skal indgå i løsning (Scale/One)", componentWrappers);
			componentWrappers.add(wrapper);
			inSolutionDropDownChoice = wrapper.getFormComponent();
			inSolutionDropDownChoice.setRequired(true);
//			sortOptions(inSolutionDropDownChoice.getChoices());
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getTdcRouter()), Lists.newArrayList(YES, NO), KEY_TDC_ROUTER, bi,
							formContainer, "TDC udstyr", componentWrappers);
			componentWrappers.add(wrapper);
			tdcRouterDropDownChoice = wrapper.getFormComponent();
			tdcRouterDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					if (YES.equals(values.get(makeKey(KEY_IN_SOLUTION, bi)))) {
						wrapper.getComponent().setEnabled(false);
						values.put(LocationAccessComponentWrapper.makeKey(KEY_TDC_ROUTER, wrapper.getIndex()), YES);
					} else {
						wrapper.getComponent().setEnabled(true);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			MobileProductGroupEnum mobileProductGroup = MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS;

			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getFirewallEntityId(), mobileProductGroup,
					KEY_FIREWALL , bi, formContainer, "Firewall", componentWrappers, "Nej", null);
			componentWrappers.add(wrapper);
			firewallDropDownChoice = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					if (YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi)))) {
						wrapper.getComponent().setEnabled(true);
					} else {
						wrapper.getComponent().setEnabled(false);
						values.put(LocationAccessComponentWrapper.makeKey(KEY_FIREWALL, wrapper.getIndex()), NO);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getWiFi()), Lists.newArrayList(YES, NO), KEY_WIFI, bi, formContainer,
					"Wi-Fi", componentWrappers);
			componentWrappers.add(wrapper);
			wiFiDropDownChoice = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					if (!YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi)))) {
						values.put(makeKey(KEY_WIFI, wrapper.getIndex()), NO);
					}
					if (NO.equals(values.get(makeKey(KEY_FIREWALL, bi)))) {
						values.put(makeKey(KEY_WIFI, wrapper.getIndex()), NO);
					}

					Long keyFiberEntityId = (Long) values.get(makeKey(KEY_FIBER_SPEED, bi));
					wrapper.getComponent().getParent().getParent().setVisible(keyFiberEntityId != null);
//								boolean moreThan100MBit = false;
//								if (keyFiberEntityId != null) {
//									Product product = productDao.findById(keyFiberEntityId);
//									moreThan100MBit = Long.valueOf(product.getProductId()) > 4402005;
//								}
//								if (moreThan100MBit) {
//									values.put(KEY_WIFI , bi, NO);
//								}

//								wiFiDropDownChoice.setEnabled(!moreThan100MBit && YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi))) && !NO.equals(values.get(makeKey(KEY_FIREWALL, bi))));
					wiFiDropDownChoice.setEnabled(YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi))) && !NO.equals(values.get(makeKey(KEY_FIREWALL, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getGuestNetwork()), Lists.newArrayList(YES, NO), KEY_GUEST_NETWORK , bi, formContainer,
					"Gæstenetværk", componentWrappers);
			componentWrappers.add(wrapper);
			guestNetworkDropDownChoice = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					if (!YES.equals(values.get(makeKey(KEY_WIFI, bi)))) {
						values.put(LocationAccessComponentWrapper.makeKey(KEY_GUEST_NETWORK, wrapper.getIndex()), NO);
					}
//								guestNetworkDropDownChoice.setEnabled(YES.equals(values.get(makeKey(KEY_WIFI, bi))));
					wrapper.getComponent().getParent().getParent().setVisible(YES.equals(values.get(makeKey(KEY_WIFI, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<NumberTextField> wrapper = (LocationAccessComponentWrapper<NumberTextField>)
					LocationAccessComponentWrapper.addNumberTextFieldWrapper(values, labelMap,
					fiberBundle.getNoOfAccesspoints(), 0, 100, KEY_ACCESSPOINT , bi, formContainer,
					"Cisco Meraki MR33 accesspunkt", componentWrappers);
			componentWrappers.add(wrapper);
			accesspointField = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					if (!YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi))) || NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_FIREWALL, bi))) || NO.equals(values.get(makeKey(KEY_WIFI, bi)))) {
						values.put(LocationAccessComponentWrapper.makeKey(KEY_ACCESSPOINT, wrapper.getIndex()), Long.valueOf(0));
						wrapper.getComponent().getParent().getParent().setVisible(false);
					} else {
						wrapper.getComponent().getParent().getParent().setVisible(true);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<NumberTextField> wrapper = (LocationAccessComponentWrapper<NumberTextField>)
					LocationAccessComponentWrapper.addNumberTextFieldWrapper(values, labelMap,
					fiberBundle.getNoOfAccesspointPsus(), 0, 100, KEY_ACCESSPOINT_PS , bi, formContainer,
					"Strømforsyning Cisco Meraki MR AP", componentWrappers);
			componentWrappers.add(wrapper);
			accesspointPsuField = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					if ((values.get(makeKey(KEY_ACCESSPOINT, bi)) != null) && (((Long) values.get(makeKey(KEY_ACCESSPOINT, bi))) > 0)) {
						wrapper.getComponent().getParent().getParent().setVisible(true);
					} else {
						values.put(LocationAccessComponentWrapper.makeKey(KEY_ACCESSPOINT_PS, wrapper.getIndex()), Long.valueOf(0));
						wrapper.getComponent().getParent().getParent().setVisible(false);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getSupervisionEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_SUPERVISION, KEY_SUPERVISION , bi, formContainer,
					"Overvågning", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			BootstrapSelectSingle dropDownChoice = wrapper.getFormComponent();
			dropDownChoice.setChoiceRenderer(new IChoiceRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					return ((MobileProduct) productDao.findById((Long) value)).getFlags();  // Walther wants text which is different from product name!
				}

				@Override
				public String getIdValue(Object object, int index) {
					return "" + index;
				}
			});
			dropDownChoice.setNullValid(false);
			dropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					if (YES.equals(values.get(makeKey(KEY_TDC_ROUTER, bi)))) {
						wrapper.getComponent().setEnabled(true);
					} else {
						wrapper.getComponent().setEnabled(false);
//									values.put(makeKey(KEY_SUPERVISION, bi), NO);
						values.put(LocationAccessComponentWrapper.makeKey(KEY_SUPERVISION, wrapper.getIndex()), null);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getIpAddressEntityId(), PRODUCT_GROUP_ACCESS_IP, KEY_IP , bi, formContainer,
					"Tilkøb af IP adresser", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			ipAddressDropDownChoice = wrapper.getFormComponent();
			ipAddressDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					Integer newIpAddressOptionsState = calcIpAddressOptionState(values, bi);
					if (!newIpAddressOptionsState.equals(fiberBundle.getIpAddressOptionState())) {
						values.put(LocationAccessComponentWrapper.makeKey(KEY_IP, wrapper.getIndex()), null); // Optimize?
						fiberBundle.setIpAddressOptionState(newIpAddressOptionsState);
						updateIpAdressOptions(contract, PRODUCT_GROUP_ACCESS_IP,
								ipAddressDropDownChoice, newIpAddressOptionsState);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<NumberTextField> wrapper = (LocationAccessComponentWrapper<NumberTextField>)
					LocationAccessComponentWrapper.addNumberTextFieldWrapper(values, labelMap,
					fiberBundle.getFloors(), 0, 100,
					KEY_FLOORS , bi, formContainer, "Etageskift", componentWrappers);
			componentWrappers.add(wrapper);
			floorsField = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getZoneOprettelseEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_ZONEOPRETTELSE,
					KEY_ZONEOPRETTELSE , bi, formContainer, "Zoneoprettelse", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			zoneOprettelseDropDownChoice = wrapper.getFormComponent();
			zoneOprettelseDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					fiberBundle.getZoneDriftEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_ZONEDRIFT,
					KEY_ZONEDRIFT , bi, formContainer, "Zonedrift", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			zoneDriftDropDownChoice = wrapper.getFormComponent();
			zoneDriftDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberBundle.getCoordination()), Lists.newArrayList(YES, NO),
					KEY_LEVERANCEKOORDINERING , bi, formContainer, "Leverancekoordinering", componentWrappers);
			componentWrappers.add(wrapper);
			coordinationDropDownChoice = wrapper.getFormComponent();
			coordinationDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_FIBER_SPEED, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		Integer ipAddressOptionState = calcIpAddressOptionState(values, bi);
		fiberBundle.setIpAddressOptionState(ipAddressOptionState);
		updateIpAdressOptions(contract, PRODUCT_GROUP_ACCESS_IP, ipAddressDropDownChoice,
				ipAddressOptionState);

		prevCampaign = fiberBundle.getCampaign();
	}

/*
	private void addBundleForm(MobileContract contract, FiberPanelValues values, Map<String, String> labelMap, Jsr303Form<FiberPanelValues> form, int i) {
		final int bi = i;
		final FiberErhvervPlusBundleData fiberPlusBundle = contract.getFiberErhvervPlusBundle(i);

		FormGroup<FiberPanelValues> group = form.createGroup(Model.of("Lokation " + (i + 1)));
		final int lastComponentIndex = 18;
		final ComponentWrapper[] components = new ComponentWrapper[lastComponentIndex + 1];
		int componentIndex = 0;

		if (bi == 0) {
			if (StringUtils.isEmpty(contract.getFiberErhvervPlusBundles().get(i).getAddressRoad())) {
				fiberPlusBundle.setAddressRoad(contract.getCustomer().getAddress());
			}
			if (StringUtils.isEmpty(fiberPlusBundle.getAddressZipCode())) {
				fiberPlusBundle.setAddressZipCode(contract.getCustomer().getZipCode());
			}
			if (StringUtils.isEmpty(fiberPlusBundle.getAddressCity())) {
				fiberPlusBundle.setAddressCity(contract.getCustomer().getCity());
			}
		}

		{
			addTextField(contract, values, labelMap, fiberPlusBundle.getAddressRoad(), KEY_ADDRESS_ROAD , bi, formContainer,
					"Adresse", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
						}
					});
		}

		{
			addTextField(contract, values, labelMap, fiberPlusBundle.getAddressZipCode(), KEY_ADDRESS_ZIPCODE + bi,
					group, "Postnr.", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
						}
					});
		}

		{
			addTextField(contract, values, labelMap, fiberPlusBundle.getAddressCity(), KEY_ADDRESS_CITY , bi, formContainer,
					"By", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
						}
					});
		}

		{
			String productName = "Fiber plus";
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, productName, Lists.newArrayList(productName),
					KEY_PRODUCT , bi, formContainer, "Produkt", componentWrappers,
					new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent()
									.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
						}
					});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getCampaign(), FiberErhvervPlusBundleData.CAMPAIGN_STANDARD),
					Lists.newArrayList(FiberErhvervPlusBundleData.CAMPAIGN_STANDARD, FiberErhvervPlusBundleData.CAMPAIGN_PRICE_A,
							FiberErhvervPlusBundleData.CAMPAIGN_PRICE_B),
					KEY_CAMPAIGN , bi, formContainer, "Kampagne", componentWrappers,
					new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent()
									.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
						}
					});
		}

		{
			fiberSpeedDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getFiberSpeedEntityId(), getProductGroupBasedOnCampaign(fiberPlusBundle.getCampaign()),
					KEY_FIBER_SPEED , bi, formContainer, "Hastighed", componentWrappers,
					new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent()
									.setVisible(!StringUtils.isEmpty((String) values.get(KEY_CAMPAIGN + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
							if (!prevCampaign.equals(values.get(KEY_CAMPAIGN + bi))) {
								prevCampaign = (String) values.get(KEY_CAMPAIGN + bi);
								fiberSpeedDropDownChoice.getChoices().clear();
								MobileProductGroupEnum productGroup = getProductGroupBasedOnCampaign(prevCampaign);
								BusinessArea businessArea = contract.getBusinessArea();
								MobileProductGroup pg = getProductGroup(businessArea, productGroup);
								List<Product> products = pg.getProducts();
								for (Product product : products) {
									fiberSpeedDropDownChoice.getChoices().add(product.getId());
								}
								sortOptions(fiberSpeedDropDownChoice.getChoices());
								if (target != null) {
									target.add(fiberSpeedDropDownChoice);
								}
								values.put(KEY_FIBER_SPEED , bi, null);
							}
						}
					});
			sortOptions(fiberSpeedDropDownChoice.getChoices());
		}

		{
			inSolutionDropDownChoice = LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getInSolution()), Lists.newArrayList(YES, NO),
					KEY_IN_SOLUTION , bi, formContainer, "Skal indgå i løsning (Scale/One)", componentIndex++,
					lastComponentIndex, components, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
						}
					});
			inSolutionDropDownChoice.setRequired(true);
		}

		{
			tdcRouterDropDownChoice = LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getTdcRouter()), Lists.newArrayList(YES, NO), KEY_TDC_ROUTER + bi,
					group, "TDC udstyr", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
							if (YES.equals(values.get(KEY_IN_SOLUTION + bi))) {
								component.setEnabled(false);
								values.put(KEY_TDC_ROUTER , bi, YES);
							} else {
								component.setEnabled(true);
							}
						}
					});
			tdcRouterDropDownChoice.setRequired(true);
		}

		{
			MobileProductGroupEnum mobileProductGroup = MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS;
			firewallDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getFirewallEntityId(), mobileProductGroup,
					KEY_FIREWALL , bi, formContainer, "Firewall", componentWrappers,
					new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
							if (YES.equals(values.get(KEY_TDC_ROUTER + bi))) {
								component.setEnabled(true);
							} else {
								component.setEnabled(false);
								values.put(KEY_FIREWALL , bi, NO);
							}
						}
					});
		}

		{
			wiFiDropDownChoice = LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getWiFi()), Lists.newArrayList(YES, NO), KEY_WIFI , bi, formContainer,
					"Wi-Fi", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							if (!YES.equals(values.get(KEY_TDC_ROUTER + bi))) {
								values.put(KEY_WIFI , bi, NO);
							}
							if (NO.equals(values.get(KEY_FIREWALL + bi))) {
								values.put(KEY_WIFI , bi, NO);
							}

							Long keyFiberEntityId = (Long) values.get(KEY_FIBER_SPEED + bi);
							wrapper.getComponent().getParent().getParent().setVisible(keyFiberEntityId != null);
//								boolean moreThan100MBit = false;
//								if (keyFiberEntityId != null) {
//									Product product = productDao.findById(keyFiberEntityId);
//									moreThan100MBit = Long.valueOf(product.getProductId()) > 4402005;
//								}
//								if (moreThan100MBit) {
//									values.put(KEY_WIFI , bi, NO);
//								}

//								wiFiDropDownChoice.setEnabled(!moreThan100MBit && YES.equals(values.get(KEY_TDC_ROUTER + bi)) && !NO.equals(values.get(KEY_FIREWALL + bi)));
							wiFiDropDownChoice.setEnabled(YES.equals(values.get(KEY_TDC_ROUTER + bi)) && !NO.equals(values.get(KEY_FIREWALL + bi)));
						}
					});
		}

		{
			guestNetworkDropDownChoice = LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getGuestNetwork()), Lists.newArrayList(YES, NO), KEY_GUEST_NETWORK , bi, formContainer,
					"Gæstenetværk", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							if (!YES.equals(values.get(KEY_WIFI + bi))) {
								values.put(KEY_GUEST_NETWORK , bi, NO);
							}
//								guestNetworkDropDownChoice.setEnabled(YES.equals(values.get(KEY_WIFI + bi)));
							wrapper.getComponent().getParent().getParent().setVisible(YES.equals(values.get(KEY_WIFI + bi)));
						}
					});

		}

		{
			accesspointField = addNumberTextField(contract, values, labelMap,
					fiberPlusBundle.getNoOfAccesspoints(), 0, 100, KEY_ACCESSPOINT , bi, formContainer,
					"Cisco Meraki MR33 accesspunkt", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							if (!YES.equals(values.get(KEY_TDC_ROUTER + bi)) || NO_CHOICE_ENTITY_ID.equals(values.get(KEY_FIREWALL + bi)) || NO.equals(values.get(KEY_WIFI + bi))) {
								values.put(KEY_ACCESSPOINT , bi, Long.valueOf(0));
								wrapper.getComponent().getParent().getParent().setVisible(false);
							} else {
								wrapper.getComponent().getParent().getParent().setVisible(true);
							}
						}
					});
		}

		{
			accesspointPsuField = addNumberTextField(contract, values, labelMap,
					fiberPlusBundle.getNoOfAccesspointPsus(), 0, 100, KEY_ACCESSPOINT_PS , bi, formContainer,
					"Strømforsyning Cisco Meraki MR AP", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							if ((values.get(KEY_ACCESSPOINT + bi) != null) && (((Long) values.get(KEY_ACCESSPOINT + bi)) > 0)) {
								wrapper.getComponent().getParent().getParent().setVisible(true);
							} else {
								values.put(KEY_ACCESSPOINT_PS , bi, Long.valueOf(0));
								wrapper.getComponent().getParent().getParent().setVisible(false);
							}
						}
					});
		}

		{
			BootstrapSelectSingle dropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getSupervisionEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_SUPERVISION, KEY_SUPERVISION , bi, formContainer,
					"Overvågning", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
							if (YES.equals(values.get(KEY_TDC_ROUTER + bi))) {
								component.setEnabled(true);
							} else {
								component.setEnabled(false);
//									values.put(KEY_SUPERVISION , bi, NO);
								values.put(KEY_SUPERVISION , bi, null);
							}
						}
					});
			dropDownChoice.setChoiceRenderer(new IChoiceRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					return ((MobileProduct) productDao.findById((Long) value)).getFlags();  // Walther wants text which is different from product name!
				}

				@Override
				public String getIdValue(Object object, int index) {
					return "" + index;
				}
			});
			dropDownChoice.setNullValid(false);
			dropDownChoice.setRequired(true);
		}

		{
			ipAddressDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getIpAddressEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_IP, KEY_IP , bi, formContainer,
					"Tilkøb af IP adresser", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
							Integer newIpAddressOptionsState = calcIpAddressOptionState(values, bi);
							if (!newIpAddressOptionsState.equals(fiberPlusBundle.getIpAddressOptionState())) {
								values.put(KEY_IP , bi, null); // Optimize?
								fiberPlusBundle.setIpAddressOptionState(newIpAddressOptionsState);
								updateIpAdressOptions(contract, PRODUCT_GROUP_FIBER_BUNDLE_IP,
										ipAddressDropDownChoice, newIpAddressOptionsState);
							}
						}
					});
			ipAddressDropDownChoice.setRequired(true);
		}

		{
			floorsField = addNumberTextField(contract, values, labelMap, fiberPlusBundle.getFloors(), 0, 100,
					KEY_FLOORS , bi, formContainer, "Etageskift", componentWrappers,
					new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
						}
					});
		}

		{
			zoneOprettelseDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getZoneOprettelseEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_ZONEOPRETTELSE,
					KEY_ZONEOPRETTELSE , bi, formContainer, "Zoneoprettelse", componentIndex++, lastComponentIndex,
					components, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
						}
					});
			zoneOprettelseDropDownChoice.setRequired(true);
		}

		{
			zoneDriftDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberPlusBundle.getZoneDriftEntityId(), PRODUCT_GROUP_FIBER_BUNDLE_ZONEDRIFT, KEY_ZONEDRIFT + bi,
					group, "Zonedrift", componentWrappers, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
						}
					});
			zoneDriftDropDownChoice.setRequired(true);
		}

		{
			coordinationDropDownChoice = LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>) LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(fiberPlusBundle.getCoordination()), Lists.newArrayList(YES, NO),
					KEY_LEVERANCEKOORDINERING , bi, formContainer, "Leverancekoordinering", componentIndex++,
					lastComponentIndex, components, new LocationAccessComponentWrapper.UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							wrapper.getComponent().getParent().getParent().setVisible(values.get(KEY_FIBER_SPEED + bi) != null);
						}
					});
			coordinationDropDownChoice.setRequired(true);
		}

		Integer ipAddressOptionState = calcIpAddressOptionState(values, bi);
		fiberPlusBundle.setIpAddressOptionState(ipAddressOptionState);
		updateIpAdressOptions(contract, PRODUCT_GROUP_FIBER_BUNDLE_IP, ipAddressDropDownChoice,
				ipAddressOptionState);

		prevCampaign = fiberPlusBundle.getCampaign();

		for (ComponentWrapper componentWrapper : components) {
			componentWrapper.update(null);
		}

		AjaxButton button = group.addButton("action.delete", Buttons.Type.Info, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				contract.getFiberErhvervPlusBundles().remove(contract.getFiberErhvervPlusBundles().size() - 1);
				List<FiberErhvervPlusBundleData> bundles = contract.getFiberErhvervPlusBundles();
				bundles.remove(bundles.size() - 1);
				contract.setFiberErhvervPlusBundles(bundles);

				if (contract.getBusinessArea().isOnePlus()) {
					saveAndNavigate(contract, values, LocationsPage.class);
				} else {
					saveAndNavigate(contract, values, FiberPage.class);
				}
			}
		});
		button.add(AttributeModifier.append("style", "margin-bottom: 20px"));
	}
*/

//	private void addButtons(final MobileContract contract, final FiberPanelValues values,
//							Map<String, String> labelMap, Jsr303Form<FiberPanelValues> form) {
//		labelMap.put("action.prev", 	"Tilbage");
//		labelMap.put("action.next", 	"Videre");
//		labelMap.put("action.add", 		"Tilføj lokation");
//		labelMap.put("action.delete", 	"Slet lokation");
//
//		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				saveAndNavigate(contract, values, navigator.prev(getWebPage()));
//			}
//		});
//		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				saveAndNavigate(contract, values, navigator.next(getWebPage()));
//			}
//		});
//
//		AjaxButton addButton = form.addSubmitButton("action.add", Buttons.Type.Info, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				// NOT USED FOR ONE+
//
//				if (contract.getFiberErhvervPlusBundles().size() < 10) {
//					List<FiberErhvervPlusBundleData> bundles = contract.getFiberErhvervPlusBundles();
//					bundles.add(new FiberErhvervPlusBundleData());
//					contract.setFiberErhvervPlusBundles(bundles);
//					saveAndNavigate(contract, values, FiberPage.class);
//				}
//			}
//		});
//	}

	@Override
	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page,
								int tabIndex, AjaxRequestTarget target) {
		if (!super.saveAndNavigate(contract, page, tabIndex, target)) {
			return false;
		}
		
		int bi = tabIndex;

		try {
			// Transfer values to contract
			List<FiberErhvervPlusBundleData> fiberPlusBundles = contract.getFiberErhvervPlusBundles();
//			for (int i = 0; i < contract.getFiberErhvervPlusBundles().size(); i++) {
				FiberErhvervPlusBundleData bundle = fiberPlusBundles.get(bi);
//				bundle.setAddressRoad((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)));
//				bundle.setAddressZipCode((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)));
//				bundle.setAddressCity((String) values.get(makeKey(KEY_ADDRESS_CITY, bi)));
				bundle.setFiberSpeedEntityId((Long) values.get(makeKey(KEY_FIBER_SPEED, bi)));
				bundle.setInSolution(nullYesOrNo(values.get(makeKey(KEY_IN_SOLUTION, bi))));
				bundle.setTdcRouter(nullYesOrNo(values.get(makeKey(KEY_TDC_ROUTER, bi))));
//				bundle.setFirewall(nullYesOrNo(values.get(makeKey(KEY_FIREWALL, bi))));
				try {
					bundle.setFirewallEntityId((Long) values.get(makeKey(KEY_FIREWALL, bi)));
				} catch (Exception e) {
					bundle.setFirewallEntityId(null);
				}
				bundle.setWiFi(nullYesOrNo(values.get(makeKey(KEY_WIFI, bi))));
				bundle.setGuestNetwork(nullYesOrNo(values.get(makeKey(KEY_GUEST_NETWORK, bi))));
				bundle.setIpAddressEntityId((Long) values.get(makeKey(KEY_IP, bi)));
				bundle.setFloors((Long) values.get(makeKey(KEY_FLOORS, bi)));
				bundle.setNoOfAccesspoints((Long) values.get(makeKey(KEY_ACCESSPOINT, bi)));
				bundle.setNoOfAccesspointPsus((Long) values.get(makeKey(KEY_ACCESSPOINT_PS, bi)));
				bundle.setZoneOprettelseEntityId((Long) values.get(makeKey(KEY_ZONEOPRETTELSE, bi)));
				bundle.setZoneDriftEntityId((Long) values.get(makeKey(KEY_ZONEDRIFT, bi)));
				bundle.setSupervisionEntityId((Long) values.get(makeKey(KEY_SUPERVISION, bi)));
				bundle.setCoordination(nullYesOrNo(values.get(makeKey(KEY_LEVERANCEKOORDINERING, bi))));
				bundle.setCampaign((String) values.get(makeKey(KEY_CAMPAIGN, bi)));
				
				// in_solution => 4401942 - pr. lokation??
//			}
			contract.setFiberErhvervPlusBundles(fiberPlusBundles);

			// Adjust order lines
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();
			
			MobileProductGroup parentGroup = getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE);
			
//			for (int bi = 0; bi < contract.getFiberErhvervPlusBundles().size(); bi++) {
				FiberErhvervPlusBundleData fiberPlusBundle = fiberPlusBundles.get(bi);
				
				if (fiberPlusBundle.getFiberSpeedEntityId() != null) {
					ProductBundle innerBundle = null;
					for (ProductBundle productBundle : contract.getCampaigns().get(0).getProductBundles()) {
						if (productBundle.hasRelationToProduct(fiberPlusBundle.getFiberSpeedEntityId())) {
							innerBundle = productBundle;
							break;
						}
					}
					if (innerBundle != null) {
						// Wi-Fi Bundle
						BundleCount bundleCount =  bundleToCountMap.get((MobileProductBundle) innerBundle);
						if (bundleCount == null) {
							bundleCount = new BundleCount((MobileProductBundle) innerBundle, bi, 1, 0);
							bundleToCountMap.put((MobileProductBundle) innerBundle, bundleCount);
						} else {
							bundleCount.setCountNew(bundleCount.getCountNew() + 1);
						}
					}
					
					List<Long> entityIds = fiberPlusBundle.getProductEntityIds();
					
					for (ProductGroup group: parentGroup.getChildProductGroups()) {
						for (Product product : group.getProducts()) {
							if (entityIds.contains(product.getId())) {
								if (("4401718".equals(product.getProductId())) && (fiberPlusBundle.getCampaign() == null || !fiberPlusBundle.getCampaign().equals(FiberErhvervPlusBundleData.CAMPAIGN_STANDARD))) {
									addToProductToCountsMap(productToCountMap, productDao.findByProductGroupAndProductId(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey(), "4401718-kampagne"), bi);
								} else {
									addToProductToCountsMap(productToCountMap, product, bi);
								}
							}
						}
					}
					
					// Some products are handled specially
					{
						// These products are configured to always be included
						for (Product p : productDao.findByBusinessAreaAndProductGroupUniqueName(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey())) {
							if ((p.getMinCount().intValue() == 1) && (p.getMaxCount().intValue() == 1)) {
								addToProductToCountsMap(productToCountMap, p, bi);
							}
						}
					}
					{
						Product p = productDao.findByProductGroupAndProductName(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey(), "Etageskift");
						if (p != null) {
							if (fiberPlusBundle.getFloors() != null) {
								addToProductToCountsMap(productToCountMap, p, fiberPlusBundle.getFloors(), bi);
							}
						}
					}
					{
						if (fiberPlusBundle.getInSolution() != null && fiberPlusBundle.getInSolution()) {
							// Scale channels
							addToProductToCountsMap(
									productToCountMap, 
									productDao.findByProductGroupAndProductId(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey(), "4401942"), 
									18, 
									bi);
						}
					}
					{
						Product p = productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "4401721");
						if (p != null) {
							addToProductToCountsMap(productToCountMap, p, fiberPlusBundle.getWiFi() ? 1 : 0, bi);
						}
					}
					{
						Product p = productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "4401722");
						if (p != null) {
							addToProductToCountsMap(productToCountMap, p, fiberPlusBundle.getGuestNetwork() ? 1 : 0, bi);
						}
					}

//					{
//						if (fiberPlusBundle.getSupervision() != null && fiberPlusBundle.getSupervision()) {
//							// Overvågning
//							addToProductToCountsMap(
//									productToCountMap, 
//									productDao.findByProductGroupAndProductId(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey(), "4401934"), 
//									bi);
//						}
//					}
					
					{
						if (fiberPlusBundle.getCoordination() != null && fiberPlusBundle.getCoordination()) {
							// Leverancekoordinering
							addToProductToCountsMap(
									productToCountMap, 
									productDao.findByProductGroupAndProductId(contract.getBusinessArea().getId(), PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey(), "8299700"), 
									bi);
						}
					}

					int onsiteDatatekniker = 0;
					if (fiberPlusBundle.getNoOfAccesspoints() != null && fiberPlusBundle.getNoOfAccesspoints() > 0) {
						addToProductToCountsMap(productToCountMap, productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "6940024"),
								fiberPlusBundle.getNoOfAccesspoints(), bi);
						if (fiberPlusBundle.getNoOfAccesspoints() > 1) {
							onsiteDatatekniker = 1;
						}
					}
					addToProductToCountsMap(productToCountMap, productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "4401714"),
							onsiteDatatekniker, bi);

					if (fiberPlusBundle.getNoOfAccesspointPsus() != null && fiberPlusBundle.getNoOfAccesspointPsus() > 0) {
						addToProductToCountsMap(productToCountMap, productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "6940061"),
								fiberPlusBundle.getNoOfAccesspointPsus(), bi);
					}

					List<String> productIds = new ArrayList<>();
					if (Boolean.TRUE.equals(fiberPlusBundle.getTdcRouter())) {
						productIds.add("4401182");		// Datatekniker	
						productIds.add("6940004");		// Cisco 1111-8p PoE router
						
						if (NO_CHOICE_ENTITY_ID.equals(fiberPlusBundle.getFirewallEntityId())) {
							productIds.add("4401251");		// 4 IP adresser mulighed for opsalg til 8	
						} else {
							productIds.add("6940015");		// Firewall licens	
						}
					} else {
						// No router
						if (Objects.isNull(fiberPlusBundle.getFirewallEntityId()) && Boolean.FALSE.equals(fiberPlusBundle.getWiFi())) {
							productIds.add("4401251");		// 4 IP adresser mulighed for opsalg til 8
							productIds.add("0439600");		// Mediekonverter
						}
					}
					
					for (String productId : productIds) {
						MobileProductGroup mainGroup = (MobileProductGroup) contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE.getKey());
						for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
							for (Product product : mobileProductGroup.getProducts()) {
								if (productId.equals(product.getProductId())) {
									addToProductToCountsMap(
											productToCountMap,
											product,
											bi);
									break;
								}
							}
						}
					}
				}
//			}
			
			contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.FIBER_BUNDLE);
			for (ProductGroup group: parentGroup.getChildProductGroups()) {
				contract.adjustOrderLinesForProducts(group, productToCountMap, bi);
			}
			
			for (OrderLine orderLine : contract.getOrderLines()) {
				orderLineDao.save(orderLine);
			}
			
			contractSaver.save(contract);

			if (page != null) {
				setResponsePage(page);
			}
		} catch (Exception e) {
			ExceptionUtils.handleException(e);
		}
		return true;
	}

	private MobileProductGroupEnum getProductGroupBasedOnCampaign(final String campaign) {
		MobileProductGroupEnum productGroup;
		if (FiberErhvervPlusBundleData.CAMPAIGN_PRICE_A.equals(campaign)) {
			productGroup = MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_A;
		} else if (FiberErhvervPlusBundleData.CAMPAIGN_PRICE_B.equals(campaign)) {
			productGroup = MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_B;
		} else {
			productGroup = MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_STANDARD;
		}
		return productGroup;
	}

	private void updateIpAdressOptions(MobileContract contract, MobileProductGroupEnum productGroup,
			BootstrapSelectSingle ipAddressDropDownChoice, Integer ipAddressOptionsState) {
		List choices = ipAddressDropDownChoice.getChoices();
		choices.clear();
		for (Product product : getProductGroup(contract.getBusinessArea(), productGroup).getProducts()) {
			if ((ipAddressOptionsState != FiberErhvervPlusBundleData.IP_ADDRESS_4_OR_8) || (product.getPublicName().indexOf("Ingen") == -1)) {
				choices.add(product.getId());
			}
		}
	}

	private String getDefaultString(String value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	private Integer calcIpAddressOptionState(ValueMap values, int index) {
		/*
		 * Hvis man har sagt ja til at ”indgår i løsning”, så skal der ud over muligheden 
		 * for 4 eller 8 IP adresser også være mulighed for ”Ingen LAN IP adresser”. 
		 * Alle andre kombinationsmuligheder skal præsenteres for valget mellem 4 el. 8 IP adresser
		 */
		if (YES.equals(values.get(makeKey(KEY_IN_SOLUTION, index)))) {
			return FiberErhvervPlusBundleData.IP_ADDRESS_ALL_OPTIONS;
		}
		return FiberErhvervPlusBundleData.IP_ADDRESS_4_OR_8;
	}

//	private String getDefaultString(Boolean value) {
//		if (value == null) {
//			return null;
//		}
//		if (value) {
//			return YES;
//		} else {
//			return NO;
//		}
//	}
//
//	private void updateComponents(int ci, ComponentWrapper[] components, AjaxRequestTarget target) {
//		for (int j = ci + 1; j < components.length; j++) {
//			components[j].update(target);
//		}
//	}
//
//	private BootstrapSelectSingle addEntityDropDownChoice(final MobileContract contract, final FiberPanelValues values,
//			Map<String, String> labelMap, Long initialValue, MobileProductGroupEnum productGroup, String key,
//			FormGroup<FiberPanelValues> formGroup, String label, final int componentIndex, final int lastComponentIndex,
//			final ComponentWrapper[] components, UpdateListener listener) {
//		values.put(key, initialValue);
//		labelMap.put(key, label);
//		List<Long> entityIds = new ArrayList<>();
//		if (MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.equals(productGroup)) {
//			entityIds.add(NO_CHOICE_ENTITY_ID);
//			for (Product product : getProductGroup(contract.getBusinessArea(), productGroup).getProducts()) {
//				if (product.getProductId().equals("4401711") || product.getProductId().equals("4401718")) {
//					entityIds.add(product.getId());
//				}
//			}
//		} else {
//			for (Product product : getProductGroup(contract.getBusinessArea(), productGroup).getProducts()) {
//				entityIds.add(product.getId());
//			}
//		}
//
//		BootstrapSelectSingle component = formGroup.addSelectSinglePanel(key, entityIds, new IChoiceRenderer() {
//			@Override
//			public Object getDisplayValue(Object value) {
//				if (NO_CHOICE_ENTITY_ID.equals(value)) {
//					return "Nej";
//				}
//				return productDao.findById((Long) value).getPublicName();
//			}
//
//			@Override
//			public String getIdValue(Object object, int index) {
//				return "" + index;
//			}
//		}, new BootstrapSelectOptions());
//
//		component.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				updateComponents(componentIndex, components, target);
//			}
//		});
//
//		components[componentIndex] = new ComponentWrapper(component, listener);
//		return component;
//	}
//
//	private DropDownChoice addStringDropDownChoice(final MobileContract contract, final FiberPanelValues values,
//			Map<String, String> labelMap, String initialValue, List<String> options, String key,
//			FormGroup<FiberPanelValues> formGroup, String label, final int componentIndex, final int lastComponentIndex,
//			final ComponentWrapper[] components, UpdateListener listener) {
//		values.put(key, initialValue);
//		labelMap.put(key, label);
//
//		BootstrapSelectSingle component = formGroup.addSelectSinglePanel(key, options, new IChoiceRenderer() {
//			@Override
//			public Object getDisplayValue(Object value) {
//				return value;
//			}
//
//			@Override
//			public String getIdValue(Object object, int index) {
//				return "" + index;
//			}
//		}, new BootstrapSelectOptions());
//
//		component.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				updateComponents(componentIndex, components, target);
//			}
//		});
//
//		components[componentIndex] = new ComponentWrapper(component, listener);
//		return component;
//	}
//
//	private NumberTextField addNumberTextField(final MobileContract contract, final FiberPanelValues values,
//			Map<String, String> labelMap, Number initialValue, Integer min, Integer max, String key,
//			FormGroup<FiberPanelValues> group, String label, final int componentIndex, final int lastComponentIndex,
//			final ComponentWrapper[] components, UpdateListener listener) {
//		values.put(key, initialValue);
//		labelMap.put(key, label);
//
//		NumberTextField component = group.addNumberTextField(key);
//		component.add(AttributeModifier.append("min", min));
//		component.add(AttributeModifier.append("max", max));
//
//		// For updating of "values"
//		component.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				// Nothing needed
//			}
//		});
//
//		// For throttling
//		for (String event: new String[] {"change", "keyup"}) {
//			component.add(new AjaxEventBehavior(event) {
//				@Override
//				protected void onEvent(AjaxRequestTarget target) {
//					updateComponents(componentIndex, components, target);
//				}
//
//				@Override
//				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//					super.updateAjaxAttributes(attributes);
//					attributes.setThrottlingSettings(new ThrottlingSettings(key, Duration.ONE_SECOND, true));
//				}
//			});
//		}
//
//		components[componentIndex] = new ComponentWrapper(component, listener);
//		return component;
//	}
//
//	private TextField addTextField(final MobileContract contract, final FiberPanelValues values,
//			Map<String, String> labelMap, String initialValue, final String key, FormGroup<FiberPanelValues> group,
//			String label, final int componentIndex, final int lastComponentIndex, final ComponentWrapper[] components,
//			UpdateListener listener) {
//		values.put(key, initialValue);
//		labelMap.put(key, label);
//
//		TextField component = group.addTextField(key);
//
//		// For updating of "values"
//		component.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				// Nothing needed
//			}
//		});
//
//		// For throttling
//		component.add(new AjaxEventBehavior("keyup") {
//			@Override
//			protected void onEvent(AjaxRequestTarget target) {
//				updateComponents(componentIndex, components, target);
//			}
//
//			@Override
//			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//				super.updateAjaxAttributes(attributes);
//				attributes.setThrottlingSettings(new ThrottlingSettings(key, Duration.ONE_SECOND, true));
//			}
//		});
//
//		components[componentIndex] = new ComponentWrapper(component, listener);
//		return component;
//	}

	private String getFormKey(final MobileProductGroupEnum deviceGroup, int bundleIndex) {
		return deviceGroup.getKey().replace('.', '_') + '_' + bundleIndex;
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

	private Boolean nullYesOrNo(Object value) {
		if (value == null) {
			return null;
		}
		if (YES.equals(value)) {
			return true;
		}
		if (YES_CUSTOM.equals(value)) {
			return true;
		}
		return false;
	}

	private void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountMap, Product product, int bi) {
		if (product != null) {
			addToProductToCountsMap(productToCountMap, product, 1, bi);
		}
	}

	// private void addProduct(Map<Product, List<CountAndInstallation>>
	// productToCountMap, Product product, long count) {
	// if (count > 0) {
	// List<CountAndInstallation> countAndInstallations =
	// productToCountMap.get(product);
	// if (countAndInstallations == null) {
	// countAndInstallations = new ArrayList<>();
	// CountAndInstallation countAndInstallation = new CountAndInstallation();
	// countAndInstallation.setCountNew((int) count);
	// countAndInstallations.add(countAndInstallation);
	// productToCountMap.put(product, countAndInstallations);
	// } else {
	// countAndInstallations.get(0).setCountNew(countAndInstallations.get(0).getCountNew()
	// + (int) count);
	// }
	// }
	// }

	private void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountsMap, Product product,
			long count, int bi) {
		if (count > 0) {
			List<CountAndInstallation> countAndInstallations = productToCountsMap.get(product);
			if (countAndInstallations == null) {
				countAndInstallations = new ArrayList<>();
			}
			CountAndInstallation countAndInstallation = new CountAndInstallation();
			countAndInstallation.setCountNew((int) count);
			countAndInstallation.setSubIndex(bi);
			countAndInstallations.add(countAndInstallation);
			productToCountsMap.put(product, countAndInstallations);
			if (count > 0) {
				log.info("prod: " + product.getProductId());
			}
		}
	}
}
