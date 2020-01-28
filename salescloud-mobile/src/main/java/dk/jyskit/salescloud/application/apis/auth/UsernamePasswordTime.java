package dk.jyskit.salescloud.application.apis.auth;

import lombok.Data;

@Data
public class UsernamePasswordTime {
	private String username;
	private String password;
	private long time;
}
