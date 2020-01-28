package dk.jyskit.waf.wicket.components.progress;

import java.io.Serializable;

import org.wicketstuff.async.task.IProgressObservableRunnable;

public abstract class AbstractProgressTask implements IProgressObservableRunnable, Serializable {

	private StringBuilder logText;
    private double progress;
    private String message;

    public AbstractProgressTask() {
    }
    
    @Override
    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String getProgressMessage() {
        return message;
    }

    public void setProgressMessage(String message) {
        this.message = message;
    }
    
    public void sleep(long millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
		}
    }

	public StringBuilder getLogText() {
		return logText;
	}

	public void setLogText(StringBuilder logText) {
		this.logText = logText;
	}

}
