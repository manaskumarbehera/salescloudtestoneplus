package dk.jyskit.salescloud.application.pages.admin.sorting;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.MobileSortableItem;
import dk.jyskit.waf.wicket.lists.RefreshingListView;
import dk.jyskit.waf.wicket.utils.WicketUtils;

@Slf4j
public class SortableItemView<T extends MobileSortableItem>  extends RefreshingListView<T> {

	private IModel<List<T>> listModel;

	/**
	 * Constructs
	 * 
	 * @param id
	 *            Wicket ID of list container
	 */
	public SortableItemView(String id, final IModel<List<T>> model) {
		super(id, model, true);
		listModel = model;
	}
	
	@Override
	protected void populateItem(Item<T> item) {
		T entity = item.getModelObject();

		boolean odd = (item.getIndex() % 2 == 0);
		String rowClass = (odd ? "odd" : "even") + "-row";
		item.add(new AttributeAppender("class", rowClass));
		
		item.add(new Label("name", entity.getTextForSorting()));
		
		item.add(createMoveUpButton(item));
		item.add(createMoveDownButton(item));
	}

	/**
	 * Creates button to move productGroup one step up in productGroup list
	 * 
	 * @param item
	 *            productGroup item
	 * @param productGroupPath
	 *            productGroup's path
	 * @return move up button
	 */
	private BootstrapAjaxLink createMoveUpButton(final ListItem<T> item) {
		BootstrapAjaxLink<T> upLink = new BootstrapAjaxLink<T>("up", Type.Default) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				for (int i = 0; i < listModel.getObject().size(); i++) {
					if (item.getModelObject().equals(listModel.getObject().get(i))) {
						SortingPanel sortingPanel = (SortingPanel) WicketUtils.findParentOfClass(SortableItemView.this, SortingPanel.class);
						
						if (SortingType.TYPE_UI.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getSortIndex();
							long sortIndex2 = listModel.getObject().get(i-1).getSortIndex();
							listModel.getObject().get(i).setSortIndex(sortIndex2);
							listModel.getObject().get(i-1).setSortIndex(sortIndex1);
						} else if (SortingType.TYPE_OFFER.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getOfferSortIndex();
							long sortIndex2 = listModel.getObject().get(i-1).getOfferSortIndex();
							listModel.getObject().get(i).setOfferSortIndex(sortIndex2);
							listModel.getObject().get(i-1).setOfferSortIndex(sortIndex1);
						} else if (SortingType.TYPE_PRODUCTION.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getOutputSortIndex();
							long sortIndex2 = listModel.getObject().get(i-1).getOutputSortIndex();
							listModel.getObject().get(i).setOutputSortIndex(sortIndex2);
							listModel.getObject().get(i-1).setOutputSortIndex(sortIndex1);
						}  
						
						sortingPanel.refresh(target);
						break;
					}
				}
			}
		}.setIconType(FontAwesomeIconType.arrow_up).setLabel(Model.of(""));
		
		if (item.getIndex() == 0) {
			upLink.setVisible(false);
		}
		return upLink;
	}

	private BootstrapAjaxLink createMoveDownButton(final ListItem<T> item) {
		BootstrapAjaxLink<T> downLink = new BootstrapAjaxLink<T>("down", Type.Default) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				for (int i = 0; i < listModel.getObject().size(); i++) {
					if (item.getModelObject().equals(listModel.getObject().get(i))) {
						SortingPanel sortingPanel = (SortingPanel) WicketUtils.findParentOfClass(SortableItemView.this, SortingPanel.class);
						
						if (SortingType.TYPE_UI.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getSortIndex();
							long sortIndex2 = listModel.getObject().get(i+1).getSortIndex();
							listModel.getObject().get(i).setSortIndex(sortIndex2);
							listModel.getObject().get(i+1).setSortIndex(sortIndex1);
						} else if (SortingType.TYPE_OFFER.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getOfferSortIndex();
							long sortIndex2 = listModel.getObject().get(i+1).getOfferSortIndex();
							listModel.getObject().get(i).setOfferSortIndex(sortIndex2);
							listModel.getObject().get(i+1).setOfferSortIndex(sortIndex1);
						} else if (SortingType.TYPE_PRODUCTION.equals(sortingPanel.getSelectedType())) {
							long sortIndex1 = listModel.getObject().get(i).getOutputSortIndex();
							long sortIndex2 = listModel.getObject().get(i+1).getOutputSortIndex();
							listModel.getObject().get(i).setOutputSortIndex(sortIndex2);
							listModel.getObject().get(i+1).setOutputSortIndex(sortIndex1);
						}  
						
						sortingPanel.refresh(target);
						break;
					}
				}
			}
		}.setIconType(FontAwesomeIconType.arrow_down).setLabel(Model.of(""));
		
		if (item.getIndex() == listModel.getObject().size() - 1) {
			downLink.setVisible(false);
		}
		return downLink;
	}
}