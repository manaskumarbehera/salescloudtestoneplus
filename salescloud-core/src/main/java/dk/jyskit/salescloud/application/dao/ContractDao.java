package dk.jyskit.salescloud.application.dao;

import java.util.Date;
import java.util.List;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.waf.application.dao.Dao;

public interface ContractDao extends Dao<Contract> {
	List<Contract> findByBusinessArea(BusinessArea businessArea);
	List<Contract> findNewerThan(Date date);
	List<Contract> findOlderThan(Date date);
}
