package dk.jyskit.salescloud.application.extensions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.ContractFinansialInfo;
import dk.jyskit.salescloud.application.model.ContractStatusEnum;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SwitchboardIpsaDiscountScheme;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContractsSpreadsheet implements Provider<Workbook>, Serializable{
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM yyyy");

	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("Kontrakter");
		s.incRow(0);
		
		List<Col> cols = getCols();
		
	    addHeaderRow(s, IndexedColors.DARK_BLUE, cols);
	    
	    MobileContract contractInSession = MobileSession.get().getContract();
	    MobileContract contract = null;
		log.info("---------- start ----------");
//		for (Contract c : Lookup.lookup(ContractDao.class).findNewerThan(DateUtils.addMonths(new Date(), -6))) {
	    for (Contract c : Lookup.lookup(ContractDao.class).findByYearMonth(MobileSession.get().getDumpYear(), MobileSession.get().getDumpMonth())) {
		    try {
		    	contract = (MobileContract) c;
				log.info("Contract: " + contract.getId());
			    MobileSession.get().setContract(contract);
		    	ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(true, false, false);	// CHECKMIG
		    	if (contract.getBusinessArea().isActive()) {
		    		addRow(s, null, contract, contractFinansialInfo, cols);
		    	}
				log.info("Contract: " + contract.getId() + " - added");
		    } catch (Exception e) {
		    	log.error("Some problem with contract " + contract.getId(), e);
			}
	    }
		log.info("---------- done ----------");
		MobileSession.get().updateMonthToDump();
	    MobileSession.get().setContract(contractInSession);
		return s.getWorkbook();
	}

	private void addRow(Spreadsheet s, IndexedColors color, MobileContract contract, ContractFinansialInfo contractFinansialInfo, List<Col> cols) {
	    for (Col col : cols) {
	    	Object value = col.getValue(contract, contractFinansialInfo);
	    	if (color == null) {
	    		s.addValue(value);
//	    		if (value instanceof Double) {
//	    			s.addDouble((Double) value);
//	    		} else {
//	    			s.addText((String) value);
//	    		}
	    	} else {
			    s.addValueAndColor(value, color);
	    	}
		}
	    s.incRow();
	}

	private void addHeaderRow(Spreadsheet s, IndexedColors color, List<Col> cols) {
	    for (Col col : cols) {
	    	if (color == null) {
			    s.addValue(col.getHeader());
	    	} else {
	    		s.addColoredValue(col.getHeader(), color);
	    	}
		}
	    s.incRow();
	}

	abstract class Col {
		public String header;

		String getHeader() {
			return header;
		}
		abstract Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo);
		
		public Col(String header) {
			this.header = header;
		}
	}
	
	private List<Col> getCols() {
		List<Col> cols = new ArrayList<>(50);
		cols.add(new Col("Dato") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getCreationDate() == null) {
					return "";
				} else {
					return dateFormat.format(contract.getCreationDate());
				}
			}
		});
		cols.add(new Col("Status") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getStatus() == null) {
					return "";
				} else {
					return contract.getStatus().getText();
				}
			}
		});
		cols.add(new Col("Slettet") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (((MobileContract) contract).getDeleted() != null && ((MobileContract) contract).getDeleted().booleanValue()) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Forretningsområde") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getBusinessArea().getName();
			}
		});
		cols.add(new Col("Sælger afd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getSalesperson().getDivision();
			}
		});
		cols.add(new Col("Sælger type") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				String s = contract.getSalesperson().toString();
				if (s.length() > 9) {
					s = s.substring("Sælger (".length());
					s = s.substring(0, s.length() - 1);
				}
				return s;
			}
		});
		cols.add(new Col("Sælger email") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getSalesperson().getUser().getEmail();
			}
		});
		cols.add(new Col("Sælger navn") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getSalesperson().getUser().getFullName();
			}
		});
		cols.add(new Col("Kunde navn") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getCompanyName();
			}
		});
		cols.add(new Col("Kunde CVR") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getCompanyId();
			}
		});
		cols.add(new Col("Kunde adresse") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getAddress();
			}
		});
		cols.add(new Col("Kunde postnr.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getZipCode();
			}
		});
		cols.add(new Col("Kunde by") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getCity();
			}
		});
		cols.add(new Col("Kunde tlf.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getPhone();
			}
		});
		cols.add(new Col("Kunde email") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getEmail();
			}
		});
		cols.add(new Col("Kunde kontaktperson") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCustomer().getName();
			}
		});
		cols.add(new Col("Kampagne") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getCampaigns().get(0).getName();
			}
		});
		cols.add(new Col("Antal mobilpakker") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				int count = 0;
				for (OrderLine orderLine : contract.getOrderLines()) {
					MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
					if (bundle != null) {
						if (MobileProductBundleEnum.MOBILE_BUNDLE.equals(bundle.getBundleType())) {
							count += orderLine.getTotalCount();
						}
					}
				}
				return Double.valueOf(count);
			}
		});
		
