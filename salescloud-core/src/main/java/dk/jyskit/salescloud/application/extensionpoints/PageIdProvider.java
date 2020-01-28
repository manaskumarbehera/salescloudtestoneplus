package dk.jyskit.salescloud.application.extensionpoints;

import java.util.List;

import org.apache.wicket.request.mapper.info.PageInfo;

public interface PageIdProvider {
	/**
	 * @return list of project specific page ids. PageIds are used as a link between a page and its ({@link PageInfo} .
	 */
	List<String> get();
}
