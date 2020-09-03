package dk.jyskit.salescloud.application.dao;

import java.util.Date;
import java.util.List;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface ContractDao extends Dao<Contract> {
	List<Contract> findByBusinessArea(BusinessArea businessArea);
	List<Contract> findNewerThan(Date date);
	List<Contract> findOlderThan(Date date);
	List<Contract> findByYearMonth(int year, int month);  // fx 2020, 12
}
