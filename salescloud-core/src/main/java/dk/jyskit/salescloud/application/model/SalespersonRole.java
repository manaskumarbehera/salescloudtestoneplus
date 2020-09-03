package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.jyskit.waf.application.model.BaseRole;

@Entity
public class SalespersonRole extends BaseRole {
	public final static String ROLE_NAME	= "salesperson";

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANISATION_ID")
	private Organisation organisation;

	@JsonIgnore
	@Embedded
	@Deprecated
	protected BusinessEntity companyInfo = new BusinessEntity();
	
	// TODO: This does not belong in CORE
	private boolean agent;
	private boolean agent_sa;
	private boolean agent_mb;
	private boolean agent_lb;
	private boolean partner;
	private boolean partner_ec;
	
	@Column(length=30)
	private String division;
	
	// Commaseparated list of codes granting access to certain functionality
	private String accessCodes;
	
	public SalespersonRole() {
		roleName = ROLE_NAME;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "salesperson", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContractCategory> contractCategories = new ArrayList<ContractCategory>();

	@JsonIgnore
	@OneToMany(mappedBy = "salesperson", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Contract> contracts = new ArrayList<Contract>();
	
	// --------------------------------
	
	public void addContractCategory(ContractCategory contractCategory) {
		contractCategories.add(contractCategory);
		contractCategory.setSalesperson(this);
	}
	
	public void addContract(Contract contract) {
		contracts.add(contract);
		contract.setSalesperson(this);
	}

	// --------------------------------

	public String getAccessCodes() {
		return accessCodes;
	}
	
	public void setAccessCodes(String accessCodes) {
		this.accessCodes = accessCodes;
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public String getDivision() {
		return division;
	}
	
	public void setDivision(String division) {
		this.division = division;
	}
	
	public BusinessEntity getCompanyInfo() {
		return companyInfo;
	}

	public void setCompanyInfo(BusinessEntity companyInfo) {
		this.companyInfo = companyInfo;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public boolean isAgent_sa() {
		return agent_sa;
	}

	public void setAgent_sa(boolean agent_sa) {
		this.agent_sa = agent_sa;
	}

	public boolean isAgent_mb() {
		return agent_mb;
	}

	public void setAgent_mb(boolean agent_mb) {
		this.agent_mb = agent_mb;
	}

	public boolean isAgent_lb() {
		return agent_lb;
	}

	public void setAgent_lb(boolean agent_lb) {
		this.agent_lb = agent_lb;
	}

	public boolean isPartner() {
		return partner;
	}

	public void setPartner(boolean partner) {
		this.partner = partner;
	}

	public boolean isPartner_ec() {
		return partner_ec;
	}

	public void setPartner_ec(boolean partner_ec) {
		this.partner_ec = partner_ec;
	}

	public List<ContractCategory> getContractCategories() {
		return contractCategories;
	}

	public void setContractCategories(List<ContractCategory> contractCategories) {
		this.contractCategories = contractCategories;
	}

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}
	
	// --------------------------------

	public int hashCode() {
		return (int) Integer.valueOf("" + id);
	}
	
	public boolean equals(Object o) {
		return id.equals(((BaseRole) o).getId());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (agent) {
			sb.append(", Agent");
		}
		if (agent_sa) {
			sb.append(", Agent-SA");
		}
		if (agent_mb) {
			sb.append(", Agent-MB");
		}
		if (agent_lb) {
			sb.append(", Agent-LB");
		}
		if (partner) {
			sb.append(", Partner");
		}
		if (partner_ec) {
			sb.append(", Partner-EC");
		}
		if (sb.length() == 0) {
			return "Sælger (?)";
		} else {
			return "Sælger (" + sb.toString().substring(2) + ")";
		}
	}
}
