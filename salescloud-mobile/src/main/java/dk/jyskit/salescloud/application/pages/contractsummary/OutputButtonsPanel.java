package dk.jyskit.salescloud.application.pages.contractsummary;

import java.util.List;

import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.links.file.AnyFileLink;
import dk.jyskit.salescloud.application.links.pdf.PdfWithDateStampLink;
import dk.jyskit.salescloud.application.links.reports.ReportLink;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.services.sendmail.Mail;
import dk.jyskit.waf.application.services.sendmail.MailResult;
import dk.jyskit.waf.application.services.sendmail.NameAndEmail;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class OutputButtonsPanel extends Panel {
	private WebMarkupContainer offerLinkContainer;
	private WebMarkupContainer offerAlertContainer;

	public OutputButtonsPanel(String id) {
		super(id);
		
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		
		final boolean isOffice 	= (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE);
		final boolean isFiber 	= ((MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER) ||
									(MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV));
		final boolean isOnePlus = MobileSession.get().isBusinessAreaOnePlus();

//		LoadableDetachableModel<Contract> model = new LoadableDetachableModel<Contract>() {
//			@Override
//			protected Contract load() {
//				return MobileSession.get().getContract();
//			}
//		};
		
		Form<Contract> form = new Form<>("form");
		add(form);
		
		WebMarkupContainer partnerLinks = new WebMarkupContainer("partnerLinks");
		form.add(partnerLinks);
		
		WebMarkupContainer officeSection = new WebMarkupContainer("office");
		form.add(officeSection);
		officeSection.setVisible(isOffice);
		
		WebMarkupContainer fiberSection = new WebMarkupContainer("fiber");
		form.add(fiberSection);
		fiberSection.setVisible(isFiber);

		WebMarkupContainer onePlusSection = new WebMarkupContainer("onePlus");
		form.add(onePlusSection);
		onePlusSection.setVisible(isOnePlus);

		WebMarkupContainer otherSection = new WebMarkupContainer("other");
		form.add(otherSection);
		otherSection.setVisible(!isOffice && !isFiber && !isOnePlus);
		
		final MobileContract contract = MobileSession.get().getContract();
		
		SalespersonRole salesperson =  (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
			ReportLink partnerSupportOgRateLink = new ReportLink("partnerSupportOgRateLink", "support-og-rate-aftale.pdf",
					new PartnerSupportOgRateAftaleReport("TDC Erhvervscenter", true, true));
			partnerLinks.add(partnerSupportOgRateLink);
			partnerSupportOgRateLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_PARTNER_SUPPORT));
			
			ReportLink partnerSupportLink = new ReportLink("partnerSupportLink", "support-aftale.pdf", new PartnerSupportAftaleReport("TDC Erhvervscenter"));
			partnerLinks.add(partnerSupportLink);
			partnerSupportLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_PARTNER_SUPPORT));
			
