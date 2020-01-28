package dk.jyskit.salescloud.application.model;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.waf.application.model.BaseEntity;

/**
 *
 * @author jan
 */
@Entity(name="ContractCategory")
@Table(name = "contractcategory")
@Data
@EqualsAndHashCode(callSuper=true, of={"name"})
@RequiredArgsConstructor
@NoArgsConstructor
public class ContractCategory extends BaseEntity {
	@NonNull
	private String name;
	
	@Nonnull
	@ManyToOne(optional = false)
	private SalespersonRole salesperson;
	
	@Override
	public String toString() {
		return name;
	}
	
	// --------------------------------
	
}
