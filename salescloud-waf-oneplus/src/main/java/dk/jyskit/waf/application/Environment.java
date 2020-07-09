package dk.jyskit.waf.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import dk.jyskit.waf.utils.guice.Lookup;
/**
 * Environment configuration for waf.
 * <p>
 * <strong><code>-Dwaf.env=dev</code></strong> loads the environment configuration properties from /META-INF/env/dev.properties
 * </p>
 * /META-INF/env/common.properties is always loaded before the specific environment properties.
 * System properties can override any environment property.
 * The resulting properties are exposed thru this class but also thru System properties.
 * @author palfred
 *
 */
@Slf4j
public class Environment {
	/** can be used to make environment specific log level using logback */
	private static final String LOG_LEVEL_PREFIX = "log.level.";

	/** System property used to defined which environment waf loads */
	public static final String WAF_ENV = "waf.env";

	/** Name of persistence unit: default=persistence */
	public static final String WAF_PERSISTENCE = "waf.persistence";

	/** Default fade in for modal: Boolean (default=true ~ animate)*/
	public static final String WAF_MODAL_FADEIN = "waf.modal.fadein";

	/** Default fade in for modal: Boolean (default=true ~ animate)*/
	public static final String WAF_AJAX_VALIDATE = "waf.ajax.validate";

	private Properties properties = new Properties();

	private String name;
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public Properties getProperties() {
		return properties;
	}

	public Environment() {
		try {
			String fileName = "/META-INF/env/common.properties";
			InputStream inStream = getClass().getResourceAsStream(fileName);
			if (inStream == null) {
				log.error(fileName + " not found");
			} else {
				properties.load(inStream);
				name = System.getProperty(WAF_ENV);
				if (name == null) {
					log.warn("No environment (-Dwaf.env=...) configured");
				} else {	
					properties.load(getClass().getResourceAsStream("/META-INF/env/" + name + ".properties"));
				}
				properties.putAll(System.getProperties());

				// Special handling for Heroku environments
				String dbUrl = System.getenv("JAWSDB_URL");
				if (!StringUtils.isEmpty(dbUrl)) {
					// mysql://jcyzi5exh1e2tu6f:amfofpi0siruphs0@itg0sxltai3omdne.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/primary_app_db
					// salescloud.javax.persistence.jdbc.url=jdbc:mysql://honmv7ftvy8nenqd.chr7pe7iynqr.eu-west-1.rds.amazonaws.com/primary_app_db
					// salescloud.javax.persistence.jdbc.user=
					// salescloud.javax.persistence.jdbc.password=
					String s[] = dbUrl.split("@");
					if (s.length == 2) {
						properties.put("salescloud.javax.persistence.jdbc.url", "jdbc:mysql://" + s[1].replace(":3306", ""));
						s = s[0].replace("mysql://", "").split(":");
						if (s.length == 2) {
							properties.put("salescloud.javax.persistence.jdbc.user", s[0]);
							properties.put("salescloud.javax.persistence.jdbc.password", s[1]);
						}
					}
				}

				// sync environment and system properties.
				System.getProperties().putAll(properties);
				reconfigureLogger();
			}
		} catch (IOException e) {
			log.error("Unable to load JITWU environment", e);
		}
	}

	private void reconfigureLogger() {
		log.info("reconfigureLogger");
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if (loggerFactory instanceof LoggerContext) {
			LoggerContext logCtx = (LoggerContext) loggerFactory;
			for ( Object keyO : properties.keySet()) {
				String key = (String) keyO;
				if (key.startsWith(LOG_LEVEL_PREFIX)) {
					Level level = Level.toLevel(getProperty(key));
					String logger = key.replace(LOG_LEVEL_PREFIX, "");
					log.info("reconfigureLogger logger: " + logger + " level: " + level);
					logCtx.getLogger(logger).setLevel(level);
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public Boolean getBoolean(String key) {
		if (isDefined(key)) {
			return Boolean.valueOf(getProperty(key));
		}
		return null;
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		Boolean val = getBoolean(key);
		return val == null ? defaultVal : val;
	}

	public Integer getInteger(String key) {
		if (isDefined(key)) {
			try {
				return Integer.valueOf(getProperty(key));
			} catch (NumberFormatException e) {
				log.warn("Environment key: " + key + " is not an integer: " + getProperty(key));
				e.printStackTrace();
			}
		}
		return null;
	}

	public int getInteger(String key, int defaultVal) {
		Integer val = getInteger(key);
		return val == null ? defaultVal : val;
	}

	public boolean isDefined(String key) {
		return properties.containsKey(key);
	}

	public static Environment get() {
		return Lookup.lookup(Environment.class);
	}

	public static boolean useAjaxValidate() {
		return get().getBoolean(WAF_AJAX_VALIDATE, false);
	}

	public boolean isDevelopment() {
		return name.equals("dev");
	}

	public static boolean isOneOf(String ... envNames) {
		for (String envName : envNames) {
			if (Environment.get().getProperty(Environment.WAF_ENV).equals(envName)) {
				return true;
			}
		}
		return false;
	}
}
