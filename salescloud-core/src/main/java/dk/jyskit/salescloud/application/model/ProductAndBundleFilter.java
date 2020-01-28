package dk.jyskit.salescloud.application.model;

public interface ProductAndBundleFilter {
	default boolean acceptProduct(Product product) {
		return false;
	}

	default boolean acceptProductBundle(ProductBundle productBundle) {
		return false;
	}

	default boolean acceptOrderLine(OrderLine orderLine) {
		return false;
	}
}
