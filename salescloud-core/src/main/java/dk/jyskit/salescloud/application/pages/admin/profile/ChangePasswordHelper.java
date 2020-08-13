package dk.jyskit.salescloud.application.pages.admin.profile;

import dk.jyskit.waf.application.model.BaseUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.io.Serializable;

@Data
@Slf4j
public class ChangePasswordHelper implements Serializable {
	private String oldPassword;
	private String newPassword;
	private String repeatNewPassword;
	private BaseUser u;

	public boolean changePassword(String plainText) {
		return u.isAuthenticatedBy(plainText);
	}

	@Transient
	public boolean isStrongEnough(String password) {
		if (StringUtils.isEmpty(password)) {
			return false;
		}
		if (password.length() < 8) {
			return false;
		}
		boolean hasAlphaLower = false;
		boolean hasAlphaUpper = false;
		boolean hasNumeric = false;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (CharUtils.isAsciiAlphaLower(c)) {
				hasAlphaLower = true;
			} else if (CharUtils.isAsciiAlphaUpper(c)) {
				hasAlphaUpper = true;
			} else if (CharUtils.isAsciiNumeric(c)) {
				hasNumeric = true;
			}
		}
		return ((hasAlphaLower || hasAlphaUpper) && hasNumeric);
	}
}
