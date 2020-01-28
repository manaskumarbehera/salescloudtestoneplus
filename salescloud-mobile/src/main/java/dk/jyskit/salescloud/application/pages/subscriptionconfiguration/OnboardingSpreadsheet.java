package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.salescloud.application.model.Constants;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.NumberTransferType;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.SimCardType;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.Data;

public class OnboardingSpreadsheet implements Provider<Workbook>, Serializable{

	private boolean isNabs;

	public OnboardingSpreadsheet(boolean isNabs) {
		this.isNabs = isNabs;
	}
	
	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("Onboarding");
		s.resetRowNo();
		
		MobileContract contract = MobileSession.get().getContract();
		
	    addRow(s, null, "Emailadresse", "Fornavn", "Efternavn", "Licens", "Type", "Mailmigrering");
	    
		boolean hasMailMigrering = false;
		Product product = Lookup.lookup(ProductDao.class).findByBusinessAreaAndProductId(
				contract.getBusinessArea().getId(), "101005");
		if (product != null) {
			List<OrderLine> orderLines = contract.getOrderLines(product);
			for (OrderLine orderLine : orderLines) {
				if (orderLine.getCountNew() == Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT) {
					hasMailMigrering = true;
					break;
				}
			}
		}
	    
    	for (Subscription subscription : contract.getSubscriptions()) {
    		addRow(s, null, subscription.getEmail(), subscription.getFirstName(), subscription.getLastName(), subscription.getBundle().getPublicName(), "Bruger", hasMailMigrering ? "Ja" : "Nej");
		}

    	s.addSheet("Metadata");
		addMetaData(s, contract, false);
    	
		return s.getWorkbook();
	}

	private void addMetaData(Spreadsheet s, MobileContract contract, boolean verbose) {
	    addHeaderRow(s, null, "Format - Version", "1.0");
	    
	    String prefix = "";
	    if (verbose) {
	    	s.incRow();
	    	addHeaderRow(s, null, "Kunde:");
	    } else {
	    	prefix = "Kunde - ";
	    }
	    
	    addHeaderRow(s, null, prefix + "Navn", contract.getCustomer().getCompanyName());
	    addHeaderRow(s, null, prefix + "CVR nummer", contract.getCustomer().getCompanyId());
	    addHeaderRow(s, null, prefix + "Adresse", contract.getCustomer().getAddress());
	    addHeaderRow(s, null, prefix + "Postnr./By", (contract.getCustomer().getZipCode() == null ? "" : contract.getCustomer().getZipCode() + " ") + (contract.getCustomer().getCity() == null ? "" : contract.getCustomer().getCity()));
	    addHeaderRow(s, null, prefix + "Kontaktperson", contract.getCustomer().getName());
	    addHeaderRow(s, null, prefix + "Email", contract.getCustomer().getEmail());
	    addHeaderRow(s, null, prefix + "Telefon nummer", contract.getCustomer().getPhone(), "", "");
	    addHeaderRow(s, null, prefix + "Teknisk kontaktperson - Navn", contract.getTechnicalContactName(), "", "");
	    addHeaderRow(s, null, prefix + "Teknisk kontaktperson - Tlf.", contract.getTechnicalContactPhone(), "", "");
	    addHeaderRow(s, null, prefix + "Teknisk kontaktperson - Email", contract.getTechnicalContactEmail(), "", "");
	    addHeaderRow(s, null, prefix + "e-Faktura email", contract.getEFakturaEmail(), "", "");
//	    addHeaderRow(s, null, prefix + "Ønsket oprettelsesdato", (MobileSession.get().getContract().getInstallationDate() == null ? "Ikke fastlagt" : new SimpleDateFormat("dd/MM/yyyy").format(MobileSession.get().getContract().getInstallationDate())));
	    
	    if (verbose) {
	    	s.incRow();
	    	addHeaderRow(s, null, "Sælger:");
	    } else {
	    	prefix = "Sælger - ";
	    }
	    addHeaderRow(s, null, prefix + "Navn", contract.getSalesperson().getUser().getFirstName() + " " + contract.getSalesperson().getUser().getLastName());
	    addHeaderRow(s, null, prefix + "Email", contract.getSalesperson().getUser().getEmail());
	    addHeaderRow(s, null, prefix + "Telefon nummer", contract.getSalesperson().getUser().getSmsPhone());
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

	private void addHeaderRow(Spreadsheet s, IndexedColors color, Object ... objects) {
	    s.incRow();
	    for (Object object : objects) {
	    	if (color == null) {
			    s.addValue(object);
	    	} else {
	    		s.addColoredValue(object, color);
	    	}
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
