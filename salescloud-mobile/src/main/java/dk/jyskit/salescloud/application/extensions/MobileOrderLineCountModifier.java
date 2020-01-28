package dk.jyskit.salescloud.application.extensions;

import dk.jyskit.salescloud.application.extensionpoints.OrderLineCountModifier;
import dk.jyskit.salescloud.application.model.Constants;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.OrderLineCount;

public class MobileOrderLineCountModifier implements OrderLineCountModifier {
	@Override
	public OrderLineCount modifyCount(OrderLine orderLine) {
		OrderLineCount count = new OrderLineCount();
		if (Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT == orderLine.getCountNew()) {
			count.setCountNew(((MobileContract) orderLine.getContract()).getSubscriptions().size());
			count.setCountExisting(orderLine.getCountExisting());
		} else {
			count.setCountNew(orderLine.getCountNew());
			count.setCountExisting(orderLine.getCountExisting());
		}
		return count;
	}
}
