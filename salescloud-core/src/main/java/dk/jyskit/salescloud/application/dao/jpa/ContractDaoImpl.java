package dk.jyskit.salescloud.application.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ContractDaoImpl extends GenericDaoImpl<Contract> implements ContractDao {
    @Inject
	public ContractDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Contract.class), emp);
	}
    
    @Override
    public List<Contract> findByBusinessArea(BusinessArea businessArea) {
        TypedQuery<Contract> q = em()
        		.createQuery("select c from " + clazz.getSimpleName() + " c "
        				+ "where c.businessArea = :businessArea", clazz)
        		.setParameter("businessArea", businessArea);
        return q.getResultList();
    }
    
    @Override
    public List<Contract> findNewerThan(Date date) {
        TypedQuery<Contract> q = em()
        		.createQuery("select c from " + clazz.getSimpleName() + " c "
        				+ "where c.creationDate > :date", clazz)
        		.setParameter("date", date);
        return q.getResultList();
    }
    
    @Override
    public List<Contract> findOlderThan(Date date) {
    	TypedQuery<Contract> q = em()
    			.createQuery("select c from " + clazz.getSimpleName() + " c "
    					+ "where c.creationDate < :date", clazz)
    			.setParameter("date", date);
    	return q.getResultList();
    }

    @Override
    public List<Contract> findByYearMonth(int year, int month) {
    	TypedQuery<Contract> q = em()
    			.createQuery("select c from " + clazz.getSimpleName() + " c "
    					+ "where (SQL('EXTRACT(YEAR FROM ?)', c.creationDate) = :year) and (SQL('EXTRACT(MONTH FROM ?)', c.creationDate) = :month)", clazz)
				.setParameter("year", year)
				.setParameter("month", month);
    	return q.getResultList();
    }
}
