package dk.jyskit.waf.application.pages.admin;

import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.pages.JITBasePage;
import dk.jyskit.waf.wicket.components.panels.modal.ModalContainer;

@SuppressWarnings("serial")
public abstract class JITAdminBasePage extends JITBasePage {
	protected ModalContainer modalContainer;

	public JITAdminBasePage() {
		super();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(JITAuthenticatedWicketApplication.get().createAdminNavbar("navbar", this));

		modalContainer = new ModalContainer("modalContainer");
		add(modalContainer);
	}
	
	protected boolean hasCss() {
		// Admin pages CAN have their "own" (classname + '.css') CSS. It is just less likely.
		return false;
	}

	@Override
	protected void onConfigure() {
		JITAuthenticatedWicketApplication.get().useAdminTheme();
	}
}
