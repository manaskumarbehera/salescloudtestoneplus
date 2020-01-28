package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import lombok.Data;

@Data
public class ProductPriceAndCountRow implements Serializable {
	private Product product;
	private ProductBundle bundle;
	private Integer price;
	private Integer count;
	
	public ProductPriceAndCountRow(Product product, Integer price, Integer count) {
		this.product = product;
		this.price = price;
		this.count = count;
	}

	public ProductPriceAndCountRow(ProductBundle bundle, Integer price, Integer count) {
		this.bundle = bundle;
		this.price = price;
		this.count = count;
	}
}
