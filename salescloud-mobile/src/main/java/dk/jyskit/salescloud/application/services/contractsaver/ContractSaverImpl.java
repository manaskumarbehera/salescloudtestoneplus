package dk.jyskit.salescloud.application.services.contractsaver;

import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.MobileContractSummaryDao;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileContractSummary;
import dk.jyskit.waf.utils.guice.Lookup;

public class ContractSaverImpl implements ContractSaver {
	public void save(Contract contract) {
		MobileContract mobileContract = (MobileContract) contract;
		MobileContractDao mobileContractDao = Lookup.lookup(MobileContractDao.class);
		
		Long contractId = mobileContractDao.save(mobileContract).getId();
		
		MobileContractSummaryDao mobileContractSummaryDao = Lookup.lookup(MobileContractSummaryDao.class);
		if (contractId != null) {
			mobileContractSummaryDao.deleteByContractId(contractId);
		}
		mobileContractSummaryDao.save(MobileContractSummary.create(mobileContract));
	}
}
