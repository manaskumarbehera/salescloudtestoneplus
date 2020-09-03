package dk.jyskit.waf.wicket.components.jquery;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import lombok.extern.slf4j.Slf4j;

import com.googlecode.wicket.jquery.core.Options;

/**
 * Base calls for type options for JQuery behaviors
 * @author palfred
 *
 */
@Slf4j
public class TypedOptions extends Options {

	public TypedOptions() {
	}

	/** 
	 * Extends base JSON with a JSON version of the properties of this.
	 */
	@Override
	public String toString() {
		resetPropertyOptions();
		set("X_X", "Y_Y");
		String superString = super.toString();
		String json = Json.toJson(this);
		String extended = superString.replace("\"X_X\": Y_Y", json.substring(1, json.length() -1));
		return extended;
	}

	public void resetPropertyOptions() {
		 try {
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors();
			 for (PropertyDescriptor descriptor : propertyDescriptors) {
				 if (descriptor.getReadMethod() != null) {
					 if (log.isDebugEnabled()) {
						 log.debug("Resetting defined property from generic map. Name: " + descriptor.getName());
					 }
					 set(descriptor.getName(), (Object) null);
				 }
			}
		} catch (IntrospectionException e) {
			log.warn("Unable to introspect properties of " + this.getClass().getName() + " " + e.getMessage());
		}
	}
}

