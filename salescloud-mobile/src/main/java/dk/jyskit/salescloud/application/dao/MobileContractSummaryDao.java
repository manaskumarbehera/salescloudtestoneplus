package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.MobileContractSummary;
import dk.jyskit.waf.application.dao.Dao;

public interface MobileContractSummaryDao extends Dao<MobileContractSummary> {
	void deleteByContractId(Long contractId);
}
