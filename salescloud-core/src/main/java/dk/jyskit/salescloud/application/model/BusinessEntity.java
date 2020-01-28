package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.hibernate.validator.constraints.Email;

/**
 * This class is 
 * @author jan
 *
 */
@NoArgsConstructor
@Embeddable
public class BusinessEntity implements Serializable {
	@NonNull @NotNull
	private String name;
	
	private String position;
	
	@NonNull @NotNull
	private String companyName;
	
	@NonNull @NotNull
	private String companyId;  // eg. CVR
	
//	// NY ->
//	
//	@Enumerated(EnumType.STRING)
//	@NotNull @NonNull
//	private BusinessEntityType type; 
//	
//	private String businessEntityId;   // now: division
//	
//	// <- NY
	
	@NonNull @NotNull
	private String phone;
	
	@NonNull @NotNull
	@Email
	private String email;

	private String comment;
	
	@NonNull @NotNull
	private String address;
	
	@NonNull @NotNull
	private String zipCode;
	
	@NonNull @NotNull
	private String city;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	// --------------------------------

	@Transient
	public String getFullAddress() {
		return Strings.nullToEmpty(address) + ", " + Strings.nullToEmpty(getZipCode() + " " + Strings.nullToEmpty(getCity())).trim();
	}
}
