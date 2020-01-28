package dk.jyskit.waf.wicket.components.jquery.kendo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

import org.apache.wicket.util.io.IClusterable;

@Getter
@Setter
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class AnimationEffect  implements IClusterable {
	private String effects;
	private int duration;
}
