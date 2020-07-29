package dk.jyskit.waf.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import dk.jyskit.waf.application.utils.exceptions.SystemException;
import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Application;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.response.filter.AjaxServerAndClientTimeFilter;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;
import org.apache.wicket.util.file.Path;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.wicket.kendo.ui.settings.KendoUILibrarySettings;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapTheme;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.DefaultThemeProvider;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.ITheme;
import de.agilecoders.wicket.webjars.WicketWebjars;
import dk.jyskit.waf.application.pages.nonadmin.google.GoogleVerificationPage;
import dk.jyskit.waf.application.pages.nonadmin.sitemap.SiteMapPage;
import dk.jyskit.waf.application.servlet.TransactionalRequestCycleListener;
import dk.jyskit.waf.components.jquery.kendo.KendoBehavior;
import dk.jyskit.waf.components.jquery.kendo.KendoJsReference;
import dk.jyskit.waf.eclipselink.PropertiesConverter;
import dk.jyskit.waf.wicket.response.JITJavaScriptHeaderResponseDecorator;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 */
@Slf4j
public abstract class JITWicketApplication extends WebApplication {
    private @Inject Environment environment;
	protected Injector injector;
	private ITheme nonAdminTheme;

	/**
	 * Constructor.
	 */
	public JITWicketApplication(Injector injector) {
		super();
		this.injector = injector;
		injector.injectMembers(this);

		// Try use normal wicket property
		String configProp = environment.getProperty(Environment.getNamespace() + ".wicket.configuration");
		RuntimeConfigurationType configEnum = RuntimeConfigurationType.DEPLOYMENT;
		if (configProp != null) {
			try {
				configEnum = RuntimeConfigurationType.valueOf(configProp);
			} catch (Exception ignore) {
				// ignore fallback to deployment.
				log.warn("Environment has wrong wicket.configuration: '" + configProp + "'. Proper values: " + Arrays.toString(RuntimeConfigurationType.values()));
			}
		}
		
		setConfigurationType(configEnum);
	}
	
	public String getSetting(String key) {
		if (Environment.WAF_ENV.equals(key)) {
			return Environment.get().getProperty(key);
		}
		return Environment.get().getProperty(Environment.getNamespace() + "." + key);
	}

    public static JITWicketApplication get() {
    	return (JITWicketApplication) WebApplication.get();
    }

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public abstract Class<? extends Page> getHomePage();

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
        super.init();
		setJavaScriptLibrarySettings(new LibrarySettings());
		
		KendoUILibrarySettings settings = KendoUILibrarySettings.get();
		settings.setJavaScriptReference(KendoJsReference.instance());
		settings.setCommonStyleSheetReference(KendoBehavior.COMMON_CSS);
		settings.setThemeStyleSheetReference(KendoBehavior.THEME_CSS); 
		
        Bootstrap.install(this, getBootstrapSettings());
        configureResourceBundles();

        optimizeForWebPerformance();

        mountPages();

        // mount additional pages
        if (getGoogleSiteVerificationId() != null) {
        	GoogleVerificationPage.setSiteId(getGoogleSiteVerificationId());
        	mountPage(getGoogleSiteVerificationId(), GoogleVerificationPage.class);
        }
        if (getSiteMapXml() != null) {
        	SiteMapPage.setXml(getSiteMapXml());
        	mountPage("sitemap.xml", SiteMapPage.class);
        }

		// Register the fact that wicket components should use the GuiceComponentInjector,
        // configured with the injector that we created via our subclass of ServletContextListener.
		getComponentInstantiationListeners().add(new GuiceComponentInjector(this, injector));

		getRequestCycleListeners().add(new TransactionalRequestCycleListener());  // Session-per-request

		WicketWebjars.install(this);

        initCleanMarkup();
        initResources();
        initRequestLogger();

        getApplicationSettings().setUploadProgressUpdatesEnabled(true);

        if (usesDevelopmentConfig()) {
            initHtmlHotDeploy();
            initDebugInformation();
        }

		IBootstrapSettings bootstrapSettings = Bootstrap.getSettings(Application.get());
		DefaultThemeProvider themeProvider = createThemeProvider();
		bootstrapSettings.setThemeProvider(themeProvider);

		// Add themes not already registered in theme provider
		for (ITheme theme : getThemes()) {
			try {
				themeProvider.add(theme);
			} catch (Exception e) {
				log.info("Theme already registered: " + theme.name());
			}
		}
		
		IPackageResourceGuard packageResourceGuard = getResourceSettings().getPackageResourceGuard();
		if (packageResourceGuard instanceof SecurePackageResourceGuard) {
			((SecurePackageResourceGuard) packageResourceGuard).addPattern("+*.woff");
			((SecurePackageResourceGuard) packageResourceGuard).addPattern("+*.woff2");
		}

