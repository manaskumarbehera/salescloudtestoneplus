package dk.jyskit.waf.application.dao.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.utils.filter.queries.QueryBuilder;

@SuppressWarnings("unchecked")
@Slf4j
public class GenericDaoImpl<T extends BaseEntity> implements Dao<T> {
    protected final Class<T> clazz;
	protected final Provider<EntityManager> emp;
	protected final CriteriaBuilder builder;

    @Inject
	public GenericDaoImpl(TypeLiteral<T> type, Provider<EntityManager> emp) {
        checkArgument(type != null, "type must not be null.");
        this.clazz = (Class<T>) type.getRawType();
        this.emp = emp;
        this.builder = emp.get().getCriteriaBuilder();
	}

    /**
     * {@inheritDoc}
     */
    public EntityManager em() {
        return emp.get();
    }

    /**
     * {@inheritDoc}
     */
	@Transactional
	public void delete(T object) {
		em().remove(object);
	}

    /**
     * {@inheritDoc}
     */
	@Transactional()
	public T findById(Long id) {
		if (id == null) {
			return null;
		}
		return em().getReference(clazz, id);
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
	public T save(T obj) {
		obj.updateDates();
		if (obj.getId() != null) {
			obj = em().merge(obj);
        } else {
            em().persist(obj);
        }
		return obj;
	}

	@Override
	public T saveAndFlush(T entity) {
		T result = save(entity);
		flush();
		return result;
	}

	@Override
	public void flush() {
		em().flush();
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
	public T reload(T entity) {
		if (entity == null) {
			return null;
		}
		return findById(entity.getId());
	}

    /**
     * {@inheritDoc}
     */
	public long getCount() {
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz);
		query.select(builder.count(root));
		return em().createQuery(query).getSingleResult();
	}

    /**
     * {@inheritDoc}
     */
	public long getCount(Filter ... filters) {
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz);
		query.select(builder.count(root));
		for (Filter filter : filters) {
			if (filter != null) {
				query.where(QueryBuilder.getPredicateForFilter(filter, root, builder));
			}
		}
		return em().createQuery(query).getSingleResult();
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
    public List<T> findAll() {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		query.from(clazz);
		TypedQuery<T> q = em().createQuery(query);
		return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public List<T> findPage(long startIndex, long count, SortParam<String> sort, Filter ... filters) {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> r = query.from(clazz);
		query.select(r);

		for (Filter filter : filters) {
			if (filter != null) {
				query.where(QueryBuilder.getPredicateForFilter(filter, r, builder));
			}
		}

		if (!StringUtils.isEmpty(sort.getProperty())) {
			if (sort.isAscending()) {
				query.orderBy(builder.asc(getPath(r, sort.getProperty())));
			} else {
				query.orderBy(builder.desc(getPath(r, sort.getProperty())));
			}
		}
		TypedQuery<T> q = em().createQuery(query);
		q.setMaxResults((int)count);
		q.setFirstResult((int)startIndex);
		return q.getResultList();
	}

	protected Path getPath(Root root, String attributeName) {
		String[] elements = Strings.split(attributeName, '.');
		Path path = root;
		for (String element : elements) {
			path = path.get(element);
		}
		return path;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByPredicates(Predicate ... predicates) {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		query = query.where(predicates);
        TypedQuery<T> q = em().createQuery(query);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findUniqueByPredicates(Predicate ... predicates) {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		query = query.where(predicates);
        TypedQuery<T> q = em().createQuery(query);
        return q.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public <Y> List<T> findByField(String field, Object value) {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.where(builder.equal(root.get(field), value));
		return em().createQuery(query).getResultList();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public T findUniqueByField(String field, Object value) {
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.where(builder.equal(root.get(field), value));
		try {
			return em().createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

//    /**
//     * {@inheritDoc}
//     */
//	@Override
//	public <Y> List<T> findByFields(Pair<String, Object> ... fieldAndValues) {
//		CriteriaQuery<T> query = builder.createQuery(clazz);
//		Root<T> root = query.from(clazz);
//		for (Pair<String, String> fieldAndValue : fieldAndValues) {
//			query.where(builder.equal(root.get(field), value));
//		}
//		return em().createQuery(query).getResultList();
//	}
//
//    /**
//     * {@inheritDoc}
//     */
//	@Override
//	public T findUniqueByFields(Pair<String, Object> ... fieldAndValue) {
//		CriteriaQuery<T> query = builder.createQuery(clazz);
//		Root<T> root = query.from(clazz);
//		query.where(builder.equal(root.get(fieldAndValue), value));
//		return em().createQuery(query).getSingleResult();
//	}

	/**
     * {@inheritDoc}
     */
	@Override
	public TypedQuery<T> getTypedQuery(String query) {
        return em().createQuery(query, clazz);
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public List<T> findByTypedQuery(TypedQuery<T> query) {
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public T findUniqueByTypedQuery(TypedQuery<T> query) {
        return query.getSingleResult();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Query getQuery(String query) {
        return em().createQuery(query);
	}

//	public void findByCriteria(CriteriaTuner tuner) {
//		CriteriaBuilder qb = em().getCriteriaBuilder();
//		CriteriaQuery<T> cq = qb.createQuery(clazz);
//		Root<T> p = cq.from(clazz);
//		Predicate predicate = tuner.tune(qb);
//		Predicate condition = qb.gt(p.get(Shop_.age), 20);
//		cq.where(condition);
//		TypedQuery<Person> q = em.createQuery(c);
//		List<Person> result = q.getResultList();
//	}

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
    	Query query = em().createNativeQuery(nativeQuery);
        return query.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
    	Query query = em().createNativeQuery(nativeQuery);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        Query query = em().createNativeQuery(nativeQuery);
        int idx = 1;
        for (Object param : parameters) {
            query.setParameter(String.valueOf(idx), param);
            idx++;
        }
        return query.executeUpdate();
    }

//    /**
//     * {@inheritDoc}
//     */
//    public int executeUpdate(final String namedQueryName, final Object... parameters) {
//        final Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
//        int idx = 1;
//        if (parameters != null) {
//            for (Object param : parameters) {
//                query.setParameter(String.valueOf(idx), param);
//                idx++;
//            }
//        }
//        return query.executeUpdate();
//    }

    /**
     * {@inheritDoc}
     */
    public void flushClear() {
    	em().flush();
    	em().clear();
    }
}
