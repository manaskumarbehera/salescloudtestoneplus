package dk.jyskit.salescloud.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import dk.jyskit.waf.application.model.BaseRole;

@Entity
public class SalesmanagerRole extends BaseRole {
	public final static String ROLE_NAME	= "salesmanager";
	public final static String WILDCARD		= "*";
	
//	@Embedded
//	protected BusinessEntity companyInfo = new BusinessEntity();
	
	@Column(length=500)
	private String divisions;
	
	public SalesmanagerRole() {
		roleName = ROLE_NAME;
	}

	// --------------------------------
	
	public boolean hasDivision(String division) {
		if (WILDCARD.equals(divisions)) {
			return true;
		} else {
			if (StringUtils.isEmpty(division)) {
				return false;
			}
			for (String d : divisions.split(",[ ]*")) {
				if (division.equals(d)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getDivisions() {
		return divisions;
	}
	
	public void setDivisions(String divisions) {
		this.divisions = divisions;
	}
	
	// --------------------------------
	
	public boolean equals(Object o) {
		return id.equals(((BaseRole) o).getId());
	};
	
	@Override
	public String toString() {
		return "Sales Manager";
	}
}
