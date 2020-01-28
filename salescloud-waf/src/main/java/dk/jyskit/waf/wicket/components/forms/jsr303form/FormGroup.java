package dk.jyskit.waf.wicket.components.forms.jsr303form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;

public class FormGroup<T> extends ComponentContainerPanel<T> {
	private boolean renderFieldset = true;

	/**
	 * Constructor for a form group. A form group is just a container for
	 * components. It adds a fieldset around the components.
	 *
	 * @param form
	 * @param beanModel
	 * @param legendModel
	 * @param first
	 * @param ajaxValidate
	 * @param labelStrategy
	 */
	public FormGroup(Form<T> form, IModel<T> beanModel, IModel<String> legendModel, boolean first, boolean ajaxValidate, ILabelStrategy labelStrategy) {
		super("panel", beanModel, ajaxValidate);
		this.form = form;
		setLabelStrategy(labelStrategy);

		WebMarkupContainer fieldset = new WebMarkupContainer("fieldset");
		if (first) {
			fieldset.add(new AttributeModifier("class", Model.of("first")));
		}
		add(fieldset);

		fieldset.add(new Label("legend", legendModel) {
			@Override
			protected void onConfigure() {
				super.onConfigure();
				if ((!renderFieldset) || Strings.isEmpty(getDefaultModelObjectAsString())) {
					setVisible(false);
				}
			}
		});
		fieldset.add(getBeforeButtonsPanelsView());
		fieldset.add(getButtonOrLinkPanels());
		fieldset.add(getAfterButtonsPanelsView());
	}

	@Override
	public String getVariation() {
		if (!renderFieldset) {
			return "nofieldset";
		}
		return super.getVariation();
	}

	public boolean isFieldset() {
		return renderFieldset;
	}

	public void setFieldset(boolean fieldset) {
		this.renderFieldset = fieldset;
	}

	public FormGroup<T> noFieldset() {
		this.renderFieldset = false;
		return this;
	}

}
