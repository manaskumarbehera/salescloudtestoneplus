package dk.jyskit.salescloud.application.apis.auth;

import lombok.Data;

@Data
public class AuthorizationSuccess {
	private String token;
	
	private Long userId;
	private String firstName;
	private String lastName;
	private String fullName;
	private String personalPhone;
	private String email;
	private String[] roles;
	
	private Organisation organisation;
	private boolean agent;
	private boolean agent_sa;
	private boolean agent_mb;
	private boolean agent_lb;
	private boolean partner;
	private boolean partner_ec;
}
