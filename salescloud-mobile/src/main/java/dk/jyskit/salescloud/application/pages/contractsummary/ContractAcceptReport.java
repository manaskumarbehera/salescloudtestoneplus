package dk.jyskit.salescloud.application.pages.contractsummary;

import java.util.ArrayList;
import java.util.List;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.x5.template.Chunk;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ContractAcceptReport extends AbstractContractReport {

	protected boolean showOffer;
	protected boolean showRammeaftale;
	protected boolean showBilagOne;
	protected boolean showBilagNetwork;

	public ContractAcceptReport(boolean showOffer, boolean showRammeaftale, boolean bilagOne, boolean bilagNetwork) {
		super(!showOffer);
		this.showOffer 			= showOffer;
		this.showRammeaftale 	= showRammeaftale;
		this.showBilagOne 		= bilagOne;
		this.showBilagNetwork 	= bilagNetwork;
	}

	@Override
	protected void setProperties(Chunk html) {
		MobileContract contract = MobileSession.get().getContract();
		MobileCampaign campaign = (MobileCampaign) contract.getCampaigns().get(0);
		
		ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(true, false, partnerDocument);	// CHECKMIG
		
        for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
        	if (discountScheme instanceof RabatAftaleDiscountScheme) {
				DiscountPoint discountPoint = ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNonNetwork(); 	// CHECKMIG
        		html.set("contract_discount_pct", Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage()));
//        		html.set("contract_discount_pct", Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage() * 100 / discountPoint.getDivisor()));
        	}
        }
        
    	html.set("customer_name", contract.getCustomer().getName());
    	html.set("customer_cvr", contract.getCustomer().getCompanyId());
    	
    	DiscountPoint discountPoint = MobileSession.get().getDiscountPointNonNetwork();  // CHECKMIG
        html.set("prisaftale_1", ((MobileCampaign) contract.getBusinessArea().getPermanentCampaign()).getPrisaftale(discountPoint.getStep(), contract.getAdjustedContractLength()));
        if (campaign.equals(contract.getBusinessArea().getPermanentCampaign())) {
        	html.set("prisaftale_2", "");
        } else {
        	html.set("prisaftale_2", campaign.getPrisaftale(discountPoint.getStep(), contract.getAdjustedContractLength()));
        }

		if (showRammeaftale && !showOffer) {
			html.set("is_show_rammeaftale_with_signature", true);
		} else {
			html.set("is_offer", showOffer);
			html.set("show_offer", showOffer);
			html.set("is_show_rammeaftale", showRammeaftale);
		}
		html.set("show_bilag_one", showBilagOne && contract.getAdjustedContractLength() > 0);
