package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303FormExceptionHandler;

/**
 * Translates and reports and exception as an error on a component.
 * The DefaultExceptionHandler can be configured with specific matchers and report specifications. 
 * This handler tries to match each cause with the root cause first. 
 * For each of these causes this handler first checks the specific added matchers in the add-order.
 * If no matches is found then the {@link #standardExceptionHandling(Exception)} is used.   
 * @author palfred
 *
 */
@Slf4j
public class DefaultExceptionHandler implements Jsr303FormExceptionHandler {
	private final Component defaultReportOn;
	private List<MatcherWithReportSpec> matchAndReports = new ArrayList<>();

	/**
	 * Constructs with error messages going to given component (usually a form).
	 * @param defaultReportOn The default component to use for error messages. Also used to resolved "standard exceptions" 
	 * @see #standardExceptionHandling(Exception)
	 */
	public DefaultExceptionHandler(Component defaultReportOn) {
		super();
		this.defaultReportOn = defaultReportOn;
	}

	/**
	 * Adds a matcher with corresponding report message and potential component.
	 * @param matcherWithReportSpec
	 * @return
	 */
	public DefaultExceptionHandler add(MatcherWithReportSpec matcherWithReportSpec) {
		matchAndReports.add(matcherWithReportSpec);
		return this;
	}

	/**
	 * Adds a {@link MessageContainsMatcher} that can match the given contained text and report the message on reportOn or defaultReportOn. 
	 * @param contained
	 * @param message
	 * @param reportOn
	 * @return
	 */
	public DefaultExceptionHandler addMessageContains(String contained, IModel<String> message, Component reportOn) {
		matchAndReports.add(new MatcherWithReportSpec(new MessageContainsMatcher(contained), new ReportSpec(message, reportOn)));
		return this;
	}

	public DefaultExceptionHandler addMessageContains(String container, String message, Component reportOn) {
		return addMessageContains(container, Model.of(message), reportOn);
	}

	public DefaultExceptionHandler addMessageContains(String container, IModel<String> message) {
		return addMessageContains(container, message, null);
	}

	public DefaultExceptionHandler addMessageContains(String container, String message) {
		return addMessageContains(container, Model.of(message));
	}

	public DefaultExceptionHandler addTypeMatch(Class<? extends Throwable> type, IModel<String> message, Component reportOn) {
		matchAndReports.add(new MatcherWithReportSpec(new ExceptionTypeMatcher(type), new ReportSpec(message, reportOn)));
		return this;
	}

	public DefaultExceptionHandler addTypeMatch(Class<? extends Throwable> type, String message, Component reportOn) {
		return addTypeMatch(type, Model.of(message), reportOn);
	}

	public DefaultExceptionHandler addTypeMatch(Class<? extends Throwable> type, IModel<String> message) {
		return addTypeMatch(type, message, null);
	}

	public DefaultExceptionHandler addTypeMatch(Class<? extends Throwable> type, String message) {
		return addTypeMatch(type, Model.of(message));
	}

	@Override
	public void onException(Exception e) {
		log.info("Got exception: " + e.getMessage(), e);
		List<Throwable> causes = ExceptionUtils.getThrowableList(e);
		Collections.reverse(causes);
		for (Throwable cause : causes) {
			for (MatcherWithReportSpec mar : matchAndReports) {
				if (mar.matchAndReport(cause, defaultReportOn)) {
					return;
				}
			}
		}
		standardExceptionHandling(e);
	}

	/**
	 * Handles exceptions not handle by the explicit matchers.
	 * Investigates from the root cause to the given exception to find the first existing string resource of the form: 
	 * <pre><code>form303.general.exception.&lt;Simple Class Name&gt;</code></pre>
	 * <p>The string resource is resolved based on {@link #defaultReportOn}, and the found string is reported on that as an error.</p>
	 * <p>If no specific string resource is found the the string resource <code>form303.general.exception</code> is used for the message.</p>
	 * @param e The exception with causes to handle.
	 */
	protected void standardExceptionHandling(Exception e) {
		String dummyValue = "¤¤¤";
		String value = dummyValue;
		List<Throwable> causes = ExceptionUtils.getThrowableList(e);
		Collections.reverse(causes);

		// Look for exception types
		Iterator<Throwable> i = causes.iterator();
		while (dummyValue.equals(value) && i.hasNext()) {
			Throwable cause = i.next();
			value = defaultReportOn.getString("form303.general.exception." + cause.getClass().getSimpleName(), Model.of(cause), dummyValue);
		}
		if (dummyValue.equals(value)) {
			value = defaultReportOn.getString("form303.general.exception", Model.of(e));
		}
		defaultReportOn.error(value);
	}

}
