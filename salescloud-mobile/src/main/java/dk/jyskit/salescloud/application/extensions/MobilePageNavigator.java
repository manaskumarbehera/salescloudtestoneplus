package dk.jyskit.salescloud.application.extensions;

import dk.jyskit.salescloud.application.pages.accessnew.locations.LocationsPage;
import org.apache.wicket.markup.html.WebPage;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.AccessCodes;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.accessold.adsl.AdslPage;
import dk.jyskit.salescloud.application.pages.contractsettings.MobileContractSettingsPage;
import dk.jyskit.salescloud.application.pages.contractsummary.ContractSummaryPage;
import dk.jyskit.salescloud.application.pages.accessnew.fiber.FiberPage;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundlesPage;
import dk.jyskit.salescloud.application.pages.officeadditional.OfficeAdditionalProductsPage;
import dk.jyskit.salescloud.application.pages.officeimplementation.OfficeImplementationPage;
import dk.jyskit.salescloud.application.pages.partner.PartnerSettingsPage;
import dk.jyskit.salescloud.application.pages.productselection.ProductSelectionPage;
import dk.jyskit.salescloud.application.pages.sales.existingcontract.ExistingContractPage;
import dk.jyskit.salescloud.application.pages.sales.masterdata.MasterDataPage;
import dk.jyskit.salescloud.application.pages.standardbundles.StandardBundlesPage;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.SubscriptionConfigurationPage;
import dk.jyskit.salescloud.application.pages.switchboard.SwitchboardPage;
import dk.jyskit.salescloud.application.pages.wifiadditionalinfo.WiFiAdditionalInfoPage;
import dk.jyskit.salescloud.application.pages.wifibundles.WiFiBundlesPage;
import dk.jyskit.salescloud.application.services.accesscodes.AccessCodeChecker;

public class MobilePageNavigator implements PageNavigator {

	@Override
	public Class<? extends WebPage> first() {
		return MasterDataPage.class;
	}
	
	// MasterData -> MobileContractSettings -> StandardBundles/MixBundles -> ProductSelection -> ContractSummary -> SubscriptionConfiguration
	
