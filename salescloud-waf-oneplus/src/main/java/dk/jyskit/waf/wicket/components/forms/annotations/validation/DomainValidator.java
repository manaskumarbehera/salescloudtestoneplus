package dk.jyskit.waf.wicket.components.forms.annotations.validation;

import java.util.regex.Matcher;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import dk.jyskit.waf.wicket.components.forms.annotations.Domain;

public class DomainValidator implements ConstraintValidator<Domain, String> {
	private java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(".*?([^.]+\\.[^.]+)", java.util.regex.Pattern.CASE_INSENSITIVE);

	public void initialize(Domain domain) {
	}

	public boolean isValid(String value, ConstraintValidatorContext context) {
		if ( value == null || value.length() == 0 ) {
			return true;
		}
		Matcher m = pattern.matcher( value );
		return m.matches();
	}
}