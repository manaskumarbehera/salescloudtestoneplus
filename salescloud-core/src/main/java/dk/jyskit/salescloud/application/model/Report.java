package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="Report")
@Table(name = "report")
@Data
@EqualsAndHashCode(callSuper=true, of={"uniqueId"})
@RequiredArgsConstructor
@NoArgsConstructor
public class Report extends BaseEntity {
	@NonNull
	private String title;
	
	@NonNull
	@Column(unique=true, length=40)
	private String uniqueId;
	
	@Nonnull
	@ManyToOne(optional = false)
	private BusinessArea businessArea;
	
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReportElement> reportElements = new ArrayList<>();

	// --------------------------------
	
	public void addReportElement(ReportElement reportElement) {
		reportElements.add(reportElement);
		reportElement.setReport(this);
	}
	
	public void removeReportElement(ReportElement reportElement) {
		reportElements.remove(reportElement);
	}
	
	// --------------------------------
	
	@Override
	public String toString() {
		return title;
	}
}
