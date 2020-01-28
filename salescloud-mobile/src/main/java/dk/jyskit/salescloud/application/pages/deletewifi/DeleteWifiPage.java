package dk.jyskit.salescloud.application.pages.deletewifi;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME, UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class DeleteWifiPage extends BasePage {
	private static boolean running = false;
	
	@Inject
	private BusinessAreaDao businessAreaDao;

	@Inject
	private OrderLineDao orderLineDao;
	
	@Inject
	private ProductGroupDao productGroupDao;
	
	@Inject
	private ContractDao contractDao;
	
	public DeleteWifiPage(PageParameters parameters) {
		super(parameters);
		log.info("Deleting Wi-Fi");
		try {
			if (!running) {
				running = true;
				for (BusinessArea bu : businessAreaDao.findAll()) {
					if (bu.getBusinessAreaId() == BusinessAreas.WIFI) {
						log.info("Step 1");
						for (OrderLine ol : orderLineDao.findAll()) {
							if (ol.getContract().getBusinessArea().equals(bu)) {
								orderLineDao.delete(ol);
							}
						}
						System.gc();
						log.info("Step 2");
						for (Contract contract : contractDao.findByBusinessArea(bu)) {
							if (contract.getBusinessArea().equals(bu)) {
								contractDao.delete(contract);
								System.gc();
							}
						}
						System.gc();
						log.info("Step 3");
						for (ProductGroup productGroup : bu.getProductGroups()) {
							productGroupDao.delete(productGroup);
							System.gc();
						}
						System.gc();
						log.info("Step 4");
						businessAreaDao.delete(bu);
						log.info("Done");
//					businessAreaDao.flush();
//					log.info("Flushed");
						break;
					}
				}
				running = false;
			}
		} catch (Exception e) {
			log.error("Failed to delete Wi-Fi?", e);
		}
	}
}
