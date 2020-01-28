package dk.jyskit.salescloud.application.pages.contractsummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.util.collections.MicroMap;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.CountProductOrBundleComparator.Criteria;
import lombok.AllArgsConstructor;

import static dk.jyskit.salescloud.application.model.FeeCategory.ONETIME_FEE;
import static dk.jyskit.salescloud.application.model.FeeCategory.RECURRING_FEE;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS_INSTALLATION;

@Slf4j
public class OfferReportDataSource {
	public enum LineType {header, space, columns, total, discount, campaign_comment, single_column};
	public enum SummaryLineType {header, header_indent, amount, amount_indent, space};
	public enum WifiLocationLineType {location_name, table_header, product, space};

	public Data getData(MobileContract contract, boolean dataForPartner) {
		Data data = new Data();
		
		data.addLine(LineType.header, null, "Detaljeret oversigt over drift pr. måned", null, null, RECURRING_FEE);
		data.addLine(LineType.header, null, "Detaljeret oversigt over oprettelse", null, null, FeeCategory.ONETIME_FEE, FeeCategory.NON_RECURRING_FEE);
		data.addLine(LineType.header, null, "Detaljeret oversigt over installation", null, null, FeeCategory.INSTALLATION_FEE);
		
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = contract.getProductOrBundleCountInOrderLines(false, dataForPartner);

		for (FeeCategory feeCategory : FeeCategory.values()) {
			data.addLine(LineType.space, null, "", null, null, feeCategory);
			data.addLine(LineType.columns, "Stk.", "Navn", "Pris/stk","Beløb", feeCategory);
			
			List<CountProductOrBundleAmounts> sortedCountProductOrBundleAmounts = new ArrayList(productOrBundleToCount.values());
			Collections.sort(sortedCountProductOrBundleAmounts, new CountProductOrBundleComparator(Criteria.offer, feeCategory));

			for (CountProductOrBundleAmounts countTextAmount : sortedCountProductOrBundleAmounts) {
				MobileProduct product = countTextAmount.getProductSafely();
				long sum = countTextAmount.getAmountsAfterCampaignAndContractDiscounts().sum(feeCategory);

				if (product == null) {
					// Bundle
					MobileProductBundle bundle = countTextAmount.getProductBundle();

					if (bundle.getBundleType().equals(MobileProductBundleEnum.XDSL_BUNDLE)) {
						if (ONETIME_FEE.equals(feeCategory)) {		// Only add once!
							data.addXdslBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
						}
					} else if (bundle.getBundleType().equals(MobileProductBundleEnum.WIFI_BUNDLE)) {
						if (ONETIME_FEE.equals(feeCategory)) {		// Only add once!
							data.addWiFiBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
						}
					} else if (bundle.getBundleType().equals(MobileProductBundleEnum.FIBER_BUNDLE)) {
						if (ONETIME_FEE.equals(feeCategory)) {		// Only add once!
							data.addFiberBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
						}
					} else if (bundle.getBundleType().equals(MobileProductBundleEnum.HARDWARE_BUNDLE)) {
						if (dataForPartner) {
							if (ONETIME_FEE.equals(feeCategory)) {		// Only add once!
								data.addHardwareBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
							}
						} else {
							continue;
						}
					} else if (bundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
						if (ONETIME_FEE.equals(feeCategory)) {
							data.addMobileBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
						}
					} else {
						// Is this ok?:
						if (ONETIME_FEE.equals(feeCategory)) {
							data.addMobileBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
						}
					}

					if (sum == 0) {
						continue;
					}

					// --- By now one of the data.addXxx method must have been called. If we fail to do that, the
					// line may get out in the offer, but the amount won't be added.

					if (!dataForPartner && bundle.getBundleType().equals(MobileProductBundleEnum.HARDWARE_BUNDLE)) {
						log.info("ignoring hardware bundles for TDC");
					} else {
						if (feeCategory.equals(FeeCategory.INSTALLATION_FEE) && !(dataForPartner ? bundle.isInstallationHandledByTDCErhvervscenter() : bundle.isInstallationHandledByTDC())) {
							log.info("ignoring");
						} else {
							String text = bundle.getTextInOffer();
							if (StringUtils.isEmpty(text)) {
								text = bundle.getPublicName();
							}
							int count = countTextAmount.getCount().getCountForFeeCategory(feeCategory);
							if ((bundle.getDiscountAmounts().sum(feeCategory) > 0) &&
									(bundle.isExtraRowInOffer()) && (!StringUtils.isEmpty(bundle.getExtraRowInOfferText()))) {
								long amount = countTextAmount.getBaseAmountsWithContractDiscountsDeducted().sum(feeCategory);
								data.addLine(LineType.columns, "" + count, bundle.getPublicName(),
										Amounts.getFormattedWithDecimals(amount/count) + " kr.",
										Amounts.getFormattedWithDecimals(amount) + " kr.",
										feeCategory);
								amount = -countTextAmount.getCampaignDiscountsWithContractDiscountsDeducted().sum(feeCategory);
								data.addLine(LineType.columns, "" + count, bundle.getExtraRowInOfferText(),
										Amounts.getFormattedWithDecimals(amount/count) + " kr.",
										Amounts.getFormattedWithDecimals(amount) + " kr.",
										feeCategory);
							} else {
								data.addLine(LineType.columns, "" + count, text,
										Amounts.getFormattedWithDecimals(sum/count) + " kr.",
										Amounts.getFormattedWithDecimals(sum) + " kr.",
										feeCategory);
							}
						}
					}
				} else {
					// Product
					MobileProductGroup group = (MobileProductGroup) product.getProductGroup();
					CampaignProductRelation campaignProductRelation = ((MobileContract) contract).getCampaignProductRelation(product);

					LocationBundleData location = null;
					try {
						location = contract.getLocationBundles().get(countTextAmount.getSubIndex());
					} catch (Exception ex) {
						log.info("Index out of range for product: " + product.getPublicName());
					}

					if (MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE.getKey().equals(countTextAmount.getProduct().getProductGroup().getUniqueName()) ||
							MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION.getKey().equals(countTextAmount.getProduct().getProductGroup().getUniqueName())) {
						// ignore partner installation products
						continue;
					} else if (group.isOfType(PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC, PRODUCT_GROUP_LOCATIONS_HARDWARE_IP)) {
						if (location.isTDCHardwareProvider() && !dataForPartner) {
							if (ONETIME_FEE.equals(feeCategory)) {
								data.addHardwareBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
							}
						} else {
							if ("Forsendelsesgebyr".equals(countTextAmount.getProduct().getPublicName())) {
								data.addHardwareBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
							} else {
								continue;
							}
						}
					} else if (PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.getKey().equals(group.getUniqueName()) && dataForPartner) {
						// ignore switches if not TDC output
						continue;
					} else if (PRODUCT_GROUP_LOCATIONS_INSTALLATION.getKey().equals(group.getUniqueName())) {
						if (location.isTDCInstallationProvider() && !dataForPartner) {
							if (ONETIME_FEE.equals(feeCategory)) {
								if (location.getAccessType() == AccessTypeEnum.XDSL.getId()) {
									data.addXdslBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
								} else if ((location.getAccessType() == AccessTypeEnum.FIBER.getId()) || (location.getAccessType() == AccessTypeEnum.FIBER_PLUS.getId())) {
									data.addFiberBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
								} else {
									data.addWiFiBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
								}
							}
						} else {
							// ignore location installation products
							continue;
						}
					} else {
						if (product.isExtraProduct()) {
							if (ONETIME_FEE.equals(feeCategory)) {
								// Handle extra products specially in order to be able to show them explicitly on last page of offer
								String s = product.getProductId().substring(MobileProduct.PRODUCT_EXTRA_PREFIX.length());
								data.addAmounts(countTextAmount.getAmounts(), Integer.valueOf(s) - 1, product.getPublicName());
							}
						} else {
							if (ONETIME_FEE.equals(feeCategory)) {
								if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name().startsWith(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE.name() + "_")) {
									data.addWiFiBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
								} else {
									data.addMobileBundlesAmounts(countTextAmount.getAmountsAfterCampaignAndContractDiscounts());
								}
							}
						}
					}

					if (feeCategory.equals(FeeCategory.INSTALLATION_FEE) && !(dataForPartner ? product.isInstallationHandledByTDCErhvervscenter() : product.isInstallationHandledByTDC())) {
						continue;
					}
					if (sum == 0) {
						if ((FeeCategory.ONETIME_FEE.equals(feeCategory) && StringUtils.contains(countTextAmount.getProduct().getFlags(), "one_time_amount_in_offer")) ||
										(FeeCategory.INSTALLATION_FEE.equals(feeCategory) && StringUtils.contains(countTextAmount.getProduct().getFlags(), "installation_amount_in_offer")) ||
										(RECURRING_FEE.equals(feeCategory) && StringUtils.contains(countTextAmount.getProduct().getFlags(), "recurring_amount_in_offer"))) {
							// Output these even if amounts are zero
						} else {
							continue;
						}
					}

					// --- By now one of the data.addXxx method must have been called. If we fail to do that, the
					// line may get out in the offer, but the amount won't be added.

					String text = countTextAmount.getProduct().getPublicName();
					if ((campaignProductRelation != null) && (!StringUtils.isEmpty(campaignProductRelation.getOutputTextOverride()))) {
						text = campaignProductRelation.getOutputTextOverride();
					}
					int count = countTextAmount.getCount().getCountForFeeCategory(feeCategory);
					if ((campaignProductRelation != null) && (campaignProductRelation.getCampaignDiscountAmounts().sum(feeCategory) > 0) &&
							(campaignProductRelation.isExtraRowInOffer()) && (!StringUtils.isEmpty(campaignProductRelation.getExtraRowInOfferText()))) {
						long amount = countTextAmount.getBaseAmountsWithContractDiscountsDeducted().sum(feeCategory);
						data.addLine(LineType.columns, "" + count, text,
								Amounts.getFormattedWithDecimals(amount/count) + " kr.",
								Amounts.getFormattedWithDecimals(amount) + " kr.",
								feeCategory);
						amount = -countTextAmount.getCampaignDiscountsWithContractDiscountsDeducted().sum(feeCategory);
						data.addLine(LineType.columns, "" + count, campaignProductRelation.getExtraRowInOfferText(),
								Amounts.getFormattedWithDecimals(amount/count) + " kr.",
								Amounts.getFormattedWithDecimals(amount) + " kr.",
								feeCategory);
					} else {
						data.addLine(LineType.columns, "" + count, text,
								Amounts.getFormattedWithDecimals(sum/count) + " kr.",
								Amounts.getFormattedWithDecimals(sum) + " kr.",
								feeCategory);
					}

				}
			}
			
			switch (feeCategory) {
			case RECURRING_FEE:
				data.addLine(LineType.total, "", "Samlet drift pr. måned", "", Amounts.getFormattedWithDecimals(data.getTotal().getRecurringFee()) + " kr.", feeCategory);
				break;
			case ONETIME_FEE:
				data.addLine(LineType.total, "", "Samlet oprettelsespris", "", Amounts.getFormattedWithDecimals(data.getTotal().getOneTimeFee()) + " kr.", feeCategory);
				break;
			case INSTALLATION_FEE:
				data.addLine(LineType.total, "", "Samlet installationspris", "", Amounts.getFormattedWithDecimals(data.getTotal().getInstallationFee()) + " kr.", feeCategory);
				break;
			case NON_RECURRING_FEE:
				data.addLine(LineType.total, "", "Samlet oprettelsespris", "", Amounts.getFormattedWithDecimals(data.getTotal().getNonRecurringFees()) + " kr.", feeCategory);
				break;
			}
			data.addLine(LineType.space, null, "", "", null, feeCategory);

			if (contract.getDiscountSchemes().size() > 0 ) {
				if (contract.getDiscountSchemes().size() > 1 ) {
					data.addLine(LineType.discount, "", "Prisen er inkl. rabat", "", "", feeCategory);
				} else {
					Amounts discountPercentages = null;
					DiscountScheme discountScheme = contract.getDiscountSchemes().get(0);
					if (discountScheme instanceof FixedDiscount) {
						discountPercentages = ((FixedDiscount) discountScheme).getDiscountPercentages();
					} else if (discountScheme instanceof SwitchboardIpsaDiscountScheme) {
						discountPercentages = ((SwitchboardIpsaDiscountScheme) discountScheme).getDiscountPercentages();
					}
					
					if (discountPercentages != null) {
						long maxDiscountPercentage = 0;
						long[] percentages = discountPercentages.getAmounts(feeCategory);
						for (int i = 0; i < percentages.length; i++) {
							maxDiscountPercentage = Math.max(maxDiscountPercentage, percentages[i]);
						}
						if (maxDiscountPercentage > 0) {
							data.addLine(LineType.discount, "", "Prisen er inkl. rabat", "", "", feeCategory);
//							data.addLine(LineType.discount, "", "Prisen er inkl. " + Amounts.getFormattedWithDecimals(maxDiscountPercentage) + " % rabat", "", feeCategory);
//							if (FeeCategory.RECURRING_FEE.equals(feeCategory)) {
//								if ((contract.getCampaigns().get(0).getFromDate() != null) || (contract.getCampaigns().get(0).getToDate() != null)) {
//									data.addLine(LineType.campaign_comment, "", "OBS: Kampagnerabat fratrækkes inden øvrige rabatter.", "", feeCategory);
//									data.addLine(LineType.campaign_comment, "", "Tilbuddet er derfor præsenteret med netto kampagnerabat.", "", feeCategory);
//									data.addLine(LineType.space, null, "", null, feeCategory);
//								}
//							}
						}
					}
				}
			}

			if (contract.getBusinessArea().isOnePlus()) {
				if (RECURRING_FEE == feeCategory) {
					data.addLine(LineType.single_column, "", "Alle priser er ekskl. moms", "", "", feeCategory);
				}
			} else {
				data.addLine(LineType.single_column, "", "Alle priser er ekskl. moms", "", "", feeCategory);
			}
		}
		
