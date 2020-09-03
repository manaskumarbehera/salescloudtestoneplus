package dk.jyskit.waf.utils;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtils {
	public static void handleException(Exception e) {
		if (e instanceof RollbackException) {
			if (((RollbackException) e).getCause() instanceof ConstraintViolationException) {
				handleConstraintViolationException((ConstraintViolationException) ((RollbackException) e).getCause());
			} else {
				log.error("This is a problem (1)", e);
			}
		} else if (e instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e);
		} else if (e instanceof PersistenceException) {
			handlePersistenceException((PersistenceException) e);
		} else {
			log.error("This is a problem (2)", e);
		}
	}

	private static void handleConstraintViolationException(ConstraintViolationException e) {
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			log.error(constraintViolation.getMessage());
			log.error("I'm guessing this is the problem: \n"
					+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
					+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
		}
	} 
	
	private static void handlePersistenceException(PersistenceException e) {
		if (e.getCause() instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e.getCause());
		} else {
			log.error("We may need to improve logging here!", e);
		}
	} 
}
