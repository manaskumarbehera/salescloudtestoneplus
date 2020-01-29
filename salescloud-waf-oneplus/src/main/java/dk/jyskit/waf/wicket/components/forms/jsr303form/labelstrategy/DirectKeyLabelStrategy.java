package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Label strategy that uses the given names directly as key for localization
 * @author palfred
 *
 */
public class DirectKeyLabelStrategy implements ILabelStrategy {
	public DirectKeyLabelStrategy() {
		super();
	}

	@Override
	public IModel<String> fieldLabel(String property) {
		return new ResourceModel(property);
	}

	@Override
	public IModel<String> groupLabel(String groupKey) {
		return new ResourceModel(groupKey);
	}

	@Override
	public IModel<String> columnLabel(String property) {
		return fieldLabel(property);
	}

	@Override
	public IModel<String> buttonLabel(String labelKey) {
		return new ResourceModel(labelKey);
	}

	@Override
	public IModel<String> linkLabel(String labelKey) {
		return new ResourceModel(labelKey);
	}}
