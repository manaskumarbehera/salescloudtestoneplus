package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CountAndInstallation implements Serializable {
	private Integer countNew = 0;
	private Integer countExisting = 0;
	private boolean installationSelected;  // I.e. TDC installation - not partner installation
	private Integer subIndex;
}
