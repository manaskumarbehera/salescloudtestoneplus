package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Label strategy which first looks up key in map. As a fallback, another strategy can be used.
 * 
 * @author jan
 *
 */
public class MapLabelStrategy implements ILabelStrategy {
	private Map<String, String> map;
	private ILabelStrategy fallbackStrategy;

	public MapLabelStrategy(Map<String, String> map, ILabelStrategy fallbackStrategy) {
		super();
		this.map = map;
		this.fallbackStrategy = fallbackStrategy;
	}

	@Override
	public IModel<String> fieldLabel(String property) {
		String text = map.get(property);
		if (text == null) {
			return fallbackStrategy.fieldLabel(property);
		} else {
			return Model.of(text);
		}
	}

	@Override
	public IModel<String> groupLabel(String groupKey) {
		String text = map.get(groupKey);
		if (text == null) {
			return fallbackStrategy.groupLabel(groupKey);
		} else {
			return Model.of(text);
		}
	}

	@Override
	public IModel<String> columnLabel(String property) {
		String text = map.get(property);
		if (text == null) {
			return fallbackStrategy.columnLabel(property);
		} else {
			return Model.of(text);
		}
	}

	@Override
	public IModel<String> buttonLabel(String labelKey) {
		String text = map.get(labelKey);
		if (text == null) {
			return fallbackStrategy.buttonLabel(labelKey);
		} else {
			return Model.of(text);
		}
	}

	@Override
	public IModel<String> linkLabel(String labelKey) {
		String text = map.get(labelKey);
		if (text == null) {
			return fallbackStrategy.linkLabel(labelKey);
		} else {
			return Model.of(text);
		}
	}}
