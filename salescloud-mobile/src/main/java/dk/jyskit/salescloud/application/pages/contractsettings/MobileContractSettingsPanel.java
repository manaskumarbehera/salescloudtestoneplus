package dk.jyskit.salescloud.application.pages.contractsettings;

import java.util.*;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.links.reports.ReportLink;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.pages.contractsummary.CdmOutputReport;
import dk.jyskit.salescloud.application.pages.contractsummary.ContractAcceptReport;
import dk.jyskit.salescloud.application.pages.contractsummary.TilbudRammeaftaleOgPBReport;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.services.accesscodes.AccessCodeChecker;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;

public class MobileContractSettingsPanel extends Panel {
	@Inject
	private ContractSaver contractSaver;
	@Inject
	private CampaignDao campaignDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private PageNavigator navigator;
	private boolean oldPoolsMode;
	private final ContractStatusEnum oldStatus;
	private final MobileContractMode oldContractMode;

	public MobileContractSettingsPanel(String id) {
		super(id);
		
		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		oldStatus = contract.getStatus();
		oldContractMode = contract.getContractMode();

		List<Campaign> campaigns = campaignDao.findAvailableByBusinessArea(CoreSession.get().getBusinessArea().getId());
		Iterator<Campaign> iterator = campaigns.iterator();
		while(iterator.hasNext()) {
			Campaign campaign = iterator.next();
			if (!StringUtils.isEmpty(campaign.getFilter())) {
				// We expect this syntax: <filter type>: value1, value2, ...
				String[] strings = StringUtils.split(campaign.getFilter().trim(), ':');
				boolean match = false;
				if ("sælgertype".equalsIgnoreCase(strings[0].trim())) {
					SalespersonRole salesperson = MobileSession.get().getSalespersonRole();
					if (salesperson != null) {
						String[] values = StringUtils.split(strings[1], ',');
						for (String value : values) {
							value = value.trim().toLowerCase();
							if ("agent".equals(value)) {
								if (salesperson.isAgent()) {
									match = true;
								}
							} else if ("agent_sa".equals(value)) {
								if (salesperson.isAgent_sa()) {
									match = true;
								}
							} else if ("agent_mb".equals(value)) {
								if (salesperson.isAgent_mb()) {
									match = true;
								}
							} else if ("agent_lb".equals(value)) {
								if (salesperson.isAgent_lb()) {
									match = true;
								}
							} else if ("partner".equals(value)) {
								if (salesperson.isPartner()) {
									match = true;
								}
							} else if ("partner_ec".equals(value)) {
								if (salesperson.isPartner_ec()) {
									match = true;
								}
							}
						}
					}
				} else if ("sælger".equalsIgnoreCase(strings[0].trim())) {
					SalespersonRole salesperson = MobileSession.get().getSalespersonRole();
					if (salesperson != null) {
						String[] values = StringUtils.split(strings[1], ',');
						for (String value : values) {
							if (value.trim().equalsIgnoreCase(salesperson.getUser().getEmail())) {
								match = true;
							}
						}
					}
				} else if ("afdeling".equalsIgnoreCase(strings[0].trim())) {
					SalespersonRole salesperson = MobileSession.get().getSalespersonRole();
					if (salesperson != null) {
						String[] values = StringUtils.split(strings[1], ',');
						for (String value : values) {
							value = value.trim().toLowerCase();
							if (value.equals(salesperson.getDivision())) {
								match = true;
							}
						}
					}
				} else if ("division".equalsIgnoreCase(strings[0].trim())) {    // deprecated
					SalespersonRole salesperson = MobileSession.get().getSalespersonRole();
					if (salesperson != null) {
						String[] values = StringUtils.split(strings[1], ',');
						for (String value : values) {
							value = value.trim().toLowerCase();
							if (value.equals(salesperson.getDivision())) {
								match = true;
							}
						}
					}
				}
				if (!match) {
					iterator.remove();
				}
			}
		}
		
		Campaign oldCampaign = contract.getCampaigns().get(0);	// It is a list of exactly one element !
		
		final ContractWithSingleCampaign contractWithSingleCampaign = new ContractWithSingleCampaign();
		contractWithSingleCampaign.setContract(contract);
		contractWithSingleCampaign.setCampaign(oldCampaign);

		if (MobileSession.get().isBusinessAreaOnePlus()) {
			ProductGroup group = contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_INSTALLATIONTYPE.getKey());
			for (Product p: group.getProducts()) {
				if (Objects.equals(p.getId(), contract.getInstallationTypeBusinessEntityId())) {
					contractWithSingleCampaign.setInstallationTypeBusiness(p);
				}
				if (Objects.equals(p.getId(), contract.getInstallationTypeUserProfilesEntityId())) {
					contractWithSingleCampaign.setInstallationTypeUserProfiles(p);
				}
			}

			for (Product p: contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_SERVICE.getKey()).getProducts()) {
				if (Objects.equals(p.getId(), contract.getServiceLevelEntityId())) {
					contractWithSingleCampaign.setServiceLevel(p);
				}
			}
		}

		if (oldCampaign != null) {
			if (!campaigns.contains(oldCampaign)) {
				if ((oldCampaign.getExtensionFromDate() != null) && (oldCampaign.getExtensionToDate() != null)) {
					Date now = new Date();
					if (now.after(oldCampaign.getExtensionFromDate()) && now.before(oldCampaign.getExtensionToDate())) {
						campaigns.add(oldCampaign);
					}
				}
			}
		}
		
		Jsr303Form<ContractWithSingleCampaign> form = new Jsr303Form<>("form", contractWithSingleCampaign);
		add(form);
		
		form.setLabelStrategy(new EntityLabelStrategy("MobileContract"));
		form.setLabelSpans(SmallSpanType.SPAN8);
		form.setEditorSpans(SmallSpanType.SPAN4);
		
		// form.addSelectSinglePanel("contractType", MobileContractType.valuesAsList(), new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
		
		SalespersonRole salesperson =  (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
		
		List<MobileContractMode> modes = new ArrayList<>();
    	modes.add(MobileContractMode.NEW_SALE);
    	
		if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD)) { 
			if (!CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod") || 
					(!((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) || AccessCodeChecker.isCodeActiveForUser(AccessCodes.VOICE_TO_SWITCHBOARD))) {
				if (!contract.getBusinessArea().isOnePlus()) {
					modes.add(MobileContractMode.VOICE_TO_SWITCHBOARD);
				}
			}
		}
		if (contract.getBusinessArea().isOnePlus()) {
			modes.add(MobileContractMode.CONVERSION);
			modes.add(MobileContractMode.CONVERSION_1_TO_1);
		} else {
			if (contract.getBusinessArea().hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)
					|| contract.getBusinessArea().isWorks()
			) {
				if (!CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod") ||
						(!((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) || AccessCodeChecker.isCodeActiveForUser(AccessCodes.RENOGIATION))) {
					modes.add(MobileContractMode.RENEGOTIATION);
				}
			}
		}

//    	if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//			if ((BusinessAreas.SWITCHBOARD == contract.getBusinessArea().getBusinessAreaId()) ||
//				(BusinessAreas.FIBER == contract.getBusinessArea().getBusinessAreaId()) ||
//				(BusinessAreas.WIFI == contract.getBusinessArea().getBusinessAreaId())) {
//				if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
//		    		if (AccessCodeChecker.isCodeActiveForUser(AccessCodes.VOICE_TO_SWITCHBOARD)) {
//		    			modes.add(MobileContractMode.VOICE_TO_SWITCHBOARD);
//		    		}
//		    		if (AccessCodeChecker.isCodeActiveForUser(AccessCodes.RENOGIATION)) {
//		    			modes.add(MobileContractMode.RENEGOTIATION);
//		    		}
//				} else {
//					modes.add(MobileContractMode.VOICE_TO_SWITCHBOARD);
//					modes.add(MobileContractMode.RENEGOTIATION);
//				}
//			}
//    	} else {
//    		modes.add(MobileContractMode.VOICE_TO_SWITCHBOARD);
//    		modes.add(MobileContractMode.RENEGOTIATION);
//    	}

		if (!contract.getBusinessArea().isOneOf(BusinessAreas.TDC_OFFICE)) {
			form.addSelectSinglePanel("contract.contractMode", modes, new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
		}

//		List<String> modes = new ArrayList<>();


		if (contract.getBusinessArea().hasFeature(FeatureType.POOLS)) {
			oldPoolsMode = contract.isPoolsMode();
			form.addCheckBox("contract.poolsMode");
		}

		BootstrapSelectSingle contractStatusField =
    			form.addSelectSinglePanel("contract.status", ContractStatusEnum.valuesAsList(ContractStatusEnum.BUSINESSAREA_SPECIFIC), new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
    	
    	final NumberTextField salesforceNoField = form.addNumberTextField("contract.salesforceNo");
    	salesforceNoField.getParent().getParent().setOutputMarkupId(true);
    	if (!ContractStatusEnum.WON.equals(contract.getStatus())) {
    		salesforceNoField.getParent().getParent().setOutputMarkupPlaceholderTag(true);
    		salesforceNoField.getParent().getParent().setVisible(false);
    	}
    	
    	contractStatusField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
	    		salesforceNoField.getParent().getParent().setVisible(ContractStatusEnum.WON.equals(contract.getStatus()));
	    		target.add(salesforceNoField.getParent().getParent());
			}
		});
		if (contract.getBusinessArea().isOneOf(BusinessAreas.TDC_OFFICE, BusinessAreas.ONE_PLUS)) {
			contractStatusField.getParent().getParent().setVisible(false);
		}
		
