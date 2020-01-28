package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import lombok.Data;

@Data
public class ProductCategoryNamePriceAndCountRow implements Serializable {
	private Product product;
	private ProductBundle bundle;
	private String category;
	private String name;
	private Integer price;
	private Integer count;

	public ProductCategoryNamePriceAndCountRow(Product product, String category, String name, Integer price, Integer count) {
		this.product 	= product;
		this.category 	= category;
		this.name 		= name;
		this.price 		= price;
		this.count 		= count;
	}

	public ProductCategoryNamePriceAndCountRow(ProductBundle productBundle, String category, String name, Integer price, Integer count) {
		this.bundle 	= productBundle;
		this.category 	= category;
		this.name 		= name;
		this.price 		= price;
		this.count 		= count;
	}
}
