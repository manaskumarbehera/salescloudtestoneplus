package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;

import dk.jyskit.waf.application.JITWicketApplication;
import dk.jyskit.waf.wicket.components.jquery.floathead.FloatHeadBehavior;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.toolbars.filter.FilterPanel;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.toolbars.paging.BootstrapPagingToolbar;
import dk.jyskit.waf.wicket.security.UserSession;

/**
 * A DataTable modified for the Twitter bootstrap look and feel.
 *
 * Original code:
 *
 * se.dmsoftware.bootstraptable.pagingtoolbar.BootstrapPagingToolbar
 *
 *      T: Domain model class S: Class for sorting - typically String
 */
public class BootstrapTable<T, S> extends DataTable<T, S> {
	public static final String SESSION_ATTRIBUTE_NO_OF_RECORDS_PER_PAGE = "ui.table_rows_per_page";
	private Integer noOfRecordsPerPage;
	private String searchText;
	private TransparentWebMarkupContainer table;
	private WebMarkupContainer responsiveContainer;
	private int[] entityStates;
	
	public BootstrapTable(String id, List<IColumn<T, S>> columns, final SortableDataProvider<T, S> dataProvider) {
		this(id, columns, new int[] { 10, 20, 50 }, dataProvider,
				BootstrapTableStyle.toSet("true".equals(JITWicketApplication.get().getSetting("ie8safe")) ? BootstrapTableStyle.IE8_SAFE_STYLES : BootstrapTableStyle.DEFAULT_STYLES), null);
	}

	public BootstrapTable(String id, List<IColumn<T, S>> columns, int[] rowsPerPageOptions,
			final SortableDataProvider<T, S> dataProvider, BootstrapTableStyle[] tableStyles, int[] entityStates) {
		this(id, columns, rowsPerPageOptions, dataProvider, BootstrapTableStyle.toSet(tableStyles), entityStates);
	}

	/**
	 * @param id
	 * @param columns
	 * @param rowsPerPageOptions
	 * @param dataProvider
	 * @param tableStyles
	 */
	public BootstrapTable(String id, List<IColumn<T, S>> columns, int[] rowsPerPageOptions,
			final SortableDataProvider<T, S> dataProvider, Set<BootstrapTableStyle> tableStyles, int[] entityStates) {
		super(id, columns, dataProvider, rowsPerPageOptions[0]);
		this.entityStates = entityStates;

		setOutputMarkupId(true);
		
		responsiveContainer = new TransparentWebMarkupContainer("responsive");
		add(responsiveContainer);
		
		table = new TransparentWebMarkupContainer("table");
		table.add(new AttributeModifier("class", getTableStyles(tableStyles)));
		responsiveContainer.add(table);

		// Number of records per page
		noOfRecordsPerPage = Integer.valueOf(rowsPerPageOptions[0]);
		String userPreferedPageSize = UserSession.get().getUserPreference(getSessionAttributeForDefaultPageSize());
		if (userPreferedPageSize != null) {
			noOfRecordsPerPage = Integer.parseInt(userPreferedPageSize);
		}
		setItemsPerPage(noOfRecordsPerPage);

		FilterPanel<T,S> filterAndRecordsPerPage = createFilterPanel("filterAndRecordsPerPage", rowsPerPageOptions, tableStyles, entityStates);
		add(filterAndRecordsPerPage);

		// Options
		if (tableStyles.contains(BootstrapTableStyle.HEADERTOOLBAR)) {
			addTopToolbar(new AjaxFallbackHeadersToolbar<S>(this, dataProvider));
		}
		if (tableStyles.contains(BootstrapTableStyle.FILTERTOOLBAR)) {
			filterAndRecordsPerPage.setVisible(true);
		} else {
			filterAndRecordsPerPage.setVisible(false);
		}
		if (tableStyles.contains(BootstrapTableStyle.FILTER_STATE)) {
			filterAndRecordsPerPage.setEntityStateFilterVisible(true);
		}
		if (tableStyles.contains(BootstrapTableStyle.PAGINGBOTTOMTOOLBAR)) {
			addBottomToolbar(new BootstrapPagingToolbar(this));
		}
		if (tableStyles.contains(BootstrapTableStyle.FLOATINGHEADER)) {
			table.add(new FloatHeadBehavior(JQueryWidget.getSelector(table)));
		}
	}
	
	public void setResponsive(BootstrapTableResponsiveType type) {
		switch (type) {
		case NONE:
			break;
		case SCROLLABLE:
			responsiveContainer.add(AttributeModifier.replace("class", "table-scrollable"));
			break;
		case FLIP_SCROLL_NARROW:
			responsiveContainer.add(AttributeModifier.replace("class", "flip-scroll"));
			break;
		case FLIP_SCROLL_WIDE:
			responsiveContainer.add(AttributeModifier.replace("class", "flip-scroll-full"));
			break;
		case SCROLLBAR_NARROW:
			responsiveContainer.add(AttributeModifier.replace("class", "table-scrollbar"));
			break;
		case SCROLLBAR_WIDE:
			responsiveContainer.add(AttributeModifier.replace("class", "table-scrollbar-full"));
			break;
		}
	}

	protected FilterPanel<T,S> createFilterPanel(String wicketId, int[] rowsPerPageOptions, Set<BootstrapTableStyle> tableStyles, int[] entityStates) {
		return new FilterPanel<T,S>(wicketId, this, rowsPerPageOptions, tableStyles, entityStates);
	}


	@SuppressWarnings("unchecked")
	public BootstrapTableDataProvider<T,S> getBootstrapDataProvider() {
		return (BootstrapTableDataProvider<T,S>)getDataProvider();
	}

	/**
	 * Hack to get around that someone change DataTable to must have table tag.
	 * Copied form component.
	 */
	protected void onComponentTag(final ComponentTag tag) {
		/** Whether or not the component should print out its markup id into the id attribute */
		final int FLAG_OUTPUT_MARKUP_ID = 0x4000;
		final String MARKUP_ID_ATTR_NAME = "id";

		if (getFlag(FLAG_OUTPUT_MARKUP_ID))
		{
			tag.putInternal(MARKUP_ID_ATTR_NAME, getMarkupId());
		}

		if (getApplication().getDebugSettings().isOutputComponentPath())
		{
			String path = getPageRelativePath();
			path = path.replace("_", "__");
			path = path.replace(":", "_");
			tag.put("wicketpath", path);
		}
		getMarkupSourcingStrategy().onComponentTag(this, tag);
	}

	/**
	 * Gets the name of the session attribute for default page size. Override this to provide different page sizes for different tables.
	 * @return
	 */
	public String getSessionAttributeForDefaultPageSize() {
		return SESSION_ATTRIBUTE_NO_OF_RECORDS_PER_PAGE;
	}

	private String getTableStyles(Set<BootstrapTableStyle> tableStyles) {
		StringBuilder styles = new StringBuilder("table");
		for (BootstrapTableStyle tableStyle : tableStyles) {
			styles.append(" ");
			styles.append(tableStyle.getCssClass());
		}
		return styles.toString();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(CssHeaderItem.forReference(new PackageResourceReference(BootstrapTable.class, "BootstrapTable.css"), "screen"));
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getNoOfRecordsPerPage() {
		return noOfRecordsPerPage;
	}

	public void setNoOfRecordsPerPage(Integer noOfRecordsPerPage) {
		this.noOfRecordsPerPage = noOfRecordsPerPage;
	}
}
