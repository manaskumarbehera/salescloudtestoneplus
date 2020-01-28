package dk.jyskit.salescloud.application.pages.admin.useradmin.users;

import java.io.Serializable;

import javax.annotation.Nonnull;

import lombok.Data;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.model.BaseUser;

@SuppressWarnings("serial")
@Data
public class BaseUserWrapper implements Serializable {

	public static final String DUMMY_PASSWORD = "¤Dummy¤¤PassWord¤";

	@Nonnull
	private BaseUser user;

	@Nonnull
	private String password = DUMMY_PASSWORD;

	private boolean isAdmin;
	private boolean isSalesmanager;
	private boolean isSalesperson;
	private boolean isUserManager;

	public BaseUserWrapper(BaseUser user) {
		this.user = user;
		currentUserPolicy();
	}

	public void currentUserPolicy() {
		isAdmin 		= user.hasRole(AdminRole.class);
		isSalesmanager	= user.hasRole(SalesmanagerRole.class);
		isSalesperson	= user.hasRole(SalespersonRole.class);
		isUserManager	= user.hasRole(UserManagerRole.class);
	}

}
