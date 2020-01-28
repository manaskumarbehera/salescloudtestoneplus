package dk.jyskit.waf.wicket.components.progress;

import java.util.concurrent.TimeUnit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.async.components.IRunnableFactory;
import org.wicketstuff.async.components.InteractionState;
import org.wicketstuff.async.components.ProgressBar;
import org.wicketstuff.async.components.ProgressButton;
import org.wicketstuff.async.components.TaskState;
import org.wicketstuff.async.task.AbstractTaskContainer;
import org.wicketstuff.async.task.DefaultTaskManager;

import dk.jyskit.waf.wicket.components.containers.AjaxContainer;
import dk.jyskit.waf.wicket.components.widget.WidgetPanel;

public class ProgressSection extends WidgetPanel {
	private StringBuilder logText;

	public ProgressSection(String wicketId, IModel<String> headerModel, 
			final AbstractProgressTask progressTask, final ProgressSection ... nextSections) {
		super(wicketId);
		
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		
		label(headerModel);
		
        Form<?> form = new Form<Void>("form");
        add(form);
        
        final WebMarkupContainer logContainer = new AjaxContainer("logContainer", false);
        form.add(logContainer);
		
    	logText = new StringBuilder();
        final Label logLabel = new Label("log", new AbstractReadOnlyModel<String>() {
        	@Override
        	public String getObject() {
        		return logText.toString();
        	}
		});
        logLabel.setEscapeModelStrings(false);
        logContainer.add(logLabel);
        
        progressTask.setLogText(logText);
        
        AbstractTaskContainer taskContainer	= DefaultTaskManager.getInstance().makeContainer(1000L, TimeUnit.MINUTES);
        // Create a progress button.
        ProgressButton progressButton = new ProgressButton("startButton", form, Model.of(taskContainer), new IRunnableFactory() {
			@Override
			public Runnable getRunnable() {
				 return progressTask;
			}
        }, Duration.milliseconds(2000L)) {
        	@Override
        	protected void onTaskSuccess(AjaxRequestTarget target) {
        		target.add(logContainer.setVisible(true));
        		if (nextSections.length > 0) {
            		target.add(nextSections[0].setVisible(true));
        		}
        	}
        	
        	@Override
        	protected void onTaskCancel(AjaxRequestTarget target) {
        		target.add(logContainer.setVisible(true));
        	}
        	
        	@Override
        	protected void onTaskError(AjaxRequestTarget target) {
        		target.add(logContainer.setVisible(true));
        	}
        	
        	@Override
        	protected void onTaskStart(AjaxRequestTarget target) {
        		
        		logText.delete(0, logText.length());
        		target.add(logContainer.setVisible(false));
        		for (ProgressSection section : nextSections) {
               		target.add(section.setVisible(false));
        		}
        	}
        };
        
        progressButton.registerMessageModel(Model.of("Start"), InteractionState.STARTABLE, InteractionState.RESTARTABLE);
        progressButton.registerMessageModel(Model.of("Cancel"), InteractionState.CANCELABLE);
        progressButton.registerMessageModel(Model.of("Running..."), InteractionState.NON_INTERACTIVE);

        progressButton.registerCssClassModel(Model.of("btn-primary"), TaskState.PLAIN_NON_RUNNING, TaskState.CANCELED_NON_RUNNING);
        progressButton.registerCssClassModel(Model.of("btn-warning"), TaskState.PLAIN_RUNNING, TaskState.CANCELED_RUNNING);
        progressButton.registerCssClassModel(Model.of("btn-danger"), TaskState.ERROR_NON_RUNNING);

        // Create a progress bar
        ProgressBar progressBar = new ProgressBar("progressBar", progressButton);

        progressBar.registerCssClassModel(Model.of("progress-info progress-striped active"), TaskState.PLAIN_RUNNING);
        progressBar.registerCssClassModel(Model.of("progress-warning progress-striped active"), TaskState.CANCELED_RUNNING);
        progressBar.registerCssClassModel(Model.of("progress-info progress-striped"), TaskState.PLAIN_NON_RUNNING);
        progressBar.registerCssClassModel(Model.of("progress-warning progress-striped"), TaskState.CANCELED_NON_RUNNING);
        progressBar.registerCssClassModel(Model.of("progress-danger progress-striped"), TaskState.ERROR_NON_RUNNING);

        // Add components to page
        form.add(progressButton);
        form.add(progressBar);
	}
}
