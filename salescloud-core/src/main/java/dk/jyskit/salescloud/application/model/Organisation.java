package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.google.common.base.Strings;
import org.hibernate.validator.constraints.Email;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@SuppressWarnings("serial")
@Entity
@Table(name = "organisation")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class Organisation extends BaseEntity {
	@Enumerated(EnumType.STRING)
	@NotNull @NonNull
	private OrganisationType type;   
	
	private String organisationId;   // now: division
	
	@NonNull @NotNull
	private String companyName;
	
	@NonNull @NotNull
	private String companyId;  // eg. CVR
	
	@NonNull @NotNull
	private String phone;
	
	@Email
	private String email;
	
	String supportPhone; 
	
	@Email
	String supportEmail;

	private String comment;
	
	@NonNull @NotNull
	private String address;
	
	@NonNull @NotNull
	private String zipCode;
	
	@NonNull @NotNull
	private String city;

	// Commaseparated list of codes granting access to certain functionality
	private String accessCodes;
	
	@OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SalespersonRole> salespersonRoles = new ArrayList<>();
	
	// --------------------------------

	@Transient
	public String getFullAddress() {
		return Strings.nullToEmpty(address) + ", " + Strings.nullToEmpty(getZipCode() + " " + Strings.nullToEmpty(getCity())).trim();
	}

	public void addSalespersonRole(SalespersonRole salespersonRole) {
		salespersonRoles.add(salespersonRole);
		salespersonRole.setOrganisation(this);
	}
	
	public void removeSalespersonRole(SalespersonRole salespersonRole) {
		salespersonRoles.remove(salespersonRole);
	}
	
	// --------------------------------

	public String toString() {
		return companyName;
	}
}
