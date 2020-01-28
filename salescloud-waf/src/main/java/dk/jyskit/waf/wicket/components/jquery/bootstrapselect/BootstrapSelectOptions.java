package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import com.fasterxml.jackson.annotation.JsonRawValue;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import dk.jyskit.waf.wicket.components.jquery.TypedOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

@Getter
@Setter
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapSelectOptions extends TypedOptions {
	/**
	 * The container for the popup e.g.,: 'body' | '.main-body'
	 */
	
	private String container;
	
	/**
	 * The format for the text displayed when selectedTextFormat is count or count &gt; #. {0} is the selected amount. {1} is total available for selection.
	 */
	private String countSelectedText;

	/**
	 * The selected format
	 * <ul>
	 * <li><code>values</code> A comma delimited list of selected values. (default)</li>
	 * <li><code>count</code> If one item is selected, then the value is shown, if more than one is selected then the number of selected items is displayed, eg <span class="label">2 of 6 selected</span></li>
	 * <li><code>count &gt; x</code> Where X is the number of items selected when the display format changes from <code>values</code> to <code>count</code></li>
	 * </ul>
	 */
	private String selectedTextFormat;
	
	/**
	 * Controls whether to use Kendo UI.
	 */
	private Boolean kendoUI;

	/**
	 * Controls whether to dropup if not has enough room to fully open normally, but there is more room above.
	 */
	private Boolean dropupAuto;
	
	/**
	 * Header to the top of the menu; includes a close button by default
	 */
	private String header;
	
	/**
	 * Controls if disabled options and optgroups should be hidden from the menu
	 */
	private Boolean hideDisabled;
	
	/**
	 * Size is 'auto' | integer | false.
	 * 
	 * <p>When set to <code>'auto'</code>, the menu always opens up to show as many items as the window will allow without being cut off</p><p>set to <code>false</code> to always show all items</p>
	 * @see #withSizeAuto()
	 * @see #withSizeSize(int)
	 * @see #withSizeFalse()
	 */
	@JsonRawValue
	private String size;	
	
	/**
	 * Controls show of sub text of selected options. 
	 */
	private Boolean showSubtext;	

	/**
	 * Controls show of icons of selected options. 
	 */
	private Boolean showIcon;	
	
	/**
	 * Controls show of any custom HTML from the selected options.
	 */
	private Boolean showContent;
	
	/**
	 * Style of the "select"/ button. 
	 * Button types {@link Type} can be used.
	 * @see #withStyleProvider(ICssClassNameProvider)
	 */
	private String style;
	
	/**
	 * Default text for bootstrap-select
	 */
	private String title;	
	
	/**
	 * Controls whether to have a live search in the dropdown menu..
	 */
	private Boolean liveSearch;
	/**
	 * The width is one of 'auto' | '#px' | '#%' | null (where # is an integer). 
	 * @see #withWidthAuto()
	 * @see #withWidthPct(int)
	 * @see #withWidthPx(int)
	 */
	@JsonRawValue
	private String width;
	
	public BootstrapSelectOptions withSelectedTextFormatValues() {
		return withSelectedTextFormat("values");
	}

	public BootstrapSelectOptions withSelectedTextFormatCount() {
		return withSelectedTextFormat("count");
	}
	
	public BootstrapSelectOptions withSelectedTextFormatCount(int count) {
		return withSelectedTextFormat("count > " + count);
	}
	
	public BootstrapSelectOptions withSizeAuto() {
		return withSize("'auto'");
	}
	
	public BootstrapSelectOptions withSizeFalse() {
		return withSize("false");
	}

	public BootstrapSelectOptions withSizeSize(int size) {
		return withSize(Integer.toString(size));
	}
	
	public BootstrapSelectOptions withWidthAuto() {
		return withWidth("'auto'");
	}
	
	public BootstrapSelectOptions withWidthPx(int size) {
		return withWidth(Integer.toString(size) + "px");
	}

	public BootstrapSelectOptions withWidthPct(int size) {
		return withWidth(Integer.toString(size) + "pct");
	}
	
	/**
	 * Set the style of the select button.
	 * @param styleProvider could be a {@link Buttons.Type}
	 * @return
	 */
	public BootstrapSelectOptions withStyleProvider(ICssClassNameProvider styleProvider) {
		return withStyle(styleProvider.cssClassName());
	}
}
