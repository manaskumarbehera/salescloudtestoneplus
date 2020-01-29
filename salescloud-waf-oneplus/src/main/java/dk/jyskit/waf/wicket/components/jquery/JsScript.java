package dk.jyskit.waf.wicket.components.jquery;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * Represents a piece of javascript to be encoded "Raw"
 * @author palfred
 *
 */
@Data
@Wither
@AllArgsConstructor
@NoArgsConstructor
public class JsScript implements Serializable {
	private String script;

	public String toScript() {
		return getScript();
	}
}