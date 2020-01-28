package dk.jyskit.salescloud.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@SuppressWarnings("serial")
@Entity(name="Segment")
@Table(name = "t_segment")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Segment extends BaseEntity {
	@Column(length=30)
	@NotNull @NonNull
	private String name;
	
	private int csvIndex;
	
	// --------------------------------

	public Segment(String name, int csvIndex) {
		this.name 	= name;
		this.csvIndex 	= csvIndex;
	}
	
	// --------------------------------
	
	@Override
	public String toString() {
		return name;
	}
}
