package dk.jyskit.salescloud.application.pages.accessnew.adsl;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.accessnew.basiclocation.BasicLocationPanel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper.WrapperUpdateListener;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationBooleanCallback;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.UpdateLevel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.VisibleAndEnabled;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.value.ValueMap;

import java.util.*;
import java.util.function.Predicate;

import static dk.jyskit.salescloud.application.model.AccessConstants.NON_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.model.OneXdslBundleData.*;
import static dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper.makeKey;

@Slf4j
public class AdslPanel extends BasicLocationPanel {
	private static final String KEY_MODE 					= "mod";
	private static final String KEY_LINE_NO					= "lno";
	private static final String KEY_SPEED 					= "spe";
	private static final String KEY_SPEECH_CHANNELS			= "spc";
	private static final String KEY_ROUTER_FIREWALL			= "rof";
	private static final String KEY_REUSE_HW 				= "reh";
	private static final String KEY_IP		 				= "ip";
	private static final String KEY_REDUNDANCY 				= "red";
	private static final String KEY_SMS			 			= "sms";
	private static final String KEY_SUPERVISION				= "sup";
	private static final String KEY_SUPERVISION_NAME		= "sna";
	private static final String KEY_SUPERVISION_PHONE		= "sph";
	private static final String KEY_SUPERVISION_EMAIL		= "sem";
	private static final String KEY_SERVICE			 		= "ser";
	private static final String KEY_TECH_PHONE		 		= "tep";

	private static final int OUTPUT_A = 1;
	private static final int OUTPUT_B = 2;
	private static final int OUTPUT_C = 3;
	private static final int OUTPUT_D = 4;
	private static final int OUTPUT_E = 5;
	private static final int OUTPUT_F = 6;
	public static final String EXTRA_IPS = "44 012 19";

	@Inject
	private OrderLineDao orderLineDao;
	@Inject
	private ContractSaver contractSaver;

	private BootstrapSelectSingle speechChannelField;
	private boolean lowSpeed;

	public AdslPanel(String id, Integer tabIndex) {
		super(id, tabIndex);
	}

	@Override
	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
		super.initBundle(contract, values, labelMap, bi);

		contract.getXdslBundle(bi);  // Make sure index exists
		List<OneXdslBundleData> xdslBundles = contract.getOneXdslBundles();

		// Init stuff here