		String mobilPakkeProduktNavn;
		if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS) {
			mobilPakkeProduktNavn = "TDC Works";
		} else if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER) {
			mobilPakkeProduktNavn = "TDC Erhverv Fiber Plus";
		} else if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			mobilPakkeProduktNavn = "TDC Erhverv Fiber";
		} else if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.ONE_PLUS) {
			mobilPakkeProduktNavn = "TDC Erhverv One+";
		} else {
			mobilPakkeProduktNavn = "TDC Mobilpakker";
		}
		
		String tilbuddetEllerPrisoverslaget = "tilbuddet";
		if (BusinessAreas.WIFI == contract.getBusinessArea().getBusinessAreaId()) {
			tilbuddetEllerPrisoverslaget = "prisoverslaget";
		}
		
        List<SummaryLine> summaryLines = new ArrayList<>();
		if (contract.getBusinessArea().hasFeature(FeatureType.RECURRING_FEE_SPLIT)) {
			{
				boolean first = true;
				summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede oprettelsesomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getOneTimeFeeFormatted()));
				if (!contract.getBusinessArea().isOnePlus()) {
					if (!(contract.getBusinessArea().isOffice() || contract.getBusinessArea().isFiberErhverv())) {
						if (data.getMobileBundlesAmounts().getOneTimeFee() > 0) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							// TODO: TDC Erhverv Omstilling?
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), mobilPakkeProduktNavn + ":", data.getMobileBundlesAmounts().getOneTimeFeeFormatted()));
						}
					}
					if (data.getXdslBundlesAmounts().getOneTimeFee() > 0) {
						if (first) {
							first = false;
							summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
						}
						summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "xDSL pakker:", data.getXdslBundlesAmounts().getOneTimeFeeFormatted()));
					}
					if (data.getWiFiBundlesAmounts().getOneTimeFee() > 0) {
						if (first) {
							first = false;
							summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
						}
						summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Wi-Fi pakker:", data.getWiFiBundlesAmounts().getOneTimeFeeFormatted()));
					}
					if (data.getFiberBundlesAmounts().getOneTimeFee() > 0) {
						if (first) {
							first = false;
							summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
						}
						summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Fiber pakker:", data.getFiberBundlesAmounts().getOneTimeFeeFormatted()));
					}
					for (int i = 0; i < 3; i++) {
						if (data.getExtraTexts()[i] != null) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), data.getExtraTexts()[i] + ":", data.getExtraAmounts()[i].getOneTimeFeeFormatted()));
						}
					}
				}
			}
			
			if (!(contract.getBusinessArea().isOnePlus() || (BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) ||
					(BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()))) {
				summaryLines.add(new SummaryLine(SummaryLineType.space.toString(), "", ""));
			}
			
			if (BusinessAreas.FIBER != contract.getBusinessArea().getBusinessAreaId() &&
				BusinessAreas.FIBER_ERHVERV != contract.getBusinessArea().getBusinessAreaId()) {
				if (contract.getBusinessArea().isOnePlus()) {
					if (data.getTotal().getInstallationFee() > 0) {
						summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede installationsomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getInstallationFeeFormatted()));
					}
				} else if ((BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) ||
						(BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId())) {
					summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede installationsomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getInstallationFeeFormatted()));
				} else {
					boolean first = true;
					summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede installationsomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getInstallationFeeFormatted()));

					if (!MobileSession.get().isBusinessAreaOnePlus() || contract.isTdcInstallation()) {
						if (data.getMobileBundlesAmounts().getInstallationFee() > 0) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), mobilPakkeProduktNavn + ":", data.getMobileBundlesAmounts().getInstallationFeeFormatted()));
						}
						if (data.getXdslBundlesAmounts().getInstallationFee() > 0) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "xDSL pakker:", data.getXdslBundlesAmounts().getInstallationFeeFormatted()));
						}
						if (data.getWiFiBundlesAmounts().getInstallationFee() > 0) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Wi-Fi pakker:", data.getWiFiBundlesAmounts().getInstallationFeeFormatted()));
						}
						if (data.getFiberBundlesAmounts().getInstallationFee() > 0) {
							if (first) {
								first = false;
								summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
							}
							summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Fiber pakker:", data.getFiberBundlesAmounts().getInstallationFeeFormatted()));
						}
						for (int i = 0; i < 3; i++) {
							if (data.getExtraTexts()[i] != null) {
								if (first) {
									first = false;
									summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
								}
								summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), data.getExtraTexts()[i] + ":", data.getExtraAmounts()[i].getInstallationFeeFormatted()));
							}
						}
					}
				}
			}
		} else {
			if ((BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) ||
					(BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId())) {
				summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede oprettelsesomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getNonRecurringFeesFormatted()));
			} else {
				summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede oprettelsesomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getNonRecurringFeesFormatted()));
				boolean first = true;
				if (data.getMobileBundlesAmounts().getNonRecurringFees() > 0) {
					if (first) {
						first = false;
						summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
					}
					summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), mobilPakkeProduktNavn + ":", data.getMobileBundlesAmounts().getNonRecurringFeesFormatted()));
				}
				if (data.getXdslBundlesAmounts().getNonRecurringFees() > 0) {
					if (first) {
						first = false;
						summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
					}
					summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "xDSL pakker:", data.getXdslBundlesAmounts().getNonRecurringFeesFormatted()));
				}
				if (data.getWiFiBundlesAmounts().getNonRecurringFees() > 0) {
					if (first) {
						first = false;
						summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
					}
					summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Wi-Fi pakker:", data.getWiFiBundlesAmounts().getNonRecurringFeesFormatted()));
				}
				if (data.getFiberBundlesAmounts().getNonRecurringFees() > 0) {
					if (first) {
						first = false;
						summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
					}
					summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Fiber pakker:", data.getFiberBundlesAmounts().getNonRecurringFeesFormatted()));
				}
				for (int i = 0; i < 3; i++) {
					if (data.getExtraTexts()[i] != null) {
						if (first) {
							first = false;
							summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
						}
						summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), data.getExtraTexts()[i] + ":", data.getExtraAmounts()[i].getNonRecurringFeesFormatted()));
					}
				}
			}
		}
		
		if (!(contract.getBusinessArea().isOnePlus() || (BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) ||
			(BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()))) {
			summaryLines.add(new SummaryLine(SummaryLineType.space.toString(), "", ""));
		}

		if ((contract.getBusinessArea().isOnePlus() || BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) ||
				(BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId())) {
			summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede månedlige driftsomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getRecurringFeeFormatted()));
		} else {
			boolean first = true;
			summaryLines.add(new SummaryLine(SummaryLineType.amount.toString(), "Samlede månedlige driftsomkostninger jf. " + tilbuddetEllerPrisoverslaget + ":", data.getTotal().getRecurringFeeFormatted()));
			if (data.getMobileBundlesAmounts().getRecurringFee() > 0) {
				if (first) {
					first = false;
					summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
				}
				summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), mobilPakkeProduktNavn + ":", data.getMobileBundlesAmounts().getRecurringFeeFormatted()));
			}
			if (data.getXdslBundlesAmounts().getRecurringFee() > 0) {
				if (first) {
					first = false;
					summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
				}
				summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "xDSL pakker:", data.getXdslBundlesAmounts().getRecurringFeeFormatted()));
			}
			if (data.getWiFiBundlesAmounts().getRecurringFee() > 0) {
				if (first) {
					first = false;
					summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
				}
				summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Wi-Fi pakker:", data.getWiFiBundlesAmounts().getRecurringFeeFormatted()));
			}
			if (data.getFiberBundlesAmounts().getRecurringFee() > 0) {
				if (first) {
					first = false;
					summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
				}
				summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), "Fiber pakker:", data.getFiberBundlesAmounts().getRecurringFeeFormatted()));
			}
			for (int i = 0; i < 3; i++) {
				if (data.getExtraTexts()[i] != null) {
					if (first) {
						first = false;
						summaryLines.add(new SummaryLine(SummaryLineType.header_indent.toString(), "Som fordeles på:", ""));
					}
					summaryLines.add(new SummaryLine(SummaryLineType.amount_indent.toString(), data.getExtraTexts()[i] + ":", data.getExtraAmounts()[i].getRecurringFeeFormatted()));
				}
			}
		}
		data.setSummaryLines(summaryLines);
        
        List<WifiLocationLine> wifiLocationLines = new ArrayList<>();
        
		if (contract.getBusinessArea().hasFeature(FeatureType.WIFI)) {
	        for (int locationIndex = 0; locationIndex < contract.getWiFiBundles().size(); locationIndex++) {
	        	MobileSession.get().setPricingSubIndex(locationIndex);
				WiFiBundleIds location = contract.getWiFiBundles().get(locationIndex);
				wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.location_name.toString(), "", "Lokation " + (locationIndex+1) + " - " + location.getAddress()));
				wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.table_header.toString(), "Stk.", "Navn"));
				{
					Product product = contract.getBusinessArea().getProductById(location.getSiteSurveyEntityId());
					if (product != null) {
						wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.product.toString(), "1", product.getPublicName()));
					}
				}
				{
					Product product = contract.getBusinessArea().getProductById(location.getAccessPointEntityId());
					if (product != null) {
						wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.product.toString(), "" + location.getAccessPointCount(), product.getPublicName()));
					}
				}
				{
					Product product = contract.getBusinessArea().getProductById(location.getSwitchEntityId());
					if (product != null) {
						wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.product.toString(), "1", product.getPublicName()));
					}
				}
				{
					for (Product cablingProduct : contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING.getKey()).getProducts()) {
						for (OrderLine orderLine : contract.getOrderLines(cablingProduct)) {
							if (orderLine.getSubIndex() == locationIndex) {
								MicroMap<Object,CountProductOrBundleAmounts> tmpMap = new MicroMap<>();
								contract.updateDataForProduct(tmpMap, orderLine, cablingProduct, null, dataForPartner);
								CountProductOrBundleAmounts countAndAmounts = tmpMap.get(cablingProduct);
								if (!countAndAmounts.getAmounts().isAllZero()) {
									wifiLocationLines.add(new WifiLocationLine(WifiLocationLineType.product.toString(), "1", cablingProduct.getPublicName()));
								}
							}
						}
					}
				}
			}
		}
		data.setWifiLocationLines(wifiLocationLines);
		
		return data;
	}

