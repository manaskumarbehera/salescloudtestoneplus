package dk.jyskit.salescloud.application.apis.auth;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.OrganisationType;
import lombok.Data;

@Data
public class Organisation implements Serializable {
	private OrganisationType type;   
	private String organisationId;   // division
	private String companyName;
	private String companyId;  // eg. CVR
	private String phone;
	private String email;
	private String supportPhone; 
	private String supportEmail;
	private String comment;
	private String address;
	private String zipCode;
	private String city;
}