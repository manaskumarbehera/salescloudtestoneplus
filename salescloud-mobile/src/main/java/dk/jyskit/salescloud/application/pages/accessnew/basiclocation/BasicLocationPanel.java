package dk.jyskit.salescloud.application.pages.accessnew.basiclocation;

import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import dk.jyskit.salescloud.application.editors.simpleproductcount.SimpleProductCount;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.*;
import dk.jyskit.salescloud.application.pages.accessnew.locations.LocationsPage;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.utils.ExceptionUtils;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import java.util.*;
import java.util.function.Predicate;

import static dk.jyskit.salescloud.application.model.AccessConstants.NON_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.LocationBundleData.*;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.pages.accessnew.locationaccess.LocationAccessComponentWrapper.makeKey;

@SuppressWarnings("serial")
@Slf4j
public class BasicLocationPanel extends AbstractLocationAccessPanel implements IEventSink {
	protected static final String YES 			= "Ja";
	protected static final String YES_STANDARD 	= "Ja, standard";
	protected static final String YES_CUSTOM 	= "Ja, kundetilpasset";
	protected static final String NO 			= "Nej";

	protected static final String KEY_ADDRESS_ROAD 			= "_ar";
	protected static final String KEY_ADDRESS_ZIPCODE 		= "_az";
	protected static final String KEY_ADDRESS_CITY 			= "_ac";
	protected static final String KEY_INSTALLATION_PROVIDER	= "inp";
	protected static final String KEY_HARDWARE_PROVIDER		= "hap";
	public static final String FORSENDELSESGEBYR_ID = "8222500";

	@Inject
	private PageNavigator navigator;

	@Inject
	private ContractSaver contractSaver;

	@Inject
	private ProductDao productDao;

	private BiMap<String, String> keyToProductId;

//	private Set<Integer> oneTimeRuleSet = new HashSet<>();

	public BasicLocationPanel(String id, Integer tabIndex) {
		super(id, tabIndex);
	}

	@Override
	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
		contract.getLocationBundle(bi);  // Make sure index exists
		List<LocationBundleData> locationBundles = contract.getLocationBundles();
		LocationBundleData bundleData = locationBundles.get(bi);

		if (bi == 0) {
			if (StringUtils.isEmpty(bundleData.getAddressRoad())) {
				bundleData.setAddressRoad(contract.getCustomer().getAddress());
			}
			if (StringUtils.isEmpty(bundleData.getAddressZipCode())) {
				bundleData.setAddressZipCode(contract.getCustomer().getZipCode());
			}
			if (StringUtils.isEmpty(bundleData.getAddressCity())) {
				bundleData.setAddressCity(contract.getCustomer().getCity());
			}
		}