		contract.setOneXdslBundles(xdslBundles);

//		List<LocationBundleData> locationBundles = contract.getLocationBundles();
//		LocationBundleData locationBundleData	= locationBundles.get(bi);
	}

	@Override
	protected void addComponents(final MobileContract contract, Map<String, String> labelMap,
								 ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		final LocationBundleData locationBundle = contract.getLocationBundle(bi);
		final OneXdslBundleData bundle 			= contract.getOneXdslBundle(bi);
		contract.getFiberErhvervBundle(bi);
		contract.getFiberErhvervPlusBundle(bi);

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, OneXdslBundleData.getModeAsString(bundle.getMode()),
							Lists.newArrayList(TEXT_NYSALG, TEXT_GENFORHANDLING, TEXT_KONVERTERING),
							KEY_MODE, bi, formContainer, "Handling", componentWrappers);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnAddressFields(w)));
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Linje nr.", bundle.getLineNo(),
					KEY_LINE_NO, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w,
						getVisibleBasedOnAddressFields(w) && getVisibleBasedOnOtherValueNotSelected(w, KEY_MODE, TEXT_NYSALG)
				);
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getSpeedEntityId(), PRODUCT_GROUP_XDSL_BUNDLE_SPEED,
							KEY_SPEED, bi, formContainer, "Hastighed", componentWrappers, PICK_ONE, null);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnAddressFields(w)));
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getSpeechChannelEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_ACCESS_QOS,
							KEY_SPEECH_CHANNELS, bi, formContainer, "Skal linjen bruges til bordapparater? (indgå i One +)",
							componentWrappers, PICK_ONE, "Nej", null);
			speechChannelField = wrapper.getComponent();
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				boolean visible = getVisibleBasedOnAnyEntitySelected(w, KEY_SPEED, true);
				if (visible) {
					// Regel: Hvis ”10/1” eller ”20/2” kan man maksimalt vælge 5 talekanaler
					if (lowSpeed != isLowSpeed(bi)) {
						lowSpeed = !lowSpeed;
						modifySpeechChannelOptions(wrapper, lowSpeed);
					}
					return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, visible).setUpdateComponent(true);
				}
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, visible);
			});
			lowSpeed = isLowSpeed(bi);
			modifySpeechChannelOptions(wrapper, lowSpeed);
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, bundle.getRouterFirewallAsString(),
							OneXdslBundleData.getRouterFirewallStringOptions(),
							KEY_ROUTER_FIREWALL, bi, formContainer, "Router/Firewall/Wi-Fi", componentWrappers);
			DropDownChoice dropDownChoice = wrapper.getFormComponent();
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w,
					getVisibleBasedOnAnyEntitySelected(w, KEY_SPEECH_CHANNELS, true)));
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
							getDefaultString(bundle.getReuseHardware()), Lists.newArrayList(YES, NO),
							KEY_REUSE_HW, bi, formContainer, "Eksisterende udstyr genbruges, hvis muligt?", componentWrappers);
			DropDownChoice dropDownChoice = wrapper.getFormComponent();
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				if (TEXT_NYSALG.equals(values.get(makeKey(KEY_MODE, bi)))) {
					values.put(makeKey(KEY_REUSE_HW, bi), NO);
				}
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w,
						getVisibleBasedOnAnyStringSelected(w, KEY_ROUTER_FIREWALL) && getVisibleBasedOnOtherValueNotSelected(w, KEY_MODE, TEXT_NYSALG));
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getIpEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_ACCESS_IP,
							KEY_IP, bi, formContainer, "Ekstra offentlige LAN IP adresser", componentWrappers, PICK_ONE, "Nej", null);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				// Hvis ”TDC Router m. firewall og wifi” eller “Kunden benytter egen firewall - 1 DHCP”  så ”Nej”
				boolean enabled = true;
				if (TEXT_ROUTER_FIREWALL_TDC_DHCP.equals((String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi))) ||
						TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP.equals((String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi)))) {
					enabled = false;
					values.put(makeKey(KEY_IP, bi), NON_ENTITY_ID);   // "Nej"
				}
				return UpdateLevel.createByVisibleAndEnabled(new VisibleAndEnabled(w), w, getVisibleBasedOnAnyStringSelected(w, KEY_ROUTER_FIREWALL), enabled);
			});
			removeProductsFromEntityDropDown(wrapper, new Predicate<Product>() {
				public boolean test(Product product) {
					return Objects.equals(product.getProductId(), EXTRA_IPS);
				}
			});
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getRedundancyEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_ACCESS_REDUNDANCY,
							KEY_REDUNDANCY, bi, formContainer, "Redundans mobil", componentWrappers, PICK_ONE, "Nej", null);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				boolean visible = getVisibleBasedOnAnyEntitySelected(w, KEY_IP, true);
				boolean enabled = true;
				if ((OUTPUT_A != getOutputType(values, bi)) && (OUTPUT_D != getOutputType(values, bi))) {
					enabled = false;
					values.put(makeKey(KEY_REDUNDANCY, bi), visible ? NON_ENTITY_ID : NO_CHOICE_ENTITY_ID);
				}
				return UpdateLevel.createByVisibleAndEnabled(new VisibleAndEnabled(w), w, visible, enabled);
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Mobilnummer til SMS varsling", bundle.getSms(), KEY_SMS, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnAnyEntitySelected(w, KEY_REDUNDANCY, false));
			});
		}

		{
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap,
							getDefaultString(bundle.getSupervision()), Lists.newArrayList(YES, NO),
							KEY_SUPERVISION, bi, formContainer, "Overvågning", componentWrappers);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				// Hvis output B eller C, så ”Nej”
				boolean enabled = true;
				int outputType = getOutputType(values, bi);
				if ((outputType == OUTPUT_B) || (outputType == OUTPUT_C)) {
					enabled = false;
					values.put(makeKey(KEY_SUPERVISION, bi), NO);
				}
				return UpdateLevel.createByVisibleAndEnabled(new VisibleAndEnabled(w), w,
						getVisibleBasedOnAnyEntitySelected(w, KEY_REDUNDANCY, true), enabled);
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Kontaktperson overvågning", bundle.getContactName(), KEY_SUPERVISION_NAME, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			// Hvis nej i punkt 10, skjules feltet. Udfyldelse af feltet er obligatorisk for visning af tastebilag
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnOtherValueSelected(w, KEY_SUPERVISION, YES)));
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Kontaktperson telefonnummer", bundle.getContactPhone(), KEY_SUPERVISION_PHONE, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			// Hvis nej i punkt 10, skjules feltet. Udfyldelse af feltet er obligatorisk for visning af tastebilag
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnOtherValueSelected(w, KEY_SUPERVISION, YES)));
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Kontaktperson email", bundle.getContactEmail(), KEY_SUPERVISION_EMAIL, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			// Hvis nej i punkt 10, skjules feltet. Udfyldelse af feltet er obligatorisk for visning af tastebilag
			wrapper.setListener((WrapperUpdateListener) (target, w) ->
					UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnOtherValueSelected(w, KEY_SUPERVISION, YES)));
		}

		{
			LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = (LocationAccessComponentWrapper<BootstrapSelectSingle>)
					LocationAccessComponentWrapper.addEntityDropDownChoiceWrapper(values, labelMap,
							bundle.getServiceEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_ACCESS_SERVICE,
							KEY_SERVICE, bi, formContainer, "Servicegrad", componentWrappers, PICK_ONE, null);
			wrapper.setListener((WrapperUpdateListener) (target, w) -> {
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, getVisibleBasedOnAnyStringSelected(w, KEY_SUPERVISION));
			});
		}

		{
			LocationAccessComponentWrapper<TextField> wrapper = (LocationAccessComponentWrapper<TextField>) LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer,
					componentWrappers, "Tlf. nr. til opringning 30 minutter før teknikerbesøg", bundle.getTechPhone(), KEY_TECH_PHONE, bi);
			TextField textField = wrapper.getComponent();
			textField.add(AttributeModifier.append("placeholder", "Udfyldes når tilbud er underskrevet"));
			wrapper.setListener((WrapperUpdateListener) (target, w) -> UpdateLevel.createByVisible(new VisibleAndEnabled(w), w,
					getVisibleBasedOnAnyEntitySelected(w, KEY_SERVICE, true)));
		}

		addHardwareComponents(contract, bi, componentWrappers, formContainer, new LocationBooleanCallback() {
			@Override
			public boolean isTrue(LocationAccessComponentWrapper w) {
				return getVisibleBasedOnAnyEntitySelected(w, KEY_SERVICE, true);
			}
		});

		addInstallationComponents(contract, bi, componentWrappers, formContainer, new LocationBooleanCallback() {
			@Override
			public boolean isTrue(LocationAccessComponentWrapper w) {
				return getVisibleBasedOnAnyEntitySelected(w, KEY_SERVICE, true);
			}
		});

