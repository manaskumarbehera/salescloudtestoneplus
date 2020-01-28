package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import java.util.HashMap;

import dk.jyskit.salescloud.application.MobileSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;

import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class ProductCountAndInstallationPanel extends ComponentWithLabelAndValidationPanel<FormComponent<Object>> {
	private SelectProductCountEditor selectProductCountEditor;

	public ProductCountAndInstallationPanel(ComponentContainerPanel container, String fieldName, ProductCountAndInstallation productRow) {
		super(container, fieldName);
		Product product = productRow.getProduct();
		if (productRow.getAlternatives() == null) {
			if (MobileSession.get().isBusinessAreaOnePlus()) {
				selectProductCountEditor = new SelectProductCountEditor("editor", propertyModel);
				init(selectProductCountEditor, new HashMap<String, String>());
			} else {
				if ((product.getMinCount() == 0) && (product.getMaxCount() == 1)) {
					init(new SelectProductAndTDCInstallationEditor("editor", propertyModel), new HashMap<String, String>());
				} else {
					init(new SelectProductCountAndTDCInstallationEditor("editor", propertyModel), new HashMap<String, String>());
				}
			}
		} else {
			init(new SelectSingleProductEditor("editor", propertyModel), new HashMap<String, String>());
		}
	}

	public void onMaxLimitsChanged(int maxNew, int maxExisting, AjaxRequestTarget target) {
		if (selectProductCountEditor != null) {
			selectProductCountEditor.onMaxLimitsChanged(maxNew, maxExisting, target);
		}
	}
}
