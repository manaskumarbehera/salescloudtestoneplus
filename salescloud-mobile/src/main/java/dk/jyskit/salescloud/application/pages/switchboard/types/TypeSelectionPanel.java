package dk.jyskit.salescloud.application.pages.switchboard.types;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.SwitchboardInitializer;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.mixbundles.MixBundleEditorPanel;
import dk.jyskit.salescloud.application.pages.switchboard.SwitchboardPage;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.utils.WicketUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.*;

@Slf4j
public class TypeSelectionPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@Inject
	private MobileContractDao contractDao;
	
	@Inject
	private ContractSaver contractSaver;
	
	@Inject
	private OrderLineDao orderLineDao;
	
	@Inject 
	private ProductDao productDao;
	
	@Inject 
	private ProductGroupDao productGroupDao;
	
	@Inject
	private CanOrderFilter canOrderFilter;

	private InstallationType installationType;
	private Product installationTypeBusiness;
	private Product installationTypeUserProfiles;

	private BootstrapSelectSingle<InstallationType> installationTypeDropdown;
	private BootstrapSelectSingle<Product> installationTypeUserProfilesDropdown;
	private BootstrapSelectSingle<Product> installationTypeBusinessDropdown;

	private boolean oldMobileOnly;

	public TypeSelectionPanel(String id, final NotificationPanel notificationPanel, MixBundleEditorPanel mixBundleEditorPanel) {
		super(id);
		
		final MobileContract contract = MobileSession.get().getContract();
		installationType = contract.getInstallationType();

		oldMobileOnly = contract.isMobileOnlySolution();

		if (contract.getInstallationTypeBusinessEntityId() != null) {
			installationTypeBusiness = ProductDao.lookup().findById(contract.getInstallationTypeBusinessEntityId());
		}
		if (contract.getInstallationTypeUserProfilesEntityId() != null) {
			installationTypeUserProfiles = ProductDao.lookup().findById(contract.getInstallationTypeUserProfilesEntityId());
		}

		ProductBundle currentSwitchboardBundle = null;
		for (OrderLine orderLine: contract.getOrderLines()) {
			if ((orderLine.getBundle() != null) && (orderLine.getTotalCount() > 0) && 
					(MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(((MobileProductBundle) orderLine.getBundle()).getBundleType()))) {
				currentSwitchboardBundle = orderLine.getBundle();
				break;
			}
		}

		ProductBundle firstBundle = null;
		ProductBundle lastBundle = null;
		List<ProductBundle> bundles = new ArrayList<>();
		if (BusinessAreas.match(BusinessAreas.TDC_WORKS, contract.getBusinessArea())) {
			bundles.add(null);
		}  // One+ works a bit differenty. It has the product: "Mobile Only"

		for (ProductBundle productBundle : contract.getCampaigns().get(0).getProductBundles()) {
			if (MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(((MobileProductBundle) productBundle).getBundleType())) {
				bundles.add(productBundle);
				lastBundle = productBundle;
				if (firstBundle == null) {
					firstBundle = productBundle;
				}
			}
		}
		
		if (currentSwitchboardBundle == null) {
			if (MobileSession.get().isBusinessAreaOnePlus()) {
				currentSwitchboardBundle = firstBundle;
			} else if (!MobileSession.get().isBusinessAreaTdcWorks()) {
				currentSwitchboardBundle = lastBundle;
			}
		}
		
		final ArrayList<BundleSelection> bundleCountBundleSelectionList = new ArrayList<>();
		for (ProductBundle productBundle : bundles) {
			BundleSelection bundleCount = new BundleSelection((MobileProductBundle) productBundle, (Objects.equals(productBundle, currentSwitchboardBundle)));
			bundleCountBundleSelectionList.add(bundleCount);
		}
		setDefaultModel(new Model(bundleCountBundleSelectionList));
		
		setOutputMarkupId(true);

		WebMarkupContainer installationTypeDropdownContainer = new WebMarkupContainer("installationTypeDropdownContainer");
		installationTypeDropdownContainer.setOutputMarkupPlaceholderTag(true);
		installationTypeDropdownContainer.setOutputMarkupId(true);

		WebMarkupContainer installationUserProfilesContainer = new WebMarkupContainer("installationUserProfilesContainer");
		installationUserProfilesContainer.setOutputMarkupId(true);
		installationUserProfilesContainer.setOutputMarkupPlaceholderTag(true);
		installationUserProfilesContainer.setVisible(contract.isBusinessArea(BusinessAreas.ONE_PLUS));
//		installationUserProfilesContainer.setVisible(contract.isBusinessArea(BusinessAreas.ONE_PLUS) && !contract.isMobileOnlySolution());

		WebMarkupContainer installationBusinessContainer = new WebMarkupContainer("installationBusinessContainer");
		installationBusinessContainer.setOutputMarkupId(true);
		installationBusinessContainer.setOutputMarkupPlaceholderTag(true);
//		installationBusinessContainer.setVisible(contract.isBusinessArea(BusinessAreas.ONE_PLUS));
		installationBusinessContainer.setVisible(contract.isBusinessArea(BusinessAreas.ONE_PLUS) && !contract.isMobileOnlySolution());

		final SwitchboardBundleForm form = new SwitchboardBundleForm("form", (IModel<ArrayList<BundleSelection>>) getDefaultModel()) {
			public void onBundleSelected(AjaxRequestTarget target, MobileProductBundle productBundle) {
				if (MobileSession.get().isBusinessAreaOnePlus()) {
					if (productBundle.getPublicName().toLowerCase().indexOf("only") != -1) {
						if (!oldMobileOnly) {
							installationTypeUserProfiles = null;
							installationTypeBusiness = null;
							if (save()) {
							}
							setResponsePage(new SwitchboardPage(new PageParameters()));
						} else {
							installationUserProfilesContainer.setVisible(false);
							installationBusinessContainer.setVisible(false);
							installationTypeUserProfiles = null;
							installationTypeBusiness = null;
							target.add(installationUserProfilesContainer);
							target.add(installationBusinessContainer);
						}
					} else {
						if (oldMobileOnly) {
							if (save()) {
							}
							setResponsePage(new SwitchboardPage(new PageParameters()));
						} else {
							installationUserProfilesContainer.setVisible(true);
							installationBusinessContainer.setVisible(true);
							target.add(installationUserProfilesContainer);
							target.add(installationBusinessContainer);
						}
					}
				} else {
					target.add(installationTypeDropdownContainer);
				}
			}
		};
		add(form);

		if (MobileSession.get().isBusinessAreaOnePlus()) {
			ProductGroup group = contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_INSTALLATIONTYPE.getKey());

			{
				installationTypeUserProfilesDropdown = new BootstrapSelectSingle<Product>("installationTypeUserProfiles",
						new PropertyModel<Product>(this, "installationTypeUserProfiles"),
						new AbstractReadOnlyModel<List<Product>>() {
							@Override
							public List<Product> getObject() {
								List<Product> products = Lists.newArrayList(group.getProducts());
								Iterator<Product> iter = products.iterator();
								while (iter.hasNext()) {
									Product p = iter.next();
									if (p.getPublicName().toLowerCase().indexOf("onsite") != -1) {
										iter.remove();
									} else if ((p.getPublicName().toLowerCase().indexOf("partner") != -1) || (p.getPublicName().toLowerCase().indexOf("erhverv") != -1)) {
										if (!MobileSession.get().userIsPartner() ||
												contract.getContractMode().equals(MobileContractMode.CONVERSION) ||
												contract.getContractMode().equals(MobileContractMode.CONVERSION_1_TO_1)) {
											iter.remove();
										}
									} else if ((p.getPublicName().toLowerCase().indexOf("remote") != -1)) {
										if (contract.getContractMode().equals(MobileContractMode.CONVERSION_1_TO_1)) {
											iter.remove();
										}
									}
								}
								if (products.size() == 1) {
									installationTypeUserProfiles = products.get(0);
								}
								return products;
							}
						},
						new IdPropChoiceRenderer("publicName")
				);

				installationUserProfilesContainer.add(installationTypeUserProfilesDropdown);
				installationTypeUserProfilesDropdown.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						MobileContract contract = MobileSession.get().getContract();
						if (installationTypeUserProfiles != null) {
							if ((installationTypeUserProfiles.getPublicName().toLowerCase().indexOf("remote") != -1) ||
								(installationTypeUserProfiles.getPublicName().toLowerCase().indexOf("erhverv") != -1)) {
								installationTypeBusiness = installationTypeUserProfiles;
								contract.setInstallationTypeBusinessEntityId(installationTypeBusiness.getId());
							}
							target.add(installationBusinessContainer);
						}
						contract.setInstallationTypeUserProfilesEntityId(installationTypeUserProfiles.getId());
						contractSaver.save(contract);
					}
				});
			}
			{
				installationTypeBusinessDropdown = new BootstrapSelectSingle<Product>("installationTypeBusiness",
						new PropertyModel<Product>(this, "installationTypeBusiness"),
						new AbstractReadOnlyModel<List<Product>>() {

							@Override
							public List<Product> getObject() {
								List<Product> products = Lists.newArrayList(group.getProducts());
								Iterator<Product> iter = products.iterator();
								while (iter.hasNext()) {
									Product p = iter.next();
									if (p.getPublicName().toLowerCase().indexOf("oyo") != -1) {
										iter.remove();
									} else if (p.getPublicName().toLowerCase().indexOf("onsite") != -1) {
										if (!MobileSession.get().userIsPartner()) {
											iter.remove();
										} else {
											if ((installationTypeUserProfiles != null) && installationTypeUserProfiles.getPublicName().toLowerCase().indexOf("oyo") != -1) {
												iter.remove();
											}
										}
									} else if (p.getPublicName().toLowerCase().indexOf("erhverv") != -1) {
										if (!MobileSession.get().userIsPartner()) {
											iter.remove();
										}
									}
								}
								if (products.size() == 1) {
									installationTypeBusiness = products.get(0);
								}
								return products;
							}
						},
						new IdPropChoiceRenderer("publicName")
				);

				installationBusinessContainer.add(installationTypeBusinessDropdown);
				installationTypeBusinessDropdown.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						MobileContract contract = MobileSession.get().getContract();
						contract.setInstallationTypeBusinessEntityId(installationTypeBusiness.getId());
						contractSaver.save(contract);
					}
				});
			}
		} else {
			// Installation - brugere
			installationTypeDropdown = new BootstrapSelectSingle<InstallationType>("installation",
					new PropertyModel<InstallationType>(this, "installationType"),
					new AbstractReadOnlyModel<List<InstallationType>>() {

						@Override
						public List<InstallationType> getObject() {
							List<InstallationType> list = InstallationType.valuesAsList();
							if (!contract.isBusinessArea(BusinessAreas.ONE_PLUS)) {
								for (BundleSelection bundleCount : bundleCountBundleSelectionList) {
									if (bundleCount.getBundle() == null) {
										installationType = InstallationType.NONE;
										MobileContract contract = MobileSession.get().getContract();
										contract.setInstallationType(installationType);
//								contractDao.save(contract);
										contractSaver.save(contract);
									} else {
										if (bundleCount.getBundle().getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE)) {
											if (bundleCount.isSelected()) {
												Product switchboardProduct = null;
												if (contract.isBusinessArea(BusinessAreas.ONE_PLUS)) {
													switchboardProduct = productDao.findByProductGroupAndProductId(MobileSession.get().getBusinessAreaEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED.getKey(), SwitchboardInitializer.PRODUCT_OMSTILLINGSBORD);
												} else {
													switchboardProduct = productDao.findByProductGroupAndProductId(MobileSession.get().getBusinessAreaEntityId(), MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), SwitchboardInitializer.PRODUCT_OMSTILLINGSBORD);
												}
												if (bundleCount.getBundle().hasRelationToProduct(switchboardProduct)) {
													// Den store omstillingspakke (med omstillingsbord)
													Iterator<InstallationType> iterator = list.iterator();
													while (iterator.hasNext()) {
														InstallationType iType = iterator.next();
														if (iType.equals(InstallationType.TDC_REMOTE)) {
															iterator.remove();
														} else if (iType.equals(InstallationType.NONE) && (!MobileContractMode.RENEGOTIATION.equals(contract.getContractMode()))) {
															iterator.remove();
														} else if (iType.equals(InstallationType.PARTNER) && (!MobileSession.get().userIsPartner())) {
															iterator.remove();
														}
													}
												} else {
													Iterator<InstallationType> iterator = list.iterator();
													while (iterator.hasNext()) {
														InstallationType iType = iterator.next();
														if (iType.equals(InstallationType.TDC_ON_SITE)) {
															iterator.remove();
														} else if (iType.equals(InstallationType.PARTNER)) {
															iterator.remove();
														} else if (iType.equals(InstallationType.NONE) && (!MobileContractMode.RENEGOTIATION.equals(contract.getContractMode()))) {
															iterator.remove();
														}
													}
												}
												if (!list.contains(installationType)) {
													installationType = list.get(0);
													MobileContract contract = MobileSession.get().getContract();
													contract.setInstallationType(installationType);
//											contractDao.save(contract);
													contractSaver.save(contract);
												}
												break;
											}
										}
									}
								}
							}

							return list;
						}
					},
					new IdPropChoiceRenderer("text")
			);

			installationTypeDropdownContainer.add(installationTypeDropdown);
			installationTypeDropdown.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					MobileContract contract = MobileSession.get().getContract();
					contract.setInstallationType(installationType);
					contractSaver.save(contract);
