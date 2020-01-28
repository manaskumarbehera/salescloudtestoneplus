package dk.jyskit.salescloud.application.extensions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Provider;

import dk.jyskit.salescloud.application.links.spreadsheets.Spreadsheet;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.guice.Lookup;

@Slf4j
public class UsersSpreadsheet implements Provider<Workbook>, Serializable{
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM yyyy");

	@Override
	public Workbook get() {
		Spreadsheet s = new Spreadsheet("Brugere");
		s.incRow(0);
		
		List<Col> cols = getCols();
		
	    addHeaderRow(s, IndexedColors.DARK_BLUE, cols);

	    for (BaseUser user : Lookup.lookup(UserDao.class).findAll()) {
	    	addRow(s, null, user, cols);
	    }
		return s.getWorkbook();
	}

	private void addRow(Spreadsheet s, IndexedColors color, BaseUser user, List<Col> cols) {
	    for (Col col : cols) {
	    	if (color == null) {
			    s.addValue(col.getValue(user));
	    	} else {
			    s.addValueAndColor(col.getValue(user), color);
	    	}
		}
	    s.incRow();
	}

	private void addHeaderRow(Spreadsheet s, IndexedColors color, List<Col> cols) {
	    for (Col col : cols) {
	    	if (color == null) {
			    s.addValue(col.getHeader());
	    	} else {
	    		s.addColoredValue(col.getHeader(), color);
	    	}
		}
	    s.incRow();
	}

	abstract class Col {
		public String header;

		String getHeader() {
			return header;
		}
		abstract Object getValue(BaseUser user);
		
		public Col(String header) {
			this.header = header;
		}
	}
	
	private List<Col> getCols() {
		List<Col> cols = new ArrayList<>(50);
		cols.add(new Col("Oprettet") {
			Object getValue(BaseUser user) {
				if (user.getCreationDate() == null) {
					return "";
				} else {
					return dateFormat.format(user.getCreationDate());
				}
			}
		});
		cols.add(new Col("Administrator") {
			Object getValue(BaseUser user) {
				if (user.hasRole(AdminRole.class)) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Bruger admininistrator") {
			Object getValue(BaseUser user) {
				if (user.hasRole(UserManagerRole.class)) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Sales manager") {
			Object getValue(BaseUser user) {
				if (user.hasRole(SalesmanagerRole.class)) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Sælger") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if (role != null) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Afdeling") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if (role != null) {
					return role.getDivision();
				} else {
					return "-";
				}
			}
		});
		cols.add(new Col("Agent") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isAgent())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Agent SA") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isAgent_sa())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Agent MB") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isAgent_mb())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Agent LB") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isAgent_lb())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Partner") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isPartner())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Partner EC") {
			Object getValue(BaseUser user) {
				SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
				if ((role != null) && (role.isPartner_ec())) {
					return "Ja";
				} else {
					return "Nej";
				}
			}
		});
		cols.add(new Col("Fornavn") {
			Object getValue(BaseUser user) {
				return user.getFirstName();
			}
		});
		cols.add(new Col("Efternavn") {
			Object getValue(BaseUser user) {
				return user.getLastName();
			}
		});
		cols.add(new Col("Email") {
			Object getValue(BaseUser user) {
				return user.getEmail();
			}
		});
		return cols;
	}

	
}
