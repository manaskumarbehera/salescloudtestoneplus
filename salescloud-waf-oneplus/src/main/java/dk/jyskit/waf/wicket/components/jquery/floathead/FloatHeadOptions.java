package dk.jyskit.waf.wicket.components.jquery.floathead;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

import com.fasterxml.jackson.annotation.JsonRawValue;

import dk.jyskit.waf.wicket.components.jquery.JsScript;
import dk.jyskit.waf.wicket.components.jquery.TypedOptions;

@Getter
@Setter
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class FloatHeadOptions extends TypedOptions {
	/**
	 * Defines a container element inside of which the table scrolls vertically
	 * and/or horizontally. usually a wrapping div
	 */
	JsScript scrollContainer;

	/**
	 * Offset from the top of the window where the floating header will 'stick'
	 * when scrolling down. Number or function.
	 */
	@JsonRawValue
	String scrollingTop;

	/**
	 * Offset from the bottom of the window where the floating header will
	 * 'stick' when scrolling down. Number or function.
	 */
	@JsonRawValue
	String scrollingBottom;

	/**
	 * Position the floated header using absolute positioning or using fixed
	 * positioning. Fixed positioning performs better with tables that use
	 * window scrolling, but fails miserably on highly dynamic pages where DOM
	 * can be suddenly modified causing the location of the floated table to
	 * shift. (You should call .floatThead('reflow') in this case, but you cant
	 * always instrument your code to make that call.) Default: true
	 */
	Boolean useAbsolutePositioning;

	/**
	 * The headers are repositioned on resize. Because this event has the
	 * potential to fire a bunch of times, it is debounced. This is the debounce
	 * rate. Default: 1
	 */
	Integer debounceResizeMs;

	/**
	 * z-index of the floating header. Default 1001
	 */
	Integer zIndex = 999;

	/**
	 * Specifies which tag is used to find header cells (in the table'sthead
	 * element). Default: th
	 */
	String cellTag;

	/**
	 * Point out various possible issues via console.log if it is available.
	 * Default: false
	 */
	Boolean debug;

	/**
	 * Used by IE Only. Function.
	 */
	JsScript getSizingRow;

}
