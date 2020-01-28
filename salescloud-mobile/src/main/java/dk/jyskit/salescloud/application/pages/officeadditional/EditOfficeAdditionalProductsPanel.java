package dk.jyskit.salescloud.application.pages.officeadditional;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.MediumSpanType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAll;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAllEditorPanel;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditOfficeAdditionalProductsPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private PageNavigator navigator;
	@Inject
	private ContractSaver contractSaver;
	@Inject
	private ProductDao productDao;

	private List<Product> supportProducts;

	private Jsr303Form<OfficeAdditionalProducts> form;

	private NotificationPanel notificationPanel;
	
	public EditOfficeAdditionalProductsPanel(String id, NotificationPanel notificationPanel) {
		super(id);
		this.notificationPanel = notificationPanel;
		
		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		final OfficeAdditionalProducts values = new OfficeAdditionalProducts();

		form = new Jsr303Form<>("form", values);
		add(form);
		
		form.setLabelSpans(MediumSpanType.SPAN6);
		form.setEditorSpans(MediumSpanType.SPAN6);
		
		Map<String, String> labelMap = new HashMap<>();
		MapLabelStrategy labelStrategy = new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace()));
		form.setLabelStrategy(labelStrategy);		
		
		{
			List<Product> products = productDao.findByBusinessAreaAndProductGroupUniqueName(MobileSession.get().getBusinessAreaEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey());
			supportProducts = new ArrayList<>();
			List<String> supportProductNames = new ArrayList<>();
			String currentSupport = "Ingen";
			supportProductNames.add(currentSupport);
			
			for (Product p : products) {
				MobileProduct product = (MobileProduct) p;   
				if (!product.isExcludeFromConfigurator()) {
					String key = product.getProductId();
					labelMap.put(key, product.getPublicName());
					if (product.hasFlag("option_support")) {
						supportProducts.add(product);
						supportProductNames.add(product.getPublicName());
						if (contract.hasOrderLineFor(product)) {
							currentSupport = product.getPublicName();
						}
					} else {
						if (Integer.valueOf(1).equals(product.getMaxCount())) {
							form.addCheckBox(key);
							List<OrderLine> orderLines = contract.getOrderLines(product);
							if (orderLines.size() > 0) {
								values.put(key, Boolean.valueOf(orderLines.get(0).getCountNew() > 0));
							} else {
								values.put(key, Boolean.FALSE);
							}
						} else if (Integer.valueOf(Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT).equals(product.getMaxCount())) {
							values.put(key, new ProductCountOrAll(product, contract.getCountExistingForProduct(product), contract.getCountNewForProduct(product)));
							form.addCustomComponent(new ProductCountOrAllEditorPanel(form, key));
						} else {
							List<OrderLine> orderLines = contract.getOrderLines(product);
							if (orderLines.size() > 0) {
								values.put(key, orderLines.get(0).getCountNew());
							} else {
								values.put(key, Integer.valueOf(0));
							}
							form.addNumberTextField(key).setMinimum(0);
						}
					}
				}
			}
			
			if (supportProductNames.size() > 0) {
				String key = "support";
				labelMap.put(key, "Support aftale");
				values.put(key, currentSupport);
				form.addDropDownChoice(key, supportProductNames);
			}
		}

		labelMap.put("action.prev", "Tilbage");
		labelMap.put("action.next", "Videre");
		
		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.prev(getWebPage()), target);
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.next(getWebPage()), target);
			}
		});
	}
	
	private void saveAndNavigate(final OfficeAdditionalProducts values, Class<? extends WebPage> page, AjaxRequestTarget target) {
		// Transfer values to contract
		try {
			final MobileContract contract = (MobileContract) CoreSession.get().getContract();

			ProductGroup productGroup = null;
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			
			for (String key : values.keySet()) {
				Object value = values.get(key);
				if (value instanceof ProductCountOrAll) {
					ProductCountOrAll productRow = (ProductCountOrAll) value;
					CountAndInstallation countAndInstallation = new CountAndInstallation();
					countAndInstallation.setCountNew(productRow.getCountNew());
					countAndInstallation.setCountExisting(productRow.getCountExisting());
					List<CountAndInstallation> countAndInstallations = new ArrayList<>();
					countAndInstallations.add(countAndInstallation);
					productToCountMap.put(productRow.getProduct(), countAndInstallations);
					productGroup = productRow.getProduct().getProductGroup();  // All products are from the same group!
					
					int countForLicenses = 0;
					for (Subscription subscription : contract.getSubscriptions()) {
						for (MobileProduct productInSubscription : subscription.getProducts()) {
							if (productInSubscription.equals(productRow.getProduct())) {
								countForLicenses++;
							}
						}
					}
					if (!productRow.isAll() && (countForLicenses > productRow.getCountNew())) {
						form.error("Der er konfigureret flere " + productRow.getProduct().getPublicName()
								+ " end du prøver at vælge. Fjern først afkrydsning på Konfiguration.");
						target.add(notificationPanel);
						return;
					}
				} else if (value instanceof Integer) {
					MobileProduct product = (MobileProduct) productDao.findByField("productId", key).get(0);
					CountAndInstallation countAndInstallation = new CountAndInstallation();
					countAndInstallation.setCountNew((Integer) value);
					countAndInstallation.setCountExisting(0);
					List<CountAndInstallation> countAndInstallations = new ArrayList<>();
					countAndInstallations.add(countAndInstallation);
					productToCountMap.put(product, countAndInstallations);
					productGroup = product.getProductGroup();  // All products are from the same group!
				} else if (value instanceof String) {
					String productName = (String) value;
					if (!productName.equals("Ingen")) {
						CountAndInstallation countAndInstallation = new CountAndInstallation();
						countAndInstallation.setCountNew(1);
						countAndInstallation.setCountExisting(0);
						List<CountAndInstallation> countAndInstallations = new ArrayList<>();
						countAndInstallations.add(countAndInstallation);
						for (Product product : supportProducts) {
							if (productName.equals(product.getPublicName())) {
								productToCountMap.put(product, countAndInstallations);
								productGroup = product.getProductGroup();  // All products are from the same group!
							}
						}
					}
				} else if (value instanceof Boolean) {
					MobileProduct product = (MobileProduct) productDao.findByField("productId", key).get(0);
					CountAndInstallation countAndInstallation = new CountAndInstallation();
					
					if (product.hasFlag("all_subscribers")) {  // Mail migrering
						countAndInstallation.setCountNew(((Boolean) value) ? Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT : 0);
					} else {
						countAndInstallation.setCountNew(((Boolean) value) ? 1 : 0);
					}
					countAndInstallation.setCountExisting(0);
					List<CountAndInstallation> countAndInstallations = new ArrayList<>();
					countAndInstallations.add(countAndInstallation);
					productToCountMap.put(product, countAndInstallations);
					productGroup = product.getProductGroup();  // All products are from the same group!
				}
			}
			((MobileContract) CoreSession.get().getContract()).adjustOrderLinesForProducts(productGroup, productToCountMap, null);
			
			contractSaver.save(contract);
			setResponsePage(page);
		} catch (Exception e) {
			System.out.println("Shit happens");
		}
	}
}
