package dk.jyskit.waf.utils.filter.queries;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.apache.wicket.util.string.Strings;

public abstract class AbstractTranslator implements FilterTranslator {

	protected Path getPath(Root root, String attributeName) {
		String[] elements = Strings.split(attributeName, '.');
		Path path = root;
		for (String element : elements) {
			path = path.get(element);
		}
		return path;
	}
}
