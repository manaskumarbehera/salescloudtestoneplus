package dk.jyskit.salescloud.application.pages.contractsummary;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

import dk.jyskit.salescloud.application.MobileSession;

@Slf4j
public class FinancialSummaryPanel extends Panel {
	public FinancialSummaryPanel(String id) {
		super(id);
		
		setOutputMarkupId(true);
		
		LoadableDetachableModel<List<String[]>> model = new LoadableDetachableModel<List<String[]>>() {
			@Override
			protected List<String[]> load() {
				return MobileSession.get().getContract().getFinansialOverviewLines();
			}
		};
		
		add(new ListView<String[]>("listView", model) {
			public void populateItem(final ListItem<String[]> item) {
				final String[] data = item.getModelObject();
				if ("header".equals(data[0])) {
					item.add(new Label("header", data[1]));
					item.add(new Label("left", "").setVisible(false));
					item.add(new Label("right", "").setVisible(false));
				} else {
					item.add(new Label("header", "").setVisible(false));
					Label left = new Label("left", data[1]);
					item.add(left);
					Label right = new Label("right", data[2]);
					item.add(right);
					if ("total".equals(data[0])) {
						left.add(AttributeModifier.append("style", "font-weight:bold; "));
						right.add(AttributeModifier.append("style", "font-weight:bold; "));
					} else if ("space".equals(data[0])) {
						left.add(AttributeModifier.append("style", "height:20px; "));
					} else if ("discount".equals(data[0])) {
						left.add(AttributeModifier.append("style", "font-style: italic; "));
					}
				}
			}
		});
	}
}
