package dk.jyskit.waf.wicket.components.tables.bootstraptable.toolbars.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTable;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle;
import dk.jyskit.waf.wicket.security.UserSession;

public class FilterPanel<T,S> extends Panel {
	private final class NofRecordsPerUpdateBehavior extends AjaxFormComponentUpdatingBehavior {
		private NofRecordsPerUpdateBehavior(String event) {
			super(event);
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			bsTable.setItemsPerPage(bsTable.getNoOfRecordsPerPage());
			UserSession.get().storeUserPreference(bsTable.getSessionAttributeForDefaultPageSize(), Integer.toString(bsTable.getNoOfRecordsPerPage()));
			target.add(bsTable);
		}
	}

	private BootstrapTable<T, S> bsTable;
	private FormComponent<Integer> entityStateFilterSelect;
	private TextField<String> filterTextField;
	private AjaxButton filterButton;
	private int[] entityStates;

	public FilterPanel(String id, BootstrapTable<T, S> table, int[] rowsPerPageOptions, Set<BootstrapTableStyle> tableStyles, int[] entityStates) {
		super(id);
		this.bsTable = table;
		this.entityStates = entityStates;
		List<Integer> noOfRecordsOptions = new ArrayList<Integer>();
		for (Integer rowsPerPage : rowsPerPageOptions) {
			noOfRecordsOptions.add(rowsPerPage);
		}
		FormComponent<Integer> noOfRecordsPerPageDropdown = createNofRecordsPageChoice("noOfRecords", new PropertyModel<Integer>(bsTable, "noOfRecordsPerPage"), noOfRecordsOptions);
		add(noOfRecordsPerPageDropdown);

		noOfRecordsPerPageDropdown.add(new NofRecordsPerUpdateBehavior("change"));
		if (rowsPerPageOptions.length < 2) {
			noOfRecordsPerPageDropdown.setVisible(false);
		}

		// Filter

		final Form<Object> filterForm = new Form<Object>("filterForm");
		add(filterForm);

		entityStateFilterSelect = createEntityStateFilter("stateFilter");
		entityStateFilterSelect.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(bsTable);
			}
		});
		filterForm.add(entityStateFilterSelect);

		filterTextField = new TextField<String>("searchText", new PropertyModel<String>(bsTable, "searchText"));
		filterForm.add(filterTextField);

		filterButton = new AjaxButton("filter") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				bsTable.getBootstrapDataProvider().setFilter(bsTable.getSearchText());
				target.add(bsTable);
			}
		};
		filterForm.add(filterButton);
		filterForm.setDefaultButton(filterButton);

		setSearchFilterVisible(tableStyles.contains(BootstrapTableStyle.FILTER_SEARCH));
		setEntityStateFilterVisible(tableStyles.contains(BootstrapTableStyle.FILTER_STATE));
	}

	public void setEntityStateFilterVisible(boolean visible) {
		entityStateFilterSelect.setVisible(visible);
	}

	public void setSearchFilterVisible(boolean visible) {
		filterTextField.setVisible(visible);
		filterButton.setVisible(visible);
	}

	private FormComponent<Integer> createEntityStateFilter(String wicketId) {
		BootstrapSelectOptions options = new BootstrapSelectOptions().withWidthAuto();
		List<Integer> choices = new ArrayList();
		if (entityStates != null) {
			for (int state : entityStates) {
				choices.add(Integer.valueOf(state));
			}
		}
		
		IModel<Integer> selectModel = new IModel<Integer>() {
			@Override
			public Integer getObject() {
				List<Integer> filterEntityStates = bsTable.getBootstrapDataProvider().getEntityStateFilter();
				if (filterEntityStates.isEmpty()) {
					return null;
				} else {
					return filterEntityStates.iterator().next();
				}
			}

			@Override
			public void setObject(Integer object) {
				List<Integer> filterEntityStates = bsTable.getBootstrapDataProvider().getEntityStateFilter();
				filterEntityStates.clear();
				if (object != null) {
					filterEntityStates.add(object);
				}
			}

			@Override
			public void detach() {
			}
		};
		
		if (choices.size() > 0) {
			selectModel.setObject(Integer.valueOf(entityStates[0]));
		}
		
		IChoiceRenderer<? super Integer> renderer = new IChoiceRenderer<Integer>() {
			@Override
			public Object getDisplayValue(Integer object) {
				return EntityState.of(object).getDisplayModel().getObject();
			}

			@Override
			public String getIdValue(Integer object, int index) {
				return object == null ? null : Integer.toString(object);
			}
		};
		BootstrapSelectSingle<Integer> bootstrapSelectSingle = new BootstrapSelectSingle<Integer>(wicketId, selectModel, choices, renderer ).withShowTick();
		options.setStyle("btn-sm btn-default");
		bootstrapSelectSingle.setOptions(options);
//		bootstrapSelectSingle.setNullValid(true);
		return bootstrapSelectSingle;
	}


	protected FormComponent<Integer> createNofRecordsPageChoice(String wicketId, PropertyModel<Integer> nofRecordsPerPageModel, List<Integer> noOfRecordsOptions) {
		BootstrapSelectOptions options = new BootstrapSelectOptions().withWidthAuto();
		BootstrapSelectSingle<Integer> bootstrapSelectSingle = new BootstrapSelectSingle<Integer>(wicketId, nofRecordsPerPageModel, noOfRecordsOptions).withShowTick();
		options.setStyle("btn-sm btn-default");
		bootstrapSelectSingle.setOptions(options);
		return bootstrapSelectSingle;
	}
}
