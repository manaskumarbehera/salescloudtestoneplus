package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import java.util.ArrayList;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.links.spreadsheets.SpreadsheetLink;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.ContractStatusEnum;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.salescloud.application.pages.contractsummary.ContractSummaryPage;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.panels.confirmation.ConfirmDialog;
import dk.jyskit.waf.wicket.utils.IAjaxCall;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntroPanelWithDownloadLinks extends IntroPanel {
	@Inject
	private MobileContractDao contractDao;
	private EntityModel<MobileContract> contractModel;
	
	public IntroPanelWithDownloadLinks(String id, PageInfo pageInfo, final EntityModel<MobileContract> contractModel) {
		super(id, pageInfo);
		this.contractModel = contractModel;
		
		final boolean isOffice = MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE;

		// ---

		WebMarkupContainer enableLoginContainer = new WebMarkupContainer("enableLoginContainer");
		add(enableLoginContainer);
		AjaxLink<Void> enableLoginLink = new AjaxLink<Void>("enableLoginLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = contractDao.findById(contractModel.getObject().getId());
				contract.setStatus(ContractStatusEnum.AWAITING_DATA_FROM_CUSTOMER);
				contractDao.save(contract);
				if (MobileSession.get().isExternalAccessMode()) {
					setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), MobileSession.get().isImplementerLoggedIn()));
				} else {
					setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
				}
			}
		};
		enableLoginContainer.add(enableLoginLink);
		enableLoginContainer.setOutputMarkupId(true);
		enableLoginContainer.setOutputMarkupPlaceholderTag(true);
		enableLoginContainer.setVisible(!MobileSession.get().isExternalAccessMode() && contractModel.getObject().getStatus().equals(ContractStatusEnum.OPEN));
		
		WebMarkupContainer loginInfo = new WebMarkupContainer("loginInfo");
		add(loginInfo);
		loginInfo.add(new Label("url", contractModel.getObject().getConfigurationUrl()));
		loginInfo.add(new Label("username", contractModel.getObject().getConfigurationUsername()));
		loginInfo.add(new Label("password", contractModel.getObject().getConfigurationPassword()));
		
		loginInfo.setOutputMarkupId(true);
		loginInfo.setOutputMarkupPlaceholderTag(true);
		loginInfo.setVisible(!MobileSession.get().isExternalAccessMode()  && contractModel.getObject().getStatus().equals(ContractStatusEnum.AWAITING_DATA_FROM_CUSTOMER) && (contractModel.getObject().getSubscriptions().size() > 0));
		
		// ---
		
		WebMarkupContainer nonLoginInfo = new WebMarkupContainer("nonLoginInfo");
		add(nonLoginInfo);
		nonLoginInfo.setVisible(!ContractStatusEnum.IMPLEMENTED.equals(contractModel.getObject().getStatus()) && (!MobileSession.get().isExternalAccessMode() || MobileSession.get().isCustomerLoggedIn() || MobileSession.get().isImplementerLoggedIn()));
		
		SpreadsheetLink nabsLink = new SpreadsheetLink("nabs", "nabs.xls", new TastegrundlagSpreadsheet(true)) {
			@Override
			protected void onConfigure() {
				setVisible(!isOffice && contractModel.getObject().getIncompleteSubscriptions() == 0);
			}
		};
		nonLoginInfo.add(nabsLink);

		SpreadsheetLink kvikLink = new SpreadsheetLink("kvik", "kvik.xls", new TastegrundlagSpreadsheet(false)) {
			@Override
			protected void onConfigure() {
				setVisible(!isOffice && contractModel.getObject().getIncompleteSubscriptions() == 0);
			}
		};
		nonLoginInfo.add(kvikLink);
		
