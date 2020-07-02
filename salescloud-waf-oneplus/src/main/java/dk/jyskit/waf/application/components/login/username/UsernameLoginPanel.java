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
package dk.jyskit.waf.application.components.login.username;

import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.waf.application.components.login.LoginAuxErrorProvider;

public class UsernameLoginPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public UsernameLoginPanel(String id) {
		super(id);

		add(new UsernameLoginForm("loginForm"));
	}
}