//			html.set("is_show_bilag", showBilag);
		html.set("show_bilag_network", showBilagNetwork && contract.getAdjustedContractLengthNetwork() > 0);
        html.set("rabataftale_kontraktsum", Amounts.getFormattedWithDecimals(contractFinansialInfo.getRabataftaleKontraktsum()));
        
        List<String> productsWithContractDiscountOnly = new ArrayList<>();
        {
        	if (MobileSession.get().isBusinessAreaOnePlus() || MobileSession.get().isBusinessAreaTdcWorks()) {
				for (ProductBundle bundle : contract.getCampaigns().get(0).getProductBundles()) {
					if (bundle.isActive()) {
						for (BundleProductRelation bundleProductRelation : bundle.getProducts()) {
							if ((bundleProductRelation.getProduct() != null) &&
									((MobileProductGroup) bundleProductRelation.getProduct().getProductGroup()).isOfType(
											MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE
											, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE
											, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD
									)) {
								productsWithContractDiscountOnly.add(bundle.getPublicName().replace("&", "&amp;"));
								break;
							}
						}
					}
				}
			}
			if (MobileSession.get().isBusinessAreaOnePlus()) {
				for (ProductGroup group : contract.getBusinessArea().getProductGroupsAndChildren()) {
					for (Product product: group.getProducts()) {
						if (((MobileProductGroup) group).isOfType(
								MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_FUNCTIONS
								, MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING_ILD
								, MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_DATA
								, MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_ILD
						)) {
							productsWithContractDiscountOnly.add(product.getPublicName().replace("&", "&amp;"));
						}
					}
				}
			}

//        	for (Product product : contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE.getKey()).getProducts()) {
//        		products.add(product.getPublicName());
//        	}


			productsWithContractDiscountOnly.add("Mobilt Bredbånd 500MB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 2GB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 10GB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 50GB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 50GB 24 MD");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 200GB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 500GB");
			productsWithContractDiscountOnly.add("Mobilt Bredbånd 1000MB");

        	html.set("products_with_contract_discount", productsWithContractDiscountOnly);
        	int x = productsWithContractDiscountOnly.size() / 3;

			List<String> productsWithContractDiscountOnlyCol1 = new ArrayList<>();
			List<String> productsWithContractDiscountOnlyCol2 = new ArrayList<>();
			List<String> productsWithContractDiscountOnlyCol3 = new ArrayList<>();
			for (int i = 0; i < productsWithContractDiscountOnly.size(); i++) {
				if (i < x) {
					productsWithContractDiscountOnlyCol1.add(productsWithContractDiscountOnly.get(i));
				} else if (i < x*2) {
					productsWithContractDiscountOnlyCol2.add(productsWithContractDiscountOnly.get(i));
				} else {
					productsWithContractDiscountOnlyCol3.add(productsWithContractDiscountOnly.get(i));
				}
			}
			html.set("products_with_contract_discount_col1", productsWithContractDiscountOnlyCol1);
			html.set("products_with_contract_discount_col2", productsWithContractDiscountOnlyCol2);
			html.set("products_with_contract_discount_col3", productsWithContractDiscountOnlyCol3);
        }
        
        {
        	List<CampaignProductOrBundle> campaignProductOrBundles = new ArrayList<>();
        	
			if (!"Ingen kampagne".equals(contract.getCampaigns().get(0).getName())) {
				OrderLineCount count = new OrderLineCount();
				count.setCountNew(1);
				for (OrderLine orderLine : contract.getOrderLines()) {
					ProductBundle bundle = orderLine.getBundle();
					if (bundle != null && orderLine.getTotalCount() > 0) {
						Amounts amountsWithCampaignDiscount 	= bundle.getAmounts(count, false, true, true, contract);
						Amounts amountsWithoutCampaignDiscount 	= bundle.getAmounts(count, false, false, true, contract);
						if (amountsWithCampaignDiscount.getRecurringFee() < amountsWithoutCampaignDiscount.getRecurringFee()) {
							Product mainProduct = null;
							for (BundleProductRelation productRelation : bundle.getProducts()) {
								if ((productRelation.getProduct() != null) &&
										(((MobileProduct) productRelation.getProduct()).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE) ||
//										 ((MobileProduct) productRelation.getProduct()).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED) ||
										 ((MobileProduct) productRelation.getProduct()).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE))) {
									mainProduct = productRelation.getProduct();
									break;
								}
							}
							if (mainProduct != null) {
								campaignProductOrBundles.add(new CampaignProductOrBundle(mainProduct.getProductId(), bundle.getPublicName(), Amounts.getFormattedWithDecimals(amountsWithCampaignDiscount.getRecurringFee())));
								productsWithContractDiscountOnly.remove(bundle.getPublicName());
							}
						}
					}
				}
				for (OrderLine orderLine : contract.getOrderLines()) {
					Product product = orderLine.getProduct();
					if (product != null && orderLine.getTotalCount() > 0) {
						if (((MobileProduct) product).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE) ||
//							((MobileProduct) product).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED) ||
								((MobileProduct) product).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE)) {
							Amounts amountsWithCampaignDiscount 	= product.getAmounts(count, true, false, contract);
							Amounts amountsWithoutCampaignDiscount 	= product.getAmounts(count, false, false, contract);
							if (amountsWithCampaignDiscount.getRecurringFee() < amountsWithoutCampaignDiscount.getRecurringFee()) {
								campaignProductOrBundles.add(new CampaignProductOrBundle(product.getProductId(), product.getPublicName(), Amounts.getFormattedWithDecimals(amountsWithCampaignDiscount.getRecurringFee())));
							}
						}
					}
				}
			}
        	html.set("products_with_campaign_discount", campaignProductOrBundles);
        	html.set("is_products_with_campaign_discount", campaignProductOrBundles.size() > 0);
        }
	}

	@Override
	protected String getTitle() {
//		if (BusinessAreas.TDC_WORKS == MobileSession.get().getContract().getBusinessArea().getBusinessAreaId()) {
//			return "TDC Works Kontrakt Accept";
//		}
		return "TDC ERHVERV RABATAFTALE";
	}

	@Override
	protected String getTemplateName() {
		return "rammeaftale_og_bilag";
	}
	
	@Data
	@AllArgsConstructor
	class CampaignProductOrBundle {
		String id;
		String name;
		String price;
	}
}
