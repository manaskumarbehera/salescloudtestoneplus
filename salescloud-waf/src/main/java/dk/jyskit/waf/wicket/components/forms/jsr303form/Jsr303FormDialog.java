package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.Draggable;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.DraggableConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.Resizable;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.utils.WicketUtils;

/**
 * Modal to wrap a {@link Jsr303Form}. Have utility methods to aid use of
 * standard submit and cancel buttons.
 *
 * @author palfred
 *
 */
public class Jsr303FormDialog<T extends Serializable> extends Modal<T> implements IGenericComponent<T> {
	private String submitLabelKey = "submit";
	private String cancelLabelKey = "cancel";

//	@Delegate(excludes = Modal.class)
	private Jsr303Form<T> form;

	public Jsr303FormDialog(String wicketId, IModel<T> model, boolean ajaxValidate) {
		super(wicketId);
		setDefaultModel(model);
		setOutputMarkupId(true);
		this.form = createForm("form", model, ajaxValidate);
		add(form);
		// other defaults
		setFooterVisible(false);
		setUseKeyboard(false);
		add(AttributeModifier.replace("data-backdrop", "static"));
		show(true);
	}

	@Override
	public MarkupContainer setDefaultModel(IModel<?> model) {
		if (form != null)
			form.setDefaultModel(model);
		return super.setDefaultModel(model);
	}

	public Jsr303FormDialog<T> dragable() {
		add(new Draggable(new DraggableConfig().withHandle(".modal-header").withCursor("move")));
		return this;
	}

	public Jsr303FormDialog<T> resizable() {
		add(new Resizable());
		return this;
	}

	protected Jsr303Form<T> createForm(String formId, IModel<T> model, boolean ajaxValidate) {
		return new Jsr303Form<>(formId, model, ajaxValidate);
	}

	protected void onSubmit(AjaxRequestTarget target) {

	}

	protected void onCancel(AjaxRequestTarget target) {

	}

	public Modal appendCloseDialogJavaScript(final AjaxRequestTarget target) {
 		super.appendCloseDialogJavaScript(target);
		replaceWith(new EmptyPanel(getId()));
		return this;
	}

	public Jsr303FormDialog<T> addStandardButtons() {
		addSubmitButton();
		addCancelButton();
		return this;
	}

	public AjaxButton addSubmitButton() {
		return form.addSubmitButton(submitLabelKey, Buttons.Type.Primary, createSubmitListener());
	}

	public AjaxButton addCancelButton() {

		return form.addButton(cancelLabelKey, Buttons.Type.Default, createCancelListener());
	}

