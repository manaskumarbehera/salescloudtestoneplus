package dk.jyskit.salescloud.application;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.JITApplicationInitializer;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;

public class TestInitializer implements JITApplicationInitializer {

	@Inject private UserDao userDao;
	
	@Override
	public void init() {
		{
			BaseUser user = new BaseUser();
			user.setUsername("testadmin");
			user.setPassword("pw");
			user.setFirstName("Søren");
			user.setLastName("Sørensen");
			user.setIdentity("123");
			
			user.addRole(new AdminRole());
			
			userDao.save(user);
		}
		
		{
			BaseUser user = new BaseUser();
			user.setUsername("testpartner");
			user.setPassword("pw");
			user.setFirstName("Anders1");
			user.setLastName("Andersen");
			user.setIdentity("123");
			
			SalespersonRole partnerRole = new SalespersonRole();
			ContractCategory contractCategory = new ContractCategory();
			contractCategory.setName("Jylland");
			partnerRole.getContractCategories().add(contractCategory);
			user.addRole(partnerRole);
			
			userDao.save(user);
		}
	}
}
