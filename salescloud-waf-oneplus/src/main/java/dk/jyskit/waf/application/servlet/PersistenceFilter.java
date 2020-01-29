package dk.jyskit.waf.application.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

/**
 * Jan: PersistFilter, but modified so that it can co-exist with PersistenceInitializer,
 * which seems to be needed by unit tests. 
 *
 * Apply this filter to enable the HTTP Request unit of work and to have
 * guice-persist manage the lifecycle of active units of work. The filter
 * automatically starts and stops the relevant {@link PersistService} upon
 * {@link javax.servlet.Filter#init(javax.servlet.FilterConfig)} and
 * {@link javax.servlet.Filter#destroy()} respectively.
 * 
 * <p>
 * To be able to use the open session-in-view pattern (i.e. work per request),
 * register this filter <b>once</b> in your Guice {@code ServletModule}. It is
 * important that you register this filter before any other filter.
 * 
 * For multiple providers, you should register this filter once per provider,
 * inside a private module for each persist module installed (this must be the
 * same private module where the specific persist module is itself installed).
 * 
 * <p>
 * Example configuration:
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	public class MyModule extends ServletModule {
 * 		public void configureServlets() {
 * 			filter(&quot;/*&quot;).through(PersistFilter.class);
 * 
 * 			serve(&quot;/index.html&quot;).with(MyHtmlServlet.class);
 * 			// Etc.
 * 		}
 * 	}
 * }
 * </pre>
 * <p>
 * This filter is thread safe and allows you to create injectors concurrently
 * and deploy multiple guice-persist modules within the same injector, or even
 * multiple injectors with persist modules withing the same JVM or web app.
 * <p>
 * This filter requires the Guice Servlet extension.
 * 
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 */
@Singleton
public final class PersistenceFilter implements Filter {
	private final UnitOfWork unitOfWork;
	private final PersistService persistService;

	@Inject
	public PersistenceFilter(UnitOfWork unitOfWork, PersistService persistService) {
		this.unitOfWork = unitOfWork;
		this.persistService = persistService;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			persistService.start();
		} catch (Exception e) {
			// ignore it
		}
	}

	public void destroy() {
		persistService.stop();
	}

	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain filterChain) throws IOException, ServletException {

		unitOfWork.begin();
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			unitOfWork.end();
		}
	}
}
