package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;

import java.io.Serializable;

import lombok.Value;

import org.apache.wicket.Component;

@Value
public class MatcherWithReportSpec implements Serializable {
	private CauseMatcher matcher;
	private ReportSpec reportSpec;
	
	public boolean matchAndReport(Throwable cause, Component defaulReportOn) {
		if (matcher.matches(cause)) {
			reportSpec.report(defaulReportOn);
			return true;
		} else {
			return false;
		}
	}
}
