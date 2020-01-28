package dk.jyskit.salescloud.application.pages.admin.sortproducts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileSortableItem;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.pages.admin.sorting.SortingItemFilter;
import dk.jyskit.salescloud.application.pages.admin.sorting.SortingPanel;
import dk.jyskit.salescloud.application.pages.admin.sorting.SortingType;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudPanel;

@SuppressWarnings("serial")
public class SortProductsPanel extends CrudPanel {

	@Inject
	private ProductDao productDao;
	
	@Inject
	private ProductGroupDao productGroupDao;
	
	private SortingType type;

	private ProductGroup productGroup;
	
	public SortProductsPanel(CrudContext context, final IModel<BusinessArea> parentModel) {
		super(context);
		
		labelKey("header");
		
		Jsr303Form<SortProductsPanel> form = new Jsr303Form<>("form", this);
		add(form);
		
		List<SortingType> types = new ArrayList<SortingType>();
		types.add(SortingType.TYPE_UI);
		types.add(SortingType.TYPE_OFFER);
		if (parentModel.getObject().getBusinessAreaId() != BusinessAreas.MOBILE_VOICE) {
			types.add(SortingType.TYPE_PRODUCTION);
		}
		type = SortingType.TYPE_UI;
		
		List<ProductGroup> productGroups = productGroupDao.findByBusinessArea(parentModel.getObject());
		productGroup = productGroups.get(0);
		
		List<Product> list = productDao.findByBusinessArea(parentModel.getObject());
		List<MobileProduct> originalList = new ArrayList<>();
		for (Product product : list) {
			originalList.add((MobileProduct) product);
		}
		
		final SortingPanel sortingPanel = new SortingPanel<MobileProduct>("panel", originalList, type, productDao, new SortingItemFilter<MobileProduct>() {
			public boolean includeItem(MobileSortableItem item) {
				return productGroup.equals(((MobileProduct) item).getProductGroup());
			}
		});
		
		BootstrapSelectSingle typeSelector = form.addSelectSinglePanel("type", types, new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
		typeSelector.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				sortingPanel.selectType(target, type);
			}
		});
		
		BootstrapSelectSingle groupSelector = form.addSelectSinglePanel("productGroup", productGroups, new IdPropChoiceRenderer("fullPath"), new BootstrapSelectOptions());
		groupSelector.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				sortingPanel.setFilter(target, new SortingItemFilter<MobileProduct>() {
					@Override
					public boolean includeItem(MobileSortableItem item) {
						return productGroup.equals(((MobileProduct) item).getProductGroup());
					}
				});
			}
		});
		
		form.addPanel(sortingPanel, true);
		
		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				sortingPanel.save();
				goBack(target);
			}
		});
		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				goBack(target);
			}
		});
	}

	private void goBack(AjaxRequestTarget target) {
		List<IBreadCrumbParticipant> participants = getBreadCrumbModel().allBreadCrumbParticipants();
		IBreadCrumbParticipant breadCrumbParticipant = participants.get(participants.size()-2);
		getBreadCrumbModel().setActive(breadCrumbParticipant);
		context.getRootMarkupContainer().addOrReplace(breadCrumbParticipant.getComponent());
		target.add(breadCrumbParticipant.getComponent());
		target.add((Breadcrumb) getBreadCrumbModel());
	}
	
	@Override
	public IModel<String> getBreadCrumbText() {
		return Model.of("Sortering af produktgrupper");
	}
}
