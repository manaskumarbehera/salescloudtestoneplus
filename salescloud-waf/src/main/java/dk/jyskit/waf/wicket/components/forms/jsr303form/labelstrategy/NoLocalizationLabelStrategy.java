package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Label strategy that does not localize label and therefore does not require editing
 * of property files. This is useful when you do mockups.
 * 
 * @author jan
 *
 */
public class NoLocalizationLabelStrategy implements ILabelStrategy {
	public NoLocalizationLabelStrategy() {
		super();
	}

	@Override
	public IModel<String> fieldLabel(String property) {
		return Model.of(property);
	}

	@Override
	public IModel<String> groupLabel(String groupKey) {
		return Model.of(groupKey);
	}

	@Override
	public IModel<String> columnLabel(String property) {
		return Model.of(property);
	}

	@Override
	public IModel<String> buttonLabel(String labelKey) {
		return Model.of(labelKey);
	}

	@Override
	public IModel<String> linkLabel(String labelKey) {
		return Model.of(labelKey);
	}
}