//        if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("scprod")) {
//        	List<MobileContractMode> modes = new ArrayList<>();
//        	modes.add(MobileContractMode.NEW_SALE);
//        	
//    		SalespersonRole salesperson =  (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
//    		if ((salesperson.getOrganisation() != null) && (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER))) {
//    		} else {
//    			modes.add(MobileContractMode.VOICE_TO_SWITCHBOARD);
//    		}
//        	form.addSelectSinglePanel("contract.contractMode", modes, new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
//        } else {
//        	form.addSelectSinglePanel("contract.contractMode", MobileContractMode.valuesAsList(), new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
//        }
    	
		form.addSelectSinglePanel("campaign", campaigns, new IdPropChoiceRenderer("name"), new BootstrapSelectOptions());

//		if (MobileSession.get().isBusinessAreaOnePlus()) {
//			ProductGroup group = contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_INSTALLATIONTYPE.getKey());
//			{
//				List<Product> products = Lists.newArrayList(group.getProducts());
//				Iterator<Product> iter = products.iterator();
//				while (iter.hasNext()) {
//					Product p = iter.next();
//					if (p.getPublicName().toLowerCase().indexOf("oyo") != -1) {
//						iter.remove();
//					}
//				}
//				BootstrapSelectSingle selectSinglePanel = form.addSelectSinglePanel("installationTypeBusiness", products, new IdPropChoiceRenderer("publicName"), new BootstrapSelectOptions());
//				selectSinglePanel.setRequired(true);
//			}
//			{
//				List<Product> products = Lists.newArrayList(group.getProducts());
//				Iterator<Product> iter = products.iterator();
//				while (iter.hasNext()) {
//					Product p = iter.next();
//					if (p.getPublicName().toLowerCase().indexOf("onsite") != -1) {
//						iter.remove();
//					}
//				}
//				BootstrapSelectSingle selectSinglePanel = form.addSelectSinglePanel("installationTypeUserProfiles", products, new IdPropChoiceRenderer("publicName"), new BootstrapSelectOptions());
//				selectSinglePanel.setRequired(true);
//			}
//		}

		if (MobileSession.get().isBusinessAreaOnePlus()) {
//			List<Long> serviceLevelIds = new ArrayList<>();
			MobileProductGroup group = (MobileProductGroup) MobileSession.get().getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_SERVICE.getKey());
			BootstrapSelectSingle selectServiceLevelPanel = form.addSelectSinglePanel("serviceLevel", group.getProductsSortedForUI(), new IdPropChoiceRenderer("publicName"), new BootstrapSelectOptions());
			selectServiceLevelPanel.setRequired(true);

