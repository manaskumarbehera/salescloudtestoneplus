package dk.jyskit.salescloud.application.apis.auth;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiAuthUpdatePage extends WebPage {
	private static Gson gson = new GsonBuilder().create();
	
	@Inject
	private UserDao userDao;
	
	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("json", "application/json");
	}

	@Override
	public void renderPage() {
		AuthorizationError error = null;
		AuthorizationSuccess success = null;

		Response response = getRequestCycle().getResponse();
		
		String token = null;
		
		// Using POST!
		try {
			HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
			String body = IOUtils.toString(request.getInputStream(), "UTF-8");

			RoleChangeInput roleChangeInput = gson.fromJson(body, RoleChangeInput.class);
			token = roleChangeInput.getToken();
			
			String errorText = AuthUtils.checkToken(token); 
			if (errorText == null) {
				try {
					UsernamePasswordTime usernamePasswordTime = AuthUtils.getUsernamePasswordTime(token);
					BaseUser user = AuthUtils.getUser(usernamePasswordTime);
					SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
					role.setAgent(roleChangeInput.isAgent());
					role.setAgent_lb(roleChangeInput.isAgent_lb());
					role.setAgent_mb(roleChangeInput.isAgent_mb());
					role.setAgent_sa(roleChangeInput.isAgent_sa());
					role.setPartner(roleChangeInput.isPartner());
					role.setPartner_ec(roleChangeInput.isPartner_ec());
					userDao.save(user);
					success = AuthUtils.buildResponse(usernamePasswordTime, token);
					if (success == null) {
						error = new AuthorizationError();
						error.setError("Username or password is incorrect");
					}
				} catch (Exception e) {
					error = new AuthorizationError();
					error.setError("Bad token");
				}
			} else {
				error = new AuthorizationError();
				error.setError(errorText);
			}
		} catch (IOException e) {
			log.error("Failed to authorize", e);
		} 		
				
		try {
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());
			if (success != null) {
				out.write(gson.toJson(success)); 
			} else {
				out.write(gson.toJson(error)); 
				((HttpServletResponse) response.getContainerResponse()).setStatus(400);
			}
			out.flush();
		} catch (Exception e) {
			((HttpServletResponse) response.getContainerResponse()).setStatus(500);
		}
	}
} 