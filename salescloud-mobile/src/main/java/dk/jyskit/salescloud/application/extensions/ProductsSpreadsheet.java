package dk.jyskit.salescloud.application.extensions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import dk.jyskit.salescloud.application.model.EntityState;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductsSpreadsheet implements Provider<Workbook>, Serializable {
	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("Produkter");
		s.incRow(0);
		
		List<ColType> cols = new ArrayList<>();
		cols.add(new EntityStateCol());
		cols.add(new BusinessAreaCol());
		cols.add(new ProductGroupCol());
		cols.add(new PublicNameCol());
		cols.add(new InternalNameCol());
		cols.add(new KvikCodeCol());
		cols.add(new ProductIdCol());
		cols.add(new OneTimeFeeCol());
		cols.add(new InstallationFeeCol());
		cols.add(new RecurringFeeCol());
		cols.add(new PaymentFrequencyCol());
		cols.add(new DefaultCountCol());
		cols.add(new MinCountCol());
		cols.add(new MaxCountCol());
		cols.add(new DiscountEligibleCol());
		cols.add(new RabataftaleDiscountEligibleCol());
		cols.add(new IPSADiscountEligibleCol());
		cols.add(new GKSCol());
		cols.add(new TDCInstallationCol());
		cols.add(new ExcludeFromConfiguratorCol());
		cols.add(new ExcludeFromProductionOutputCol());
		cols.add(new ExcludeFromOfferCol());
		cols.add(new PartnerProvisionInstallationCol());
		cols.add(new PartnerProvisionOneTimeCol());
		cols.add(new PartnerProvisionRecurringCol());
		cols.add(new VariableInstallationFeeCol());
		cols.add(new VariableRecurringFeeCol());
		cols.add(new VariableCategoryCol());
		cols.add(new VariableProductNameCol());
		cols.add(new RemarksCol());
		cols.add(new FlagsCol());
		cols.add(new CountAllSubscribersCol());
		cols.add(new SortIndexCol());
		cols.add(new OutputSortIndexCol());
		cols.add(new OfferSortIndexCol());
		
	    for (Product p : Lookup.lookup(ProductDao.class).findAll()) {
	    	for (ColType colType : cols) {
				colType.init((MobileProduct) p);
			}
		}
	    
	    addHeaderRow(s, IndexedColors.DARK_BLUE, cols);
	    
	    for (Product p : Lookup.lookup(ProductDao.class).findAll().stream().sorted(new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				if (o1.getBusinessArea().equals(o2.getBusinessArea())) {
					if (o1.getBusinessArea().equals(o2.getBusinessArea())) {
						if (o1.getProductGroup().equals(o2.getProductGroup())) {
							return o1.getProductId().compareTo(o2.getProductId());
						} else {
							return o1.getProductGroup().getName().compareTo(o2.getProductGroup().getName());
						}
					} else {
						return o1.getBusinessArea().getName().compareTo(o2.getBusinessArea().getName());
					}
				} else {
					return o1.getBusinessArea().getName().compareTo(o2.getBusinessArea().getName());
				}
			}
		}).collect(Collectors.toList())) {
	    	if (p.getBusinessArea().isActive()) {
	    		addValueRow(s, null, p, cols);
	    	}
		}
	    
		return s.getWorkbook();
	}

	private void addHeaderRow(Spreadsheet s, IndexedColors color, List<ColType> cols) {
	    for (ColType colType : cols) {
	    	if (colType.isOutputThis()) {
		    	if (color == null) {
				    s.addValue(colType.getHeader());
		    	} else {
		    		s.addColoredValue(colType.getHeader(), color);
		    	}
	    	}
		}
	    s.incRow();
	}

	private void addValueRow(Spreadsheet s, IndexedColors color, Product p, List<ColType> cols) {
	    for (ColType colType : cols) {
	    	if (colType.isOutputThis()) {
	    		try {
			    	Object value = colType.getValue((MobileProduct) p);
			    	if (color == null) {
			    		s.addValue(value);
//			    		if (value instanceof Double) {
//			    			s.addDouble((Double) value);
//			    		} else {
//			    			s.addText((String) value);
//			    		}
			    	} else {
					    s.addValueAndColor(value, color);
			    	}
				} catch (Exception e) {
					log.warn("Problem with product: " + p.getId() + " - ", e);
				}
	    	}
		}
	    s.incRow();
	}

	abstract class ColType {
		boolean outputThis = false;
		boolean isOutputThis() {
			return outputThis;
		}
		abstract String getHeader();
		abstract Object getValue(MobileProduct p);
		void init(MobileProduct p) {
			outputThis = true;
		}
	}
	
	class EntityStateCol extends ColType {
		@Override
		String getHeader() {
			return "EntityState";
		}

		@Override
		void init(MobileProduct p) {
			if (EntityState.DELETED.equals(p.getState()) || EntityState.INACTIVE.equals(p.getState())) {
				outputThis = true;
			}
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getState() == null ? "" : p.getState().getKey());
		}
	}
	
	class BusinessAreaCol extends ColType {
		@Override
		String getHeader() {
			return "Forretningsområde";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getBusinessArea().getName();
		}
	}
	
	class ProductGroupCol extends ColType {
		@Override
		String getHeader() {
			return "Produktgruppe";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getProductGroup().getFullPath();
		}
	}
	
	class PublicNameCol extends ColType {
		@Override
		String getHeader() {
			return "Navn";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getPublicName();
		}
	}
	
	class InternalNameCol extends ColType {
		@Override
		String getHeader() {
			return "Internt navn (Nabs/CDM kode)";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getInternalName();
		}
	}
	
	class KvikCodeCol extends ColType {
		@Override
		String getHeader() {
			return "Kvik kode";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getKvikCode();
		}
	}
	
	class ProductIdCol extends ColType {
		@Override
		String getHeader() {
			return "Varenummer";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getProductId();
		}
	}
	
	class OneTimeFeeCol extends ColType {
		@Override
		String getHeader() {
			return "Oprettelsespris";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getPrice() == null ? "-" : Double.valueOf(p.getPrice().getOneTimeFee()));
		}
	}
	
	class InstallationFeeCol extends ColType {
		@Override
		String getHeader() {
			return "Installationspris";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getPrice() == null ? "-" : Double.valueOf(p.getPrice().getInstallationFee()));
		}
	}
	
	class RecurringFeeCol extends ColType {
		@Override
		String getHeader() {
			return "Pris pr. betalingsperiode";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getPrice() == null ? "-" : Double.valueOf(p.getPrice().getRecurringFee()));
		}
	}
	
	class PaymentFrequencyCol extends ColType {
		@Override
		String getHeader() {
			return "Betalingsperiode";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getPaymentFrequency().getKey();
		}
	}
	
	class DefaultCountCol extends ColType {
		@Override
		String getHeader() {
			return "Std. antal";
		}

		@Override
		Object getValue(MobileProduct p) {
			return Double.valueOf(p.getDefaultCount());
		}
	}
	
	class MinCountCol extends ColType {
		@Override
		String getHeader() {
			return "Min. antal";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getMinCount() == null ? "-" : Double.valueOf(p.getMinCount()));
		}
	}
	
	class MaxCountCol extends ColType {
		@Override
		String getHeader() {
			return "Max. antal";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.getMaxCount() == null ? "-" : Double.valueOf(p.getMaxCount()));
		}
	}
	
	class DiscountEligibleCol extends ColType {
		@Override
		String getHeader() {
			return "Fordelsaftale rabatberettiget";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isDiscountEligible() ? "Ja" : "Nej");
		}
	}
	
	class IPSADiscountEligibleCol extends ColType {
		@Override
		String getHeader() {
			return "IPSA rabatberettiget";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isIpsaDiscountEligible() ? "Ja" : "Nej");
		}
	}
	
	class RabataftaleDiscountEligibleCol extends ColType {
		@Override
		String getHeader() {
			return "Rabataftale rabatberettiget";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return (p.isRabataftaleDiscountEligible() ? "Ja" : "Nej");
		}
	}
	
