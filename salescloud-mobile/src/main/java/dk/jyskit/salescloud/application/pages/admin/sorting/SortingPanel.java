package dk.jyskit.salescloud.application.pages.admin.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

import dk.jyskit.salescloud.application.model.MobileSortableItem;
import dk.jyskit.salescloud.application.model.MobileSortableItemImpl;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseEntity;

public class SortingPanel<T extends MobileSortableItem> extends Panel {
//	private Map<String, List<MobileSortableItem>> typeToList = new HashMap<>();
	private SortingType selectedType;
	private LoadableDetachableModel<List<MobileSortableItem>> model;
	private List<T> originalList;
	private List<MobileSortableItem> sortableList;
	private Dao<BaseEntity> dao;
	private SortingItemFilter<T> selectedFilter;
	
	public SortingPanel(String id, final List<T> originalList, SortingType defaultType, Dao dao, final SortingItemFilter<T> filter) {
		super(id);
		this.originalList = originalList;
		this.dao = dao;
		this.selectedFilter = filter;
		setOutputMarkupId(true);
		selectedType = defaultType;
		
		model = new LoadableDetachableModel<List<MobileSortableItem>>() {
			@Override
			protected List<MobileSortableItem> load() {
				if (sortableList == null) {
					sortableList = new ArrayList<>();
					Set<Long> usedUIIndexes = new HashSet<>();
					Set<Long> usedOfferIndexes = new HashSet<>();
					Set<Long> usedOutputIndexes = new HashSet<>();
					for (T entity : originalList) {
						MobileSortableItemImpl item = new MobileSortableItemImpl();
						item.setSortIndex(entity.getSortIndex());
						item.setOfferSortIndex(entity.getOfferSortIndex());
						item.setOutputSortIndex(entity.getOutputSortIndex());
						item.setTextForSorting(entity.getTextForSorting());
						item.setOriginalEntity(entity);
						
						sortableList.add(item);
						
						if ((item.getSortIndex() == 0) || (!usedUIIndexes.add(Long.valueOf(item.getSortIndex())))) {
							// find next available index
							long index = 1;
							while (true) {
								if (usedUIIndexes.add(Long.valueOf(index))) {
									item.setSortIndex(index);
									break;
								}
								index++;
							}
						}
						if ((item.getOfferSortIndex() == 0) || (!usedOfferIndexes.add(Long.valueOf(item.getOfferSortIndex())))) {
							// find next available index
							long index = 1;
							while (true) {
								if (usedOfferIndexes.add(Long.valueOf(index))) {
									item.setOfferSortIndex(index);
									break;
								}
								index++;
							}
						}
						if ((item.getOutputSortIndex() == 0) || (!usedOutputIndexes.add(Long.valueOf(item.getOutputSortIndex())))) {
							// find next available index
							long index = 1;
							while (true) {
								if (usedOutputIndexes.add(Long.valueOf(index))) {
									item.setOutputSortIndex(index);
									break;
								}
								index++;
							}
						}
					}
				}
				
				List<MobileSortableItem> filteredList = new ArrayList<>();
				for (MobileSortableItem item : sortableList) {
					if (selectedFilter.includeItem(((MobileSortableItemImpl) item).getOriginalEntity())) {
						filteredList.add(item);
					}
				}
				
				Collections.sort(filteredList, new Comparator<MobileSortableItem>() {
					@Override
					public int compare(MobileSortableItem o1, MobileSortableItem o2) {
						if (SortingType.TYPE_UI.equals(selectedType)) {
							return Long.valueOf(o1.getSortIndex()).compareTo(Long.valueOf(o2.getSortIndex()));
						} else if (SortingType.TYPE_OFFER.equals(selectedType)) {
							return Long.valueOf(o1.getOfferSortIndex()).compareTo(Long.valueOf(o2.getOfferSortIndex()));
						} else if (SortingType.TYPE_PRODUCTION.equals(selectedType)) {
							return Long.valueOf(o1.getOutputSortIndex()).compareTo(Long.valueOf(o2.getOutputSortIndex()));
						}
						return 0;
					}
				});
				return filteredList;
			}
		};
		add(new SortableItemView<MobileSortableItem>("list", model));
	}

	public void selectType(AjaxRequestTarget target, SortingType type) {
		selectedType = type;
		refresh(target);
	}

	public void setFilter(AjaxRequestTarget target, SortingItemFilter<T> filter) {
		this.selectedFilter = filter;
		refresh(target);
	}

	public void refresh(AjaxRequestTarget target) {
		model.detach();
		target.add(this);
	}

	public SortingType getSelectedType() {
		return selectedType;
	}

	public void save() {
		for (T entity : originalList) {
			for (MobileSortableItem sortedEntity : sortableList) {
				if (entity.getTextForSorting().equals(sortedEntity.getTextForSorting())) {
					entity.setSortIndex(sortedEntity.getSortIndex());
					entity.setOfferSortIndex(sortedEntity.getOfferSortIndex());
					entity.setOutputSortIndex(sortedEntity.getOutputSortIndex());
					break;
				}
			}
			dao.save((BaseEntity) entity);
		}
	}
}