//			for (Product product : group.getProducts()) {
//				serviceLevelIds.add(product.getId());
//			}
//			DropDownChoice serviceField = form.addDropDownChoice("serviceLevel", serviceLevelIds, new ChoiceRenderer<Long>() {
//				@Override
//				public Object getDisplayValue(Long id) {
//					return productDao.findById(id).getPublicName();
//				}
//			});
//			serviceField.setRequired(true);
		}

		if (contract.getBusinessArea().hasFeature(FeatureType.PREFERENCES)) {
			if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD)) {
				form.addSliderField("contract.emphasisOnOrdersByPhone", 1, 10, 1);
				form.addSliderField("contract.emphasisOnMainNumberWellKnown", 1, 10, 1);
				form.addSliderField("contract.emphasisOnGoodService", 1, 10, 1);
				form.addSliderField("contract.emphasisOnReceptionist", 1, 10, 1);
				form.addSliderField("contract.emphasisOnTransferCalls", 1, 10, 1);
			} else if (contract.getBusinessArea().hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
				form.addSliderField("contract.emphasisOnFixedMonthlyPrice", 1, 10, 1);
				form.addSliderField("contract.emphasisOnDeviceSecurity", 1, 10, 1);
				form.addSliderField("contract.emphasisOnNewDevices", 1, 10, 1);
				form.addSliderField("contract.emphasisOnTravelling", 1, 10, 1);
				form.addSliderField("contract.emphasisOnInputMobility", 1, 10, 1);
			}
		}

//		List<ResourceLink> list = Lists.newArrayList();
//		list.add(newReportLink("Rabataftale", "One+ - Rabataftale.pdf", new ContractAcceptReport(true, false)));
//		list.add(newReportLink("Rabataftale + Produktspecifikt Bilag", "One+ - Rabataftale + Produktspecifikt Bilag.pdf", new ContractAcceptReport(true, true)));
//		list.add(newReportLink("Produktspecifikt Bilag Tale", "One+ - Produktspecifikt Bilag Tale.pdf", new TilbudRammeaftaleOgPBReport()));
//		list.add(newReportLink("Produktspecifikt Bilag Netværk", "One+ - Produktspecifikt Bilag Netværk.pdf", new CdmOutputReport()));
//
//		add(new ListView<ResourceLink>("rabataftaleDocuments", list) {
//			@Override
//			protected void populateItem(ListItem<ResourceLink> item) {
//				ResourceLink<Void> link = item.getModelObject();
//				item.add(link);
//			}
//		});

