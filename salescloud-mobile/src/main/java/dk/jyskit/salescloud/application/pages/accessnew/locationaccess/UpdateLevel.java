package dk.jyskit.salescloud.application.pages.accessnew.locationaccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLevel implements Serializable {
	private boolean updateParent 	= false;
	private boolean updateComponent = false;

	public UpdateLevel(VisibleAndEnabled oldVisibleAndEnabled, LocationAccessComponentWrapper wrapper) {
		UpdateLevel updateLevel = new UpdateLevel();
		if (oldVisibleAndEnabled.isVisible() != wrapper.getComponent().getParent().getParent().isVisible()) {
			updateLevel.updateParent = true;
		}
		if (oldVisibleAndEnabled.isEnabled() != wrapper.getComponent().isEnabled()) {
			updateLevel.updateComponent = true;
		}
	}

	public static UpdateLevel createByVisible(VisibleAndEnabled oldVisibleAndEnabled, LocationAccessComponentWrapper wrapper, boolean visible) {
		UpdateLevel updateLevel = new UpdateLevel();
		if (oldVisibleAndEnabled.isVisible() != visible) {
			updateLevel.updateParent = true;
			wrapper.getComponent().getParent().getParent().setVisible(visible);
		}
		return updateLevel;
	}

	public static UpdateLevel createByEnabled(VisibleAndEnabled oldVisibleAndEnabled, LocationAccessComponentWrapper wrapper, boolean enabled) {
		UpdateLevel updateLevel = new UpdateLevel();
		if (oldVisibleAndEnabled.isEnabled() != enabled) {
			updateLevel.updateComponent = true;
			wrapper.getComponent().setEnabled(enabled);
		}
		return updateLevel;
	}

	public static UpdateLevel createByVisibleAndEnabled(VisibleAndEnabled oldVisibleAndEnabled, LocationAccessComponentWrapper wrapper, boolean visible, boolean enabled) {
		UpdateLevel updateLevel = new UpdateLevel();
		if (oldVisibleAndEnabled.isVisible() != visible) {
			updateLevel.updateParent = true;
			wrapper.getComponent().getParent().getParent().setVisible(visible);
		}
		if (oldVisibleAndEnabled.isEnabled() != enabled) {
			updateLevel.updateComponent = true;
			wrapper.getComponent().setEnabled(enabled);
		}
		return updateLevel;
	}

	public UpdateLevel setUpdateParent(boolean updateParent) {
		this.updateParent = updateParent;
		return this;
	}

	public UpdateLevel setUpdateComponent(boolean updateComponent) {
		this.updateComponent = updateComponent;
		return this;
	}
}
