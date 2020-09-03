package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

import com.google.inject.Provider;
import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.DateTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;
import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SpanType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import dk.jyskit.waf.application.utils.exceptions.SystemException;
import dk.jyskit.waf.utils.dataexport.pdf.PdfLink;
import dk.jyskit.waf.utils.dataexport.pdf.PdfLinkCallback;
import dk.jyskit.waf.utils.dataexport.spreadsheets.ExcelLink;
import dk.jyskit.waf.utils.dataexport.spreadsheets.ExcelLinkCallback;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentAndLabel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.bootstrapselect.BootstrapSelectSinglePanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxButtonPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.checkbox.CheckBoxPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.datepicker.DatePickerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime.KendoDatePickerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime.KendoDateTimePickerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime.KendoTimePickerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.dropdownchoice.DropDownChoicePanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.fileupload.FileUploadsFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.hiddenfield.HiddenFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.image.ImagePanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagefield.ImageFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagepreviewanduploadindialog.ImagePreviewAndUploadInDialogPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.label.LabelPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.AjaxLinkPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.BookmarkableLinkPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.excel.ExcelLinkPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.pdf.PdfLinkPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.spreadsheet.SpreadsheetLinkPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.moneyfield.MoneyFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.numbertextfield.NumberTextFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.panel.PanelPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.progressbar.UploadProgressBarPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.radio.RadioPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.readonly.ReadonlyPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.select2.Select2ChoicePanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.select2.Select2MultiChoicePanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.slider.SliderField;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.slider.SliderFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.textarea.TextAreaPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.textfield.TextFieldPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling.DefaultExceptionHandler;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.panels.IPanelWrapable;
import dk.jyskit.waf.wicket.components.spreadsheets.SpreadsheetLink;

public class ComponentContainerPanel<T> extends Panel {
	protected List<Panel> beforeButtonsPanels = new ArrayList<Panel>();
	protected List<Panel> afterButtonsPanels = new ArrayList<Panel>();
	protected List<Panel> buttonOrLinkPanels = new ArrayList<Panel>();
	protected IModel<T> beanModel;
	protected Form<T> form;
	protected String errorMessagePattern;
	protected ComponentContainerPanel parentContainer;

	private final boolean ajaxValidate;
	private boolean usingInlineHelp = false;
	private boolean usingLabels = true;
	private ILabelStrategy labelStrategy;
	private Jsr303FormExceptionHandler exceptionHandler;

	private SpanType[] labelSpans = new SpanType[] {};
	private SpanType[] editorSpans = new SpanType[] {};
	private SpanType[] helpSpans = new SpanType[] {};

	public ComponentContainerPanel(String id, IModel<T> beanModel, boolean ajaxValidate) {
		super(id);
		this.beanModel = beanModel;
		this.ajaxValidate = ajaxValidate;
	}

	public ComponentContainerPanel<T> inlineHelp() {
		usingInlineHelp = true;
		return this;
	}

	public ComponentContainerPanel<T> hideLabels() {
		usingLabels = false;
		return this;
	}

	/**
	 * Creates and adds a row for layout of components.
	 *
	 * @return
	 */
	public FormRow<T> createRow() {
		FormRow<T> row = new FormRow<T>("panel", this);
		beforeButtonsPanels.add(row);
		return row;
	}

