package dk.jyskit.salescloud.application.pages.switchboard.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;

public class SwitchboardBundleForm extends Form<ArrayList<BundleSelection>> {
	private static final long serialVersionUID = 1L;
	
	private enum ProductType {PRIMARY, SECONDARY, TERTIARY};
	
	@Inject
	private MobileProductBundleDao bundleDao;

	public SwitchboardBundleForm(String id, final IModel<ArrayList<BundleSelection>> bundleSelectionModel) {
		super(id, bundleSelectionModel);
		
		final Map<ProductBundle, AjaxCheckBox> bundleToCheckBox = new HashMap<ProductBundle, AjaxCheckBox>();

		IModel<List<? extends BundleSelection>> firstBundleSelectionModel = null;
		IModel<List<? extends BundleSelection>> secondBundleSelectionModel = null;

		if (MobileSession.get().isBusinessAreaOnePlus()) {
			firstBundleSelectionModel = Model.ofList(Lists.newArrayList(bundleSelectionModel.getObject().get(0)));
			secondBundleSelectionModel = Model.ofList(Lists.newArrayList(bundleSelectionModel.getObject()));
			secondBundleSelectionModel.getObject().remove(0);		// Mobile only
		} else {
			firstBundleSelectionModel = Model.ofList(Lists.newArrayList(bundleSelectionModel.getObject()));
		}

		addBundles(this, "firstBundles", firstBundleSelectionModel, bundleSelectionModel, bundleToCheckBox);

		WebMarkupContainer allSecondBundles = new WebMarkupContainer("allSecondBundles");
		add(allSecondBundles);
		addBundles(allSecondBundles, "secondBundles", secondBundleSelectionModel, bundleSelectionModel, bundleToCheckBox);
		if (MobileSession.get().getContract().getContractMode().equals(MobileContractMode.CONVERSION_1_TO_1)) {
			allSecondBundles.setVisible(false);
		}
	}

	private void addBundles(WebMarkupContainer parent, String wicketId, IModel<List<? extends BundleSelection>> bundleSelectionModel, final IModel<ArrayList<BundleSelection>> bothBundleSelectionModel,
							Map<ProductBundle, AjaxCheckBox> bundleToCheckBox) {
		parent.add(new ListView<BundleSelection>(wicketId, bundleSelectionModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<BundleSelection> item) {
				final MobileProductBundle productBundle = item.getModelObject().getBundle();
				if (productBundle == null) {
					item.add(new Label("name", "Ingen omstilling"));
				} else {
					item.add(new Label("name", productBundle.getPublicName()));
				}

				item.add(new ListView<Product>("primaryProducts", new ProductsModel(item.getModelObject().getBundle(), ProductType.PRIMARY)) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<Product> item) {
						item.add(new Label("productName", item.getModelObject().getPublicName()));
					}
				});

				AjaxCheckBox countCheckBox = new AjaxCheckBox("bundleSelection", new PropertyModel<Boolean>(item.getModelObject(), "selected")) {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						for (BundleSelection count : bothBundleSelectionModel.getObject()) {
							if (!Objects.equals(count.getBundle(), productBundle)) {
								count.setSelected(false);
								bundleToCheckBox.get(count.getBundle()).setEnabled(true);
								target.add(bundleToCheckBox.get(count.getBundle()));
							}
							bundleToCheckBox.get(productBundle).setEnabled(false);
							target.add(bundleToCheckBox.get(productBundle));
							onBundleSelected(target, productBundle);
						}
					}
				};
				if (item.getModelObject().isSelected()) {
					countCheckBox.setEnabled(false);
				}
				countCheckBox.setOutputMarkupId(true);
				bundleToCheckBox.put(productBundle, countCheckBox);

				item.add(countCheckBox);
			}
		});
	}

	public void onBundleSelected(AjaxRequestTarget target, MobileProductBundle productBundle) {
	}

	class ProductsModel extends LoadableDetachableModel<List<Product>> {

		private ProductType type;
		private MobileProductBundle bundle;

		public ProductsModel(MobileProductBundle bundle, ProductType type) {
			this.bundle = bundle;
			this.type = type;
		}
		
		@Override
		protected List<Product> load() {
			List<Product> result = new ArrayList<>();
			if (bundle != null) {
				for (BundleProductRelation relation : bundle.getProducts()) {
					if ((relation.getProduct() != null) && !((MobileProduct) relation.getProduct()).isExcludeFromConfigurator()) {
						result.add(relation.getProduct());
					}
				}
				Collections.sort(result, new Comparator<Product>() {
					@Override
					public int compare(Product o1, Product o2) {
						return Long.valueOf(
								o1.getProductGroup().getSortIndex() * 1000 + o1.getSortIndex()).compareTo(
										o2.getProductGroup().getSortIndex() * 1000 + o2.getSortIndex());
					}
				});
			}
			return result;
		}
		
	}

}