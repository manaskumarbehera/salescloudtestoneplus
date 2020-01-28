package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

public interface ILabelStrategy extends Serializable {

	IModel<String> columnLabel(String property);
	IModel<String> fieldLabel(String property);
	IModel<String> groupLabel(String groupKey);
	IModel<String> buttonLabel(String labelKey);
	IModel<String> linkLabel(String labelKey);

}