//		form.addSliderField("contract.confidenceRating", 1, 10, 1);
		
		AjaxButton prevButton = form.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, contractWithSingleCampaign, navigator.prev(getWebPage()), target);
			}
		});
		AjaxButton nextButton = form.addSubmitButton("action.next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(contract, contractWithSingleCampaign, navigator.next(getWebPage()), target);
			}
		});
	}

	private ResourceLink newReportLink(String title, String outputFileName, Provider<String> report) {
		ReportLink link = new ReportLink("link", outputFileName, report);
		link.add(new Label("title", title));
		return link;
	}

	private void saveAndNavigate(final MobileContract contract, final ContractWithSingleCampaign contractWithSingleCampaign,
								 Class<? extends WebPage> page, AjaxRequestTarget target) {
		Campaign campaign = contractWithSingleCampaign.getCampaign();
		
		if (campaign == null) {
			contract.getCampaigns().clear();
		} else {
			if (!contract.getCampaigns().contains(campaign)) {
				Campaign oldCampaign = contract.getCampaigns().get(0);	// It is a list of exactly one element !
				contract.campaignChanged(oldCampaign, campaign);

//				boolean hasSelectedBundles = false;
//				List<OrderLine> bundleOrderLines = contract.getBundleOrderLines();
//				for (OrderLine orderLine : bundleOrderLines) {
//					if (orderLine.getTotalCount() > 0) {
//						hasSelectedBundles = true;
//						break;
//					}
//				}
//				if (hasSelectedBundles) {
//					target.appendJavaScript("alert('Bemærk: pakkevalg er nu fjernet')");
//				}
//				contract.removeBundleOrderlines();
				contract.getCampaigns().clear();
				contract.getCampaigns().add(campaign);
			}
		}
		if ((contract.getStatusChangedDate() == null) || !contract.getStatus().equals(oldStatus)) {
			contract.setStatusChangedDate(new Date());
		}

		if (!contract.getContractMode().equals(oldContractMode)) {
			// Fjern valg af virksomhedsløsning / mobile only
			contract.removeOrderlines(orderLine -> (orderLine.getBundle() != null) &&
					(((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE)));
		}

		if (contractWithSingleCampaign.getInstallationTypeBusiness() != null) {
			contract.setInstallationTypeBusinessEntityId(contractWithSingleCampaign.getInstallationTypeBusiness().getId());
		} else {
			contract.setInstallationTypeBusinessEntityId(null);
		}

		if (contractWithSingleCampaign.getInstallationTypeUserProfiles() != null) {
			contract.setInstallationTypeUserProfilesEntityId(contractWithSingleCampaign.getInstallationTypeUserProfiles().getId());
		} else {
			contract.setInstallationTypeUserProfilesEntityId(null);
		}

		{
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			MobileProduct product = (MobileProduct) contractWithSingleCampaign.getServiceLevel();
			if (product != null) {
				contract.setServiceLevelEntityId(product.getId());
				product.addToProductToCountsMap(productToCountMap, contract.getNoOfUsers(false), 0, null);
			} else {
				contract.setServiceLevelEntityId(null);
			}
			contract.adjustOrderLinesForProducts(contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_SERVICE.getKey()), productToCountMap, null);
		}

		if (oldPoolsMode != contract.isPoolsMode()) {
			Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>();
			contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.MOBILE_BUNDLE);
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			contract.adjustOrderLinesForProducts(contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_DATA.getKey()), productToCountMap, null);
			contract.adjustOrderLinesForProducts(contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_ILD.getKey()), productToCountMap, null);
		}

		MobileContractMode mcm = contract.getContractMode();
		if (!mcm.isNewAccount() || !mcm.isExistingAccount()) {
			Iterator<OrderLine> orderLineIterator = contract.getOrderLines().iterator();
			while (orderLineIterator.hasNext()) {
				OrderLine orderLine = orderLineIterator.next();
				if (!mcm.isNewAccount()) {
					orderLine.setCountNew(0);
					OrderLineDao.lookup().save(orderLine);
				}
				if (!mcm.isExistingAccount()) {
					orderLine.setCountExisting(0);
					OrderLineDao.lookup().save(orderLine);
				}
			}
		}

//		contractDao.save(contract);
		contractSaver.save(contract);
		
		setResponsePage(page);
	}
}