//			partnerLinks.add(new ReportLink("partnerHardwareLink", "hardware-aftale.pdf", new PartnerHardwareReport()));
			ReportLink reportLink = new ReportLink("partnerGodtIgangLink", "godt-i-gang-aftale.pdf", new PartnerGodtIgangReport("TDC Erhvervscenter"));
			reportLink.setVisible(BusinessAreas.WIFI != contract.getBusinessArea().getBusinessAreaId() && !isOffice && !isFiber);
			partnerLinks.add(reportLink);
		} else {
			partnerLinks.setVisible(false);
		}

		ReportLink preliminaryOfferLink = new ReportLink("preliminaryOfferLink", "prisoverslag.pdf", new TilbudOgKontraktReport(false));
		if (isOffice) {
//			officeSection.add(preliminaryOfferLink);
		} else if (isFiber) {
			fiberSection.add(preliminaryOfferLink);
		} else if (isOnePlus) {
			onePlusSection.add(preliminaryOfferLink);
		} else {
			otherSection.add(preliminaryOfferLink);
		}
		
		offerAlertContainer = new WebMarkupContainer("offerAlert");
		offerAlertContainer.setOutputMarkupId(true);
		offerAlertContainer.setOutputMarkupPlaceholderTag(true);
		offerAlertContainer.setVisible(false);
		form.add(offerAlertContainer);
		
		offerLinkContainer = new WebMarkupContainer("offerLinkContainer");
		offerLinkContainer.setOutputMarkupId(true);
		offerLinkContainer.setOutputMarkupPlaceholderTag(true);
		otherSection.add(offerLinkContainer);
		ReportLink offerLink = new ReportLink("offerLink", "tilbud.pdf", new TilbudOgKontraktReport(true));
		PdfWithDateStampLink ecProcessLink = null;
		if (isOffice) {
			officeSection.add(offerLink);
			ecProcessLink = new PdfWithDateStampLink("ecProcessLink", "documents/office_in_the_cloud_ec_proces.pdf", "EC proces for Office in the Cloud.pdf");
			officeSection.add(ecProcessLink);
		} else if (isFiber) {
			fiberSection.add(offerLink);
		} else if (isOnePlus) {
			onePlusSection.add(offerLink);
		} else {
			offerLinkContainer.add(offerLink);
		}
		offerLinkContainer.setVisible(BusinessAreas.WIFI != contract.getBusinessArea().getBusinessAreaId());
		
		ReportLink rammeaftaleLink = new ReportLink("rammeaftaleLink", "rammeaftale_og_bilag.pdf", new ContractAcceptReport(false, true, true, true));
		if (isOnePlus) {
			onePlusSection.add(rammeaftaleLink);
		} else {
			otherSection.add(rammeaftaleLink);
		}
		rammeaftaleLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.CONTRACT_ACCEPT_REPORT));
		
		ReportLink productSpecificLink = new ReportLink("productSpecificLink", "rammeaftale_og_bilag.pdf", new ContractAcceptReport(false, false, true, true));
		if (isOnePlus) {
			onePlusSection.add(productSpecificLink);
		} else {
			otherSection.add(productSpecificLink);
		}
		productSpecificLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.CONTRACT_ACCEPT_REPORT));
		
		ReportLink tilbudRammeaftalePBLink = new ReportLink("tilbudRammeaftalePBLink", "tilbud_rammeaftale_og_bilag.pdf", new TilbudRammeaftaleOgPBReport(true, true, true, true));
		if (isOnePlus) {
			onePlusSection.add(tilbudRammeaftalePBLink);
		} else {
			otherSection.add(tilbudRammeaftalePBLink);
		}
		tilbudRammeaftalePBLink.setVisible(BusinessAreas.match(BusinessAreas.TDC_WORKS, MobileSession.get().getBusinessArea()));
		
		ReportLink cdmOutputLink = new ReportLink("cdmOutputLink", "cdm.pdf", new CdmOutputReport(false));
		otherSection.add(cdmOutputLink);
		
		ReportLink cdmOutputWifiLink = new ReportLink("cdmOutputWifiLink", "cdm.pdf", new CdmOutputReport(false));
		otherSection.add(cdmOutputWifiLink);
		
		WebMarkupContainer cdmOutputWifiLinkDisabled = new WebMarkupContainer("cdmOutputWifiLinkDisabled");
		form.add(cdmOutputWifiLinkDisabled);
		
		if (BusinessAreas.WIFI == contract.getBusinessArea().getBusinessAreaId()) {
			cdmOutputLink.setVisible(false);
			
			cdmOutputWifiLinkDisabled.setVisible(false);
			cdmOutputWifiLink.setVisible(true);
			if (contract.getTechnicalContactName() == null) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (contract.getTechnicalContactEmail() == null) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (contract.getTechnicalContactPhone() == null) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (contract.getTechnicalSolution() == null) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (contract.getTechnicalSolution().length() < 20) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (contract.isNewAccount() && contract.getInvoicingType() == null) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (!contract.isNewAccount() && StringUtils.isEmpty(contract.getAccountNo())) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else if (StringUtils.isEmpty(contract.getOrderConfirmationEmailAdresses())) {
				cdmOutputWifiLink.setVisible(false);
				cdmOutputWifiLinkDisabled.setVisible(true);
			} else {
				for (WiFiBundleIds location : contract.getWiFiBundles()) {
					if (StringUtils.isEmpty(location.getContactName())) {
						cdmOutputWifiLink.setVisible(false);
						cdmOutputWifiLinkDisabled.setVisible(true);
					} else if (StringUtils.isEmpty(location.getContactPhone())) {
						cdmOutputWifiLink.setVisible(false);
						cdmOutputWifiLinkDisabled.setVisible(true);
					} else if (StringUtils.isEmpty(location.getLidId())) {
						cdmOutputWifiLink.setVisible(false);
						cdmOutputWifiLinkDisabled.setVisible(true);
					}
				}
			}
		} else {
			cdmOutputWifiLink.setVisible(false);
			cdmOutputWifiLinkDisabled.setVisible(false);
			cdmOutputLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_CDM));
		}
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.SWITCHBOARD) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_omstilling.pdf", "Tillægsvilkår for TDC Omstilling.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_works.pdf", "Tillægsvilkår for TDC Erhverv Works.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.ONE_PLUS) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_one_plus.pdf", "Tillægsvilkår for TDC Erhverv One+.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_fiber.pdf", "Vilkår Erhverv netværk.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_fiber.pdf", "Vilkår Erhverv netværk.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.WIFI) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_wifi.pdf", "Tillægsvilkår for TDC Wi-Fi Intelligence.pdf"));
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.MOBILE_VOICE) {
			otherSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår.pdf", "Tillægsvilkår for TDC Erhverv Mobilmix og Mobilpakker.pdf"));
		}
		
		PdfWithDateStampLink fuldmagtMobilLink = new PdfWithDateStampLink("fuldmagtMobilLink", "documents/multifuldmagt_mobil.pdf", "Fuldmagt - overførsel af mobilnumre til TDC.pdf");
		otherSection.add(fuldmagtMobilLink);
		fuldmagtMobilLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_AUTHORITY));
		
		PdfWithDateStampLink fuldmagtFastnetLink = new PdfWithDateStampLink("fuldmagtFastnetLink", "documents/fuldmagt_fastnet.pdf", "Fuldmagt - overførsel af fastnetnumre til TDC.pdf");
		otherSection.add(fuldmagtFastnetLink);
		fuldmagtFastnetLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_AUTHORITY));
		
		AnyFileLink brugerListeLink = new AnyFileLink("brugerListeLink", "documents/brugerliste_omstilling.xlsm", "Brugerliste Omstilling.xlsm", "application/xlsm");
		otherSection.add(brugerListeLink);
		brugerListeLink.setVisible(BusinessAreas.match(BusinessAreas.TDC_WORKS, MobileSession.get().getBusinessArea()));

		AnyFileLink aftalePapirLink = new AnyFileLink("aftalePapirLink", "documents/aftalepapir_works.docm", "Aftalepapir.docm", "application/docm");
		otherSection.add(aftalePapirLink);
		aftalePapirLink.setVisible(MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS);
		
		PdfWithDateStampLink firePOnboardingLink = new PdfWithDateStampLink("firePOnboardingLink", "documents/4P_Onboarding.pdf", "4 P-er onboarding.pdf");
		otherSection.add(firePOnboardingLink);
		if (!MobileSession.get().getBusinessArea().hasFeature(FeatureType.TDC_OFFICE)) {
			firePOnboardingLink.setVisible(false);
		}
		
		PdfWithDateStampLink firePOmstillingLink = new PdfWithDateStampLink("firePOmstillingLink", "documents/4P_Omstillingspakker.pdf", "4 P-er omstilling.pdf");
		otherSection.add(firePOmstillingLink);
		PdfWithDateStampLink firePOmstillingBredbaandLink = new PdfWithDateStampLink("firePOmstillingBredbaandLink", "documents/4P_Omstillingspakker_bredbånd.pdf", "4 P-er omstilling bredbånd.pdf");
		otherSection.add(firePOmstillingBredbaandLink);
		PdfWithDateStampLink firePMobilAbonnementLink = new PdfWithDateStampLink("firePMobilAbonnementLink", "documents/proces_mobilnummer.pdf", "4 P-er mobilabonnement.pdf");
		otherSection.add(firePMobilAbonnementLink);
		PdfWithDateStampLink process3Link = new PdfWithDateStampLink("process3Link", "documents/Brudt_fakturaforloeb.pdf", "Brudt fakturaforløb.pdf");
		otherSection.add(process3Link);
		if (!MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_PROCESS)) {   // -> switchboard?
			firePOmstillingLink.setVisible(false);
			firePOmstillingBredbaandLink.setVisible(false);
			firePMobilAbonnementLink.setVisible(false);
//			process2Link.setVisible(false);
			process3Link.setVisible(false);
		}
		
		PdfWithDateStampLink wiFi4PLink = new PdfWithDateStampLink("wiFi4pLink", "documents/4P_Wi-Fi_Intelligence_partner.pdf", "Wi-Fi Intelligence - 4 P-er.pdf");
		otherSection.add(wiFi4PLink);
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() != BusinessAreas.WIFI) {
			wiFi4PLink.setVisible(false);
		}
		
		PdfWithDateStampLink officeCrashCourseLink = new PdfWithDateStampLink("officeCrashCourseLink", "documents/Lynkursus i O365.pdf", "Lynkursus i O365.pdf");
		officeSection.add(officeCrashCourseLink);
		if (!MobileSession.get().getBusinessArea().hasFeature(FeatureType.TDC_OFFICE)) {
			officeCrashCourseLink.setVisible(false);
		}
		
		AnyFileLink officePresentationLink = new AnyFileLink("officePresentationLink", "documents/tdc_office_praesentation_ec.pptx", "TDC Office in the cloud præsentation_EC.pptx", "application/pptx");
		officeSection.add(officePresentationLink);
		officePresentationLink.setVisible(MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE);
		
		ReportLink officeTastebilagLink = new ReportLink("officeTastebilagLink", "tastebilag.pdf", new CdmOutputReport(false));
		officeSection.add(officeTastebilagLink);
		officeSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_office.pdf", "Tillægsvilkår for TDC Office.pdf"));
		officeSection.add(new PdfWithDateStampLink("firePOfficeLink", "documents/4P_Office.pdf", "4 P-er.pdf"));

		ReportLink fiberTastebilagLink = null;
		PdfWithDateStampLink fiberFirePFiberLink = null;
		if (isOnePlus) {
			ReportLink onePlusTastebilagLink = new ReportLink("tastebilagLink", "tastebilag.pdf", new CdmOutputReport(false));
			onePlusSection.add(onePlusTastebilagLink);
			onePlusSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_fiber.pdf", "Tillægsvilkår for TDC Erhverv One+.pdf"));

			PdfWithDateStampLink firePFiberLink = new PdfWithDateStampLink("firePLink", "documents/4P_fiber.pdf", "4 P-er.pdf");
			onePlusSection.add(firePFiberLink);
		} else if (isFiber) {
			fiberTastebilagLink = new ReportLink("tastebilagLink", "tastebilag.pdf", new CdmOutputReport(false));
			fiberSection.add(fiberTastebilagLink);
			fiberSection.add(new PdfWithDateStampLink("termsLink", "documents/vilkår_fiber.pdf", "Tillægsvilkår for TDC Erhverv Fiber.pdf"));

			fiberFirePFiberLink = new PdfWithDateStampLink("firePLink", "documents/4P_fiber.pdf", "4 P-er.pdf");
			fiberSection.add(fiberFirePFiberLink);
		}


		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			fiberSection.add(new PdfWithDateStampLink("productSpecificationLink", "documents/faktaark_erhverv_fiber.pdf", "Faktaark TDC Erhverv Fiber.pdf"));
		} else {
			fiberSection.add(new PdfWithDateStampLink("productSpecificationLink", "documents/fiber_plus_produktspecifikation.pdf", "Produktspecifikation TDC Erhverv Fiber Plus.pdf"));
		}

		boolean fiberInfoOk = false;
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			List<FiberErhvervBundleData> fiberBundles = contract.getFiberErhvervBundles();
			fiberInfoOk = fiberBundles.size() > 0;
			for (int i = 0; i < fiberBundles.size(); i++) {
				FiberErhvervBundleData bundle = fiberBundles.get(i);
				if (StringUtils.isEmpty(bundle.getContactPhone30Minutes())) {
					fiberInfoOk = false;
					break;
				}
				if (bundle.isRedundancySelected()) {
					if (StringUtils.isEmpty(bundle.getSmsAlertNo())) {
						fiberInfoOk = false;
						break;
					}
				}

				if (bundle.isSupervisionSelected()) {
					if (StringUtils.isEmpty(bundle.getContactSupervision())) {
						fiberInfoOk = false;
						break;
					}
					if (StringUtils.isEmpty(bundle.getContactSupervisionEmail())) {
						fiberInfoOk = false;
						break;
					}
					if (StringUtils.isEmpty(bundle.getContactSupervisionPhone())) {
						fiberInfoOk = false;
						break;
					}
				}

				if (bundle.isInspectionSelected()) {
					if (StringUtils.isEmpty(bundle.getContactInspection())) {
						fiberInfoOk = false;
						break;
					}
					if (StringUtils.isEmpty(bundle.getContactInspectionPhone())) {
						fiberInfoOk = false;
						break;
					}
				}
			}
		} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER) {
			List<FiberErhvervPlusBundleData> fiberBundles = contract.getFiberErhvervPlusBundles();
			fiberInfoOk = fiberBundles.size() > 0;
		}

		WebMarkupContainer salesforceDocumentsDisabled = new WebMarkupContainer("salesforceDocumentsDisabled");
		fiberSection.add(salesforceDocumentsDisabled);
		
		if (fiberInfoOk) {
			salesforceDocumentsDisabled.setVisible(false);
		} else {
//			offerLink.setVisible(false);
			if (fiberTastebilagLink != null) {
				fiberTastebilagLink.setVisible(false);
			}
		}
		
