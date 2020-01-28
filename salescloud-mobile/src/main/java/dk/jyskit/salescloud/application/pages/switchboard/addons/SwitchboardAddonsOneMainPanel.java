package dk.jyskit.salescloud.application.pages.switchboard.addons;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallation;
import dk.jyskit.salescloud.application.editors.productcountandinstallation.ProductCountAndInstallationPanel;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ValueMapLabelStrategy;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import java.util.*;
import java.util.stream.Collectors;

public class SwitchboardAddonsOneMainPanel extends PanelWithSave {
	@Inject
	private ProductGroupDao productGroupDao;

	private ValueMap valueMap;

	private ProductGroup productGroup;

	@Inject
	private CanOrderFilter canOrderFilter;

	public SwitchboardAddonsOneMainPanel(String wicketId, boolean poolsPage) {
		super(wicketId);
		
		BusinessArea businessArea = MobileSession.get().getBusinessArea();
		MobileContract contract = MobileSession.get().getContract();
		
		valueMap = new ValueMap();
		
		Jsr303Form<ValueMap> form = new Jsr303Form<>("form", valueMap, true);
		add(form);
		
		form.setLabelSpans(SmallSpanType.SPAN5);
		form.setEditorSpans(SmallSpanType.SPAN7);
		
		form.setLabelStrategy(new ValueMapLabelStrategy());

		if (poolsPage) {
			addGroup(form, businessArea, "Data", MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_DATA);
			addGroup(form, businessArea, "ILD", MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_ILD);
		} else {
			addGroup(form, businessArea, "Identitet", MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY);
			addGroup(form, businessArea, "Funktionalitet", MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_FEATURES);
		}
	}

	private void addGroup(Jsr303Form<ValueMap> form, BusinessArea businessArea, String groupLabel, MobileProductGroupEnum productGroupType) {
		FormGroup<ValueMap> group = form.createGroup(Model.of(groupLabel));
		productGroup = productGroupType.getProductGroup(businessArea);
		for (MobileProduct product : productGroup.getProducts().stream()
				.map(product -> (MobileProduct) product)
				.filter(mobileProduct -> (!mobileProduct.isExcludeFromConfigurator() && canOrderFilter.accept(mobileProduct)))
				.sorted(Comparator.comparing(Product::getSortIndex))
				.collect(Collectors.toList())) {

			int productCountNew 		= MobileSession.get().getContract().getCountNewForProduct(product);
			int productCountExisting 	= MobileSession.get().getContract().getCountExistingForProduct(product);

			if (product.getMinCount() != null) {
				ProductCountAndInstallation productRow = new ProductCountAndInstallation(product, null, productCountNew, productCountExisting, false, false, false);

				String fieldName = ValueMapLabelStrategy.convertLabelTextToFieldName(product.getPublicName());
				valueMap.put(fieldName, productRow);
				group.addCustomComponent(new ProductCountAndInstallationPanel(form, fieldName, productRow));
			}
		}
	}

	public boolean save() {
		Map<Product, List<CountAndInstallation>> productToCountsMap = new HashMap<>();
		for (Object value: valueMap.values()) {
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
		Set<ProductGroup> groups = new HashSet<>();
		productToCountsMap.keySet().forEach(product -> groups.add(product.getProductGroup()));
		groups.forEach(productGroup -> {
			MobileSession.get().getContract().adjustOrderLinesForProducts(productGroup, productToCountsMap, null);
		});

		MobileSession.get().getContract().adjustOrderLinesForRemoteInstallation();
		return true;
	}
}
