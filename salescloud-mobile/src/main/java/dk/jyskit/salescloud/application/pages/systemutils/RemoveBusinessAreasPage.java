package dk.jyskit.salescloud.application.pages.systemutils;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.model.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME,
		UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class RemoveBusinessAreasPage extends BasePage {
	@Inject
	private MobileContractDao mobileContractDao;
	@Inject
	private MobileContractSummaryDao mobileContractSummaryDao;
	@Inject
	private SystemUpdateDao systemUpdateDao;
	@Inject
	private BusinessAreaDao businessAreaDao;
	private boolean working;

	public RemoveBusinessAreasPage(PageParameters parameters) {
		super(parameters);

		if (!working) {
			synchronized (RemoveBusinessAreasPage.class) {
				working = true;
				String name = "Remove businessarea " + parameters.get("ba"); // Don't change this name!
//			SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.ONE_PLUS);
//
//			if (update == null) {
//				update = new SystemUpdate();
//				update.setBusinessAreaId(BusinessAreas.ONE_PLUS);
//				update.setName(name);
//				log.info("Update starting: " + name);

				if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.TDC_OFFICE)) {
					removeBusinessArea(BusinessAreas.TDC_OFFICE);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.FIBER_ERHVERV)) {
					removeBusinessArea(BusinessAreas.FIBER_ERHVERV);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.FIBER)) {
					removeBusinessArea(BusinessAreas.FIBER);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.WIFI)) {
					removeBusinessArea(BusinessAreas.WIFI);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.TDC_WORKS)) {
					removeBusinessArea(BusinessAreas.TDC_WORKS);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.MOBILE_VOICE)) {
					removeBusinessArea(BusinessAreas.MOBILE_VOICE);
				} else if (Objects.equals(parameters.get("ba").toString(), "" + BusinessAreas.SWITCHBOARD)) {
					removeBusinessArea(BusinessAreas.SWITCHBOARD);
				}

//				try {
//					systemUpdateDao.save(update);
//				} catch (Exception e) {
//					log.error("FAIL", e);
//					handleInitializationException(e);
//				}
//
//				log.info("Update done: " + name);
//			} else {
//				log.info("Update already done");
//			}
				working = false;
			}
		}
	}

	public void removeBusinessArea(int businessAreaId) {
		long startTime = System.currentTimeMillis();
		BusinessArea businessArea = businessAreaDao.findUniqueByField("businessAreaId", businessAreaId);
		SubscriptionDao subscriptionDao = SubscriptionDao.lookup();
		if (businessArea != null) {
			StringBuilder sb = new StringBuilder();
			try {
				log.info("002 - Deleting... " + businessAreaId);

				int c = 0;
				boolean all = true;
				List<MobileContract> contracts = mobileContractDao.findAll();
				log.info("Contracts: " + contracts.size());
				for (MobileContract contract : mobileContractDao.findAll()) {
					if (Objects.equals(contract.getBusinessArea(), businessArea)) {
						if (System.currentTimeMillis() - startTime > 1000 * 60) {
							sb.append('Z');
							log.info("all = false;");
							all = false;
							mobileContractDao.flush();
							break;
						}
						log.info("Deleting contract " + contract.getName());
						if (contract.getSubscriptions().size() > 0) {
							log.info("Deleting " + contract.getSubscriptions().size() + " subscriptions");
							Iterator<Subscription> iter = contract.getSubscriptions().iterator();
							while (iter.hasNext()) {
								Subscription subscription = iter.next();
								iter.remove();
								subscriptionDao.delete(subscription);
							}
						}
						if (contract.getOrderLines().size() > 0) {
							log.info("Deleting " + contract.getOrderLines().size() + " orderlines");
							Iterator<OrderLine> iter = contract.getOrderLines().iterator();
							while (iter.hasNext()) {
								OrderLine orderLine = iter.next();
								iter.remove();
								OrderLineDao.lookup().delete(orderLine);
							}
						}
						sb.append('A');
						mobileContractDao.delete(contract);
						sb.append('B');
					}
				}

				contracts = mobileContractDao.findAll();
				log.info("--- Contracts: " + contracts.size());
				for (MobileContract contract : mobileContractDao.findAll()) {
					if (Objects.equals(contract.getBusinessArea(), businessArea)) {
						log.info("Still exists: " + contract.getId());
					}
				}

				sb.append('S');
				if (all) {
					sb.append('T');
					log.info("--- Deleting business area " + businessAreaId + " ---");
					businessAreaDao.delete(businessArea);
					businessAreaDao.flush();
					log.info("--- Deleted business area " + businessAreaId + " ---");
				} else {
					sb.append('U');
					log.info("--- Saving business area " + businessAreaId + " ---");
					businessAreaDao.save(businessArea);
					log.info("Not done deleting " + businessAreaId);
				}
			} catch (Exception e) {
				log.error("FAIL", e);
				handleInitializationException(e);
			}
			log.error(sb.toString());
		} else {
			log.info("Business area not found");
		}
	}

	private void handleInitializationException(Exception e) {
		if (e instanceof RollbackException) {
			if (((RollbackException) e).getCause() instanceof ConstraintViolationException) {
				handleConstraintViolationException((ConstraintViolationException) ((RollbackException) e).getCause());
			} else {
				log.error("A problem occured during initialization", e);
			}
		} else if (e instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e);
		} else if (e instanceof PersistenceException) {
			handlePersistenceException((PersistenceException) e);
		} else {
			log.error("A problem occured during initialization", e);
		}
	}

	private void handleConstraintViolationException(ConstraintViolationException e) {
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			log.error(constraintViolation.getMessage());
			log.error("I'm guessing this is the problem: \n" + "An object of type '"
					+ constraintViolation.getLeafBean().getClass().getSimpleName() + "' has a property '"
					+ constraintViolation.getPropertyPath() + "' which has value '"
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage()
					+ "'");
		}
	}

	private void handlePersistenceException(PersistenceException e) {
		if (e.getCause() instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e.getCause());
		} else {
			log.error("We may need to improve logging here!", e);
		}
	}
}
