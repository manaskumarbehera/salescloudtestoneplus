package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WiFiBundleIds implements Serializable {
	private String address = "";
	private String contactName = "";
	private String contactPhone = "";
	
	private String lidId = "";
	
	private Long areaSizeEntityId;
	
	private Integer accessPointCount = 1;
	private Long accessPointEntityId;
	
	private Long serviceLevelEntityId;
	
	private Long switchEntityId;
	private Long siteSurveyEntityId;
	private String siteSurveyName;
	
	private Boolean newAccess;
	
	public boolean isValid() {
		return !StringUtils.isEmpty(address) && (areaSizeEntityId != null);
	}
}
