package dk.jyskit.waf.utils.dataexport.common;

import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.time.Duration;

public class NonCachedResourceStreamResource extends ResourceStreamResource {
	public NonCachedResourceStreamResource() {
		super();
		setCacheDuration(Duration.NONE);
	}
}
