package dk.jyskit.waf.wicket.components.forms.jsr303form.components.textarea;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.validator.constraints.Length;

import dk.jyskit.waf.application.utils.exceptions.SystemException;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

/**
 * Special attributes:
 * 		auto-grow=yes: rows are added automatically.
 *		show-remaining=yes: shows how many characters you have left.
 * 		
 * Use max-height in css to limit height. When limit is reached, scroll bars appear
 * 
 * @author jan
 */
@Slf4j
public class TextAreaPanel extends ComponentWithLabelAndValidationPanel<TextArea> {
	private boolean autoGrow;
	private TextArea textArea;
	private boolean showRemaining;
	private int maxLength;
	private static final ResourceReference JS_ELASTIC 		= new PackageResourceReference(TextAreaPanel.class, "js/jquery.elastic.js");
	private static final ResourceReference JS_MAX_LENGTH	= new PackageResourceReference(TextAreaPanel.class, "js/jquery.maxlength.js");
	
	public TextAreaPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		textArea = new TextArea("editor", propertyModel) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		};
		textArea.setOutputMarkupId(true);
		
		autoGrow = "yes".equalsIgnoreCase(attributesMap.get("auto-grow"));
        attributesMap.remove("auto-grow");

        showRemaining = "yes".equalsIgnoreCase(attributesMap.get("show-remaining"));
		if (showRemaining) {
			// introspect to get a max length
			Field f;
			try {
				f = propertyModel.getObjectClass().getDeclaredField(propertyModel.getPropertyExpression());
				Annotation[] annotations = f.getAnnotations();

				Length length = null;
				for (Annotation a : annotations) {
				    if (a instanceof Length) {
				    	length = (Length) a; // here it is !!!
				    	break;
				    }
				}
				if (length == null) {
					throw new SystemException("The annotation @Length is not set for the field '" + propertyModel.getPropertyExpression() + "'");
				}
				maxLength = length.max();
			} catch (Exception e) {
				log.warn("Unable to get Length annotation from field: " + fieldName, e);
			}
			
	        attributesMap.remove("auto-grow");
		}
		
		init(textArea, attributesMap);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (autoGrow) {
			response.render(JavaScriptReferenceHeaderItem.forReference(JS_ELASTIC));
			response.render(OnDomReadyHeaderItem.forScript("$(function() {$(\"#" + textArea.getMarkupId() + "\").elastic();})"));
		}
		if (showRemaining) {
			response.render(JavaScriptReferenceHeaderItem.forReference(JS_MAX_LENGTH));
			
			response.render(OnDomReadyHeaderItem.forScript(
	        		"$(function() {$(\"#" + textArea.getMarkupId() + "\").maxlength({" +  
	                    	"events: [], " +   
	                    	"maxCharacters: " + maxLength + ", " +  
	                    	"status: true, " +   
	                    	"statusClass: \"textarea-remaining\", " +  
	                    	"statusText: \"bogstaver tilbage\", " + 
	                    	"notificationClass: \"textarea-maxlength-reached\", " +   
	                    	"showAlert: false, " +   
	                    	"alertText: \"Du har skrevet for meget tekst\", " +   
	                    	"slider: false " +   
	                    	"});})"
					));
		}
	}
}

/*
$(
	function() {
		$("#editor").elastic();
	}
)

Wicket.Event.add(window, "domready", 
function(event) { 
	$(function() {
		$(#editor1c).elastic();
	};
});

$('#textarea_1_1').maxlength({  
    events: [],    
    maxCharacters: 10,   
    status: true,    
    statusClass: "textarea-remaining",  
    statusText: "bogstaver tilbage",  
    notificationClass: "textarea-maxlength-reached",   
    showAlert: false,    
    alertText: "Du har skrevet for meget tekst",   
    slider: false    
  });


*/