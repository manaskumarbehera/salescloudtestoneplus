package dk.jyskit.waf.wicket.components.jquery.kendo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;
import dk.jyskit.waf.wicket.components.jquery.JsScript;
import dk.jyskit.waf.wicket.components.jquery.TypedOptions;

/**
 * Example config options for a kendo timepicker 
 * @author palfred
 *
 */
@Getter
@Setter
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class TimePickerOptions extends TypedOptions {
	private static final boolean TEST = false;
	private OpenCloseAnimation animation;
	private int interval;
	private String culture;
	private String format = "HH:mm";
	private String[] parseFormats = {"HH:mm", "HH:mm:ss", "HH"};
	
	private JsScript testScript;
	
	{
		if (TEST) { 
			testScript = new JsScript("alert('This is a test')");
			animation = new OpenCloseAnimation();
			animation.setOpen(new AnimationEffect("fadeIn zoom:in", 500));
			animation.setClose(new AnimationEffect("fadeOut zoom:out", 500));
		}
	}
}