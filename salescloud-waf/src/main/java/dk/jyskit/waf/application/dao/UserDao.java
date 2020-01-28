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
package dk.jyskit.waf.application.dao;

import java.util.List;

import dk.jyskit.waf.application.model.BaseUser;

public interface UserDao extends Dao<BaseUser> {
	List<BaseUser> findByUsername(String username);
	List<BaseUser> findByEmail(String email);
}