//	private Amounts adjustInstallation(MobileProduct product, boolean partner, Amounts amounts) {
//		return amounts;
////		return product.adjustInstallation(partner, amounts);
//	}
//
//	private Amounts adjustInstallation(MobileProductBundle bundle, boolean partner, Amounts amounts) {
//		return amounts;
////		return bundle.adjustInstallation(partner, amounts);
//	}

	@lombok.Data
	public class Data {
		private Map<FeeCategory, List<DataElement>> categoryToLines = new HashMap<>();
		private Amounts total = new Amounts();
//		private Amounts totalExcludingExtras = new Amounts();
		private Amounts mobileBundlesAmounts = new Amounts();
		private Amounts xdslBundlesAmounts = new Amounts();
		private Amounts wiFiBundlesAmounts = new Amounts();
		private Amounts fiberBundlesAmounts = new Amounts();
		private Amounts hardwareBundlesAmounts = new Amounts();
		private Amounts[] extraAmounts = new Amounts[3];
		private String[] extraTexts = new String[3];
		private List<SummaryLine> summaryLines = new ArrayList<>();
		private List<WifiLocationLine> wifiLocationLines = new ArrayList<>();
		
		public Data() {
			for (FeeCategory feeCategory : FeeCategory.values()) {
				categoryToLines.put(feeCategory, new ArrayList());
			}
		}

		public void addMobileBundlesAmounts(Amounts amounts) {
//			totalExcludingExtras.add(amounts);
			mobileBundlesAmounts.add(amounts);
			total.add(amounts);
		}

		public void addXdslBundlesAmounts(Amounts amounts) {
//			totalExcludingExtras.add(amounts);
			xdslBundlesAmounts.add(amounts);
			total.add(amounts);
		}

		public void addWiFiBundlesAmounts(Amounts amounts) {
			wiFiBundlesAmounts.add(amounts);
			total.add(amounts);
		}

		public void addFiberBundlesAmounts(Amounts amounts) {
			fiberBundlesAmounts.add(amounts);
			total.add(amounts);
		}

		public void addHardwareBundlesAmounts(Amounts amounts) {
			hardwareBundlesAmounts.add(amounts);
			total.add(amounts);
		}

//		public void subtractDiscountAmounts(Amounts amounts) {
//			totalExcludingExtras.subtract(amounts);
//			total.subtract(amounts);
//		}

		public void addAmounts(Amounts amounts, Integer extraIndex, String extraText) {
			extraAmounts[extraIndex] = amounts;
			extraTexts[extraIndex] = extraText;
			total.add(amounts);
		}

		public void addLine(LineType type, String countColumn, String text, String amountPerItemColumn, String amountColumn, FeeCategory ... feeCategories) {
			for (FeeCategory feeCategory: feeCategories) {
				categoryToLines.get(feeCategory).add(new DataElement(type, countColumn, text, amountPerItemColumn, amountColumn));
			}
		}

		public long getTotal(FeeCategory feeCategory) {
			return total.getAmounts()[feeCategory.getFromIndex()];
		}

		public boolean isTotalZero(FeeCategory feeCategory) {
			return (0l == getTotal(feeCategory));
		}
	}
	
	@lombok.Data
	@AllArgsConstructor
	public class DataElement {
		LineType type;
		String countColumn;
		String label;
		String amountPerItemColumn;
		String amountColumn;

		public Line asLine() {
			return new Line(type.toString(),
					(countColumn == null ? "" : String.valueOf(countColumn)),
					label.replace("&", "&amp;"),
					(amountPerItemColumn == null ? "" : amountPerItemColumn),
					(amountColumn == null ? "" : amountColumn));
		}
	}
	
	@lombok.Data
	@AllArgsConstructor
	public static class Line {
		String type;
		String count;
		String label;
		String amount_per_item;
		String amount;
	}
	
	@lombok.Data
	@AllArgsConstructor
	public class SummaryLine {
		String type;
		String label;
		String amount;
	}
	
	@lombok.Data
	@AllArgsConstructor
	public class WifiLocationLine {
		String type;
		String count;
		String text;
	}
}