		contract.setLocationBundles(locationBundles);
	}

	@Override
	protected void addAllComponents(final MobileContract contract,
			Map<String, String> labelMap, ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		final LocationBundleData bundle = contract.getLocationBundle(bi);

		componentWrappers.add(LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer, componentWrappers,
				"Adresse", bundle.getAddressRoad(), KEY_ADDRESS_ROAD, bi));
		componentWrappers.add(LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer, componentWrappers,
				"Postnr.", bundle.getAddressZipCode(), KEY_ADDRESS_ZIPCODE, bi));
		componentWrappers.add(LocationAccessComponentWrapper.addTextFieldWrapper(values, labelMap, formContainer, componentWrappers,
				"By", bundle.getAddressCity(), KEY_ADDRESS_CITY, bi));

		addComponents(contract, labelMap, formContainer, componentWrappers, bi);
	}

	protected void addHardwareComponents(MobileContract contract, int bi, List<LocationAccessComponentWrapper> componentWrappers,
										 ComponentContainerPanel<ValueMap> formContainer, LocationBooleanCallback listVisibleCallback) {
		FormGroup headerFormGroup = ((Jsr303Form) formContainer).createGroup(Model.of("Hardware"));
		headerFormGroup.setOutputMarkupId(true);
		headerFormGroup.setOutputMarkupPlaceholderTag(true);
		FormGroup[] headerFormGroups = new FormGroup[3];
		{
			final LocationBundleData bundle = contract.getLocationBundle(bi);
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, LocationBundleData.getHardwareProviderAsString(bundle.getHardwareProvider()),
							MobileSession.get().userIsPartner()
							? Lists.newArrayList(TEXT_HARDWARE_NONE, TEXT_HARDWARE_TDC, TEXT_HARDWARE_PARTNER)
							: Lists.newArrayList(TEXT_HARDWARE_NONE, TEXT_HARDWARE_TDC),
							KEY_HARDWARE_PROVIDER, bi, formContainer, "Leverandør", componentWrappers);
			wrapper.setListener((LocationAccessComponentWrapper.WrapperUpdateListener) (target, w) -> {
				VisibleAndEnabled oldVisibleAndEnabled = new VisibleAndEnabled(w);
				boolean newVisible = listVisibleCallback.isTrue(w);
				if (oldVisibleAndEnabled.isVisible() != newVisible) {
					headerFormGroup.setVisible(newVisible);
					if (target != null) {
						target.add(headerFormGroup);
					}
				}
				return UpdateLevel.createByVisible(oldVisibleAndEnabled, w, newVisible);
			});
		}

		for (MobileProductGroupEnum groupType: new MobileProductGroupEnum[]
				{PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES, PRODUCT_GROUP_LOCATIONS_HARDWARE_IP, PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC}) {

			if (PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.equals(groupType)) {
				headerFormGroups[0] = ((Jsr303Form) formContainer).createGroup(Model.of("Switch"));
				headerFormGroups[0].setOutputMarkupId(true);
				headerFormGroups[0].setOutputMarkupPlaceholderTag(true);
				headerFormGroups[0].setVisible(false);
				headerFormGroups[0].add(AttributeModifier.append("class", "smaller"));
			} else if (PRODUCT_GROUP_LOCATIONS_HARDWARE_IP.equals(groupType)) {
				headerFormGroups[1] = ((Jsr303Form) formContainer).createGroup(Model.of("IP terminaler"));
				headerFormGroups[1].setOutputMarkupId(true);
				headerFormGroups[1].setOutputMarkupPlaceholderTag(true);
				headerFormGroups[1].setVisible(false);
				headerFormGroups[1].add(AttributeModifier.append("class", "smaller"));
			} else if (PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC.equals(groupType)) {
				headerFormGroups[2] = ((Jsr303Form) formContainer).createGroup(Model.of("Tilbehør"));
				headerFormGroups[2].setOutputMarkupId(true);
				headerFormGroups[2].setOutputMarkupPlaceholderTag(true);
				headerFormGroups[2].setVisible(false);
				headerFormGroups[2].add(AttributeModifier.append("class", "smaller"));
			}

			for (MobileProduct product : groupType.getProducts(MobileSession.get().getBusinessArea())) {
				ProductCountAndInstallation productRow = new ProductCountAndInstallation(product, null,
						contract.getCountNewForProduct(product, bi), contract.getCountExistingForProduct(product, bi));
				LocationAccessComponentWrapper wrapper = LocationAccessComponentWrapper.addSelectProductCountEditorWrapper(values, labelMap, productRow,
						String.valueOf(product.getId()), bi, formContainer, product.getPublicName(), componentWrappers);
				componentWrappers.add(wrapper);

				try {
					// log.info("PUTTING " + product.getPublicName() + " - " + product.getProductId());
					getKeyToProductId().put(wrapper.getKey(), product.getProductId());
				} catch (Exception e) {
					log.info("failed");
				}

				String productId = keyToProductId.get(wrapper.getKey());
				for (String id : new String[] {"Switch_fravalgt", FORSENDELSESGEBYR_ID}) {
					if (StringUtils.equals(productId, id)) {
						wrapper.getComponent().setEnabled(false);
					}
				}

				wrapper.setListener((LocationAccessComponentWrapper.WrapperUpdateListener) (target, w) -> {
					boolean updated = false;
					boolean switchSelected	= sumCountNewOf(PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES, new Predicate<Product>() {
						@Override
						public boolean test(Product product) {
							return (product.getPublicName().toLowerCase().indexOf("fravalgt") == -1);
						}
					}) > 0;
					int sumOfIpTerminals	= getSumOfIpTerminals();
					int sumOfMiscTerminals	= sumCountNewOf(PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC, new Predicate<Product>() {
						@Override
						public boolean test(Product product) {
							return (product.getPublicName().toLowerCase().indexOf("forsendelse") == -1);
						}
					});

					ProductCountAndInstallation oldValue = (ProductCountAndInstallation) values.get(wrapper.getKey());
					ProductCountAndInstallation newValue = oldValue.clone();

					if ("Switch_fravalgt".equals(productId)) {
						newValue.setCountNew(switchSelected ? 0 : 1);
					} else if ("4400213".equals(productId)) {
						newValue.setCountNew(switchSelected ? 0 : sumOfIpTerminals);
					} else if ("4400254".equals(productId)) {
						newValue.setCountNew(sumOfIpTerminals);
					} else if (FORSENDELSESGEBYR_ID.equals(productId)) {
						int count = 0;
						if (TEXT_HARDWARE_PARTNER.equals(values.get(makeKey(KEY_HARDWARE_PROVIDER, bi)))) {
							// Forsendelsesgebyr udløses KUN når der skal sendes en switch, da dette foretages
							// af TDC, ikke EC
							if (switchSelected) {
								count = 1;
							}
						} else {
							if ((sumOfIpTerminals > 0) || (sumOfMiscTerminals > 0) || switchSelected) {
								count = 1;
							}
						}
						newValue.setCountNew(count);
					}
					if (!Objects.equals(oldValue.getCountNew(), newValue.getCountNew())) {
						values.put(wrapper.getKey(), newValue);
						wrapper.getComponent().modelChanged();
						updated = true;
					}
					boolean visible = getVisibleBasedOnOtherValueNotSelected(w, KEY_HARDWARE_PROVIDER, TEXT_HARDWARE_NONE);
					{
						boolean oldSubHeadersVisible = headerFormGroups[0].isVisible();
						boolean newSubHeadersVisible = false;
						if (visible && !TEXT_HARDWARE_NONE.equals(values.get(wrapper.getKey()))) {
							newSubHeadersVisible = true;
						}
						if (oldSubHeadersVisible != newSubHeadersVisible) {
							for (FormGroup fg : headerFormGroups) {
								fg.setVisible(newSubHeadersVisible);
								if (target != null) {
									target.add(fg);
								}
							}
						}
					}
					return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, visible).setUpdateComponent(updated);
				});
			}
		}
	}

	private int getSumOfIpTerminals() {
		return sumCountNewOf(PRODUCT_GROUP_LOCATIONS_HARDWARE_IP, new Predicate<Product>() {
			@Override
			public boolean test(Product product) {
				return true;
			}
		});
	}

	protected void addInstallationComponents(MobileContract contract, int bi, List<LocationAccessComponentWrapper> componentWrappers,
											 ComponentContainerPanel<ValueMap> formContainer, LocationBooleanCallback listVisibleCallback) {
		FormGroup headerFormGroup = ((Jsr303Form) formContainer).createGroup(Model.of("Installation"));
		headerFormGroup.setOutputMarkupId(true);
		headerFormGroup.setOutputMarkupPlaceholderTag(true);
		{
			final LocationBundleData bundle = contract.getLocationBundle(bi);
			LocationAccessComponentWrapper<DropDownChoice> wrapper = (LocationAccessComponentWrapper<DropDownChoice>)
					LocationAccessComponentWrapper.addStringDropDownChoiceWrapper(values, labelMap, LocationBundleData.getInstallationProviderAsString(bundle.getInstallationProvider()),
							MobileSession.get().userIsPartner()
									? Lists.newArrayList(TEXT_INSTALLATION_NONE, TEXT_INSTALLATION_TDC, TEXT_INSTALLATION_PARTNER)
									: Lists.newArrayList(TEXT_INSTALLATION_NONE, TEXT_INSTALLATION_TDC),
							KEY_INSTALLATION_PROVIDER, bi, formContainer, "Leverandør", componentWrappers);
			wrapper.setListener((LocationAccessComponentWrapper.WrapperUpdateListener) (target, w) -> {
				VisibleAndEnabled oldVisibleAndEnabled = new VisibleAndEnabled(w);
				boolean newVisible = listVisibleCallback.isTrue(w);
				if (oldVisibleAndEnabled.isVisible() != newVisible) {
					headerFormGroup.setVisible(newVisible);
					if (target != null) {
						target.add(headerFormGroup);
					}
				}
				return UpdateLevel.createByVisible(oldVisibleAndEnabled, w, newVisible);
			});
			wrapper.setSelfUpdateListener((LocationAccessComponentWrapper.WrapperUpdateListener) (target, w) -> {
				if (!TEXT_INSTALLATION_NONE.equals(values.get(KEY_INSTALLATION_PROVIDER))) {
					String opstartOgKoersalId = "4401533";
					Product p = productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), opstartOgKoersalId);
					String key = getKeyToProductId().inverse().get(opstartOgKoersalId);
					if ((key != null) && (p != null)) {
						values.put(key, new SimpleProductCount(p, Integer.valueOf(1)));
						getWrapperByKey(key).getComponent().modelChanged();
						if (target != null) {
							target.add(getWrapperByKey(key).getParent());
						}
					}
				}
				return null;
			});
		}

		for (MobileProduct product : MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS_INSTALLATION.getProducts(MobileSession.get().getBusinessArea())) {
			SimpleProductCount productRow = new SimpleProductCount(product, contract.getCountNewForProduct(product, bi));
			LocationAccessComponentWrapper wrapper = LocationAccessComponentWrapper.addSimpleProductCountEditorWrapper(values, labelMap, productRow,
					String.valueOf(product.getId()), bi, formContainer, product.getPublicName(), componentWrappers);
			componentWrappers.add(wrapper);

			try {
				// log.info("Putting " + product.getPublicName() + " - " + product.getProductId());
				getKeyToProductId().put(wrapper.getKey(), product.getProductId());
			} catch (Exception e) {
				log.info("failed");
			}

			String productId = keyToProductId.get(wrapper.getKey());
			for (String id : new String[] {"Tjek_fravalgt"}) {
				if (StringUtils.equals(productId, id)) {
					wrapper.getComponent().setEnabled(false);
				}
			}

			wrapper.setListener((LocationAccessComponentWrapper.WrapperUpdateListener) (target, w) -> {
				boolean updated = false;
				boolean networkCheckSelected = sumCountNewOf("4401519", "4401520", "4401528") > 0;

				int sumOfIpTerminals = getSumOfIpTerminals();

				SimpleProductCount oldValue = (SimpleProductCount) values.get(wrapper.getKey());
				SimpleProductCount newValue = oldValue.clone();

				boolean visible 	= getVisibleBasedOnOtherValueNotSelected(w, KEY_INSTALLATION_PROVIDER, TEXT_INSTALLATION_NONE);
				boolean tdcSelected	= getVisibleBasedOnOtherValueSelected(w, KEY_INSTALLATION_PROVIDER, TEXT_INSTALLATION_TDC);

				if (visible) {
					if (countTotalFromBundleOrderLine("3235012") > 0) {
						// Standard omstilling
						if ("4401519".equals(productId)) {
//							if (!oneTimeRuleSet.contains(Integer.valueOf(1))) {
//								newValue.setCountNew(1);
//								oneTimeRuleSet.add(Integer.valueOf(1));
//							}
						}
						if ("4401520".equals(productId) || "4401528".equals(productId)) {
							newValue.setCountNew(0);
							visible = false;
						}
					} else if (countTotalFromBundleOrderLine("3235013") > 0) {
						// Professionel omstilling
						if ("4401520".equals(productId)) {
//							if (!oneTimeRuleSet.contains(Integer.valueOf(1))) {
//								newValue.setCountNew(1);
//								oneTimeRuleSet.add(Integer.valueOf(1));
//							}
						}
						if ("4401519".equals(productId) || "4401528".equals(productId)) {
							newValue.setCountNew(0);
							visible = false;
						}
					} else if (countTotalFromBundleOrderLine("3235014") > 0) {
						// Premium omstilling
						if ("4401528".equals(productId)) {
//							if (!oneTimeRuleSet.contains(Integer.valueOf(1))) {
//								newValue.setCountNew(1);
//								oneTimeRuleSet.add(Integer.valueOf(1));
//							}
						}
						if ("4401519".equals(productId) || "4401520".equals(productId)) {
							newValue.setCountNew(0);
							visible = false;
						}
					} else {
						// Mobile only
						if ("4401519".equals(productId) || "4401520".equals(productId) || "4401528".equals(productId)) {
							newValue.setCountNew(0);
							visible = false;
						}
					}
				}

				if (countNewFromProductOrderLine("32 223 16") > 0) {
					// Ops. U-Receptionst
					if ("44 015 34".equals(productId)) {
						newValue.setCountNew(1);
					}
				}

				if ("Tjek_fravalgt".equals(productId)) {
					newValue.setCountNew(networkCheckSelected ? 0 : 1);
				} else if ("4401533".equals(productId)) {
					if (sumCountNewOf("4401509") > 0) {
						newValue.setCountNew(0);
					}
				} else if ("4401509".equals(productId)) {
					if (sumCountNewOf("4401533") > 0) {
						newValue.setCountNew(0);
					}
				} else if ("4401515".equals(productId)) {
					newValue.setCountNew(sumCountNewOf("4401521", "4401522", "4401523"));
				} else if ("4401516".equals(productId)) {
					newValue.setCountNew(sumCountNewOf("4400244"));
				} else if ("_4401523".equals(productId) || "4401524".equals(productId) || "4401525".equals(productId)) {
					// Konfiguration af ekstra Call Center
					// Konfiguration af ekstra Menuvalg
					// Opsætning af communicator
					if (tdcSelected) {
						newValue.setCountNew(0);
						visible = false;
					}
				}

				if (!Objects.equals(oldValue.getCountNew(), newValue.getCountNew())) {
					values.put(wrapper.getKey(), newValue);
					wrapper.getComponent().modelChanged();
					updated = true;
				}
				return UpdateLevel.createByVisible(new VisibleAndEnabled(w), w, visible).setUpdateComponent(updated);
			});
		}
	}

	protected void addComponents(final MobileContract contract, Map<String, String> labelMap,
								 ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> componentWrappers, int bi) {
		// Mobile Only
		addHardwareComponents(contract, bi, componentWrappers, formContainer, new LocationBooleanCallback() {
			@Override
			public boolean isTrue(LocationAccessComponentWrapper w) {
				return getVisibleBasedOnAddressFields(w);
			}
		});
		addInstallationComponents(contract, bi, componentWrappers, formContainer, new LocationBooleanCallback() {
			@Override
			public boolean isTrue(LocationAccessComponentWrapper w) {
				return getVisibleBasedOnAddressFields(w);
			}
		});
	}

	private int sumCountNewOf(String ... productIds) {
		int sum = 0;
		for (String productId : productIds) {
			sum += countNewOf(productId);
		}
		return sum;
	}

	private int sumCountNewOf(MobileProductGroupEnum group, Predicate<Product> productPredicate) {
		int sum = 0;
		for (MobileProduct product : group.getProducts(MobileSession.get().getBusinessArea())) {
			if (productPredicate.test(product)) {
				sum += countNewOf(product.getProductId());
			}
		}
		return sum;
	}

	private int countNewOf(String productId) {
		Object value = values.get(keyToProductId.inverse().get(productId));
		if (value == null) {
			throw new IllegalArgumentException("Not found: " + productId);
		}
		if (value instanceof ProductCountAndInstallation) {
			ProductCountAndInstallation productCountAndInstallation = (ProductCountAndInstallation) values.get(keyToProductId.inverse().get(productId));
			return productCountAndInstallation.getCountNew().intValue();
		} else if (value instanceof SimpleProductCount) {
			SimpleProductCount simpleProductCount = (SimpleProductCount) values.get(keyToProductId.inverse().get(productId));
			return simpleProductCount.getCountNew();
		}
		throw new IllegalArgumentException("Unexpected type of value: " + productId);
	}

	private int countNewFromBundleOrderLine(String productId) {
		for (OrderLine orderLine: MobileSession.get().getContract().getBundleOrderLines()) {
			if (orderLine.getBundle() != null) {
				for (BundleProductRelation productRelation: orderLine.getBundle().getProducts()) {
					if (Objects.equals(productId, productRelation.getProduct().getProductId())) {
						return orderLine.getCountNew();
					}
				}
			}
		}
		return 0;
	}

	private int countTotalFromBundleOrderLine(String productId) {
		for (OrderLine orderLine: MobileSession.get().getContract().getBundleOrderLines()) {
			if (orderLine.getBundle() != null) {
				for (BundleProductRelation productRelation: orderLine.getBundle().getProducts()) {
					if (Objects.equals(productId, productRelation.getProduct().getProductId())) {
						return orderLine.getTotalCount();
					}
				}
			}
		}
		return 0;
	}

	private int countNewFromProductOrderLine(String productId) {
		for (OrderLine orderLine: MobileSession.get().getContract().getProductOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (Objects.equals(productId, orderLine.getProduct().getProductId())) {
					return orderLine.getCountNew();
				}
			}
		}
		return 0;
	}

	@Override
	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page, int tabIndex,
								AjaxRequestTarget target) {
		try {
			int bi = tabIndex;

			// ---------------------------------
			// Transfer values to bundles
			// ---------------------------------
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();

			List<LocationBundleData> bundles = contract.getLocationBundles();
			LocationBundleData bundle = bundles.get(bi);
			bundle.setAddressRoad((String) values.get(makeKey(KEY_ADDRESS_ROAD, bi)));
			bundle.setAddressZipCode((String) values.get(makeKey(KEY_ADDRESS_ZIPCODE, bi)));
			bundle.setAddressCity((String) values.get(makeKey(KEY_ADDRESS_CITY, bi)));

			bundle.setInstallationProvider(LocationBundleData.getInstallationProviderAsCode((String) values.get(makeKey(KEY_INSTALLATION_PROVIDER, bi))));
			bundle.setHardwareProvider(LocationBundleData.getHardwareProviderAsCode((String) values.get(makeKey(KEY_HARDWARE_PROVIDER, bi))));

			for (MobileProductGroupEnum groupType: new MobileProductGroupEnum[]
					{PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES, PRODUCT_GROUP_LOCATIONS_HARDWARE_IP, PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC}) {
				for (MobileProduct product : groupType.getProducts(MobileSession.get().getBusinessArea())) {
					ProductCountAndInstallation productRow = (ProductCountAndInstallation) values.get(makeKey(product.getId(), bi));
					if (productRow != null) {
						addToProductToCountsMap(productToCountMap, ProductDao.lookup().findById(product.getId()), productRow.getCountNew(), productRow.getCountExisting(), bi);
					}
				}
			}
			for (MobileProduct product : MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS_INSTALLATION.getProducts(MobileSession.get().getBusinessArea())) {
				SimpleProductCount productRow = (SimpleProductCount) values.get(makeKey(product.getId(), bi));
				if (productRow != null) {
					addToProductToCountsMap(productToCountMap, ProductDao.lookup().findById(product.getId()), productRow.getCountNew(), 0, bi);
				}
			}
			contract.setLocationBundles(bundles);

			// ---------------------------------
			// Check if CDM output is allowed
			// ---------------------------------
			bundle.setCdmOk(true);

			// ---------------------------------
			// Save changes
			// ---------------------------------
			contract.setLocationBundles(bundles);
			bundles = contract.getLocationBundles();

			// Get bundle again (unnecessary?)
			bundle = bundles.get(bi);

			// ---------------------------------
			// Validate data
			// ---------------------------------
			{
				boolean validationProblems = false;

				int sumOfSwitchesAndIpTerminals = sumCountNewOf("4401642", "4401641", "4400244", "4401521", "4401522", "4401523");
				{
					LocationAccessComponentWrapper wrapper = getWrapper(KEY_INSTALLATION_PROVIDER);
					if ((sumOfSwitchesAndIpTerminals > 0) && (TEXT_INSTALLATION_NONE.equals((String) values.get(wrapper.getKey())))) {
						wrapper.getComponent().error("Der er valgt udstyr der kræver installation, konfigurer installation");
						if (target != null) {
							target.add(wrapper.getParent());
						}
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
			for (MobileProductGroupEnum type: new MobileProductGroupEnum[] {PRODUCT_GROUP_LOCATIONS,
					PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES, PRODUCT_GROUP_LOCATIONS_HARDWARE_IP,
					PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC, PRODUCT_GROUP_LOCATIONS_INSTALLATION}) {
				contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), type), productToCountMap, bi);
			}

//			for (OrderLine orderLine : contract.getOrderLines()) {
//				System.out.println(orderLine.toString() + " - " + orderLine.getCountNew());
//			}

			for (OrderLine orderLine : contract.getOrderLines()) {
				OrderLineDao.lookup().save(orderLine);
			}

//			contractSaver.save(contract);
//
//			if (page != null) {
//				setResponsePage(page);
//			}
		} catch (Exception e) {
			ExceptionUtils.handleException(e);
		}
		return true;
	}

	protected void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountsMap, Product product, Integer countNew, Integer countExisting, Integer subIndex) {
		if (product == null) {
			log.warn("Trying to add unknown product!");
		} else {
			((MobileProduct) product).addToProductToCountsMap(productToCountsMap, countNew, countExisting, subIndex);
		}
	}

	protected void addProductsForBundle(MobileContract contract, Map<Product, List<CountAndInstallation>> productToCountMap,
										int subIndex, String ... productIds) {
		for (String productId : productIds) {
			Product p = productDao.findByBusinessAreaAndProductId(contract.getBusinessArea().getId(), productId);
			if (p == null) {
				log.error("Product not found: " + productId);
			} else {
				addToProductToCountsMap(productToCountMap, p, Integer.valueOf(1), Integer.valueOf(0), subIndex);
			}
		}
	}

	private BiMap<String, String> getKeyToProductId() {
		if (keyToProductId == null) {
			keyToProductId = HashBiMap.create();
		}
		return keyToProductId;
	}

