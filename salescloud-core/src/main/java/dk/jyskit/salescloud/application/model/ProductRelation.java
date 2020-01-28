package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.salescloud.application.extensionpoints.ProductRelationTypeProvider;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.guice.Lookup;

@Entity(name="ProductRelation")
@Table(name = "productrelation")
@Data
@EqualsAndHashCode(callSuper=true)
@RequiredArgsConstructor
@NoArgsConstructor
public class ProductRelation extends BaseEntity {
	@NonNull @NotNull
	private Long relationTypeId;
	
	@ManyToOne(optional = false)
	private BusinessArea businessArea;
	
	private String displayName;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "productRelation_id", referencedColumnName = "id")
	private List<Product> products = new ArrayList<>();
	
	// --------------------------------
	
	public void addProduct(Product product) {
		products.add(product);
	}
	
	// --------------------------------
	
	@Transient
	public String getProductNames() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			if (i > 0) {
				if ((i == 1) && Lookup.lookup(ProductRelationTypeProvider.class).getById(relationTypeId).isFirstProductIsSpecial()) {
					sb.append(" => ");
				} else {
					sb.append(", ");
				}
			}
			sb.append(product.getPublicName() + " (" + product.getProductId() + ")");
		}
		return sb.toString();
	}
}
