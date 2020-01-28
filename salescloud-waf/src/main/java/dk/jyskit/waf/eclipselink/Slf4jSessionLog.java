package dk.jyskit.waf.eclipselink;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.util.string.Strings;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/** Elcipse link logger the outputs to SLf4j / logback */
@Slf4j
public class Slf4jSessionLog extends AbstractSessionLog implements SessionLog {
	private boolean allwaysLogException = false;

	public Slf4jSessionLog(){
	}
	
	public Slf4jSessionLog(boolean allwaysLogException) {
		super();
		this.allwaysLogException = allwaysLogException;
	}

	@Override
	public void log(SessionLogEntry entry) {
		if (shouldLog(entry.getLevel())) {
			StringBuilder msg = new StringBuilder();
			msg.append(getSessionString(entry.getSession()));
			if (entry.hasMessage()) {
				msg.append(' ').append(formatMessage(entry));
			}
			
			Throwable exception = entry.getException();
			if (entry.getLevel() < SessionLog.SEVERE && !allwaysLogException) {
				exception = null;
				if (entry.getException() != null) {
					msg.append(" Exception: " + entry.getException().getMessage()); 
				}
			}
			if (entry.getLevel() >= SessionLog.SEVERE) {
				log.error(msg.toString(), exception);
			} else if (entry.getLevel() >= SessionLog.WARNING) {
				log.warn(msg.toString(), exception);
			} else if (entry.getLevel() >= SessionLog.INFO) {
				log.info(msg.toString(), exception);
			} else if (entry.getLevel() >= SessionLog.FINER) {
				log.debug(msg.toString(), exception);
			} else if (entry.getLevel() >= SessionLog.FINEST) {
				log.trace(msg.toString(), exception);
			} else {
				log.info(msg.toString(), exception);
			}
		}
	}

	public boolean isAllwaysLogException() {
		return allwaysLogException;
	}

	public void setAllwaysLogException(boolean allwaysLogException) {
		this.allwaysLogException = allwaysLogException;
	}

}
