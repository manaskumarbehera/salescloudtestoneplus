package dk.jyskit.salescloud.application;

import com.google.inject.Inject;
import com.google.inject.Injector;

import dk.jyskit.waf.application.JITApplicationInitializer;

public class TestApplication extends MobileSalescloudApplication {

    @Inject
	public TestApplication(Injector injector) {
		super(injector);
	}

	@Override
	public JITApplicationInitializer getApplicationInitializer() {
		return new TestInitializer();
	}

}