//		SpreadsheetLink userOnboardingExcelLink = new SpreadsheetLink("onboardingExcel", "onboarding.xls", new OnboardingSpreadsheet(false)) {
//			@Override
//			protected void onConfigure() {
//				setVisible(isOffice && !MobileSession.get().isCustomerMode() && contractModel.getObject().getIncompleteSubscriptions() == 0 && (contractModel.getObject().getSubscriptions().size() > 0));
//			}
//		};
//		nonLoginInfo.add(userOnboardingExcelLink);
		
		Label statusLabel = new Label("status", new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				String things = "abonnementer";
				if (isOffice) {
					things = "licenser";
				}
				
				int incompleteSubscriptions = contractModel.getObject().getIncompleteSubscriptions();
				if (incompleteSubscriptions == 0) {
					int unassignedProducts = contractModel.getObject().getUnassignedAddonProducts();
					if (unassignedProducts == 0) {
						int duplicates = contractModel.getObject().getDuplicateEmailsInSubscriptions();
						if (duplicates == 0) {
//							if (contractModel.getObject().isConfigurationAccepted()) {
							if (contractModel.getObject().getStatus().getId() >= ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId()) {
								return "Alle " + things + " er konfigureret og godkendt";
							} else {							
								return "Alle " + things + " er konfigureret";
							}
						} else {
							return "Der er " + duplicates + " emails som er brugt flere gange";
						}
					} else {
						return "Der er " + unassignedProducts + " produkter som ikke er fordelt på " + things;
					}
				} else {
					return "Der er " + incompleteSubscriptions + " " + things + " som ikke er 100% konfigurerede";
				}
			}
		}) {
			@Override
			protected void onConfigure() {
				if (contractModel.getObject().getIncompleteSubscriptions() == 0 && contractModel.getObject().getDuplicateEmailsInSubscriptions() == 0) {
					add(AttributeModifier.replace("class", "label label-success"));
				} else {
					add(AttributeModifier.replace("class", "label label-danger"));
				}
			}
		};
		statusLabel.setVisible(contractModel.getObject().getSubscriptions().size() > 0);
		nonLoginInfo.add(statusLabel);
		
//		Label installationDateLabel = new Label("installation", new AbstractReadOnlyModel<String>() {
//			@Override
//			public String getObject() {
//				if (MobileSession.get().getContract().getInstallationDate() == null) {
//					return "Ønsket oprettelsesdato er ikke valgt";
//				} else {
//					return "Ønsket oprettelsesdato er d. " + new SimpleDateFormat("dd/MM/yyyy").format(MobileSession.get().getContract().getInstallationDate());
//				}
//			}
//		});
//		add(installationDateLabel);
		
		WebMarkupContainer updateContainer = new WebMarkupContainer("updateContainer");
		nonLoginInfo.add(updateContainer);
		AjaxLink<Void> updateLink = new AjaxLink<Void>("updateLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				if (MobileSession.get().isExternalAccessMode()) {
					setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), MobileSession.get().isImplementerLoggedIn()));
				} else {
					setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
				}
			}
		};
		updateContainer.add(updateLink);
//		updateContainer.setVisible(!contractModel.getObject().isConfigurationAccepted());
		updateContainer.setVisible(contractModel.getObject().getStatus().getId() < ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId());
		
//		updateContainer.setVisible(!contractModel.getObject().isConfigurationAccepted() && (contractModel.getObject().getIncompleteSubscriptions() > 0));
		
		WebMarkupContainer contractStatusContainer = new WebMarkupContainer("contractStatusContainer") {
			@Override
			protected void onConfigure() {
				setVisible(MobileSession.get().isImplementerLoggedIn());
			}
		};
		nonLoginInfo.add(contractStatusContainer);
		BootstrapSelectSingle statusSelect = new BootstrapSelectSingle("contractStatus", new PropertyModel<>(contractModel.getObject(), "status"), ContractStatusEnum.valuesAsList(ContractStatusEnum.BUSINESSAREA_SPECIFIC));
		contractStatusContainer.add(statusSelect);
		statusSelect.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (ContractStatusEnum.IMPLEMENTED.equals(contractModel.getObject().getStatus())) {
					new ConfirmDialog(
							Model.of("Data fjernes"), 
							Model.of("Bekræft venligst at sagen er implementeret. Samtidig accepterer du at data vedr. licenser slettes.")).confirmer(onImplemented).show();
				} else {
					saveContractStatus(contractModel);
				}
			}

			private void saveContractStatus(final EntityModel<MobileContract> contractModel) {
				MobileContract contract = contractDao.findById(contractModel.getObject().getId());
				contract.setStatus(contractModel.getObject().getStatus());
				contractDao.save(contract);
				if (MobileSession.get().isExternalAccessMode()) {
					setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), MobileSession.get().isImplementerLoggedIn()));
				} else {
					setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
				}
			}
			
			IAjaxCall onImplemented = new IAjaxCall() {
				@Override
				public void invoke(AjaxRequestTarget target) {
					contractModel.getObject().setSubscriptions(new ArrayList<Subscription>());
					contractDao.save(contractModel.getObject());
					
					saveContractStatus(contractModel);
					if (MobileSession.get().isExternalAccessMode()) {
						setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), MobileSession.get().isImplementerLoggedIn()));
					} else {
						setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
					}
				}
			};
		});		
		
		WebMarkupContainer acceptContainer = new WebMarkupContainer("acceptContainer");
		nonLoginInfo.add(acceptContainer);
		AjaxLink<Void> acceptLink = new AjaxLink<Void>("acceptLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = contractDao.findById(contractModel.getObject().getId());
//				contract.setConfigurationAccepted(true);
				contract.setStatus(ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER);				
				contractDao.save(contract);
				if (MobileSession.get().isExternalAccessMode()) {
					setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), MobileSession.get().isImplementerLoggedIn()));
				} else {
					setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
				}
			}
		};
		acceptContainer.add(acceptLink);
