package dk.jyskit.salescloud.application.editors.productcountorall;

import java.io.Serializable;

import lombok.Data;
import dk.jyskit.salescloud.application.model.Product;

@Data
public class ProductCountOrAll implements Serializable {
	private Product product;
	private Integer countNew;
	private Integer countExisting;
	private boolean all;
	
	public ProductCountOrAll(Product product, Integer countExisting, Integer countNew) {
		this.product = product;
		this.countNew = countNew;
		this.countExisting = countExisting;
		if (countNew == null) {
			all = true;
		}
	}
}
