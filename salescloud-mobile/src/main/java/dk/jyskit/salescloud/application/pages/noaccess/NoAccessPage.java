package dk.jyskit.salescloud.application.pages.noaccess;

import org.apache.wicket.markup.html.basic.Label;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.pages.base.BasePage;

public class NoAccessPage extends BasePage {
	public NoAccessPage(String reason) {
		CoreSession.get().setContract(null);
		
		add(new Label("reason", reason));
	}
}
