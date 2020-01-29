package dk.jyskit.waf.wicket.components.panels.markdown;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.markdownj.MarkdownProcessor;

public class MarkdownPanel extends Panel {
	public MarkdownPanel(String id, IModel<String> markdownModel) {
		super(id);
		
		setRenderBodyOnly(true);
		
//		PegDownProcessor processor = new PegDownProcessor();
//		String html = processor.markdownToHtml(markdownModel.getObject());
		
		String html = (new MarkdownProcessor()).markdown(markdownModel.getObject());
		
		Label label = new Label("markdown", html);
		label.setEscapeModelStrings(false);
		add(label);
	}
}
