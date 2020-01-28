package dk.jyskit.salescloud.application.pages.productselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.value.ValueMap;

import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.editors.productcountorall.ProductCountOrAllEditorPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.NoLocalizationLabelStrategy;

public class FormPanel extends Panel {
	public FormPanel(String id, ProductGroup productGroup, ValueMap valueMap) {
		super(id);
		
		Jsr303Form<ValueMap> form = new Jsr303Form<>("form", valueMap, true);
		add(form);
		
		form.setLabelSpans(SmallSpanType.SPAN8);
		form.setEditorSpans(SmallSpanType.SPAN4);
		
		form.setLabelStrategy(new NoLocalizationLabelStrategy());

		List<Product> allProducts = productGroup.getProducts();
		List<Product> products = new ArrayList<>();
		for (Product product : allProducts) {
			if (!((MobileProduct) product).isExcludeFromConfigurator()) {
				products.add(product);
			}
		}
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return Long.valueOf(o1.getSortIndex()).compareTo(Long.valueOf(o2.getSortIndex()));
			}
		});
		for(Product product: products) {
			form.addCustomComponent(new ProductCountOrAllEditorPanel(form, product.getPublicName()));
		}
	}
}