		applicationInitialization();
	}

	protected void applicationInitialization() {
		JITApplicationInitializer initializer = getApplicationInitializer();
		if (initializer != null) {
			injector.injectMembers(initializer);
			initializer.init();
		}
	}

    /**
     * Override as needed
     * @return theme provider
     */
    protected DefaultThemeProvider createThemeProvider() {
		return new DefaultThemeProvider();
	}

	/**
     * Override this method to register themes used in your application.
     * @return all themes used in the application.
     */
    protected ITheme[] getThemes() {
		return new ITheme[] {getNonAdminTheme()};
	}

	public ITheme getNonAdminTheme() {
		if (nonAdminTheme == null) {
			nonAdminTheme = createNonAdminTheme();
		}
		return nonAdminTheme;
	}

	protected ITheme createNonAdminTheme() {
		return new BootstrapTheme();
	}

//	public String getNonAdminThemeName() {
//		return "bootstrap-responsive";  // hard-coded in BootstrapResponsiveTheme
//	}

	/**
     * Enables automatic reloading of HTML templates from your source code
     * directory. This means that whenever you modify an HTML file the
     * changes will appear immediately in your browser without needing to
     * redeploy. However you will still need to redeploy if you recompile
     * a Java class.
     * <p>
     * This method assumes that you are running the webapp "in place"; for
     * example using <code>mvn jetty:run</code>. It
     * will not work if you are running from a WAR or exploded WAR.
     * <p>
     * You must also be using the
     * standard maven project structure for your Wicket project, like so:
     * <pre class="example">
     * pom.xml
     * src/
     *   main/
     *     java/
     *     resources/
     *     webapp/</pre>
     *
     * @see <a href="http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin">http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin</a>
     */
    protected void initHtmlHotDeploy() {
        getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
        String htmlDir = getServletContext().getRealPath("/");
        if(htmlDir != null && !htmlDir.endsWith("/")) {
            htmlDir += "/";
        }
        try {
            getResourceSettings().getResourceFinders().add(new Path(htmlDir + "../java"));
		} catch (Exception e) {
			log.info("'/java' folder not found.");
		}
        try {
            getResourceSettings().getResourceFinders().add(new Path(htmlDir + "../resources"));
		} catch (Exception e) {
			log.info("'/resources' folder not found.");
		}
    }

    /**
     * Turns off Wicket's non-standard markup and sets the default markup
     * encoding to UTF-8. By default, Wicket will add markup to disabled
     * links; for example, it may wrap them in an &lt;em&gt; element. We
     * prefer to control the markup and don't want Wicket adding elements,
     * so we turn this off. We also instruct Wicket to strip the wicket:id
     * attributes and wicket: elements from the markup. This ensures that
     * the markup will be valid (X)HTML.
     */
    protected void initCleanMarkup() {
        getMarkupSettings().setDefaultBeforeDisabledLink(null);
        getMarkupSettings().setDefaultAfterDisabledLink(null);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
    }

    protected void initDebugInformation() {
        getRequestCycleSettings().addResponseFilter(
            new AjaxServerAndClientTimeFilter()
        );
        getDebugSettings().setOutputMarkupContainerClassName(true);
    }

    /**
     * Set the cache duration for resources to zero if in development mode
     * (discouraging browser cache), or 1 year if in deployment mode.
     */
    protected void initResources() {
        getResourceSettings().setDefaultCacheDuration(
            usesDevelopmentConfig() ? Duration.NONE : Duration.days(365)
        );
    }

    /**
     * Enables Wicket's request logging facility if an SLF4J logger is
     * configured for {@code INFO} with the category
     * {@code org.apache.wicket.protocol.http.RequestLogger}. For example,
     * if using log4j properties configuration, this would cause the Wicket
     * request logger to be enabled:
     * <pre class="example">
     * log4j.logger.org.apache.wicket.protocol.http.RequestLogger = INFO</pre>
     *
     * @since 2.0
     */
    protected void initRequestLogger() {
        if (LoggerFactory.getLogger(RequestLogger.class).isInfoEnabled()) {
            getRequestLoggerSettings().setRequestLoggerEnabled(true);
        }
    }

	public abstract JITApplicationInitializer getApplicationInitializer();

	public abstract void mountPages();

	/**
	 * @return Guice injector
	 */
	public Injector getInjector() {
		return injector;
	}

	private IBootstrapSettings getBootstrapSettings() {
        final BootstrapSettings settings = new BootstrapSettings();
        settings.setJsResourceFilterName("footer-container");
        Bootstrap.install(this, settings);
		return settings;
	}

	/**
     * optimize wicket for a better web performance
     */
    private void optimizeForWebPerformance() {
        if (usesDeploymentConfig()) {
        	// May cause problems:
            // getResourceSettings().setJavaScriptCompressor(new GoogleClosureJavaScriptCompressor(CompilationLevel.SIMPLE_OPTIMIZATIONS));

        	// May cause problems:
            // getResourceSettings().setCssCompressor(new YuiCssCompressor());
        	
            getFrameworkSettings().setSerializer(new DeflatedJavaSerializer(getApplicationKey()));
        }
        setHeaderResponseDecorator(new JITJavaScriptHeaderResponseDecorator());
    }

    /**
     * configure all resource bundles (css and js)
     */
    protected abstract void configureResourceBundles();


	/**
	 * ID for verification for Google Webmaster tools, etc. The value should be similar to this:
	 * 		google659ba521b15cbee3
	 * This will automatically create a verification page with the url: http://mydomain.com/google659ba521b15cbee3.html
	 */
	public abstract String getGoogleSiteVerificationId();

	/**
	 * Site Map. If you have a pretty static site you can use this page to generate a site map
	 * once and for all: http://www.xml-sitemaps.com/.
	 * This is optional, but useful for Google Webmaster tools and possibly other things.
	 */
	public abstract String getSiteMapXml();

	public DefaultThemeProvider getThemeProvider() {
		IBootstrapSettings bootstrapSettings = Bootstrap.getSettings(Application.get());
		return (DefaultThemeProvider) bootstrapSettings.getThemeProvider();
	}

	public void useNonAdminTheme() {
		Bootstrap.getSettings(this).getActiveThemeProvider().setActiveTheme(getNonAdminTheme().name());
	}

	public String getBootstrapVersion() {
		return Bootstrap.getSettings(this).getVersion();
	}

	public boolean isBootstrapVersionNewerThan(String version) {
		return getBootstrapVersion().compareTo(version) >= 0;
	}

	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator converterLocator = new ConverterLocator();
		converterLocator.set(java.util.Date.class, new DateConverter() {
			@Override
			public DateFormat getDateFormat(Locale locale) {
				// TODO locale setup
				return new SimpleDateFormat("dd-MM-yyyy HH:mm");
			}

			@Override
			public Date convertToObject(String value, Locale locale) {
				return super.convertToObject(value, locale);
			}
		});
		converterLocator.set(java.sql.Date.class, new DateConverter() {
			@Override
			public DateFormat getDateFormat(Locale locale) {
				// TODO locale setup
				return new SimpleDateFormat("dd-MM-yyyy");
			}

			@Override
			public Date convertToObject(String value, Locale locale) {
				Date date = super.convertToObject(value, locale);
				return date == null ? null : new java.sql.Date(date.getTime());
			}
		});
		converterLocator.set(java.sql.Time.class, new DateConverter() {
			@Override
			public DateFormat getDateFormat(Locale locale) {
				// TODO locale setup
				return new SimpleDateFormat("HH:mm");
			}

			@Override
			public Date convertToObject(String value, Locale locale) {
				Date date = super.convertToObject(value, locale);
				return date == null ? null : new java.sql.Time(date.getTime());
			}
		});
		converterLocator.set(java.sql.Timestamp.class, new DateConverter() {
			@Override
			public DateFormat getDateFormat(Locale locale) {
				// TODO locale setup
				return new SimpleDateFormat("dd-MM-yyyy HH:mm");
			}

			@Override
			public Date convertToObject(String value, Locale locale) {
				Date date = super.convertToObject(value, locale);
				return date == null ? null : new java.sql.Timestamp(date.getTime());
			}
		});

		converterLocator.set(DateTime.class, new IConverter<DateTime>() {
			@Override
			public DateTime convertToObject(String value, Locale locale) {
				if (Strings.isEmpty(value)) {
					return null;
				}
				return DateTime.parse(value, formatter(locale));
			}

			@Override
			public String convertToString(DateTime value, Locale locale) {
				return value == null ? null : value.toString(formatter(locale));
			}

			public DateTimeFormatter formatter(Locale locale) {
				return DateTimeFormat.forPattern("dd-MM-yyyy HH:mm").withLocale(locale);
			}
		});

		converterLocator.set(Period.class, new IConverter<Period>() {
			PeriodFormatter hoursAndMinutes = new PeriodFormatterBuilder().printZeroAlways().appendHours().appendSuffix(":").appendMinutes().toFormatter();

			@Override
			public Period convertToObject(String value, Locale locale) {
				return hoursAndMinutes.parsePeriod(value);
			}

			@Override
			public String convertToString(Period value, Locale locale) {
				return hoursAndMinutes.print(value);
			}
		});

		converterLocator.set(Properties.class, new IConverter<Properties>() {
			PropertiesConverter propsConverter = new PropertiesConverter();

			@Override
			public Properties convertToObject(String value, Locale locale) {
				return propsConverter.convertToEntityAttribute(value);
			}

			@Override
			public String convertToString(Properties value, Locale locale) {
				return propsConverter.convertToDatabaseColumn(value);
			}
		});

		return converterLocator;
	}
}
