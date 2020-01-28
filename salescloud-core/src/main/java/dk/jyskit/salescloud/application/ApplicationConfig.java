package dk.jyskit.salescloud.application;

import static dk.jyskit.waf.application.model.extradata.ExtraDataReader.*;
import lombok.extern.slf4j.Slf4j;
import dk.jyskit.waf.wicket.security.UserSession;

/**
 * "Constants" and configuration. 
 *
 * @author jan
 */
@Slf4j
public class ApplicationConfig {
	private static ApplicationConfig instance = new ApplicationConfig();

	public static ApplicationConfig get() {
		return instance;
	}

	public int getTableRowsPerPage() {
		return toInt(getUserPref("ui.table_rows_per_page", "20"));
	}
	
	public int[] getTableRowsPerPageOptions() {
		return toIntArray(getUserPref("ui.table_rows_per_page_option", "10,20,40,80"));
	}

	public String getUserPref(String key, String defaultValue) {
		try {
			String pref = UserSession.get().getUserPreference(key);
			return nvl(pref, defaultValue);
		} catch (IllegalArgumentException e) {
			log.warn("Get session preferences called without request cycle " + e.getMessage());
			return defaultValue;
		}
	}

}
