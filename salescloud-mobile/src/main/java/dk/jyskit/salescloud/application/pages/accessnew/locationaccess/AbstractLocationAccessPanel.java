package dk.jyskit.salescloud.application.pages.accessnew.locationaccess;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
@Slf4j
public abstract class AbstractLocationAccessPanel extends Panel {
	@Inject
	private PageNavigator navigator;

	@Inject
	private ProductDao productDao;

	@Inject
	private ContractSaver contractSaver;

	protected ValueMap values = new ValueMap();
	protected Map<String, String> labelMap = new HashMap<>();
	protected List<LocationAccessComponentWrapper> wrappers;
	private Integer tabIndex;

	public AbstractLocationAccessPanel(String id, Integer tabIndex) {
		super(id);
		this.tabIndex = tabIndex;

		final MobileContract contract = (MobileContract) CoreSession.get().getContract();

		Jsr303Form<ValueMap> form = new Jsr303Form<>("form", values);
		add(form);

//		Map<String, String> labelMap = new HashMap<>();

		form.setLabelStrategy(new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace())));

		form.setLabelSpans(SmallSpanType.SPAN5);
		form.setEditorSpans(SmallSpanType.SPAN7);

		wrappers = new ArrayList<>();

		if (contract.getBusinessArea().isOnePlus()) {
			addBundleForm(contract, labelMap, form, wrappers, tabIndex);
		} else {
			for (int i = 0; i < contract.getLocationBundles().size(); i++) {
				addBundleForm(contract, labelMap, form, wrappers, i);
			}
			addButtons(contract, labelMap, wrappers, form);
		}
	}

	private void addBundleForm(MobileContract contract, Map<String, String> labelMap, Jsr303Form<ValueMap> form,
							   List<LocationAccessComponentWrapper> wrappers, int bi) {
		ComponentContainerPanel<ValueMap> formContainer = form;

//		String groupName = getGroupName(contract, bi);
//		if (groupName != null) {
//			formContainer = form.createGroup(Model.of(groupName));
//		}

		initBundle(contract, values, labelMap, bi);

		addAllComponents(contract, labelMap, formContainer, wrappers, bi);

		for (LocationAccessComponentWrapper c : wrappers) {
			c.update(null);
		}

		labelMap.put("action.delete", "Slet");
		AjaxButton button = formContainer.addButton("action.delete", Buttons.Type.Info, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				onDelete(contract, bi);
			}
		});
		button.add(AttributeModifier.append("style", "margin-bottom: 20px"));
	}

	protected abstract void onDelete(MobileContract contract, int bi);

	public boolean saveAndNavigate(final MobileContract contract, Class<? extends WebPage> page, int tabIndex, AjaxRequestTarget target) {
		return true;
	}

//	protected String getGroupName(MobileContract contract, int bi) {
//		if (contract.getBusinessArea().isOnePlus()) {
//			return null;
//		} else {
//			return "Fiber Erhverv";
//		}
//	}

	protected void initBundle(MobileContract contract, ValueMap values, Map<String, String> labelMap, int bi) {
	}

	protected abstract void addAllComponents(final MobileContract contract,
			Map<String, String> labelMap, ComponentContainerPanel<ValueMap> formContainer, List<LocationAccessComponentWrapper> components, int bi);

	private void addButtons(final MobileContract contract, Map<String, String> labelMap,
							List<LocationAccessComponentWrapper> wrappers, Jsr303Form<ValueMap> form) {
		labelMap.put("action.prev", "Tilbage");
		labelMap.put("action.next", "Videre");
		labelMap.put("action.add", "Tilføj lokation");
		labelMap.put("action.delete", "Slet lokation");

		@SuppressWarnings("unused")
		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, navigator.prev(getWebPage()), tabIndex, target);
			}
		});
		@SuppressWarnings("unused")
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, navigator.next(getWebPage()), tabIndex, target);
			}
		});

		@SuppressWarnings("unused")
		AjaxButton addButton = form.addSubmitButton("action.add", Buttons.Type.Info, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, null, tabIndex, target);
//				onSave(contract);
			}
		});
	}

	private String getFormKey(final MobileProductGroupEnum deviceGroup, int bundleIndex) {
		return deviceGroup.getKey().replace('.', '_') + '_' + bundleIndex;
	}

	protected LocationAccessComponentWrapper getWrapperByKey(String key) {
		for (LocationAccessComponentWrapper w: wrappers) {
			if (key.equals(w.getKey())) {
				return w;
			}
		}
		return null;
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
}
