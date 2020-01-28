package dk.jyskit.salescloud.application.extensions;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MobileCanOrderFilter implements CanOrderFilter {
	@Override
	public boolean accept(Product product) {
		MobileProduct p = (MobileProduct) product;
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (!StringUtils.isEmpty(p.getFilter())) {
				MobileContract contract = MobileSession.get().getContract();
				String[] productFilterDefinitions = p.getFilter().split(",");		// eg V1_P,V1_I,V2_P,V2_I,V3_P,V3_I

				String currentContractFilter = "";

				// M, V1, V2 or V3
				List<OrderLine> virksomhedspakkeOrderLines = MobileSession.get().getContract().getBundleOrderLines();
				if (virksomhedspakkeOrderLines.size() > 0) {
					for (OrderLine orderLine : virksomhedspakkeOrderLines) {
						if ((orderLine.getTotalCount() > 0) && (orderLine.getBundle() != null)) {
							MobileProductBundle b = (MobileProductBundle) orderLine.getBundle();
							for (BundleProductRelation prod : b.getProducts()) {
								if (!StringUtils.isEmpty(((MobileProduct) prod.getProduct()).getFilterID())) {
									currentContractFilter = ((MobileProduct) prod.getProduct()).getFilterID();
									break;
								}
							}
						}
					}
				}

				// P or I
				currentContractFilter += (StringUtils.isEmpty(currentContractFilter) ? "" : "_") + (contract.isPoolsMode() ? "P" : "I");

				String[] rulesForProduct = p.getFilter().split(",");
				return hasAnyOfRules(rulesForProduct, currentContractFilter);
			}
		}
		return true;
	}

	@Override
	public boolean accept(ProductBundle bundle) {
		for (BundleProductRelation pr: bundle.getProducts()) {
			if (!ProductAccessType.SEPARATE_COUNT.equals(pr.getProductAccessType())) {
				Product p = pr.getProduct();
				if (p != null) {
					if (!accept(p)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean hasAnyOfRules(String[] rules, String ... rulesToFind) {
		for (String ruleToFind: rulesToFind) {
			for (String rule: rules) {
				String[] modifiedRules;
				if (rule.indexOf("_") > -1) {
					modifiedRules = new String[] {rule.trim(), StringUtils.reverseDelimited(rule.trim(), '_')};
				} else {
					modifiedRules = new String[] {rule.trim()};
				}
				for (String modifiedRule: modifiedRules) {
					if (StringUtils.equalsIgnoreCase(ruleToFind.trim(), modifiedRule)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