//	class Tem5DiscountMatrixCol extends ColType {
//		@Override
//		String getHeader() {
//			return "TEM5 rabatmatrix";
//		}
//		
//		@Override
//		Object getValue(MobileProduct p) {
//			return p.getTem5CampaignDiscountMatrix();
//		}
//	}
	
	class GKSCol extends ColType {
		@Override
		String getHeader() {
			return "GKS";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isGks() ? "Ja" : "Nej");
		}
	}
	
	class TDCInstallationCol extends ColType {
		@Override
		String getHeader() {
			return "TDC Installation";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isTdcInstallation() ? "Ja" : "Nej");
		}
	}
	
	class ExcludeFromConfiguratorCol extends ColType {
		@Override
		String getHeader() {
			return "Vis ikke i konfigurator";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isExcludeFromConfigurator() ? "Ja" : "Nej");
		}
	}
	
	class ExcludeFromProductionOutputCol extends ColType {
		@Override
		String getHeader() {
			return "Vis ikke i tastegrundlag";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isExcludeFromProductionOutput() ? "Ja" : "Nej");
		}
	}
	
	class ExcludeFromOfferCol extends ColType {
		@Override
		String getHeader() {
			return "Vis ikke i tilbud";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return (p.isExcludeFromOffer() ? "Ja" : "Nej");
		}
	}
	
	class PartnerProvisionInstallationCol extends ColType {
		@Override
		String getHeader() {
			return "Provision - vedr. inst. (pr. segment)";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getProvisionInstallationFee();
		}
	}
	
	class PartnerProvisionOneTimeCol extends ColType {
		@Override
		String getHeader() {
			return "Provision - vedr. etabl. (pr. segment)";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getProvisionOneTimeFee();
		}
	}
	
	class PartnerProvisionRecurringCol extends ColType {
		@Override
		String getHeader() {
			return "Provision - vedr. mnd. bet. (pr. segment)";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getProvisionRecurringFee();
		}
	}
		
	class VariableInstallationFeeCol extends ColType {
		@Override
		String getHeader() {
			return "Variabel inst.pris";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isVariableInstallationFee() ? "Ja" : "Nej");
		}
	}
	
	class VariableRecurringFeeCol extends ColType {
		@Override
		String getHeader() {
			return "Variabel driftpris";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isVariableRecurringFee() ? "Ja" : "Nej");
		}
	}
	
	class VariableCategoryCol extends ColType {
		@Override
		String getHeader() {
			return "Variabel kategori";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isVariableCategory() ? "Ja" : "Nej");
		}
	}
	
	class VariableProductNameCol extends ColType {
		@Override
		String getHeader() {
			return "Variabelt produktnavn";
		}

		@Override
		Object getValue(MobileProduct p) {
			return (p.isVariableProductName() ? "Ja" : "Nej");
		}
	}
	
	class RemarksCol extends ColType {
		@Override
		String getHeader() {
			return "Noter";
		}

		@Override
		Object getValue(MobileProduct p) {
			return p.getRemarks();
		}
	}
	
	class FlagsCol extends ColType {
		@Override
		String getHeader() {
			return "Specielle flag";
		}

		@Override
		Object getValue(MobileProduct p) {
			return StringUtils.defaultString(p.getFlags());
		}
	}
	
	class CountAllSubscribersCol extends ColType {
		@Override
		String getHeader() {
			return "Tæl alle abonnenter";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return (p.isSubscriberProduct() ? "Ja" : "Nej");
		}
	}
	
	class SortIndexCol extends ColType {
		@Override
		String getHeader() {
			return "Sortering i UI";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return Double.valueOf(p.getSortIndex());
		}
	}
	
	class OutputSortIndexCol extends ColType {
		@Override
		String getHeader() {
			return "Sortering i CDM";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return Double.valueOf(p.getOutputSortIndex());
		}
	}
	
	class OfferSortIndexCol extends ColType {
		@Override
		String getHeader() {
			return "Sortering i tilbud";
		}
		
		@Override
		Object getValue(MobileProduct p) {
			return Double.valueOf(p.getOfferSortIndex());
		}
	}
}