//		for (BusinessArea businessArea : ((BusinessAreaDao) Lookup.lookup(BusinessAreaDao.class)).findAll()) {
//			final String mixColName = "Mix (" + businessArea.getTypeId() + ")";
//			cols.add(new Col(mixColName) {
//				Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
//					MutableInt count = contractFinansialInfo.getBundleNameToSubscriptionCount().get(mixColName);
//					if (count == null) {
//						return "";
//					} else {
//						return count.doubleValue();
//					}
//				}
//			});
//			
//			Campaign campaign = businessArea.getCampaigns().get(0);
//			for (ProductBundle bundle : campaign.getProductBundles()) {
//				final String colName = bundle.getPublicName() + " (" + businessArea.getTypeId() + ")";
//				cols.add(new Col(colName) {
//					Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
//						MutableInt count = contractFinansialInfo.getBundleNameToSubscriptionCount().get(colName);
//						if (count == null) {
//							return "";
//						} else {
//							return count.doubleValue();
//						}
//					}
//				});
//			}
//		}
		
//		cols.add(new Col(SwitchboardInitializer.BUNDLE_OMSTILLING) {
//			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
//				int count = 0;
//				for (OrderLine orderLine : contract.getOrderLines()) {
//					if (orderLine.getTotalCount() > 0) {
//						MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
//						if (bundle != null) {
//							if (MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(bundle.getBundleType()) && (SwitchboardInitializer.BUNDLE_OMSTILLING.equals(bundle.getPublicName()))) {
//								count++;
//							}
//						}
//					}
//				}
//				return "" + count;
//			}
//		});
//		
//		cols.add(new Col(SwitchboardInitializer.BUNDLE_OMSTILLING_EKSTRA) {
//			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
//				int count = 0;
//				for (OrderLine orderLine : contract.getOrderLines()) {
//					if (orderLine.getTotalCount() > 0) {
//						MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
//						if (bundle != null) {
//							if (MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(bundle.getBundleType()) && (SwitchboardInitializer.BUNDLE_OMSTILLING_EKSTRA.equals(bundle.getPublicName()))) {
//								count++;
//							}
//						}
//					}
//				}
//				return "" + count;
//			}
//		});
		cols.add(new Col("Årlig IPSA rabatsum") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getDiscountScheme(SwitchboardIpsaDiscountScheme.class) != null) {
					return Double.valueOf(contractFinansialInfo.getIpsaSumPrYear() / 100);
				} else {
					return "n/a";
				}
			}
		});
		cols.add(new Col("Årlig TDC Works kontraktsum") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT) || contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
					return contractFinansialInfo.getRabataftaleKontraktsum() / 100;
				} else {
					return "n/a";
				}
			}
		});
		cols.add(new Col("Årlig mobil kontraktsum") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getMobileSumPrYear() / 100);
			}
		});
		cols.add(new Col("Årlig GKS sum") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getGksSumPrYear() / 100);
			}
		});
		cols.add(new Col("Oprettelse af løsningen") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractTotalsBeforeDiscounts().getOneTimeFee() / 100);
			}
		});
		cols.add(new Col("Installation af løsningen") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractTotalsBeforeDiscounts().getInstallationFee() / 100);
			}
		});
		cols.add(new Col("Ialt før rabat") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractTotalsBeforeDiscounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Rabat (kampagne)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getCampaignDiscount() / 100);
			}
		});
		cols.add(new Col("Rabat (kontrakt)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractDiscount() / 100);
			}
		});
		cols.add(new Col("Etableringspris ialt") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf((contractFinansialInfo.getContractTotalsBeforeDiscounts().getRecurringFee() - contractFinansialInfo.getTotalDiscount()) / 100);
			}
		});
		cols.add(new Col("TDC Omstilling pr. mnd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD)) {
					return Double.valueOf(contractFinansialInfo.getSwitchboardTotals().getRecurringFee() / 100);
				} else {
					return "n/a";
				}
			}
		});
		cols.add(new Col("TDC Omstilling pr. mnd. (tilvalg)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD)) {
					return Double.valueOf(contractFinansialInfo.getSwitchboardAddonAmounts().getRecurringFee() / 100);
				} else {
					return "n/a";
				}
			}
		});
		cols.add(new Col("Antal abonnementer") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getSubscriptionCount());
			}
		});
		cols.add(new Col("Abonnementer pr. mnd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getSubscriptionTotals().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Roaming tilvalg pr. mnd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getRoamingAmounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Funktionstilvalg pr. mnd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getFunctionsAmounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Ekstra produkter pr. mnd.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getExtraProductsAmounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Ialt pr. mnd. før rabat") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractTotalsBeforeDiscounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Antal abonnementer") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getSubscriptionTotals().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Rabat (kampagne)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getCampaignDiscounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Rabat (kontrakt)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getContractDiscounts().getRecurringFee() / 100);
			}
		});
		cols.add(new Col("Ialt pr. mnd. efter rabat") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf((contractFinansialInfo.getContractTotalsBeforeDiscounts().getRecurringFee()
								- contractFinansialInfo.getContractDiscounts().getRecurringFee()
								- contractFinansialInfo.getCampaignDiscounts().getRecurringFee()) / 100);
			}
		});
		cols.add(new Col("Årlig mobil kontraktsum (efter kampagnerabat)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getMobileSumPrYear() / 100);
			}
		});
		cols.add(new Col("Antal rabatmekanismer") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getNoOfDiscountSchemes());
			}
		});
		cols.add(new Col("Fast rabat (%)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getFixedDiscountPct());
			}
		});
		cols.add(new Col("IPSA rabat (%)") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return Double.valueOf(contractFinansialInfo.getSwitchboardIpsaDiscountPct());
			}
		});
		cols.add(new Col("TM nummer") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getTmNumber() == null ? "" : contract.getTmNumber();
			}
		});
		cols.add(new Col("Dato for tastning af TM nummer") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				return contract.getTmNumberDate() == null ? "" : dateFormat.format(contract.getTmNumberDate());
			}
		});
		cols.add(new Col("Segment") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getSegment() == null) {
					return "";
				} else {
					return contract.getSegment().getName();
				}
			}
		});
		cols.add(new Col("Provision") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
			    MutableLong stykProvisions	= new MutableLong();
			    MutableLong satsProvisions	= new MutableLong();
			    MutableLong totalProvisions	= new MutableLong();
			    contract.calculatePartnerProvision(stykProvisions, satsProvisions, totalProvisions);
			    float factor = contract.calculatePartnerProvisionFactor();
				return Double.valueOf(Math.round(totalProvisions.longValue() * factor));
			}
		});
		cols.add(new Col("Salesforce nr.") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getSalesforceNo() == null) {
					return "";
				} else {
					return contract.getSalesforceNo();
				}
			}
		});
		
		// Office 365
		cols.add(new Col("Office 365 oprettelse/installation") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
					if (contract.getStatus().equals(ContractStatusEnum.SENT_TO_IMPLEMENTATION) || contract.getStatus().equals(ContractStatusEnum.IMPLEMENTED)) {
						return Double.valueOf(1);
					}
				}
				return Double.valueOf(0);
			}
		});
		cols.add(new Col("Pulje") {
			Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
				if (((MobileContract) contract).isPoolsMode()) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		for (BusinessArea businessArea : ((BusinessAreaDao) Lookup.lookup(BusinessAreaDao.class)).findAll()) {
			if (!businessArea.isActive()) {
				continue;
			}
			if (businessArea.getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {				
				for (Campaign campaign: businessArea.getCampaigns()) {
					if ("Ingen kampagne".equals(campaign.getName())) {
						// Licenser
						ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE.getKey());
						for (Product product: productGroup.getProducts()) {
							cols.add(new Col(product.getInternalName()) {
								Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
									if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
										for (OrderLine orderLine : contract.getOrderLines()) {
											if ((orderLine.getBundle() != null) && (orderLine.getBundle().getPublicName().equals(product.getPublicName()))) {
												return Double.valueOf(orderLine.getCountNew());
											}
										}
									}
									return Double.valueOf(0);
								}
							});
						}
						
						productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey());
						for (Product product: productGroup.getProducts()) {
							cols.add(new Col(product.getInternalName()) {
								Object getValue(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
									if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
										for (OrderLine orderLine : contract.getOrderLines()) {
											if ((orderLine.getProduct() != null) && (orderLine.getProduct().getPublicName().equals(product.getPublicName()))) {
												return Double.valueOf(orderLine.getDeferredCount().getCountNew());
											}
										}
									}
									return Double.valueOf(0);
								}
							});
						}
						break;
					}
				}
			}
		}
		
		return cols;
	}
}

