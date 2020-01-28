package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;
import dk.jyskit.salescloud.application.model.Product;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCountAndInstallation implements Serializable {
	private Product product;
	private List<Product> alternatives;
	private Integer countNew;
	private Integer countExisting;
	private boolean hasInstallationProducts;
	private boolean installationSelected;
	private boolean installationEditable;

	public ProductCountAndInstallation(Product product, List<Product> alternatives, Integer countNew, Integer countExisting, boolean hasInstallationProducts, boolean installationSelected, boolean installationEditable) {
		this.product 					= product;
		this.alternatives 				= alternatives;
		this.countNew 					= countNew;
		this.countExisting				= countExisting;
		this.hasInstallationProducts	= hasInstallationProducts;
		this.installationSelected		= installationSelected;
		this.installationEditable		= installationEditable;
	}

	public ProductCountAndInstallation(Product product, List<Product> alternatives, Integer countNew, Integer countExisting) {
		this.product 					= product;
		this.alternatives 				= alternatives;
		this.countNew 					= countNew;
		this.countExisting				= countExisting;
		this.hasInstallationProducts	= false;
		this.installationSelected		= false;
		this.installationEditable		= false;
	}

	public ProductCountAndInstallation clone() {
		ProductCountAndInstallation copy = new ProductCountAndInstallation();
		copy.setProduct(product);
		if (alternatives != null) {
			copy.setAlternatives(Lists.newArrayList(alternatives));
		}
		copy.setCountNew(countNew);
		copy.setCountExisting(countExisting);
		copy.setHasInstallationProducts(hasInstallationProducts);
		copy.setInstallationSelected(installationSelected);
		copy.setInstallationEditable(installationEditable);
		return copy;
	}
}
