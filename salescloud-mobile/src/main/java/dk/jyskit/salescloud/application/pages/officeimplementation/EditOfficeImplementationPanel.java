package dk.jyskit.salescloud.application.pages.officeimplementation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;

public class EditOfficeImplementationPanel extends Panel {
	private static final String KEY_PRIMARY_DOMAIN 		= "primaryDomain";
	private static final String KEY_TECHNICAL_CONTACT_EMAIL	= "technicalContactEmail";
	private static final String KEY_TECHNICAL_CONTACT_PHONE	= "technicalContactPhone";
	private static final String KEY_TECHNICAL_CONTACT_NAME	= "technicalContactName";
	private static final String KEY_E_FAKTURA_EMAIL			= "eFakturaEmail";
	
	@Inject
	private PageNavigator navigator;
	@Inject
	private ContractSaver contractSaver;
	@Inject
	private ProductDao productDao;
	private FormComponent fakturaEmail;
	private FormComponent technicalContactEmail;
	
	public EditOfficeImplementationPanel(String id) {
		super(id);
		
		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		final OfficeImplementation values = new OfficeImplementation();

		Jsr303Form<OfficeImplementation> form = new Jsr303Form<>("form", values);
		add(form);
		
		Map<String, String> labelMap = new HashMap<>();
		MapLabelStrategy labelStrategy = new MapLabelStrategy(labelMap, new DefaultLabelStrategy(form.getNameSpace()));
		form.setLabelStrategy(labelStrategy);		
		
//		{
//			FormGroup<OfficeImplementation> group = form.createGroup(Model.of("Produkter"));
//			
//			String productIds[] = new String[] {
//					"365_02_001", "365_02_002", "365_02_003", "365_02_004", "365_02_005", "365_02_006"
//			};
//			
//			for (String productId : productIds) {
//				MobileProduct product = (MobileProduct) productDao.findByProductGroupAndProductId(MobileSession.get().getBusinessAreaEntityId(),
//						MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey(), productId);   
//				if (product == null) {
//					System.out.println("Not found: " + productId);
//				}
//				if (!product.isExcludeFromConfigurator()) {
//					String key = productId;
//					values.put(key, new ProductRow(product, contract.getCountExistingForProduct(product), contract.getCountNewForProduct(product)));
//					labelMap.put(key, product.getPublicName());
//					if (Integer.valueOf(1).equals(product.getMaxCount())) {
//						form.addCheckBox(key);
//					} else {
//						form.addCustomComponent(new ProductRowPanel(form, key));
//					}
//				}
//			}
//		}
		
		{
			FormGroup<OfficeImplementation> group = form.createGroup(Model.of("Oplysninger"));
			
			{
				String key = KEY_TECHNICAL_CONTACT_NAME;
				labelMap.put(key, "Primær Teknisk Kontakt - Navn");
				values.put(key, contract.getTechnicalContactName());
				form.addTextField(key).setRequired(true);
			}
			{
				String key = KEY_TECHNICAL_CONTACT_EMAIL;
				labelMap.put(key, "Primær Teknisk Kontakt - Email");
				values.put(key, contract.getTechnicalContactEmail());
				technicalContactEmail = form.addTextField(key).setRequired(true);
			}
			{
				String key = KEY_TECHNICAL_CONTACT_PHONE;
				labelMap.put(key, "Primær Teknisk Kontakt - Telefon nr.");
				values.put(key, contract.getTechnicalContactPhone());
				form.addTextField(key).setRequired(true);
			}
			{
				String key = KEY_E_FAKTURA_EMAIL;
				labelMap.put(key, "eFaktura email");
				values.put(key, contract.getEFakturaEmail());
				fakturaEmail = form.addTextField(key).setRequired(true);
			}
		}

		labelMap.put("action.prev", "Tilbage");
		labelMap.put("action.next", "Videre");
		
		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.prev(getWebPage()));
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(values, navigator.next(getWebPage()));
			}
		});
	}
	
	private void saveAndNavigate(final OfficeImplementation values, Class<? extends WebPage> page) {
		// Transfer values to contract
		try {
			final MobileContract contract = (MobileContract) CoreSession.get().getContract();
			
			boolean errorFound = false;
			if (!EmailValidator.getInstance().isValid(values.getString(KEY_TECHNICAL_CONTACT_EMAIL))) {
				errorFound = true;
				technicalContactEmail.error("Ikke korrekt email adresse");
			}
			if (!EmailValidator.getInstance().isValid(values.getString(KEY_E_FAKTURA_EMAIL))) {
				errorFound = true;
				fakturaEmail.error("Ikke korrekt email adresse");
			}
				
			if (!errorFound) {
				contract.setTechnicalContactName(values.getString(KEY_TECHNICAL_CONTACT_NAME));
				contract.setTechnicalContactEmail(values.getString(KEY_TECHNICAL_CONTACT_EMAIL));
				contract.setTechnicalContactPhone(values.getString(KEY_TECHNICAL_CONTACT_PHONE));
				contract.setEFakturaEmail(values.getString(KEY_E_FAKTURA_EMAIL));
				
				contractSaver.save(contract);
				setResponsePage(page);
			}
		} catch (Exception e) {
			System.out.println("Shit happens");
		}
	}
}
