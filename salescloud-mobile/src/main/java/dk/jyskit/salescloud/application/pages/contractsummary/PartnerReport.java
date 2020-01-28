package dk.jyskit.salescloud.application.pages.contractsummary;

import java.io.Serializable;

import com.google.inject.Provider;
import com.x5.template.Chunk;
import com.x5.template.Theme;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.PartnerData;

public abstract class PartnerReport implements Provider<String>, Serializable {
	private String templateName;
	private String documentHeader;
	private String imageName;
	private boolean includeSupport;
	private boolean includeHardware;

	public PartnerReport(String templateName, String documentHeader, String imageName, boolean includeSupport, boolean includeHardware) {
		this.templateName = templateName;
		this.documentHeader = documentHeader;
		this.imageName = imageName;
		this.includeSupport = includeSupport;
		this.includeHardware = includeHardware;
	}
	
	@Override
	public String get() {
        Theme theme = new Theme("themes", "reports");
        Chunk html = theme.makeChunk(templateName);

        PartnerData data = MobileSession.get().getContract().getPartnerData(getVariant());

        for (String key: data.getValues().keySet()) {
        	Object value = data.getValues().get(key);
        	html.set(key, value);
        }

		html.set("main_image", "/images/" + imageName);	// Lavet med Gimp. Source fil ligger i samme folder
		html.set("aig_image", "/images/aig.jpg");
		html.set("document_header", documentHeader);
		html.set("include_support", includeSupport);
		html.set("include_hardware", includeHardware);

		return html.toString();
	}

	abstract int getVariant();
}
