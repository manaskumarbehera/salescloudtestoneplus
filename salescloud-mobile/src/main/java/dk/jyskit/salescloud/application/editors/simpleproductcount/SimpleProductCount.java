package dk.jyskit.salescloud.application.editors.simpleproductcount;

import dk.jyskit.salescloud.application.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class SimpleProductCount implements Serializable {
	private Product product;
	private Integer countNew;

	public SimpleProductCount clone() {
		return new SimpleProductCount(product, countNew);
	}
}
