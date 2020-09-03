package dk.jyskit.waf.application.servlet;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.onami.scheduler.QuartzModule;
import org.apache.onami.scheduler.Scheduled;
import org.apache.wicket.util.string.Strings;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.LoggerFactory;
import org.wicketstuff.config.MatchingResources;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import dk.jyskit.waf.application.Environment;

public abstract class JITGuiceServletContextListener extends GuiceServletContextListener {
	public JITGuiceServletContextListener() {
		/*
		 * Configure logging by environment file in "/META.INF/env/" named "logback-<envname>.xml"
		 * Fallbacks to any name up to logback.xml by cutting a levels.
		 * E.g waf.env=dev-mysql tries "logback-dev-mysql.xml", "logback-dev.xml", "logback.xml" and use the first found.
		 */
		String path = "/META-INF/env/";
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		String envName = System.getProperty(Environment.WAF_ENV);
		if (envName != null) {
			try {
				List<String> envNameParts = new ArrayList<String>();
				envNameParts.add("logback");
				envNameParts.addAll(Arrays.asList(Strings.split(envName, '-')));
				while (!envNameParts.isEmpty()) {
					String name = path + Strings.join("-", envNameParts) + ".xml";
					URL url = getClass().getResource(name);
					if (url != null) {
						System.out.println("Logback configuration: " + name);
						reconfigureLogging(context, url);
						break;
					}
					envNameParts.remove(envNameParts.size() -1);
				}
			} catch (JoranException je) {
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		}
	}

	public void reconfigureLogging(LoggerContext context, URL url) throws JoranException {
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		context.reset();
		configurator.doConfigure(url);
	} 
	
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(getApplicationServletModule());
	}

	protected abstract JITApplicationServletModule getApplicationServletModule();
	
	protected QuartzModule getQuartzModule() {
		return new QuartzModule() {

			@Override
			protected void schedule() {
				for (Class<? extends Job> jobClass : getQuartzJobs()) {
					scheduleJob(jobClass);
				}
			}
		};
	}

	/**
	 * Gets a list of jobs to be scheduled. The jobs must be annotated with {@link Scheduled}.
	 * Defaults scans dk packages for jobs with the annotation and classname matching in *Job.
	 * @return
	 */
	protected List<Class<? extends Job> > getQuartzJobs() {
		List<Class<? extends Job>> jobClasses = findQuartzJobs("dk/jyskit/**/*Job");
		return jobClasses;
	}

	protected List<Class<? extends Job>> findQuartzJobs(String pattern) {
		MatchingResources jobs = new MatchingResources(pattern + ".class");
		List<Class<? extends Job>> jobClasses = new ArrayList<>();
		for (Class<?> jobClass : jobs.getAnnotatedMatches(Scheduled.class)) {
			if (Job.class.isAssignableFrom(jobClass)) {
				@SuppressWarnings("unchecked")
				Class<? extends Job> jobClass2 = (Class<? extends Job>) jobClass;
				jobClasses.add(jobClass2);
			}
		}
		return jobClasses;
	} 

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		final String INJECTOR_NAME = Injector.class.getName();
		Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(INJECTOR_NAME);
		if (injector != null) {
			Scheduler scheduler = injector.getInstance(Scheduler.class);
			if (scheduler != null) {
				try {
					scheduler.shutdown();
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
		}
		super.contextDestroyed(servletContextEvent);
	} 
}