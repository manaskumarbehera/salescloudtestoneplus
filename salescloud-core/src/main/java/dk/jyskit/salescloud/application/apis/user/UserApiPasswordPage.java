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
import org.apache.wicket.request.http.handler.ErrorCodeRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

@Slf4j
public class UserApiPasswordPage extends WebPage {
	private static Gson gson = new GsonBuilder().create();

	@Inject
	private UserDao userDao;

	public UserApiPasswordPage(final PageParameters pp) {
		super(pp);

		ApiResponse apiResponse = null;

		String username = null;
		String password = null;
		String token = null;

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

		try {
			String error = null;
			if (!StringUtils.isEmpty(token)) {
				error = SimpleApiUtils.checkToken(token);
				if (StringUtils.isEmpty(error)) {
					if (StringUtils.isEmpty(username)) {
						error = "Username not specified";
					} else if (StringUtils.isEmpty(password)) {
						error = "Password not specified";
					} else {
						// Change password
						List<BaseUser> users = userDao.findByUsername(username);
						if (users.size() == 0) {
							error = "No users with username '" + username + "'";
						} else {
							if (users.size() > 1) {
								log.warn("Multiple users with username '" + username + "'");
							}
							BaseUser user = users.get(0);
							user.setPassword(password);
							user.setPasswordChangedDate(new Date());
							user = userDao.save(user);
							log.error("Updated password for " + user.getUsername());
						}
					}
				} else {
					log.warn("Reason for no change of password: " + error);
				}
			}

			if (error == null) {
				apiResponse = new ApiResponse();
				apiResponse.setResult("success");
				apiResponse.setToken(token);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
						new TextRequestHandler("application/json", "UTF-8", gson.toJson(apiResponse).toString()));
			} else {
				log.warn(error);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ErrorCodeRequestHandler(400));
			}
		} catch (Exception e) {
			getRequestCycle().scheduleRequestHandlerAfterCurrent(new ErrorCodeRequestHandler(500));
		}
	}
} 