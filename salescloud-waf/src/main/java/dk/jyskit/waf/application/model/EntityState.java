package dk.jyskit.waf.application.model;

import java.io.Serializable;

import javax.persistence.Column;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import dk.jyskit.waf.wicket.utils.WicketUtils;

@lombok.Value
public class EntityState implements Serializable {
	@Column(name = "entity_state")
	private final int entityState;

	public static final int ACTIVE_VAL 		= 0;
	public static final int INACTIVE_VAL 	= 1;
	public static final int SOFTDELETE_VAL 	= 2;
	public static final int ALL_VAL 		= 99;

	public static final EntityState ACTIVE 		= new EntityState(ACTIVE_VAL);
	public static final EntityState INACTIVE 	= new EntityState(INACTIVE_VAL);
	public static final EntityState SOFTDELETE 	= new EntityState(SOFTDELETE_VAL);

	public boolean isActive() {
		return entityState == ACTIVE_VAL;
	}

	public static EntityState of(int entityState) {
		switch (entityState) {
		case ACTIVE_VAL:
			return ACTIVE;
		case INACTIVE_VAL:
			return INACTIVE;
		case SOFTDELETE_VAL:
			return SOFTDELETE;
		default:
			return new EntityState(entityState);
		}
	}

	public EntityState() {
		entityState = ACTIVE_VAL;
	}

	public EntityState(int stateVal) {
		entityState = stateVal;
	}
	
	public IModel<String> getDisplayModel() {
		switch (entityState) {
			case EntityState.ACTIVE_VAL: 		return new ResourceModel("EntityState.ACTIVE");
			case EntityState.INACTIVE_VAL: 		return new ResourceModel("EntityState.INACTIVE");
			case EntityState.SOFTDELETE_VAL: 	return new ResourceModel("EntityState.SOFTDELETE");
			case EntityState.ALL_VAL: 			return new ResourceModel("EntityState.ALL");
		}
		return null;
	}
	
	@Override
	public String toString() {
		switch (entityState) {
			case EntityState.ACTIVE_VAL: 		return WicketUtils.getLocalized("EntityState.ACTIVE", "please localize: EntityState.ACTIVE");
			case EntityState.INACTIVE_VAL: 		return WicketUtils.getLocalized("EntityState.INACTIVE", "please localize: EntityState.INACTIVE");
			case EntityState.SOFTDELETE_VAL: 	return WicketUtils.getLocalized("EntityState.SOFTDELETE", "please localize: EntityState.SOFTDELETE");
			case EntityState.ALL_VAL: 			return WicketUtils.getLocalized("EntityState.ALL", "please localize: EntityState.ALL");
		}
		return null;
	}
}
