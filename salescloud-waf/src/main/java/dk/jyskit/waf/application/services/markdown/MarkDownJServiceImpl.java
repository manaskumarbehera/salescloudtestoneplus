package dk.jyskit.waf.application.services.markdown;

import org.markdownj.MarkdownProcessor;

public class MarkDownJServiceImpl implements MarkDownService {

	@Override
	public String convertToHtml(String markdown) {
		MarkdownProcessor processor = new MarkdownProcessor();
		return processor.markdown(markdown);
	}

}
