package dk.jyskit.salescloud.application.apis.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.List;

@Slf4j
public class UserApiPasswordPage extends WebPage {
	private static Gson gson = new GsonBuilder().create();

	@Inject
	private UserDao userDao;

	public UserApiPasswordPage(final PageParameters pp) {
		super(pp);
		log.info("A");
		getRequestCycle().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8", sendResponse(pp)));
	}

	private String sendResponse(PageParameters pp) {
		ApiResponse apiResponse = null;

		String username = null;
		String password = null;
		String token = null;

		log.info("B");
		// Using POST!
		try {
			HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
			String body = IOUtils.toString(request.getInputStream(), "UTF-8");

			String[] arr = body.split("&");
			for (String s : arr) {
				String[] keyValue = s.split("=");
				String key 		= keyValue[0];
				String value 	= URLDecoder.decode(keyValue[1], "UTF-8");
				if (key.equals("username")) {
					username = value;
				}
				if (key.equals("password")) {
					password = value;
				}
				if (key.equals("token")) {
					token = value;
				}
			}
		} catch (IOException e) {
			log.error("Failed to authorize " + username, e);
		}

		Response response = getRequestCycle().getResponse();
		try {
			String error = null;
			if (!StringUtils.isEmpty(token)) {
				error = SimpleApiUtils.checkToken(token);
				if (error == null) {
					if (StringUtils.isEmpty(username)) {
						error = "Username not specified";
					} else if (StringUtils.isEmpty(password)) {
						error = "Password not specified";
					} else {
						// Change password
						List<BaseUser> users = userDao.findByUsername(username);
						if (users.size() == 0) {
							error = "No users with username '" + username + "'";
						} else if (users.size() > 1) {
							error = "Multiple users with username '" + username + "'";
						} else {
							BaseUser user = users.get(0);
							user.setPassword(password);
							user = userDao.save(user);
						}
					}
				}
			}

			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());
			if (error == null) {
				apiResponse = new ApiResponse();
				apiResponse.setResult("success");
				apiResponse.setToken(token);
				return gson.toJson(apiResponse).toString();
			} else {
				apiResponse = new ApiResponse();
				apiResponse.setResult(error);
				apiResponse.setToken(token);
				out.write(gson.toJson(apiResponse));
				((HttpServletResponse) response.getContainerResponse()).setStatus(400);
				log.warn(error);
				return gson.toJson(apiResponse).toString();
			}
		} catch (Exception e) {
			((HttpServletResponse) response.getContainerResponse()).setStatus(500);
		}
		return null;
	}

	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("json", "application/json");
	}

	@Override
	public void renderPage() {
		ApiResponse apiResponse = null;

		String username = null;
		String password = null;
		String token = null;

		log.info("C");
		// Using POST!
		try {
			HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
			String body = IOUtils.toString(request.getInputStream(), "UTF-8");

			PasswordChangeRequest passwordChangeInput = gson.fromJson(body, PasswordChangeRequest.class);
			username 	= passwordChangeInput.getUsername();
			password 	= passwordChangeInput.getPassword();
			token	 	= passwordChangeInput.getToken();
		} catch (IOException e) {
			log.error("Failed to authorize " + username, e);
		} 		
				
		Response response = getRequestCycle().getResponse();
		try {
			String error = null;
			if (!StringUtils.isEmpty(token)) {
				error = SimpleApiUtils.checkToken(token);
				if (error == null) {
					if (StringUtils.isEmpty(username)) {
						error = "Username not specified";
					} else if (StringUtils.isEmpty(password)) {
						error = "Password not specified";
					} else {
						// Change password
						List<BaseUser> users = userDao.findByUsername(username);
						if (users.size() == 0) {
							error = "No users with username '" + username + "'";
						} else if (users.size() > 1) {
							error = "Multiple users with username '" + username + "'";
						} else {
							BaseUser user = users.get(0);
							user.setPassword(password);
							user = userDao.save(user);
						}
					}
				}
			}

			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());
			if (error == null) {
				apiResponse = new ApiResponse();
				apiResponse.setResult("success");
				apiResponse.setToken(token);
				out.write(gson.toJson(apiResponse));
			} else {
				apiResponse = new ApiResponse();
				apiResponse.setResult(error);
				apiResponse.setToken(token);
				out.write(gson.toJson(apiResponse));
				((HttpServletResponse) response.getContainerResponse()).setStatus(400);
				log.warn(error);
			}

			out.flush();
		} catch (Exception e) {
			((HttpServletResponse) response.getContainerResponse()).setStatus(500);
		}
	}
} 