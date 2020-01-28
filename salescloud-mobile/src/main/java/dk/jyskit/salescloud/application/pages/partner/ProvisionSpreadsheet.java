package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.NumberTransferType;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.SimCardType;
import dk.jyskit.salescloud.application.model.Subscription;
import lombok.Data;

public class ProvisionSpreadsheet implements Provider<Workbook>, Serializable{

	private boolean isNabs;

	public ProvisionSpreadsheet(boolean isNabs) {
		this.isNabs = isNabs;
	}
	
	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("Provision");
		
		MobileContract contract = MobileSession.get().getContract();

	    s.incRow();
	    s.addValue("Partner provision");
	    
	    s.incRow();
	    
	    List<SubscriptionGroup> groups = new ArrayList<>();
	    for (Subscription subscription : contract.getSubscriptions()) {
			SubscriptionGroup group = null;
			List<MobileProduct> addons = new ArrayList<>();
			for (OrderLine orderLine : contract.getProductOrderLines()) {
				if (((MobileProduct) orderLine.getProduct()).isAddOn()) {
					addons.add((MobileProduct) orderLine.getProduct());
				}
			}
			
			for (SubscriptionGroup subscriptionGroup : groups) {
				if (addons.size() != subscriptionGroup.getAddons().size()) {
					continue;
				}
				boolean match = true;
				for (Product addon: addons) {
					if (!subscriptionGroup.getAddons().contains(addon)) {
						match = false;
						break;
					}
				}
				if (!match) {
					continue;
				}
				
				if (!ObjectUtils.equals(subscription.getSimCardType(), subscriptionGroup.getSimCardType()) ||
					!ObjectUtils.equals(subscription.getDatadelingSimCardType(), subscriptionGroup.getDatadelingSimCardType()) ||
					!ObjectUtils.equals(subscription.getNumberTransferType(), subscriptionGroup.getNumberTransferType()) ||
					!subscription.getBundle().equals(subscriptionGroup.getBundle())) {
					continue;
				}
				
				subscriptionGroup.getSubscriptions().add(subscription);
				group = subscriptionGroup;
				break;
			}
			if (group == null) {
				group = new SubscriptionGroup();
				groups.add(group);
				group.getSubscriptions().add(subscription);
				
				group.setSimCardType(subscription.getSimCardType());
				group.setDatadelingSimCardType(subscription.getDatadelingSimCardType());
				group.setNumberTransferType(subscription.getNumberTransferType());
				group.setBundle(subscription.getBundle());
				for (MobileProduct addon : addons) {
					group.getAddons().add(addon);
				}
			}
		}
	    Collections.sort(groups, new Comparator<SubscriptionGroup>() {
	    	@Override
	    	public int compare(SubscriptionGroup o1, SubscriptionGroup o2) {
	    		return Integer.valueOf(o2.getSubscriptions().size()).compareTo(Integer.valueOf(o1.getSubscriptions().size()));
	    	}
		});
	    
	    addHeaderRow(s, null, "Kunde navn:", contract.getCustomer().getCompanyName(), "Sælger navn:", contract.getSalesperson().getUser().getFirstName() + " " + contract.getSalesperson().getUser().getLastName());
	    addHeaderRow(s, null, "CVR nummer:", contract.getCustomer().getCompanyId(), "Email adresse:", contract.getSalesperson().getUser().getEmail());
	    addHeaderRow(s, null, "Kontaktperson:", contract.getCustomer().getName(), "Telefon nummer:", contract.getSeller().getPhone());
	    addHeaderRow(s, null, "Adresse:", contract.getCustomer().getAddress(), "", "");
	    addHeaderRow(s, null, "Postnr./By:", contract.getCustomer().getZipCode() + " " + contract.getCustomer().getCity(), "", "");
	    addHeaderRow(s, null, "Telefon nummer:", contract.getCustomer().getPhone(), "", "");
	    addHeaderRow(s, null, "Email:", contract.getCustomer().getEmail(), "", "");
	    addHeaderRow(s, null, "Ønsket oprettelsesdato:", (MobileSession.get().getContract().getInstallationDate() == null ? "Ikke fastlagt" : new SimpleDateFormat("dd/MM/yyyy").format(MobileSession.get().getContract().getInstallationDate())), "", "");
	    MobileCampaign campaign = (MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0);
	    if ((campaign.getFromDate() != null) || (campaign.getToDate() != null)) {
		    addHeaderRow(s, IndexedColors.RED, "Kampagnekode:", campaign.getProductId(), "", "");
	    }
	    