//	@Override
//	public void onEvent(IEvent<?> event) {
//		if (event.getPayload() instanceof ValueChangedEvent) {
//			ValueChangedEvent e = (ValueChangedEvent) event.getPayload();
//			log.info(e.toString());
//		}
//	}

	@Override
	protected void onDelete(MobileContract contract, int bi) {
		contract.removeLocation(bi);

		try {
			// Adjust order lines
			Iterator<OrderLine> iter = contract.getOrderLines().iterator();
			while (iter.hasNext()) {
				OrderLine orderLine = iter.next();
				if (orderLine.getSubIndex() != null) {
					if (orderLine.getSubIndex().intValue() == bi) {
						iter.remove();
					} else if (orderLine.getSubIndex().intValue() > bi) {
						orderLine.setSubIndex(orderLine.getSubIndex() - 1);
					}
				}
			}
			contractSaver.save(contract);
			setResponsePage(LocationsPage.class);
		} catch (Exception e) {
			ExceptionUtils.handleException(e);
		}
	}

	protected Long getEntityId(ValueMap values, String key) {
		if (NO.equals(values.get(key))) {
			return null;
		} else {
			return (Long) values.get(key);
		}
	}

	protected void sortOptions(List options, boolean noEntityFirst) {
		Collections.sort(options, new Comparator<Long>() {
			@Override
			public int compare(Long a, Long b) {
				if (NO_CHOICE_ENTITY_ID.equals(a)) {
					return noEntityFirst ? -1 : 1;
				}
				if (NO_CHOICE_ENTITY_ID.equals(b)) {
					return noEntityFirst ? 1 : -1;
				}
				return a.compareTo(b);
			}
		});
	}

	protected void sortOptions(List options) {
		Collections.sort(options, new Comparator<Long>() {
			@Override
			public int compare(Long a, Long b) {
				return a.compareTo(b);
			}
		});
	}

	protected String getDefaultString(Boolean value) {
		if (value == null) {
			return null;
		}
		if (value) {
			return YES;
		} else {
			return NO;
		}
	}

	protected Boolean nullFalseTrue(String value) {
		if (value == null) {
			return null;
		}
		return YES.equals(value);
	}

	protected boolean setValueBasedOnOtherValueSelected(LocationAccessComponentWrapper wrapper, String otherFieldKey, String otherFieldValue, String thisFieldValue) {
		String otherKey = makeKey(otherFieldKey, wrapper.getIndex());
		Object oldOtherValue = values.get(otherKey);
		if (Objects.equals(otherFieldValue, values.get(otherKey))) {
			values.put(wrapper.getKey(), thisFieldValue);
			return Objects.equals(oldOtherValue, otherFieldValue);
		}
		return false;
	}

	protected Object valueOfKey(LocationAccessComponentWrapper wrapper, String key) {
		return values.get(makeKey(key, wrapper.getIndex()));
	}

	protected boolean getVisibleBasedOnOtherValueSelected(LocationAccessComponentWrapper wrapper, String otherFieldKey, String otherFieldValue) {
		return Objects.equals(otherFieldValue, valueOfKey(wrapper, otherFieldKey));
	}

	protected boolean getVisibleBasedOnOtherValueNotSelected(LocationAccessComponentWrapper wrapper, String otherFieldKey, String otherFieldValue) {
		return !Objects.equals(otherFieldValue, valueOfKey(wrapper, otherFieldKey));
	}

	protected boolean getVisibleBasedOnAnyEntitySelected(LocationAccessComponentWrapper wrapper, String otherFieldKey, boolean alsoAcceptNonEntity) {
		Long id = (Long) valueOfKey(wrapper, otherFieldKey);
		return !NO_CHOICE_ENTITY_ID.equals(id) && (alsoAcceptNonEntity || !NON_ENTITY_ID.equals(id));
	}

	protected boolean getVisibleBasedOnEntitySelected(LocationAccessComponentWrapper wrapper, String otherFieldKey, Long entityId) {
		return entityId.equals((Long) valueOfKey(wrapper, otherFieldKey));
	}

	protected boolean getVisibleBasedOnAnyStringSelected(LocationAccessComponentWrapper wrapper, String otherFieldKey) {
		return null != (String) valueOfKey(wrapper, otherFieldKey);
	}

	protected boolean getVisibleBasedOnStringEmpty(LocationAccessComponentWrapper wrapper, String otherFieldKey) {
		return !StringUtils.isEmpty((String) valueOfKey(wrapper, otherFieldKey));
	}

	protected boolean getVisibleBasedOnAddressFields(LocationAccessComponentWrapper wrapper) {
		return (getVisibleBasedOnStringEmpty(wrapper, KEY_ADDRESS_ROAD) &&
				getVisibleBasedOnStringEmpty(wrapper, KEY_ADDRESS_ZIPCODE) &&
				getVisibleBasedOnStringEmpty(wrapper, KEY_ADDRESS_CITY));
	}

	protected Product getProductFromValues(String key, int bi) {
		Long entityId = (Long) values.get(makeKey(key, bi));
		if (!NO_CHOICE_ENTITY_ID.equals(entityId) && !NON_ENTITY_ID.equals(entityId)) {
			Product p = ProductDao.lookup().findById(entityId);
			if (p == null) {
				log.warn("Oh nooooo");
			}
			return p;
		}
		return null;
	}

	protected Boolean getTriStateBooleanValue(String key, int i) {
		if (YES.equals((String) values.get(makeKey(key, i)))) {
			return Boolean.valueOf(true);
		} else if (NO.equals((String) values.get(makeKey(key, i)))) {
			return Boolean.valueOf(false);
		} else {
			return null;
		}
	}

	protected LocationAccessComponentWrapper getWrapper(String key) {
		for (LocationAccessComponentWrapper w: wrappers) {
			if (w.getBaseKey().equals(key)) {
				return w;
			}
		}
		return null;
	}

	protected void addProductsFromEntityFields(MobileContract contract,
											 Map<Product, List<CountAndInstallation>> productToCountMap,
											 int bi, String[] keys) {
		for (String key: keys) {
			Product product = getProductFromValues(key, bi);
			if (product != null) {
				addProductsForBundle(contract, productToCountMap, bi, product.getProductId());
			}
		}
	}

	protected boolean removeProductsFromEntityDropDown(LocationAccessComponentWrapper wrapper, Predicate<Product> predicate) {
		boolean dirty = false;
		Iterator<Long> entityIds = ((BootstrapSelectSingle) wrapper.getComponent()).getChoices().iterator();
		while (entityIds.hasNext()) {
			Long entityId = entityIds.next();
			if ((entityId != null) && (!NON_ENTITY_ID.equals(entityId)) && (!NO_CHOICE_ENTITY_ID.equals(entityId))) {
				Product product = ProductDao.lookup().findById(entityId);
				if (predicate.test(product)) {
					entityIds.remove();
					dirty = true;
				}
			}
		}
		return dirty;
	}

	protected void updateCdmOkBasedOnRequiredTextFields(LocationBundleData locationBundle, String... keys) {
		for (String key: keys) {
			LocationAccessComponentWrapper wrapper = getWrapper(key);
			if (wrapper.getComponent().getParent().getParent().isVisible() && StringUtils.isEmpty((String) values.get(wrapper.getKey()))) {
				log.info("CDM is not ok because of: " + wrapper.getKey());
				locationBundle.setCdmOk(false);
			}
		}
	}
}
