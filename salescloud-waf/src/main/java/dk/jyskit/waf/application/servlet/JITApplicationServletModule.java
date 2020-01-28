package dk.jyskit.waf.application.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.h2.server.web.WebServlet;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.dao.ExtraDataDao;
import dk.jyskit.waf.application.dao.ExtraDataDefinitionDao;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.dao.impl.ExtraDataDaoImpl;
import dk.jyskit.waf.application.dao.impl.ExtraDataDefinitionDaoImpl;
import dk.jyskit.waf.application.dao.impl.RoleDaoImpl;
import dk.jyskit.waf.application.dao.impl.UserDaoImpl;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.extradata.ExtraData;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;
import dk.jyskit.waf.application.services.markdown.MarkDownJServiceImpl;
import dk.jyskit.waf.application.services.markdown.MarkDownService;
import dk.jyskit.waf.application.services.passwordencryption.PasswordEncryptionService;
import dk.jyskit.waf.application.services.passwordencryption.pbkdf2.PasswordEncryptionPbkdf2;

/**
 * See http://software.danielwatrous.com/wicket-guice-including-unittests/
 *
 * @author jan
 */
public abstract class JITApplicationServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		bind(WebApplication.class).to(getWicketApplicationClass());
		
		Environment environment = new Environment();

		bind(Environment.class).toInstance(environment);
		bind(WicketFilter.class).to(CustomWicketFilter.class);

		// Note the argument to the JpaPersistModule constructor must match the name of the persistence unit in persistence.xml.
		String persistenceModule = environment.getProperty(getNamespace() + "." + Environment.WAF_PERSISTENCE, getPersistenceUnitName());

		Properties nonNamespacedProperties = new Properties();
		Properties namespacedProperties = environment.getProperties();
		for (Object key : namespacedProperties.keySet()) {
			if (((String) key).startsWith(getNamespace())) {
				nonNamespacedProperties.put(((String) key).replaceFirst(getNamespace() + ".", ""), namespacedProperties.get(key));
			}
		}
		install(new JpaPersistModule(persistenceModule).properties(nonNamespacedProperties));

		// Everything filters through the WicketFilter next. So all of your wicket pages will have access to the EntityManager
		// (from Guice-Persist) as well as all of the other dependencies injected by Guice-Core.
		filter("/*").through(WicketFilter.class, createWicketFilterInitParams());

		bind(PasswordEncryptionService.class).to(PasswordEncryptionPbkdf2.class);
		bind(MarkDownService.class).to(MarkDownJServiceImpl.class);

		if ("org.h2.Driver".equals(nonNamespacedProperties.get("javax.persistence.jdbc.driver"))) {
			serve("/h2/*").with(WebServlet.class);
			bind(WebServlet.class).in(Singleton.class);
			
			// H2 console can be accessed at http://localhost:8080/h2/
			// Enter URL from environment configuration
		}
		
		// Binding of WAF provided DAOs
		// NOTE: We expose the DAOs in two ways. The generic one is for generic lists
		
		TypeLiteral<RoleDaoImpl> roleDaoImpl = new TypeLiteral<RoleDaoImpl>(){};
		bind(new TypeLiteral<RoleDao>(){}).to(roleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<BaseRole>>(){}).to(roleDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<UserDaoImpl> userDaoImpl = new TypeLiteral<UserDaoImpl>(){};
		bind(new TypeLiteral<UserDao>(){}).to(userDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<BaseUser>>(){}).to(userDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ExtraDataDaoImpl> extraDataDaoImpl = new TypeLiteral<ExtraDataDaoImpl>(){};
		bind(new TypeLiteral<ExtraDataDao>(){}).to(extraDataDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ExtraData>>(){}).to(extraDataDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ExtraDataDefinitionDaoImpl> extraDataDefinitionDaoImpl = new TypeLiteral<ExtraDataDefinitionDaoImpl>(){};
		bind(new TypeLiteral<ExtraDataDefinitionDao>(){}).to(extraDataDefinitionDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ExtraDataDefinition>>(){}).to(extraDataDefinitionDaoImpl).in(Scopes.SINGLETON);
		
		// Binding of DAOs for the rest of the application, etc.
		guiceInit();
	}

	protected String getPersistenceUnitName() {
		return "persistence";
	}

	public abstract void guiceInit();

	public abstract Class getWicketApplicationClass();
	public abstract String getNamespace();

	private Map<String, String> createWicketFilterInitParams() {
		Map<String, String> wicketFilterParams = new HashMap<String, String>();

		wicketFilterParams.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");

		// Add an init-param to the wicket filter giving the full
		// class name of our Wicket application class.
		wicketFilterParams.put("applicationClassName", getWicketApplicationClass().getCanonicalName());
		return wicketFilterParams;
	}

	@Singleton
	private static class CustomWicketFilter extends WicketFilter {
		@Inject
		private Provider<WebApplication> webApplicationProvider;

		@Override
		protected IWebApplicationFactory getApplicationFactory() {
			return new IWebApplicationFactory() {
				@Override
				public WebApplication createApplication(WicketFilter filter) {
					return webApplicationProvider.get();
				}

				@Override
				public void destroy(WicketFilter filter) {
				}
			};
		}
	}
}
