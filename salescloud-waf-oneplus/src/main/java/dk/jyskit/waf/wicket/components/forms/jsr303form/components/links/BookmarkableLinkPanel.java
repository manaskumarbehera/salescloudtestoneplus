package dk.jyskit.waf.wicket.components.forms.jsr303form.components.links;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class BookmarkableLinkPanel extends Panel {
	private BookmarkablePageLink link;

	/**
	 * Non-submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param pageClass
	 * @param parameters
	 */
	public BookmarkableLinkPanel(ComponentContainerPanel container, String labelKey, final Class pageClass, final PageParameters parameters) {
		super("panel");

		link = new BookmarkablePageLink<Void>("link", pageClass, parameters);

		add(link);

		link.add(new AttributeModifier("class", true, new Model(labelKey)));

		Fragment fragment = new Fragment("fragment", "f_text", this);
		fragment.add(new Label("text", container.getLabelStrategy().linkLabel(labelKey)));
		link.add(fragment);
	}

	/**
	 * Non-submitting link with image.
	 *
	 * @param container
	 * @param imageResource
	 * @param pageClass
	 * @param parameters
	 */
	public BookmarkableLinkPanel(ComponentContainerPanel container, ResourceReference imageResource, final Class pageClass, final PageParameters parameters) {
		super("panel");

		BookmarkablePageLink link = new BookmarkablePageLink<Void>("link", pageClass, parameters);
		add(link);

		Fragment fragment = new Fragment("fragment", "f_img", this);
		fragment.add(new Image("img", imageResource));
        link.add(fragment);
	}

	public BookmarkablePageLink getLink() {
		return link;
	}
}
