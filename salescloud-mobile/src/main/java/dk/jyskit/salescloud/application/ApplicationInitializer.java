package dk.jyskit.salescloud.application;

import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.JITApplicationInitializer;

/**
 * Initialization of application. This may include initialization of database the FIRST
 * time the application is executed. It may also include initialization that must be done
 * EVERY time the application is executed.
 * 
 * @author jan
 */
public class ApplicationInitializer implements JITApplicationInitializer {

	public void init() {
		Initializer[] initializers;
		
		// This property is not prefixed with namespace!
		if (Environment.isOneOf("dev")) {
			initializers = new Initializer[] { 
					new UserInitializer()
//					, new MobileVoiceInitializer()
//					, new SwitchboardInitializer(true)
//					, new SwitchboardInitializer(false)
//					, new FiberErhvervPlusInitializer()
//					, new FiberErhvervInitializer()
//					, new WiFiInitializer()
//					, new TdcWorksInitializer()
//					, new TdcOfficeInitializer()
					, new OnePlusInitializer()
				};
		} else if (Environment.isOneOf("prod-low", "oneplus", "heroku")) {
			initializers = new Initializer[] {
					new UserInitializer()
//					, new MobileVoiceInitializer()
//					, new SwitchboardInitializer(true)
//					, new FiberErhvervPlusInitializer()
//					, new FiberErhvervInitializer()
//					, new WiFiInitializer()
//					, new TdcWorksInitializer()
//					, new TdcOfficeInitializer()
					, new OnePlusInitializer()
			};
		} else {
			initializers = new Initializer[] { 
					new UserInitializer()
					, new MobileVoiceInitializer()
					, new SwitchboardInitializer(true)      
					, new FiberErhvervPlusInitializer()
					, new FiberErhvervInitializer()
					, new WiFiInitializer()
					, new TdcWorksInitializer()
					, new TdcOfficeInitializer()
				};
		}
		
		for (Initializer initializer : initializers) {
			MobileSalescloudApplication.get().getInjector().injectMembers(initializer);
			if (initializer.needsInitialization()) {
				initializer.initialize();
			}
			initializer.makeUpgrades();
		}
	}
}
