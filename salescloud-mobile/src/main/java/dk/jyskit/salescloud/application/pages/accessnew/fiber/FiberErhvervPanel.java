package dk.jyskit.salescloud.application.pages.accessnew.fiber;

import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.FiberErhvervBundleData.*;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper.makeKey;

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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.utils.ExceptionUtils;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.value.ValueMap;


@SuppressWarnings("serial")
@Slf4j
public class FiberErhvervPanel extends BasicLocationPanel {
	private static final String KEY_PHONE_30_MIN 			= "p3";
//	private static final String KEY_PERIOD		 			= "p";
	private static final String KEY_SPEED		 			= "s";
	private static final String KEY_FIBER		 			= "f";
	private static final String KEY_ROUTER_FIREWALL			= "r";
	private static final String KEY_PUBLIC_IP				= "pi";
	private static final String KEY_REDUNDANCY				= "re";
	private static final String KEY_SUPERVISION 			= "su";
	private static final String KEY_SERVICE 				= "se";
	private static final String KEY_INSTALLATION 			= "i";
	private static final String KEY_DIGGING		 			= "d";
	private static final String KEY_INSPECTION	 			= "in";
	private static final String KEY_LEVERANCEKOORDINERING	= "lk";
	private static final String KEY_INFRASTRUCTURE			= "if";

	private static final String KEY_SMS_ALERT_NO				= "sa";
	private static final String KEY_CONTACT_SUPERVISION			= "cs";
	private static final String KEY_CONTACT_SUPERVISION_PHONE	= "csp";
	private static final String KEY_CONTACT_SUPERVISION_EMAIL	= "cse";
	private static final String KEY_CONTACT_INSPECTION			= "ci";
	private static final String KEY_CONTACT_INSPECTION_PHONE	= "cip";
	
	private static final byte OUTPUT_A	= 1;
	private static final byte OUTPUT_B	= 2;
	private static final byte OUTPUT_C	= 3;
	private static final byte OUTPUT_D	= 4;
	private static final byte OUTPUT_E	= 5;
	private static final byte OUTPUT_F	= 6;
	
	private static final String ROUTER_FIREWALL_TDC_DHCP		 			= "TDC Router med firewall og Wi-Fi - 1 DHCP fast WAN IP";
	private static final String ROUTER_FIREWALL_OWN_FIREWALL_DHCP 			= "Kunden benytter egen firewall – 1 DHCP fast WAN IP";
	private static final String ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP 		= "Kunden benytter egen firewall – 1 statisk fast WAN IP";

	@Inject
	private PageNavigator navigator;
	@Inject
	private OrderLineDao orderLineDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private ProductGroupDao productGroupDao;
	@Inject
	private ContractSaver contractSaver;
	
	@SuppressWarnings("rawtypes")
	private DropDownChoice periodDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle speedDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle fiberDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private DropDownChoice routerFirewallDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle publicIpDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle redundancyDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle supervisionDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle serviceDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private BootstrapSelectSingle installationDropDownChoice;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private NumberTextField diggingField;
	
	@SuppressWarnings("rawtypes")
	private DropDownChoice inspectionDropDownChoice;
	
	@SuppressWarnings("rawtypes")
	private DropDownChoice coordinationDropDownChoice;

	private MobileProduct productFejlretning0_24;

	public FiberErhvervPanel(String id, Integer tabIndex) {
		super(id, tabIndex);
	}

	@Override
	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
		super.initBundle(contract, values, labelMap, bi);

		contract.getFiberErhvervBundle(bi);  // Make sure index exists
		List<FiberErhvervBundleData> fiberBundles = contract.getFiberErhvervBundles();

		// Init stuff here

		contract.setFiberErhvervBundles(fiberBundles);

		List<LocationBundleData> locationBundles = contract.getLocationBundles();

		LocationBundleData locationBundleData 			= locationBundles.get(bi);
		FiberErhvervBundleData fiberErhvervBundleData 	= fiberBundles.get(bi);

		if (StringUtils.isEmpty(locationBundleData.getAddress()) && !StringUtils.isEmpty(fiberBundles.get(bi).getAddress())) {
			log.info("Transfer address from fiber bundle to location bundle");
			locationBundleData.setAddressRoad(fiberErhvervBundleData.getAddressRoad());
			locationBundleData.setAddressCity(fiberErhvervBundleData.getAddressCity());
			locationBundleData.setAddressZipCode(fiberErhvervBundleData.getAddressZipCode());
			contract.setLocationBundles(locationBundles);
		}

