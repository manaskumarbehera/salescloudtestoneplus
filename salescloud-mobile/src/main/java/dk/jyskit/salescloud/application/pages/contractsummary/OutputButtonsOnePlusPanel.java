package dk.jyskit.salescloud.application.pages.contractsummary;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.links.file.AnyFileLink;
import dk.jyskit.salescloud.application.links.pdf.PdfWithDateStampLink;
import dk.jyskit.salescloud.application.links.reports.ReportLink;
import dk.jyskit.salescloud.application.model.LocationBundleData;
import dk.jyskit.salescloud.application.model.MobileContract;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class OutputButtonsOnePlusPanel extends AbstractOutputButtonsPanel {
	public OutputButtonsOnePlusPanel(String id) {
		super(id);

		List<ResourceLink> list = Lists.newArrayList();

		list.add(newPdfLink("Vilkår - One+", "One+ - Vilkår - One - {date}.pdf", "documents/one/terms_one.pdf"));
		list.add(newPdfLink("Vilkår - Netværk", "One+ - Vilkår - Netværk - {date}.pdf", "documents/one/terms_network.pdf"));
		list.add(newPdfLink("4P - Mobil", "One+ - 4P - Mobil - {date}.pdf", "documents/one/4p_mobil.pdf"));
		list.add(newPdfLink("4P - Bredbånd", "One+ - 4P - Bredbånd - {date}.pdf", "documents/one/4p_bredbaand.pdf"));
//		list.add(newPdfLink("4P - Fiber", "One+ - 4P - Fiber - {cvr}{date}.pdf", "documents/one/4p_fiber.pdf"));
		list.add(newPdfLink("Brudt fakturaforløb", "One+ - Brudt fakturaforløb - {date}.pdf", "documents/one/brudt_fakturaforloeb.pdf"));
		list.add(newPdfLink("One+ Prislaminat Individuel", "One+ - Prislaminat Individuel - {date}.pdf", "documents/one/prislaminat_individuel.pdf"));
		list.add(newPdfLink("One+ Prislaminat Pulje", "One+ - Prislaminat Pulje - {date}.pdf", "documents/one/prislaminat_pulje.pdf"));
		list.add(newPdfLink("Prisliste One+", "One+ - Prisliste - {date}.pdf", "documents/one/prisliste.pdf"));
//		list.add(newPdfLink("Produktark - Netværk", "One+ - Produktark - Netværk - {cvr}{date}.pdf", "documents/one/x.pdf"));

		form.add(new ListView<ResourceLink>("customerDocuments", list) {
			@Override
			protected void populateItem(ListItem<ResourceLink> item) {
				ResourceLink<Void> link = item.getModelObject();
				item.add(link);
			}
		});

		list = Lists.newArrayList();
//		list.add(newReportLink("Tilbud", "One+ - Tilbud - {cvr}{date}.pdf", new TilbudOgKontraktReport(true)));
//		list.add(newReportLink("Tilbud", "One+ - Tilbud - {cvr}{date}.pdf", new TilbudRammeaftaleOgPBReport(true, false, false, false)));
		list.add(newReportLink("Tilbud med rabataftale + Produktspecifikt Bilag",
				"One+ - Tilbud med rabataftale + Produktspecifikt Bilag - {cvr}{date}.pdf",
				new TilbudRammeaftaleOgPBReport(true, true, true, true)));

//		try {
//			if (new Date().after(new SimpleDateFormat("yyyyMMdd").parse("20200430"))) {
				list.add(newReportLink("Allonge","One+ - Allonge - {cvr}{date}.pdf", new AllongeReport()));
//			}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		ResourceLink tastebilag = newReportLink("Tastebilag", "One+ - Tastebilag - {cvr}{date}.pdf", new CdmOutputReport(false));
		list.add(tastebilag);
		List<LocationBundleData> locationBundles = MobileSession.get().getContract().getLocationBundles();
		String tastebilagsproblem = null;
		for (int i=0; i<locationBundles.size(); i++) {
			LocationBundleData locationBundle = locationBundles.get(i);
			if (!locationBundle.isCdmOk()) {
				tastebilag.setVisible(false);
				tastebilagsproblem = "Tastebilag kan ikke udskrives pga. manglende info vedr. " +
						"lokation: " + locationBundle.getAddress();
				break;
			}
		}
		if (tastebilagsproblem == null) {
			MobileContract contract = MobileSession.get().getContract();
			if (contract.getBusinessArea().isOnePlus() && (contract.getExistingFlexConnectSubscriptions() == null)) {
				tastebilag.setVisible(false);
				tastebilagsproblem = "Tastebilag kan ikke udskrives fordi \"Er der nuværende Mobil FlexConnect abonnementer der skal overføres til løsningen?\" ikke er udfyldt";
			}
		}
		if (tastebilagsproblem == null) {
			form.add(new Label("tastebilagsproblem", "").setVisible(false));
		} else {
			form.add(new Label("tastebilagsproblem", tastebilagsproblem));
		}

/*
Extension MIME Type
.doc      application/msword
.dot      application/msword

.docx     application/vnd.openxmlformats-officedocument.wordprocessingml.document
.dotx     application/vnd.openxmlformats-officedocument.wordprocessingml.template
.docm     application/vnd.ms-word.document.macroEnabled.12
.dotm     application/vnd.ms-word.template.macroEnabled.12

.xls      application/vnd.ms-excel
.xlt      application/vnd.ms-excel
.xla      application/vnd.ms-excel

.xlsx     application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
.xltx     application/vnd.openxmlformats-officedocument.spreadsheetml.template
.xlsm     application/vnd.ms-excel.sheet.macroEnabled.12
.xltm     application/vnd.ms-excel.template.macroEnabled.12
.xlam     application/vnd.ms-excel.addin.macroEnabled.12
.xlsb     application/vnd.ms-excel.sheet.binary.macroEnabled.12

.ppt      application/vnd.ms-powerpoint
.pot      application/vnd.ms-powerpoint
.pps      application/vnd.ms-powerpoint
.ppa      application/vnd.ms-powerpoint

.pptx     application/vnd.openxmlformats-officedocument.presentationml.presentation
.potx     application/vnd.openxmlformats-officedocument.presentationml.template
.ppsx     application/vnd.openxmlformats-officedocument.presentationml.slideshow
.ppam     application/vnd.ms-powerpoint.addin.macroEnabled.12
.pptm     application/vnd.ms-powerpoint.presentation.macroEnabled.12
.potm     application/vnd.ms-powerpoint.template.macroEnabled.12
.ppsm     application/vnd.ms-powerpoint.slideshow.macroEnabled.12

.mdb      application/vnd.ms-access
 */
		list.add(newPdfLink("Fuldmagt Mobil", "One+ - Fuldmagt mobil - {cvr}{date}.pdf", "documents/one/fuldmagt_mobil.pdf"));
		list.add(newPdfLink("Fuldmagt Fastnet", "One+ - Fuldmagt fastnet - {cvr}{date}.pdf", "documents/one/fuldmagt_fastnet.pdf"));
		list.add(newPdfLink("Ejerskifteskema", "One+ - Ejerskifteskema - {cvr}{date}.pdf", "documents/one/overtagelsesblanket.pdf"));
		if (!MobileSession.get().userIsPartnerEC()) {
			list.add(newAnyFileLink("Smulskema", "One+ - Smulskema - {cvr}{date}.xlsx", "documents/one/smulskema.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		}
		list.add(newAnyFileLink("Brugerliste", "One+ - Brugerliste - {cvr}{date}.xlsm", "documents/one/brugerliste.xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12"));
		list.add(newAnyFileLink("Aftalepapir", "One+ - Aftalepapir - {cvr}{date}.docx", "documents/one/aftalepapir.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
		list.add(newAnyFileLink("Nummertillæg til aftalepapir", "One+ - Nummertillæg til aftalepapir - {cvr}{date}.xlsx", "documents/one/nummertillaeg_til_aftalepapir.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//		list.add(newAnyFileLink("Aftalepapir", "One+ - Aftalepapir - {cvr}{date}.docm", "documents/one/aftalepapir.docx", "application/vnd.ms-word.document.macroEnabled.12"));
		if (MobileSession.get().userIsPartnerEC()) {
//		list.add(newPdfLink("Installation", "One+ - Installation - {cvr}{date}.pdf", "documents/one/x.pdf"));
			list.add(newReportLink("Installation", "One+ - Installation - {cvr}{date}.pdf", new PartnerInstallationReport("TDC Erhvervscenter installation One+")));

			list.add(newReportLink("Rate - løsning", "One+ - Rate - løsning - {cvr}{date}.pdf", new PartnerSupportOgRateAftaleReport("TDC Erhvervscenter One+ Support- og Rate aftale", true, true)));
			list.add(newReportLink("Rate - hardware", "One+ - Rate - hardware - {cvr}{date}.pdf", new PartnerSupportOgRateAftaleReport("TDC Erhvervscenter udstyrsaftale", false, true)));
			list.add(newReportLink("Support", "One+ - Support - {cvr}{date}.pdf", new PartnerSupportAftaleReport("TDC Erhvervscenter One+ Supportaftale")));
//		list.add(newPdfLink("Support", "One+ - Support - {cvr}{date}.pdf", "documents/one/x.pdf"));
			list.add(newReportLink("Tastebilag Rate", "One+ - Tastebilag Rate - {cvr}{date}.pdf", new CdmOutputReport(true)));
		}

		form.add(new ListView<ResourceLink>("salesforceDocuments", list) {
			@Override
			protected void populateItem(ListItem<ResourceLink> item) {
				ResourceLink<Void> link = item.getModelObject();
				item.add(link);
			}
		});

		list = Lists.newArrayList();
		list.add(newReportLink("Rabataftale", "One+ - Rabataftale.pdf", new TilbudRammeaftaleOgPBReport(false, true, false, false)));
		list.add(newReportLink("Rabataftale + Produktspecifikt Bilag", "One+ - Rabataftale + Produktspecifikt Bilag.pdf", new TilbudRammeaftaleOgPBReport(false, true, true, true)));
		list.add(newReportLink("Produktspecifikt Bilag Tale", "One+ - Produktspecifikt Bilag Tale.pdf", new TilbudRammeaftaleOgPBReport(false, false, true, false)));
		list.add(newReportLink("Produktspecifikt Bilag Netværk", "One+ - Produktspecifikt Bilag Netværk.pdf", new TilbudRammeaftaleOgPBReport(false, false, false, true)));

		form.add(new ListView<ResourceLink>("rabataftaleDocuments", list) {
			@Override
			protected void populateItem(ListItem<ResourceLink> item) {
				ResourceLink<Void> link = item.getModelObject();
				item.add(link);
			}
		});
	}

	private ResourceLink newReportLink(String title, String outputFileName, Provider<String> report) {
		outputFileName = outputFileName.replace("{cvr}",
				StringUtils.isEmpty(MobileSession.get().getContract().getCustomer().getCompanyId())
						? ""
						: MobileSession.get().getContract().getCustomer().getCompanyId() + " - ");
		outputFileName = outputFileName.replace("{date}", ((new SimpleDateFormat("ddMMyy").format(new Date()))));

		ReportLink link = new ReportLink("link", outputFileName, report);
		link.add(new Label("title", title));
		return link;
	}

	private PdfWithDateStampLink newPdfLink(String title, String outputFileName, final String pathRelativeToClassPathRoot) {
		outputFileName = outputFileName.replace("{cvr}",
				StringUtils.isEmpty(MobileSession.get().getContract().getCustomer().getCompanyId())
						? ""
						: MobileSession.get().getContract().getCustomer().getCompanyId() + " - ");
		outputFileName = outputFileName.replace("{date}", ((new SimpleDateFormat("ddMMyy").format(new Date()))));

		PdfWithDateStampLink link = new PdfWithDateStampLink("link", pathRelativeToClassPathRoot, outputFileName);
		link.add(new Label("title", title));
		return link;
	}

	private AnyFileLink newAnyFileLink(String title, String outputFileName, final String pathRelativeToClassPathRoot, final String mimeType) {
		outputFileName = outputFileName.replace("{cvr}",
				StringUtils.isEmpty(MobileSession.get().getContract().getCustomer().getCompanyId())
						? ""
						: MobileSession.get().getContract().getCustomer().getCompanyId() + " - ");
		outputFileName = outputFileName.replace("{date}", ((new SimpleDateFormat("ddMMyy").format(new Date()))));

		AnyFileLink link = new AnyFileLink("link", pathRelativeToClassPathRoot, outputFileName, mimeType);
		link.add(new Label("title", title));
		return link;
	}
}
