package dk.jyskit.waf.wicket.localization;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class MessageUtils {

	/**
	 * Shortcut to create message translation model
	 * 
	 * @param message
	 * @param args
	 * @return
	 */
	public static StringResourceModel translationModel(String message, Object... args) {
		return new StringResourceModel(message, new Model<Object[]>(args), message);
	}
}
