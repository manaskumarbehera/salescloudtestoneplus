package dk.jyskit.salescloud.application.pages.admin.productbundles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.crud.AbstractWrappedEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

/**
 * Edit panel for product bundles in campaigns.
 * 
 * @author jan
 */
public class EditProductBundlePanel extends AbstractWrappedEditPanel<ProductBundle, ProductBundleExtended, Campaign> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ObjectFactory objectFactory;

	@Inject
	private ProductBundleDao childDao;
	
	@Inject
	private CampaignDao parentDao;

	private TextField extraRowInOutputCode;
	private TextField extraRowInOutputKvikCode;
	private TextField extraRowInOutputText;
	
	private TextField extraRowInOfferText;

	private CheckBox extraRowInOutput;

	private CheckBox extraRowInOffer;
	
	public EditProductBundlePanel(CrudContext context, IModel<ProductBundle> model, final IModel<Campaign> parentModel) {
		super(context, model, parentModel);
	}
	
	@Override
	public IModel<ProductBundle> createChildModel() {
		return new EntityModel<ProductBundle>(objectFactory.createProductBundle());
	}
	
	@Override
	public void addFormFields(Jsr303Form<ProductBundleExtended> form) {
		form.addTextField("bundle.publicName");
		form.addTextField("bundle.textInOffer");
		
		form.addCheckBox("bundle.active");
		
		form.addSelectSinglePanel("bundle.bundleType", Arrays.asList(MobileProductBundleEnum.values()), new IChoiceRenderer<MobileProductBundleEnum>() {
			@Override
			public Object getDisplayValue(MobileProductBundleEnum value) {
				return value.getDisplayText();
			}
			@Override
			public String getIdValue(MobileProductBundleEnum value, int index) {
				return String.valueOf(index);
			}
		}, new BootstrapSelectOptions());
		
		form.addTextField("bundle.internalName");
		form.addTextField("bundle.productId"); 
		form.addTextField("bundle.kvikCode");  
		
		form.addCheckBox("bundle.addProductPrices");
		form.addCheckBox("bundle.addProductPricesToBundlePrice");
		
		// ----------------------------
		
		extraRowInOutput = form.addCheckBox("bundle.extraRowInOutput", new AjaxEventListener() {
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
		 
		extraRowInOutputCode = form.addTextField("bundle.extraRowInOutputCode");
		extraRowInOutputCode.getParent().getParent().setVisible(((MobileProductBundle) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputCode.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputCode.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		extraRowInOutputKvikCode = form.addTextField("bundle.extraRowInOutputKvikCode");
		extraRowInOutputKvikCode.getParent().getParent().setVisible(((MobileProductBundle) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputKvikCode.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputKvikCode.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		extraRowInOutputText = form.addTextField("bundle.extraRowInOutputText");
		extraRowInOutputText.getParent().getParent().setVisible(((MobileProductBundle) childModel.getObject()).isExtraRowInOutput());
		extraRowInOutputText.getParent().getParent().setOutputMarkupId(true);
		extraRowInOutputText.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		// ----------------------------
		
		extraRowInOffer = form.addCheckBox("bundle.extraRowInOffer", new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				boolean visible = extraRowInOffer.getConvertedInput();
				
				extraRowInOfferText.getParent().getParent().setVisible(visible);
				target.add(extraRowInOfferText.getParent().getParent());
			}
		});
		 
		extraRowInOfferText = form.addTextField("bundle.extraRowInOfferText");
		extraRowInOfferText.getParent().getParent().setVisible(((MobileProductBundle) childModel.getObject()).isExtraRowInOffer());
		extraRowInOfferText.getParent().getParent().setOutputMarkupId(true);
		extraRowInOfferText.getParent().getParent().setOutputMarkupPlaceholderTag(true);
		
		// ----------------------------
		
		List<Integer> addToContractDiscountOptions = new ArrayList<Integer>();
		addToContractDiscountOptions.add(0);
		addToContractDiscountOptions.add(ProductBundle.FIXED_DISCOUNT_CONTRIBUTION);
		addToContractDiscountOptions.add(ProductBundle.IPSA_DISCOUNT_CONTRIBUTION);
		addToContractDiscountOptions.add(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
		form.addSelectSinglePanel("bundle.addToContractDiscount", addToContractDiscountOptions, new IChoiceRenderer<Integer>() {
			@Override
			public Object getDisplayValue(Integer value) {
				if (value.intValue() == 0) {
					return "Nej";
				} 
				if (value.intValue() == ProductBundle.FIXED_DISCOUNT_CONTRIBUTION) {
					return "Rabat vedr. kontraktlængde";
				} 
				if (value.intValue() == ProductBundle.IPSA_DISCOUNT_CONTRIBUTION) {
					return "Rabat vedr. IPSA";
				} 
				if (value.intValue() == ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION) {
					return "Rabat vedr. rabataftale";
				} 
				return null;
			}
			@Override
			public String getIdValue(Integer object, int index) {
				return String.valueOf(index);
			}
		}, new BootstrapSelectOptions());
		
		form.addTextField("bundle.rabataftaleCampaignDiscountMatrix");
		
		form.addCheckBox("bundle.gks");

		form.addTextField("baseOneTimeFee");
		form.addTextField("baseInstallationFee");
		form.addTextField("baseRecurringFee");
		
//		form.addTextField("discountInternalName");
//		form.addTextField("discountProductId"); 
//		form.addTextField("discountKvikCode");  
		
		form.addTextField("discountOneTimeFee");
		form.addTextField("discountInstallationFee");
		form.addTextField("discountRecurringFee");
		form.addTextField("bundle.flags");
	}

	@Override
	public ProductBundleExtended wrapChild(ProductBundle child) {
		return new ProductBundleExtended((MobileProductBundle) child);
	}

	@Override
	public boolean prepareSave(ProductBundleExtended wrappedChild) {
		return true;
	}

	@Override
	public boolean save(ProductBundleExtended wrappedChild, Jsr303Form<ProductBundleExtended> form, AjaxRequestTarget target) {
		childDao.save(wrappedChild.getProductBundle());
		return true;
	}

	@Override
	public boolean addToParentAndSave(Campaign parent, ProductBundleExtended wrappedChild) {
		ProductBundle child = wrappedChild.getProductBundle();
		childDao.save(child);
		parent.addProductBundle(child);
		parentDao.save(parent);
		return true;
	}
}
