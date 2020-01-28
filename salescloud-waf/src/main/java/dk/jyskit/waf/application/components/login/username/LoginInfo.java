package dk.jyskit.waf.application.components.login.username;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginInfo implements Serializable {
	private String username;
	private String password;
	private String width;
}
