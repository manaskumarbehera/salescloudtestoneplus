/*******************************************************************************
 * Copyright (c) 2012 Anton Bessonov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons
 * Attribution 3.0 License which accompanies this distribution,
 * and is available at
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Contributors:
 *     Anton Bessonov - initial API and implementation
 ******************************************************************************/
package dk.jyskit.waf.application.model;

import java.util.Collections;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import dk.jyskit.waf.application.BaseAppRoles;
import dk.jyskit.waf.wicket.security.IAuthModel;

@SuppressWarnings("serial")
public class AnonymousUser implements IAuthModel {
	private static Roles ROLES = new Roles(BaseAppRoles.ANONYMOUS);
	
	@Override
	public Roles getRoles() {
		return ROLES;
	}

	@Override
	public boolean hasAnyRole(Roles roles) {
		return !Collections.disjoint(getRoles(), roles);
	}

	@Override
	public boolean hasRole(String role) {
		return getRoles().contains(role);
	}

	@Override
	public boolean equals(Object user) {
		if (user instanceof AnonymousUser) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
