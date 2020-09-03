package dk.jyskit.waf.application.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import dk.jyskit.waf.utils.guice.Lookup;

/**
 * Helper to ease access to current {@link EntityManager} and {@link EntityTransaction}
 * @author Palfred
 *
 */
public class DaoHelper {
	public static EntityManager getEntityManager() {
		return Lookup.lookup(EntityManager.class);
	}


	public static EntityTransaction getTransaction() {
		return getEntityManager().getTransaction();
	}

	public static void detach(Object entity) {
		getEntityManager().detach(entity);
	}

	public static void flush() {
		getEntityManager().flush();
	}

	public static void persist(Object entity) {
		getEntityManager().persist(entity);
	}


	public static <T> T merge(T entity) {
		return getEntityManager().merge(entity);
	}


	public static boolean contains(Object entity) {
		return getEntityManager().contains(entity);
	}


	public static void remove(Object entity) {
		getEntityManager().remove(entity);
	}


	public static void refresh(Object entity) {
		getEntityManager().refresh(entity);
	}

	public static void txBegin() {
		getTransaction().begin();
	}

	public static void txCommit() {
		getTransaction().commit();
	}

	public static boolean getRollbackOnly() {
		return getTransaction().getRollbackOnly();
	}

	public static boolean isActive() {
		return getTransaction().isActive();
	}

	public static void rollback() {
		getTransaction().rollback();
	}

	public static void setRollbackOnly() {
		getTransaction().setRollbackOnly();
	}


	public static <T> List<T> findByField(Class<T> clazz, String field, Object value) {
		EntityManager em = getEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.where(builder.equal(root.get(field), value));
		return em.createQuery(query).getResultList();
	}


}
