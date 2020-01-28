package dk.jyskit.salescloud.application.pages.contractsummary;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.links.reports.ReportLink;
import dk.jyskit.salescloud.application.model.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class AbstractOutputButtonsPanel extends Panel {
	private WebMarkupContainer offerLinkContainer;
	private MobileContract contract;
	protected Form<Contract> form;

	public AbstractOutputButtonsPanel(String id) {
		super(id);

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		form = new Form<>("form");

		add(form);

//		WebMarkupContainer partnerLinks = new WebMarkupContainer("partnerLinks");
//		form.add(partnerLinks);

		contract = MobileSession.get().getContract();

/*
		SalespersonRole salesperson =  (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
			ReportLink partnerSupportOgRateLink = new ReportLink("partnerSupportOgRateLink", "support-og-rate-aftale.pdf", new PartnerSupportOgRateAftaleReport());
			partnerLinks.add(partnerSupportOgRateLink);
			partnerSupportOgRateLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_PARTNER_SUPPORT));

			ReportLink partnerSupportLink = new ReportLink("partnerSupportLink", "support-aftale.pdf", new PartnerSupportAftaleReport());
			partnerLinks.add(partnerSupportLink);
			partnerSupportLink.setVisible(MobileSession.get().getBusinessArea().hasFeature(FeatureType.OUTPUT_PARTNER_SUPPORT));

			ReportLink reportLink = new ReportLink("partnerGodtIgangLink", "godt-i-gang-aftale.pdf", new PartnerGodtIgangReport());
			partnerLinks.add(reportLink);
		} else {
			partnerLinks.setVisible(false);
		}
*/
	}

//	@Override
//	protected void onConfigure() {
//		super.onConfigure();
//
//		MobileContract contract = MobileSession.get().getContract();
//
//		if (contract.getCampaigns().get(0).isGksValidation()) {
//			ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo();
//			long gskDiscountLimit = (contract.getGksSumPrYear() * contract.getAdjustedContractLength() * 5) / 100;
//			if (!CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//				info("DEBUG: GSK råderum: " + gskDiscountLimit/100 + ", Campaign discount (installation+engangs): " + contractFinansialInfo.getCampaignDiscounts().getNonRecurringFees()/100);
//			}
//			if (gskDiscountLimit < contractFinansialInfo.getCampaignDiscounts().getNonRecurringFees()) {
//				offerLinkContainer.setVisible(false);
//				offerAlertContainer.setVisible(true);
//			} else {
//				offerLinkContainer.setVisible(true);
//				offerAlertContainer.setVisible(false);
//			}
//		}
//
//	}
}
