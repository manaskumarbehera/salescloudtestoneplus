package dk.jyskit.waf.wicket.layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import dk.jyskit.waf.application.JITWicketApplication;
import dk.jyskit.waf.wicket.components.panels.WrapperPanel;

/**
 * Used to make row
 *
 * @author palfred
 *
 */
public class BootstrapRow extends Panel {
	public enum Width implements ICssClassNameProvider {
        COL_1,
        COL_2,
        COL_3,
        COL_4,
        COL_5,
        COL_6,
        COL_7,
        COL_8,
        COL_9,
        COL_10,
        COL_11,
        COL_12,
        AUTO() {
        	@Override
        	public String cssClassName() {
        		return "container";
        	}
        }
        ;

        private int colspan;

        private Width() {
			this.colspan = ordinal() + 1;
        }

        private Width(int colspan) {
			this.colspan = colspan;
        }

        @Override
        public String cssClassName() {
        	if (isBootstrap3()) {
        		return "col-sm-" + colspan;
			}
            return "span" + colspan;
        }

    }

	public static boolean isBootstrap3() {
		return JITWicketApplication.get().isBootstrapVersionNewerThan("3");
	}

	protected List<IModel<WrapperPanel>> columns = new ArrayList<IModel<WrapperPanel>>();

	public BootstrapRow(String wicketId) {
		super(wicketId);
		WebMarkupContainer row = new WebMarkupContainer("row");
		row.add(new CssClassNameAppender(isBootstrap3() ? "row" : "row"));
		add(row);
		row.add(createColumnsView());
	}

	public BootstrapRow addColumn(Component component, ICssClassNameProvider widthClass) {
		component.add(new CssClassNameAppender(widthClass));
		columns.add(Model.of((WrapperPanel)new WrapperPanel("column", component) {}));
		return this;
	}

	protected RefreshingView<WrapperPanel> createColumnsView() {
		RefreshingView<WrapperPanel> rowColView = new RefreshingView<WrapperPanel>("columns") {
			@Override
			protected Iterator<IModel<WrapperPanel>> getItemModels() {
				return columns.iterator();
			}

			@Override
			protected void populateItem(Item<WrapperPanel> item) {
				WrapperPanel panel = item.getModelObject();
				item.add(panel);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (columns.size() == 0) {
					setVisible(false);
				}
			}
		};
		rowColView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return rowColView;
	}

}
