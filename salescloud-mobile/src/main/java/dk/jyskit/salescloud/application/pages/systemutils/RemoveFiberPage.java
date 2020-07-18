package dk.jyskit.salescloud.application.pages.systemutils;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.MobileContractSummaryDao;
import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME,
		UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class RemoveFiberPage extends BasePage {
	@Inject
	private MobileContractDao mobileContractDao;
	@Inject
	private MobileContractSummaryDao mobileContractSummaryDao;
	@Inject
	private SystemUpdateDao systemUpdateDao;
	@Inject
	private BusinessAreaDao businessAreaDao;

	public RemoveFiberPage(PageParameters parameters) {
		super(parameters);

		{
			String name = "Remove Fiber 4"; // Don't change this name!
			
			SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.TDC_WORKS); 
			
			if (update == null) {
				log.info("Update starting: " + name);
				
				BusinessArea fiber = businessAreaDao.findUniqueByField("businessAreaId", BusinessAreas.FIBER);
				if (fiber != null) {
					try {
						log.info("Deleting...");
						for (MobileContract contract : mobileContractDao.findByField("businessArea", fiber)) {
							if (fiber.equals(contract.getBusinessArea())) {
								log.info("Deleting " + contract.getName());
								mobileContractDao.delete(contract);
							}
						}
						businessAreaDao.delete(fiber);
						log.info("Deleted");

						update = new SystemUpdate();
						update.setBusinessAreaId(BusinessAreas.TDC_WORKS);
						update.setName(name);
						systemUpdateDao.save(update);
					} catch (Exception e) {
						log.error("FAIL", e);
						handleInitializationException(e);
					}
				} else {
					log.info("Fiber not found");
				}

				log.info("Update done: " + name);
			}
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
