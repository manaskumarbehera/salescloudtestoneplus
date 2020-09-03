package dk.jyskit.salescloud.application.pages.contractsummary;

import com.x5.template.Chunk;

public class AllongeReport extends ContractAcceptReport {

	public AllongeReport() {
		super(true, true, true, true);
	}

	@Override
	protected void setProperties(Chunk html) {
		super.setProperties(html);
	}

	@Override
	protected String getTitle() {
   		return "TDC Erhverv One+";
	}

	@Override
	protected String getTemplateName() {
		return "allonge";
	}
}
