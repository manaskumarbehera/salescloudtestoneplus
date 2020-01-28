package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;

/**
 * This is a special label strategy for those cases where a ValueMap is used instead
 * of a Java bean in JSR303Forms. The problem it solves is that without the encoding 
 * any '.' characters in the label text would cause big problems.
 * 
 * @author jan
 */
public class ValueMapLabelStrategy implements ILabelStrategy {
	
	@Override
	public IModel<String> fieldLabel(String property) {
		try {
			return Model.of(new String(Base64.decodeBase64(property), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return Model.of("??");
		}
	}
	
	public static String convertLabelTextToFieldName(String labelText) {
		return Base64.encodeBase64String(labelText.getBytes());
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