//		bundle.setDeviceGroup(null);
//		BusinessArea businessArea = contract.getBusinessArea();
	}

	private void modifySpeechChannelOptions(LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper, boolean lowSpeed) {
		if (lowSpeed) {
			log.info("low speed");
			Iterator iter = wrapper.getComponent().getChoices().iterator();
			while (iter.hasNext()) {
				Long entityId = (Long) iter.next();
				if ((entityId != NON_ENTITY_ID) && (entityId != NO_CHOICE_ENTITY_ID)) {
					Product product = ProductDao.lookup().findById(entityId);
					if ((product.getPublicName().toLowerCase().indexOf("qos") != -1) && (product.getPublicName().indexOf(" 5 ") == -1)) {
						log.info("Removing " + product.getPublicName());
						iter.remove();
					}
				}
			}
			Long existingValue = (Long) values.get(wrapper.getKey());
			if (!(NO_CHOICE_ENTITY_ID.equals(existingValue) || NON_ENTITY_ID.equals(existingValue))) {
				values.put(wrapper.getKey(), wrapper.getComponent().getChoices().get(wrapper.getComponent().getChoices().size()-1));
			}
		} else {
			log.info("high speed");
			for (Product product : getProductGroup(MobileSession.get().getContract().getBusinessArea(), PRODUCT_GROUP_ACCESS_QOS).getProducts()) {
				if (!wrapper.getComponent().getChoices().contains(product.getId())) {
					wrapper.getComponent().getChoices().add(product.getId());
				}
			}
		}
	}

	private boolean isLowSpeed(int bi) {
		Product product = getProductFromValues(KEY_SPEED, bi);
		return (product == null ? false : ((product.getPublicName().indexOf("10/1") != -1) || (product.getPublicName().indexOf("20/2") != -1)));
	}

	@Override
	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page,
								int tabIndex, AjaxRequestTarget target) {
		if (!super.saveAndNavigate(contract, page, tabIndex, target)) {
			return false;
		}

		int bi = tabIndex;

		// ---------------------------------
		// Transfer values to bundles
		// ---------------------------------
		List<OneXdslBundleData> bundles = contract.getOneXdslBundles();
		OneXdslBundleData bundle = bundles.get(bi);
		List<LocationBundleData> locationBundles = contract.getLocationBundles();
		LocationBundleData locationBundle = locationBundles.get(bi);

		bundle.setMode(OneXdslBundleData.getModeAsCode((String) values.get(makeKey(KEY_MODE, bi))));
		bundle.setLineNo((String) values.get(makeKey(KEY_LINE_NO, bi)));
		bundle.setSpeedEntityId((Long) values.get(makeKey(KEY_SPEED, bi)));
		bundle.setSpeechChannelEntityId((Long) values.get(makeKey(KEY_SPEECH_CHANNELS, bi)));
		bundle.setRouterFirewallRule(OneXdslBundleData.getRouterFirewallAsCode((String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi))));
		bundle.setReuseHardware(getTriStateBooleanValue(KEY_REUSE_HW, bi));
		bundle.setIpEntityId((Long) values.get(makeKey(KEY_IP, bi)));
		bundle.setRedundancyEntityId((Long) values.get(makeKey(KEY_REDUNDANCY, bi)));
		bundle.setSms((String) values.get(makeKey(KEY_SMS, bi)));
		bundle.setSupervision(getTriStateBooleanValue(KEY_SUPERVISION, bi));
		bundle.setContactName((String) values.get(makeKey(KEY_SUPERVISION_NAME, bi)));
		bundle.setContactEmail((String) values.get(makeKey(KEY_SUPERVISION_EMAIL, bi)));
		bundle.setContactPhone((String) values.get(makeKey(KEY_SUPERVISION_PHONE, bi)));
		bundle.setServiceEntityId((Long) values.get(makeKey(KEY_SERVICE, bi)));
		bundle.setTechPhone((String) values.get(makeKey(KEY_TECH_PHONE, bi)));

		locationBundle.setInstallationProvider(LocationBundleData.getInstallationProviderAsCode((String) values.get(makeKey(KEY_INSTALLATION_PROVIDER, bi))));
		locationBundle.setHardwareProvider(LocationBundleData.getHardwareProviderAsCode((String) values.get(makeKey(KEY_HARDWARE_PROVIDER, bi))));

		// ---------------------------------
		// Check if CDM output is allowed
		// ---------------------------------
		locationBundle.setCdmOk(true);
		updateCdmOkBasedOnRequiredTextFields(locationBundle, KEY_SMS, KEY_SUPERVISION_NAME, KEY_SUPERVISION_EMAIL, KEY_SUPERVISION_PHONE, KEY_TECH_PHONE);
		if (bundle.getMode() != MODE_NYSALG) {
			updateCdmOkBasedOnRequiredTextFields(locationBundle, KEY_LINE_NO);
		}

		// Save changes
		contract.setOneXdslBundles(bundles);
		bundles = contract.getOneXdslBundles();
		contract.setLocationBundles(locationBundles);
		locationBundles = contract.getLocationBundles();

		// Get bundle again (unnecessary?)
		bundle 			= bundles.get(bi);
		locationBundle 	= locationBundles.get(bi);

		// ---------------------------------
		// Validate data
		// ---------------------------------
		{
			boolean validationProblems = false;
			for (String key: new String[] {KEY_SPEED, KEY_SPEECH_CHANNELS, KEY_ROUTER_FIREWALL, KEY_REUSE_HW, KEY_IP, KEY_REDUNDANCY, KEY_SERVICE}) {
				LocationAccessComponentWrapper wrapper = getWrapper(key);
				if (wrapper.getComponent().getParent().getParent().isVisible()
						&& wrapper.getComponent().isEnabled()
						&& ((values.get(wrapper.getKey()) == null) || NO_CHOICE_ENTITY_ID.equals(values.get(wrapper.getKey())))) {
					wrapper.getComponent().error("Feltet skal udfyldes");
					target.add(wrapper.getParent());
					validationProblems = true;
					log.info("Missing value in " + wrapper.getKey());
				}
			}
			if (validationProblems) {
				return false;
			}
		}

		// ---------------------------------
		// Create orderlines
		// ---------------------------------
		Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
		Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();

		addProductsFromEntityFields(contract, productToCountMap, bi,
				new String[] {KEY_SPEED, KEY_SPEECH_CHANNELS, KEY_REDUNDANCY, KEY_SERVICE});

		{
			// ALWAYS INCLUDED
			for (Product product: getProductGroup(contract.getBusinessArea(), PRODUCT_GROUP_ACCESS_INCLUDED).getProducts()) {
				addProductsForBundle(contract, productToCountMap, bi, product.getProductId());
			}
		}

		if (Boolean.TRUE.equals(bundle.getReuseHardware())) {
			addProductsForBundle(contract, productToCountMap, bi, "82 496 00_1");
		}

		{
			// Redundancy
			Product product = getProductFromValues(KEY_REDUNDANCY, bi);
			if ((product != null) && (product.getPublicName().indexOf("ekstra") != -1)) {
				addProductsForBundle(contract, productToCountMap, bi, "3242201");
			}
		}

		{
			// LAN IPs
			if (TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals((String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi)))) {
				Long lanIpEntityId = (Long) values.get(makeKey(KEY_IP, bi));
				if (Objects.equals(lanIpEntityId, NO_CHOICE_ENTITY_ID)) {
					addProductsForBundle(contract, productToCountMap, bi, "44 012 51");	// 4 LAN IPs
				} else if (Objects.equals(lanIpEntityId, NON_ENTITY_ID)) {	// Nej
					addProductsForBundle(contract, productToCountMap, bi, "44 012 51");	// 4 LAN IPs
				} else {
					Product lanIpProduct = ProductDao.lookup().findById(lanIpEntityId);
					if (lanIpProduct.getProductId().equals("44 012 51")) { // 4 LAN IPs
						addProductsForBundle(contract, productToCountMap, bi, "44 012 51");	// 4 LAN IPs
						addProductsForBundle(contract, productToCountMap, bi, "44 012 19");	// 4 extra LAN IPs
					} else if (lanIpProduct.getProductId().equals("44 012 52")) { // 8 LAN IPs
						addProductsForBundle(contract, productToCountMap, bi, "44 012 51");	// 4 LAN IPs
						addProductsForBundle(contract, productToCountMap, bi, "44 012 52");	// 8 LAN IPs
					}
				}
			}
		}

		if (Boolean.TRUE.equals(bundle.getSupervision())) {
			addProductsForBundle(contract, productToCountMap, bi, "4401934");
		}

		if (bundle.getMode() == MODE_NYSALG) {
			addProductsForBundle(contract, productToCountMap, bi, "99 999 01", "82 496 00");
		}

		//		for (MobileProductGroupEnum groupType: new MobileProductGroupEnum[] {PRODUCT_GROUP_XDSL_BUNDLE, PRODUCT_GROUP_ACCESS}) {
