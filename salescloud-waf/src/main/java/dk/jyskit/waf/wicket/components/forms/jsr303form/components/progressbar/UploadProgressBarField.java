package dk.jyskit.waf.wicket.components.forms.jsr303form.components.progressbar;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.Model;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.UploadProgressBar;

import dk.jyskit.waf.wicket.components.forms.jsr303form.components.fileupload.FileUploadField;

public class UploadProgressBarField extends FormComponentPanel {
	private UploadProgressBar progressBar;

    /**
     * @param id
     */
    public UploadProgressBarField(String id, Form form, FileUploadField fileUploadField) {
        super(id, Model.of(""));
        
        progressBar = new UploadProgressBar("progress", form, fileUploadField, Model.of(Integer.valueOf(0)));
        add(progressBar);
    }
}