package dk.jyskit.waf.wicket.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.waf.application.JITWicketApplication;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.components.panels.confirmation.ConfirmDialog;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTable;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.PropertyColumnWithCellCss;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.MultiActionsColumn;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.MultiActionsPanel;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkmark.CheckmarkColumn;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkmark.CheckmarkColumn.Markup;
import dk.jyskit.waf.wicket.utils.IAjaxCall;

public abstract class AbstractListPanel<T extends Serializable, P> extends CrudPanel {

	private static final long serialVersionUID = 1L;

	protected BootstrapTable<T, String> table;

	private final ILabelStrategy labelStrategy;
	
	private IModel<P> parentModel;

	private String breadCrumbText;

	public AbstractListPanel(CrudContext context, String simpleClassName, int ... entityStates) {
		this(context, new EntityLabelStrategy(simpleClassName), simpleClassName, null, entityStates);
	}

	public AbstractListPanel(CrudContext context, String simpleClassName, IModel<P> parentModel, int ... entityStates) {
		this(context, new EntityLabelStrategy(simpleClassName), simpleClassName, parentModel, entityStates);
	}

	public AbstractListPanel(CrudContext context, ILabelStrategy labelStrategy, String namespace, IModel<P> parentModel, int ... entityStates) {
		super(context.clone());
		
		this.context.setNamespace(namespace);
		
		this.parentModel = parentModel;
		this.labelStrategy = labelStrategy;
		
		setOutputMarkupId(true);
		
		labelKey(namespace + ".entities");
		
		breadCrumbText = getString(namespace + ".entities");
		if (parentModel != null) {
			breadCrumbText = getParentModel().getObject().toString() + " :: " + breadCrumbText;
		}

		List<IColumn<T, String>> cols = new ArrayList<IColumn<T, String>>();
		cols.add(actionsColumn(new ResourceModel("actions")));
		addDataColumns(cols);

		BootstrapTableDataProvider dataProvider = (BootstrapTableDataProvider) getDataProvider();
		boolean includeFilter = getIncludeFilter();
		if (dataProvider instanceof DaoTableDataProvider) {
			if (((DaoTableDataProvider) dataProvider).getFilterProps().length == 0) {
				includeFilter = false;
			}
		}
		
		BootstrapTableStyle[] styles = BootstrapTableStyle.DEFAULT_STYLES;
		if (!includeFilter) {
			styles = ArrayUtils.removeElement(styles, BootstrapTableStyle.FILTER_SEARCH);
			styles = ArrayUtils.removeElement(styles, BootstrapTableStyle.FILTERTOOLBAR);
		}
		
//		boolean includeStateFilter = getIncludeStateFilter();
		
//		List<Integer> entityStateValues = getEntityStateValues(); 
		if (entityStates.length > 0) {
			styles = ArrayUtils.add(styles, BootstrapTableStyle.FILTER_STATE);
			this.context.setEntityStates(entityStates);
		}
		
		if ("true".equals(JITWicketApplication.get().getSetting("ie8safe"))) {
			styles = ArrayUtils.removeElement(styles, BootstrapTableStyle.FLOATINGHEADER);
		}
		
		table = new BootstrapTable<T, String>("dataTable", cols, getTableRowsPerPageOptions(), dataProvider, styles, entityStates); 
		add(table);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if (getParentModel() != null) {
			getParentModel().detach();
		}
	} 

	protected boolean getIncludeFilter() {
		return true;
	}

//	protected List<Integer> getEntityStateValues() {
//		return null;
//	}

	protected int[] getTableRowsPerPageOptions() {
		return new int[] { 10, 20, 50 };
	}

	protected abstract BootstrapTableDataProvider<T, String> getDataProvider();

	protected abstract void addDataColumns(List<IColumn<T, String>> cols);

	protected abstract Panel createEditPanel(CrudContext context, IModel<T> model);
	
	protected abstract void deleteObject(T obj);
	
	protected abstract void saveEntityWithNewState(T entity);

	protected PropertyColumnWithCellCss<T> createColumn(String property) {
		return createColumn(property, property);
	}

	protected PropertyColumnWithCellCss<T> createColumnNonSort(String property) {
		return createNonSortColumn(property, property);
	}

	protected PropertyColumnWithCellCss<T> createColumn(String property, String headerKey) {
		return new PropertyColumnWithCellCss<T>(labelStrategy.columnLabel(headerKey), property, property);
	}

	protected PropertyColumnWithCellCss<T> createNonSortColumn(String property, String headerKey) {
		return new PropertyColumnWithCellCss<T>(labelStrategy.columnLabel(headerKey), property);
	}

	protected CheckmarkColumn<T> createCheckColumn(String property) {
		return createCheckColumn(property, property);
	}

	protected CheckmarkColumn<T> createCheckColumn(String property, String headerKey) {
		return new CheckmarkColumn<T>(labelStrategy.columnLabel(headerKey), property, property).style(Markup.BALLOT_X);
	}

