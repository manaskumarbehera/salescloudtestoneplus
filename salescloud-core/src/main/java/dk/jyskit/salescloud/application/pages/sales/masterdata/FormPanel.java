package dk.jyskit.salescloud.application.pages.sales.masterdata;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.MediumSpanType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.services.cvr.CVRService;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormRow;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.iframe.IFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormPanel extends Panel {
	@Inject
	private PageNavigator navigator;
	@Inject
	private ContractDao contractDao;
	@Inject
	private ContractSaver contractSaver;
	
	public FormPanel(String id) {
		super(id);
		final Contract contract = CoreSession.get().getContract();
		
		WebMarkupContainer mapContainer = new WebMarkupContainer("mapContainer");
		add(mapContainer);
		if (contract.getBusinessArea().hasFeature(FeatureType.NETWORK_COVERAGE_MAP)) {
			final IFrame mapIframe = new IFrame("map", new AbstractReadOnlyModel<String>() {
				@Override
				public String getObject() {
					if (StringUtils.isEmpty(contract.getCustomer().getZipCode()) || StringUtils.isEmpty(contract.getCustomer().getAddress())) {
						return "http://daekning.tdc.dk/tdcnetmap_ext/Map.aspx?Site=TDCMOBIL&Page=Public_E&westpanel=collapsed";
					} else {
						String address = contract.getCustomer().getAddress();
						int pNumber = StringUtils.indexOfAny(address, "0123456789");
						if (pNumber != -1) {
							String rest = address.substring(pNumber);
							address = address.substring(0, pNumber).trim();
							int pSpace = StringUtils.indexOfAny(rest, " -");
							if (pSpace == -1) {
								address += rest;
							} else {
								address += rest.substring(0, pSpace);
							}
						}
						return "http://daekning.tdc.dk/tdcnetmap_ext/Map.aspx?Site=TDCMOBIL&Page=Public_E&westpanel=collapsed&SearchName=Adresse&"
								+ "SearchStep0=" + contract.getCustomer().getZipCode()
								+ "&SearchStep1=" + StringEscapeUtils.escapeHtml4(address).replace(" ", "%20");
					}
				}
			}); 
			
			// http://daekning.tdc.dk/tdcnetmap_ext/Map.aspx?Site=TDCMOBIL&Page=Public_E&westpanel=collapsed&SearchName=Adresse&SearchStep0=4450&SearchStep1=Drivs&aring;tvej%204%20B
			// http://daekning.tdc.dk/tdcnetmap_ext/Map.aspx?Site=TDCMOBIL&Page=Public_E&westpanel=collapsed&SearchName=Adresse&SearchStep0=2450&SearchStep1=Teglholmsgade%201-3
			mapIframe.setOutputMarkupId(true);
			mapContainer.add(mapIframe);
		} else {
			mapContainer.setVisible(false);
		}
		
		final WebMarkupContainer formContainer = new WebMarkupContainer("formContainer");
		formContainer.setOutputMarkupId(true);
		add(formContainer);
		
		Jsr303Form<Contract> form = new Jsr303Form<>("form", contract, true);
		formContainer.add(form);
		
		form.setLabelStrategy(new EntityLabelStrategy("Contract"));
		
		// Place the legend on the form row
		FormRow<Contract> formRow = form.createRow();

		FormGroup<Contract> sellerGroup = formRow.createGroup("seller", MediumSpanType.SPAN6);
		sellerGroup.addTextField("salesperson.user.fullName");
		sellerGroup.addTextField("seller.companyId");
		sellerGroup.addTextField("seller.companyName");
		sellerGroup.addTextField("seller.address");
		sellerGroup.addTextField("seller.zipCode");
		sellerGroup.addTextField("seller.city");
		sellerGroup.addTextField("salesperson.user.smsPhone");
		sellerGroup.addTextField("seller.email");
		
		FormGroup<Contract> customerGroup = formRow.createGroup("customer", MediumSpanType.SPAN6);
		final TextField customerCompanyIdField 		= customerGroup.addTextField("customer.companyId");
		final TextField customerCompanyNameField 	= customerGroup.addTextField("customer.companyName");
		final TextField customerCompanyAddressField = customerGroup.addTextField("customer.address");
		final TextField customerCompanyZipCodeField	= customerGroup.addTextField("customer.zipCode");
		final TextField customerCompanyCityField 	= customerGroup.addTextField("customer.city");
		
//		FormGroup<Contract> contactGroup = formRow.createGroup("contact", MediumSpanType.SPAN6);
		final TextField customerContactNameField 	= customerGroup.addTextField("customer.name");
		final TextField customerCompanyPhoneField 	= customerGroup.addTextField("customer.phone");
		final TextField customerCompanyEmailField 	= customerGroup.addTextField("customer.email");
		
//		FormGroup<Contract> commentsGroup = formRow.createGroup("comments", MediumSpanType.SPAN12);
		customerGroup.addTextArea("customer.comment");
		
//		FormGroup<Contract> customerGroup2 = formRow.createNoLegendGroup(MediumSpanType.SPAN12);
//		customerGroup2.setLabelSpans(MediumSpanType.SPAN3);
//		customerGroup2.setEditorSpans(MediumSpanType.SPAN9);
//		customerGroup2.addTextArea("customer.comment");
		
		OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            	if (!StringUtils.isEmpty(contract.getCustomer().getCompanyId())) {
	            	if (contract.getCustomer().getCompanyId().length() == 8) {
	            		CVRService cvrService = new CVRService();
	            		try {
		            		CVRService.Response response = cvrService.fetchDetails(Integer.valueOf(contract.getCustomer().getCompanyId()));
		            		if (!StringUtils.isEmpty(response.navn)) {
			            		contract.getCustomer().setCompanyName(response.navn == null ? "" : response.navn);
			            		contract.getCustomer().setCity(response.by == null ? "" : response.by);
			            		contract.getCustomer().setZipCode(response.postnr == null ? "" : response.postnr);
			            		contract.getCustomer().setAddress(response.adresse == null ? "" : response.adresse);
			            		contract.getCustomer().setEmail(response.email == null ? "" : response.email);
			            		contract.getCustomer().setPhone(response.telefon == null ? "" : response.telefon);

			            		try {
				            		// contractDao.save(contract);
				            		contractSaver.save(contract);
								} catch (Exception e) {
									log.warn("Strange exception: " + ExceptionUtils.getStackTrace(e));
								}
			            		
//			            		target.add(mapIframe);
			            		setResponsePage(MasterDataPage.class);
		            		}
						} catch (Exception e) {
		            		contract.getCustomer().setCompanyId("Ukendt CVR nr: " + contract.getCustomer().getCompanyId());
							log.warn("Failed to get CVR data: " + contract.getCustomer().getCompanyId(), e);
		            		try {
//			            		contractDao.save(contract);
			            		contractSaver.save(contract);
							} catch (Exception e1) {
								log.warn("Strange exception: " + ExceptionUtils.getStackTrace(e));
							}
		            		setResponsePage(MasterDataPage.class);
						}
	            	}
            	}
            }
        };
		customerCompanyIdField.add(onChangeAjaxBehavior);

		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
//				contractDao.save(contract);
				contractSaver.save(contract);
				setResponsePage(navigator.prev(getWebPage()));
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
//				contractDao.save(contract);
				contractSaver.save(contract);
				setResponsePage(navigator.next(getWebPage()));
			}
		});
		
	}
}
