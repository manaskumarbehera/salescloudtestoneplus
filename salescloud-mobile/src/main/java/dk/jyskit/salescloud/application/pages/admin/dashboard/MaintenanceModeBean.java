package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.io.Serializable;

import lombok.Data;

@Data
public class MaintenanceModeBean implements Serializable {
	private String text;
	private boolean active;
	private boolean warning;
}
