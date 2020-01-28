package dk.jyskit.waf.application.components.login;

import java.io.Serializable;

import dk.jyskit.waf.application.model.BaseUser;

public interface LoginAuxErrorProvider extends Serializable {
	/**
	 * In case of a problem, a key for translation should be returned.
	 * 
	 * @param user
	 * @return
	 */
	String evaluateUser(BaseUser user);
}
