package dk.jyskit.salescloud.application.pages.admin.campaignproducts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditProductInCampaignPanel extends AbstractEditPanel<CampaignProductRelation, Campaign> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProductDao productDao;
	
	@Inject
	private CampaignDao parentDao;
	
	private Product origProduct;
	
	private TextField extraRowInOutputCode;
	private TextField extraRowInOutputKvikCode;
	private TextField extraRowInOutputText;
	
	private TextField extraRowInOfferText;

	private CheckBox extraRowInOutput;

	private CheckBox extraRowInOffer;
	
	public EditProductInCampaignPanel(CrudContext context, IModel<CampaignProductRelation> childModel, IModel<Campaign> parentModel) {
		super(context, childModel, parentModel);
		if (childModel != null) {
			origProduct = childModel.getObject().getProduct();
		}
	}
	
	@Override
	public IModel<CampaignProductRelation> createChildModel() {
		return new Model(new CampaignProductRelation());
	}
	
	public void addFormFields(Jsr303Form<CampaignProductRelation> form) {
		List<Product> products = productDao.findByBusinessArea(parentModel.getObject().getBusinessArea());
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product p1, Product p2) {
				return getCompositeName(p1).compareTo(getCompositeName(p2));
			}
		});
		
		form.addSelectSinglePanel("product", products, new ChoiceRenderer<Product>() {
			@Override
			public Object getDisplayValue(Product value) {
				return getCompositeName(value);
			}
		}, new BootstrapSelectOptions());
		
		form.addTextField("rabataftaleCampaignDiscountMatrix");

		form.addTextField("campaignDiscountAmounts.oneTimeFee");
		form.addTextField("campaignDiscountAmounts.installationFee");
		form.addTextField("campaignDiscountAmounts.recurringFee");
		
		form.addTextField("outputCodeOverride");
		form.addTextField("outputCodeKvikOverride");
		form.addTextField("outputTextOverride");
		
		// ----------------------------
		
		extraRowInOutput = form.addCheckBox("extraRowInOutput", new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				boolean visible = extraRowInOutput.getConvertedInput();
				extraRowInOutputCode.getParent().getParent().setVisible(visible);
				target.add(extraRowInOutputCode.getParent().getParent());
				
				extraRowInOutputKvikCode.getParent().getParent().setVisible(visible);
				target.add(extraRowInOutputKvikCode.getParent().getParent());
				
				extraRowInOutputText.getParent().getParent().setVisible(visible);
				target.add(extraRowInOutputText.getParent().getParent());
			}
		});
		 
		extraRowInOutputCode = form.addTextField("extraOutputCode");
		extraRowInOutputCode.getParent().getParent().setVisible(((CampaignProductRelation) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputCode.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputCode.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		extraRowInOutputKvikCode = form.addTextField("extraOutputCodeKvik");
		extraRowInOutputKvikCode.getParent().getParent().setVisible(((CampaignProductRelation) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputKvikCode.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputKvikCode.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		extraRowInOutputText = form.addTextField("extraOutputText");
		extraRowInOutputText.getParent().getParent().setVisible(((CampaignProductRelation) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputText.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputText.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		// ----------------------------
		
		extraRowInOffer = form.addCheckBox("extraRowInOffer", new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				boolean visible = extraRowInOffer.getConvertedInput();
				
				extraRowInOfferText.getParent().getParent().setVisible(visible);
				target.add(extraRowInOfferText.getParent().getParent());
			}
		});
		 
		extraRowInOfferText = form.addTextField("extraRowInOfferText");
		extraRowInOfferText.getParent().getParent().setVisible(((CampaignProductRelation) childModel.getObject()).isExtraRowInOffer());
		extraRowInOfferText.getParent().getParent().setOutputMarkupId(true);
		extraRowInOfferText.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		// ----------------------------
	}

	private String getCompositeName(Product product) {
		return product.getProductGroup().getFullPath() + " - " + product.getProductId() + " - " + product.getPublicName();
	}

	@Override
	protected boolean onSave(final Jsr303Form<CampaignProductRelation> form, AjaxRequestTarget target) {
		if (origProduct != null) {
			parentModel.getObject().removeCampaignProductRelation(origProduct);
			parentDao.save(parentModel.getObject());
		}
		parentModel.getObject().addCampaignProductRelation(childModel.getObject());
		parentDao.save(parentModel.getObject());
		return true;
	}
	
	@Override
	public boolean prepareSave(CampaignProductRelation entity) {
		// Method is never called, since onSave has been overridden
		return true;  
	}

	@Override
	public boolean save(CampaignProductRelation entity, Jsr303Form<CampaignProductRelation> form) {
		// Method is never called, since onSave has been overridden
		return true;  
	}
	
	@Override
	public boolean addToParentAndSave(Campaign parent, CampaignProductRelation child) {
		// Method is never called, since onSave has been overridden
		return true;  
	}
}
