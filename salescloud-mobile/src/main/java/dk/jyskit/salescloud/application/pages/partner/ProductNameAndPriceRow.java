package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.Product;
import lombok.Data;

@Data
public class ProductNameAndPriceRow implements Serializable {
	private Product product;
	private String name;
	private Integer price;
	
	public ProductNameAndPriceRow(Product product, String name, Integer price) {
		this.product 	= product;
		this.name 		= name;
		this.price 		= price;
	}
}
