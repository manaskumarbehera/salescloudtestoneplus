package dk.jyskit.salescloud.application.pages;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.JITApplicationInitializer;

public class TestInitializer implements JITApplicationInitializer {

	@Inject private BusinessAreaDao businessAreaDao;
	@Inject private PageInfoDao pageInfoDao;
	
	@Override
	public void init() {
		BusinessArea businessArea = new BusinessArea();
		businessArea.setName("Mobile");
		businessAreaDao.save(businessArea);

		// ----------------------------
		// Pages
		// ----------------------------
		{
			PageInfo pageInfo = new PageInfo(CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", businessArea);
			pageInfo.setIntroHtml("Velkommen til One salgskonfigurator.<br><br>Konfiguratoren skal hjælpe dig til at lave det materiale, "
					+ "du skal bruge over mod dine kunder. Du kan danne et prisoverslag eller et egentligt handout, som kan "
					+ "bruges til at afstemme økonomi og løsning med kunderne.<br><br>Nedenfor har du to muligheder. Du kan "
					+ "åbne en eksisterende kundesag eller du kan oprette en ny sag ved at klikke på \"Ny kundesag\".");
			businessArea.addPage(pageInfo);
			pageInfoDao.save(pageInfo);
			
			pageInfo = new PageInfo(CorePageIds.SALES_MASTER_DATA, "Stamdata", businessArea);
			pageInfo.setIntroHtml("For at kunne lave et prisoverslag og et bilag, er det nødvendigt, at du indtaster "
					+ "stamdata, som systemet kan brevflette med.<br><br>"
					+ "Der er tale om oplysninger på dig som sælger, men også vigtige oplysninger omkring kunden og "
					+ "de kontaktpersoner, som du har talt med, og som du vil referere til i sagens videre forløb. ");
			businessArea.addPage(pageInfo);
			pageInfoDao.save(pageInfo);
		}
	}
}
