package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.ReportElementDao;
import dk.jyskit.salescloud.application.model.ReportElement;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ReportElementDaoImpl extends GenericDaoImpl<ReportElement> implements ReportElementDao {
    @Inject
	public ReportElementDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(ReportElement.class), emp);
	}
}
