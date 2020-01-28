package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.MobileCampaignDao;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class MobileCampaignDaoImpl extends GenericDaoImpl<MobileCampaign> implements MobileCampaignDao {
    @Inject
	public MobileCampaignDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(MobileCampaign.class), emp);
	}
}
