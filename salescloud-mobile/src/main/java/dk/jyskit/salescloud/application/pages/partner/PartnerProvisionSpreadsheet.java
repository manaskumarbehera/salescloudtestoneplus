package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import lombok.Data;

public class PartnerProvisionSpreadsheet implements Provider<Workbook>, Serializable{

	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("PartnerProvision");
		
		MobileContract contract = MobileSession.get().getContract();

		MutableLong stykProvisions	= new MutableLong();
		MutableLong satsProvisions	= new MutableLong();
		MutableLong totalProvisions	= new MutableLong();

		// -----------------------
		// Her beregnes provision:

		contract.calculatePartnerProvision(stykProvisions, satsProvisions, totalProvisions);

		// -----------------------

		s.incRow();
	    s.addValue("Opgørelse af provision for TDC Erhverv partner");
	    
	    s.incRow();
	    
	    addHeaderRow(s, null, "Kunde navn:", contract.getCustomer().getCompanyName(), "Sælger navn:", contract.getSalesperson().getUser().getFirstName() + " " + contract.getSalesperson().getUser().getLastName());
	    addHeaderRow(s, null, "CVR nummer:", contract.getCustomer().getCompanyId(), "Email adresse:", contract.getSalesperson().getUser().getEmail());
	    addHeaderRow(s, null, "Kontaktperson:", contract.getCustomer().getName(), "Telefon nummer:", contract.getSeller().getPhone());
	    addHeaderRow(s, null, "Adresse:", contract.getCustomer().getAddress(), "", "");
	    addHeaderRow(s, null, "Postnr./By:", contract.getCustomer().getZipCode() + " " + contract.getCustomer().getCity(), "", "");
	    addHeaderRow(s, null, "Telefon nummer:", contract.getCustomer().getPhone(), "", "");
	    addHeaderRow(s, null, "Email:", contract.getCustomer().getEmail(), "", "");
//	    addHeaderRow(s, null, "Ønsket oprettelsesdato:", (MobileSession.get().getContract().getInstallationDate() == null ? "Ikke fastlagt" : new SimpleDateFormat("dd/MM/yyyy").format(MobileSession.get().getContract().getInstallationDate())), "", "");
	    if (contract.getSegment() == null) {
	    	addHeaderRow(s, null, "Segment:", "Ikke valgt", "", "");
	    } else {
	    	addHeaderRow(s, null, "Segment:", contract.getSegment().getName(), "", "");
	    }
		addHeaderRow(s, null, "Installation løsning:", contract.getInstallationTypeBusiness(), "", "");
		addHeaderRow(s, null, "Installation brugere:", contract.getInstallationTypeUserProfiles(), "", "");
		if (contract.getLocationBundles().size() == 0) {
			addHeaderRow(s, null, "Installation lokationer:", "Ingen", "", "");
		} else {
			addHeaderRow(s, null, "Installation lokationer:", LocationBundleData.getInstallationProviderAsString(contract.getLocationBundle(0).getInstallationProvider()), "", "");
		}
		if (contract.getLocationBundles().size() == 0) {
			addHeaderRow(s, null, "Hardware lokationer:", "Ingen", "", "");
		} else {
			addHeaderRow(s, null, "Hardware lokationer:", LocationBundleData.getHardwareProviderAsString(contract.getLocationBundle(0).getHardwareProvider()), "", "");
		}
		for (Provision provision: contract.getProvisions()) {
			if (provision.getType() == Provision.TYPE_HEADER) {
				addHeaderRow(s, null, provision.getText(), provision.getAmounts().getRecurringFeeAsFloat(), "", "");
			}
		}

	    s.incRow();
	    
	    float factor = contract.calculatePartnerProvisionFactor();
		
	    addRow(s, null, "Vejledende stykprovision brugerprofiler",	Amounts.x100ToFloat(stykProvisions.longValue() * 100));
	    addRow(s, null, "Vejledende satsprovision omsætning", 		Amounts.x100ToFloat(satsProvisions.longValue() * 100));
//	    addRow(s, null, "Vejledende provision total (før faktor)", 	"" + totalProvisions.longValue());
//	    addRow(s, null, "Provisionsfaktor", "" + Amounts.getFormattedWithDecimals(Math.round(factor * 100)));
	    addRow(s, null, "Vejledende provision total", 				Amounts.x100ToFloat(totalProvisions.longValue() * 100));
	    
	    s.incRow();
	    addRow(s, null, "Oprettelse");

