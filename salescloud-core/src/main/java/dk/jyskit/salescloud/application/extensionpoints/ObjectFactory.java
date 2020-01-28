package dk.jyskit.salescloud.application.extensionpoints;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.wicket.crud.CrudContext;

public interface ObjectFactory {
	Contract createContract();
	
	Contract createAndSaveContract(BusinessArea businessArea, SalespersonRole salesperson);
	
	Product createProduct();

	ProductGroup createProductGroup();
	
	ProductBundle createProductBundle();
	
	Panel createEditProductPanel(CrudContext context, IModel<Product> childModel, IModel<ProductGroup> parentModel);

	Campaign createCampaign();

	Provider<Workbook> getContractsSpreadsheet();

	Provider<Workbook> getProductsSpreadsheet();

	Provider<Workbook> getUsersSpreadsheet();
}
