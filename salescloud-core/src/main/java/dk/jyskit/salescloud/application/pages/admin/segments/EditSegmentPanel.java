package dk.jyskit.salescloud.application.pages.admin.segments;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.SegmentDao;
import dk.jyskit.salescloud.application.model.Segment;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditSegmentPanel extends AbstractEditPanel<Segment, Void> {

	@Inject
	private SegmentDao childDao;
	
	public EditSegmentPanel(CrudContext context, IModel<Segment> childModel) {
		super(context, childModel);
	}
	
	@Override
	public IModel<Segment> createChildModel() {
		return new EntityModel<Segment>(new Segment());
	}

	public void addFormFields(Jsr303Form<Segment> form) {
		form.addTextField("name");
		form.addTextField("csvIndex");
	}
	
	@Override
	public boolean prepareSave(Segment entity) {
		return true;
	}

	@Override
	public boolean save(Segment entity, Jsr303Form<Segment> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(Void parent, Segment entity) {
		// no parent
		return true;
	}
}
