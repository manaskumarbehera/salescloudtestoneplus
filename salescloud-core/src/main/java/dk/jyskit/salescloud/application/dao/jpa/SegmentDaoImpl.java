package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.SegmentDao;
import dk.jyskit.salescloud.application.model.Segment;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class SegmentDaoImpl extends GenericDaoImpl<Segment> implements SegmentDao {
    @Inject
	public SegmentDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Segment.class), emp);
	}
}
