package dk.jyskit.waf.application.components.login.email;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginInfo implements Serializable {
	private String email;
	private String password;
}
