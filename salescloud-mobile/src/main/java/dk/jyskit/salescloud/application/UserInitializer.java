package dk.jyskit.salescloud.application;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.dao.SalespersonRoleDao;
import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.model.AccessCodes;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserInitializer implements Initializer {

	@Inject private UserDao userDao;
	@Inject private RoleDao roleDao;
	@Inject private SystemUpdateDao systemUpdateDao;
	@Inject private OrganisationDao organisationDao;
	@Inject private SalespersonRoleDao salespersonRoleDao;
	
	@Override
	public void makeUpgrades() {
//		List<BaseUser> users = userDao.findAll();
//		for (BaseUser user : users) {
//			SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
//			if (role != null) {
//				if (role.getCompanyInfo().get)
//			}
//		}
		
		makeSystemUpdates();
	}

	private void makeSystemUpdates() {
		{
			String name = "Partnersalg"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, 0);
			if (update == null) {
				log.info("Update starting: '" + name + "' for businessarea 0");
				
				List<Organisation> organisations = organisationDao.findAll();
				if (organisations.size() == 0) {
					addOrganisation(organisations, OrganisationType.TDC, "TDC", "TDC", "Teglholmsgade 1", "0900", "København C", "70 70 90 90", "", 14773908, "", "");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "25050", "TDC Erhvervscenter Holbæk", "Ved Faurgården 3", 		"4300", "Holbæk", 		"59 45 06 60", "holbaek@tdcerhvervscenter.dk", 26379490,	"70 25 08 00",	"70250800@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "25099", "TDC Erhvervscenter Glostrup", "Naverland 1 A", 		"2610", "Glostrup", 	"70 26 30 00", "glostrup@tdcerhvervscenter.dk", 27089569, "70 26 30 00", "glostrup@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "26048", "TDC Erhvervscenter Slagelse", "Sorøvej 6", 			"4200", "Slagelse", 	"58 58 15 15", "slagelse@tdcerhvervscenter.dk", 27296734, "70 25 08 00", "70250800@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "26496", "TDC Erhvervscenter Herning", "Lollandsvej 4", 		"7400", "Herning", 		"70 10 67 11", "herning@tdcerhvervscenter.dk", 29827729, "70 10 67 11", "herning@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "26502", "TDC Erhvervscenter Esbjerg", "Sædding Strandvej 61", 	"7610", "Esbjerg V", 	"70 10 67 07", "esbjerg@tdcerhvervscenter.dk", 29827729, "70 25 75 00", "support@scalejylland.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "26506", "TDC Erhvervscenter Rønne", "Industrivej 1", 			"3700", "Rønne", 		"70 10 67 04", "ronne@tdcerhvervscenter.dk", 19064107, "56 95 85 15", "ronne@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "95154", "TDC Erhvervscenter Bagsværd", "Vadstrupvej 77", 		"2880", "Bagsværd", 	"44 44 05 45", "bagsvaerd@tdcerhvervscenter.dk", 30555120, "44 44 05 11", "bagsvaerd@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "95158", "TDC Erhvervscenter Roskilde", "Københavnsvej 130", 	"4000", "Roskilde", 	"44 25 10 20", "roskilde@tdcerhvervscenter.dk", 33354983, "70 25 08 00", "70250800@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "95160", "TDC Erhvervscenter Køge", "Unionsvej 10", 			"4600", "Køge", 		"56 65 70 00", "koege@tdcerhvervscenter.dk", 25773098, "70 25 08 00", "70250800@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "96313", "TDC Erhvervscenter Odense S", "Svendborgvej 39", 		"5260", "Odense S", 	"70 20 34 54", "odenses@tdcerhvervscenter.dk", 21667803, "70 20 34 54", "odenses@tdcerhvervscenter.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "96503", "TDC Erhvervscenter Aalborg SV", "Gøtheborgvej 6", 	"9200", "Aalborg SV", 	"70 10 67 13", "aalborgsv@tdcerhvervscenter.dk", 29827729, "70 25 75 00", "support@scalejylland.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "96510", "TDC Erhvervscenter Århus S", "Øllegårdsvej 3", 		"8260", "Viby J", 		"87 38 91 11", "aarhus@tdcerhvervscenter.dk", 29827729, "70 25 75 00", "support@scalejylland.dk");
					addOrganisation(organisations, OrganisationType.PARTNER_CENTER, "96511", "TDC Erhvervscenter Kolding", "Agtrupvej 2", 			"6000", "Kolding", 		"70 22 70 21", "kolding_c@tdcerhvervscenter.dk", 29827729, "70 25 75 00", "support@scalejylland.dk");
					Set<String> centerIds = new TreeSet<>();
					List<SalespersonRole> salespersons = salespersonRoleDao.findAll();
					for (SalespersonRole salespersonRole : salespersons) {
						centerIds.add("id: " + salespersonRole.getDivision());
						for(Organisation organisation: organisations) {
							if (organisation.getType().equals(OrganisationType.TDC)) {
								salespersonRole.setOrganisation(organisation);
							}
							if (!StringUtils.isEmpty(salespersonRole.getDivision())) {
								if (salespersonRole.getDivision().equals(organisation.getOrganisationId())) {
									salespersonRole.setOrganisation(organisation);
									break;
								}
							}
						}
						log.info(salespersonRole.getUser().getEmail() + " - " + salespersonRole.getOrganisation().getCompanyName());
						salespersonRoleDao.save(salespersonRole);
					}
					for (String centerId: centerIds) {
						log.info("Center ID: " + centerId);
					}
					
					Organisation org = organisationDao.findUniqueByField("organisationId", "25050");
					String[] admins = new String[] {"jan@escapetech.dk", "thber@tdc.dk", "whe@tdc.dk"};
					for (String admin : admins) {
						List<BaseUser> users = userDao.findByEmail(admin);
						for (BaseUser user : users) {
							SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
							if (salesperson != null) {
								salesperson.setOrganisation(org);
							}
							AdminRole adminRole = (AdminRole) user.getRole(AdminRole.class);
							if (adminRole == null) {
								user.addRole(new AdminRole());
							}
							userDao.save(user);
						}
					}
				}
				update = new SystemUpdate();
				update.setBusinessAreaId(0);
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		{
			String name = "Firma telefon til privat telefon"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, 0);
			if (update == null) {
				log.info("Update starting: '" + name + "' for businessarea 0");
				
				List<SalespersonRole> salespersons = salespersonRoleDao.findAll();
				for (SalespersonRole salespersonRole : salespersons) {
					boolean dirty = false;
					if (salespersonRole.getOrganisation() != null) {
						if (StringUtils.isEmpty(salespersonRole.getUser().getSmsPhone())) {
							if (StringUtils.isEmpty(salespersonRole.getOrganisation().getPhone())) {
								log.info("No phone no for user: " + salespersonRole.getUser().getEmail());
							} else {
								salespersonRole.getUser().setSmsPhone(salespersonRole.getOrganisation().getPhone());
								dirty = true;
							}
						}
					}
					if (StringUtils.isEmpty(salespersonRole.getUser().getFirstName())) {
						if (StringUtils.isEmpty(salespersonRole.getCompanyInfo().getName())) {
							log.info("No name for user: " + salespersonRole.getUser().getEmail());
						} else {
							salespersonRole.getUser().setFirstName(salespersonRole.getCompanyInfo().getName());
							salespersonRole.getUser().setLastName("");
							dirty = true;
						}
					} else if (!StringUtils.isEmpty(salespersonRole.getUser().getLastName())) {
						if (!StringUtils.isEmpty(salespersonRole.getCompanyInfo().getName())) {
							salespersonRole.getUser().setFirstName(salespersonRole.getCompanyInfo().getName());
							salespersonRole.getUser().setLastName("");
							dirty = true;
						}
					}  
					if (dirty) {
						salespersonRoleDao.save(salespersonRole);
					}
				}
				update = new SystemUpdate();
				update.setBusinessAreaId(0);
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		{
			String name = "CVR nr."; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, 0);
			if (update == null) {
				log.info("Update starting: '" + name + "' for businessarea 0");

				List<SalespersonRole> salespersons = salespersonRoleDao.findAll();
				for (SalespersonRole salespersonRole : salespersons) {
					if (salespersonRole.getCompanyInfo() != null && "14773908".equals(salespersonRole.getCompanyInfo().getCompanyId())) {
						salespersonRole.getCompanyInfo().setCompanyId("40075291");
						salespersonRoleDao.save(salespersonRole);
					}
				}
				update = new SystemUpdate();
				update.setBusinessAreaId(0);
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		{
			String name = "Supportmail for EC Jylland"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, 0);
			if (update == null) {
				log.info("Update starting: '" + name + "' for businessarea 0");

				for (Organisation organisation : organisationDao.findAll()) {
					boolean dirty = false;
					if ("support@scalejylland.dk".equals(organisation.getSupportEmail())) {
						organisation.setSupportEmail("support@tdcerhvervscenter.dk");
						organisationDao.save(organisation);
					}
				}
				update = new SystemUpdate();
				update.setBusinessAreaId(0);
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
	}


	private void addOrganisation(List<Organisation> organisations, OrganisationType type, String organisationId, String name, String address, String zipCode, String city, String phone, String email, int cvr, String supportPhone, String supportEmail) {
		Organisation organisation = new Organisation();
		organisation.setType(type);
		organisation.setCompanyName(name);
		organisation.setOrganisationId(organisationId);
		organisation.setAddress(address);
		organisation.setZipCode(zipCode);
		organisation.setCity(city);
		organisation.setCompanyId("" + cvr);
		organisation.setSupportPhone(supportPhone);
		organisation.setSupportEmail(supportEmail);
		organisation.setPhone(phone);
		organisation.setEmail(email);
		organisationDao.save(organisation);
		organisations.add(organisation);
	}
	
	@Override
	public boolean needsInitialization() {
		return (userDao.findAll().size() == 0);
	}
	
	@Override
	public void initialize() {
		{
			BaseUser user = new BaseUser();
			user.setUsername("uadmin");
			user.setPassword("pw");
			user.setFirstName("Søren");
			user.setLastName("Sørensen");
			user.setIdentity("124");
			
			user.addRole(new UserManagerRole());
			
			userDao.save(user);
		}
		
		{
			for (int i = 1; i <= 2; i++) {
				BaseUser user = new BaseUser();
				user.setUsername("partner" + i);
				user.setPassword("pw");
				user.setFirstName("Anders");
				user.setLastName("Andersen");
				user.setIdentity("123");
				user.setEmail("anders.andersen@tdc.dk");
				
				SalespersonRole salespersonRole = new SalespersonRole();
				salespersonRole.getCompanyInfo().setName(user.getFirstName() + " " + user.getLastName());
				salespersonRole.getCompanyInfo().setCompanyId("14773908");
				salespersonRole.getCompanyInfo().setCompanyName("TDC Erhverv A/S");
				salespersonRole.getCompanyInfo().setAddress("Teglholmsgade 1-3");
				salespersonRole.getCompanyInfo().setZipCode("2450");
				salespersonRole.getCompanyInfo().setCity("København SV");
				salespersonRole.getCompanyInfo().setPhone("80808080");
				salespersonRole.setDivision("25050");
				salespersonRole.getCompanyInfo().setEmail(user.getEmail());
				
				salespersonRole.setPartner(true);
//				salespersonRole.setAccessCodes(AccessCodes.FIBER_CONFIGURATOR + ", " + AccessCodes.WIFI_CONFIGURATOR + ", " + AccessCodes.TEM5_CONFIGURATOR);
				salespersonRole.setAccessCodes(AccessCodes.WIFI_CONFIGURATOR);
				 
				salespersonRole.getContractCategories().add(new ContractCategory("Jylland", salespersonRole));
				
				user.addRole(salespersonRole);
				
				if (Application.get().usesDevelopmentConfig() && (i < 2)) {
					user.addRole(new AdminRole());
					
					SalesmanagerRole salesmanagerRole = new SalesmanagerRole();
					salesmanagerRole.setDivisions("*");
					user.addRole(salesmanagerRole);
					
					user.addRole(new UserManagerRole());
				}
				
				userDao.save(user);
			}
		}
		
		addUser("#frn@tdc.dk","Passiv","Frank Riege","Nissen","frn@tdc.dk","12345678","TDC Erhverv A/S", "111", "a12345","agent","AO4350","Nye sager","Lukkede sager","Henlagte sager");
		addUser("elkj@tdc.dk","a54223","Elizabeth","Kjeldsen","elkj@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("nda@tdc.dk","m29838","Nicolai Dam","Hansen","nda@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("aldi@tdc.dk","m22219","Alan","Dimke","aldi@tdc.dk","51513555","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("lina@tdc.dk","a13403","Linda","Andersson","lina@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("mafo@tdc.dk","a10077","Martin","Foged","mafo@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("vp@tdc.dk","a13881","Vibeke","Petersen","vp@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("#mstr@tdc.dk","Slettet","Mia","Stroustrup","mstr@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("niric@tdc.dk","m20375","Ninet","Richardt","niric@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("lshns@tdc.dk","a59179","Lis","Hansen","lshns@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("bkst@tdc.dk","a71821","Britta Korre","Stenholt","bkst@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("gibli@tdc.dk","a54921","Gitte","Blixt","gibli@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("tg@tdc.dk","a60652","Tina","Riemann","tg@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("mewe@tdc.dk","a70383","Mette","Wenneberg","mewe@tdc.dk","","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("gat@tdc.dk","a69881","Gitte","Auning","gat@tdc.dk","21476026","TDC Erhverv A/S", "111", "","agent","","","","");
		addUser("test@tdc.dk","pw","John","Doe","test@tdc.dk","21476026","TDC Erhverv A/S", "26506", "","agent","","","","");
		addUser("janjysk","Devguy","John","Doe","jan@escapetech.dk","21476026","TDC Erhverv A/S", "26506", "","agent","","","","");
	}
	
	private BaseUser addUser(String userName, String pw, String firstName, String lastName, String email, String phone, String companyName,
			String division, String identity, String roles, String unit, String cat1, String cat2, String cat3) {
		BaseUser user = new BaseUser();
		user.setUsername(userName);
		user.setPassword(pw);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setIdentity(identity);
		user.setEmail(email);

		SalespersonRole partnerRole = new SalespersonRole();
		partnerRole.getCompanyInfo().setName(user.getFirstName() + " " + user.getLastName());
		partnerRole.getCompanyInfo().setCompanyId("14773908");
		partnerRole.getCompanyInfo().setCompanyName("TDC Erhverv A/S");
		partnerRole.getCompanyInfo().setAddress("Teglholmsgade 1-3");
		partnerRole.getCompanyInfo().setZipCode("2450");
		partnerRole.getCompanyInfo().setCity("København SV");
		partnerRole.getCompanyInfo().setPhone("80808080");
		partnerRole.getCompanyInfo().setEmail(user.getEmail());
		partnerRole.setDivision(division);

		if (!StringUtils.isEmpty(cat1)) {
			partnerRole.getContractCategories().add(new ContractCategory(cat1, partnerRole));
		}
		if (!StringUtils.isEmpty(cat2)) {
			partnerRole.getContractCategories().add(new ContractCategory(cat2, partnerRole));
		}
		if (!StringUtils.isEmpty(cat3)) {
			partnerRole.getContractCategories().add(new ContractCategory(cat3, partnerRole));
		}
		
		user.addRole(partnerRole);
		
		userDao.save(user);
		roleDao.save(partnerRole);
		return user;
	}

	@Override
	public String getName() {
		return "User initializer";
	}

	private void upgradeUser(String email, String division, String action) {
		List<BaseUser> users = userDao.findByField("email", email);
		if (users.size() == 0) {
			log.info("no users: " + email);
		} else {
			if (users.size() > 1) {
				log.info("more than 1 user: " + email);
			}
			for (BaseUser user : users) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if (role == null) {
					log.info("not a salesperson: " + email);
				} else {
					role.setDivision(division);
					if ("agent".equalsIgnoreCase(action)) {
						role.setAgent(true);
						role.setAgent_sa(false);
						role.setAgent_mb(false);
						role.setAgent_lb(false);
						role.setPartner(false);
						role.setPartner_ec(false);
						log.info("is agent: " + email);
						userDao.save(user);
					} else if ("agent_sa".equalsIgnoreCase(action)) {
						role.setAgent(false);
						role.setAgent_sa(true);
						role.setAgent_mb(false);
						role.setAgent_lb(false);
						role.setPartner(false);
						role.setPartner_ec(false);
						log.info("is agent_sa: " + email);
						userDao.save(user);
					} else if ("agent_mb".equalsIgnoreCase(action)) {
						role.setAgent(false);
						role.setAgent_sa(false);
						role.setAgent_mb(true);
						role.setAgent_lb(false);
						role.setPartner(false);
						role.setPartner_ec(false);
						log.info("is agent_mb: " + email);
						userDao.save(user);
					} else if ("agent_lb".equalsIgnoreCase(action)) {
						role.setAgent(false);
						role.setAgent_sa(false);
						role.setAgent_mb(false);
						role.setAgent_lb(true);
						role.setPartner(false);
						role.setPartner_ec(false);
						log.info("is agent_lb: " + email);
						userDao.save(user);
					} else if ("partner".equalsIgnoreCase(action)) {
						role.setAgent(false);
						role.setAgent_sa(false);
						role.setAgent_mb(false);
						role.setAgent_lb(false);
						role.setPartner(true);
						role.setPartner_ec(false);
						log.info("is partner: " + email);
						userDao.save(user);
					} else if ("partner_ec".equalsIgnoreCase(action)) {
						role.setAgent(false);
						role.setAgent_sa(false);
						role.setAgent_mb(false);
						role.setAgent_lb(false);
						role.setPartner(false);
						role.setPartner_ec(true);
						log.info("is partner_ec: " + email);
						userDao.save(user);
					} else if ("delete".equalsIgnoreCase(action)) {
						log.info("deleting: " + email);
						userDao.delete(user);
					}
				}
			}
		}
	}

	private void deleteUser(String username) {
		changePassword(username, "Slettet");
	}

	private void changePassword(String username, String pw) {
		List<BaseUser> users = userDao.findByField("username", username);
		if (users.size() == 0) {
			log.info("no users: " + username);
		} else {
			if (users.size() > 1) {
				log.info("more than 1 user: " + username);
			} else {
				users.get(0).setPassword(pw);
				userDao.save(users.get(0));
			}
		}
	}
	
}
