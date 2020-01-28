package dk.jyskit.salescloud.application.pages.admin.pageinfo;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditPageInfoPanel extends AbstractEditPanel<PageInfo, BusinessArea> {
	private static final long serialVersionUID = 1L;

	@Inject
	private PageInfoDao dao;
	
	@Inject
	private BusinessAreaDao businessAreaDao;
	
	public EditPageInfoPanel(CrudContext context, final IModel<PageInfo> childModel, final IModel<BusinessArea> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<PageInfo> createChildModel() {
		return new EntityModel<PageInfo>(new PageInfo());
	}
	
	@Override
	public void addFormFields(Jsr303Form<PageInfo> form) {
		form.addTextField("title");
		form.addTextField("subTitle");
		form.addTextArea("introMarkdown");
		form.addTextArea("miscMarkdown");
		form.addTextArea("helpMarkdown");
	}
	
	@Override
	public boolean prepareSave(PageInfo entity) {
		if (Strings.isEmpty(entity.getIntroMarkdown())) {
			entity.setIntroHtml("&nbsp;");
		} else {
			entity.setIntroHtml(Processor.process(entity.getIntroMarkdown()));
		}
		if (Strings.isEmpty(entity.getMiscMarkdown())) {
			entity.setMiscHtml("&nbsp;");
		} else {
			entity.setMiscHtml(Processor.process(entity.getMiscMarkdown()));
		}
		if (Strings.isEmpty(entity.getHelpMarkdown())) {
			entity.setHelpHtml("&nbsp;");
		} else {
			entity.setHelpHtml(Processor.process(entity.getHelpMarkdown()));
		}
		return true;
	}

	@Override
	public boolean save(PageInfo entity, Jsr303Form<PageInfo> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BusinessArea parent, PageInfo entity) {
		parent.addPage(entity);
		businessAreaDao.save(parent);
		return true;
	}
}
