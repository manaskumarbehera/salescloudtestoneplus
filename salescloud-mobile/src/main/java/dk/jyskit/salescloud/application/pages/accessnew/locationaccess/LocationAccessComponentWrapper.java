package dk.jyskit.salescloud.application.pages.accessnew.locationaccess;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.SelectProductCountEditor;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.SelectProductCountEditorPanel;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAll;
import dk.jyskit.salescloud.application.editors.simpleproductcount.SimpleProductCount;
import dk.jyskit.salescloud.application.editors.simpleproductcount.SimpleProductCountEditor;
import dk.jyskit.salescloud.application.editors.simpleproductcount.SimpleProductCountEditorPanel;
import dk.jyskit.salescloud.application.events.ValueChangedEvent;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAllEditor;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAllEditorPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.value.ValueMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.jyskit.salescloud.application.model.AccessConstants.NON_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;
import static dk.jyskit.salescloud.application.model.FiberErhvervBundleData.*;

@Data
@NoArgsConstructor
public class LocationAccessComponentWrapper<T extends FormComponent> implements Serializable {
	private String label;
	T component;
	private String baseKey;
	private String key;
	private int index;

	WrapperUpdateListener listener;  // Listener for when a previous field has been changed and we may need to update current field
	WrapperUpdateListener selfUpdateListener;

	public void update(AjaxRequestTarget target) {
		if (listener != null) {
			UpdateLevel updateLevel = listener.onAjaxEvent(target, this);
			if (updateLevel != null) {
				if (target != null) {
					if (updateLevel.isUpdateParent()) {
						target.add(component.getParent().getParent());
					} else {
						if (updateLevel.isUpdateComponent()) {
							target.add(component.getParent().getParent());  // Really??
							target.add(component);
						}
					}
				}
			}
		}
	}

	public T getFormComponent() {
		return component;
	}

	WrapperUpdateListener getUpdateListener() {
		return new WrapperUpdateListener() {
			@Override
			public UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper) {
				return null;
			}
		};
	}

	public Component getParent() {
		return component.getParent().getParent();
	}

