package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.MobileContractSummaryDao;
import dk.jyskit.salescloud.application.model.MobileContractSummary;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class MobileContractSummaryDaoImpl extends GenericDaoImpl<MobileContractSummary> implements MobileContractSummaryDao {
    @Inject
	public MobileContractSummaryDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(MobileContractSummary.class), emp);
	}
    
    public void deleteByContractId(Long contractId) {
    	for (MobileContractSummary mobileContractSummary : findByField("contractId", contractId)) {
			delete(mobileContractSummary);
		}
    }
}
