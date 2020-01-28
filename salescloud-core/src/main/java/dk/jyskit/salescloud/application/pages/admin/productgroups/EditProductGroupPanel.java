package dk.jyskit.salescloud.application.pages.admin.productgroups;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.SimpleAbstractEditPanel;

public class EditProductGroupPanel extends AbstractEditPanel<ProductGroup, BusinessArea> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProductGroupDao childDao;
	
	@Inject
	private BusinessAreaDao parentDao;
	
	@Inject
	private ObjectFactory objectFactory;
	
	public EditProductGroupPanel(CrudContext context, final IModel<ProductGroup> childModel, final IModel<BusinessArea> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<ProductGroup> createChildModel() {
		return EntityModel.forEntity(objectFactory.createProductGroup());
	}
	
	@Override
	public void addFormFields(Jsr303Form<ProductGroup> form) {
		form.addTextField("name");
		form.addTextArea("helpMarkdown");
	}

	@Override
	public boolean prepareSave(ProductGroup entity) {
		entity.initSortIndex(childDao.findAll());
		if (Strings.isEmpty(entity.getHelpMarkdown())) {
			entity.setHelpHtml("&nbsp;");
		} else {
			entity.setHelpHtml(Processor.process(entity.getHelpMarkdown()));
		}
		return true;
	}
	
	@Override
	public boolean save(ProductGroup entity, Jsr303Form<ProductGroup> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BusinessArea parent, ProductGroup entity) {
		parent.addProductGroup(entity);
		parentDao.save(parent);
		return true;
	}
}
