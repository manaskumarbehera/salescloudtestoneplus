package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name="CampaignProductRelation")
@Table(name = "campaignproduct")
@Data
@EqualsAndHashCode(callSuper = false, of = { "campaignId", "productId" })
@IdClass(CampaignProductRelationId.class)
public class CampaignProductRelation implements Serializable {
	@Id
	private long campaignId;

	@Id
	private long productId;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "campaignId", referencedColumnName = "id")
	private Campaign campaign;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "productId", referencedColumnName = "id")
	private Product product;

	@Column(length=500)
	protected String rabataftaleCampaignDiscountMatrix;

	private long sortIndex;

//	@AttributeOverrides({
////	    @AttributeOverride(name="NUM_VALUES", column= @Column(name="p_num_values")),
//	    @AttributeOverride(name="amounts", column= @Column(name="p_amounts"))
//	  })
//	@Embedded
//	private Amounts baseAmounts = new Amounts();	
	
	@AttributeOverrides({
	    @AttributeOverride(name="amounts", column= @Column(name="d_amounts"))
	})
	@Embedded
	private Amounts campaignDiscountAmounts = new Amounts(0, 0, 0);
	
	@Column(length=100)
	private String outputCodeOverride;  // Kode i tastegrundlag (CDM/Nabs)
	
	@Column(length=100)
	private String outputCodeKvikOverride;  // Kode i tastegrundlag (Kvik)
	
	@Column(length=150)
	private String outputTextOverride;  // Tekst i tastegrundlag
	
//	CampaignProductRelation.productId = Kode i tastegrundlag (CDM/Nabs)
//	CampaignProductRelation.kvikCode = Kode i tastegrundlag (Kvik)
	
	// SHOULD NOT BE IN CORE!!
//	private int addToContractDiscount = 0;	// 0: None, 1: Fixed discount scheme, 2: IPSA discount scheme
	
	// --------------------------------
	// Extra line (with discount) for CDM/Kvik output
	
	private boolean extraRowInOutput = false;
	
	@Column(length=100)
	private String extraOutputCode;
	
	@Column(length=100)
	private String extraOutputCodeKvik;
	
	@Column(length=150)
	private String extraOutputText;
	
	// --------------------------------
	// Extra line (with discount) for offer
	
	private boolean extraRowInOffer = false;
	
	@Column(length=150)
	private String extraRowInOfferText;
	
	// --------------------------------
	
	public CampaignProductRelation() {
	}
	
	public CampaignProductRelation(Product product) {
		setProduct(product);
	}

	// --------------------------------
	
	public void setCampaign (Campaign campaign) {
		this.campaign = campaign;
		if (campaign.getId() != null) {
			this.campaignId = campaign.getId();
		}
	}
	
	public void setProduct (Product product) {
		this.product = product;
		if (product.getId() != null) {
			this.productId = product.getId();
		}
	}
	
	// --------------------------------
	
	public CampaignProductRelation clone() {
		CampaignProductRelation cpr = new CampaignProductRelation();

		cpr.setCampaign(campaign);
		cpr.setProduct(product);
		cpr.setRabataftaleCampaignDiscountMatrix(rabataftaleCampaignDiscountMatrix);
		cpr.setSortIndex(sortIndex);
		cpr.setCampaignDiscountAmounts(campaignDiscountAmounts.clone());
		cpr.setOutputCodeOverride(outputCodeOverride);
		cpr.setOutputCodeKvikOverride(outputCodeKvikOverride);
		cpr.setOutputTextOverride(outputTextOverride);
		cpr.setExtraRowInOutput(extraRowInOutput);
		cpr.setExtraOutputCode(extraOutputCode);
		cpr.setExtraOutputCodeKvik(extraOutputCodeKvik);
		cpr.setExtraOutputText(extraOutputText);
		cpr.setExtraRowInOffer(extraRowInOffer);
		cpr.setExtraRowInOfferText(extraRowInOfferText);
		
		return cpr;
	}
	
	// --------------------------------
	
	@Override
	public String toString() {
		if (product == null) {
			return "Nyt produkt i kampagne";
		}
		return product.getPublicName();
	}
}


