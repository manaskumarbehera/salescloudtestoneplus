package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dk.jyskit.salescloud.application.model.ContractLine;
import lombok.Data;

@Data
public class RollingPeriodBean implements Serializable {
	private String division = "";
	private String salespersonName = "";
	private int sortIndex;
	private List<ContractLine> contractLines = new ArrayList();
	private boolean total;
}