		productFejlretning0_24 = (MobileProduct) productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "0360700");
		if (productFejlretning0_24 == null) {
			productFejlretning0_24 = (MobileProduct) productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "360700");
		}
		if (productFejlretning0_24 == null) {
			log.error("0360700 missing");
		}
	}

	protected String getGroupName(MobileContract contract, int bi) {
		if (contract.getBusinessArea().isOnePlus()) {
			return null;
		} else {
			return "Fiber Erhverv";
		}
	}

	@Override
	protected void addComponents(final MobileContract contract,
								 Map<String, String> labelMap, ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		final LocationBundleData locationBundle	= contract.getLocationBundle(bi);
		final FiberErhvervBundleData bundle 	= contract.getFiberErhvervBundle(bi);

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Tlf. nr. til opringning 30 minutter før teknikerbesøg", bundle.getContactPhone30Minutes(), KEY_PHONE_30_MIN, bi);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)))
							&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)))
							&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_CITY, bi)));
					wrapper.getComponent().getParent().getParent().setVisible(visible);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, FiberErhvervBundleData.getInfrastructureAsString(bundle.getInfrastructureTypeNo()),
					Lists.newArrayList(INFRASTRUCTURE_TDC, INFRASTRUCTURE_ENIIG, INFRASTRUCTURE_EWII),
					KEY_INFRASTRUCTURE, bi, formContainer, "Valg af infrastruktur", componentWrappers);
			componentWrappers.add(wrapper);
			DropDownChoice field = wrapper.getComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)))
							&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)))
							&& !StringUtils.isEmpty((String) values.get(makeKey(KEY_ADDRESS_CITY, bi)));
					wrapper.getComponent().getParent().getParent().setVisible(visible);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getSpeedEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_SPEED,
							KEY_SPEED, bi, formContainer, "Hastighed", componentWrappers, "TDC Erhverv Fiber er pt. ikke muligt på EWII", null);
			componentWrappers.add(wrapper);
			speedDropDownChoice = wrapper.getFormComponent();
			if (INFRASTRUCTURE_CODE_EWII == bundle.getInfrastructureTypeNo()) {
				speedDropDownChoice.setEnabled(false);
			}
			sortOptions(speedDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(!StringUtils.isEmpty((String) values.get(makeKey(KEY_INFRASTRUCTURE, bi))));
					if (INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi)))) {
						if (!speedDropDownChoice.getChoices().contains(NO_CHOICE_ENTITY_ID)) {
							speedDropDownChoice.getChoices().add(0, NO_CHOICE_ENTITY_ID);
							values.put(makeKey(KEY_SPEED, bi), NO_CHOICE_ENTITY_ID);
						}
						wrapper.getComponent().setEnabled(false);
						values.put(makeKey(KEY_ROUTER_FIREWALL, bi), null);
					} else {
						speedDropDownChoice.getChoices().remove(NO_CHOICE_ENTITY_ID);
						wrapper.getComponent().setEnabled(true);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			AbstractRenderer renderer = new AbstractRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					if (NO_CHOICE_ENTITY_ID.equals(value)) {
						return "Nej";
					}
					String s = productDao.findById((Long) value).getPublicName();
					return s.replace("QoS", "Ja -");
				}
			};
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getFiberEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_IF_FIBER,
					KEY_FIBER, bi, formContainer, "Skal fiber indgå i løsning? (Scale/One)", componentWrappers, "Nej", renderer);
