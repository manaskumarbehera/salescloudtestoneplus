package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.block.BadgeBehavior;

public class PoolColumnHeaderPanel extends Panel {

	public PoolColumnHeaderPanel(String id, IModel<String> displayModel, final MutableInt remaining) {
		super(id);
		
		setOutputMarkupId(true);
		
		add(new Label("title", displayModel));
		
		Label badge = new Label("badge", new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				return "" + remaining.intValue();
			}
		});
		badge.add(new BadgeBehavior());
		add(badge);
	}
}
