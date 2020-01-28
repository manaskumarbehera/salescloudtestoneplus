package dk.jyskit.salescloud.application.pages.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class IntegerListChoiceProvider extends TextChoiceProvider<Integer> {

//	private final IModel<? extends List<Integer>> choices;
	private IModel<List<? extends Integer>> choices;

//	public IntegerListChoiceProvider(IModel<? extends List<Integer>> model) {
//		choices = model;
//	}
	
    public IntegerListChoiceProvider(IModel<List<? extends Integer>> model) {
		choices = model;
	}

	@Override
    protected String getDisplayText(Integer choice) {
        return choice + "";
    }

    @Override	
    protected Object getId(Integer choice) {
        return choice;
    }

    @Override
    public Collection<Integer> toChoices(Collection<String> ids) {
    	List<Integer> list = new ArrayList<>();
    	for (String id : ids) {
			list.add(Integer.valueOf(id));
		}
        return list;
    }

	@Override
	public void query(String term, int page, Response<Integer> response) {
		if (StringUtils.isEmpty(term)) {
			response.setResults((List<Integer>) choices.getObject());
		} else {
			List<Integer> validChoices = new LinkedList<>();
			for (Integer i : choices.getObject()) {
				if ((i + "").contains(term.toLowerCase())) {
					validChoices.add(i);
				}
			}
			response.setResults(validChoices);
		}
	}

}
