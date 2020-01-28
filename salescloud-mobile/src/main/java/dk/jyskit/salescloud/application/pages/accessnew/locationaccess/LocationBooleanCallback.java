package dk.jyskit.salescloud.application.pages.accessnew.locationaccess;

import java.io.Serializable;

public interface LocationBooleanCallback extends Serializable {
	boolean isTrue(LocationAccessComponentWrapper w);
}
