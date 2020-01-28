package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.Transient;

import lombok.Data;

@Data
public class OrderLineCount implements Serializable {
	private int countNew;
	private int countExisting;
	
	@Transient
	public int getCountTotal() {
		return countNew + countExisting;
	}
	
	@Transient
	public int getCountForFeeCategory(FeeCategory category) {
		if (FeeCategory.RECURRING_FEE.equals(category)) {
			return countNew + countExisting;
		}
		return countNew;
	}
	
	public static OrderLineCount add(OrderLineCount c1, OrderLineCount c2) {
		OrderLineCount c = new OrderLineCount();
		c.setCountNew(c1.getCountNew() + c2.getCountNew());
		c.setCountExisting(c1.getCountExisting() + c2.getCountExisting());
		return c;
	}

	public static OrderLineCount one() {
		OrderLineCount c = new OrderLineCount();
		c.setCountNew(1);
		c.setCountExisting(0);
		return c;
	}
}
