package dk.jyskit.salescloud.application.pages.admin.campaigns;

import java.util.List;

import dk.jyskit.salescloud.application.dao.MobileCampaignDao;
import dk.jyskit.salescloud.application.model.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditCampaignPanel extends AbstractEditPanel<Campaign, BusinessArea> {

	@Inject
	private ObjectFactory objectFactory;
	
	@Inject
	private CampaignDao dao;
	
	@Inject
	private MobileProductBundleDao bundleDao;
	
	@Inject
	private BusinessAreaDao parentDao;

	private AjaxButton addProductsButton;
	private boolean isDefaultCampaign;

	public EditCampaignPanel(CrudContext context, IModel<Campaign> model, final IModel<BusinessArea> parentModel) {
		super(context, model, parentModel);
		
		isDefaultCampaign = ((model != null) && (model.getObject().getFromDate() == null) && (model.getObject().getToDate() == null));
	}
	
	@Override
	public IModel<Campaign> createChildModel() {
		return new EntityModel(objectFactory.createCampaign());
	}

	@Override
	public void addFormFields(Jsr303Form<Campaign> form) {
		form.addTextField("name");
		form.addDatePicker("fromDate");
		form.addDatePicker("toDate");
		form.addDatePicker("extensionFromDate");
		form.addDatePicker("extensionToDate");
		form.addTextField("productId");
		form.addTextField("cdmCode");
//		form.addTextField("productText");
		form.addTextField("filter");
		
		form.addCheckBox("gksValidation");
		form.addCheckBox("allowMixBundles");
		
		form.addTextArea("offerText");
		
		form.addTextArea("campaignBundleHelpText");
		form.addTextArea("productSelectionHelpText");
		
		form.addTextArea("prisaftaleTextMatrix");
		form.addTextArea("prisaftaleTextMatrixNetwork");
		form.addTextArea("prisaftaleTextMatrixPool");

		if (parentModel.getObject().hasFeature(FeatureType.SWITCHBOARD)) {
			form.addTextArea("switchboardHelpText");
			form.addTextArea("switchboardAddonHelpText");
		}
		
		form.addCheckBox("disableContractDiscount");
		
		if (childModel.getObject().getProductBundles().size() == 0) {
			addProductsButton = form.addButton("addProducts", Buttons.Type.Default, new AjaxEventListener() {
				@Override
				public void onAjaxEvent(AjaxRequestTarget target) {
					MobileCampaign campaign = (MobileCampaign) childModel.getObject();
					List<ProductBundle> productBundlesFromPermanentCampaign = parentModel.getObject().getPermanentCampaign().getProductBundles();
					for (ProductBundle existingBundle : productBundlesFromPermanentCampaign) {
						MobileProductBundle bundle = ((MobileProductBundle) existingBundle).clone(false);
						bundle.setCampaign(campaign);
						
						if (BusinessAreas.match(BusinessAreas.TDC_WORKS, parentModel.getObject().getBusinessAreaId()) ||
								BusinessAreas.match(BusinessAreas.ONE_PLUS, parentModel.getObject().getBusinessAreaId())) {
							if (bundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
								bundle.setRabataftaleCampaignDiscountMatrix(parentModel.getObject().getStandardDiscountMatrix());
								bundle.setRabataftaleCampaignDiscountMatrixNetwork(parentModel.getObject().getStandardDiscountMatrixNetwork());
							}
						}
						campaign.getProductBundles().add(bundle);
						MobileCampaignDao.lookup().save(campaign);
					}
					
					for (CampaignProductRelation productRel : parentModel.getObject().getPermanentCampaign().getCampaignProducts()) {
						CampaignProductRelation newProductRel = productRel.clone();
						newProductRel.setCampaign(campaign);
						campaign.getCampaignProducts().add(newProductRel);
					}
					addProductsButton.setVisible(false);
					target.add(addProductsButton);

//					CampaignDao.lookup().save(campaign);
//					CampaignDao.lookup().flush();
//
//					for (ProductBundle bundle : campaign.getProductBundles()) {
//						for (BundleProductRelation productRelation: bundle.getProducts()) {
//							if (productRelation.getProductId() == 0) {
//								productRelation.setProductId(productRelation.getProduct().getId());
//							}
//						}
//					}
//					CampaignDao.lookup().save(campaign);
//					childModel.detach();
				}
			});
		}
	}

	@Override
	public IModel<String> getBreadCrumbText() {
		Campaign entity = childModel.getObject();
		return (entity.isNewObject() ? new StringResourceModel("Campaign.new.caption", this, getDefaultModel()) : Model.of(entity.getName()));
	}

	@Override
	public boolean prepareSave(Campaign entity) {
		boolean looksLikeDefaultCampaign = (entity.getFromDate() == null) && (entity.getToDate() == null);
		if (looksLikeDefaultCampaign && !isDefaultCampaign) {
			error("Fra- eller til-dato skal sættes");
			return false;
		}
		if (!looksLikeDefaultCampaign && isDefaultCampaign) {
			error("Fra- eller til-dato må ikke sættes på \"Ingen kampagne\"");
			return false;
		}
		if ((entity.getToDate() != null) && ((entity.getExtensionFromDate() == null) || (entity.getExtensionToDate() == null))) {
			error("Lukkeperioden skal også sættes");
			return false;
		}
		if ((entity.getToDate() != null) && (entity.getExtensionFromDate() != null) && (entity.getExtensionFromDate().before(entity.getToDate()))) {
			error("Lukkeperioden må ikke starte før kampagneperioden");
			return false;
		}
		if ((entity.getToDate() != null) && (entity.getFromDate() == null) && (entity.getFromDate().after(entity.getToDate()))) {
			error("Fra-dato må ikke ligge efter til-dato for kampagneperioden");
			return false;
		}
		if ((entity.getExtensionToDate() != null) && (entity.getExtensionFromDate() == null) && (entity.getExtensionFromDate().after(entity.getExtensionToDate()))) {
			error("Fra-dato må ikke ligge efter til-dato for lukkeperioden");
			return false;
		}
		return true;
	}

	@Override
	public boolean save(Campaign entity, Jsr303Form<Campaign> form) {
		saveBundles(entity);
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BusinessArea parent, Campaign child) {
		saveBundles(child);
		parent.addCampaign(child);
		parentDao.save(parent);
		return true;
	}

	private void saveBundles(Campaign entity) {
		if ((entity.getProductBundles().size() > 0) && (entity.getProductBundles().get(0).getId() == null)) {
			for (ProductBundle bundle : entity.getProductBundles()) {
				bundle = bundleDao.save((MobileProductBundle) bundle);
				for (BundleProductRelation relation : bundle.getProducts()) {
					relation.setProductBundleId(bundle.getId());
				}
			}
		}
	}
}
