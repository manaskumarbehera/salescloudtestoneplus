package dk.jyskit.waf.wicket.layouts;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

/**
 * Options for one splitter pane.
 * @author palfred
 *
 */
@Getter
@Setter
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class SplitterPane implements Serializable {
	Boolean collapsible;
	Boolean resizable;
	Boolean scrollable;

	/**
	 * The width is one of 'auto' | '#px' | '#%' | null (where # is an integer).
	 */
	private String size;

	/**
	 * The width is one of 'auto' | '#px' | '#%' | null (where # is an integer).
	 */
	private String min;

	/**
	 * The width is one of 'auto' | '#px' | '#%' | null (where # is an integer).
	 */
	private String max;
}
