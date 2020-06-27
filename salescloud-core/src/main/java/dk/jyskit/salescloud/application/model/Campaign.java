package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="Campaign")
@Table(name = "campaign")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of={"name"})
public class Campaign extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private EntityState state;	 
	
	@NonNull
	@Column(length=50)
	private String name;

	@JsonIgnore
	@Nonnull
	@ManyToOne(optional = false)
	private BusinessArea businessArea;
	
	// Perform GKS validation on summary?
	private boolean gksValidation;

	@JsonIgnore
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductBundle> productBundles = new ArrayList<>();
	
	/* Many-to-many relation */
	@JsonIgnore
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampaignProductRelation> campaignProducts = new ArrayList<>();

	@Temporal(TemporalType.DATE)
	private Date fromDate;	// Inclusive this timestamp. Value is null if the campaign is "permanent"

	@Temporal(TemporalType.DATE)
	private Date toDate;	// Inclusive this timestamp. You may want to set it to something like "dd.mm.yyyy 23:59"
	
	@Temporal(TemporalType.DATE)
	private Date extensionFromDate;	// Inclusive this timestamp. Value is null if the campaign is "permanent"
	
	@Temporal(TemporalType.DATE)
	private Date extensionToDate;	// Inclusive this timestamp. You may want to set it to something like "dd.mm.yyyy 23:59"
	
	@Column(length=30)
	private String productId;	// Bruges i tastegrundlag -> Kampagnekode (100% mobil)
	
//	@Column(length=50)
//	private String productText;
	
	@Column(name="FILTERTEXT", length=400)
	private String filter;
	
	// --------------------------------
	
	public void addProductBundle(ProductBundle productBundle) {
		productBundles.add(productBundle);
		productBundle.setCampaign(this);
	}
	
	public void removeProductBundle(ProductBundle productBundle) {
		productBundles.remove(productBundle);
		productBundle.setCampaign(null);
	}
	
	public void addCampaignProductRelation(CampaignProductRelation relation) {
		relation.setCampaign(this);  // Just to be sure
		campaignProducts.add(relation);
	}
	
	public void removeCampaignProductRelation(Product product) {
		for (CampaignProductRelation relation : campaignProducts) {
			if (product.equals(relation.getProduct())) {
				campaignProducts.remove(relation);
				break;
			}
		}
	}
}

