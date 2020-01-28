package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum PaymentFrequency {
	WEEKLY("paymentfrequency.weekly"),
	MONTHLY("paymentfrequency.monthly"),
	QUARTERLY("paymentfrequency.quarterly"),
	YEARLY("paymentfrequency.yearly");
	
	private String key;			// Used for localization

	private PaymentFrequency(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public static List<PaymentFrequency> valuesAsList() {
		List<PaymentFrequency> list = new ArrayList<PaymentFrequency>();
		for (int i = 0; i < values().length; i++) {
			PaymentFrequency value = values()[i];
			list.add(value);
		}
		return list;
	}
}
