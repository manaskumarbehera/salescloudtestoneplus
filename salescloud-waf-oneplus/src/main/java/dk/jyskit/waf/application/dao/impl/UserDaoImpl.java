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
package dk.jyskit.waf.application.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;

public class UserDaoImpl extends GenericDaoImpl<BaseUser> implements UserDao {

    @Inject
	public UserDaoImpl(Provider<EntityManager> emp) {
		super(TypeLiteral.get(BaseUser.class), emp);
	}

	@Override
	public List<BaseUser> findByUsername(String username) {
        TypedQuery<BaseUser> q = em()
        		.createQuery("select u from " + clazz.getSimpleName() + " u "
        				+ "where u.username = :username "
        				+ "order by u.entityState asc", clazz)
                		.setParameter("username", username);
        return q.getResultList();
	}

	@Override
	public List<BaseUser> findByEmail(String email) {
        TypedQuery<BaseUser> q = em()
        		.createQuery("select u from " + clazz.getSimpleName() + " u "
        				+ "where u.email = :email "
        				+ "order by u.entityState asc", clazz)
                		.setParameter("email", email);
        return q.getResultList();
	}
	
}
