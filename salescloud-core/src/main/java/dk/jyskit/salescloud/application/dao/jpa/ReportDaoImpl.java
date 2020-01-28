package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.ReportDao;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ReportDaoImpl extends GenericDaoImpl<Report> implements ReportDao {
    @Inject
	public ReportDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Report.class), emp);
	}
}