//			MobileProductGroup parentGroup = getProductGroup(contract.getBusinessArea(), groupType);
//			for (ProductGroup group: parentGroup.getChildProductGroups()) {
//				addProductsForBundle(contract, productToCountMap, bi, product.getProductId(), "5360700", "7622704");
//			}
//		}

		// Router/Firewall regler
		switch (getOutputType(values, bi)) {
//		switch (getOutputType(bundle, contract, productToCountMap, bi)) {
			case OUTPUT_A:
				addProductsForBundle(contract, productToCountMap, bi, "53 607 00", "53 967", "69 053 00", "99 914 00");
				break;
			case OUTPUT_B:
				addProductsForBundle(contract, productToCountMap, bi, "53 607 00", "53 780");
				break;
			case OUTPUT_C:
				addProductsForBundle(contract, productToCountMap, bi, "53 609 00", "53 780");
				break;
			case OUTPUT_D:
				addProductsForBundle(contract, productToCountMap, bi, "53 607 00", "69 161 00", "69 059 26", "69 725 00", "07 147 00", "69 312 00", "06 792 00", "53 967", "76 227 04");
				break;
			case OUTPUT_E:
				addProductsForBundle(contract, productToCountMap, bi, "53 607 00", "69 169 00", "53 790", "76 221 01", "89 629 00", "76 227 04");
				break;
			case OUTPUT_F:
				addProductsForBundle(contract, productToCountMap, bi, "53 607 00", "76 222 01", "69 169 00", "53 790", "76 227 04");
				break;
		}

