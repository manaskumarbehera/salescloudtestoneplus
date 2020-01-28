package dk.jyskit.salescloud.application.apis.auth;

import java.io.Serializable;

import lombok.Data;

@Data
public class RoleChangeInput implements Serializable {
	private String token;
	private boolean agent;
	private boolean agent_sa;
	private boolean agent_mb;
	private boolean agent_lb;
	private boolean partner;
	private boolean partner_ec;
}
