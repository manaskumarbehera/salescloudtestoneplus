package dk.jyskit.salescloud.application.extensionpoints;

import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;

public interface CanOrderFilter {
	boolean accept(Product product);
	boolean accept(ProductBundle bundle);
}
