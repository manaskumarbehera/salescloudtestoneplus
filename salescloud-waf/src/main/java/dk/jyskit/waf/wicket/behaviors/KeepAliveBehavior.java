package dk.jyskit.waf.wicket.behaviors;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.time.Duration;

// This old implementation caused ComponentNotFoundException!
//
//public class KeepAliveBehavior extends AbstractDefaultAjaxBehavior {
//	@Override
//	protected void respond(AjaxRequestTarget target) {
//		// prevent wicket changing focus
//		target.focusComponent(null);
//	}
//	
//	@Override
//	public void renderHead(Component component, IHeaderResponse response) {
//		super.renderHead(component, response);
//		response.renderOnLoadJavaScript("setInterval(function() { " + "  wicketAjaxGet('" + getCallbackUrl()
//				+ "', null, null, null); " + "}, 120 * 1000);");     // keep alive every 2 min.
//	}
//}

public class KeepAliveBehavior extends AbstractAjaxTimerBehavior {
	public KeepAliveBehavior() {
		super(Duration.minutes(2));
	}
	
    @Override
    protected void onTimer(AjaxRequestTarget target) {
        // Do nothing, just keep session alive
    }
}
