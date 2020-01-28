package dk.jyskit.salescloud.application.pages.accessnew.locationaccess;

import lombok.Data;

import java.io.Serializable;

@Data
public class VisibleAndEnabled implements Serializable {
	private boolean visible = false;
	private boolean enabled = true;
	private boolean dirty = false;

	public VisibleAndEnabled(LocationAccessComponentWrapper wrapper) {
		this(wrapper.getComponent().getParent().getParent().isVisible(), wrapper.getComponent().isEnabled());
	}

//	public VisibleAndEnabled() {
//	}

	public VisibleAndEnabled(boolean visible, boolean enabled) {
		this.visible = visible;
		this.enabled = enabled;
	}

//	public VisibleAndEnabled clone() {
//		VisibleAndEnabled visibleAndEnabled = new VisibleAndEnabled();
//		visibleAndEnabled.visible 	= visible;
//		visibleAndEnabled.enabled 	= enabled;
//		visibleAndEnabled.dirty 	= false;
//		return visibleAndEnabled;
//	}

//	public boolean equals(VisibleAndEnabled ve) {
//		return (ve.visible == visible) && (ve.enabled == enabled);
//	}
//
//	public static VisibleAndEnabled invisible() {
//		VisibleAndEnabled visibleAndEnabled = new VisibleAndEnabled();
//		visibleAndEnabled.setVisible(false);
//		visibleAndEnabled.setEnabled(true);
//		return visibleAndEnabled;
//	}
//
//	public static VisibleAndEnabled invisibleIfTrue(boolean condition) {
//		VisibleAndEnabled visibleAndEnabled = new VisibleAndEnabled();
//		if (condition) {
//			visibleAndEnabled.setVisible(false);
//		} else {
//			visibleAndEnabled.setVisible(true);
//		}
//		visibleAndEnabled.setEnabled(true);
//		return visibleAndEnabled;
//	}
}
