package dk.jyskit.salescloud.application.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import dk.jyskit.waf.application.model.BaseEntity;

// #todo, #bad_smell, #technical_dept
// Denne klasse burde være ejet af BusinessArea, så admin brugeren kan indstille "rammerne" for rabatten.
// Vi mangler DiscountSchemeContract til tilegning af rabat til kontrakt, inkl. aktuelle data. Lige nu er det modelleret forkert.

/**
 * Discount schemes are more permanent in nature than campaigns. They are associated with 
 * business areas.
 *  
 * @author jan
 */
@Entity(name="DiscountScheme")
@DiscriminatorColumn(name="DISCOUNT_TYPE")
@Table(name = "discountscheme")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={"name"})
@NoArgsConstructor
public class DiscountScheme extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@NonNull @NotNull
	@Column(length=50)
	private String name;
	
	// --------------------------------
	
	/**
	 * Some discount schemes need to perform some calculations before being able to 
	 * calculate discount for individual products.
	 * 
	 * @param contract
	 */
	public void prepare(Contract contract) {
	}

	public void setContract(Contract contract) {
	}
	
	public Amounts calculateContractDiscountsForProduct(Product product, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		return null;
	}
	
	public Amounts calculateContractDiscountsForProductBundle(ProductBundle productBundle, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		return null;
	}
	
	// --------------------------------
	
	public DiscountScheme clone() {
		DiscountScheme discountScheme = new DiscountScheme();
		discountScheme.setName(name);
		return discountScheme;
	}
	
	protected long round(double value) {
		if (value < 0) {
			return (long) Math.floor(value - 0.5d);
		} else {
			return (long) Math.floor(value + 0.5d);
		}
	}
}