	    s.incRow();

	    if (isNabs) {
		    addRow(s, null, "Gruppe", "Antal", "Type", "Tlf. nr.", "Navn og afdeling til faktura", "ICC", "Simkort type", "Prisplan", "Tillægs-SOC", "Simkort type (datadeling)");
	    } else {
		    addRow(s, null, "Gruppe", "Antal", "Type", "Tlf. nr.", "Navn og afdeling til faktura", "ICC", "Simkort type", "Produkt", "Tilvalgsprodukt", "Simkort type (datadeling)");
	    }
	    
	    int groupNo = 0;
	    for (SubscriptionGroup group : groups) {
	    	groupNo++;
	    	for (Subscription subscription : group.getSubscriptions()) {
	    		StringBuilder addons = new StringBuilder();
	    		
	    		/*
	    		 * Add product if it is an addon and it is:
	    		 * 1) selected for all subscriptions, or
	    		 * 2) an addon (roaming/functions) for any bundle associated with the subscription, or 
	    		 * 3) part of the mix bundle associated with the subscription 
	    		 */
	    		for(OrderLine orderLine: contract.getOrderLines()) {
	    			MobileProduct product = (MobileProduct) orderLine.getProduct();
	    			if (product != null) {
	    				if (product.isAddOn()) {
	    					if (orderLine.isCustomFlag() || subscription.getProducts().contains(product)) {
	    	    				addAddon(addons, product, contract);
	    					}
	    				}
	    			}
	    		}
	    		
	    		boolean isMixBundle = false;
    			for(BundleProductRelation productRelation: subscription.getBundle().getProducts()) {
    				MobileProduct product = (MobileProduct) productRelation.getProduct();
    				if ((product != null) && MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE.getKey().equals(product.getProductGroup().getUniqueName())) {
    					isMixBundle = true;
    				}
    			}
    			
	    		String prisplan = "";
    			if (isMixBundle) {
	    			for(BundleProductRelation productRelation: subscription.getBundle().getProducts()) {
	    				MobileProduct product = (MobileProduct) productRelation.getProduct();
	    				if ((product != null) && MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE.getKey().equals(product.getProductGroup().getUniqueName())) {
	    	    			prisplan = (isNabs ? product.getNabsCode() : product.getKvikCode());
	    				} else {
    	    				addAddon(addons, product, contract);
	    				}
	    			}
    			} else {
	    			prisplan = (isNabs ? subscription.getBundle().getProductId() : subscription.getBundle().getKvikCode());
	    			if (subscription.getBundle().isExtraRowInOutput()) {
	    				addExtraBundleInfo(addons, subscription.getBundle());
	    			}
    			}
//	Old:	    		
//	    		if (((MobileProductBundle) subscription.getBundle()).isStandardBundle()) {
//	    			prisplan = (isNabs ? subscription.getBundle().getProductId() : subscription.getBundle().getKvikCode());
//	    		} else {
//	    			for(BundleProductRelation productRelation: subscription.getBundle().getProducts()) {
//	    				MobileProduct product = (MobileProduct) productRelation.getProduct();
//	    				if (MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE.getKey().equals(product.getProductGroup().getUniqueName())) {
//	    	    			prisplan = (isNabs ? product.getNabsCode() : product.getKvikCode());
//	    				} else {
//    	    				addAddon(addons, product);
//	    				}
//	    			}
//	    		}
	    		
			    addRow(s, (groupNo % 2 == 0 ? IndexedColors.LIGHT_CORNFLOWER_BLUE : IndexedColors.LIGHT_GREEN), 
			    		String.valueOf(groupNo), String.valueOf(group.getSubscriptions().size()), subscription.getNumberTransferType().getText(), subscription.getMobileNumber(), 
			    		subscription.getName() + " - " + subscription.getDivision(), subscription.getIcc(), subscription.getSimCardType().getText(), 
			    		prisplan, 
			    		addons.toString(), 
			    		(subscription.getDatadelingSimCardType() == null ? "" : subscription.getDatadelingSimCardType().getText()));
			}
		}
	    
