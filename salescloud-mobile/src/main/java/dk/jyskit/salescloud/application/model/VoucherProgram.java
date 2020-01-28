package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="Subscription")
@Table(name = "subscription")
@NoArgsConstructor
@ToString(of={"name"})
@Data
public class VoucherProgram extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bundle_id")
	private MobileProductBundle bundle;

	@ManyToOne(optional = false)
	private MobileContract contract;
	
	private NumberTransferType numberTransferType;
	private String mobileNumber;
	private String name;
	private String division;
	private String icc;
	private SimCardType simCardType;
	private SimCardType datadelingSimCardType;
	
	@ManyToMany
	@JoinTable(name = "subscription_product", joinColumns = { @JoinColumn(name = "subscription_id", referencedColumnName = "id") }, 
			inverseJoinColumns = { @JoinColumn(name = "product_id", referencedColumnName = "id") })
	private List<MobileProduct> products = new ArrayList<>();
	
	// --------------------------------
	
	public void addProduct(MobileProduct product) {
		if (!products.contains(product)) {
			products.add(product);
		}
	}
	
	// --------------------------------
	
	@Transient
	public boolean isDirty() {
		// Note: we have to ignore datadelingSimCardType here. 
		return (mobileNumber != null) || (name != null) || (icc != null) || (simCardType != null) || (products.size() > 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoucherProgram other = (VoucherProgram) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
}
