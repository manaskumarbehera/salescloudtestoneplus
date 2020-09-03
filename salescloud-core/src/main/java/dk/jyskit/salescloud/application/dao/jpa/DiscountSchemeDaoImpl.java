package dk.jyskit.salescloud.application.dao.jpa;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.DiscountSchemeDao;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.DiscountScheme;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;
import dk.jyskit.waf.application.model.EntityState;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class DiscountSchemeDaoImpl extends GenericDaoImpl<DiscountScheme> implements DiscountSchemeDao {
    @Inject
	public DiscountSchemeDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(DiscountScheme.class), emp);
	}
}
