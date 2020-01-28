package dk.jyskit.waf.wicket.lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * The simplest repeater to use is propably listview. However, RefreshingView works 
 * better with Ajax. On the downside, RefreshingView is more complicated to use.
 * This class makes it easy to use RefreshingView.
 * 
 * @author jmi
 *
 * @param <T>
 */
public abstract class RefreshingListView<T> extends RefreshingView<T> {
	private static final long serialVersionUID = 5328472192085857979L;

	public RefreshingListView(String id, boolean reuseItems) {
		this(id, (IModel<? extends Collection<T>>) null, reuseItems);
	}

	public RefreshingListView(String id, IModel<? extends Collection<T>> model, boolean reuseItems) {
		super(id, model);
		if (reuseItems) {
			setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());
		}
	}

	public RefreshingListView(String id, Collection<T> collection, boolean reuseItems) {
		this(id, new Model((Serializable) collection), reuseItems);
	}

	@Override
	protected Iterator<IModel<T>> getItemModels() {
		Collection<T> modelObject = getModelObject();
		if (modelObject == null) {
			modelObject = new ArrayList<T>();
		}
		return new ModelIteratorAdapter<T>(modelObject.iterator()) {
			@Override
			protected IModel<T> model(T object) {
				return new CompoundPropertyModel<T>(object);
			}
		};
	}

	public Collection<T> getModelObject() {
		return getModel().getObject();
	}

	@SuppressWarnings("unchecked")
	public IModel<List<T>> getModel() {
		return (IModel<List<T>>) getDefaultModel();
	}

}