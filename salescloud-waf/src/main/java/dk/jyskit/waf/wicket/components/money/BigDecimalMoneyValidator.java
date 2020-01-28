package dk.jyskit.waf.wicket.components.money;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.Session;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class BigDecimalMoneyValidator extends Behavior implements IValidator<String>{

	@Override
	public void validate(IValidatable<String> validatable) {
		try {
			// First check that a normal conversion works
			@SuppressWarnings("unused")
			BigDecimal bigDecimal = BigDecimalParser.parseBigDecimal(validatable.getValue(), getSessionLocale());

			// Now check if the only non digit character is the decimal sign
			// corresponding to the locale and that it appears only once.
			
			char decimalSeperator = new DecimalFormatSymbols(getSessionLocale()).getDecimalSeparator();

			Pattern p = Pattern.compile("^\\d+(\\" + decimalSeperator + "\\d{1,2})?$");
			Matcher m = p.matcher(validatable.getValue());
			if (!m.find()) {
				validatable.error(new ValidationError().addKey("BigDecimalMoneyValidator.decimals"));
			}
		} catch (Exception e) {
			validatable.error(new ValidationError().addKey("BigDecimalMoneyValidator.nonumber"));
		}
	}

	private Locale getSessionLocale() {
		return Session.get().getLocale();
	}
}
