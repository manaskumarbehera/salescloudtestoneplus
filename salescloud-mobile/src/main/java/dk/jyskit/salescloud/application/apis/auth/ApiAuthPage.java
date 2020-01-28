package dk.jyskit.salescloud.application.apis.auth;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiAuthPage extends WebPage {
	private String username;
	private String password;
	private String token;
	private static Gson gson = new GsonBuilder().create();
	
	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("json", "application/json");
	}

	@Override
	public void renderPage() {
		AuthorizationError error = null;
		AuthorizationSuccess success = null;

		// Using POST!
		try {
			HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
			String body = IOUtils.toString(request.getInputStream(), "UTF-8");

			AuthorizationInput authorizationInput = gson.fromJson(body, AuthorizationInput.class);
			username 	= authorizationInput.getUsername();
			password 	= authorizationInput.getPassword();
			token	 	= authorizationInput.getToken();
		} catch (IOException e) {
			log.error("Failed to authorize " + username, e);
		} 		
				
		Response response = getRequestCycle().getResponse();
		try {
			if (!StringUtils.isEmpty(token)) {
				String errorText = AuthUtils.checkToken(token); 
				if (errorText == null) {
					try {
						success = AuthUtils.buildResponse(AuthUtils.getUsernamePasswordTime(token), token);
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
			} else if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
				UsernamePasswordTime usernamePasswordTime = new UsernamePasswordTime();
				usernamePasswordTime.setUsername(username);
				usernamePasswordTime.setPassword(password);
				success = AuthUtils.buildResponse(usernamePasswordTime, null);
				if (success == null) {
					error = new AuthorizationError();
					error.setError("Username or password is incorrect");
				}
			} else {
				error = new AuthorizationError();
				error.setError("Token, username or password is incorrect");
			}
		
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