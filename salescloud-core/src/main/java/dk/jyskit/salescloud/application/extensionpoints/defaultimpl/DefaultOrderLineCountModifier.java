package dk.jyskit.salescloud.application.extensionpoints.defaultimpl;

import dk.jyskit.salescloud.application.extensionpoints.OrderLineCountModifier;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.OrderLineCount;

public class DefaultOrderLineCountModifier implements OrderLineCountModifier {

	@Override
	public OrderLineCount modifyCount(OrderLine orderLine) {
		OrderLineCount count = new OrderLineCount();
		count.setCountNew(orderLine.getCountNew());
		count.setCountExisting(orderLine.getCountExisting());
		return count;
	}

}