//				if (INFRASTRUCTURE_CODE_ENIIG == bundle.getInfrastructureTypeNo()) {
//					fiberDropDownChoice.setEnabled(false);
//				}
			componentWrappers.add(wrapper);
			fiberDropDownChoice = wrapper.getFormComponent();
			if (INFRASTRUCTURE_CODE_EWII == bundle.getInfrastructureTypeNo()) {
				fiberDropDownChoice.setEnabled(false);
			}
			sortOptions(fiberDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !Objects.isNull((Long) values.get(makeKey(KEY_SPEED, bi))) && !INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi)));
					wrapper.getComponent().getParent().getParent().setVisible(visible);

					if (visible) {
//									if (INFRASTRUCTURE_ENIIG.equals(values.get(KEY_INFRASTRUCTURE + bi))) {
//										component.setEnabled(false);
//										values.put(KEY_FIBER + bi, NO_CHOICE_ENTITY_ID);
//									} else {
						wrapper.getComponent().setEnabled(true);
//									}
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			String presentValue = null;
			if (Long.valueOf(0).equals(bundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_TDC_DHCP;
			} else if (Long.valueOf(1).equals(bundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_OWN_FIREWALL_DHCP;
			} else if (Long.valueOf(2).equals(bundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP;
			}
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, presentValue,
					Lists.newArrayList(
							ROUTER_FIREWALL_TDC_DHCP
							,ROUTER_FIREWALL_OWN_FIREWALL_DHCP
							,ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP),
					KEY_ROUTER_FIREWALL, bi, formContainer, "Router/Firewall/Wi-Fi", componentWrappers);
			componentWrappers.add(wrapper);
			routerFirewallDropDownChoice = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(
							// skal også vare synlig v nej til fiber
							!INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi))) && (values.get(makeKey(KEY_FIBER, bi)) != null)
					);
					wrapper.getComponent().setEnabled(!INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getPublicIpEntityId(), PRODUCT_GROUP_ACCESS_IP,
					KEY_PUBLIC_IP, bi, formContainer, "Ekstra offentlige LAN IP adresser", componentWrappers, "Nej", null);
			componentWrappers.add(wrapper);
			publicIpDropDownChoice = wrapper.getFormComponent();
			sortOptions(publicIpDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = (values.get(makeKey(KEY_ROUTER_FIREWALL, bi)) != null);
					wrapper.getComponent().getParent().getParent().setVisible(visible);
					if (visible) {
						if (ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals(values.get(makeKey(KEY_ROUTER_FIREWALL, bi)))) {
							wrapper.getComponent().setEnabled(true);
						} else {
							wrapper.getComponent().setEnabled(false);
							values.put(makeKey(KEY_PUBLIC_IP, bi), NO_CHOICE_ENTITY_ID);
						}
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getRedundancyEntityId(), PRODUCT_GROUP_ACCESS_REDUNDANCY,
					KEY_REDUNDANCY, bi, formContainer, "Redundans mobil", componentWrappers, "Nej", null);
			componentWrappers.add(wrapper);
			redundancyDropDownChoice = wrapper.getFormComponent();
			sortOptions(redundancyDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(
							(values.get(makeKey(KEY_ROUTER_FIREWALL, bi)) != null)
									&& (values.get(makeKey(KEY_PUBLIC_IP, bi)) != null));	// Enable if it's configured
					String v = (String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi));
					if (v != null) {
						if (ROUTER_FIREWALL_TDC_DHCP.equals(values.get(makeKey(KEY_ROUTER_FIREWALL, bi)))) {
							wrapper.getComponent().setEnabled(true);
						} else {
							wrapper.getComponent().setEnabled(false);
							values.put(makeKey(KEY_REDUNDANCY, bi), NO_CHOICE_ENTITY_ID);
						}
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getSmsAlertNo(), KEY_SMS_ALERT_NO, bi,
					formContainer, "Mobilnummer til SMS varsling", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi))) &&
							(values.get(makeKey(KEY_REDUNDANCY, bi)) != null) && !NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_REDUNDANCY, bi)));
					wrapper.getComponent().getParent().getParent().setVisible(visible);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			AbstractRenderer renderer = new AbstractRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					return (NO_CHOICE_ENTITY_ID.equals(value) ? "Nej" : "Ja");
				}
			};
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getSupervisionEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_SUPERVISION,
					KEY_SUPERVISION, bi, formContainer, "Overvågning", componentWrappers, "Nej", renderer);
			componentWrappers.add(wrapper);
			supervisionDropDownChoice = wrapper.getFormComponent();
			sortOptions(supervisionDropDownChoice.getChoices(), false);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi))) &&
							(values.get(makeKey(KEY_REDUNDANCY, bi)) != null);
					wrapper.getComponent().getParent().getParent().setVisible(visible);
					byte outputType = getOutputType(bi, values);
					if (OUTPUT_B == outputType || OUTPUT_C == outputType) {
						wrapper.getComponent().setEnabled(false);
						values.put(makeKey(KEY_SUPERVISION, bi), NO_CHOICE_ENTITY_ID);
					} else {
						wrapper.getComponent().setEnabled(true);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getContactSupervision(), KEY_CONTACT_SUPERVISION, bi,
					formContainer, "Kontaktperson overvågning", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(
							(values.get(makeKey(KEY_SUPERVISION, bi)) != null) &&
									!NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_SUPERVISION, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getContactSupervisionPhone(), KEY_CONTACT_SUPERVISION_PHONE, bi,
					formContainer, "Kontakt telefonnummer overvågning", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(
							(values.get(makeKey(KEY_SUPERVISION, bi)) != null) &&
									!NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_SUPERVISION, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getContactSupervisionEmail(), KEY_CONTACT_SUPERVISION_EMAIL, bi,
					formContainer, "Kontakt email overvågning", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(
							(values.get(makeKey(KEY_SUPERVISION, bi)) != null) &&
									!NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_SUPERVISION, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getServiceEntityId(), PRODUCT_GROUP_ACCESS_SERVICE,
					KEY_SERVICE, bi, formContainer, "Servicegrad", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			serviceDropDownChoice = wrapper.getFormComponent();
			sortOptions(serviceDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi))) &&
							(values.get(makeKey(KEY_SUPERVISION, bi)) != null);
					wrapper.getComponent().getParent().getParent().setVisible(visible);

					if (INFRASTRUCTURE_ENIIG.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi)))) {
						serviceDropDownChoice.getChoices().remove(productFejlretning0_24.getId());
						if (productFejlretning0_24.getId().equals((Long) values.get(makeKey(KEY_SERVICE, bi)))) {
							values.put(makeKey(KEY_SERVICE, bi), null);
						}
					} else {
						if (!serviceDropDownChoice.getChoices().contains(productFejlretning0_24.getId())) {
							serviceDropDownChoice.getChoices().add(2, productFejlretning0_24.getId());
//										values.put(makeKey(KEY_SERVICE, bi), NO_CHOICE_ENTITY_ID);
						}
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
					bundle.getInstallationEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSTALLATION,
					KEY_INSTALLATION, bi, formContainer, "Installationskode", componentWrappers, null, null);
			componentWrappers.add(wrapper);
			installationDropDownChoice = wrapper.getFormComponent();
			sortOptions(installationDropDownChoice.getChoices(), true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_SERVICE, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<NumberTextField> wrapper = (LocationAccessComponentWrapper<NumberTextField>) LocationAccessComponentWrapper.addNumberTextFieldWrapper(
					values, labelMap, bundle.getDiggingMeters(), 0, 1000,
					KEY_DIGGING, bi, formContainer, "Gravemeter fra Albert", componentWrappers);
			componentWrappers.add(wrapper);
			diggingField = wrapper.getFormComponent();
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_INSTALLATION, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(bundle.getInspection()), Lists.newArrayList(YES, NO),
					KEY_INSPECTION, bi, formContainer, "Besigtigelse", componentWrappers);
			componentWrappers.add(wrapper);
			inspectionDropDownChoice = wrapper.getFormComponent();

			if (INFRASTRUCTURE_CODE_ENIIG == bundle.getInfrastructureTypeNo()) {
				values.put(makeKey(KEY_INSPECTION, bi), NO);
				inspectionDropDownChoice.setEnabled(false);
			}
			inspectionDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_DIGGING, bi)) != null);

					if (INFRASTRUCTURE_ENIIG.equals(values.get(makeKey(KEY_INFRASTRUCTURE, bi)))) {
						wrapper.getComponent().setEnabled(false);
						values.put(makeKey(KEY_INSPECTION, bi), NO);
					} else {
						wrapper.getComponent().setEnabled(true);
					}
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getContactInspection(), KEY_CONTACT_INSPECTION, bi,
					formContainer, "Kontaktperson besigtigelse", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getFormComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(YES.equals(values.get(makeKey(KEY_INSPECTION, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(
					values, labelMap, bundle.getContactInspectionPhone(), KEY_CONTACT_INSPECTION_PHONE, bi,
					formContainer, "Kontakt telefonnummer besigtigelse", componentWrappers);
			componentWrappers.add(wrapper);
			TextField textField = wrapper.getFormComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(YES.equals(values.get(makeKey(KEY_INSPECTION, bi))));
					return new UpdateLevel(state, wrapper);
				}
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
					getDefaultString(bundle.getCoordination()), Lists.newArrayList(YES, NO),
					KEY_LEVERANCEKOORDINERING, bi, formContainer, "Leverancekoordinering", componentWrappers);
			componentWrappers.add(wrapper);
			coordinationDropDownChoice = wrapper.getFormComponent();
			coordinationDropDownChoice.setRequired(true);
			wrapper.setListener(new LocationAccessComponentWrapper.WrapperUpdateListener() {
				@Override
				public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
					VisibleAndEnabled state = new VisibleAndEnabled(wrapper);
					wrapper.getComponent().getParent().getParent().setVisible(values.get(makeKey(KEY_INSPECTION, bi)) != null);
					return new UpdateLevel(state, wrapper);
				}
			});
		}
	}

