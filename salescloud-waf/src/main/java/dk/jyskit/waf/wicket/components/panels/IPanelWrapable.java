package dk.jyskit.waf.wicket.components.panels;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * Common interface for components that have the ability to wrap themselves in a panel
 * @author palfred
 *
 */
public interface IPanelWrapable {
	/**
	 * The markup must have markup with wicket:id matching the componetId.
	 * @return
	 */
	String getPanelBodyMarkup(String componentId);

	/**
	 * Wraps component in panel.
	 * The normal implementation in a wicket component should be:
	 * <code>
	 * 	return new WrapperPanelWithMarkup(panelId, this, getPanelBodyMarkup(this.getId()));
	 * </code>
	 * @param panelId
	 * @return
	 * @see WrapperPanelWithMarkup
	 */
	Panel wrapInPanel(String panelId);
}
