package dk.jyskit.salescloud.application.apis.user;

import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
public class UserApiClient {
	public static void changePasswordOnOtherServer(String username, String password) {
		String otherServer = CoreApplication.get().getSetting("baseurlOtherServer");
		if (!StringUtils.isEmpty(otherServer)) {
			try {
				RequestBody formBody = new FormBody.Builder()
						.add("username", username)
						.add("password", password)
						.add("token", SimpleStringCipher.encrypt("" + System.currentTimeMillis()))
						.build();
				Request request = new Request.Builder()
						.url(otherServer + "/v1/api/user/password")
						.addHeader("User-Agent", "Salescloud")
						.post(formBody)
						.build();
				try (Response response = new OkHttpClient().newCall(request).execute()) {
					if (!response.isSuccessful()) {
						log.warn("Response code: " + response.code());
						throw new IOException("Unexpected code " + response.code());
					}
				}
			} catch (Exception e) {
				log.error("", e);
				e.printStackTrace();
			}
		}
	}
}