	@Override
	public Class<? extends WebPage> prev(WebPage currentPage) {
		SalespersonRole salesperson = (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		BusinessArea businessArea = MobileSession.get().getBusinessArea();
		
		Class currentPageClass = currentPage.getClass();
		
		if (currentPageClass.equals(MasterDataPage.class)) {
			return ExistingContractPage.class; 
		} 
		
		if (currentPageClass.equals(SubscriptionConfigurationPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return OfficeImplementationPage.class; 
			} else {
				currentPageClass = OfficeImplementationPage.class;
			}
		}
		
//		if (currentPageClass.equals(OfficeImplementationPage.class)) {
//			if (businessArea.hasFeature(FeatureType.FIBER)) {
//				return FiberAdditionalInfoPage.class; 
//			} else {
//				currentPageClass = FiberAdditionalInfoPage.class;
//			}
//		}
//		
//		if (currentPageClass.equals(FiberAdditionalInfoPage.class)) {
//			return ContractSummaryPage.class; 
//		}

		if (currentPageClass.equals(OfficeImplementationPage.class)) {
			return ContractSummaryPage.class; 
		}
		
		if (currentPageClass.equals(ContractSummaryPage.class)) {
			if (businessArea.hasFeature(FeatureType.PARTNER_SETTINGS) &&
				(salesperson.getOrganisation() != null) && 
				(salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				return PartnerSettingsPage.class; 
			} else {
				currentPageClass = PartnerSettingsPage.class;
			}
		} 
		if (currentPageClass.equals(PartnerSettingsPage.class)) {
			if (businessArea.isOnePlus()) {
				return LocationsPage.class;
			} else {
				if (businessArea.hasFeature(FeatureType.FIBER, FeatureType.FIBER_ERHVERV)) {
					return FiberPage.class;
				} else {
					currentPageClass = FiberPage.class;
				}
			}
		}
		if (currentPageClass.equals(FiberPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return OfficeAdditionalProductsPage.class;
			} else {
				currentPageClass = OfficeAdditionalProductsPage.class;
			}
		}
		if (currentPageClass.equals(LocationsPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return OfficeAdditionalProductsPage.class;
			} else {
				currentPageClass = OfficeAdditionalProductsPage.class;
			}
		}
		if (currentPageClass.equals(OfficeAdditionalProductsPage.class)) {
			if (businessArea.hasFeature(FeatureType.WIFI)) {
				return WiFiAdditionalInfoPage.class; 
			} else {
				currentPageClass = WiFiBundlesPage.class;
			}
		} 
		if (currentPageClass.equals(WiFiAdditionalInfoPage.class)) {
			if (businessArea.hasFeature(FeatureType.WIFI)) {
				return WiFiBundlesPage.class; 
			} else {
				currentPageClass = AdslPage.class;
			}
		} 
		if (currentPageClass.equals(WiFiBundlesPage.class)) {
			if (!businessArea.isOnePlus() && businessArea.hasFeature(FeatureType.XDSL) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.XDSL_FEATURE_NO_ACCESS)) {
				return AdslPage.class;
			} else {
				currentPageClass = AdslPage.class;
			}
		} 
		if (currentPageClass.equals(AdslPage.class)) {
			if ((!businessArea.isOnePlus()) &&
					businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS)) {
				return ProductSelectionPage.class; 
			} else {
				currentPageClass = ProductSelectionPage.class;
			}
		} 
		if (currentPageClass.equals(ProductSelectionPage.class)) {
			if (businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS, FeatureType.TDC_OFFICE)) {
				return StandardBundlesPage.class; 
			} else {
				currentPageClass = StandardBundlesPage.class;
			}
		} 
		if (currentPageClass.equals(StandardBundlesPage.class)) {
			if (businessArea.hasFeature(FeatureType.SWITCHBOARD) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.SWITCHBOARD_FEATURE_NO_ACCESS)) {
				return SwitchboardPage.class; 
			} else {
				currentPageClass = SwitchboardPage.class;
			}
		} 
		if (currentPageClass.equals(ProductSelectionPage.class)) {
			if (businessArea.hasFeature(FeatureType.SWITCHBOARD) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.SWITCHBOARD_FEATURE_NO_ACCESS)) {
				return SwitchboardPage.class; 
			} else {
				currentPageClass = SwitchboardPage.class;
			}
		} 
		if (currentPageClass.equals(SwitchboardPage.class)) {
			if ((businessArea.getBusinessAreaId() != BusinessAreas.TDC_OFFICE) && 
				(businessArea.getBusinessAreaId() != BusinessAreas.FIBER) && 
				(businessArea.getBusinessAreaId() != BusinessAreas.FIBER_ERHVERV)) {
				return MobileContractSettingsPage.class; 
			} else {
				currentPageClass = MobileContractSettingsPage.class;
			}
		} 
		return MasterDataPage.class;
	}
	
	@Override
	public Class<? extends WebPage> next(WebPage currentPage) {
		SalespersonRole salesperson = (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		BusinessArea businessArea = MobileSession.get().getBusinessArea();
		
		Class currentPageClass = currentPage.getClass();
		
		if (currentPageClass.equals(MasterDataPage.class)) {
			if ((businessArea.getBusinessAreaId() != BusinessAreas.TDC_OFFICE) && 
				(businessArea.getBusinessAreaId() != BusinessAreas.FIBER) && 
				(businessArea.getBusinessAreaId() != BusinessAreas.FIBER_ERHVERV)) {
				return MobileContractSettingsPage.class;
			} else {
				currentPageClass = MobileContractSettingsPage.class;
			}
		} 
		if (currentPageClass.equals(MobileContractSettingsPage.class)) {
			if (businessArea.hasFeature(FeatureType.SWITCHBOARD) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.SWITCHBOARD_FEATURE_NO_ACCESS)) {
				return SwitchboardPage.class; 
			} else {
				currentPageClass = SwitchboardPage.class;
			}
		} 
		if (currentPageClass.equals(SwitchboardPage.class)) {
			if (businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS, FeatureType.TDC_OFFICE)) {
				return StandardBundlesPage.class; 
			} else {
				currentPageClass = StandardBundlesPage.class;
			}
		} 
		if (currentPageClass.equals(StandardBundlesPage.class)) {
			if ((businessArea.getBusinessAreaId() != BusinessAreas.ONE_PLUS) &&
					businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS)) {
				return ProductSelectionPage.class;
			} else {
				currentPageClass = ProductSelectionPage.class;
			}
		} 
		if (currentPageClass.equals(MixBundlesPage.class)) {
			if (businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS)) {
				return ProductSelectionPage.class; 
			} else {
				currentPageClass = ProductSelectionPage.class;
			}
		} 
		if (currentPageClass.equals(ProductSelectionPage.class)) {
			if (businessArea.hasFeature(FeatureType.XDSL) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.XDSL_FEATURE_NO_ACCESS)) {
				if (businessArea.isOnePlus()) {
					return LocationsPage.class;
				} else {
					return AdslPage.class;
				}
			} else {
				currentPageClass = AdslPage.class;
			}
		}
		if (currentPageClass.equals(AdslPage.class)) {
			if (businessArea.hasFeature(FeatureType.WIFI)) {
				return WiFiBundlesPage.class; 
			} else {
				currentPageClass = WiFiBundlesPage.class;
			}
		} 
		if (currentPageClass.equals(WiFiBundlesPage.class)) {
			if (businessArea.hasFeature(FeatureType.WIFI)) {
				return WiFiAdditionalInfoPage.class; 
			} else {
				currentPageClass = WiFiAdditionalInfoPage.class;
			}
		} 
		if (currentPageClass.equals(WiFiAdditionalInfoPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return OfficeAdditionalProductsPage.class; 
			} else {
				currentPageClass = OfficeAdditionalProductsPage.class;
			}
		} 
		if (currentPageClass.equals(OfficeAdditionalProductsPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return ContractSummaryPage.class; 
			} else {
				if ((businessArea.getBusinessAreaId() == BusinessAreas.FIBER) || 
					(businessArea.getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV)) {
					return FiberPage.class;
				}
				currentPageClass = FiberPage.class;
			}
		} 
		if (currentPageClass.equals(FiberPage.class)) {
			if (businessArea.hasFeature(FeatureType.PARTNER_SETTINGS) && 
				(salesperson.getOrganisation() != null) && 
				(salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				return PartnerSettingsPage.class; 
			} else {
				currentPageClass = PartnerSettingsPage.class;
			}
		}
		if (currentPageClass.equals(LocationsPage.class)) {
			if (businessArea.hasFeature(FeatureType.PARTNER_SETTINGS) &&
					(salesperson.getOrganisation() != null) &&
					(salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				return PartnerSettingsPage.class;
			} else {
				currentPageClass = PartnerSettingsPage.class;
			}
		}
		if (currentPageClass.equals(PartnerSettingsPage.class)) {
			return ContractSummaryPage.class; 
		} 
		if (currentPageClass.equals(ContractSummaryPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return OfficeImplementationPage.class; 
			} else {
				currentPageClass = OfficeImplementationPage.class;
			}
		}
		
		if (currentPageClass.equals(OfficeImplementationPage.class)) {
			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
				return SubscriptionConfigurationPage.class; 
			} else {
				currentPageClass = SubscriptionConfigurationPage.class;
			}
		} 
		
//		if (currentPageClass.equals(OfficeImplementationPage.class)) {
//			if (businessArea.hasFeature(FeatureType.FIBER)) {
//				return FiberAdditionalInfoPage.class; 
//			} else {
//				currentPageClass = FiberAdditionalInfoPage.class;
//			}
//		} 
//		
//		if (currentPageClass.equals(FiberAdditionalInfoPage.class)) {
//			if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
//				return SubscriptionConfigurationPage.class; 
//			} else {
//				currentPageClass = SubscriptionConfigurationPage.class;
//			}
//		} 
		return MasterDataPage.class;
	}
	
	public Class<? extends WebPage> prevOld(WebPage currentPage) {
		SalespersonRole salesperson = (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		switch (MobileSession.get().getBusinessArea().getBusinessAreaId()) {
		case BusinessAreas.MOBILE_VOICE:
			if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				if (currentPage instanceof MasterDataPage) {				return ExistingContractPage.class; } else 
//				if (currentPage instanceof ContractSummaryPage) {			return PartnerSettingsPage.class; } else
//				if (currentPage instanceof PartnerSettingsPage) {			return ProductSelectionPage.class; } else
				if (currentPage instanceof ContractSummaryPage) {			return ProductSelectionPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return StandardBundlesPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return MobileContractSettingsPage.class; } else
				if (currentPage instanceof StandardBundlesPage) {			return MobileContractSettingsPage.class; } else
				if (currentPage instanceof MobileContractSettingsPage) {	return MasterDataPage.class; }
				return MasterDataPage.class;
			} else {
				if (currentPage instanceof MasterDataPage) {				return ExistingContractPage.class; } else 
				if (currentPage instanceof ContractSummaryPage) {			return ProductSelectionPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return StandardBundlesPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return MobileContractSettingsPage.class; } else
				if (currentPage instanceof StandardBundlesPage) {			return MobileContractSettingsPage.class; } else
				if (currentPage instanceof MobileContractSettingsPage) {	return MasterDataPage.class; }
				return MasterDataPage.class;
			}
		case BusinessAreas.SWITCHBOARD:
		case BusinessAreas.FIBER:
		case BusinessAreas.FIBER_ERHVERV:
		case BusinessAreas.WIFI:
			if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				if (currentPage instanceof MasterDataPage) {				return ExistingContractPage.class; } else 
				if (currentPage instanceof ContractSummaryPage) {			return PartnerSettingsPage.class; } else
				if (currentPage instanceof PartnerSettingsPage) {			return AdslPage.class; } else
				if (currentPage instanceof AdslPage) {						return ProductSelectionPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return StandardBundlesPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return SwitchboardPage.class; } else
				if (currentPage instanceof StandardBundlesPage) {			return SwitchboardPage.class; } else
				if (currentPage instanceof SwitchboardPage) {				return MobileContractSettingsPage.class; } else
				if (currentPage instanceof MobileContractSettingsPage) {	return MasterDataPage.class; }
				return MasterDataPage.class;
			} else {
				if (currentPage instanceof MasterDataPage) {				return ExistingContractPage.class; } else 
				if (currentPage instanceof ContractSummaryPage) {			return ProductSelectionPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return AdslPage.class; } else
				if (currentPage instanceof AdslPage) {						return StandardBundlesPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return SwitchboardPage.class; } else
				if (currentPage instanceof StandardBundlesPage) {			return SwitchboardPage.class; } else
				if (currentPage instanceof SwitchboardPage) {				return MobileContractSettingsPage.class; } else
				if (currentPage instanceof MobileContractSettingsPage) {	return MasterDataPage.class; }
				return MasterDataPage.class;
			}
		}
		return MasterDataPage.class;
	}
	
	public Class<? extends WebPage> nextOld(WebPage currentPage) {
		SalespersonRole salesperson = (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		switch (MobileSession.get().getBusinessArea().getBusinessAreaId()) {
		case BusinessAreas.MOBILE_VOICE:
			if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				if (currentPage instanceof MasterDataPage) { 				return MobileContractSettingsPage.class; } else 
				if (currentPage instanceof MobileContractSettingsPage) {	return StandardBundlesPage.class; } else
				if (currentPage instanceof StandardBundlesPage) { 			return ProductSelectionPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return ProductSelectionPage.class; } else
//				if (currentPage instanceof ProductSelectionPage) {			return PartnerSettingsPage.class; } else
//				if (currentPage instanceof PartnerSettingsPage) {			return ContractSummaryPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return ContractSummaryPage.class; } else
				if (currentPage instanceof ContractSummaryPage) {			return MasterDataPage.class; } 
				return MasterDataPage.class;
			} else {
				if (currentPage instanceof MasterDataPage) { 				return MobileContractSettingsPage.class; } else 
				if (currentPage instanceof MobileContractSettingsPage) {	return StandardBundlesPage.class; } else
				if (currentPage instanceof StandardBundlesPage) { 			return ProductSelectionPage.class; } else
				if (currentPage instanceof MixBundlesPage) {				return ProductSelectionPage.class; } else
				if (currentPage instanceof ProductSelectionPage) {			return ContractSummaryPage.class; } else
				if (currentPage instanceof ContractSummaryPage) {			return MasterDataPage.class; } 
				return MasterDataPage.class;
			}
		case BusinessAreas.SWITCHBOARD:
		case BusinessAreas.FIBER:
		case BusinessAreas.FIBER_ERHVERV:
		case BusinessAreas.WIFI:
			if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
				if (currentPage instanceof MasterDataPage) { 					return MobileContractSettingsPage.class; } else 
					if (currentPage instanceof MobileContractSettingsPage) {	return SwitchboardPage.class; } else
					if (currentPage instanceof SwitchboardPage) {				return StandardBundlesPage.class; } else
					if (currentPage instanceof StandardBundlesPage) { 			return ProductSelectionPage.class; } else
					if (currentPage instanceof MixBundlesPage) {				return ProductSelectionPage.class; } else
					if (currentPage instanceof ProductSelectionPage) {			return AdslPage.class; } else
					if (currentPage instanceof AdslPage) {						return PartnerSettingsPage.class; } else
					if (currentPage instanceof PartnerSettingsPage) {			return ContractSummaryPage.class; } else
					if (currentPage instanceof ContractSummaryPage) {			return MasterDataPage.class; }
				return MasterDataPage.class;
			} else {
				if (currentPage instanceof MasterDataPage) { 					return MobileContractSettingsPage.class; } else 
					if (currentPage instanceof MobileContractSettingsPage) {	return SwitchboardPage.class; } else
					if (currentPage instanceof SwitchboardPage) {				return StandardBundlesPage.class; } else
					if (currentPage instanceof StandardBundlesPage) { 			return ProductSelectionPage.class; } else
					if (currentPage instanceof MixBundlesPage) {				return ProductSelectionPage.class; } else
					if (currentPage instanceof ProductSelectionPage) {			return AdslPage.class; } else
					if (currentPage instanceof AdslPage) {						return ContractSummaryPage.class; } else
					if (currentPage instanceof ContractSummaryPage) {			return MasterDataPage.class; }
				return MasterDataPage.class;
			}
		}
		return MasterDataPage.class;
	}
}