//		acceptContainer.setVisible(!contractModel.getObject().isConfigurationAccepted() && (contractModel.getObject().getIncompleteSubscriptions() == 0) && (contractModel.getObject().getSubscriptions().size() > 0));
		acceptContainer.setVisible((contractModel.getObject().getStatus().getId() < ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId()) && (contractModel.getObject().getIncompleteSubscriptions() == 0) && (contractModel.getObject().getSubscriptions().size() > 0));

		SpreadsheetLink downloadLink = new SpreadsheetLink("downloadLink", "licenser.xls", new OnboardingSpreadsheet(false)) {
			@Override
			protected void onConfigure() {
				setVisible(MobileSession.get().isImplementerLoggedIn());
			}
		};
		nonLoginInfo.add(downloadLink);
		
		WebMarkupContainer informSeller = new WebMarkupContainer("informSeller");
		nonLoginInfo.add(informSeller);
//		informSeller.setVisible(MobileSession.get().isExternalAccessMode() && MobileSession.get().isImplementerLoggedIn() && contractModel.getObject().isConfigurationAccepted());
		informSeller.setVisible(MobileSession.get().isExternalAccessMode() && MobileSession.get().isImplementerLoggedIn() && 
				(contractModel.getObject().getStatus().getId() >= ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId()));
//		informSeller.add(new Label("name", contractModel.getObject().getSalesperson().getUser().getFullName()));
//		informSeller.add(new Label("phone", contractModel.getObject().getSalesperson().getUser().getSmsPhone()));
//		informSeller.add(new Label("email", contractModel.getObject().getSalesperson().getUser().getEmail()));
		
//		add(new Link<Void>("leaveContract") {
//			@Override
//			public void onClick() {
//				CoreSession.get().setContract(null);
//				setResponsePage(ExistingContractPage.class);
//			}
//		});
		
		AjaxLink<Void> logoutLink = new AjaxLink<Void>("logoutLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				boolean impl = MobileSession.get().isImplementerLoggedIn();
				MobileSession.get().setImplementerLoggedIn(false);
				MobileSession.get().setCustomerLoggedIn(false);
				if (MobileSession.get().isExternalAccessMode()) {
					setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), impl));
				} else {
					setResponsePage(new SubscriptionConfigurationPage(new PageParameters()));
				}
			}
		};
		add(logoutLink);
		logoutLink.setVisible(MobileSession.get().isCustomerLoggedIn() || MobileSession.get().isImplementerLoggedIn());

//		WebMarkupContainer backToSummaryContainer = new WebMarkupContainer("backToSummaryContainer");
//		add(backToSummaryContainer);
//		AjaxLink<Void> backToSummaryLink = new AjaxLink<Void>("backToSummaryLink") {
//			@Override
//			public void onClick(AjaxRequestTarget target) {
//				setResponsePage(ContractSummaryPage.class);
//			}
//		};
//		backToSummaryContainer.add(backToSummaryLink);
//		backToSummaryContainer.setVisible(MobileSession.get().isExternalAccessMode() && !MobileSession.get().isCustomerLoggedIn());
		
	}
}
