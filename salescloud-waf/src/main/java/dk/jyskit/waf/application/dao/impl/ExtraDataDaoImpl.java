package dk.jyskit.waf.application.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.waf.application.dao.ExtraDataDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraData;

@Slf4j
public class ExtraDataDaoImpl extends GenericDaoImpl<ExtraData> implements ExtraDataDao {

    @Inject
	public ExtraDataDaoImpl(Provider<EntityManager> emp) {
		super(TypeLiteral.get(ExtraData.class), emp);
	}

	@Override
	public Map<String,ExtraData> findForEntity(BaseEntity entity) {
		if (entity == null) {
			return new TreeMap<String,ExtraData>();
		}
		TypedQuery<ExtraData> q = em().createQuery(
				"SELECT ed FROM ExtraData ed WHERE ed.entityId = :entityId AND ed.entityType = :entityType ",
						ExtraData.class);
		q.setParameter("entityType", entity.getClass().getName());
		q.setParameter("entityId", entity.getId());
		List<ExtraData> resultList = q.getResultList();
		Map<String,ExtraData> result = new TreeMap<>();
		for (ExtraData extraData : resultList) {
			result.put(extraData.getName(), extraData);
		}
		return result;
	}

	@Override
	public ExtraData findForEntity(BaseEntity entity, String name) {
		if (entity == null || name == null) {
			return null;
		}
		TypedQuery<ExtraData> q = em().createQuery(
				"SELECT ed FROM ExtraData ed WHERE ed.entityId = :entityId AND ed.entityType = :entityType AND ed.name = :name ",
						ExtraData.class);
		q.setParameter("entityType", entity.getClass().getName());
		q.setParameter("entityId", entity.getId());
		q.setParameter("name", name);
		try {
			ExtraData result = q.getSingleResult();
			return result;
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
				
				for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
					log.error("-----------------------------------------------------------------------------------");
					log.error(constraintViolation.getMessage());
					log.error("I'm guessing this is the problem: \n"
							+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
							+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
							+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
					log.error("-----------------------------------------------------------------------------------");
				}
			} else {
				log.error("Other problem", e);
			}
			return null;
		}
	}

}