	protected CrudEntityAction<T> getEditAction() {
		CrudEntityAction<T> editAction = new CrudEntityAction<T>(context, getKey(context.getNamespace() + ".edit.link"), getKey(context.getNamespace() + ".edit.tooltip"), FontAwesomeIconType.pencil) {
			@Override
			public Panel createPanel(CrudContext context, IModel<T> model) {
				return createEditPanel(context, model);
			}
		};
		return editAction;
	}

	@SuppressWarnings("unchecked")
	protected EntityAction<T>[] getRowActions() {
		EntityAction<?>[] actions = {getEditAction(), getDeleteAction()};
		return (EntityAction<T>[]) actions;
	}

	@SuppressWarnings("unchecked")
	protected EntityAction<T>[] getHeaderActions() {
		EntityAction<?>[] actions = {getNewAction()};
		return (EntityAction<T>[]) actions;
	}

	protected CrudEntityAction<T> getDeleteAction() {
		CrudEntityAction<T> deleteAction = new CrudEntityAction<T>(context, getKey(context.getNamespace() + ".delete.link"), getKey(context.getNamespace() + ".delete.tooltip"), FontAwesomeIconType.trash_o) {
			@Override
			public void onClick(final IModel<T> model, AjaxRequestTarget target) {
				List<String> messages = predeleteCheck(model.getObject());
				AbstractListPanel<T,P> reporter = AbstractListPanel.this;
				
				if (messages.isEmpty()) {
					IAjaxCall onConfirm = new IAjaxCall() {
						@Override
						public void invoke(AjaxRequestTarget target) {
							deleteObject(model.getObject());
							if (model.getObject() instanceof BaseEntity) {
								Lookup.lookup(EntityManager.class).flush();
							}
							target.add(table);
						}

					};
					StringResourceModel titleModel = new StringResourceModel(getKey(context.getNamespace() + ".delete.confirm.title"), reporter, model);
					StringResourceModel text = new StringResourceModel(getKey(context.getNamespace() + ".delete.confirm.text"), reporter, model);
					new ConfirmDialog(titleModel, text).confirmer(onConfirm).show();
				} else {
					StringResourceModel text = new StringResourceModel(getKey(context.getNamespace() + ".delete.messages.text"),	reporter, model);
					for (String msg : messages) {
						reporter.error(msg);
					}
					new ConfirmDialog(Model.of(reporter.getString(context.getNamespace() + ".delete.messages.title")), text, false).show();
				}
			}
			
			@Override
			public Panel createPanel(CrudContext context, IModel<T> model) {
				return null;
			}
		};
		return deleteAction;
	}

	protected CrudEntityAction<T> getToggleEntityStateAction() {
		CrudEntityAction<T> action = new CrudEntityAction<T>(context, getKey(context.getNamespace() + ".toggle_active.link"), getKey(context.getNamespace() + ".toggle_active.tooltip"), FontAwesomeIconType.rotate_right) {
			@Override
			public void onClick(final IModel<T> model, AjaxRequestTarget target) {
				BaseEntity entity = (BaseEntity) model.getObject();
				if (entity.getEntityState().equals(EntityState.ACTIVE)) {
					entity.setEntityState(EntityState.INACTIVE);
					saveEntityWithNewState((T) entity);
					Lookup.lookup(EntityManager.class).flush();
					target.add(table);
				} else if (entity.getEntityState().equals(EntityState.INACTIVE)) {
					entity.setEntityState(EntityState.ACTIVE);
					saveEntityWithNewState((T) entity);
					Lookup.lookup(EntityManager.class).flush();
					target.add(table);
				}
			}
			
			@Override
			public Panel createPanel(CrudContext context, IModel<T> model) {
				return null;
			}
		};
		return action;
	}

	/**
	 * Override this to provide a pre delete check. Any messages will prevent deletion.
	 * @param object
	 * @return
	 */
	protected List<String> predeleteCheck(T object) {
		return new ArrayList<>();
	}

	protected CrudEntityAction<T> getNewAction() {
		CrudEntityAction<T> action = new CrudEntityAction<T>(context, getKey(context.getNamespace() + ".new.link"), getKey(context.getNamespace() + ".new.tooltip"), FontAwesomeIconType.plus) {
			@Override
			public Panel createPanel(CrudContext context, IModel<T> model) {
				return createEditPanel(context, null);
			}
		};
		return action;
	}
	
	/**
	 * Hook for modifying localization key before rendering.
	 * @param key
	 * @return
	 */
	protected String getKey(String key) {
		return key;
	}

	protected MultiActionsColumn<T, String> actionsColumn(IModel<String> labelModel) {
		return new MultiActionsColumn<T, String>(labelModel, getRowActions()) {
			public Component getHeader(String componentId) {
				return new MultiActionsPanel<>(componentId, new Model<T>(null), getHeaderActions());
			};
		};
	}

	public BootstrapTable<T, String> getTable() {
		return table;
	}

	public ILabelStrategy getLabelStrategy() {
		return labelStrategy;
	}

	public String getNamespace() {
		return context.getNamespace();
	}

	public IModel<P> getParentModel() {
		return parentModel;
	}

	@Override
	public IModel<String> getBreadCrumbText() {
		return Model.of(breadCrumbText);
	}
}
