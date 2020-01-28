package dk.jyskit.waf.application.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

/**
 * This class is for Select2Choice components.
 * 
 * @author jan
 */
public class EntityStateChoiceProvider extends TextChoiceProvider<EntityState> {
	private final IModel<? extends List<EntityState>> choices;

	public EntityStateChoiceProvider(IModel<? extends List<EntityState>> model) {
		choices = model;
	}
	
	@Override
	protected String getDisplayText(EntityState choice) {
		return choice.toString();
	}

	@Override
	protected Object getId(EntityState choice) {
		return String.valueOf(choice.getEntityState());
	}

	@Override
	public void query(String term, int page, Response<EntityState> response) {
		response.setResults((List<EntityState>) choices.getObject());
	}

	@Override
	public Collection<EntityState> toChoices(Collection<String> ids) {
	    List<EntityState> result = new ArrayList<>(ids.size());
	    for (String id : ids) {
	    	result.add(EntityState.of(Integer.valueOf(id)));
	    }
	    return result;
	}
} 