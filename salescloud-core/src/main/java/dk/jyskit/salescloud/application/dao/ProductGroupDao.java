package dk.jyskit.salescloud.application.dao;

import java.util.List;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.waf.application.dao.Dao;

public interface ProductGroupDao extends Dao<ProductGroup> {
    List<ProductGroup> findRootGroupsByBusinessArea(BusinessArea businessArea);
	List<ProductGroup> findByBusinessArea(BusinessArea businessArea);
    ProductGroup findByBusinessAreaAndUniqueName(BusinessArea businessArea, String uniqueName);
}
