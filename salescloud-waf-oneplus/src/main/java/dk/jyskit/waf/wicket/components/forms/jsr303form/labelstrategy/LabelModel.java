package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import dk.jyskit.waf.application.JITWicketApplication;
/**
 * Searched for a string resource property. First found is used.
 * If used with entityType=MyType and property=user.name.first then the search is in this order:
 * <ol>
 *  <li>MyType.user.name.first</li>
 *  <li>MyType.name.first</li>
 *  <li>MyType.first</li>
 *  <li>user.name.first</li>
 *  <li>name.first</li>
 *  <li>first</li>
 * </ol>
 * @author palfred
 *
 */
@Slf4j
public class LabelModel extends AbstractReadOnlyModel<String> implements IComponentAssignedModel<String>, IWrapModel<String> {
	private Component component;
	private final String property;
	private final String entityType;
	
	public LabelModel(String entityType, String property) {
		this.entityType = entityType;
		this.property = property;
	}

	@Override
	public String getObject() {
		Localizer localizer = Application.get().getResourceSettings().getLocalizer();
		String NO_LABEL = "¤NOLABEL¤";
		String result = NO_LABEL;
		String[] prefixes = { entityType + ".", "" };
		for (String prefix : prefixes) {
			String key = property;
			while (key != null && result == NO_LABEL) {
				String currentKey = prefix + key;
				result = localizer.getString(currentKey, component, NO_LABEL);
				if (NO_LABEL.equals(result) && "true".equals(JITWicketApplication.get().getSetting("debug.labels"))) {
					log.info("debug.labels - failed to resolve key: " + currentKey);
				}
					
				if (key.indexOf('.') > -1) {
					key = key.substring(key.indexOf('.') + 1);
				} else {
					key = null;
				}
			}
		}
		return result == NO_LABEL ? property : result;
	}
	
	@Override
	public IWrapModel<String> wrapOnAssignment(Component component) {
		this.component = component;
		return this;
	}

	@Override
	public IModel<String> getWrappedModel() {
		return this;
	}
}