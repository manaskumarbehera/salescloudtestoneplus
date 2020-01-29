package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class ReportSpec implements Serializable {
	@Nonnull
	private final IModel<String> report;

	@Nullable
	private final Component reportOn;

	public ReportSpec(IModel<String> report, Component reportOn) {
		super();
		this.reportOn = reportOn;
		this.report = report;
	}

	public Component getReportOn() {
		return reportOn;
	}

	public IModel<String> getReport() {
		return report;
	}

	public void report(Component defaulReportOn) {
		String msg = getReport().getObject();
		if (getReportOn() != null) {
			getReportOn().error(msg);
		} else {
			defaulReportOn.error(msg);
		}
	}

}
