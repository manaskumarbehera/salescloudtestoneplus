package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import dk.jyskit.salescloud.application.MobileSession;

public enum ContractStatusEnum {
	OPEN (0, "Åben"),
	// Non-office
	WON (1, "Vundet"),
	LOST (2, "Tabt (konkurrent)"),
	CLOSED (3, "Lukket (anden årsag)"),
	// Office
	AWAITING_DATA_FROM_CUSTOMER (10, "Afventer data fra kunde"),
	DATA_RECEIVED_FROM_CUSTOMER (15, "Data modtaget fra kunde"),
	SENT_TO_IMPLEMENTATION (20, "Klar til SalesForce"),
	IMPLEMENTED (25, "Implementeret");
	
	public static int BUSINESSAREA_SPECIFIC = 1;
	
	private int id;
	private String text;

	private ContractStatusEnum(int id, String text) {
		this.id 	= id;
		this.text	= text;
	}
	
	public int getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<ContractStatusEnum> valuesAsList(int flags, ContractStatusEnum ... skipItems) {
		List<ContractStatusEnum> list = new ArrayList<ContractStatusEnum>();
		if ((flags | BUSINESSAREA_SPECIFIC) != 0) {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.OPEN)) {
					list.add(ContractStatusEnum.OPEN);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.AWAITING_DATA_FROM_CUSTOMER)) {
					list.add(ContractStatusEnum.AWAITING_DATA_FROM_CUSTOMER);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER)) {
					list.add(ContractStatusEnum.DATA_RECEIVED_FROM_CUSTOMER);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.SENT_TO_IMPLEMENTATION)) {
					list.add(ContractStatusEnum.SENT_TO_IMPLEMENTATION);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.IMPLEMENTED)) {
					list.add(ContractStatusEnum.IMPLEMENTED);
				}
			} else {
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.OPEN)) {
					list.add(ContractStatusEnum.OPEN);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.WON)) {
					list.add(ContractStatusEnum.WON);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.LOST)) {
					list.add(ContractStatusEnum.LOST);
				}
				if (!ArrayUtils.contains(skipItems, ContractStatusEnum.CLOSED)) {
					list.add(ContractStatusEnum.CLOSED);
				}
			}
		} else {
			for (ContractStatusEnum value : values()) {
				list.add(value);
			}
		}
		return list;
	}
	
	public String toString() {
		return text;
	}
}
