package dk.jyskit.salescloud.application.pages.mixbundles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;

@Slf4j
public abstract class MixBundleEditorPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private Product speechTime;
	private Product dataAmount;

	public MixBundleEditorPanel(String id, final IModel<MobileProductBundle> mixBundleModel, 
			final Map<Long, Product> oldSpeechTimes, final Map<Long, Product> oldDataAmounts) {
		super(id);
		
		BusinessArea businessArea = CoreSession.get().getBusinessArea();
		
		// --- Speech ---
		{
			ProductGroup speechTimeGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME.getKey());
			
			speechTime = null; 
			for (Product product : onlyEnabledProducts(speechTimeGroup.getProducts())) {
				for (BundleProductRelation relation : mixBundleModel.getObject().getProducts()) {
					if ((relation.getProduct() != null) && relation.getProduct().equals(product)) {
						speechTime = product;
						oldSpeechTimes.put(mixBundleModel.getObject().getId(), product);
						break;
					}
				}
				if (speechTime != null) {
					break;
				}
			}
			if (speechTime == null) {
				log.error("Inconsistency detected");
			}
			
			RadioChoice radioChoice = new RadioChoice("speechTime", new PropertyModel(this, "speechTime"), onlyEnabledProducts(speechTimeGroup.getProducts()), new ChoiceRenderer<>("publicName", "id"));
			radioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				protected void onUpdate(AjaxRequestTarget target) {
					Product oldProduct = oldSpeechTimes.get(mixBundleModel.getObject().getId());
					if (!speechTime.getId().equals(oldProduct)) {
						mixBundleModel.getObject().removeProductRelation(oldProduct);
						
						BundleProductRelation relation = new BundleProductRelation(speechTime);
						mixBundleModel.getObject().addProductRelation(relation );
						
						onChange(target);
					}
					oldSpeechTimes.put(mixBundleModel.getObject().getId(), speechTime);
				}
			});
			add(radioChoice);
			
			ProductGroup speechGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH.getKey());
			List<MobileProduct> speechAddonProducts = new ArrayList<>();
			for (Product product : onlyEnabledProducts(speechGroup.getProducts())) {
				speechAddonProducts.add((MobileProduct) product);
			}
			
			ListView<MobileProduct> speechAddonCheckBoxes = new ListView<MobileProduct>("speechAddonCheckBoxes", speechAddonProducts) {
				@Override
				protected void populateItem(ListItem<MobileProduct> item) {
					boolean isInBundle = false;
					for(BundleProductRelation relation: mixBundleModel.getObject().getProducts()) {
						MobileProduct product = (MobileProduct) relation.getProduct();
						if ((product != null) && product.equals(item.getModelObject())) {
							isInBundle = true;
							break;
						}
					}
					
					final ProductSelectedValue value = new ProductSelectedValue(item.getModelObject(), isInBundle);
					
					item.add(new AjaxCheckBox("checkBox", new PropertyModel(value, "selected")) {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (getModelObject()) {
								BundleProductRelation relation = new BundleProductRelation(value.getProduct());
								mixBundleModel.getObject().addProductRelation(relation );
							} else {
								mixBundleModel.getObject().removeProductRelation(value.getProduct());
							}
							
							onChange(target);
						}
					});
					item.add(new Label("label", item.getModelObject().getPublicName()));
				}
			};
			add(speechAddonCheckBoxes);
		}
		
		// --- Data ---
		{
			ProductGroup dataAmountGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT.getKey());
			dataAmount = null; 
			for (Product product : onlyEnabledProducts(dataAmountGroup.getProducts())) {
				for (BundleProductRelation relation : mixBundleModel.getObject().getProducts()) {
					if ((relation.getProduct() != null) && relation.getProduct().equals(product)) {
						dataAmount = product;
						oldDataAmounts.put(mixBundleModel.getObject().getId(), product);
						break;
					}
				}
				if (dataAmount != null) {
					break;
				}
			}
			if (dataAmount == null) {
				log.error("Inconsistency detected");
			}
			
			RadioChoice radioChoice = new RadioChoice("dataAmount", new PropertyModel(this, "dataAmount"), onlyEnabledProducts(dataAmountGroup.getProducts()), new ChoiceRenderer<>("publicName", "id"));
			radioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				protected void onUpdate(AjaxRequestTarget target) {
					Product oldProduct = oldDataAmounts.get(mixBundleModel.getObject().getId());
					if (!dataAmount.equals(oldProduct)) {
						mixBundleModel.getObject().removeProductRelation(oldProduct);
						
						BundleProductRelation relation = new BundleProductRelation(dataAmount);
						mixBundleModel.getObject().addProductRelation(relation );
						
						onChange(target);
					}
					oldDataAmounts.put(mixBundleModel.getObject().getId(), dataAmount);
				}
			});
			add(radioChoice);
			
			ProductGroup dataGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA.getKey());
			List<MobileProduct> dataAddonProducts = new ArrayList<>();
			for (Product product : onlyEnabledProducts(dataGroup.getProducts())) {
				dataAddonProducts.add((MobileProduct) product);
			}
			
			ListView<MobileProduct> dataAddonCheckBoxes = new ListView<MobileProduct>("dataAddonCheckBoxes", dataAddonProducts) {
				@Override
				protected void populateItem(ListItem<MobileProduct> item) {
					boolean isInBundle = false;
					for(BundleProductRelation relation: mixBundleModel.getObject().getProducts()) {
						MobileProduct product = (MobileProduct) relation.getProduct();
						if ((product != null) && product.equals(item.getModelObject())) {
							isInBundle = true;
							break;
						}
					}
					
					final ProductSelectedValue value = new ProductSelectedValue(item.getModelObject(), isInBundle);
					
					item.add(new AjaxCheckBox("checkBox", new PropertyModel(value, "selected")) {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (getModelObject()) {
								BundleProductRelation relation = new BundleProductRelation(value.getProduct());
								mixBundleModel.getObject().addProductRelation(relation );
							} else {
								mixBundleModel.getObject().removeProductRelation(value.getProduct());
							}
							
							onChange(target);
						}
					});
					item.add(new Label("label", item.getModelObject().getPublicName()));
				}
			};
			add(dataAddonCheckBoxes);
		}
		
		// --- Addons ---
		{
			ProductGroup addonGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON.getKey());
			List<MobileProduct> addonProducts = new ArrayList<>();
			for (Product product : onlyEnabledProducts(addonGroup.getProducts())) {
				addonProducts.add((MobileProduct) product);
			}
			
			ListView<MobileProduct> addonCheckBoxes = new ListView<MobileProduct>("addonCheckBoxes", addonProducts) {
				@Override
				protected void populateItem(ListItem<MobileProduct> item) {
					boolean isInBundle = false;
					for(BundleProductRelation relation: mixBundleModel.getObject().getProducts()) {
						MobileProduct product = (MobileProduct) relation.getProduct();
						if ((product != null) && product.equals(item.getModelObject())) {
							isInBundle = true;
							break;
						}
					}
					
					final ProductSelectedValue value = new ProductSelectedValue(item.getModelObject(), isInBundle);
					
					item.add(new AjaxCheckBox("checkBox", new PropertyModel(value, "selected")) {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (getModelObject()) {
								BundleProductRelation relation = new BundleProductRelation(value.getProduct());
								mixBundleModel.getObject().addProductRelation(relation );
							} else {
								mixBundleModel.getObject().removeProductRelation(value.getProduct());
							}
							
							onChange(target);
						}
					});
					item.add(new Label("label", item.getModelObject().getPublicName()));
				}
			};
			add(addonCheckBoxes);
		}
	}

	private List<Product> onlyEnabledProducts(List<Product> products) {
		List<Product> enabledProducts = new ArrayList<>();
		for (Product product : products) {
			if (!((MobileProduct) product).isExcludeFromConfigurator()) {
				enabledProducts.add(product);
			}
		}
		return enabledProducts;
	}
	
	public abstract void onChange(AjaxRequestTarget target);
	
	@Data
	class ProductSelectedValue implements Serializable {
		private MobileProduct product;
		private boolean selected;
		
		public ProductSelectedValue(MobileProduct product, boolean selected) {
			this.product	= product;
			this.selected 	= selected;
		}
	}
}
