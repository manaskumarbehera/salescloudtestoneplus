package dk.jyskit.waf.utils.filter;

import org.apache.wicket.model.PropertyModel;

public abstract class AbstractPropertyFilter implements Filter {

	protected final String propertyName;

	public AbstractPropertyFilter(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
	    return propertyName;
	}

	@Override
	public boolean appliesToProperty(String propertyName) {
	    return getPropertyName() != null && getPropertyName().equals(propertyName);
	}

	public Object getPropertyValue(Object entity) throws UnsupportedOperationException {
		try {
			return new PropertyModel<>(entity, getPropertyName()).getObject();
		} catch (Exception e) {
			throw new UnsupportedOperationException(e.getMessage());
		}
	}
}