	private AjaxEventListener createCancelListener() {
		return new AjaxEventListener() {

			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				Jsr303FormDialog.this.onCancel(target);
				appendCloseDialogJavaScript(target);
			}
		};
	}

	protected AjaxSubmitListener createSubmitListener() {
		return new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				Jsr303FormDialog.this.onSubmit(target);
				if (!WicketUtils.hasFeedbackMessageInHierarchy(Jsr303FormDialog.this)) {
					appendCloseDialogJavaScript(target);
				} else {
					target.add(form.getNotificationPanel());
				}
			}
		};
	}

	public String getSubmitLabelKey() {
		return submitLabelKey;
	}

	public void setSubmitLabelKey(String submitLabelKey) {
		this.submitLabelKey = submitLabelKey;
	}

	public Jsr303FormDialog<T> submitLabelKey(String key) {
		setSubmitLabelKey(key);
		return this;
	}

	/**
	 * Sets header label to {@link StringResourceModel} for the given key. The
	 * model used for StringResourceModel is the model of this
	 * {@link Jsr303FormDialog}.
	 *
	 * @param key
	 * @return
	 */
	public Jsr303FormDialog<T> headerKey(String key) {
		header(new StringResourceModel(key, this, getDefaultModel()));
		return this;
	}

	public String getCancelLabelKey() {
		return cancelLabelKey;
	}

	public void setCancelLabelKey(String cancelLabelKey) {
		this.cancelLabelKey = cancelLabelKey;
	}

	public Jsr303FormDialog<T> cancelLabelKey(String key) {
		setCancelLabelKey(key);
		return this;
	}

	public Jsr303Form<T> getJsr303Form() {
		return form;
	}

	// ==============================================================================================
	// @Delegate does not work?
	// ==============================================================================================

	public Form<T> getForm() {
		return form.getForm();
	}

	public FormRow<T> createRow() {
		return form.createRow();
	}

	/**
	 * Add a form group. You must add localization of form group name in the
	 * page properties file:
	 * <code>form-id.group.group-name = localized name</code>
	 *
	 * @param groupNameKey
	 * @return
	 */
	public FormGroup<T> createGroup(String groupNameKey) {
		return form.createGroup(groupNameKey);
	}
	
	/**
	 * Add text field. You must add localization of associated form label in the
	 * page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public TextField addTextField(String fieldName, String... attributes) {
		return form.addTextField(fieldName, attributes);
	}

	/**
	 * Add textarea field.
	 *
	 * Attributes are optional extra attributes for the HTML tag, eg.
	 * class="myclass" or "auto-grow=yes", "show-remaining=yes", "cols=40",
	 * "rows=3"
	 *
	 * You must add localization of associated form label in the page properties
	 * file: <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public TextArea addTextArea(String fieldName, String... attributes) {
		return form.addTextArea(fieldName, attributes);
	}

	/**
	 * Add date field. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public DateTextField addDatePicker(String fieldName, String... attributes) {
		return form.addDatePicker(fieldName, attributes);
	}

	/**
	 * Add money text field. You must add localization of associated form label
	 * in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public TextField addMoneyField(String fieldName, String... attributes) {
		return form.addMoneyField(fieldName, attributes);
	}

	/**
	 * Add checkbox. Attributes are optional extra attributes for the HTML tag,
	 * eg. class="myclass". You must add localization of associated form label
	 * in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public CheckBox addCheckBox(String fieldName, String... attributes) {
		return form.addCheckBox(fieldName, attributes);
	}

	/**
	 * Add checkbox. Attributes are optional extra attributes for the HTML tag,
	 * eg. class="myclass". You must add localization of associated form label
	 * in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param listener
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public CheckBox addCheckBox(String fieldName, AjaxEventListener listener, String... attributes) {
		return form.addCheckBox(fieldName, listener, attributes);
	}

	/**
	 * Add dropdownchoice. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public DropDownChoice addDropDownChoice(String fieldName, List listOfChoices, String... attributes) {
		return form.addDropDownChoice(fieldName, listOfChoices, attributes);
	}

	/**
	 * Add dropdownchoice. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public DropDownChoice addDropDownChoice(String fieldName, List listOfChoices, IChoiceRenderer choiceRenderer,
			String... attributes) {
		return form.addDropDownChoice(fieldName, listOfChoices, choiceRenderer, attributes);
	}

	/**
	 * Add fileuploadfield. A field of type List&lt;FileUpload&gt; in the form
	 * bean is required. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 * @return
	 */
	public FileUploadField addFileUploadsField(String fieldName, String... attributes) {
		return form.addFileUploadsField(fieldName, attributes);
	}

	/**
	 * @see #addImagePreviewAndUpload(String, Integer, Integer, String...)
	 * @param fieldName
	 * @param imageWidth
	 * @param imageHeight
	 * @param attributes
	 * @return
	 */
	public FormComponentPanel addImagePreviewAndUpload(String fieldName, Integer imageWidth, Integer imageHeight,
			String... attributes) {
		return form.addImagePreviewAndUpload(fieldName, imageWidth, imageHeight, attributes);
	}

	/**
	 * @deprecated use addImagePreviewAndUpload instead.
	 *
	 * @param fieldName
	 * @param imageWidth
	 * @param imageHeight
	 * @param attributes
	 * @return
	 */
	public FormComponentPanel addImagePreviewAndUploadInDialog(String fieldName, int imageWidth, int imageHeight,
			String... attributes) {
		return addImagePreviewAndUploadInDialog(fieldName, imageWidth, imageHeight, attributes);
	}

	/**
	 * Add non-submitting button. You must add localization of button label in
	 * the page properties file:
	 * <code>form-id.button.label-key = localized text</code>
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxButton addButton(String labelKey, Buttons.Type buttonType, AjaxEventListener listener) {
		return form.addButton(labelKey, buttonType, listener);
	}

	/**
	 * Add indicating, non-submitting button. You must add localization of
	 * button label in the page properties file:
	 * <code>form-id.button.label-key = localized text</code>
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxButton addIndicatingButton(String labelKey, Buttons.Type buttonType, AjaxEventListener listener) {
		return form.addIndicatingButton(labelKey, buttonType, listener);
	}

	/**
	 * Add button which will attempt to submit the form if clicked. You must add
	 * localization of button label in the page properties file:
	 * <code>form-id.button.label-key = localized text</code>
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxButton addSubmitButton(String labelKey, Buttons.Type buttonType, AjaxSubmitListener listener) {
		return form.addSubmitButton(labelKey, buttonType, listener);
	}

	/**
	 * Add indicating button which will attempt to submit the form if clicked.
	 * You must add localization of button label in the page properties file:
	 * <code>form-id.button.label-key = localized text</code>
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxButton addIndicatingSubmitButton(String labelKey, Buttons.Type buttonType, AjaxSubmitListener listener) {
		return form.addIndicatingSubmitButton(labelKey, buttonType, listener);
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addSubmitLink(String labelKey, AjaxSubmitListener listener) {
		return form.addSubmitLink(labelKey, listener);
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addSubmitLink(ResourceReference imageResource, AjaxSubmitListener listener) {
		return form.addSubmitLink(imageResource, listener);
	}

	/**
	 * Add indicating submit link which will attempt to submit the form if
	 * clicked.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addIndicatingSubmitLink(String labelKey, AjaxSubmitListener listener) {
		return form.addIndicatingSubmitLink(labelKey, listener);
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addIndicatingSubmitLink(ResourceReference imageResource, AjaxSubmitListener listener) {
		return form.addIndicatingSubmitLink(imageResource, listener);
	}

	/**
	 * Add non-submitting link.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxLink addLink(String labelKey, AjaxEventListener listener) {
		return form.addLink(labelKey, listener);
	}

	/**
	 * Add non-submitting link.
	 *
	 * @param listener
	 * @return
	 */
	public AjaxLink addLink(ResourceReference imageResource, Buttons.Type buttonType, AjaxEventListener listener) {
		return form.addLink(imageResource, buttonType, listener);
	}

	/**
	 * Add non-submitting, bookmarkable link.
	 *
	 * @param labelKey
	 * @return
	 */
	public BookmarkablePageLink addBookmarkableLink(String labelKey, Buttons.Type buttonType, Class pageClass,
			PageParameters parameters) {
		return form.addBookmarkableLink(labelKey, buttonType, pageClass, parameters);
	}

	/**
	 * Add non-submitting, bookmarkable link.
	 *
	 * @param imageResource
	 * @param buttonType
	 * @param pageClass
	 * @param parameters
	 * @return
	 */
	public BookmarkablePageLink addBookmarkableLink(ResourceReference imageResource, Buttons.Type buttonType,
			Class pageClass, PageParameters parameters) {
		return form.addBookmarkableLink(imageResource, buttonType, pageClass, parameters);
	}

	/**
	 * Add a panel consisting of label and editor. Panel must have id "panel".
	 * This is for special cases. Can be used if you need a "complex" label.
	 *
	 * @return
	 */
	public FormComponent addCustomComponent(ComponentWithLabelAndValidationPanel panel) {
		return form.addCustomComponent(panel);
	}

	// --------------------------------
	// Fields without labels
	// --------------------------------

	/**
	 * Add hidden field. Does not include a label, obviously.
	 *
	 * @param fieldName
	 */
	public void addHiddenField(String fieldName) {
		form.addHiddenField(fieldName);
	}

	/**
	 * Add image. Does not include a label.
	 *
	 * @param url
	 */
	public WebMarkupContainer addImage(String url, int width) {
		return form.addImage(url, width);
	}

	/**
	 * Add any panel. Panel must have id "panel". Does not include a label.
	 *
	 * @param panel
	 * @param beforeButtons
	 */
	public void addPanel(Panel panel, boolean beforeButtons) {
		form.addPanel(panel, beforeButtons);
	}

	/**
	 * @param fieldNames
	 */
	public void setInvisible(String... fieldNames) {
		form.setInvisible(fieldNames);
	}

	/**
	 * @param target
	 * @param fieldNames
	 */
	public void toggleVisibility(AjaxRequestTarget target, String... fieldNames) {
		form.toggleVisibility(target, fieldNames);
	}

	/**
	 * Helper method for rendering an error box as a result of an Ajax event.
	 *
	 * @param target
	 * @param component
	 */
	public void handleErrorsInForm(AjaxRequestTarget target, Component component) {
		form.handleErrorsInForm(target, component);
	}

	public String getNameSpace() {
		return form.getNameSpace();
	}

	public IModel<T> getBeanModel() {
		return form.getBeanModel();
	}

	public void setBeanModel(IModel<T> beanModel) {
		form.setBeanModel(beanModel);
	}

	public boolean isAjaxValidate() {
		return form.isAjaxValidate();
	}

	public boolean isUsingInlineHelp() {
		return form.isUsingInlineHelp();
	}

	public ILabelStrategy getLabelStrategy() {
		return form.getLabelStrategy();
	}

	/**
	 * Set the strategy for labels of the components to be added.
	 * Must be set before adding any {@link ComponentWithLabelAndValidationPanel}.
	 * @param labelStrategy
	 */
	public void setLabelStrategy(ILabelStrategy labelStrategy) {
		form.setLabelStrategy(labelStrategy);
	}

}
