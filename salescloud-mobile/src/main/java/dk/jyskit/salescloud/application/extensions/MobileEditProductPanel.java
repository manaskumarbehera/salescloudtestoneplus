package dk.jyskit.salescloud.application.extensions;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.PaymentFrequency;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.pages.admin.products.EditProductPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class MobileEditProductPanel<MobileProduct> extends EditProductPanel {
	
	public MobileEditProductPanel(CrudContext context, IModel<Product> childModel, final IModel<ProductGroup> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public void addFormFields(Jsr303Form form) {
		form.addTextField("publicName");
		form.addTextField("internalName");
		
		switch (((ProductGroup) parentModel.getObject()).getBusinessArea().getBusinessAreaId()) {
		case BusinessAreas.MOBILE_VOICE:
			form.addTextField("nabsCode"); // SOC
			form.addTextField("kvikCode"); 
			break;
		default:
			form.addTextField("productId"); 
			break;
		}
		
		form.addTextField("price.oneTimeFee");
		form.addTextField("price.installationFee");
		form.addTextField("price.recurringFee");
		form.addCheckBox("discountEligible");
		form.addCheckBox("ipsaDiscountEligible");
		form.addCheckBox("rabataftaleDiscountEligible");
		form.addCheckBox("gks");
		form.addCheckBox("excludeFromConfigurator");
		form.addCheckBox("excludeFromProductionOutput");
		form.addCheckBox("excludeFromOffer");
		form.addDropDownChoice("paymentFrequency", PaymentFrequency.valuesAsList());
		form.addTextField("defaultCount");
		form.addTextField("minCount");
		form.addTextField("maxCount");
		form.addTextField("flags");
		form.addTextField("filter");
		form.addTextField("filterID");

		FormGroup f = form.createGroup(Model.of("Partner"));
		f.addTextField("provisionOneTimeFee");
//		f.addTextField("businessValueOneTimeFee");
		f.addTextField("provisionInstallationFee");
//		f.addTextField("businessValueInstallationFee");
		f.addTextField("provisionRecurringFee");
//		f.addTextField("businessValueRecurringFee");
		f.addTextField("remarks");
		f.addCheckBox("variableInstallationFee");
		f.addCheckBox("variableRecurringFee");
		f.addCheckBox("variableCategory");
		f.addCheckBox("variableProductName");

		f.addCheckBox("poolModeBundle");
		f.addCheckBox("nonPoolModeBundle");
		f.addTextField("poolIndex");
		f.addTextField("poolContributions");
	}
}
