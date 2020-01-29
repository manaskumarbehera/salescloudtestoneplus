package dk.jyskit.waf.wicket.components.forms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Focus {
	String message() default "{dk.jyskit.indberetning.wicket.jsr303form.annotations.focus.Focus.message}";
}