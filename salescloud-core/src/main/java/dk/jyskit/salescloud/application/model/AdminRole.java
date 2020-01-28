package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;

import dk.jyskit.waf.application.model.BaseRole;

@Entity
public class AdminRole extends BaseRole {
	public final static String ROLE_NAME	= "admin";
	
	public AdminRole() {
		roleName = ROLE_NAME;
	}
	
	// --------------------------------
	
	public boolean equals(Object o) {
		return id.equals(((BaseRole) o).getId());
	};
	
	@Override
	public String toString() {
		return "Administrator";
	}
}
