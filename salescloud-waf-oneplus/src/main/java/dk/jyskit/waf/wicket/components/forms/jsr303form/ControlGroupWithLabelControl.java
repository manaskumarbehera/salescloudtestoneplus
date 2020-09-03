package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.iterator.ComponentHierarchyIterator;

import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SpanType;
import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.core.util.Components;

/**
 * Variation of wicket bootstrap control-group
 *
 * Note - Not extending ControlGroup anymore. Markup of errors had to be changed.
 */
public class ControlGroupWithLabelControl extends Border {
    private final Label label;
    private final InfoLabel help;
    private final InfoLabel error;
    private final Model<String> stateClassName;

	private boolean hideLabel = false;

	private boolean inlineHelp;
	private WebMarkupContainer editorCol;
	private SpanType[] labelSpans = new SpanType[] {};
	private SpanType[] editorSpans = new SpanType[] {};
	private SpanType[] helpSpans = new SpanType[] {};
	private boolean required;

	public ControlGroupWithLabelControl(String id, IModel<String> label, boolean inlineHelp, boolean hideLabel, boolean required) {
        this(id, label, Model.of(""), inlineHelp, hideLabel, required);
	}

	public ControlGroupWithLabelControl(String id, IModel<String> label, final IModel<String> help, boolean inlineHelp, boolean hideLabel, boolean required) {
		super(id, Model.of(""));
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		
        this.label		= new Label("label", label);
        this.editorCol	= new WebMarkupContainer("editorCol");
        this.help		= new InfoLabel("help", help, InfoLabel.TYPE_INFO);
        this.error		= new InfoLabel("error", Model.<Serializable>of(""), InfoLabel.TYPE_IMPORTANT);

		this.inlineHelp = inlineHelp;
		this.hideLabel 	= hideLabel;
		this.required   = required;

        stateClassName = Model.of("");

		if (hideLabel) {
			if (inlineHelp) {
		        addToBorder(this.editorCol, this.label, this.editorCol);
			} else {
				labelSpans	= new SpanType[] {SmallSpanType.SPAN2};
				editorSpans	= new SpanType[] {SmallSpanType.SPAN10};
		        addToBorder(this.editorCol, this.label, this.editorCol);
			}
		} else {
			if (inlineHelp) {
				labelSpans	= new SpanType[] {SmallSpanType.SPAN4};
				editorSpans	= new SpanType[] {SmallSpanType.SPAN4};
				helpSpans	= new SpanType[] {SmallSpanType.SPAN4};
		        addToBorder(this.editorCol, this.label, this.editorCol);
			} else {
				labelSpans	= new SpanType[] {SmallSpanType.SPAN5};
				editorSpans	= new SpanType[] {SmallSpanType.SPAN7};
		        editorCol.add(this.error);
		        editorCol.add(this.help);
		        addToBorder(this.label, this.editorCol);
			}
		}
	}
	
	@Override
	public String getVariation() {
		if (hideLabel) {
			if (inlineHelp) {
				return "nolabel_inline";
			} else {
				return "nolabel_block";
			}
		} else {
			if (inlineHelp) {
				return "inline";
			} else {
				return "block";
			}
		}
	}

	/**
	 * @param spans
	 */
	public void setLabelSpans(SpanType ... spans) {
		this.labelSpans = spans;
	}

	/**
	 * @param spans
	 */
	public void setEditorSpans(SpanType ... spans) {
		this.editorSpans = spans;
	}

	/**
	 * @param spans
	 */
	public void setHelpSpans(SpanType ... spans) {
		this.helpSpans = spans;
	}

	// The rest of the methods are copied from ControlGroup!

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "div");
        if (required) {
            Attributes.addClass(tag, "control-group", "required", stateClassName.getObject());
        } else {
            Attributes.addClass(tag, "control-group", stateClassName.getObject());
        }
    }

    public ControlGroupWithLabelControl label(final IModel<String> label) {
        this.label.setDefaultModel(label);
        return this;
    }

    public ControlGroupWithLabelControl help(final IModel<String> help) {
        this.help.setDefaultModel(help);
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final List<FormComponent<?>> formComponents = findFormComponents();
        for (final FormComponent<?> fc : formComponents) {
            fc.setOutputMarkupId(true);
            label.add(new AttributeModifier("for", fc.getMarkupId()));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Components.show(help, label, error);
        
    	String cssClass = "";
    	
        if ((!inlineHelp) || (!hideLabel)) {
        	cssClass = "control-label";
        	for (SpanType span : labelSpans) {
				cssClass += " " + span.cssClassName();
			}
            label.add(AttributeModifier.replace("class", cssClass));
        }
        
    	cssClass = "";
    	for (SpanType span : editorSpans) {
			cssClass += span.cssClassName() + " ";
		}
        editorCol.add(AttributeModifier.replace("class", cssClass));
        
        if ((inlineHelp) || (!hideLabel)) {
        	cssClass = "help-inline ";
        	for (SpanType span : helpSpans) {
    			cssClass += span.cssClassName() + " ";
    		}
            help.add(AttributeModifier.replace("class", cssClass));
        }
        
        stateClassName.setObject("");
        error.setDefaultModelObject("");

        final List<FormComponent<?>> formComponents = findFormComponents();
        for (final FormComponent<?> fc : formComponents) {
            final FeedbackMessages messages = fc.getFeedbackMessages();
            if (!messages.isEmpty()) {
                final FeedbackMessage worstMessage = getWorstMessage(messages);
                worstMessage.markRendered();

                stateClassName.setObject(toClassName(worstMessage));
                error.setDefaultModelObject(worstMessage.getMessage());

                break; // render worst message of first found child component with feedback message
            }
        }

        Components.hideIfModelIsEmpty(help);
        // This check makes sure the label column is kept in case the label is " ". We are dealing with two different definitions of "empty". 
        if (StringUtils.isEmpty(label.getDefaultModelObjectAsString())) {
        	Components.hideIfModelIsEmpty(label);
        }
        Components.hideIfModelIsEmpty(error);
    }

    private List<FormComponent<?>> findFormComponents() {
        final ComponentHierarchyIterator it = getBodyContainer().visitChildren(FormComponent.class);

        final List<FormComponent<?>> components = new ArrayList<FormComponent<?>>();
        while (it.hasNext()) {
            components.add((FormComponent<?>) it.next());
        }

        return components;
    }

    private FeedbackMessage getWorstMessage(final FeedbackMessages messages) {
        FeedbackMessage ret;
        ret = messages.first(FeedbackMessage.FATAL);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.ERROR);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.WARNING);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.SUCCESS);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.INFO);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.DEBUG);
        if (ret != null) {
            return ret;
        }
        ret = messages.first(FeedbackMessage.UNDEFINED);
        if (ret != null) {
            return ret;
        }
        return messages.first();
    }


    private String toClassName(final FeedbackMessage message) {
        if (message.isLevel(FeedbackMessage.ERROR)) {
            return "has-error";
        }
        if (message.isLevel(FeedbackMessage.WARNING)) {
            return "has-warning";
        }
        if (message.isLevel(FeedbackMessage.SUCCESS)) {
            return "has-success";
        }
        if (message.isLevel(FeedbackMessage.INFO)) {
            return "has-info";
        }
        return "";
    }

}
