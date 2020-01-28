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
package dk.jyskit.waf.wicket.security;

import java.io.Serializable;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import dk.jyskit.waf.application.dao.ExtraDataDao;
import dk.jyskit.waf.application.dao.ExtraDataDefinitionDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.extradata.ExtraData;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;
import dk.jyskit.waf.utils.guice.Lookup;

@Slf4j
public abstract class UserSession extends WebSession {
	protected Long userId;
	protected boolean useAdminThemeHintFromPage;
	protected Class activeRoleClass;

	public UserSession(Request request) {
        super(request);
    }

	public static UserSession get() {
		return (UserSession) Session.get();
	}
	
	/**
	 * Check if active role is one of the specified roles.
	 * 
	 * @param roleClasses
	 * @return
	 */
	public boolean isRole(Class ... roleClasses) {
		for (Class<BaseRole> roleClass : roleClasses) {
			if (roleClass.isAssignableFrom(activeRoleClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if current user has any of the specified roles.
	 * 
	 * @param roleClasses
	 * @return
	 */
	public boolean hasRole(Class ... roleClasses) {
		BaseUser user = getUser();
		if (user != null) {
			for (Class<BaseRole> roleClass : roleClasses) {
				List<BaseRole> baseRoleList = user.getBaseRoleList();
				for (BaseRole userRole : baseRoleList) {
					if (roleClass.isAssignableFrom(userRole.getClass())) {
						return true;
					}
				}
			}
		}
		return false;
	}

    public Class getActiveRoleClass() {
		return activeRoleClass;
	}

	public void setActiveRoleClass(Class newActiveRoleClass) {
		Class oldActiveRoleClass = activeRoleClass;
		this.activeRoleClass = newActiveRoleClass;
		onActiveRoleChanged(oldActiveRoleClass, newActiveRoleClass);
	}
	
	public void onActiveRoleChanged(Class oldActiveRoleClass, Class newActiveRoleClass) {
	}

	public boolean isUserLoggedIn() {
		return (userId != null);
	}

	public void setUseAdminThemeHintFromPage(boolean useAdminThemeForCurrentPage) {
		this.useAdminThemeHintFromPage = useAdminThemeForCurrentPage;
	}

	public boolean useAdminTheme() {
		return useAdminThemeHintFromPage;
	}

    @Override
    public void invalidate() {
    	super.invalidate();
    	setUser(null);
    	setActiveRoleClass(null);
    }

	public BaseUser getUser() {
		return userId == null ? null : Lookup.lookup(UserDao.class).findById(userId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUser(BaseUser user) {
		userId = (user == null ? null : user.getId());
		if (user == null) {
			setActiveRoleClass(null);
			return;
		}
		Class[] roles = getRolesByPriority();
		for (Class roleClass : roles) {
			if (getUser().hasRole(roleClass)) {
				setActiveRoleClass(roleClass);
				return;
			}
		}
		setActiveRoleClass(null);
		log.error("Unknown role for user: " + getUser().getId());
	}

	@SuppressWarnings("rawtypes")
	public abstract Class[] getRolesByPriority();
	
	public void resetPrefCache() {
		for (String name : getAttributeNames()) {
			if (name.startsWith("user¤")) {
				removeAttribute(name);
			}
		}
	}

	public String getUserPreference(String key) {
		final String NULL_PREF = "¤N¤U¤L¤L";
		Serializable attribute = getAttribute(userPrefKey(key));
		if (attribute != null && attribute instanceof String) {
			return NULL_PREF.equals(attribute) ? null : (String) attribute;
		}
		// lookup in extradata
		String lookup = lookupUserExtraData(key);
		// cache to avoid lookup for the rest of the session.
		setAttribute(userPrefKey(key), lookup == null ? NULL_PREF : lookup);
		return lookup;
	}

	public String userPrefKey(String key) {
		return "user¤" + key;
	}

	/**
	 * Override this to provide special handling of "User preferences".
	 * Default lookup in extra data for user.
	 * @param key
	 * @return
	 */
	protected String lookupUserExtraData(String key) {
		return lookupEntityExtraData(key, getUser());
	}

	/**
	 * Helper method to lookup extra data value for an entity. Tries to find the value for the entity or the default value from the definition.
	 * The key is search for the given entities in order, first find is returned. After the entities any definition is searched.
	 * @param key
	 * @param entities one or more entities to search for extradata.
	 * @return
	 */
	protected String lookupEntityExtraData(String key, BaseEntity... entities) {
		for (BaseEntity entity : entities) {
			if (entity != null) {
				ExtraData data = Lookup.lookup(ExtraDataDao.class).findForEntity(entity, key);
				if (data != null && data.getValue() != null) {
					return data.getValue();
				}
			}
		}
		// looking for defaults in definitions
		for (BaseEntity entity : entities) {
			if (entity != null) {
				ExtraDataDefinition definition = Lookup.lookup(ExtraDataDefinitionDao.class).findForEntityClass(entity.getClass(), key);
				if (definition != null) {
					return definition.getDefaultValue();
				}
			}
		}
		return null;
	}

	public void setUserPreference(String key, String value) {
		setAttribute(userPrefKey(key), value);
	}

	public void storeUserPreference(String key, String value) {
		setAttribute(userPrefKey(key), value);
		ExtraDataDao dao = Lookup.lookup(ExtraDataDao.class);
		ExtraData data = dao.findForEntity(getUser(), key);
		if (data == null) {
			data = new ExtraData(getUser(), key, value);
		} else {
			data.setValue(value);
		}
		dao.saveAndFlush(data);
	}

}
