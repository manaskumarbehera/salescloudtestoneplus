package dk.jyskit.salescloud.application.model;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class OfficeStatusPanel extends Panel {
	public OfficeStatusPanel(String id, MobileContract contract) {
		super(id);
		{
			WebMarkupContainer statusSales = new WebMarkupContainer("statusSales");
			add(statusSales);
			String css = "led-red";
			if (contract.getStatus().getId() >= ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER.getId()) {
				css = "led-yellow";
			}
			if (contract.getStatus().getId() >= ContractStatusEnum.SENT_TO_IMPLEMENTATION.getId()) {
				css = "led-green";
			}
			statusSales.add(AttributeModifier.replace("class", css));
		}

		{
			WebMarkupContainer statusImplementation = new WebMarkupContainer("statusImplementation");
			add(statusImplementation);
			String css = "led-red";
			if (contract.getStatus().getId() >= ContractStatusEnum.SENT_TO_IMPLEMENTATION.getId()) {
				css = "led-yellow";
			}
			if (contract.getStatus().getId() >= ContractStatusEnum.IMPLEMENTED.getId()) {
				css = "led-green";
			}
			statusImplementation.add(AttributeModifier.replace("class", css));
		}
	}
}
