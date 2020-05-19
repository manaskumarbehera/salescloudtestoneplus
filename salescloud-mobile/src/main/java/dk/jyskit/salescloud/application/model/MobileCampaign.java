package dk.jyskit.salescloud.application.model;

import javax.annotation.Nonnull;
import javax.persistence.*;

import dk.jyskit.salescloud.application.dao.MobileCampaignDao;
import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Slf4j
public class MobileCampaign extends Campaign {
	
	@Column(length=2000)
	private String campaignBundleHelpText;
	
	@Column(length=2000)
	private String productSelectionHelpText;
	
	@Column(length=2000)
	private String switchboardHelpText;
	
	@Column(length=2000)
	private String switchboardAddonHelpText;
	
	@Column(length=2000)
	private String offerText;
	
	private boolean allowMixBundles = false;
	
	@Column(length=50)
	private String cdmCode;		// Bruges fremadrettet til Kampagnekode (omstilling/wifi) - Kaldes "TM Kode"

	@Column(length=2000)
	private String prisaftaleTextMatrix;

	@Column(length=1000)
	private String prisaftaleTextMatrixNetwork;

	@Column(length=1000)
	private String prisaftaleTextMatrixPool;

	private boolean disableContractDiscount = false;
	
	// --------------------------------
	
	/**
	 * @param level (zero-based)
	 * @param year (not zero-based)
	 * @return
	 */
	public String getPrisaftale(int level, int year) {
		if (year == 0) {
			return "";
		}
		if (!StringUtils.isEmpty(prisaftaleTextMatrix)) {
			String[] rows = prisaftaleTextMatrix.split("#");
			String[] cells = rows[year-1].split(",");
			return cells[level].trim();
		}
		return null;
	}

	/**
	 * @param level (zero-based)
	 * @param year (not zero-based)
	 * @return
	 */
	public String getPrisaftaleNetwork(int level, int year) {
		if (year == 0) {
			return "";
		}
		if (!StringUtils.isEmpty(prisaftaleTextMatrixNetwork)) {
			String[] rows = prisaftaleTextMatrixNetwork.split("#");
			String[] cells = rows[year-1].split(",");
			return cells[level].trim();
		}
		return null;
	}

	/**
	 * @param level (zero-based)
	 * @param year (not zero-based)
	 * @return
	 */
	public String getPrisaftalePool(int level, int year) {
		if (year == 0) {
			return "";
		}
		if (!StringUtils.isEmpty(prisaftaleTextMatrixPool)) {
			String[] rows = prisaftaleTextMatrixPool.split("#");
			String[] cells = rows[year-1].split(",");
			return cells[level].trim();
		}
		return null;
	}

	// --------------------------------

	public MobileCampaign clone(BusinessArea targetBusinessArea) {
		MobileCampaign mc = new MobileCampaign();
		Lookup.lookup(MobileCampaignDao.class).save(mc);

		mc.setEntityState(getEntityState());
		mc.setState(getState());
		mc.setName(getName());
		mc.setBusinessArea(targetBusinessArea);
		mc.setGksValidation(isGksValidation());

		for (ProductBundle bundle: getProductBundles()) {
			log.info("Cloning bundle " + bundle.getPublicName());
			MobileProductBundle newProductBundle = ((MobileProductBundle) bundle).clone(targetBusinessArea);
			mc.addProductBundle(newProductBundle);
		}

		for (CampaignProductRelation cpr: getCampaignProducts()) {
			mc.addCampaignProductRelation(cpr.clone());
		}

		mc.setFromDate(getFromDate());
		mc.setToDate(getToDate());
		mc.setExtensionFromDate(getExtensionFromDate());
		mc.setExtensionToDate(getExtensionToDate());
		mc.setProductId(getProductId());
		mc.setFilter(getFilter());

		return mc;
	}

	// --------------------------------

	@Override
	public String toString() {
		return getName();
	}
}