	/**
	 * Add readonly label. You must add localization of associated form label in the
	 * page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public Label addReadonly(String fieldName, String... attributes) {
		ReadonlyPanel panel = new ReadonlyPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add label. You must add localization of associated form label in the
	 * page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public Label addLabel(String fieldName, String... attributes) {
		LabelPanel panel = new LabelPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		TextFieldPanel panel = new TextFieldPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		TextAreaPanel panel = new TextAreaPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		DatePickerPanel panel = new DatePickerPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add date+time field. Attributes are optional extra attributes for the HTML
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
	public DateTimePicker addKendoDateTimePicker(String fieldName, String... attributes) {
		KendoDateTimePickerPanel panel = new KendoDateTimePickerPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
	public DatePicker addKendoDatePicker(String fieldName, String... attributes) {
		KendoDatePickerPanel panel = new KendoDatePickerPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add time field. Attributes are optional extra attributes for the HTML
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
	public TimePicker addKendoTimePicker(String fieldName, String... attributes) {
		KendoTimePickerPanel panel = new KendoTimePickerPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add numeric text field. You must add localization of associated form label in the
	 * page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public NumberTextField addNumberTextField(String fieldName, String... attributes) {
		NumberTextFieldPanel panel = new NumberTextFieldPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		MoneyFieldPanel panel = new MoneyFieldPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		CheckBoxPanel panel = new CheckBoxPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		CheckBoxPanel panel = new CheckBoxPanel(this, fieldName, listener, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add radiochoice. Attributes are optional extra attributes for the HTML tag,
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
	public Radio addRadio(String fieldName, final AjaxEventListener listener, String... attributes) {
		RadioPanel panel = new RadioPanel(this, fieldName, listener, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		DropDownChoicePanel panel = new DropDownChoicePanel(this, fieldName, listOfChoices, null,
				getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		DropDownChoicePanel panel = new DropDownChoicePanel(this, fieldName, listOfChoices, choiceRenderer,
				getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add selectsinglepanel. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 * 
	 * In some case you may want to add an {@link OnChangeAjaxBehavior} to the component which is
	 * returned.
	 * 
	 * This is deprecated because changes are written to the database while the form is active!
	 * Also, strange "flickering" may occur.
	 * Maybe it can be fixed by using wicket-bootstrap "directly"?
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	@Deprecated
	public BootstrapSelectSingle addSelectSinglePanel(String fieldName, List listOfChoices, BootstrapSelectOptions options) {
		BootstrapSelectSinglePanel panel = new BootstrapSelectSinglePanel(this, fieldName, listOfChoices, options, new HashMap<String,String>());
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add selectsinglepanel. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * In some case you may want to add an {@link OnChangeAjaxBehavior} to the component which is
	 * returned.
	 *
	 * This is deprecated because changes are written to the database while the form is active!
	 * Also, strange "flickering" may occur.
	 * Maybe it can be fixed by using wicket-bootstrap "directly"?
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	@Deprecated
	public BootstrapSelectSingle addSelectSinglePanel(String fieldName, List listOfChoices, IChoiceRenderer choiceRenderer,
			BootstrapSelectOptions options, String... attributes) {
		BootstrapSelectSinglePanel panel = new BootstrapSelectSinglePanel(this, fieldName, listOfChoices, options, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		if (choiceRenderer != null) {
			panel.getComponent().setChoiceRenderer(choiceRenderer);
		}
		return panel.getComponent();
	}

	/**
	 * Add Select2 choice dropdown. Use this if you need advanced features, such asAjax choice filtering, custom rendering, etc.
	 * Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 * 
	 * In some case you may want to add an {@link OnChangeAjaxBehavior} to the component which is
	 * returned.
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public Select2Choice addSelect2Choice(String fieldName, ChoiceProvider choiceProvider) {
		Select2ChoicePanel panel = new Select2ChoicePanel(this, fieldName, choiceProvider, new HashMap<String,String>());
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add Select2 multichoice dropdown. Attributes are optional extra attributes for the HTML
	 * tag, eg. class="myclass". You must add localization of associated form
	 * label in the page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 * 
	 * In some case you may want to add an {@link OnChangeAjaxBehavior} to the component which is
	 * returned.
	 *
	 * @param fieldName
	 * @param listOfChoices
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public Select2MultiChoice addSelect2MultiChoice(String fieldName, ChoiceProvider choiceProvider) {
		Select2MultiChoicePanel panel = new Select2MultiChoicePanel(this, fieldName, choiceProvider, new HashMap<String,String>());
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		form.setMultiPart(true);
		FileUploadsFieldPanel panel = new FileUploadsFieldPanel(this, fieldName, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		UploadProgressBarPanel progressPanel = new UploadProgressBarPanel(this, fieldName, form, panel.getComponent(), getKeyValueMap(attributes));
		beforeButtonsPanels.add(progressPanel);
		return panel.getComponent();
	}

	/**
	 * Input of an image, which can be cropped and scaled to meet the wanted size.
	 * The wanted size be specified in imageWidth and imageHeight: If both are specified then the selection is look in the aspect ratio given.
	 * If only one is specified then the image is scaled keeping the aspect ratio of the selection, the sleect is free in the aspect ratio.
	 * @param fieldName
	 * @param imageWidth
	 * @param imageHeight
	 * @param attributes
	 * @return
	 */
	public FormComponentPanel addImagePreviewAndUpload(String fieldName, Integer imageWidth, Integer imageHeight,
			String... attributes) {
		ImageFieldPanel panel = new ImageFieldPanel(this, fieldName, imageWidth, imageHeight, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		ImagePreviewAndUploadInDialogPanel panel = new ImagePreviewAndUploadInDialogPanel(this, fieldName, imageWidth,
				imageHeight, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Add slider field. You must add localization of associated form label in the
	 * page properties file:
	 * <code>form-id.field-name.label = localized name</code>
	 *
	 * @param fieldName
	 * @param attributes
	 *            are optional extra attributes for the HTML tag, eg.
	 *            class="myclass".
	 * @return
	 */
	public SliderField addSliderField(String fieldName, int min, int max, int step, String... attributes) {
		SliderFieldPanel panel = new SliderFieldPanel(this, fieldName, min, max, step, getKeyValueMap(attributes));
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
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
		AjaxButtonPanel buttonPanel = new AjaxButtonPanel(this, labelKey, buttonType, listener, false);
		buttonOrLinkPanels.add(buttonPanel);
		return buttonPanel.getButton();
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
		AjaxButtonPanel buttonPanel = new AjaxButtonPanel(this, labelKey, buttonType, listener, true);
		buttonOrLinkPanels.add(buttonPanel);
		return buttonPanel.getButton();
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
		AjaxButtonPanel buttonPanel = new AjaxButtonPanel(this, labelKey, buttonType, listener, false);
		buttonOrLinkPanels.add(buttonPanel);
		getForm().setDefaultButton(buttonPanel.getButton());
		return buttonPanel.getButton();
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
		AjaxButtonPanel buttonPanel = new AjaxButtonPanel(this, labelKey, buttonType, listener, true);
		buttonOrLinkPanels.add(buttonPanel);
		return buttonPanel.getButton();
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addSubmitLink(String labelKey, AjaxSubmitListener listener) {
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, labelKey, listener, false);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxSubmitLink) linkPanel.getLink();
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param imageResource
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addSubmitLink(ResourceReference imageResource, AjaxSubmitListener listener) {
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, imageResource, listener, false);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxSubmitLink) linkPanel.getLink();
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
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, labelKey, listener, true);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxSubmitLink) linkPanel.getLink();
	}

	/**
	 * Add link which will attempt to submit the form if clicked.
	 *
	 * @param imageResource
	 * @param listener
	 * @return
	 */
	public AjaxSubmitLink addIndicatingSubmitLink(ResourceReference imageResource, AjaxSubmitListener listener) {
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, imageResource, listener, true);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxSubmitLink) linkPanel.getLink();
	}

	/**
	 * Add non-submitting link.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public AjaxLink addLink(String labelKey, AjaxEventListener listener) {
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, labelKey, listener);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxLink) linkPanel.getLink();
	}

	/**
	 * Add non-submitting link.
	 *
	 * @param imageResource
	 * @param buttonType
	 * @param listener
	 * @return
	 */
	public AjaxLink addLink(ResourceReference imageResource, Buttons.Type buttonType, AjaxEventListener listener) {
		AjaxLinkPanel linkPanel = new AjaxLinkPanel(this, imageResource, listener);
		buttonOrLinkPanels.add(linkPanel);
		return (AjaxLink) linkPanel.getLink();
	}

	/**
	 * Add non-submitting, bookmarkable link.
	 *
	 * @param labelKey
	 * @param buttonType
	 * @param pageClass
	 * @param parameters
	 * @return
	 */
	public BookmarkablePageLink addBookmarkableLink(String labelKey, Buttons.Type buttonType, Class pageClass,
			PageParameters parameters) {
		BookmarkableLinkPanel linkPanel = new BookmarkableLinkPanel(this, labelKey, pageClass, parameters);
		buttonOrLinkPanels.add(linkPanel);
		return (BookmarkablePageLink) linkPanel.getLink();
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
		BookmarkableLinkPanel linkPanel = new BookmarkableLinkPanel(this, imageResource, pageClass, parameters);
		buttonOrLinkPanels.add(linkPanel);
		return (BookmarkablePageLink) linkPanel.getLink();
	}

	/**
	 * Add PDF link.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public PdfLink addPdfLink(String labelKey, PdfLinkCallback callback) {
		PdfLinkPanel linkPanel = new PdfLinkPanel(this, labelKey, callback);
		buttonOrLinkPanels.add(linkPanel);
		return (PdfLink) linkPanel.getLink();
	}

	/**
	 * Add Excel link.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public ExcelLink addExcelLink(String labelKey, ExcelLinkCallback callback) {
		ExcelLinkPanel linkPanel = new ExcelLinkPanel(this, labelKey, callback);
		buttonOrLinkPanels.add(linkPanel);
		return (ExcelLink) linkPanel.getLink();
	}

	/**
	 * Add Excel link.
	 *
	 * @param labelKey
	 * @param listener
	 * @return
	 */
	public SpreadsheetLink addSpreadsheetLink(String labelKey, String fileName, IModel<String> linkLabelModel, Provider<Workbook> workbookProvider) {
		SpreadsheetLinkPanel linkPanel = new SpreadsheetLinkPanel(this, labelKey, fileName, linkLabelModel, workbookProvider);
		buttonOrLinkPanels.add(linkPanel);
		return (SpreadsheetLink) linkPanel.getLink();
	}

	/**
	 * Add a panel consisting of label and editor. Panel must have id "panel".
	 * This is for special cases. Can be used if you need a "complex" label.
	 *
	 * @param panel
	 * @return
	 */
	public <M, F extends FormComponent<M>> F addCustomComponent(ComponentWithLabelAndValidationPanel<F> panel) {
		beforeButtonsPanels.add(panel);
		return panel.getComponent();
	}

	/**
	 * Allows add of {@link FormComponent} that implement {@link IPanelWrapable}.
	 * @param field A form component having the same id as the property used for model.
	 * @see ComponentAndLabel
	 */
	public <M, F extends FormComponent<M> &  IPanelWrapable> void addCustomField(F field) {
		addCustomComponent(ComponentAndLabel.create(this, field));
	}

	/**
	 * Allows add of {@link FormComponent} that implement {@link IPanelWrapable}.
	 * @param field A form component having the any id
	 * @param fieldName The name used for the property model
	 * @see ComponentAndLabel
	 */
	public <M, F extends FormComponent<M> &  IPanelWrapable> void addCustomField(F field, String fieldName) {
		addCustomComponent(ComponentAndLabel.create(this, fieldName, field));
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
		HiddenFieldPanel panel = new HiddenFieldPanel(this, fieldName);
		beforeButtonsPanels.add(panel);
	}

	/**
	 * Add image. Does not include a label.
	 *
	 * @param url
	 * @param width
	 * @return
	 */
	public WebMarkupContainer addImage(String url, int width) {
		ImagePanel panel = new ImagePanel(this, url, width);
		beforeButtonsPanels.add(panel);
		return panel.getWebMarkupContainer();
	}

	/**
	 * Add any panel. Panel must have id "panel". Does not include a label.
	 *
	 * @param panel
	 * @param beforeButtons
	 * @return 
	 */
	public PanelPanel addPanel(Panel panel, boolean beforeButtons) {
		PanelPanel p = new PanelPanel(this, panel);
		if (beforeButtons) {
			beforeButtonsPanels.add(p);
		} else {
			afterButtonsPanels.add(p);
		}
		return p;
	}

	// Helper methods

	/**
	 * Turns sequence of key=value statements into a map. Any surrounding "
	 * characters are removed.
	 *
	 * @param attributes
	 * @return
	 */
	private Map<String, String> getKeyValueMap(String... attributes) {
		Map<String, String> attributesMap = new HashMap<String, String>();
		for (String keyValue : attributes) {
			String[] kv = StringUtils.split(keyValue, '=');
			if (kv.length == 2) {
				String value = kv[1];
				value = StringUtils.removeStart(value, "\"");
				value = StringUtils.removeEnd(value, "\"");
				value = StringUtils.removeStart(value, "'");
				value = StringUtils.removeEnd(value, "'");
				attributesMap.put(kv[0].trim(), value);
			} else {
				throw new SystemException("Correct syntax is: key=value, key=\"value\" or key='value'");
			}
		}
		return attributesMap;
	}

	/**
	 * @return
	 */
	protected RefreshingView<Panel> getBeforeButtonsPanelsView() {
		RefreshingView<Panel> formItemsView = new RefreshingView<Panel>("beforeButtonsPanels") {
			@Override
			protected Iterator<IModel<Panel>> getItemModels() {
				List<Panel> items = new ArrayList<Panel>();
				items.addAll(beforeButtonsPanels);

				return new ModelIteratorAdapter<Panel>(items.iterator()) {
					@Override
					protected IModel<Panel> model(Panel object) {
						return new CompoundPropertyModel<Panel>(object);
					}
				};
			}

			@Override
			protected void populateItem(Item<Panel> item) {
				Panel panel = item.getModelObject();
				item.add(panel);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (beforeButtonsPanels.size() == 0) {
					setVisible(false);
				}
			}
		};
		formItemsView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return formItemsView;
	}

	/**
	 * @return
	 */
	protected RefreshingView<Panel> getAfterButtonsPanelsView() {
		RefreshingView<Panel> formItemsView = new RefreshingView<Panel>("afterButtonsPanels") {
			@Override
			protected Iterator<IModel<Panel>> getItemModels() {
				List<Panel> items = new ArrayList<Panel>();
				items.addAll(afterButtonsPanels);

				return new ModelIteratorAdapter<Panel>(items.iterator()) {
					@Override
					protected IModel<Panel> model(Panel object) {
						return new CompoundPropertyModel<Panel>(object);
					}
				};
			}

			@Override
			protected void populateItem(Item<Panel> item) {
				Panel panel = item.getModelObject();
				item.add(panel);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (afterButtonsPanels.size() == 0) {
					setVisible(false);
				}
			}
		};
		formItemsView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return formItemsView;
	}

	protected RefreshingView<Panel> getButtonOrLinkPanels() {
		RefreshingView<Panel> formItemsView = new RefreshingView<Panel>("buttons") {
			@Override
			protected Iterator<IModel<Panel>> getItemModels() {
				List<Panel> items = new ArrayList<Panel>();
				items.addAll(buttonOrLinkPanels);

				return new ModelIteratorAdapter<Panel>(items.iterator()) {
					@Override
					protected IModel<Panel> model(Panel object) {
						return new CompoundPropertyModel<Panel>(object);
					}
				};
			}

			@Override
			protected void populateItem(Item<Panel> item) {
				Panel panel = item.getModelObject();
				item.add(panel.setRenderBodyOnly(true));
				item.setRenderBodyOnly(true);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (buttonOrLinkPanels.size() == 0) {
					setVisible(false);
				}
			}
		};
		formItemsView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());

		return formItemsView;
	}

	/**
	 * @param fieldNames
	 */
	public void setInvisible(String... fieldNames) {
		for (String fieldName : fieldNames) {
			for (Panel panel : beforeButtonsPanels) {
				if (panel instanceof ComponentWithLabelAndValidationPanel) {
					ComponentWithLabelAndValidationPanel componentWithLabelAndValidationPanel = (ComponentWithLabelAndValidationPanel) panel;
					if (fieldName.equals(componentWithLabelAndValidationPanel.getFieldName())) {
						componentWithLabelAndValidationPanel.setVisible(false);
						componentWithLabelAndValidationPanel.setOutputMarkupPlaceholderTag(true);
					}
				}
			}
		}
	}

	/**
	 * @param target
	 * @param fieldNames
	 */
	public void toggleVisibility(AjaxRequestTarget target, String... fieldNames) {
		for (String fieldName : fieldNames) {
			for (Panel panel : beforeButtonsPanels) {
				if (panel instanceof ComponentWithLabelAndValidationPanel) {
					ComponentWithLabelAndValidationPanel componentWithLabelAndValidationPanel = (ComponentWithLabelAndValidationPanel) panel;
					if (fieldName.equals(componentWithLabelAndValidationPanel.getFieldName())) {
						componentWithLabelAndValidationPanel.setVisible(!componentWithLabelAndValidationPanel
								.isVisible());
						target.add(componentWithLabelAndValidationPanel);
					}
				}
			}
		}
	}

	/**
	 * Helper method for rendering an error box as a result of an Ajax event.
	 *
	 * @param target
	 * @param component
	 */
	public void handleErrorsInForm(AjaxRequestTarget target, Component component) {
		target.add(component);
	}

	/**
	 * @return namespace to use in properties file. E.g. "myform" resulting in
	 *         keys like "myform.group.mygroup"
	 */
	public String getNameSpace() {
		return beanModel.getObject().getClass().getSimpleName();  
	}

	public Form<T> getForm() {
		return form;
	}

	public void setForm(Form<T> form) {
		this.form = form;
	}

	public IModel<T> getBeanModel() {
		return beanModel;
	}

	public void setBeanModel(IModel<T> beanModel) {
		this.beanModel = beanModel;
	}

	public boolean isAjaxValidate() {
		return ajaxValidate;
	}

	public boolean isUsingInlineHelp() {
		return usingInlineHelp;
	}

	public boolean isUsingLabels() {
		return usingLabels;
	}

	public  ILabelStrategy getLabelStrategy() {
		return labelStrategy;
	}

	/**
	 * Set the strategy for labels of the components to be added.
	 * Must be set before adding any {@link ComponentWithLabelAndValidationPanel}.
	 * @param labelStrategy
	 */
	public void setLabelStrategy(ILabelStrategy labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public DefaultExceptionHandler getDefaultExceptionHandler() {
		if (exceptionHandler == null || !(exceptionHandler instanceof DefaultExceptionHandler)) {
			exceptionHandler = new DefaultExceptionHandler(form);
		}
		return (DefaultExceptionHandler) exceptionHandler;
	}


	public Jsr303FormExceptionHandler getExceptionHandler() {
		if (exceptionHandler == null) {
			exceptionHandler = getDefaultExceptionHandler();
		}
		return exceptionHandler;
	}

	public ComponentContainerPanel getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(ComponentContainerPanel parentContainer) {
		this.parentContainer = parentContainer;
	}

	public void setExceptionHandler(Jsr303FormExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public String getErrorMessagePattern() {
		if (errorMessagePattern == null && parentContainer != null) {
			return parentContainer.getErrorMessagePattern();
		} else {
			return errorMessagePattern;
		}
	}

	public void setErrorMessagePattern(String errorMessagePattern) {
		this.errorMessagePattern = errorMessagePattern;
	}

	/**
	 * @return spans for labels
	 */
	public SpanType[] getLabelSpans() {
		return labelSpans;
	}
	
	/**
	 * @param spans
	 */
	public void setLabelSpans(SpanType ... spans) {
		this.labelSpans = spans;
	}

	/**
	 * @return spans for editors
	 */
	public SpanType[] getEditorSpans() {
		return editorSpans;
	}
	
	/**
	 * @param spans
	 */
	public void setEditorSpans(SpanType ... spans) {
		this.editorSpans = spans;
	}
	
	/**
	 * @return spans for help
	 */
	public SpanType[] getHelpSpans() {
		return helpSpans;
	}

	/**
	 * @param spans
	 */
	public void setHelpSpans(SpanType ... spans) {
		this.helpSpans = spans;
	}
}
