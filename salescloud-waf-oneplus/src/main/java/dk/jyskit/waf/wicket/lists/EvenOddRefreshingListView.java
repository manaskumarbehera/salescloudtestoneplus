package dk.jyskit.waf.wicket.lists;

import java.util.Collection;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

/**
 * Same as @see RefreshingListView, except that lines have alternating colors. 
 * TODO: Make sure styling is appropriate with Twitter Bootstrap.
 * 
 * @author jmi
 *
 * @param <T>
 */
public abstract class EvenOddRefreshingListView<T> extends RefreshingListView<T> {
	private static final long serialVersionUID = -9043956446750634099L;

	private String oddClass		= "oddrow";
	private String evenClass	= "evenrow";
	
	public EvenOddRefreshingListView(String id, boolean reuseItems) {
		this(id, null, reuseItems);
	}

	public EvenOddRefreshingListView(String id, IModel<? extends Collection<T>> model, boolean reuseItems) {
		super(id, model, reuseItems);
	}

	@Override
	protected Item<T> newItem(String id, int index, IModel<T> model) {
        return new OddEvenItem<T>(id, index, model) {
            private static final long serialVersionUID = 1L;

            @Override
			protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("class", (getIndex() % 2) == 0 ? evenClass : oddClass);
            }
        };
	}

	public void setOddClass(String oddClass) {
		this.oddClass = oddClass;
	}

	public void setEvenClass(String evenClass) {
		this.evenClass = evenClass;
	}
}