package dk.jyskit.waf.application.model;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;
import com.google.inject.Provider;

import dk.jyskit.waf.application.utils.exceptions.EntityNotFoundException;

public class EntityModel<T extends BaseEntity> implements IModel<T> {
	@Inject
	Provider<EntityManager> em;

	private Class<T> clazz;
	private Serializable id;
	private T entity;

	public static <T extends BaseEntity> EntityModel<T> forEntity(T entity) {
		return new EntityModel<T>(entity);
	}

	public static <T extends BaseEntity> EntityModel<T> forClassAndId(Class<T> clazz, Serializable id) {
		return new EntityModel<T>(clazz, id);
	}

	private EntityModel() {
		Injector.get().inject(this);
	}

	@SuppressWarnings("unchecked")
	public EntityModel(T entity) {
		this();
		clazz = (Class<T>) entity.getClass();
		id = entity.getId();
		this.entity = entity;
	}

	public EntityModel(Class<T> clazz, Serializable id) {
		this();
		this.clazz = clazz;
		this.id = id;
	}

	public T getObject() {
		if ((entity == null) && (id != null)) {
			entity = load(clazz, id);
			if (entity == null) {
				throw new EntityNotFoundException(clazz, id);
			}
		}
		return entity;
	}

	public void detach() {
		if (entity != null && entity.getId() != null) {
			id = entity.getId();
			entity = null;
		}
	}

	private T load(Class<T> clazz, Serializable id) {
		return (T) em.get().find(clazz, id);
	}

	public void setObject(T object) {
		throw new UnsupportedOperationException(getClass() + " does not support #setObject(T entity)");
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((EntityModel) obj).getObject().getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
