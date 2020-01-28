package dk.jyskit.salescloud.application.pages.partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class StringListChoiceProvider extends TextChoiceProvider<String> {

	private IModel<List<? extends String>> choices;

//	private final IModel<? extends List<String>> choices;

//	public StringListChoiceProvider(IModel<? extends List<String>> model) {
//		choices = model;
//	}
	
    public StringListChoiceProvider(IModel<List<? extends String>> model) {
    	choices = model;
	}

	@Override
    protected String getDisplayText(String choice) {
        return choice;
    }

    @Override	
    protected Object getId(String choice) {
        return choice;
    }

    @Override
    public Collection<String> toChoices(Collection<String> ids) {
    	List<String> list = new ArrayList<>();
    	for (String id : ids) {
			list.add(String.valueOf(id));
		}
        return list;
    }

	@Override
	public void query(String term, int page, Response<String> response) {
		if (StringUtils.isEmpty(term)) {
			response.setResults((List<String>) choices.getObject());
		} else {
			List<String> validChoices = new LinkedList<>();
			for (String s : choices.getObject()) {
				if ((s).contains(term.toLowerCase())) {
					validChoices.add(s);
				}
			}
			response.setResults(validChoices);
		}
	}

}
