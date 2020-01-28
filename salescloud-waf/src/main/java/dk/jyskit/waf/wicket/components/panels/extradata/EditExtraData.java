package dk.jyskit.waf.wicket.components.panels.extradata;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.waf.application.dao.ExtraDataDao;
import dk.jyskit.waf.application.dao.ExtraDataDefinitionDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraData;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.NoLocalizationLabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.widget.WidgetPanel;
import dk.jyskit.waf.wicket.utils.IAjaxCall;

@SuppressWarnings("serial")
public class EditExtraData extends WidgetPanel {
	@Inject
	private ExtraDataDefinitionDao defDao;

	@Inject
	private ExtraDataDao dataDao;

	private Map<String, ExtraDataItem> items = new TreeMap<>();
	private Map<String, Component> components = new TreeMap<>();

	private IAjaxCall onSaveCallback;

	public EditExtraData(String wicketId, BaseEntity entity, IAjaxCall onEndCallback) {
		super(wicketId, Model.of(entity), Type.Primary);
		this.onSaveCallback = onEndCallback;
		labelKey("edit.extradata");

		Map<String, ExtraDataDefinition> definitions = defDao.findForEntityClass(entity.getClass());
		Map<String, ExtraData> data = dataDao.findForEntity(entity);
		Set<String> nameSet = new TreeSet<>();
		nameSet.addAll(definitions.keySet());
		nameSet.addAll(data.keySet());
		final Jsr303Form<ArrayList<String>> form = new Jsr303Form<ArrayList<String>>("jsr303form", new ArrayList<String>(nameSet), false);
		form.inlineHelp();
		ILabelStrategy labelStrategy = new NoLocalizationLabelStrategy();
		form.setLabelStrategy(labelStrategy);

		for (String name : nameSet) {
			ExtraDataItem item = createExtraDataItem(name, entity, definitions, data);
			items.put(name, item);
			ExtraDataDefinition definition = item.getDefinition();
			Component field = null;
			if (definition != null) {
				String helpTxt = definition.getDescription();
				if (definition.getTypeValues() != null) {
//					IModel<List<? extends String>> valueChoices = Model.ofList(definition.getTypeValues());
//					BootstrapSelectSinglePanel fieldPanel =
//							new BootstrapSelectSinglePanel(form, name, new MicroMap<>("help", helpTxt), valueChoices);
//					field = fieldPanel.getEditor();
//					form.addCustomComponent(fieldPanel);
					
					field = form.addSelectSinglePanel(name, definition.getTypeValues(), new BootstrapSelectOptions());
				} else {
					field = form.addTextField(name, "help='" + helpTxt + "'");
				}
			} else {
				field = form.addTextField(name);
			}
			field.setDefaultModel(new PropertyModel<String>(item, "data.value"));
			components.put(name, field);
		}

		add(form);

		AjaxButton submitButton = form.addSubmitButton("Save", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				boolean validSoFar = true;
				for (String name : items.keySet()) {
					ExtraDataItem item = items.get(name);
					ExtraDataDefinition defi = item.getDefinition();
					if (defi != null) {
						if (!defi.isValid(item.getData().getValue())) {
							components.get(name).error("Not valid: " + defi.getType() + " " + defi.getTypeProperties() + " default:" + defi.getDefaultValue());
							validSoFar = false;
						}
					}
				}
				if (validSoFar) {
					for (String name : items.keySet()) {
						ExtraDataItem item = items.get(name);
						ExtraDataDefinition defi = item.getDefinition();
						if (defi != null) {
							if (item.getData().isNewObject() && defi.getDefaultValue().equals(item.getData().getValue())) {
								// do not save default
							} else {
								dataDao.save(item.getData());
							}
						} else {
							dataDao.save(item.getData());
						}
					}
					dataDao.flush();
					onSaveCallback.invoke(target);
				}
			}
		});
		form.getForm().setDefaultButton(submitButton);
		form.addButton("Cancel", Buttons.Type.Default, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				onSaveCallback.invoke(target);
			}
		});
	}

	public ExtraDataItem createExtraDataItem(String name, BaseEntity entity, Map<String, ExtraDataDefinition> definitions, Map<String, ExtraData> data) {
		ExtraDataItem item;
		if (definitions.get(name) != null) {
			if (data.get(name) != null) {
				item = new ExtraDataItem(data.get(name), definitions.get(name));
			} else {
				item = new ExtraDataItem(entity, definitions.get(name));
			}
		} else {
			item = new ExtraDataItem(data.get(name));
		}
		return item;
	}

}
