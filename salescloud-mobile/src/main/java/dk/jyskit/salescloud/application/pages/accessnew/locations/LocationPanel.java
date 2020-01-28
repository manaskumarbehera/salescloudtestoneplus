package dk.jyskit.salescloud.application.pages.accessnew.locations;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.utils.ExceptionUtils;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
@Slf4j
public class LocationPanel extends Panel {
	private static final String KEY_ADDRESS_ROAD 			= "a";
	private static final String KEY_ADDRESS_ZIPCODE 		= "b";
	private static final String KEY_ADDRESS_CITY 			= "c";
	private static final String KEY_ACCESS_TYPE 			= "d";

	@Inject
	private ContractSaver contractSaver;

	@Inject
	private PageNavigator navigator;
	private LocationsPanel parentPanel;
	private int tabIndex;

	public LocationPanel(String id, LocationsPanel parentPanel, Map<String, String> labelMap, int tabIndex) {
		super(id);
		this.parentPanel = parentPanel;
		this.tabIndex = tabIndex;

		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		// final BusinessArea businessArea = contract.getBusinessArea();

		final LocationPanelValues values = new LocationPanelValues();
		Jsr303Form<LocationPanelValues> form = new Jsr303Form<>("form", values);
		add(form);

//		Map<String, String> labelMap = new HashMap<>();
//		labelMap.put("action.prev", "Tilbage");
//		labelMap.put("action.next", "Videre");
//		labelMap.put("action.add", "Tilføj lokation");
//		labelMap.put("action.delete", "Slet lokation");

		MapLabelStrategy labelStrategy = new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace()));
		form.setLabelStrategy(labelStrategy);

		form.setLabelSpans(SmallSpanType.SPAN8);
		form.setEditorSpans(SmallSpanType.SPAN4);

