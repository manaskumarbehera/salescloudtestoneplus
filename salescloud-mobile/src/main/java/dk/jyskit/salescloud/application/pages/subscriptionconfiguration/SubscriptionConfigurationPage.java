package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class SubscriptionConfigurationPage extends ContentPage {
	@Inject
	private MobileContractDao contractDao;
	private EntityModel<MobileContract> contractModel = null;
	private boolean impl;

	public SubscriptionConfigurationPage(PageParameters parameters, boolean impl) {
		this(parameters);
		this.impl = impl;
	}
	
	public SubscriptionConfigurationPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_SUBSCRIPTION_CONFIGURATION));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		if (MobileSession.get().isExternalAccessMode()) {
			miscPanel.setVisible(false);
		}
	}
	
	private EntityModel<MobileContract> getContractModel() {
		if (contractModel == null) {
			contractModel = EntityModel.forEntity((MobileContract) CoreSession.get().getContract());
		}
		return contractModel;
	}
	
	@Override
	protected void handleParameters(PageParameters parameters) {
		String businessAreaId = parameters.get("businessAreaId").toString();
		if (!StringUtils.isEmpty(businessAreaId)) {
			MobileSession.get().setBusinessAreaEntityId(Long.valueOf(businessAreaId));
		}
		
		String contractEncryptedId = parameters.get("contract").toString();
		if (!StringUtils.isEmpty(contractEncryptedId)) {
			try {
				String contractDecryptedId = SimpleStringCipher.decrypt(contractEncryptedId);
				MobileContract contract = contractDao.findById(Long.valueOf(contractDecryptedId));
				
				MobileSession.get().setContract(contract);
				MobileSession.get().setBusinessArea(contract.getBusinessArea());
				MobileSession.get().setExternalAccessMode(true);
			} catch (Exception e) {
				error("Check venligst om URL'en er korrekt");
				log.error("", e);
			}
		}
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new SubscriptionConfigurationPanel(wicketId, getContractModel(), impl);
	}

	protected Panel getIntroPanel(String wicketId) {
		return new IntroPanelWithDownloadLinks(wicketId, pageInfo, getContractModel());
	}
	
	@Override
	protected boolean withHelp() {
		return false;
	}
}
