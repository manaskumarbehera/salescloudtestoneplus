package dk.jyskit.salescloud.application.pages.admin.sortgroups;

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
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.MobileProductGroup;
import dk.jyskit.salescloud.application.model.MobileSortableItem;
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
public class SortProductGroupsPanel extends CrudPanel {

	@Inject
	private ProductGroupDao productGroupDao;
	
	private SortingType type;
	
	public SortProductGroupsPanel(CrudContext context, final IModel<BusinessArea> parentModel) {
		super(context);
		
		labelKey("header");
		
		Jsr303Form<SortProductGroupsPanel> form = new Jsr303Form<>("form", this);
		add(form);
		
		List<SortingType> types = new ArrayList<>();
		types.add(SortingType.TYPE_UI);
		types.add(SortingType.TYPE_OFFER);
		if (parentModel.getObject().getBusinessAreaId() != BusinessAreas.MOBILE_VOICE) {
			types.add(SortingType.TYPE_PRODUCTION);
		}
		type = SortingType.TYPE_UI;
		
		List<ProductGroup> list = productGroupDao.findByBusinessArea(parentModel.getObject());
		List<MobileProductGroup> originalList = new ArrayList<>();
		for (ProductGroup productGroup : list) {
			originalList.add((MobileProductGroup) productGroup);
		}
		
		final SortingPanel sortingPanel = new SortingPanel<MobileProductGroup>("panel", originalList, type, productGroupDao, new SortingItemFilter<MobileProductGroup>() {
			public boolean includeItem(MobileSortableItem item) {
				return true;
			}
		});
		
		BootstrapSelectSingle typeSelector = form.addSelectSinglePanel("type", types, new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
		typeSelector.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				sortingPanel.selectType(target, type);
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
