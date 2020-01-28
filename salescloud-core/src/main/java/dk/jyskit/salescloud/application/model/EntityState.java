package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.waf.wicket.utils.WicketUtils;

public enum EntityState {
	ACTIVE("entitystate.active"),
	INACTIVE("entitystate.inactive"),	// For making entity unavailable temporarily 
	DELETED("entitystate.deleted");		// For soft-deleting
	
	private String key;			// Used for localization

	private EntityState(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public static List<EntityState> valuesAsList() {
		List<EntityState> list = new ArrayList<EntityState>();
		for (int i = 0; i < values().length; i++) {
			EntityState value = values()[i];
			list.add(value);
		}
		return list;
	}
	
	@Override
	public String toString() {
		return WicketUtils.getLocalized(key, key); 
	}
}