//			if (bundleData.getSpeedEntityId() != null) {
//				ProductBundle productBundle = null;
//				for (ProductBundle bundle : contract.getCampaigns().get(0).getProductBundles()) {
//					if (bundle.hasRelationToProduct(bundleData.getSpeedEntityId())) {
//						productBundle = bundle;
//						break;
//					}
//				}
//				if (productBundle != null) {
//					// XDSL Product Bundle
//					BundleCount bundleCount =  bundleToCountMap.get((MobileProductBundle) productBundle);
//					if (bundleCount == null) {
//						int subIndex = 0;
//						bundleCount = new BundleCount((MobileProductBundle) productBundle, subIndex, 1, 0);
//						bundleToCountMap.put((MobileProductBundle) productBundle, bundleCount);
//					} else {
//						bundleCount.setCountNew(bundleCount.getCountNew() + 1);
//					}
//
//					// Speed
//					// addProduct(productToCountMap, productDao.findById(bundleIds.getSpeedEntityId()));
//				}
//			}
//		}

//		contract.adjustOrderLinesForBundles(bundleToCountMap, true, MobileProductBundleEnum.XDSL_BUNDLE);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED), productToCountMap, bi);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MISC), productToCountMap, bi);
//		contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION), productToCountMap, bi);

		{
			MobileProductGroup parentGroup = getProductGroup(contract.getBusinessArea(), PRODUCT_GROUP_XDSL_BUNDLE);
			for (ProductGroup group: parentGroup.getChildProductGroups()) {
				contract.adjustOrderLinesForProducts(group, productToCountMap, bi);
			}
		}

		{
			MobileProductGroup parentGroup = getProductGroup(contract.getBusinessArea(), PRODUCT_GROUP_ACCESS);
			for (ProductGroup group: parentGroup.getChildProductGroups()) {
				contract.adjustOrderLinesForProducts(group, productToCountMap, bi);
			}
		}

		for (OrderLine orderLine : contract.getOrderLines()) {
			orderLineDao.save(orderLine);
		}

		contractSaver.save(contract);

		if (page != null) {
			setResponsePage(page);
		}
		return true;
	}

	private int getOutputType(ValueMap values, int bi) {
		Long speechChannelEntityId 	= (Long) values.get(makeKey(KEY_SPEECH_CHANNELS, bi));
		String routerFirewall 		= (String) values.get(makeKey(KEY_ROUTER_FIREWALL, bi));
		if (routerFirewall != null) {
			if (NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_TDC_DHCP))) {
				return OUTPUT_A;
			} else if (NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP))) {
				return OUTPUT_B;
			} else if (NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP))) {
				return OUTPUT_C;
			} else if (!NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_TDC_DHCP))) {
				return OUTPUT_D;
			} else if (!NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP))) {
				return OUTPUT_E;
			} else if (!NON_ENTITY_ID.equals(speechChannelEntityId) && (routerFirewall.equals(TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP))) {
				return OUTPUT_F;
			}
		}
		return -1;
	}
}
