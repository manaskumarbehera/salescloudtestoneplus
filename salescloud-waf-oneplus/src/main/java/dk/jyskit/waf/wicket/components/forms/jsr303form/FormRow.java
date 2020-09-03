package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.util.*;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.layout.SpanBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SpanType;

/**
 *
 * @author m43634
 *
 * @param <T>
 */
public class FormRow<T> extends Panel {

	private final ComponentContainerPanel<T> container;
	protected List<FormGroup<T>> columns = new ArrayList<FormGroup<T>>();
	protected Map<FormGroup<T>, SpanType> spanByGroup = new HashMap<>();

	public FormRow(String wicketId, ComponentContainerPanel<T> container) {
		super(wicketId);
		this.container = container;
		add(createColumnsView());
	}

	protected RefreshingView<Panel> createColumnsView() {
		RefreshingView<Panel> formItemsView = new RefreshingView<Panel>("columns") {
			@Override
			protected Iterator<IModel<Panel>> getItemModels() {
				List<Panel> items = new ArrayList<Panel>();
				items.addAll(columns);

				return new ModelIteratorAdapter<Panel>(items.iterator()) {
					@Override
					protected IModel<Panel> model(Panel object) {
						return new CompoundPropertyModel<Panel>(object);
					}
				};
			}

			@Override
			protected void populateItem(Item<Panel> item) {
				Panel panel = item.getModelObject();
				item.add(panel);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (columns.size() == 0) {
					setVisible(false);
				} else {
					List<FormGroup<T>> missingSpan = new ArrayList<>();
					for (FormGroup<T> col : columns) {
						if (!spanByGroup.containsKey(col)) {
							missingSpan.add(col);
						}
					}
					if (!missingSpan.isEmpty()) {
						int columnWidth = 0;
						Collection<SpanType> values = spanByGroup.values();
						for (SpanType spanType : values) {
							columnWidth += Integer.parseInt(Strings.afterLast(spanType.cssClassName(), '-'));
						}
						int toUse = 12 -(columnWidth % 12);
						int forEach = (toUse  / missingSpan.size());
						if (forEach == 0) {
							forEach = ((12 + toUse)  / missingSpan.size());
						}
						for (FormGroup<T> col : missingSpan) {
							col.add(new SpanBehavior(SmallSpanType.values()[forEach -1]));
						}
					}

				}
			}
		};
		formItemsView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return formItemsView;
	}

	public FormGroup<T> createNoLegendGroup(SpanType... spans) {
		return createGroup(Model.of(""), spans);
	}

	public FormGroup<T> createGroup(String groupNameKey, SpanType... spans) {
		return createGroup(container.getLabelStrategy().groupLabel(groupNameKey), spans);
	}

	public FormGroup<T> createGroup(IModel<String> titleModel, SpanType... spans) {
		boolean first = columns.isEmpty();
		FormGroup<T> group = new FormGroup(container.getForm(), container.getBeanModel(), titleModel, first,
				container.isAjaxValidate(), container.getLabelStrategy());
		for (SpanType spanType : spans) {
			spanByGroup.put(group, spanType);
			group.add(new SpanBehavior(spanType));
		}
		columns.add(group);
		return group;
	}

}
