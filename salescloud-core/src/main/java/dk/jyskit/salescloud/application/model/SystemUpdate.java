package dk.jyskit.salescloud.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name="SystemUpdate")
@Table(name = "systemupdate")
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
public class SystemUpdate extends BaseEntity {
	@NonNull
	@Column(length=50)
	private String name;
	
	private int businessAreaId;
	
	// --------------------------------
	
	@Override
	public String toString() {
		return name;
	}
}
