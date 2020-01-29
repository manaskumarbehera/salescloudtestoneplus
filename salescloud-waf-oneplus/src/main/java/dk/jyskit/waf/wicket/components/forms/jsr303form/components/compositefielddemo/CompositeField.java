package dk.jyskit.waf.wicket.components.forms.jsr303form.components.compositefielddemo;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class CompositeField extends FormComponentPanel {
    private TextField<String>nameField;
    private TextField<String>emailField;

    /**
     * @param id
     */
    public CompositeField(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        emailField = new TextField("emailField", new Model<String>(""));
        add(emailField);
        nameField = new TextField("nameField", new Model<String>(""));
        add(nameField);
    }

    @Override
    protected void convertInput() {
        
        /**
         * Build up a new Composite instance from the values in the fields.
         */
        Composite u = new Composite(emailField.getModelObject(), nameField.getModelObject());
        
        setConvertedInput(u);
    }

    /*
     * Here we pull out each field from the Composite if it exists and put the contents into the fields.
     */
    @Override
    protected void onBeforeRender() {
    	super.onBeforeRender();
        Composite u = (Composite) this.getModelObject();
        
        if (u != null) {
            // copy the field values into the form fields.
            this.emailField.setModelObject(u.getEmail());
            this.nameField.setModelObject(u.getName());
        }
    }
}