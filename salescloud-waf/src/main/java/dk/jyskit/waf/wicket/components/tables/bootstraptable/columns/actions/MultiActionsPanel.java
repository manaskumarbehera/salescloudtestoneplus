package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;

public class MultiActionsPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	@SafeVarargs
	public MultiActionsPanel(String id, final IModel<T> model, EntityAction<T> ... actions) {
		this(id, model, Arrays.asList(actions));
	}

	public MultiActionsPanel(String id, final IModel<T> model, List<EntityAction<T>> actionList) {
		super(id, model);
		ListView<EntityAction<T>> actionListView = new ListView<EntityAction<T>>("actions", actionList) {
			@Override
			protected void populateItem(ListItem<EntityAction<T>> item) {
				final EntityAction<T> action = item.getModelObject();
//				AbstractLink link = new PermissionedAjaxLink<T>("link", model, Operation.Update) {
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					public void onClick(AjaxRequestTarget target) {
//						action.onClick(model, target);
//					}
//				};
				AjaxLink<T> link = new AjaxLink<T>("link") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						action.onClick(model, target);
					}
				};
				link.add(new Label("text", new ResourceModel(action.getTextKey(model))));
				Icon icon;
				if (action.getIconType(model) == null) {
					icon = new Icon(FontAwesomeIconType.edit);
					icon.setVisible(false);
				} else {
					icon = new Icon(action.getIconType(model));
				}
				link.add(icon);

//				link.add(new Image("image", action.getImageResource()));
				if (!action.getAuthorizedRoles().isEmpty()) {
					MetaDataRoleAuthorizationStrategy.authorize(link, RENDER, action.getAuthorizedRoles().toString());
				}
				link.setVisible(action.isEnabled(model));
				link.add(AttributeModifier.replace("title", new ResourceModel(action.getTooltipKey(model))));
				item.add(link);
			}
		};
		actionListView.setReuseItems(true);
		add(actionListView);
	}
}