		return s.getWorkbook();
	}

	private void addExtraBundleInfo(StringBuilder addons, MobileProductBundle bundle) {
		if (isNabs && !StringUtils.isEmpty(bundle.getExtraRowInOutputCode())) {
			if (addons.length() > 0) {
				addons.append(", ");
			}
			addons.append(bundle.getExtraRowInOutputCode());
		} else if (!isNabs && !StringUtils.isEmpty(bundle.getExtraRowInOutputKvikCode())) {
			if (addons.length() > 0) {
				addons.append(", ");
			}
			addons.append(bundle.getExtraRowInOutputKvikCode());
		}
	}

	private void addAddon(StringBuilder addons, MobileProduct product, MobileContract contract) {
		if (!product.isExcludeFromProductionOutput()) {
			if (addons.length() > 0) {
				addons.append(", ");
			}
			
			CampaignProductRelation campaignProductRelation = contract.getCampaignProductRelation(product);
			
			if (isNabs) {
				if ((campaignProductRelation != null) && (!StringUtils.isEmpty(campaignProductRelation.getOutputCodeOverride()))) {
					addons.append(campaignProductRelation.getOutputCodeOverride());
				} else {
					addons.append(((MobileProduct) product).getNabsCode());
				}
			} else {
				if ((campaignProductRelation != null) && (!StringUtils.isEmpty(campaignProductRelation.getOutputCodeKvikOverride()))) {
					addons.append(campaignProductRelation.getOutputCodeKvikOverride());
				} else {
					addons.append(((MobileProduct) product).getKvikCode());
				}
			}
			
			if (campaignProductRelation != null) {
				if (isNabs && campaignProductRelation.isExtraRowInOutput() && (!StringUtils.isEmpty(campaignProductRelation.getExtraOutputCode()))) {
					addons.append(", ");
					addons.append(campaignProductRelation.getExtraOutputCode());
				}
				if (!isNabs && campaignProductRelation.isExtraRowInOutput() && (!StringUtils.isEmpty(campaignProductRelation.getExtraOutputCodeKvik()))) {
					addons.append(", ");
					addons.append(campaignProductRelation.getExtraOutputCodeKvik());
				}
			}
		}
	}

	private void addRow(Spreadsheet s, IndexedColors color, String ... texts) {
	    s.incRow();
	    
	    for (String text : texts) {
	    	if (color == null) {
			    s.addValue(text);
	    	} else {
			    s.addValueAndColor(text, color);
	    	}
		}
	}

	private void addHeaderRow(Spreadsheet s, IndexedColors color, String ... texts) {
	    s.incRow();
	    for (String text : texts) {
	    	if (color == null) {
			    s.addValue(text);
	    	} else {
	    		s.addColoredValue(text, color);
	    	}
		    s.incCol(2);
		}
	}
	
	@Data
	class SubscriptionGroup {
		private List<Subscription> subscriptions = new ArrayList<>();
		private NumberTransferType numberTransferType;
		private SimCardType simCardType;
		private SimCardType datadelingSimCardType;
		private MobileProductBundle bundle;
		private List<MobileProduct> addons = new ArrayList<>();
	}
}
