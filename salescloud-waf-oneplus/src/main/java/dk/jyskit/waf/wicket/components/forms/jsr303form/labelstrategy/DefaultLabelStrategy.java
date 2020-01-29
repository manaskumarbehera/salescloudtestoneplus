package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class DefaultLabelStrategy implements ILabelStrategy {
	private final String namespace;

	public DefaultLabelStrategy(String namespace) {
		super();
		this.namespace = namespace;
	}

	@Override
	public IModel<String> fieldLabel(String property) {
		return new ResourceModel(namespace + "." + property + ".label");
	}

	@Override
	public IModel<String> groupLabel(String groupKey) {
		return new ResourceModel(namespace + ".group." + groupKey);
	}

	@Override
	public IModel<String> columnLabel(String property) {
		return new ResourceModel(namespace + "." + property + ".header");
	}

	@Override
	public IModel<String> buttonLabel(String labelKey) {
		return new ResourceModel(namespace + ".button." + labelKey);
	}

	@Override
	public IModel<String> linkLabel(String labelKey) {
		return new ResourceModel(namespace + ".link." + labelKey);
	}

}
