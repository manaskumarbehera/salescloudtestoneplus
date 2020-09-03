package dk.jyskit.waf.wicket.layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import dk.jyskit.waf.wicket.components.panels.WrapperPanel;

/**
 * Used to make row, with no particular width on the "columns".
 * TODO use  css flex box, and add support for more "FlowTypes", Flex needs ability control "grow" and the alike on elements.
 * @author palfred
 *
 */
public class FlowBox extends Panel {

	public static enum FlowType {
        Row ("row", ""),
        Horizontal ("row-box", "row-element"),
        Vertical ("", "container col-sm-12"),
        /**
         * Experimental does seem to work in firefox
         */
        FlexVertical ("flexbox flex-vert", "flex-elm"),
        FlexHorizontal ("flexbox flex-horz", "flex-elm");
        private final String[] boxCssClass;
        private final String[] elementCssClass;

        private FlowType(String boxCssClass, String elementCssClass) {
			this.boxCssClass = boxCssClass.split(" ");
			this.elementCssClass = elementCssClass.split(" ");
		}

		public String[] boxCssClass() {
        	return boxCssClass;
        }
        public String[] elementCssClass() {
        	return elementCssClass;
        }
    }

	protected List<IModel<WrapperPanel>> childWrappers = new ArrayList<IModel<WrapperPanel>>();
	private FlowType flow;

	public FlowBox(String wicketId) {
		this(wicketId, FlowType.Horizontal);
	}

	public FlowBox(String wicketId, FlowType flowType) {
		super(wicketId);
		this.flow = flowType;
		WebMarkupContainer row = new WebMarkupContainer("box");
		row.add(new CssClassNameAppender(getFlow().boxCssClass()));
		super.add(row);
		row.add(createColumnsView());
	}

	@Override
	public FlowBox add(Component... childs) {
		for (Component component : childs) {
			addElement(component);
		}
		return this;
	}

	@Override
	public MarkupContainer addOrReplace(Component... childs) {
		for (Component component : childs) {
			addOrReplaceElement(component);
		}
		return this;
	}

	public FlowBox addOrReplaceElement(Component component) {
		for (IModel<WrapperPanel> wrapperPanelModel : childWrappers) {
			WrapperPanel wrapperPanel = wrapperPanelModel.getObject();
			String wrappedId = wrapperPanel.getWrapped().getId();
			if (component.getId().equals(wrappedId)) {
				wrapperPanel.setWrapped(component);
				return this;
			}
		}
		addElement(component);
		return this;
	}


	public FlowBox addElement(Component component) {
		WrapperPanel wrapperPanel = new WrapperPanel("element", component) {};
		wrapperPanel.add(new CssClassNameAppender(getFlow().elementCssClass()));
		childWrappers.add(Model.of((WrapperPanel)wrapperPanel));
		return this;
	}

	protected RefreshingView<WrapperPanel> createColumnsView() {
		RefreshingView<WrapperPanel> rowColView = new RefreshingView<WrapperPanel>("elements") {
			@Override
			protected Iterator<IModel<WrapperPanel>> getItemModels() {
				return childWrappers.iterator();
			}

			@Override
			protected void populateItem(Item<WrapperPanel> item) {
				WrapperPanel panel = item.getModelObject();
				item.add(panel);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (childWrappers.size() == 0) {
					setVisible(false);
				}
			}
		};
		rowColView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return rowColView;
	}

	public FlowType getFlow() {
		return flow;
	}

	public void setFlow(FlowType flowType) {
		this.flow = flowType;
	}

	public FlowBox withFlow(FlowType flowType) {
		setFlow(flowType);
		return this;
	}
}
