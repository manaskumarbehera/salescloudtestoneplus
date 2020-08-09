package dk.jyskit.salescloud.application.pages.admin.profile;

import java.io.Serializable;

import lombok.Data;
import dk.jyskit.waf.application.model.BaseUser;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;

@Data
public class ChangePasswordHelper implements Serializable {	
	private String oldPassword;
	private String newPassword;
	private String repeatNewPassword;
	
	private BaseUser u;
	
	public boolean changePassword(String plainText) {
		boolean succes = u.isAuthenticatedBy(plainText);
		return succes;
	}

	@Transient
	public boolean isStrongEnough(String password) {
		return !StringUtils.isEmpty(password) && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
	}
}
