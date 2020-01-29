package dk.jyskit.waf.wicket.components.forms.jsr303form.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ValidatorSingleton;

/**
 * Inspired by wicket-jsr303-validators.
 * 
 * Validates constraints on a given bean property with a possible value.
 * 
 * This validator should be added on a <code>FormComponent</code>.
 * 
 * @see javax.validation.Validator#validateValue(Class, String, Object, Class...)
 *  
 * @author jan
 *
 * @param <T>
 * @param <Z>
 */
public class Jsr303PropertyValidator<T, Z> implements INullAcceptingValidator<T> {
 	protected String propertyName;
	protected Class<Z> beanType;
	private final Class<?>[] groups;
 
	public Jsr303PropertyValidator(Class<Z> clazz, String propertyName, Class<?>... groups) {
		this.propertyName = propertyName;
		this.beanType = clazz;
		this.groups = groups;
	}

	@SuppressWarnings("unchecked")
	public void validate(IValidatable<T> validatable) {
		Set<ConstraintViolation<Z>> res = ValidatorSingleton.getInstance().validateValue(this.beanType, this.propertyName, validatable.getValue(), groups);
		for (ConstraintViolation<Z> vio : res ) {
			validatable.error(new ValidationError().setMessage(vio.getMessage()));
		}
	}
}