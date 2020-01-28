package dk.jyskit.waf.application.servlet;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

import dk.jyskit.waf.application.JITWicketApplication;

@Slf4j
public class TransactionalRequestCycleListener extends
		AbstractRequestCycleListener implements IApplicationListener {

	// EntityManager must wrap with Provider interface.
	@Inject
	private Provider<EntityManager> entityManager;

	// UnitOfWork must wrap with Provider interface.
	@Inject
	private Provider<UnitOfWork> workManager;

	@Inject
	private PersistService persistService;

	private boolean invalidateCacheOnRollback = false;

	public TransactionalRequestCycleListener() {
		Injector.get().inject(this);

		invalidateCacheOnRollback = "true".equals(JITWicketApplication.get().getSetting("waf.cache.invalidate_on_rollback"));
		// add this to applicationListeners for persistService start/stop.
		Application.get().getApplicationListeners().add(this);
		this.persistService.start();
	}

	@Override
	public void onBeginRequest(RequestCycle cycle) {
		try {
			log.debug("Begin unit of work url: " + cycle.getRequest().getUrl());
			this.workManager.get().begin();
		} catch (IllegalStateException e) {
			log.warn("Request already started unit of work url: "
					+ cycle.getRequest().getUrl());
		}
		this.entityManager.get().getTransaction().begin();
	}

	@Override
	public void onEndRequest(RequestCycle cycle) {
		try {
			if (this.entityManager.get().getTransaction().isActive()) {
				if (this.entityManager.get().getTransaction().getRollbackOnly()) {
					this.entityManager.get().getTransaction().rollback();
					onRollback();
				} else {
					try {
						this.entityManager.get().getTransaction().commit();
					} catch (RollbackException e) {
						if (e.getCause() instanceof ConstraintViolationException) {
							ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
							
							for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
								log.error("-----------------------------------------------------------------------------------");
								log.error(constraintViolation.getMessage());
								log.error("I'm guessing this is the problem: \n"
										+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
										+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
										+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
								log.error("-----------------------------------------------------------------------------------");
							}
						} else {
							log.error("Other problem", e);
						}
						onRollback();
						log.error("Error on commit", e);
						throw e;
					}
				}
			}
		} finally {
			this.workManager.get().end();
			log.debug("End unit of work url: " + cycle.getRequest().getUrl());
		}
	}

	public void onRollback() {
		if (invalidateCacheOnRollback) {
			Cache cache = entityManager.get().getEntityManagerFactory()
					.getCache();
			if (cache != null) {
				cache.evictAll();
			}
		}
	}

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		if (this.entityManager.get().getTransaction().isActive()) {
			this.entityManager.get().getTransaction().setRollbackOnly();
		}
		log.debug("Excption caught in request cycle listener", ex);
		return super.onException(cycle, ex);
	}

	@Override
	public void onAfterInitialized(Application application) {
	}

	@Override
	public void onBeforeDestroyed(Application application) {
		this.persistService.stop();
	}

	private boolean isProcessingAjaxRequest() {
		RequestCycle rc = RequestCycle.get();
		Request request = rc.getRequest();
		if (request instanceof WebRequest) {
			return ((WebRequest) request).isAjax();
		}
		return false;
	}
}