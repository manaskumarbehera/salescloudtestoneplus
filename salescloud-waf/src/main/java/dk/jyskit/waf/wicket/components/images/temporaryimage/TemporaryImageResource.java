package dk.jyskit.waf.wicket.components.images.temporaryimage;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;

@Slf4j
public class TemporaryImageResource extends DynamicImageResource {
	private byte[] temporaryImage;

	/**
	 * Construct.
	 * 
	 * @param imageData
	 */
	public TemporaryImageResource(byte[] imageData) {
		this.temporaryImage = imageData;
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {
		if (!isEmpty()) {
			return temporaryImage;
		}
		return null;
	}

	/**
	 * @return true if image is not provided or it does not contain input stream
	 */
	public boolean isEmpty() {
		return temporaryImage == null;
	}
	
	@Override
	protected IResourceCachingStrategy getCachingStrategy() {
		return new NoOpResourceCachingStrategy();
	}
}