//		SpreadsheetLink kundebrugerdataExcelLink = new SpreadsheetLink("onboardingExcel", "onboarding.xls", new OnboardingSpreadsheet(false)) {
//			@Override
//			protected void onConfigure() {
//				setVisible(isOffice && contract.getIncompleteSubscriptions() == 0 && (contract.getSubscriptions().size() > 0));
//			}
//		};
//		officeSection.add(kundebrugerdataExcelLink);
		
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			preliminaryOfferLink.setVisible(false);
			fiberFirePFiberLink.setVisible(false);
		}
		
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
			if (contract.getSubscriptions().size() == 0) {
				preliminaryOfferLink.setVisible(false);
				offerLink.setVisible(false);
				ecProcessLink.setVisible(false);
				officeTastebilagLink.setVisible(false);
				firePOnboardingLink.setVisible(false);
			}
			if (!contract.hasOfficeImplementationInfo()) {
//				offerLink.setVisible(false);
				officeTastebilagLink.setVisible(false);
				firePOnboardingLink.setVisible(false);
			}
//			if (!contract.isConfigurationAccepted()) {
			if (contract.getStatus().getId() < ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId()) {
				officeTastebilagLink.setVisible(false);
			}
		}
		
		AjaxButton subscriptionConfigurationMailButton = new AjaxButton("subscriptionConfigurationMail") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				MailResult result = new Mail()
					.withSubject("TDC Mobile Voice konfigurering")
					.withHtml("<p>Kære " + CoreSession.get().getContract().getCustomer().getName() + "</p>" +
							"<p>Tak for en behagelig samtale omkring jeres kommende mobil løsning ved TDC.</p>" + 
							"<p>Inden vi kan bestille din løsning, skal vi have overblik over hvordan dine abonnementer skal konfigureres med<br>"
							+ "mobilnumre, navne, simkort type, tilvalg mv. Du skal i denne forbindelse udfylde vores guide på nedenstående link:<br>" +
							"<a href=\"http://...\">Din konfigurationsguide</a></p>" +
							"<p>Du er velkommen til at besøge guiden flere gange. Guiden gemmer automatisk dine input løbende og når du er færdig<br> "
							+ "anvendes knappen \"Send konfiguration\". " + 
							"Når du har gjort dette vil jeg bestille din løsning og hvorefter du vil modtage en ordrebekræftelse.</p>" + 
							"<p>Hvis du har spørgsmål er du velkommen til at kontakte mig.</p><br>" + 
							"<p>Venlig hilsen</p>" + 
							"<p>" + CoreSession.get().getContract().getSalesperson().getUser().getFullName() + "</p>")
					.withSender(new NameAndEmail(CoreSession.get().getContract().getSalesperson().getUser().getFullName(), CoreSession.get().getContract().getSeller().getEmail()))
					.withRecipient(new NameAndEmail(CoreSession.get().getContract().getCustomer().getName(), CoreSession.get().getContract().getCustomer().getEmail())).send();
				
				for (NameAndEmail nameAndEmail : result.getGoodRecipients()) {
					log.info("Successfully sent to: " + nameAndEmail.getEmail());
				}
				for (NameAndEmail nameAndEmail : result.getBadRecipients()) {
					log.info("Failed to send to: " + nameAndEmail.getEmail());
				}
			}
			
			@Override
			protected void onConfigure() {
				if (StringUtils.isEmpty(CoreSession.get().getContract().getCustomer().getName()) || StringUtils.isEmpty(CoreSession.get().getContract().getCustomer().getEmail())) {
					setEnabled(false);
				}
			}
		};
		subscriptionConfigurationMailButton.setVisible(false);
		otherSection.add(subscriptionConfigurationMailButton);

	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		MobileContract contract = MobileSession.get().getContract();
		
		if (contract.getCampaigns().get(0).isGksValidation()) {
			ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(true, false, false);
			long gskDiscountLimit = (contract.getGksSumPrYear() * contract.getAdjustedContractLength() * 5) / 100;
//			if (!CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//				info("DEBUG: GSK råderum: " + gskDiscountLimit/100 + ", Campaign discount (installation+engangs): " + contractFinansialInfo.getCampaignDiscounts().getNonRecurringFees()/100);
//			}
			if (gskDiscountLimit < contractFinansialInfo.getCampaignDiscounts().getNonRecurringFees()) {
				offerLinkContainer.setVisible(false);
				offerAlertContainer.setVisible(true);
			} else {
				offerLinkContainer.setVisible(true);
				offerAlertContainer.setVisible(false);
			}
		}
		
	}
}
