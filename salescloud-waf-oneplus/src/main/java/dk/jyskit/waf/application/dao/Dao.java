package dk.jyskit.waf.application.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.filter.Filter;

/**
 * @author Jan
 */
public interface Dao<T extends BaseEntity> {
	public void delete(T entity);

    /**
     * Find entity by Id.
     *
     * @param id   primary key
     * @return instance of T or null if not found
     */
	T findById(Long id);

	T save(T entity);

	/**
	 * @see #save(BaseEntity)
	 * @see #flush()
	 * @param entity
	 * @return
	 */
	T saveAndFlush(T entity);

	/**
	 * {@link EntityManager#flush()}
	 */
	void flush();

	T reload(T entity);

	long getCount();

	long getCount(Filter ... filter);

	List<T> findAll();

	List<T> findPage(long startIndex, long count, SortParam<String> sort, Filter ... filter);

    List<T> findByPredicates(Predicate ... predicates);

    T findUniqueByPredicates(Predicate ... predicates);

	<Y> List<T> findByField(String field, Object value);

	T findUniqueByField(String field, Object value);

    /**
     * Get query object for typed query.
     *
     * @param query JPQL query string
     * @return query object
     */
	TypedQuery<T> getTypedQuery(String query);

    List<T> findByTypedQuery(TypedQuery<T> query);

    T findUniqueByTypedQuery(TypedQuery<T> query);

    /**
     * Get query object for update queries or untyped select queries.
     *
     * @param query JPQL query string
     * @return query object
     */
	Query getQuery(String query);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery);

    /**
     * Execute native sql.
     *
     * @param nativeQuery native sql
     * @return result of select.
     */
    List executeNativeQuery(String nativeQuery);

    /**
     * Flush clear.
     */
    void flushClear();

    EntityManager em();

}