/*

	private void addBundleForm(MobileContract contract, FiberPanelValues values, Map<String, String> labelMap, Jsr303Form<FiberPanelValues> form, int i) {
		final int bi = i;
		final FiberErhvervBundleData fiberBundle = contract.getFiberErhvervBundle(i);

		ComponentContainerPanel<FiberPanelValues> formContainer = form;
//		FormGroup<FiberPanelValues> group;
		if (!contract.getBusinessArea().isOnePlus()) {
			formContainer = form.createGroup(Model.of("Fiber Erhverv"));
		} else {
			formContainer = form.createGroup(Model.of("Lokation " + (i + 1)));
		}
		final int lastComponentIndex = 21;
		final ComponentWrapper[] components = new ComponentWrapper[lastComponentIndex + 1];
		int componentIndex = 0;

		if (bi == 0) {
			if (StringUtils.isEmpty(contract.getFiberErhvervBundles().get(i).getAddressRoad())) {
				fiberBundle.setAddressRoad(contract.getCustomer().getAddress());
			}
			if (StringUtils.isEmpty(fiberBundle.getAddressZipCode())) {
				fiberBundle.setAddressZipCode(contract.getCustomer().getZipCode());
			}
			if (StringUtils.isEmpty(fiberBundle.getAddressCity())) {
				fiberBundle.setAddressCity(contract.getCustomer().getCity());
			}
		}

		{
			addTextField(contract, values, labelMap, fiberBundle.getAddressRoad(), KEY_ADDRESS_ROAD + bi, formContainer,
					"Adresse", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
						}
					});
		}

		{
			addTextField(contract, values, labelMap, fiberBundle.getAddressZipCode(), KEY_ADDRESS_ZIPCODE + bi,
					formContainer, "Postnr.", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
						}
					});
		}

		{
			addTextField(contract, values, labelMap, fiberBundle.getAddressCity(), KEY_ADDRESS_CITY + bi, formContainer,
					"By", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
						}
					});
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactPhone30Minutes(), KEY_PHONE_30_MIN + bi, formContainer,
					"Tlf. nr. til opringning 30 minutter før teknikerbesøg", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent()
									.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			addStringDropDownChoice(contract, values, labelMap, FiberErhvervBundleData.getInfrastructureAsString(fiberBundle.getInfrastructureTypeNo()),
					Lists.newArrayList(INFRASTRUCTURE_TDC, INFRASTRUCTURE_ENIIG, INFRASTRUCTURE_EWII),
					KEY_INFRASTRUCTURE + bi, formContainer, "Valg af infrastruktur", componentIndex++, lastComponentIndex, components,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent()
									.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
											&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
						}
					});
		}

//			{
//				String presentValue = null;
//				if (Integer.valueOf(99).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_12_MONTHS_NO_DISCOUNT;
//				} else if (Integer.valueOf(12).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_12_MONTHS;
//				} else if (Integer.valueOf(24).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_24_MONTHS;
//				} else if (Integer.valueOf(36).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_36_MONTHS;
//				}
//				periodDropDownChoice = addStringDropDownChoice(contract, values, labelMap, presentValue,
//						Lists.newArrayList(
//								null
//								,FiberErhvervBundleData.PERIOD_12_MONTHS_NO_DISCOUNT
//								,FiberErhvervBundleData.PERIOD_12_MONTHS
//								,FiberErhvervBundleData.PERIOD_24_MONTHS
//								,FiberErhvervBundleData.PERIOD_36_MONTHS),
//						KEY_PERIOD + bi, formContainer, "Bindingsperiode / Rabat", componentIndex++, lastComponentIndex, components,
//						new UpdateListener() {
//							@Override
//							public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
//								component.getParent().getParent()
//										.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
//												&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
//												&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi))
//												&& !StringUtils.isEmpty((String) values.get(KEY_PHONE_30_MIN + bi)));
//							}
//						});
//			}

		{
			speedDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getSpeedEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_SPEED,
					KEY_SPEED + bi, formContainer, "Hastighed", componentIndex++, lastComponentIndex, components, "TDC Erhverv Fiber er pt. ikke muligt på EWII",
//						KEY_SPEED + bi, formContainer, "Hastighed", componentIndex++, lastComponentIndex, components, null,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(!StringUtils.isEmpty((String) values.get(KEY_INFRASTRUCTURE + bi)));
//								component.getParent().getParent()
//								.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
//										&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
//										&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi))
//										&& !StringUtils.isEmpty((String) values.get(KEY_PHONE_30_MIN + bi)));
							if (INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi))) {
								if (!speedDropDownChoice.getChoices().contains(NO_CHOICE_ENTITY_ID)) {
									speedDropDownChoice.getChoices().add(0, NO_CHOICE_ENTITY_ID);
									values.put(KEY_SPEED + bi, NO_CHOICE_ENTITY_ID);
								}
								component.setEnabled(false);
								values.put(KEY_ROUTER_FIREWALL + bi, null);
							} else {
								speedDropDownChoice.getChoices().remove(NO_CHOICE_ENTITY_ID);
								component.setEnabled(true);
							}
						}
					}, null);
			if (INFRASTRUCTURE_CODE_EWII == fiberBundle.getInfrastructureTypeNo()) {
				speedDropDownChoice.setEnabled(false);
			}
			sortOptions(speedDropDownChoice.getChoices(), true);
		}

		{
			AbstractRenderer renderer = new AbstractRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					if (NO_CHOICE_ENTITY_ID.equals(value)) {
						return "Nej";
					}
					String s = productDao.findById((Long) value).getPublicName();
					return s.replace("QoS", "Ja -");
				}
			};
			fiberDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getFiberEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_IF_FIBER,
					KEY_FIBER + bi, formContainer, "Skal fiber indgå i løsning? (Scale/One)", componentIndex++, lastComponentIndex, components, "Nej",
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							boolean visible = !Objects.isNull((Long) values.get(KEY_SPEED + bi)) && !INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi));
							component.getParent().getParent().setVisible(visible);

							if (visible) {
//									if (INFRASTRUCTURE_ENIIG.equals(values.get(KEY_INFRASTRUCTURE + bi))) {
//										component.setEnabled(false);
//										values.put(KEY_FIBER + bi, NO_CHOICE_ENTITY_ID);
//									} else {
								component.setEnabled(true);
//									}
							}
						}
					}, renderer);
//				if (INFRASTRUCTURE_CODE_ENIIG == fiberBundle.getInfrastructureTypeNo()) {
//					fiberDropDownChoice.setEnabled(false);
//				}
			if (INFRASTRUCTURE_CODE_EWII == fiberBundle.getInfrastructureTypeNo()) {
				fiberDropDownChoice.setEnabled(false);
			}
			sortOptions(fiberDropDownChoice.getChoices(), true);
		}

		{
			String presentValue = null;
			if (Long.valueOf(0).equals(fiberBundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_TDC_DHCP;
			} else if (Long.valueOf(1).equals(fiberBundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_OWN_FIREWALL_DHCP;
			} else if (Long.valueOf(2).equals(fiberBundle.getRouterFirewallRule())) {
				presentValue = ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP;
			}
			routerFirewallDropDownChoice = addStringDropDownChoice(contract, values, labelMap, presentValue,
					Lists.newArrayList(
							ROUTER_FIREWALL_TDC_DHCP
							,ROUTER_FIREWALL_OWN_FIREWALL_DHCP
							,ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP),
					KEY_ROUTER_FIREWALL + bi, formContainer, "Router/Firewall/Wi-Fi", componentIndex++, lastComponentIndex, components,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(
									// skal også vare synlig v nej til fiber
									!INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi)) && (values.get(KEY_FIBER + bi) != null)
							);
							component.setEnabled(!INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi)));
						}
					});
		}

		{
			publicIpDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getPublicIpEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_LAN_IP,
					KEY_PUBLIC_IP + bi, formContainer, "Ekstra offentlige LAN IP adresser", componentIndex++,
					lastComponentIndex, components, "Nej", new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							boolean visible = (values.get(KEY_ROUTER_FIREWALL + bi) != null);
							component.getParent().getParent().setVisible(visible);
							if (visible) {
								if (ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals(values.get(KEY_ROUTER_FIREWALL + bi))) {
									component.setEnabled(true);
								} else {
									component.setEnabled(false);
									values.put(KEY_PUBLIC_IP + bi, NO_CHOICE_ENTITY_ID);
								}
							}
						}
					}, null);
			sortOptions(publicIpDropDownChoice.getChoices(), true);
		}

		{
			redundancyDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getRedundancyEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_REDUNDANCY,
					KEY_REDUNDANCY + bi, formContainer, "Redundans mobil", componentIndex++, lastComponentIndex, components, "Nej", new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(
									(values.get(KEY_ROUTER_FIREWALL + bi) != null)
											&& (values.get(KEY_PUBLIC_IP + bi) != null));	// Enable if it's configured
							String v = (String) values.get(KEY_ROUTER_FIREWALL + bi);
							if (v != null) {
								if (ROUTER_FIREWALL_TDC_DHCP.equals(values.get(KEY_ROUTER_FIREWALL + bi))) {
									component.setEnabled(true);
								} else {
									component.setEnabled(false);
									values.put(KEY_REDUNDANCY + bi, NO_CHOICE_ENTITY_ID);
								}
							}
						}
					}, null);
			sortOptions(redundancyDropDownChoice.getChoices(), true);
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getSmsAlertNo(), KEY_SMS_ALERT_NO + bi,
					formContainer, "Mobilnummer til SMS varsling", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi)) &&
									(values.get(KEY_REDUNDANCY + bi) != null) && !NO_CHOICE_ENTITY_ID.equals(values.get(KEY_REDUNDANCY + bi));
							component.getParent().getParent().setVisible(visible);
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			AbstractRenderer renderer = new AbstractRenderer() {
				@Override
				public Object getDisplayValue(Object value) {
					return (NO_CHOICE_ENTITY_ID.equals(value) ? "Nej" : "Ja");
				}
			};
			supervisionDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getSupervisionEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_SUPERVISION,
					KEY_SUPERVISION + bi, formContainer, "Overvågning", componentIndex++, lastComponentIndex, components, "Nej", new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi)) &&
									(values.get(KEY_REDUNDANCY + bi) != null);
							component.getParent().getParent().setVisible(visible);
							byte outputType = getOutputType(bi, values);
							if (OUTPUT_B == outputType || OUTPUT_C == outputType) {
								component.setEnabled(false);
								values.put(KEY_SUPERVISION + bi, NO_CHOICE_ENTITY_ID);
							} else {
								component.setEnabled(true);
							}
						}
					}, renderer);
			sortOptions(supervisionDropDownChoice.getChoices(), false);
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactSupervision(), KEY_CONTACT_SUPERVISION + bi,
					formContainer, "Kontaktperson overvågning", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(
									(values.get(KEY_SUPERVISION + bi) != null) &&
											!NO_CHOICE_ENTITY_ID.equals(values.get(KEY_SUPERVISION + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactSupervisionPhone(), KEY_CONTACT_SUPERVISION_PHONE + bi,
					formContainer, "Kontakt telefonnummer overvågning", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(
									(values.get(KEY_SUPERVISION + bi) != null) &&
											!NO_CHOICE_ENTITY_ID.equals(values.get(KEY_SUPERVISION + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactSupervisionEmail(), KEY_CONTACT_SUPERVISION_EMAIL + bi,
					formContainer, "Kontakt email overvågning", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(
									(values.get(KEY_SUPERVISION + bi) != null) &&
											!NO_CHOICE_ENTITY_ID.equals(values.get(KEY_SUPERVISION + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			serviceDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getServiceEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_SERVICE,
					KEY_SERVICE + bi, formContainer, "Servicegrad", componentIndex++, lastComponentIndex, components, null,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							boolean visible = !INFRASTRUCTURE_EWII.equals(values.get(KEY_INFRASTRUCTURE + bi)) &&
									(values.get(KEY_SUPERVISION + bi) != null);
							component.getParent().getParent().setVisible(visible);

							if (INFRASTRUCTURE_ENIIG.equals(values.get(KEY_INFRASTRUCTURE + bi))) {
								serviceDropDownChoice.getChoices().remove(productFejlretning0_24.getId());
								if (productFejlretning0_24.getId().equals((Long) values.get(KEY_SERVICE + bi))) {
									values.put(KEY_SERVICE + bi, null);
								}
							} else {
								if (!serviceDropDownChoice.getChoices().contains(productFejlretning0_24.getId())) {
									serviceDropDownChoice.getChoices().add(2, productFejlretning0_24.getId());
//										values.put(KEY_SERVICE + bi, NO_CHOICE_ENTITY_ID);
								}
							}
						}
					}, null);
			sortOptions(serviceDropDownChoice.getChoices(), true);
		}

		{
			installationDropDownChoice = addEntityDropDownChoice(contract, values, labelMap,
					fiberBundle.getInstallationEntityId(), PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSTALLATION,
					KEY_INSTALLATION + bi, formContainer, "Installationskode", componentIndex++, lastComponentIndex, components, null,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(values.get(KEY_SERVICE + bi) != null);
						}
					}, null);
			sortOptions(installationDropDownChoice.getChoices(), true);
		}

		{
			diggingField = addNumberTextField(contract, values, labelMap, fiberBundle.getDiggingMeters(), 0, 1000,
					KEY_DIGGING + bi, formContainer, "Gravemeter fra Albert", componentIndex++, lastComponentIndex, components,
					new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							component.getParent().getParent().setVisible(values.get(KEY_INSTALLATION + bi) != null);
						}
					});
		}

		{
			inspectionDropDownChoice = addStringDropDownChoice(contract, values, labelMap,
					getDefaultString(fiberBundle.getInspection()), Lists.newArrayList(YES, NO),
					KEY_INSPECTION + bi, formContainer, "Besigtigelse", componentIndex++,
					lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							component.getParent().getParent().setVisible(values.get(KEY_DIGGING + bi) != null);

							if (INFRASTRUCTURE_ENIIG.equals(values.get(KEY_INFRASTRUCTURE + bi))) {
								component.setEnabled(false);
								values.put(KEY_INSPECTION + bi, NO);
							} else {
								component.setEnabled(true);
							}
						}
					});
			if (INFRASTRUCTURE_CODE_ENIIG == fiberBundle.getInfrastructureTypeNo()) {
				values.put(KEY_INSPECTION + bi, NO);
				inspectionDropDownChoice.setEnabled(false);
			}
			inspectionDropDownChoice.setRequired(true);
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactInspection(), KEY_CONTACT_INSPECTION + bi,
					formContainer, "Kontaktperson besigtigelse", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(YES.equals(values.get(KEY_INSPECTION + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			TextField textField = addTextField(contract, values, labelMap, fiberBundle.getContactInspectionPhone(), KEY_CONTACT_INSPECTION_PHONE + bi,
					formContainer, "Kontakt telefonnummer besigtigelse", componentIndex++, lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							component.getParent().getParent().setVisible(YES.equals(values.get(KEY_INSPECTION + bi)));
						}
					});
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
		}

		{
			coordinationDropDownChoice = addStringDropDownChoice(contract, values, labelMap,
					getDefaultString(fiberBundle.getCoordination()), Lists.newArrayList(YES, NO),
					KEY_LEVERANCEKOORDINERING + bi, formContainer, "Leverancekoordinering", componentIndex++,
					lastComponentIndex, components, new UpdateListener() {
						@Override
						public void onAjaxEvent(AjaxRequestTarget target, FormComponent component) {
							component.getParent().getParent().setVisible(values.get(KEY_INSPECTION + bi) != null);
						}
					});
			coordinationDropDownChoice.setRequired(true);
		}

		for (ComponentWrapper componentWrapper : components) {
			componentWrapper.update(null);
		}

		AjaxButton button = formContainer.addButton("action.delete", Buttons.Type.Info, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				if (contract.getBusinessArea().isOnePlus()) {
					{
						List<LocationBundleData> bundles = contract.getLocationBundles();
						bundles.remove(bi);
						contract.setLocationBundles(bundles);
					}
					{
						List<FiberErhvervBundleData> bundles = contract.getFiberErhvervBundles();
						bundles.remove(bi);
						contract.setFiberErhvervBundles(bundles);
					}
				} else {
					contract.getFiberErhvervBundles().remove(contract.getFiberErhvervBundles().size() - 1);
					List<FiberErhvervBundleData> bundles = contract.getFiberErhvervBundles();
					bundles.remove(bundles.size() - 1);
					contract.setFiberErhvervBundles(bundles);
				}


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

	@Override
	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page,
								int tabIndex, AjaxRequestTarget target) {
		if (!super.saveAndNavigate(contract, page, tabIndex, target)) {
			return false;
		}
		
		int bi = tabIndex;

		try {
			// Transfer values to contract
			List<FiberErhvervBundleData> fiberBundles = contract.getFiberErhvervBundles();
			contract.setFiberErhvervBundles(fiberBundles);
//			for (int bi = 0; bi < fiberBundles.size(); bi++) {
				FiberErhvervBundleData bundle = fiberBundles.get(bi);
//				bundle.setAddressRoad((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)));
//				bundle.setAddressZipCode((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)));
//				bundle.setAddressCity((String) values.get(makeKey(KEY_ADDRESS_CITY, bi)));
				bundle.setContactPhone30Minutes((String) values.get(makeKey(KEY_PHONE_30_MIN, bi)));
				bundle.setCoordination(nullFalseTrue((String) values.get(makeKey(KEY_LEVERANCEKOORDINERING, bi))));
				bundle.setInspection(nullFalseTrue((String) values.get(makeKey(KEY_INSPECTION, bi))));
				bundle.setDiggingMeters(getEntityId(values, makeKey(KEY_DIGGING, bi)));
				bundle.setFiberEntityId(getEntityId(values, makeKey(KEY_FIBER, bi)));
				bundle.setInstallationEntityId(getEntityId(values, makeKey(KEY_INSTALLATION, bi)));
				bundle.setPublicIpEntityId(getEntityId(values, makeKey(KEY_PUBLIC_IP, bi)));
				bundle.setRedundancyEntityId(getEntityId(values, makeKey(KEY_REDUNDANCY, bi)));
				bundle.setServiceEntityId(getEntityId(values, makeKey(KEY_SERVICE, bi)));
				bundle.setSpeedEntityId(getEntityId(values, makeKey(KEY_SPEED, bi)));
				bundle.setSupervisionEntityId(getEntityId(values, makeKey(KEY_SUPERVISION, bi)));

				if (bundle.isRedundancySelected()) {
					bundle.setSmsAlertNo((String) values.get(makeKey(KEY_SMS_ALERT_NO, bi)));
				} else {
					bundle.setSmsAlertNo(null);
				}
				
				if (bundle.isSupervisionSelected()) {
					bundle.setContactSupervision((String) values.get(makeKey(KEY_CONTACT_SUPERVISION, bi)));
					bundle.setContactSupervisionEmail((String) values.get(makeKey(KEY_CONTACT_SUPERVISION_EMAIL, bi)));
					bundle.setContactSupervisionPhone((String) values.get(makeKey(KEY_CONTACT_SUPERVISION_PHONE, bi)));
				} else {
					bundle.setContactSupervision(null);
					bundle.setContactSupervisionEmail(null);
					bundle.setContactSupervisionPhone(null);
				}
				
				if (bundle.isInspectionSelected()) {
					bundle.setContactInspection((String) values.get(makeKey(KEY_CONTACT_INSPECTION, bi)));
					bundle.setContactInspectionPhone((String) values.get(makeKey(KEY_CONTACT_INSPECTION_PHONE, bi)));
				} else {
					bundle.setContactInspection(null);
					bundle.setContactInspectionPhone(null);
				}

				bundle.setInfrastructureTypeNo(FiberErhvervBundleData.getInfrastructureAsCode((String) values.get(makeKey(KEY_INFRASTRUCTURE, bi))));
				String infrastructure = (String) values.get(makeKey(KEY_INFRASTRUCTURE, bi));
				if (infrastructure == null) {
					bundle.setInfrastructureTypeNo(null);
				} else {
					switch (infrastructure) {
						case INFRASTRUCTURE_TDC:
							bundle.setInfrastructureTypeNo(Integer.valueOf(0));
							break;
						case INFRASTRUCTURE_ENIIG:
							bundle.setInfrastructureTypeNo(Integer.valueOf(1));
							break;
						case INFRASTRUCTURE_EWII:
							bundle.setInfrastructureTypeNo(Integer.valueOf(2));
							break;
					}
				}

				String routerFirewall = (String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi));
				if (routerFirewall == null) {
					bundle.setRouterFirewallRule(null);
				} else {
					switch (routerFirewall) {
					case ROUTER_FIREWALL_TDC_DHCP:
						bundle.setRouterFirewallRule(Long.valueOf(0));
						break;
					case ROUTER_FIREWALL_OWN_FIREWALL_DHCP:
						bundle.setRouterFirewallRule(Long.valueOf(1));
						break;
					case ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP:
						bundle.setRouterFirewallRule(Long.valueOf(2));
						break;
					}
				}
				bundle.setCoordination(nullYesOrNo(values.get(makeKey(KEY_LEVERANCEKOORDINERING, bi))));
//			}
			contract.setFiberErhvervBundles(fiberBundles);
			
			// Adjust order lines
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();
			
			MobileProductGroup parentGroup = getProductGroup(contract.getBusinessArea(), PRODUCT_GROUP_FIBER_NEW_BUNDLE);
			
//			for (int bi = 0; bi < fiberBundles.size(); bi++) {
				FiberErhvervBundleData fiberBundle = fiberBundles.get(bi);
				
				if (fiberBundle.getSpeedEntityId() != null) {
					Product p;

					if (fiberBundle.getInstallationEntityId() != null) {
						p = productDao.findById(fiberBundle.getInstallationEntityId());
						if (p.getProductId().equals("8256300") || p.getProductId().equals("8256400")) {
							addProductsForBundle(contract, productToCountMap, bi, "8249600", "9999900");
						} else {
							// Hvis installationskode 2-5, så GIG tekniker til kr. 0,-
							addProductsForBundle(contract, productToCountMap, bi, "8249600_1");
						}
					}

					if (fiberBundle.getDiggingMeters() != null && fiberBundle.getDiggingMeters() > 30) {
						addToProductToCountsMap(productToCountMap, productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), "8274100"), 
								fiberBundle.getDiggingMeters().intValue() - 30, 0, bi);
					}
	
					if (Boolean.TRUE.equals(fiberBundle.getCoordination())) {
						addProductsForBundle(contract, productToCountMap, bi, "8299600");
					}
					
					if (fiberBundle.isInspectionSelected()) {
						addProductsForBundle(contract, productToCountMap, bi, "8284800");
					}
					
					if (fiberBundle.isRedundancySelected()) {
						addProductsForBundle(contract, productToCountMap, bi, "3242201");
						addToProductToCountsMap(productToCountMap, productDao.findById(fiberBundle.getRedundancyEntityId()), 1, 0, bi);
					}
					
					for (Long entityId : fiberBundle.getProductEntityIds()) {
						if (!NO_CHOICE_ENTITY_ID.equals(entityId)) {
							addToProductToCountsMap(productToCountMap, productDao.findById(entityId), 1, 0, bi);
						}
					}
	
					if (fiberBundle.isFiberSelected()) {
						addProductsForBundle(contract, productToCountMap, bi, "7622704");
					}

					// Add products based on "output type"
					switch (getOutputType(bi, values)) {
					case OUTPUT_A:
						addProductsForBundle(contract, productToCountMap, bi, "0053967", "6905300", "9991400", "5360700");
						break;
					case OUTPUT_B:
						addProductsForBundle(contract, productToCountMap, bi, "0053780", "5360700");
						break;
					case OUTPUT_C:
						addProductsForBundle(contract, productToCountMap, bi, "5360900", "0053780", "4401251");
						
						if (!fiberBundle.nullOrNoEntity(fiberBundle.getPublicIpEntityId())) {
							p = productDao.findById(fiberBundle.getPublicIpEntityId());
							if (p.getProductId().equals("4401251")) {
								addProductsForBundle(contract, productToCountMap, bi, "4401251", "4401219");
							} else if (p.getProductId().equals("4401252")) {
								addProductsForBundle(contract, productToCountMap, bi, "4401251");
							}
						}
						break;
					case OUTPUT_D:
						addProductsForBundle(contract, productToCountMap, bi, "6916100", "6905926", "6972500", "0714700", "6931200", "0679200", "0053967", "9991400", "5360700");
						break;
					case OUTPUT_E:
						addProductsForBundle(contract, productToCountMap, bi, "6916900", "0053790", "7622101", "8962800", "5360700");
						break;
					case OUTPUT_F:
						addProductsForBundle(contract, productToCountMap, bi, "6916900", "0053790", "7622201", "4401251", "5360700");
						
						if (!fiberBundle.nullOrNoEntity(fiberBundle.getPublicIpEntityId())) {
							p = productDao.findById(fiberBundle.getPublicIpEntityId());
							if (p.getProductId().equals("4401251")) {
								addProductsForBundle(contract, productToCountMap, bi, "4401219", "4401251");
							}
						}
						break;
					}
					
					// Add mandatory products
					ProductGroup productGroup = productGroupDao.findByBusinessAreaAndUniqueName(contract.getBusinessArea(), PRODUCT_GROUP_ACCESS_INCLUDED.getKey());
					for (Product product : productGroup.getProducts()) {
						addToProductToCountsMap(productToCountMap, product, 1, 0, bi);
					}
				}
//			}
			
			contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.FIBER_BUNDLE);
			for (ProductGroup group: parentGroup.getChildProductGroups()) {
				contract.adjustOrderLinesForProducts(group, productToCountMap, bi);
			}
			
			for (OrderLine orderLine : contract.getOrderLines()) {
				System.out.println(orderLine.toString() + " - " + orderLine.getCountNew());
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

	private byte getOutputType(int index, ValueMap values) {
		if (NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_FIBER, index))) && ROUTER_FIREWALL_TDC_DHCP.equals(values.get(KEY_ROUTER_FIREWALL + index))) {
			return OUTPUT_A;
		}
		if (NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_FIBER, index))) && ROUTER_FIREWALL_OWN_FIREWALL_DHCP.equals(values.get(KEY_ROUTER_FIREWALL + index))) {
			return OUTPUT_B;
		}
		if (NO_CHOICE_ENTITY_ID.equals(values.get(makeKey(KEY_FIBER, index))) && ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals(values.get(KEY_ROUTER_FIREWALL + index))) {
			return OUTPUT_C;
		}
		if (values.get(KEY_FIBER + index) != null && !NO_CHOICE_ENTITY_ID.equals(values.get(KEY_FIBER + index)) 
				&& ROUTER_FIREWALL_TDC_DHCP.equals(values.get(makeKey(KEY_ROUTER_FIREWALL, index)))) {
			return OUTPUT_D;
		}
		if (values.get(KEY_FIBER + index) != null && !NO_CHOICE_ENTITY_ID.equals(values.get(KEY_FIBER + index)) 
				&& ROUTER_FIREWALL_OWN_FIREWALL_DHCP.equals(values.get(makeKey(KEY_ROUTER_FIREWALL, index)))) {
			return OUTPUT_E;
		}
		if (values.get(KEY_FIBER + index) != null && !NO_CHOICE_ENTITY_ID.equals(values.get(KEY_FIBER + index)) 
				&& ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals(values.get(makeKey(KEY_ROUTER_FIREWALL, index)))) {
			return OUTPUT_F;
		}
		return 0;
	}

	private Boolean nullYesOrNo(Object value) {
		if (value == null) {
			return null;
		}
		if (YES.equals(value)) {
			return true;
		}
//		if (YES_CUSTOM.equals(value)) {
//			return true;
//		}
		return false;
	}
}
