package dk.jyskit.salescloud.application.apis.auth;

import java.io.Serializable;

import lombok.Data;

@Data
public class AuthorizationInput implements Serializable {
	private String username;
	private String password;
	private String token;
}
