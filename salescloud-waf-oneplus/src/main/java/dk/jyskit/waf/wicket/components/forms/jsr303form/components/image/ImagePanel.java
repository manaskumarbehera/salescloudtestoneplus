package dk.jyskit.waf.wicket.components.forms.jsr303form.components.image;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class ImagePanel extends Panel {
	private WebMarkupContainer image;

	public ImagePanel(ComponentContainerPanel container, final String url, int width) {
		super("panel");
		
		image = new WebMarkupContainer("image");
		image.add(AttributeModifier.replace("src", url));
		image.add(AttributeModifier.replace("width", width + "px"));
		add(image);
	}
	
	public WebMarkupContainer getWebMarkupContainer() {
		return image;
	}
}

