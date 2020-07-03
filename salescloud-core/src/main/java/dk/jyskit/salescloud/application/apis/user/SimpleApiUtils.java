package dk.jyskit.salescloud.application.apis.user;

import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class SimpleApiUtils {
	private static Long previousTime = null;

	public static String checkToken(String token) {
		if (!StringUtils.isEmpty(token)) {
			try {
				Long time = getTime(token);
				if (time == null) {
					return "Invalid token (1)";
				} else if (System.currentTimeMillis() - time > 1000 * 60) {
					return "Invalid token (2)";		// Too old
				} else if (System.currentTimeMillis() - time < 0) {
					return "Invalid token (3)";		// Too new
				} else {
					return null; // OK
				}
			} catch (Exception e) {
				return "Invalid token (4)";		// Other error
			}
		}
		return "Invalid token (5)";		// No token
	}

	public static Long getTime(String token) {
		try {
			return Long.valueOf(SimpleStringCipher.decrypt(token));
		} catch (Exception e) {
			log.error("Bad token", e);
		}
		return null;
	}
}
