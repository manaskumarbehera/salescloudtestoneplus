package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.wicket.Session;

public class ValidatorSingleton {
    private static Map<Locale, Validator> validatorMap = new HashMap<Locale, Validator>();

    public static Validator getInstance() {
	    Locale locale = Session.get().getLocale();
	    Validator validator = validatorMap.get(locale);
    	if (validator == null) {
//    		validator = Validation.buildDefaultValidatorFactory().getValidator();
    		
    		Locale l = locale;
    		MessageInterpolator messageInterpolator = Validation.buildDefaultValidatorFactory().getMessageInterpolator();
    	    MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(messageInterpolator, locale);

    	    validator = Validation.buildDefaultValidatorFactory().usingContext()
    	                                          .messageInterpolator(interpolator)
    	                                          .getValidator();
    	    validatorMap.put(locale, validator);
    	}
    	return validator;
    }
    
    /**
     * delegates to a MessageInterpolator implementation but enforce a given Locale
     */
    private static class LocaleSpecificMessageInterpolator implements MessageInterpolator {
        private final MessageInterpolator defaultInterpolator;
        private final Locale defaultLocale;

        public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
            this.defaultLocale = locale;
            this.defaultInterpolator = interpolator;
        }

        /**
         * enforce the locale passed to the interpolator
         */
        public String interpolate(String message, Context context) {
            return defaultInterpolator.interpolate(message, context, this.defaultLocale);
        }

        // no real use, implemented for completeness
        public String interpolate(String message, Context context, Locale locale) {
            return defaultInterpolator.interpolate(message, context, locale);
        }
    }
}
