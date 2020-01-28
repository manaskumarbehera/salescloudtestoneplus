package dk.jyskit.salescloud.application.extensionpoints;

import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.OrderLineCount;

public interface OrderLineCountModifier {
	OrderLineCount modifyCount(OrderLine orderLine);
}