//	public interface UpdateListener extends Serializable {
//		public void onAjaxEvent(AjaxRequestTarget target, FormComponent component);
//	}

	public interface WrapperUpdateListener extends Serializable {
		UpdateLevel onAjaxEvent(AjaxRequestTarget target, LocationAccessComponentWrapper wrapper);
	}

	public static String makeKey(String baseKey, int index) {
		return "" + index + '_' + baseKey;
	}

	public static String makeKey(Long baseKey, int index) {
		return "" + index + '_' + String.valueOf(baseKey);
	}

	public static LocationAccessComponentWrapper<TextField> addTextFieldWrapper(
			final ValueMap values, Map<String, String> labelMap, ComponentContainerPanel<ValueMap> formContainer,
			List<LocationAccessComponentWrapper> components, final String label, final String initialValue, final String baseKey, final int index) {

		LocationAccessComponentWrapper<TextField> wrapper = new LocationAccessComponentWrapper<TextField>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), initialValue);
		labelMap.put(wrapper.getKey(), label);

		wrapper.setComponent(formContainer.addTextField(wrapper.getKey()));

		// For updating of "values"
		wrapper.getComponent().add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing needed
			}
		});

		// For throttling
		wrapper.getComponent().add(new AjaxEventBehavior("keyup") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				updateRestOfFields(target, components, wrapper);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setThrottlingSettings(new ThrottlingSettings(wrapper.getKey(), Duration.ONE_SECOND, true));
			}
		});

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<TextField> addTextFieldWrapper(
			final ValueMap values, Map<String, String> labelMap, final String initialValue, final String baseKey, final int index,
			ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<TextField> wrapper = new LocationAccessComponentWrapper<TextField>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), initialValue);
		labelMap.put(wrapper.getKey(), label);

		wrapper.setComponent(formContainer.addTextField(wrapper.getKey()));

		// For updating of "values"
		wrapper.getComponent().add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing needed
			}
		});

		// For throttling
		wrapper.getComponent().add(new AjaxEventBehavior("keyup") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				updateRestOfFields(target, components, wrapper);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setThrottlingSettings(new ThrottlingSettings(wrapper.getKey(), Duration.ONE_SECOND, true));
			}
		});

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<NumberTextField> addNumberTextFieldWrapper(
			final ValueMap values, Map<String, String> labelMap, final Long initialValue,
			Integer min, Integer max, final String baseKey, final int index,
			ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<NumberTextField> wrapper = new LocationAccessComponentWrapper<NumberTextField>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), initialValue);
		labelMap.put(wrapper.getKey(), label);

		wrapper.setComponent(formContainer.addNumberTextField(wrapper.getKey()));
		wrapper.getComponent().setType(Long.class);
		wrapper.getComponent().add(AttributeModifier.append("min", min));
		wrapper.getComponent().add(AttributeModifier.append("max", max));

		wrapper.getComponent().add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateRestOfFields(target, components, wrapper);
			}
		});

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<DropDownChoice> addStringDropDownChoiceWrapper(
			final ValueMap values, Map<String, String> labelMap, final String initialValue,
			List<String> options, final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<DropDownChoice> wrapper = new LocationAccessComponentWrapper<DropDownChoice>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), initialValue);
		labelMap.put(wrapper.getKey(), label);

		wrapper.setComponent(formContainer.addSelectSinglePanel(wrapper.getKey(), options, new IChoiceRenderer() {
			@Override
			public Object getDisplayValue(Object value) {
				return value;
			}

			@Override
			public String getIdValue(Object object, int index) {
				return "" + index;
			}
		}, new BootstrapSelectOptions()));

		wrapper.getComponent().add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateRestOfFields(target, components, wrapper);
			}
		});

		components.add(wrapper);
		return wrapper;
	}

	private static void updateRestOfFields(AjaxRequestTarget target, List<LocationAccessComponentWrapper> components, LocationAccessComponentWrapper<?> wrapper) {
		if (wrapper.selfUpdateListener != null) {
			wrapper.selfUpdateListener.onAjaxEvent(target, wrapper);
		}
		boolean foundSelf = false;
		for (LocationAccessComponentWrapper c : components) {
			if (c.equals(wrapper)) {
				foundSelf = true;
			} else {
				c.update(target);
//				if (foundSelf) {
//					c.update(target);
//				}
			}
		}
	}

	public static LocationAccessComponentWrapper<BootstrapSelectSingle> addEntityDropDownChoiceWrapper(
			final ValueMap values, Map<String, String> labelMap, final Long initialValue, MobileProductGroup productGroup,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components, String noChoiceEntityLabel, IChoiceRenderer renderer) {
		return addEntityDropDownChoiceWrapper(values, labelMap, initialValue, MobileProductGroupEnum.getValueByKey(productGroup.getUniqueName()),
			baseKey, index, formContainer, label, components, noChoiceEntityLabel, renderer);
	}

	public static LocationAccessComponentWrapper<BootstrapSelectSingle> addEntityDropDownChoiceWrapper(
			final ValueMap values, Map<String, String> labelMap, final Long initialValue, MobileProductGroupEnum productGroupType,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components, String noChoiceEntityLabel, IChoiceRenderer renderer) {
		return addEntityDropDownChoiceWrapper(values, labelMap, initialValue, productGroupType, baseKey, index, formContainer,
		label, components, noChoiceEntityLabel, null, renderer);
	}

	public static LocationAccessComponentWrapper<BootstrapSelectSingle> addEntityDropDownChoiceWrapper(
			final ValueMap values, Map<String, String> labelMap, final Long initialValue, MobileProductGroupEnum productGroupType,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components, String noChoiceEntityLabel, String nonEntityChoiceLabel, IChoiceRenderer renderer) {

		LocationAccessComponentWrapper<BootstrapSelectSingle> wrapper = new LocationAccessComponentWrapper<BootstrapSelectSingle>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), initialValue);
		labelMap.put(wrapper.getKey(), label);

		List<Long> entityIds = new ArrayList<>();

		if (noChoiceEntityLabel != null) {
			entityIds.add(NO_CHOICE_ENTITY_ID);
		}
		if (nonEntityChoiceLabel != null) {
			entityIds.add(NON_ENTITY_ID);
		}
		if (MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.equals(productGroupType)) {
			for (Product product : getProductGroup(MobileSession.get().getContract().getBusinessArea(), productGroupType).getProducts()) {
				if (product.getProductId().equals("4401711") || product.getProductId().equals("4401718")) {
					entityIds.add(product.getId());
				}
			}
		} else {
			for (Product product : getProductGroup(MobileSession.get().getContract().getBusinessArea(), productGroupType).getProducts()) {
				entityIds.add(product.getId());
			}
		}

		wrapper.setComponent(formContainer.addSelectSinglePanel(wrapper.getKey(), entityIds, renderer == null ? new IChoiceRenderer() {
			@Override
			public Object getDisplayValue(Object value) {
				if (NO_CHOICE_ENTITY_ID.equals(value)) {
					return noChoiceEntityLabel;
				}
				if (NON_ENTITY_ID.equals(value)) {
					return nonEntityChoiceLabel;
				}
				return ProductDao.lookup().findById((Long) value).getPublicName();
			}

			@Override
			public String getIdValue(Object object, int index) {
				return "" + index;
			}
		} : renderer, new BootstrapSelectOptions()));

		wrapper.getComponent().add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateRestOfFields(target, components, wrapper);
			}
		});

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<ProductCountOrAllEditor> addProductRowEditorWrapper(
			final ValueMap values, Map<String, String> labelMap, ProductCountOrAll productRow,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<ProductCountOrAllEditor> wrapper = new LocationAccessComponentWrapper<ProductCountOrAllEditor>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), productRow);
		labelMap.put(wrapper.getKey(), label);

		ProductCountOrAllEditorPanel prp = new ProductCountOrAllEditorPanel(formContainer, wrapper.getKey());
		formContainer.addCustomComponent(prp);
		wrapper.setComponent((ProductCountOrAllEditor) prp.getComponent());

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<SelectProductCountEditor> addSelectProductCountEditorWrapper(
			final ValueMap values, Map<String, String> labelMap, ProductCountAndInstallation productRow,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<SelectProductCountEditor> wrapper = new LocationAccessComponentWrapper<SelectProductCountEditor>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), productRow);
		labelMap.put(wrapper.getKey(), label);

		SelectProductCountEditorPanel prp = new SelectProductCountEditorPanel(formContainer, wrapper.getKey(), false) {
			@Override
			public void onEvent(IEvent<?> event) {
				if (event.getPayload() instanceof ValueChangedEvent) {
					ValueChangedEvent valueChangedEvent = (ValueChangedEvent) event.getPayload();
					values.put(wrapper.getKey(), new ProductCountAndInstallation(productRow.getProduct(), null, (Integer) valueChangedEvent.getNewValue(), null)); // null for existing!
					updateRestOfFields(((ValueChangedEvent) event.getPayload()).getTarget(), components, wrapper);
				}
			}
		};
		formContainer.addCustomComponent(prp);
		wrapper.setComponent((SelectProductCountEditor) prp.getComponent());

		components.add(wrapper);
		return wrapper;
	}

	public static LocationAccessComponentWrapper<SimpleProductCountEditor> addSimpleProductCountEditorWrapper(
			final ValueMap values, Map<String, String> labelMap, SimpleProductCount productRow,
			final String baseKey, final int index, ComponentContainerPanel<ValueMap> formContainer, final String label,
			List<LocationAccessComponentWrapper> components) {

		LocationAccessComponentWrapper<SimpleProductCountEditor> wrapper = new LocationAccessComponentWrapper<SimpleProductCountEditor>();
		wrapper.setLabel(label);
		wrapper.setBaseKey(baseKey);
		wrapper.setIndex(index);
		wrapper.setKey(makeKey(baseKey, index));

		values.put(wrapper.getKey(), productRow);
		labelMap.put(wrapper.getKey(), label);

		SimpleProductCountEditorPanel prp = new SimpleProductCountEditorPanel(formContainer, wrapper.getKey(), false) {
			@Override
			public void onEvent(IEvent<?> event) {
				if (event.getPayload() instanceof ValueChangedEvent) {
					ValueChangedEvent valueChangedEvent = (ValueChangedEvent) event.getPayload();
					values.put(wrapper.getKey(), new SimpleProductCount(productRow.getProduct(), (Integer) valueChangedEvent.getNewValue()));
					updateRestOfFields(((ValueChangedEvent) event.getPayload()).getTarget(), components, wrapper);
				}
			}
		};
		formContainer.addCustomComponent(prp);
		wrapper.setComponent((SimpleProductCountEditor) prp.getComponent());

		components.add(wrapper);
		return wrapper;
	}

	public static MobileProductGroup getProductGroup(BusinessArea businessArea, MobileProductGroupEnum groupValue) {
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
}

