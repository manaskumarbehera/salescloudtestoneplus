package dk.jyskit.salescloud.application.extensions.editroles;

import java.io.Serializable;

import lombok.Data;
import dk.jyskit.salescloud.application.model.BusinessEntity;
import dk.jyskit.salescloud.application.model.MobileUserRoleType;

@Data
public class RoleEditBean implements Serializable {
	private MobileUserRoleType roleType;
	private BusinessEntity companyInfo = new BusinessEntity();
}