	    for (Provision provision: contract.getProvisions()) {
			if ((provision.getType() != Provision.TYPE_HEADER) &&  (provision.getAmounts().getOneTimeFee() != 0)) {
				addRow(s, null, provision.getText(), Integer.valueOf(provision.getCount()), provision.getAmounts().getOneTimeFeeAsFloat());
			}
		}

//		for (OrderLine orderLine : contract.getOrderLines()) {
//			if (orderLine.getDeferredCount().getCountNew() > 0) {
//				ProductBundle b = orderLine.getBundle();
//				if (b == null) {
//					MobileProduct product = (MobileProduct) orderLine.getProduct();
//					for (Provision provision: product.getProvisions(orderLine)) {
//						if (provision.getAmounts().getOneTimeFee() != 0) {
//							addRow(s, null, product.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getOneTimeFee()));
//						}
//					}
//				} else {
//					MobileProductBundle bundle = (MobileProductBundle) b;
//					for (Provision provision: bundle.getProvisions(orderLine)) {
//						if (provision.getAmounts().getOneTimeFee() != 0) {
//							addRow(s, null, bundle.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getOneTimeFee()));
//						}
//					}
//				}
//			}
//		}
	    
	    s.incRow();
	    addRow(s, null, "Installation af løsning");

		for (Provision provision: contract.getProvisions()) {
			if ((provision.getType() != Provision.TYPE_HEADER) && (provision.getAmounts().getInstallationFee() != 0)) {
				addRow(s, null, provision.getText(), Integer.valueOf(provision.getCount()), provision.getAmounts().getInstallationFeeAsFloat());
			}
		}

//		for (OrderLine orderLine : contract.getOrderLines()) {
//			if (orderLine.getDeferredCount().getCountNew() > 0) {
//				ProductBundle b = orderLine.getBundle();
//				if (b == null) {
//					MobileProduct product = (MobileProduct) orderLine.getProduct();
//					for (Provision provision: product.getProvisions(orderLine)) {
//						if (provision.getAmounts().getInstallationFee() != 0) {
//							addRow(s, null, product.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getInstallationFee()));
//						}
//					}
//				} else {
//					MobileProductBundle bundle = (MobileProductBundle) b;
//					for (Provision provision: bundle.getProvisions(orderLine)) {
//						if (provision.getAmounts().getInstallationFee() != 0) {
//							addRow(s, null, bundle.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getInstallationFee()));
//						}
//					}
//				}
//			}
//		}
	    
	    s.incRow();
	    addRow(s, null, "Drift");

		for (Provision provision: contract.getProvisions()) {
			if ((provision.getType() != Provision.TYPE_HEADER) && (provision.getAmounts().getRecurringFee() != 0)) {
				addRow(s, null, provision.getText(), Integer.valueOf(provision.getCount()), provision.getAmounts().getRecurringFeeAsFloat());
			}
		}

//		for (OrderLine orderLine : contract.getOrderLines()) {
//			if (orderLine.getDeferredCount().getCountNew() > 0) {
//				ProductBundle b = orderLine.getBundle();
//				if (b == null) {
//					MobileProduct product = (MobileProduct) orderLine.getProduct();
//					for (Provision provision: product.getProvisions(orderLine)) {
//						if (provision.getAmounts().getRecurringFee() != 0) {
//							addRow(s, null, product.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getRecurringFee()));
//						}
//					}
//				} else {
//					MobileProductBundle bundle = (MobileProductBundle) b;
//					for (Provision provision: bundle.getProvisions(orderLine)) {
//						if (provision.getAmounts().getRecurringFee() != 0) {
//							addRow(s, null, bundle.getInternalName(), "" + orderLine.getDeferredCount().getCountNew(), formatAmount(provision.getAmounts().getRecurringFee()));
//						}
//					}
//				}
//			}
//		}
	    
		return s.getWorkbook();
	}
	
	private void addRow(Spreadsheet s, IndexedColors color, Object ... values) {
	    s.incRow();

	    for (Object value : values) {
	    	Cell cell = null;
	    	if (value instanceof Float) {
	    		cell = s.addDouble((Float) value);
	    		s.addCellStyle(cell, color, Integer.valueOf(2));
	    	} else if (value instanceof Double) {
	    		cell = s.addDouble((Float) value);
	    		s.addCellStyle(cell, color, Integer.valueOf(2));
	    	} else if (value instanceof Integer) {
	    		cell = s.addDouble((Integer) value);
	    		s.addCellStyle(cell, color, null);
	    	} else if (value instanceof String) {
	    		cell = s.addValue((String) value);
	    		s.addCellStyle(cell, color, null);
	    	}
		}
	}

	private void addHeaderRow(Spreadsheet s, IndexedColors color, Object ... values) {
	    s.incRow();
		for (Object value : values) {
			if (color == null) {
				s.addValue(value);
			} else {
				s.addColoredValue(value, color);
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