/*
* 
* 
2017-11-02 14:01:05,427 ERROR d.j.s.a.m.MobileContract ~ !contractTotals.equals(contractTotalsCalculated) : 499900, 539100, 279100 != 0, 0, 0
2017-11-02 14:01:05,428 ERROR d.j.s.a.e.ContractsSpreadsheet ~ Some problem with contract 1680374java.lang.NumberFormatException: For input string: "x"
at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1241) ~[na:1.7.0_45]
at java.lang.Double.valueOf(Double.java:504) ~[na:1.7.0_45]
at dk.jyskit.salescloud.application.extensions.ContractsSpreadsheet$47.getValue(ContractsSpreadsheet.java:439) ~[ContractsSpreadsheet$47.class:na]
at dk.jyskit.salescloud.application.extensions.ContractsSpreadsheet.addRow(ContractsSpreadsheet.java:69) [ContractsSpreadsheet.class:na]
at dk.jyskit.salescloud.application.extensions.ContractsSpreadsheet.get(ContractsSpreadsheet.java:57) [ContractsSpreadsheet.class:na]
at dk.jyskit.salescloud.application.extensions.ContractsSpreadsheet.get(ContractsSpreadsheet.java:36) [ContractsSpreadsheet.class:na]
at dk.jyskit.salescloud.application.links.spreadsheets.SpreadsheetLink$1$1.write(SpreadsheetLink.java:25) [salescloud-core-1.0-SNAPSHOT.jar:na]
at org.apache.wicket.request.resource.ResourceStreamResource$1.writeData(ResourceStreamResource.java:192) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.request.resource.AbstractResource.respond(AbstractResource.java:528) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.markup.html.link.ResourceLink.onResourceRequested(ResourceLink.java:115) [wicket-core-6.19.0.jar:6.19.0]
at sun.reflect.GeneratedMethodAccessor282.invoke(Unknown Source) ~[na:na]
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.7.0_45]
at java.lang.reflect.Method.invoke(Method.java:606) ~[na:1.7.0_45]
at org.apache.wicket.RequestListenerInterface.internalInvoke(RequestListenerInterface.java:258) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.RequestListenerInterface.invoke(RequestListenerInterface.java:216) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler.invokeListener(ListenerInterfaceRequestHandler.java:243) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler.respond(ListenerInterfaceRequestHandler.java:236) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.request.cycle.RequestCycle$HandlerExecutor.respond(RequestCycle.java:890) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.request.RequestHandlerStack.execute(RequestHandlerStack.java:64) [wicket-request-6.19.0.jar:6.19.0]
at org.apache.wicket.request.cycle.RequestCycle.execute(RequestCycle.java:261) [wicket-core-6.19.0.jar:6.19.0]
at org.apache.wicket.request.cycle.RequestCycle.processRequest(RequestCycle.java:218) [wicket-core-6.19.0.jar:6.19.0]
* 
* 	    
*/


