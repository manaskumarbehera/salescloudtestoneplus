package dk.jyskit.waf.wicket.components.images.temporaryimage;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * This class creates and mounts a resource for an image. The image is unmounted when the 
 * object is garbagecollected. 
 * 
 * @author jan
 */
public class TemporaryImage implements Serializable {
	private static final String BASICPATH = "/tmpimg/";

	private static final long serialVersionUID = 5858912069381703366L;
	
	private String imgResourceKey;
	private ResourceReference resourceReference;

	public TemporaryImage(byte[] imageData) {
		imgResourceKey = String.valueOf(System.currentTimeMillis());
		
		final TemporaryImageResource imageResource = new TemporaryImageResource(imageData);
		
		resourceReference = new ResourceReference(TemporaryImage.class, imgResourceKey) {
			@Override
			public IResource getResource() {
				return imageResource;
			}
		};
		WebApplication.get().mountResource(BASICPATH + imgResourceKey, resourceReference);
	}
	
	public TemporaryImage(final IModel<byte[]> model) {
		imgResourceKey = String.valueOf(System.currentTimeMillis());
		
		resourceReference = new ResourceReference(TemporaryImage.class, imgResourceKey) {
			@Override
			public IResource getResource() {
				return new TemporaryImageResource(model.getObject());
			}
		};
		WebApplication.get().mountResource(BASICPATH + imgResourceKey, resourceReference);
	}
	
	public TemporaryImage(final IModel<byte[]> model, String extension) {
		imgResourceKey = String.valueOf(System.currentTimeMillis()) + "." + extension;
		
		resourceReference = new ResourceReference(TemporaryImage.class, imgResourceKey) {
			@Override
			public IResource getResource() {
				return new TemporaryImageResource(model.getObject());
			}
		};
		WebApplication.get().mountResource(BASICPATH + imgResourceKey, resourceReference);
	}
	
	public ResourceReference getResourceReference() {
		return resourceReference;
	}

	public String getUrl(RequestCycle requestCycle) {
		return requestCycle.urlFor(resourceReference, null).toString();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		WebApplication.get().unmount(BASICPATH + imgResourceKey);
	}
}
