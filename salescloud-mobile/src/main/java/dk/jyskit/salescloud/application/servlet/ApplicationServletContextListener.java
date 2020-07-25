package dk.jyskit.salescloud.application.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.servlet.JITGuiceServletContextListener;

public class ApplicationServletContextListener extends JITGuiceServletContextListener {

	public ApplicationServletContextListener() {
		Environment.NAMESPACE = "salescloud-oneplus";
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(getApplicationServletModule(), new CoreModule());
	}

	@Override
	protected ApplicationServletModule getApplicationServletModule() {
		return new ApplicationServletModule();
	}	
}