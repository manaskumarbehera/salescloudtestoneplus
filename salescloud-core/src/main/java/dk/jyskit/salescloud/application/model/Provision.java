package dk.jyskit.salescloud.application.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Provision implements Serializable {
	public static final int TYPE_SATS = 0;		// Percentage
	public static final int TYPE_STYK = 1;		// Per item
	// --- special ---
	public static final int TYPE_HEADER = 2;

	private Amounts amounts = new Amounts();
	private int type;
	private int count;
	private String text;		// Product name
}
