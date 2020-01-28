package dk.jyskit.salescloud.application.pages.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.github.rjeschke.txtmark.Processor;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.base.themes.magicbootstrap.TdcTheme;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.pages.SEOInfo;
import dk.jyskit.waf.application.pages.admin.JITAdminBasePage;
import dk.jyskit.waf.wicket.components.panels.modal.ModalContainer;
import dk.jyskit.waf.wicket.security.UserSession;
import dk.jyskit.waf.wicket.utils.WicketUtils;

@SuppressWarnings("serial")
@Slf4j
public abstract class BasePage extends JITAdminBasePage {

	private static SEOInfo SEO_INFO = new SEOInfo("TDC Salescloud", "", "");
	protected ModalContainer modalContainer;
	private NotificationPanel notificationPanel;
	
	/**
	 * Construct.
	 *
	 * @param parameters
	 *          current page parameters
	 */
	public BasePage(PageParameters parameters) {
		super();
		
		WebMarkupContainer maintenanceMode = new WebMarkupContainer("maintenanceMode");
		add(maintenanceMode);
		
		WebMarkupContainer standardMode = new TransparentWebMarkupContainer("standardMode");
		add(standardMode);
		
		if (CoreSession.get().isMaintenanceMode()) {
			maintenanceMode.add(new Label("maintenanceText", Processor.process(CoreSession.get().getMaintenanceText())).setEscapeModelStrings(false));
			maintenanceMode.setVisible(true);
			if (!CoreSession.get().isMaintenanceModeWarning() && !CoreSession.get().getUser().hasRole(AdminRole.class)) {
				standardMode.setVisible(false);
				Navbar navbar = (Navbar) WicketUtils.findOnPage(this, Navbar.class);
				if (navbar != null) {
					navbar.setVisible(false);
				}
			}

			maintenanceMode.add(new Image("logo", TdcTheme.LOGO_BIG_REFERENCE));
		} else {
			maintenanceMode.setVisible(false);
			standardMode.setVisible(true);
		}
		notificationPanel = new NotificationPanel("notifications");
		standardMode.add(notificationPanel.setOutputMarkupId(true));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
//		Map<String, Object> variables = new HashMap<String, Object>();
//		IModel<Map<String, Object>> variablesModel = new Model((Serializable)variables);
//
//		BaseUser user = UserSession.get().getUser();
//		if (user != null) {
//			variables.put("userName", user.getFullName());
//			variables.put("userEmail", user.getEmail());
//			SalespersonRole salespersonRole = (SalespersonRole) user.getRole(SalespersonRole.class);
//			if (salespersonRole != null) {
//				if (salespersonRole.isAgent()) {
//					variables.put("salesperson", "Agent");
//				} else if (salespersonRole.isAgent_lb()) {
//					variables.put("salesperson", "Agent LB");
//				} else if (salespersonRole.isAgent_mb()) {
//					variables.put("salesperson", "Agent MB");
//				} else if (salespersonRole.isAgent_sa()) {
//					variables.put("salesperson", "Agent SA");
//				} else if (salespersonRole.isPartner()) {
//					variables.put("salesperson", "Partner");
//				} else if (salespersonRole.isPartner_ec()) {
//					variables.put("salesperson", "Partner EC");
//				}
//				variables.put("userDivision", salespersonRole.getDivision());
//			}
//			variables.put("userRole", "");  // TODO - fjern, eller ?
//			if (user.getCreationDate() != null) {
//				variables.put("userCreated", "" + user.getCreationDate().getTime() / 1000);
//			}
//
//	        if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//				variables.put("appId", "nr193njn");
//			} else {
//				variables.put("appId", "efv3fig3");
//			}
//
//			for (String var : new String[] {"userName", "userEmail", "userCreated", "userRole", "userDivision"}) {
//				if (variables.get(var) == null) {
//					variables.put(var, "");
//				}
//			}
//
//			if (CoreApplication.get().getSetting(Environment.WAF_ENV).startsWith("dev") ||
//				CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//				WicketUtils.renderJavaScriptInBody(response, BasePage.class, "intercom.js", variablesModel);
//			}
//		}
	}
	
	public BasePage() {
		this(new PageParameters());
	}

	@Override
	protected String getAuthor() {
		return "Jan Mikkelsen";
	}
	
	@Override
	protected SEOInfo getSEOInfo() {
		return SEO_INFO;
	}
	
//	protected Component createContentPanel(String wicketId, PageParameters parameters) {
//		return null;
//	}
	
	protected Component createContentPanel(String wicketId, PageInfo pageInfo) {
		return null;
	}
	
	public NotificationPanel getNotificationPanel() {
		return notificationPanel;
	}
}