//		for (int i = 0; i < contract.getLocationBundles().size(); i++) {
//			final int bi = i;
			final int bi = tabIndex;
			final LocationBundleData bundle = contract.getLocationBundles().get(bi);

			FormGroup<LocationPanelValues> group = form.createGroup(Model.of("Adresse"));
			final int lastComponentIndex = 3;
			final ComponentWrapper[] components = new ComponentWrapper[lastComponentIndex + 1];
			int componentIndex = 0;

			if (bi == 0) {
				if (StringUtils.isEmpty(contract.getLocationBundles().get(bi).getAddressRoad())) {
					bundle.setAddressRoad(contract.getCustomer().getAddress());
				}
				if (StringUtils.isEmpty(bundle.getAddressZipCode())) {
					bundle.setAddressZipCode(contract.getCustomer().getZipCode());
				}
				if (StringUtils.isEmpty(bundle.getAddressCity())) {
					bundle.setAddressCity(contract.getCustomer().getCity());
				}
			}

			{
				addTextField(contract, values, labelMap, bundle.getAddressRoad(), KEY_ADDRESS_ROAD + bi, group,
						"Adresse", componentIndex++, lastComponentIndex, components, new UpdateListener() {
							@Override
							public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							}
						});
			}

			{
				addTextField(contract, values, labelMap, bundle.getAddressZipCode(), KEY_ADDRESS_ZIPCODE + bi,
						group, "Postnr.", componentIndex++, lastComponentIndex, components, new UpdateListener() {
							@Override
							public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							}
						});
			}

			{
				addTextField(contract, values, labelMap, bundle.getAddressCity(), KEY_ADDRESS_CITY + bi, group,
						"By", componentIndex++, lastComponentIndex, components, new UpdateListener() {
							@Override
							public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
							}
						});
			}

			{
				DropDownChoice field = addAccessTypeDropDownChoice(contract, values, labelMap, AccessTypeEnum.getById(bundle.getAccessType()),
						KEY_ACCESS_TYPE + bi, group, "Access type", componentIndex++, components,
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

			try {
				for (ComponentWrapper componentWrapper : components) {
					componentWrapper.update(null);
				}
			} catch (Exception e) {
				log.warn("Check if lastComponentIndex is correct");
			}

			AjaxButton button = group.addButton("action.delete", Buttons.Type.Info, new AjaxEventListener() {
				@Override
				public void onAjaxEvent(AjaxRequestTarget target) {
					List<LocationBundleData> bundles = contract.getLocationBundles();
					bundles.remove(tabIndex);  // TODO: OK?
					contract.setLocationBundles(bundles);
					saveAndNavigate(contract, values, LocationsPage.class);
				}
			});
			button.add(AttributeModifier.append("style", "margin-bottom: 20px"));
//		}

//		addButtons(contract, values, form);
	}

	private void saveAndNavigate(final MobileContract contract, LocationPanelValues values, Class<? extends WebPage> page) {
		try {
			// Transfer values to contract
			List<LocationBundleData> bundles = contract.getLocationBundles();
			contract.setLocationBundles(bundles);
			for (int subIndex = 0; subIndex < bundles.size(); subIndex++) {
				LocationBundleData bundle = bundles.get(subIndex);
				bundle.setAddressRoad((String) values.get(KEY_ADDRESS_ROAD + subIndex));
				bundle.setAddressZipCode((String) values.get(KEY_ADDRESS_ZIPCODE + subIndex));
				bundle.setAddressCity((String) values.get(KEY_ADDRESS_CITY + subIndex));
				bundle.setAccessType(((AccessTypeEnum) values.get(KEY_ACCESS_TYPE + subIndex)).getId());
			}
			contract.setLocationBundles(bundles);

			contractSaver.save(contract);
			
			setResponsePage(page);
		} catch (Exception e) {
			ExceptionUtils.handleException(e);
		}
	}

	private DropDownChoice addAccessTypeDropDownChoice(final MobileContract contract, final LocationPanelValues values,
												   Map<String, String> labelMap, AccessTypeEnum initialValue, String key,
												   FormGroup<LocationPanelValues> formGroup, String label, final int componentIndex,
												   final ComponentWrapper[] components, UpdateListener listener) {
		values.put(key, initialValue);
		labelMap.put(key, label);
		List<AccessTypeEnum> options = AccessTypeEnum.valuesAsList();

		BootstrapSelectSingle component = formGroup.addSelectSinglePanel(key, options, new IChoiceRenderer() {
			@Override
			public Object getDisplayValue(Object value) {
				return ((AccessTypeEnum) value).getText();
			}

			@Override
			public String getIdValue(Object object, int index) {
				return "" + index;
			}
		}, new BootstrapSelectOptions());

		component.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateComponents(componentIndex, components, target);
			}
		});

		components[componentIndex] = new ComponentWrapper(component, listener);
		return component;
	}

	private DropDownChoice addStringDropDownChoice(final MobileContract contract, final LocationPanelValues values,
												   Map<String, String> labelMap, String initialValue, List<String> options, String key,
												   FormGroup<LocationPanelValues> formGroup, String label, final int componentIndex, final int lastComponentIndex,
												   final ComponentWrapper[] components, UpdateListener listener) {
		values.put(key, initialValue);
		labelMap.put(key, label);

		BootstrapSelectSingle component = formGroup.addSelectSinglePanel(key, options, new IChoiceRenderer() {
			@Override
			public Object getDisplayValue(Object value) {
				return value;
			}

			@Override
			public String getIdValue(Object object, int index) {
				return "" + index;
			}
		}, new BootstrapSelectOptions());

		component.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateComponents(componentIndex, components, target);
			}
		});

		components[componentIndex] = new ComponentWrapper(component, listener);
		return component;
	}

	private NumberTextField<Long> addNumberTextField(final MobileContract contract, final LocationPanelValues values,
			Map<String, String> labelMap, Number initialValue, Integer min, Integer max, String key,
			FormGroup<LocationPanelValues> group, String label, final int componentIndex, final int lastComponentIndex,
			final ComponentWrapper[] components, UpdateListener listener) {
		values.put(key, initialValue);
		labelMap.put(key, label);

		NumberTextField component = group.addNumberTextField(key);
		component.setType(Long.class);
		component.add(AttributeModifier.append("min", min));
		component.add(AttributeModifier.append("max", max));
		
		component.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateComponents(componentIndex, components, target);
			}
		});

		components[componentIndex] = new ComponentWrapper(component, listener);
		return component;
	}

	private TextField addTextField(final MobileContract contract, final LocationPanelValues values,
			Map<String, String> labelMap, String initialValue, final String key, FormGroup<LocationPanelValues> group,
			String label, final int componentIndex, final int lastComponentIndex, final ComponentWrapper[] components,
			UpdateListener listener) {
		values.put(key, initialValue);
		labelMap.put(key, label);

		TextField component = group.addTextField(key);

		// For updating of "values"
		component.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing needed
			}
		});

		// For throttling
		component.add(new AjaxEventBehavior("keyup") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				updateComponents(componentIndex, components, target);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setThrottlingSettings(new ThrottlingSettings(key, Duration.ONE_SECOND, true));
			}
		});

		components[componentIndex] = new ComponentWrapper(component, listener);
		return component;
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

	private void updateComponents(int ci, LocationPanel.ComponentWrapper[] components, AjaxRequestTarget target) {
		if (target != null) {
			for (int j = ci + 1; j < components.length; j++) {
				components[j].update(target);
			}
		}
	}

	private void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountMap, Product product, Integer subIndex) {
		if (product != null) {
			addToProductToCountsMap(productToCountMap, product, 1, subIndex);
		}
	}

	private void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountsMap, Product product, long count, Integer subIndex) {
		if (product == null) {
			log.warn("Trying to add unknown product!");
		} else {
			if (count > 0) {
				List<CountAndInstallation> countAndInstallations = productToCountsMap.get(product);
				if (countAndInstallations == null) {
					countAndInstallations = new ArrayList<>();
				}
				CountAndInstallation countAndInstallation = new CountAndInstallation();
				countAndInstallation.setCountNew((int) count);
				countAndInstallation.setSubIndex(subIndex);
				countAndInstallations.add(countAndInstallation);
				productToCountsMap.put(product, countAndInstallations);
				if (count > 0) {
					log.info("prod: " + product.getProductId());
				}
			}
		}
	}

	interface UpdateListener extends Serializable {
		public void onAjaxEvent(AjaxRequestTarget target, FormComponent component);
	}

	class ComponentWrapper implements Serializable {
		FormComponent component;
		UpdateListener listener;

		public ComponentWrapper(FormComponent component, UpdateListener listener) {
			this.component = component;
			this.listener = listener;
		}

		public void update(AjaxRequestTarget target) {
			if (target != null) {
				target.add(component.getParent().getParent());

				if (listener != null) {
					listener.onAjaxEvent(target, component);
				}
			}
		}
	}

}
