package dk.jyskit.salescloud.application.apis.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordChangeRequest implements Serializable {
	private String username;
	private String password;
	private String token;
}
