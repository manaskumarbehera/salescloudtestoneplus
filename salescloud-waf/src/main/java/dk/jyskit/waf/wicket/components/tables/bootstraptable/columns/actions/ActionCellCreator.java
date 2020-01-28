package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.Accessors;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.ICellCreator;

public class ActionCellCreator<R> implements ICellCreator<R> {

	@Accessors(fluent=true)
	private List<EntityAction<R>> actions = new ArrayList<>();


	public ActionCellCreator(List<EntityAction<R>> actions) {
		super();
		this.actions.addAll(actions);
	}

	public ActionCellCreator(EntityAction<R>... actions) {
		super();
		addActions(actions);
	}

	public void addActions(EntityAction<R>... actions) {
		for (EntityAction<R> action : actions) {
			this.actions.add(action);
		}
	}

	public void addActions(Iterable<EntityAction<R>> actions) {
		for (EntityAction<R> action : actions) {
			this.actions.add(action);
		}
	}


	@Override
	public Component newCell(String componentId, IModel<R> rowModel) {
		return new MultiActionsPanel<R>(componentId, rowModel, actions);
	}

}
