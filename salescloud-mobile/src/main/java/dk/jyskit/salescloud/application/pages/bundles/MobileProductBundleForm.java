package dk.jyskit.salescloud.application.pages.bundles;

import java.util.*;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundleEditorPanel;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundlesPage;
import dk.jyskit.waf.wicket.components.containers.AjaxContainer;
import dk.jyskit.waf.wicket.components.forms.behaviours.FloatingLabelBehaviour;
import org.apache.wicket.util.value.ValueMap;

public class MobileProductBundleForm extends Form<ArrayList<BundleCount>> {

	private ValueMap addOnsValueMap;

	private enum ProductType {PRIMARY, SECONDARY, TERTIARY};

	private static final long serialVersionUID = 1L;
	private boolean isStandardBundles;
	private NumberTextField<Integer> countNew;
	private NumberTextField<Integer> countExisting;

	@Inject
	private CanOrderFilter canOrderFilter;

	@Inject
	private MobileProductBundleDao bundleDao;

	public MobileProductBundleForm(String id, IModel<ArrayList<BundleCount>> bundleCountModel, boolean standardBundles, final MixBundleEditorPanel mixBundleEditorPanel, 
			final BundleSelectionPanel bundleSelectionPanel, ValueMap addOnsValueMap) {
		super(id, bundleCountModel);
		this.isStandardBundles 	= standardBundles;
		this.addOnsValueMap 	= addOnsValueMap;
		
		final List<CheckBox> bundleCheckBoxes = new ArrayList<>();
		
		add(new ListView<BundleCount>("bundles", bundleCountModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<BundleCount> item) {
				BundleCount bundleCount = item.getModelObject();

				List<ProductCountAndInstallationPanel> addOnList = Lists.newArrayList();

				if (MobileSession.get().isBusinessAreaOnePlus() || MobileSession.get().isBusinessAreaTdcOffice()) {
					item.add(AttributeModifier.append("class", "twocols"));
				}
				final MobileProductBundle productBundle = item.getModelObject().getBundle();
				if (productBundle.hasFlag("linebreak")) {
					item.add(AttributeModifier.append("style", "clear:both"));
				}
				item.add(new Label("name", productBundle.getPublicName()));
				
				LoadableDetachableModel<Boolean> checkBoxModel = new LoadableDetachableModel<Boolean>() {
					@Override
					protected Boolean load() {
						return (productBundle.getId().equals(((MixBundlesPage) getPage()).getSelectedBundle().getId()));
					}
				};

				CheckBox bundleCheckBox = new CheckBox("checkbox", checkBoxModel);

				AjaxFormSubmitBehavior submitBehavior = new AjaxFormSubmitBehavior(MobileProductBundleForm.this, "onclick") {
			        @Override
			        protected void onSubmit(AjaxRequestTarget target) {
						bundleDao.save(((MixBundlesPage) getPage()).getSelectedBundle());
						MobileSession.get().setSelectedMixBundle(bundleCount.getBundle());
						bundleSelectionPanel.saveAndNavigate(target, false, false, false, true);
			        }

			        @Override
			        protected void onError(AjaxRequestTarget target) {
			        }
			    };
			    bundleCheckBox.add(submitBehavior);
				
				if (isStandardBundles) {
					bundleCheckBox.setVisible(false);
				}
				item.add(bundleCheckBox);
				bundleCheckBoxes.add(bundleCheckBox);
				
//				List<SpanType> clearFixColTypes = new ArrayList<>();
//				if (item.getIndex() > 0 && (item.getIndex() + 1) % 4 == 0) {
//					clearFixColTypes.add(SpanType.MD);
//				}
//				if (item.getIndex() > 0 && (item.getIndex() + 1) % 2 == 0) {
//					clearFixColTypes.add(SpanType.SM);
//				}
//				
//				item.add(new ListView<SpanType>("clearFixes", clearFixColTypes) {
//					@Override
//					protected void populateItem(ListItem<SpanType> clearFixItem) {
//						clearFixItem.add(AttributeModifier.append("class", "clearfix no" + item.getIndex() + " visible-" + 
//								clearFixItem.getModelObject().getText() + "-block"));
//					}
//				});

				item.add(new ListView<Product>("primaryProducts", new ProductsModel(bundleCount.getBundle(), ProductType.PRIMARY, ProductAccessType.INCLUDED)) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});

				item.add(new ListView<Product>("secondaryProducts", new ProductsModel(item.getModelObject().getBundle(), ProductType.SECONDARY, ProductAccessType.INCLUDED)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});

				item.add(new ListView<Product>("tertiaryProducts", new ProductsModel(item.getModelObject().getBundle(), ProductType.TERTIARY, ProductAccessType.INCLUDED)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});
				
				AjaxContainer optionalProductsContainer = new AjaxContainer("optionalProductsContainer") {
					@Override
					protected void onConfigure() {
						for (BundleProductRelation rel : bundleCount.getBundle().getProducts()) {
							if (rel.getProductAccessType().equals(ProductAccessType.OPTIONAL)) {
								setVisible(true);
								return;
							}
						}
						setVisible(false);
					}
				};
				item.add(optionalProductsContainer);
				
				optionalProductsContainer.add(new ListView<Product>("optionalProducts", new ProductsModel(bundleCount.getBundle(), null, ProductAccessType.OPTIONAL)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});
				
				AjaxContainer nonoptionalProductsContainer = new AjaxContainer("nonoptionalProductsContainer") {
					@Override
					protected void onConfigure() {
						for (BundleProductRelation rel : bundleCount.getBundle().getProducts()) {
							if (rel.getProductAccessType().equals(ProductAccessType.NON_OPTIONAL)) {
								setVisible(true);
								return;
							}
						}
						setVisible(false);
					}
				};
				item.add(nonoptionalProductsContainer);
				
				nonoptionalProductsContainer.add(new ListView<Product>("nonoptionalProducts", new ProductsModel(bundleCount.getBundle(), null, ProductAccessType.NON_OPTIONAL)) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});

				// ----------------

				countNew = new NumberTextField<Integer>("countNew", new PropertyModel<Integer>(bundleCount, "countNew"));
				countNew.setOutputMarkupId(true);
				countNew.setOutputMarkupPlaceholderTag(true);
				if (!MobileSession.get().getContract().getContractMode().isNewAccount()) {
					countNew.setVisible(false);
				}
				countNew.add(AttributeModifier.append("min", "0"));
				countNew.add(AttributeModifier.append("max", "999"));
				if (MobileSession.get().getContract().getContractMode().isNewAccount() &&
						MobileSession.get().getContract().getContractMode().isExistingAccount()) {
					countNew.add(new FloatingLabelBehaviour());
					countNew.add(AttributeModifier.append("placeholder", "Nysalg"));
				}
				item.add(countNew);
				countNew.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						addOnList.forEach(productRowPanel -> {
							productRowPanel.onMaxLimitsChanged(bundleCount.getCountNew(), bundleCount.getCountExisting(), target);
						});
					}
				});

				// ----------------

				countExisting = new NumberTextField<Integer>("countExisting",
						new PropertyModel<Integer>(bundleCount, "countExisting"));
				countExisting.setOutputMarkupId(true);
				countExisting.setOutputMarkupPlaceholderTag(true);
				if (!MobileSession.get().getContract().getContractMode().isExistingAccount()) {
					countExisting.setVisible(false);
				}
				countExisting.add(new FloatingLabelBehaviour());
				countExisting.add(AttributeModifier.append("min", "0"));
				countExisting.add(AttributeModifier.append("max", "999"));

				if (MobileSession.get().isBusinessAreaOnePlus()) {
					countExisting.add(AttributeModifier.append("placeholder", "Konvertering"));
				} else if (MobileSession.get().isBusinessAreaTdcOffice()) {
					countExisting.add(AttributeModifier.append("placeholder", "Tilkøb"));
				} else {
					countExisting.add(AttributeModifier.append("placeholder", "Genforhandling"));
				}
				item.add(countExisting);
				countExisting.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						addOnList.forEach(productRowPanel -> {
							productRowPanel.onMaxLimitsChanged(bundleCount.getCountNew(), bundleCount.getCountExisting(), target);
						});
					}
				});

				if (MobileSession.get().isBusinessAreaOnePlus()) {
					// -----------------------------------------------------
					// Add-ons
					// -----------------------------------------------------

					Jsr303Form<ValueMap> form = new Jsr303Form<>("addonsForm", addOnsValueMap, true);
					item.add(form);

					form.setLabelSpans(SmallSpanType.SPAN6);
					form.setEditorSpans(SmallSpanType.SPAN6);
//					form.setLabelStrategy(new ValueMapLabelStrategy());
					Map<String, String> labelMap = new HashMap();
					form.setLabelStrategy(new MapLabelStrategy(labelMap, null));

					addAddOns(form, addOnsValueMap, addOnList, labelMap, bundleCount.getBundle(), "Funktioner", MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_FUNCTIONS);
//					addAddOns(form, addOnsValueMap, addOnList, labelMap, bundleCount.getBundle(), "Roaming", MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING);
					addAddOns(form, addOnsValueMap, addOnList, labelMap, bundleCount.getBundle(), "Roaming - ILD", MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING_ILD);
				} else {
					item.add(new EmptyPanel("addonsForm"));
				}
			}
		});
	}

	private void addAddOns(Jsr303Form<ValueMap> form, ValueMap valueMap, List<ProductCountAndInstallationPanel> productRowPanels, Map<String, String> labelMap,
						   MobileProductBundle bundle, String groupLabel, MobileProductGroupEnum productGroupType) {
		final MutableObject<FormGroup<ValueMap>> group = new MutableObject<>();

		bundle.getProducts(mobileProduct -> mobileProduct.isInGroup(productGroupType))
			.forEach(product -> {
				if (canOrderFilter.accept(product)) {
					int subIndex = MobileSession.get().getContract().getSubIndexOfUserProfileBundle(bundle);
					int productCountNew 		= MobileSession.get().getContract().getCountNewForProduct(product, subIndex);
					int productCountExisting 	= MobileSession.get().getContract().getCountExistingForProduct(product, subIndex);

					if (product.getMinCount() != null) {
						ProductCountAndInstallation productRow = new ProductCountAndInstallation(product, null, productCountNew, productCountExisting, false, false, false);

						String fieldName = "" + subIndex + "¤" + product.getId();
						valueMap.put(fieldName, productRow);
						if (group.getValue() == null) {
							group.setValue(form.createGroup(Model.of(groupLabel)));
						}

						labelMap.put(fieldName, product.getPublicName());

						ProductCountAndInstallationPanel panel = new ProductCountAndInstallationPanel(form, fieldName, productRow);
						productRowPanels.add(panel);
						group.getValue().addCustomComponent(panel);
					}
				}
			});
	}

	class ProductsModel extends LoadableDetachableModel<List<Product>> {
		private ProductType type;
		private MobileProductBundle bundle;
		private ProductAccessType productAccessType;

		public ProductsModel(MobileProductBundle bundle, ProductType type, ProductAccessType productAccessType) {
			this.bundle = bundle;
			this.productAccessType = productAccessType;
			this.type = type;
		}
		
		@Override
		protected List<Product> load() {
			List<Product> result = new ArrayList<>();
//			if (bundle.isStandardBundle()) {
				for (BundleProductRelation relation : bundle.getProducts()) {
					if (canOrderFilter.accept(relation.getProduct()) &&
							(productAccessType.equals(relation.getProductAccessType()) && (relation.getProduct() != null) && !((MobileProduct) relation.getProduct()).isExcludeFromConfigurator())) {
						if (type == null) {
							result.add(relation.getProduct());
						} else if (ProductType.PRIMARY.equals(type)) {
							if (MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_SPEECH.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_DATA.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
								result.add(relation.getProduct());
							}
						} else if (ProductType.SECONDARY.equals(type)) {
							if (MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_INCLUDED.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_INCLUDED.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_INCLUDED.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
								result.add(relation.getProduct());
							}
						} else if (ProductType.TERTIARY.equals(type)) {
							if (MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
								MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
								result.add(relation.getProduct());
							}
						}
					}
				}
//			} else {
//				for (BundleProductRelation relation : bundle.getProducts()) {
//					if (ProductType.PRIMARY.equals(type)) {
//						if (MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) || 
//							MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
//							result.add(relation.getProduct());
//						}
//					} else if (ProductType.SECONDARY.equals(type)) {
//						if (MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
//							MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
//							MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON.getKey().equals(relation.getProduct().getProductGroup().getUniqueName()) ||
//							MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_INCLUDED.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
//							result.add(relation.getProduct());
//						}
//					} else if (ProductType.TERTIARY.equals(type)) {
////						if (MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_NON_DOMESTIC.getKey().equals(relation.getProduct().getProductGroup().getUniqueName())) {
////							result.add(relation.getProduct());
////						}
//					}
//				}
//			}
			Collections.sort(result, new Comparator<Product>() {
				@Override
				public int compare(Product o1, Product o2) {
					return Long.valueOf(o1.getProductGroup().getSortIndex() * 1000 + o1.getSortIndex()).compareTo(
						   Long.valueOf(o2.getProductGroup().getSortIndex() * 1000 + o2.getSortIndex()));
				}
			});
			return result;
		}
		
	}

}