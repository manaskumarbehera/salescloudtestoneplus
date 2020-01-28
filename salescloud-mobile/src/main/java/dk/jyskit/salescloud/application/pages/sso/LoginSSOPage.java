package dk.jyskit.salescloud.application.pages.sso;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.jyskit.salescloud.application.apis.auth.AuthUtils;
import dk.jyskit.salescloud.application.apis.auth.AuthorizationInput;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import dk.jyskit.salescloud.application.pages.home.AdminHomePage;
import dk.jyskit.salescloud.application.pages.noaccess.NoAccessPage;
import lombok.extern.slf4j.Slf4j;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME, UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class LoginSSOPage extends BasePage {
	private String token;
	
	@Override
	public void renderPage() {
		// Using POST!
		try {
			HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
			String body = IOUtils.toString(request.getInputStream(), "UTF-8");

			Gson gson = new GsonBuilder().create();
			AuthorizationInput authorizationInput = gson.fromJson(body, AuthorizationInput.class);
			token	 	= authorizationInput.getToken();
		} catch (IOException e) {
			log.error("Failed to authorize", e);
		} 		
				
		try {
			String errorText = AuthUtils.checkToken(token);
			if (errorText == null) {
				throw new RestartResponseException(AdminHomePage.class);
			} else {
				log.info("Login failed: " + errorText);
				throw new RestartResponseException(new NoAccessPage("Det ser desværre ikke ud til at du har adgang til denne applikation."));
			}
		} catch (Exception e) {
			if (e instanceof RestartResponseException) {
				throw (RestartResponseException) e;
			} else {
				throw new RestartResponseException(new NoAccessPage("Der skete en teknisk fejl."));
			}
		}
	}

}
