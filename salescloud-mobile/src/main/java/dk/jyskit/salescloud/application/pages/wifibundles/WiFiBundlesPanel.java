package dk.jyskit.salescloud.application.pages.wifibundles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.CountAndInstallation;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroup;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.WiFiBundleIds;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.utils.MapUtils;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WiFiBundlesPanel extends Panel {
	@Inject
	private ContractDao contractDao;
	@Inject
	private ContractSaver contractSaver;
	@Inject
	private PageNavigator navigator;
	@Inject
	private OrderLineDao orderLineDao;
	@Inject
	private ProductDao productDao;

	private Map<Integer, NumberTextField<Integer>> numberOfAccessPointsFieldMap = new HashMap<>();
	private Map<Integer, Label> siteSurveyFieldMap = new HashMap<>();
	private Map<Long, Integer> idToPriceMap;
	// private Map<Integer, TextField> lidIdFieldMap = new HashMap<>();

	public WiFiBundlesPanel(String id) {
		super(id);

		final MobileContract contract = (MobileContract) CoreSession.get().getContract();

		final WiFiBundlesPanelValues values = new WiFiBundlesPanelValues();
		Jsr303Form<WiFiBundlesPanelValues> form = new Jsr303Form<>("form", values);
		add(form);

		Map<String, String> labelMap = new HashMap<>();
		labelMap.put("action.prev", "Tilbage");
		labelMap.put("action.next", "Videre");
		labelMap.put("action.add", "Tilføj lokation");
		labelMap.put("action.delete", "Slet lokation");

		MapLabelStrategy labelStrategy = new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace()));
		form.setLabelStrategy(labelStrategy);

		form.setLabelSpans(SmallSpanType.SPAN8);
		form.setEditorSpans(SmallSpanType.SPAN4);

		idToPriceMap = MapUtils.stringToLongIntMap(contract.getVariableInstallationFees());
		
		List<WiFiBundleIds> bundles = contract.getWiFiBundles();
		if (bundles.size() == 0) {
			WiFiBundleIds wiFiBundleIds = new WiFiBundleIds();
			bundles.add(wiFiBundleIds);
			contract.setWiFiBundles(bundles);
			saveAndNavigate(contract, values, WiFiBundlesPage.class);
		}

		List<Long> serviceLevelIds = new ArrayList<>();
		for (Product product : getProductGroup(contract.getBusinessArea(),
				MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE).getProducts()) {
			serviceLevelIds.add(product.getId());
		}

		List<Long> areaSizeIds = new ArrayList<>();
		for (Product product : getProductGroup(contract.getBusinessArea(),
				MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AREA_SIZE).getProducts()) {
			areaSizeIds.add(product.getId());
		}

		List<Long> apIds = new ArrayList<>();
		for (Product product : getProductGroup(contract.getBusinessArea(),
				MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP).getProducts()) {
			apIds.add(product.getId());
		}

		List<Long> switchIds = new ArrayList<>();
		for (Product product : getProductGroup(contract.getBusinessArea(),
				MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SWITCH).getProducts()) {
			switchIds.add(product.getId());
		}

		// List<Long> locationAddOnIds = new ArrayList<>();
		// for (Product product : getProductGroup(contract.getBusinessArea(),
		// MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_ADDON).getProducts())
		// {
		// locationAddOnIds.add(product.getId());
		// }

		{
//			FormGroup<WiFiBundlesPanelValues> f = form.createGroup(Model.of("Generelt for alle lokationer"));
//			String key = "service";
//			OrderLine orderLine = contract.getAnyOrderLine(getProductGroup(contract.getBusinessArea(),
//					MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE));
//			if (orderLine == null) {
//				for (Product product : getProductGroup(contract.getBusinessArea(),
//						MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE).getProducts()) {
//					if (((MobileProduct) product).hasFlag("DEFAULT")) {
//						values.put(key, product.getId());
//						break;
//					}
//				}
//			} else {
//				values.put(key, orderLine.getProduct().getId());
//			}
//			labelMap.put(key, "Service");
//			f.addDropDownChoice(key, serviceLevelIds, new ChoiceRenderer<Long>() {
//				@Override
//				public Object getDisplayValue(Long id) {
//					return productDao.findById(id).getPublicName();
//				}
//			}).setRequired(true);
		}

		for (int i = 0; i < contract.getWiFiBundles().size(); i++) {
			final int bundleIndex = i;
			// final WiFiBundleIds bundleIds =
			// contract.getWiFiBundles().get(bundleIndex);

			FormGroup<WiFiBundlesPanelValues> f = form.createGroup(Model.of("Lokation " + (bundleIndex + 1)));

			{
				String key = "address" + bundleIndex;
				values.put(key, "" + contract.getWiFiBundles().get(bundleIndex).getAddress());
				labelMap.put(key, "Adresse");
				f.addTextField(key).setRequired(true);
			}

			{
				String key = "areasize" + bundleIndex;
				values.put(key, contract.getWiFiBundles().get(bundleIndex).getAreaSizeEntityId());
				labelMap.put(key, "Areal");
				DropDownChoice areaSizeField = f.addDropDownChoice(key, areaSizeIds, new ChoiceRenderer<Long>() {
					@Override
					public Object getDisplayValue(Long id) {
						return productDao.findById(id).getPublicName();
					}
				});
				areaSizeField.setRequired(true);
				areaSizeField.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						String keyAreaSize = "areasize" + bundleIndex;
						Long areaSizeId = values.getAsLong(keyAreaSize);
						MobileProduct areaSizeProduct = (MobileProduct) contract.getBusinessArea()
								.getProductById(areaSizeId);
						int min = 1;
						String flags = areaSizeProduct.getFlags();
						if (((flags != null) && (flags.startsWith("ap-min-")))) {
							flags = flags.substring("ap-min-".length(), flags.length());
							min = Integer.valueOf(flags);
						}

						// Set number of access points accordingly
						NumberTextField<Integer> numberTextField = numberOfAccessPointsFieldMap
								.get(Integer.valueOf(bundleIndex));
						numberTextField.setMinimum(min);
						String keyNumberOfAccessPoints = "accesspointcount" + bundleIndex;
						Integer numberOfAccessPoints = (Integer) values.get(keyNumberOfAccessPoints);
						if (numberOfAccessPoints != min) {
							values.put(keyNumberOfAccessPoints, Integer.valueOf(min));
						}
						target.add(numberTextField);

						// Set type of site survey accordingly
						// ProductRelations don't work for some reason
						// for (ProductRelation productRelation :
						// contract.getBusinessArea().getProductRelations(areaSizeProduct,
						// true, false)) {
						// if
						// (productRelation.getRelationTypeId().equals(MobileProductRelationTypeProvider.ADD_ORDERLINES_1))
						// {
						// Product siteSurveyProduct =
						// productRelation.getProducts().get(1);
						// if
						// (siteSurveyProduct.getProductGroup().getUniqueName().equals(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SITE_SURVEY.getKey()))
						// {
						// {
						// String keySiteSurvey = "sitesurvey-id" + bundleIndex;
						// values.put(keySiteSurvey, siteSurveyProduct.getId());
						// }
						// {
						// String keySiteSurvey = "sitesurvey-name" +
						// bundleIndex;
						// values.put(keySiteSurvey,
						// siteSurveyProduct.getPublicName());
						// }
						//
						// Label siteSurveyField =
						// siteSurveyFieldMap.get(Integer.valueOf(bundleIndex));
						// target.add(siteSurveyField);
						// }
						// }
						// }
						// addProductRelation(businessArea,
						// MobileProductRelationTypeProvider.ADD_ORDERLINES_1,
						// "999 265 00", "44 019 81");
						// addProductRelation(businessArea,
						// MobileProductRelationTypeProvider.ADD_ORDERLINES_1,
						// "999 266 00", "44 019 89");
						// addProductRelation(businessArea,
						// MobileProductRelationTypeProvider.ADD_ORDERLINES_1,
						// "999 267 00", "44 019 89");
						// addProductRelation(businessArea,	
						// MobileProductRelationTypeProvider.ADD_ORDERLINES_1,
						// "999 268 00", "44 019 89");
						// addProductRelation(businessArea,
						// MobileProductRelationTypeProvider.ADD_ORDERLINES_1,
						// "999 269 00", "44 019 97");

						MobileProduct siteSurveyProduct = null;
						if ("999 265 00".equals(areaSizeProduct.getProductId())) {
							siteSurveyProduct = (MobileProduct) contract.getBusinessArea()
									.getProductByProductId("44 019 81");
						} else if ("999 266 00".equals(areaSizeProduct.getProductId())) {
							siteSurveyProduct = (MobileProduct) contract.getBusinessArea()
									.getProductByProductId("44 019 89");
						} else if ("999 267 00".equals(areaSizeProduct.getProductId())) {
							siteSurveyProduct = (MobileProduct) contract.getBusinessArea()
									.getProductByProductId("44 019 89");
						} else if ("999 268 00".equals(areaSizeProduct.getProductId())) {
							siteSurveyProduct = (MobileProduct) contract.getBusinessArea()
									.getProductByProductId("44 019 89");
						} else if ("999 269 00".equals(areaSizeProduct.getProductId())) {
							siteSurveyProduct = (MobileProduct) contract.getBusinessArea()
									.getProductByProductId("44 019 97");
						}
						if (siteSurveyProduct != null) {
							{
								String keySiteSurvey = "sitesurvey-id" + bundleIndex;
								values.put(keySiteSurvey, siteSurveyProduct.getId());
							}
							{
								String keySiteSurvey = "sitesurvey-name" + bundleIndex;
								values.put(keySiteSurvey, siteSurveyProduct.getPublicName());
							}

							Label siteSurveyField = siteSurveyFieldMap.get(Integer.valueOf(bundleIndex));
							target.add(siteSurveyField);
						}
					}
				});
			}

			{
				String key = "accesspointcount" + bundleIndex;
				values.put(key, Integer.valueOf(contract.getWiFiBundles().get(bundleIndex).getAccessPointCount()));
				labelMap.put(key, "Antal access points");
				NumberTextField<Integer> numberOfAccessPointsField = f.addNumberTextField(key);
				numberOfAccessPointsFieldMap.put(Integer.valueOf(bundleIndex), numberOfAccessPointsField);

				numberOfAccessPointsField.setRequired(true);
			}

			{
				List<Product> accesspointProducts = getProductGroup(contract.getBusinessArea(),
						MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP).getProducts();
				if (accesspointProducts.size() > 1) {
					String key = "ap" + bundleIndex;
					Long value = contract.getWiFiBundles().get(bundleIndex).getAccessPointEntityId();
					if (value == null) {
						Product defaultProduct = null;
						for (Product product : getProductGroup(contract.getBusinessArea(),
								MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP).getProducts()) {
							if (((MobileProduct) product).hasFlag("DEFAULT")) {
								defaultProduct = product;
								break;
							} else {
								defaultProduct = product;
							}
						}
						value = defaultProduct.getId();
					}
					values.put(key, value);
					labelMap.put(key, "Accesspoint type");
					f.addDropDownChoice(key, apIds, new ChoiceRenderer<Long>() {
						@Override
						public Object getDisplayValue(Long id) {
							return productDao.findById(id).getPublicName();
						}
					}).setRequired(true);
				}
			}

			{
				String key = "switch" + bundleIndex;
				Long value = contract.getWiFiBundles().get(bundleIndex).getSwitchEntityId();
				if (value == null) {
					Product defaultProduct = null;
					for (Product product : getProductGroup(contract.getBusinessArea(),
							MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SWITCH).getProducts()) {
						if (((MobileProduct) product).hasFlag("DEFAULT")) {
							defaultProduct = product;
							break;
						} else {
							defaultProduct = product;
						}
					}
					value = defaultProduct.getId();
				}
				values.put(key, value);
				labelMap.put(key, "Switch");
				f.addDropDownChoice(key, switchIds, new ChoiceRenderer<Long>() {
					@Override
					public Object getDisplayValue(Long id) {
						return productDao.findById(id).getPublicName();
					}
				}).setRequired(true);
			}

			// {
			// String key = "cabling" + i;
			// values.put(key,
			// contract.getWiFiBundles().get(i).getCablingEntityId());
			// labelMap.put(key, "Kabling");
			// f.addDropDownChoice(key, cablingIds, new ChoiceRenderer<Long>() {
			// @Override
			// public Object getDisplayValue(Long id) {
			// return productDao.findById(id).getPublicName();
			// }
			// });
			// }

			{
				String keyId = "sitesurvey-id" + bundleIndex;
				String keyName = "sitesurvey-name" + bundleIndex;
				Long siteSurveyId = contract.getWiFiBundles().get(bundleIndex).getSiteSurveyEntityId();
				if (siteSurveyId == null) {
					values.put(keyId, null);
					values.put(keyName, "");
				} else {
					values.put(keyId, siteSurveyId);
					values.put(keyName, contract.getBusinessArea().getProductById(siteSurveyId).getPublicName());
				}
				labelMap.put(keyName, "Site survey");
				Label siteSurveyField = f.addReadonly(keyName);
				siteSurveyField.setOutputMarkupId(true);
				siteSurveyFieldMap.put(Integer.valueOf(bundleIndex), siteSurveyField);
			}

			{
				String key = "newaccess" + bundleIndex;
				values.put(key, contract.getWiFiBundles().get(i).getNewAccess() ? "Ny" : "Eksisterende");
				labelMap.put(key, "Access – SKAL være TDC Erhverv Access");

				f.addDropDownChoice(key, Arrays.asList((new String[] { "Ny", "Eksisterende" })));
				// CheckBox newAccessCheckbox = f.addCheckBox(key,
				// "style=margin-left:0px");

				// newAccessCheckbox.add(new OnChangeAjaxBehavior() {
				// @Override
				// protected void onUpdate(AjaxRequestTarget target) {
				// TextField lidIdField = lidIdFieldMap.get(bundleIndex);
				// if (values.getAsBoolean("newaccess" + bundleIndex)) {
				// lidIdField.getParent().getParent().setVisible(true);
				// } else {
				// lidIdField.getParent().getParent().setVisible(false);
				// }
				// target.add(lidIdField.getParent().getParent());
				// }
				// });
			}
			
			{
				String key = "service" + bundleIndex;
				values.put(key, contract.getWiFiBundles().get(bundleIndex).getServiceLevelEntityId());
				labelMap.put(key, "Service");
				DropDownChoice serviceField = f.addDropDownChoice(key, serviceLevelIds, new ChoiceRenderer<Long>() {
					@Override
					public Object getDisplayValue(Long id) {
						return productDao.findById(id).getPublicName();
					}
				});
				serviceField.setRequired(true);
				
				
				
//				String keyId = "service-id" + bundleIndex;
//				String keyName = "service-name" + bundleIndex;
//				Long serviceId = contract.getWiFiBundles().get(bundleIndex).getServiceLevelEntityId();
//				if (serviceId == null) {
//					values.put(keyId, null);
//					values.put(keyName, "");
//				} else {
//					values.put(keyId, serviceId);
//					values.put(keyName, contract.getBusinessArea().getProductById(serviceId).getPublicName());
//				}
//				labelMap.put(keyName, "Service");
//				Label serviceField = f.addReadonly(keyName);
//				serviceField.setOutputMarkupId(true);
//				serviceFieldMap.put(Integer.valueOf(bundleIndex), serviceField);
				
				
				
//				
//				String key = "service";
//				OrderLine orderLine = contract.getAnyOrderLine(getProductGroup(contract.getBusinessArea(),
//						MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE));
//				if (orderLine == null) {
//					for (Product product : getProductGroup(contract.getBusinessArea(),
//							MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE).getProducts()) {
//						if (((MobileProduct) product).hasFlag("DEFAULT")) {
//							values.put(key, product.getId());
//							break;
//						}
//					}
//				} else {
//					values.put(key, orderLine.getProduct().getId());
//				}
//				labelMap.put(key, "Service");
//				f.addDropDownChoice(key, serviceLevelIds, new ChoiceRenderer<Long>() {
//					@Override
//					public Object getDisplayValue(Long id) {
//						return productDao.findById(id).getPublicName();
//					}
//				}).setRequired(true);
			}

			f = form.createGroup(Model.of("Lokation " + (bundleIndex + 1) + " - Tilkøb efter site survey"));

			// Map<Long, Integer> idToPriceMap =
			// MapUtils.stringToLongIntMap(contract.getVariableInstallationFees());
			// contract.setVariableInstallationFees(MapUtils.longIntMapToString(idToPriceMap));

			{
				for (Product product : getProductGroup(contract.getBusinessArea(),
						MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING).getProducts()) {
					Long priceId = product.getId() * 100 + bundleIndex;
					String key = "cabling" + priceId;
					Integer price = idToPriceMap.get(priceId);
					if (price == null) {
						values.put(key, Integer.valueOf(0));
					} else {
						values.put(key, price);
					}
					labelMap.put(key, product.getPublicName());
					NumberTextField cablingField = f.addNumberTextField(key);
				}
			}

			// {
			// String key = "lidid" + bundleIndex;
			// values.put(key, "" +
			// contract.getWiFiBundles().get(bundleIndex).getLidId());
			// labelMap.put(key, "LID ID");
			// TextField lidIdField = f.addTextField(key);
			// lidIdField.setRequired(true);
			// lidIdFieldMap.put(Integer.valueOf(bundleIndex), lidIdField);
			// lidIdField.getParent().getParent().setVisible((contract.getWiFiBundles().get(i).getNewAccess()
			// != null) && contract.getWiFiBundles().get(i).getNewAccess());
			// }

			// for (Product product :
			// getProductGroup(contract.getBusinessArea(),
			// MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_ADDON).getProducts())
			// {
			// String key = "" + product.getId();
			// if
			// (contract.getWiFiBundles().get(i).getAddOnEntityIds().contains(product.getId()))
			// {
			// values.put(key, Boolean.TRUE);
			// } else {
			// values.put(key, Boolean.FALSE);
			// }
			// labelMap.put(key, product.getPublicName());
			// CheckBox productCheckbox = f.addCheckBox(key,
			// "style=margin-left:0px");
			// if (((MobileProduct) product).hasFlag("read_only")) {
			// productCheckbox.setEnabled(false);
			// }
			// }

			AjaxButton button = f.addButton("action.delete", Buttons.Type.Info, new AjaxEventListener() {
				@Override
				public void onAjaxEvent(AjaxRequestTarget target) {
					List<WiFiBundleIds> bundles = contract.getWiFiBundles();

					// Move values, if necessary
					if (bundleIndex < bundles.size() - 1) {
						for (int b = bundleIndex; b < bundles.size(); b++) {
							for (String prefix : new String[] { "address", "areasize", "sitesurvey-id",
									"sitesurvey-name", "accesspointcount", "ap", "switch", "newaccess", "service" }) {
								values.put(prefix + b, values.get(prefix + (b + 1)));
							}
							for (Product product : getProductGroup(contract.getBusinessArea(),
									MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING).getProducts()) {
								String key = "cabling" + (product.getId() * 100 + b);
								String keyNext = "cabling" + (product.getId() * 100 + (b + 1));
								values.put(key, values.get(keyNext));
							}
						}
					}

					bundles.remove(bundleIndex);
					contract.setWiFiBundles(bundles);

					saveAndNavigate(contract, values, WiFiBundlesPage.class);
				}
			});
			button.add(AttributeModifier.append("style", "margin-bottom: 20px"));
		}

		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, values, navigator.prev(getWebPage()));
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, values, navigator.next(getWebPage()));
			}
		});

		AjaxButton addButton = form.addSubmitButton("action.add", Buttons.Type.Info, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				if (contract.getWiFiBundles().size() < 10) {
					List<WiFiBundleIds> bundles = contract.getWiFiBundles();
					WiFiBundleIds wiFiBundleIds = new WiFiBundleIds();
					bundles.add(wiFiBundleIds);

					contract.setWiFiBundles(bundles);
					saveAndNavigate(contract, values, WiFiBundlesPage.class);
				}
			}
		});
	}

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

	/**
	 * Save data from form
	 * 
	 * @param contract
	 * @param values
	 * @param page
	 */
	private void saveAndNavigate(final MobileContract contract, WiFiBundlesPanelValues values,
			Class<? extends WebPage> page) {
		Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();

		// Transfer values to contract

		List<WiFiBundleIds> wifiBundles = contract.getWiFiBundles();
		for (int i = 0; i < contract.getWiFiBundles().size(); i++) {
			WiFiBundleIds bundle = wifiBundles.get(i);
			bundle.setAddress((String) values.get("address" + i));
			Integer accessPointCountValue = (Integer) values.get("accesspointcount" + i);
			bundle.setAccessPointCount(accessPointCountValue == null ? 1 : Integer.valueOf(accessPointCountValue));

			List<Product> accesspointProducts = getProductGroup(contract.getBusinessArea(),
					MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP).getProducts();
			if (accesspointProducts.size() == 1) {
				bundle.setAccessPointEntityId(accesspointProducts.get(0).getId());
			} else {
				bundle.setAccessPointEntityId((Long) values.get("ap" + i));
			}
			bundle.setAreaSizeEntityId((Long) values.get("areasize" + i));
			bundle.setSwitchEntityId((Long) values.get("switch" + i));
			bundle.setServiceLevelEntityId((Long) values.get("service" + i));

			bundle.setSiteSurveyEntityId((Long) values.get("sitesurvey-id" + i));

			String newAccessValue = (String) values.get("newaccess" + i);
			bundle.setNewAccess((newAccessValue == null) || "Ny".equals(newAccessValue));
			if (bundle.getNewAccess()) {
				bundle.setLidId("ny");
			} else {
				if ("ny".equalsIgnoreCase(bundle.getLidId())) {
					bundle.setLidId("");
				}
			}
		}
		contract.setWiFiBundles(wifiBundles);

		// Adjust order lines
		Map<Product, List<CountAndInstallation>> productToCountsMap = new HashMap<>();
//		Long serviceLevelId = (Long) values.get("service");
//		if (serviceLevelId != null) {
//			addToProductToCountsMap(productToCountsMap, productDao.findById(serviceLevelId), contract.getWiFiBundles().size(), 0);
//		}

		for (int i = 0; i < contract.getWiFiBundles().size(); i++) {
			WiFiBundleIds bundleIds = wifiBundles.get(i);

			if (bundleIds.getAreaSizeEntityId() != null) {
				ProductBundle wifiBundle = null;

				// MobileProductBundle wifiBundle = new MobileProductBundle();
				// wifiBundle.setBundleType(MobileProductBundleEnum.WIFI_BUNDLE);
				// wifiBundle.setPublicName(bundleIds.getAddress());
				// wifiBundle.setInternalName(bundleIds.getAddress());
				// wifiBundle.setSortIndex(i);

				Product areaSizeProduct = productDao.findById(bundleIds.getAreaSizeEntityId());
				for (ProductBundle bundle : contract.getCampaigns().get(0).getProductBundles()) {
					if (areaSizeProduct.getPublicName().equals(bundle.getPublicName())) {
						wifiBundle = bundle;
						break;
					}
				}
				if (wifiBundle != null) {
					int subIndex = i;
					BundleCount bundleCount = bundleToCountMap.get((MobileProductBundle) wifiBundle);
					if (bundleCount == null) {
						bundleCount = new BundleCount((MobileProductBundle) wifiBundle, subIndex, 1, 0);
						bundleToCountMap.put((MobileProductBundle) wifiBundle, bundleCount);
					} else {
						bundleCount.setCountNew(bundleCount.getCountNew() + 1);
					}

					Long[] idsToAdjust = { 
							bundleIds.getAccessPointEntityId(), 
							bundleIds.getAreaSizeEntityId(),
							bundleIds.getSwitchEntityId(), 
							bundleIds.getSiteSurveyEntityId(), 
							bundleIds.getServiceLevelEntityId() 
					};

					for (Long id : idsToAdjust) {
						Product product = productDao.findById(id);
						int count = 1;
						if (id.equals(bundleIds.getAccessPointEntityId()) || id.equals(bundleIds.getServiceLevelEntityId())) {
							count = bundleIds.getAccessPointCount();
						}
						addToProductToCountsMap(productToCountsMap, product, count, subIndex);

						// CountAndInstallation countAndInstallation =
						// (CountAndInstallation)
						// productToCountMap.get(product);
						// if (countAndInstallation == null) {
						// countAndInstallation = new CountAndInstallation();
						// countAndInstallation.setSubIndex(subIndex);
						// countAndInstallation.setCountNew(count);
						// productToCountMap.put(product, countAndInstallation);
						// addToProductToCountsMap(productToCountsMap, product,
						// count, subIndex);
						// } else {
						// countAndInstallation.setCountNew(countAndInstallation.getCountNew()
						// + count);
						// }
					}
				}
			}
		}

		contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.WIFI_BUNDLE);
		for (MobileProductGroupEnum groupType : MobileProductGroupEnum.getByPrefix(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE.name() + "_")) {
			contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(), groupType), productToCountsMap, null);
		}

		// Set variable prices
		for (int locationIndex = 0; locationIndex < contract.getWiFiBundles().size(); locationIndex++) {
			for (Product product : getProductGroup(contract.getBusinessArea(),
					MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING).getProducts()) {
				Long priceId = product.getId() * 100 + locationIndex;
				String key = "cabling" + priceId;
				Integer price = (Integer) values.get(key);
				if (price == null) {
					idToPriceMap.remove(priceId);
				} else {
					if (price.intValue() == 0) {
						idToPriceMap.remove(priceId);
					} else {
						idToPriceMap.put(priceId, price);

						// Also, add an orderline for this product +
						// locationIndex
						contract.addOrderLine(new OrderLine(product, locationIndex, 1, 0));
					}
				}
			}
		}
		contract.setVariableInstallationFees(MapUtils.longIntMapToString(idToPriceMap));

		// contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(),
		// MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SWITCH),
		// productToCountMap);
		// contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(),
		// MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SITE_SURVEY),
		// productToCountMap);
		// contract.adjustOrderLinesForProducts(getProductGroup(contract.getBusinessArea(),
		// MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_INCLUDED),
		// productToCountMap);

		for (OrderLine orderLine : contract.getOrderLines()) {
			orderLineDao.save(orderLine);
		}

//		contractDao.save(contract);
		contractSaver.save(contract);

		setResponsePage(page);
	}

	private void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountsMap, Product product,
			int count, int subIndex) {
		List<CountAndInstallation> countAndInstallations = productToCountsMap.get(product);
		if (countAndInstallations == null) {
			countAndInstallations = new ArrayList<>();
		}
		CountAndInstallation countAndInstallation = new CountAndInstallation();
		countAndInstallation.setCountNew(count);
		countAndInstallation.setSubIndex(subIndex);
		countAndInstallations.add(countAndInstallation);
		productToCountsMap.put(product, countAndInstallations);
	}
}
