package dk.jyskit.salescloud.application.pages.switchboard.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallationPanel;
import org.apache.wicket.util.value.ValueMap;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.SwitchboardInitializer;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.defaultimpl.CoreProductRelationTypeProvider;
import dk.jyskit.salescloud.application.extensions.MobileProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Constants;
import dk.jyskit.salescloud.application.model.CountAndInstallation;
import dk.jyskit.salescloud.application.model.InstallationType;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ValueMapLabelStrategy;

public class SwitchboardAddonsMainPanel extends PanelWithSave {
	@Inject
	private ProductGroupDao productGroupDao;
	
	private ValueMap valueMap;

	private ProductGroup productGroup;
	
	public SwitchboardAddonsMainPanel(String wicketId) {
		super(wicketId);
		
		BusinessArea businessArea = MobileSession.get().getBusinessArea();
		MobileContract contract = MobileSession.get().getContract();
		
		valueMap = new ValueMap();
		
		Jsr303Form<ValueMap> form = new Jsr303Form<>("form", valueMap, true);
		add(form);
		
		form.setLabelSpans(SmallSpanType.SPAN5);
		form.setEditorSpans(SmallSpanType.SPAN7);
		
		form.setLabelStrategy(new ValueMapLabelStrategy());
		
		productGroup = productGroupDao.findByBusinessAreaAndUniqueName(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON.getKey());

		ProductGroup switchboardProductGroup = productGroupDao.findByBusinessAreaAndUniqueName(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey());
		boolean noSwitchboard = true;
		for (OrderLine orderLine : contract.getOrderLines()) {
			if (orderLine.getBundle() != null && 
					((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE) &&
					orderLine.getTotalCount() > 0) {
				noSwitchboard = false;
				break;
			}
		}

		List<Product> products;
		if (noSwitchboard) {
			products = new ArrayList<>();
		} else {
			products = productGroup.getProducts();
		}
		
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return Long.valueOf(o1.getSortIndex()).compareTo(Long.valueOf(o2.getSortIndex()));
			}
		});
		for (Product product : products) {
//			List<ProductRelation> productRelations = businessArea.getProductRelations(product, false, true, MobileProductRelationTypeProvider.TDC_INSTALLATION);
//			if (productRelations.size() > 0) {
//				// This is an installation product, skip it.
//				continue;
//			}
//			
			if (((MobileProduct) product).isExcludeFromConfigurator() || ((MobileProduct) product).isTdcInstallation()) {
				// This product should not be included
				continue;
			}
			
			// Already included (eg. as alternative to previous product)?
			boolean alreadyUsed = false;
			for (Object value : valueMap.values()) {
				ProductCountAndInstallation productRow = (ProductCountAndInstallation) value;
				if (productRow.getAlternatives() != null) {
					if (productRow.getAlternatives().contains(product)) {
						alreadyUsed = true;
						break;
					}
				}
			}
			if (alreadyUsed) {
				continue;  // skip product
			}
			
			// Special rule for Voquant Omstilling Ekstra
			if ("3236100".equals(product.getProductId())) {
				ProductBundle omstillingBundle = null;
				for (ProductBundle productBundle: contract.getCampaigns().get(0).getProductBundles()) {
					if (SwitchboardInitializer.BUNDLE_OMSTILLING.equals(productBundle.getPublicName())) {
						omstillingBundle = productBundle;
						break;
					}
				}
				if (omstillingBundle != null) {
					OrderLine orderLine = contract.getOrderLine(omstillingBundle);
					if ((orderLine != null) && (orderLine.getTotalCount() > 0)) {
						// Not Omstilling Ekstra, so skip product
						continue;
					}
				}
			}
			
			String label;
			ProductCountAndInstallation productRow = null;
			Product selectedProduct = product;
			List<Product> alternatives = null;
			List<Product> productsToCheckForTDCInstallation;
			int productCountNew = 0;
			int productCountExisting = 0;
			
			List<ProductRelation> productRelations = businessArea.getProductRelations(product, true, true, CoreProductRelationTypeProvider.ALTERNATIVE_PRODUCTS);
			if (productRelations.size() == 1) {
				// Find selected product, if any
				for (Product alternative : productRelations.get(0).getProducts()) {
					for (OrderLine orderLine : contract.getOrderLines()) {
						if ((orderLine.getTotalCount() > 0) && (alternative.equals(orderLine.getProduct()))) {
							selectedProduct = alternative;
							break;
						}
					}
				}
				label = productRelations.get(0).getDisplayName();
				alternatives = productRelations.get(0).getProducts();
				productCountNew = 1;
				productsToCheckForTDCInstallation = alternatives;
			} else {
				label = product.getPublicName();
				productCountNew 		= MobileSession.get().getContract().getCountNewForProduct(product);
				productCountExisting 	= MobileSession.get().getContract().getCountExistingForProduct(product);
				productsToCheckForTDCInstallation = new ArrayList<>();
				productsToCheckForTDCInstallation.add(product);
			}
			
			boolean installationSelected 	= false;
			boolean hasInstallationProducts = false;
			boolean installationEditable 	= false;
			
			if (!contract.getInstallationType().equals(InstallationType.NONE)) {
				for (Product p : productsToCheckForTDCInstallation) {
					List<ProductRelation> installationProductRelation = businessArea.getProductRelations(p, true, false, MobileProductRelationTypeProvider.ADD_ORDERLINES_N);
					for (ProductRelation productRelation : installationProductRelation) {
						for(Product installationProduct : productRelation.getProducts()) {
							if (((MobileProduct) installationProduct).isTdcInstallation()) {
								hasInstallationProducts = true;
								if (contract.getInstallationType().equals(InstallationType.PARTNER)) {
									installationEditable = true;
									if (MobileSession.get().getContract().getCountNewForProduct(installationProduct) > 0) {
										installationSelected = true;
										break;
									}
								} else {
									installationSelected = true;
								}
							}
						}
					}
				}
			}

			productRow = new ProductCountAndInstallation(selectedProduct, alternatives, productCountNew, productCountExisting, hasInstallationProducts, installationSelected, installationEditable);

			String fieldName = ValueMapLabelStrategy.convertLabelTextToFieldName(label);
			valueMap.put(fieldName, productRow);
			form.addCustomComponent(new ProductCountAndInstallationPanel(form, fieldName, productRow));
		}
	}

	@Override
	public boolean save() {
		Map<Product, List<CountAndInstallation>> productToCountsMap = new HashMap<>();
		for(Object value: valueMap.values()) {
			ProductCountAndInstallation productRow = (ProductCountAndInstallation) value;
			CountAndInstallation countAndInstallation = new CountAndInstallation();
			countAndInstallation.setInstallationSelected(productRow.isInstallationSelected());
			
			if ((productRow.getCountNew() > 0) && ((MobileProduct) productRow.getProduct()).isSubscriberProduct()) {
				countAndInstallation.setCountNew(Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT);
			} else {
				countAndInstallation.setCountNew(productRow.getCountNew());
			}
			
			if ((productRow.getCountExisting() > 0) && ((MobileProduct) productRow.getProduct()).isSubscriberProduct()) {
				countAndInstallation.setCountExisting(Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT);
			} else {
				countAndInstallation.setCountExisting(productRow.getCountExisting());
			}
			
			List<CountAndInstallation> countAndInstallations = new ArrayList<>();
			countAndInstallations.add(countAndInstallation);
			productToCountsMap.put(productRow.getProduct(), countAndInstallations);
		}
		MobileSession.get().getContract().adjustOrderLinesForProducts(productGroup, productToCountsMap, null);
		return true;
	}

}
