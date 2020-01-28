package dk.jyskit.salescloud.application.pages.contractsummary;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.BusinessAreas;

public class ButtonsDisabledPanel extends Panel {
	public ButtonsDisabledPanel(String id) {
		super(id);
		
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		List<String> messages;
		if (MobileSession.get().getBusinessArea().isOnePlus()) {
			messages = Arrays.asList("Er der ændringer i kaldsflow?");
			// messages = Arrays.asList("Startdato kontrakt (One+)", "Startdato kontrakt (Netværk)");
		} else {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				messages = Arrays.asList("Startdato TDC Erhverv Rabataftale");
			} else {
				messages = Arrays.asList("Mobil kontraktsum", "Seneste ikrafttrædelsesdato");
			}
		}
		add(new ListView<String>("missingItems", messages) {
			@Override
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("item", item.getModelObject()));
			}
		});
	}
}