//				contractDao.save(contract);
				}
			});
		}

		installationTypeDropdownContainer.setVisible(!contract.isBusinessArea(BusinessAreas.ONE_PLUS));

		form.add(installationTypeDropdownContainer);
		form.add(installationUserProfilesContainer);
		form.add(installationBusinessContainer);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		WicketUtils.renderCssByFileName(response, TypeSelectionPanel.class, "bundles.css");
		WicketUtils.renderJQueryOnDomReady(response, "$('.bundle-product-primary-container').each(function() { if ($(this).children().length == 0) { $(this).parent().addClass('empty'); } });");
	}

	public boolean save() {
		MobileContract contract = (MobileContract) CoreSession.get().getContract();
		contract.adjustOrderLinesForBundles(contract, (ArrayList<BundleSelection>) getDefaultModelObject(), MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
		MobileSession.get().getContract().adjustOrderLinesForRemoteInstallation();

		for (OrderLine orderLine : contract.getOrderLines()) {
			orderLineDao.save(orderLine);
		}
		
		// There is a special rule we have to apply here. The bundle contains installation products. Not all of them should create orderlines.
		// The installation type determines which to include.
		ProductGroup productGroup = productGroupDao.findByBusinessAreaAndUniqueName(contract.getBusinessArea(), MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey());

		if (!MobileSession.get().isBusinessAreaOnePlus()) {
			String[] allProductIds = new String[] {"0727500", "4400278", "4400279", "4400290"};
			String[] productIds = new String[] {};
			if (InstallationType.TDC_REMOTE.equals(contract.getInstallationType())) {
				productIds = new String[] {"0727500"};
			} else if (InstallationType.TDC_ON_SITE.equals(contract.getInstallationType())) {
				productIds = new String[] {"4400278", "4400279", "4400290"};
			}
			for (String productId : allProductIds) {
				Product product = productDao.findByProductGroupAndProductId(contract.getBusinessArea().getId(), productGroup.getUniqueName(), productId);
				if (product == null) {
					log.error("Product not found: " + productId);
				} else {
					int countNew = 0;
					int countExisting = 0;
					if (ArrayUtils.contains(productIds, productId)) {
						if (MobileContractMode.RENEGOTIATION.equals(contract.getContractMode())) {
							countExisting = 1;
						} else {
							countNew = 1;
						}
					}
					int subIndex = 0;
					contract.adjustOrderLineForProduct(product, subIndex, countNew, countExisting);
				}
			}
		}

		// Another twist: Choices you make here affect which products can be ordered on the bundle addon tab. So
		// clean up orderlines
		for (MobileProductGroupEnum groupType : new MobileProductGroupEnum[] {
				MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_DATA,
				MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_POOL_ILD,
				MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY,
				MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_FEATURES
		}) {
			for (OrderLine orderLine : contract.getOrderLines()) {
				if ((orderLine.getProduct() != null) && (orderLine.getProduct().getProductGroup().getUniqueName().equals(groupType.getKey()))) {
					if ((orderLine.getTotalCount() > 0) && !canOrderFilter.accept(orderLine.getProduct())) {
						orderLine.setCountNew(0);
						orderLine.setCountExisting(0);
						orderLineDao.save(orderLine);
					}
				}
			}
		}

//		contractDao.save(contract);
		contractSaver.save(contract);

		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (!contract.isMobileOnlySolution() && (installationTypeBusiness == null)) {
				return false;
			}
			if (installationTypeUserProfiles == null) {
				return false;
			}
		}
		return true;
	}
}
