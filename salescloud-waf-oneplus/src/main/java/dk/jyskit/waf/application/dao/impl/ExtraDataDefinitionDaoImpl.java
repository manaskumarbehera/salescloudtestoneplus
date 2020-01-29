package dk.jyskit.waf.application.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.waf.application.dao.ExtraDataDefinitionDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraData;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;

public class ExtraDataDefinitionDaoImpl extends GenericDaoImpl<ExtraDataDefinition> implements ExtraDataDefinitionDao {

	@Inject
	public ExtraDataDefinitionDaoImpl(Provider<EntityManager> emp) {
		super(TypeLiteral.get(ExtraDataDefinition.class), emp);
	}

	@Override
	public Map<String, ExtraDataDefinition> findForEntityClass(Class<? extends BaseEntity> entityClazz) {
		Map<String, ExtraDataDefinition> result = new TreeMap<>();
		Class<?> clazz = entityClazz;
		while (BaseEntity.class.isAssignableFrom(clazz)) {
			String ql = "SELECT ed FROM ExtraDataDefinition ed WHERE ed.entityType = :entityType ";
			TypedQuery<ExtraDataDefinition> q = em().createQuery(ql, ExtraDataDefinition.class);
			q.setParameter("entityType", clazz.getName());
			List<ExtraDataDefinition> resultList = q.getResultList();
			if (resultList != null) {
				for (ExtraDataDefinition extraData : resultList) {
					if (!result.containsKey(extraData.getName())) {
						result.put(extraData.getName(), extraData);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	@Override
	public ExtraDataDefinition findForEntityClass(Class<? extends BaseEntity> entityClazz, String name) {
		ExtraDataDefinition result = null;
		Class<?> clazz = entityClazz;
		while (BaseEntity.class.isAssignableFrom(clazz)) {
			String ql = "SELECT ed FROM ExtraDataDefinition ed WHERE ed.entityType = :entityType AND ed.name = :name";
			TypedQuery<ExtraDataDefinition> q = em().createQuery(ql, ExtraDataDefinition.class);
			q.setParameter("entityType", clazz.getName());
			q.setParameter("name", name);
			try {
				result = q.getSingleResult();
				return result;
			} catch (NoResultException e) {
				// ignore we should have zero or one;
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

}
