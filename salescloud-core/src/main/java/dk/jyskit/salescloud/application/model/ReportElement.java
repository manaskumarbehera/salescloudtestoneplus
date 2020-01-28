package dk.jyskit.salescloud.application.model;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="ReportElement")
@Table(name = "reportelement")
@Data
@EqualsAndHashCode(callSuper=true, of={"name"})
@RequiredArgsConstructor
@NoArgsConstructor
public class ReportElement extends BaseEntity {
	@NonNull
	@Column(length=100)
	private String name;
	
	@Column(length=20000)
	private String value;
	
	@Nonnull
	@ManyToOne(optional = false)
	private Report report;
	
	// --------------------------------
	
	@Override
	public String toString() {
		return name;
	}
}
