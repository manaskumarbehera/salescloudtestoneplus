package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.application.pages.SEOInfo;
import dk.jyskit.waf.application.pages.nonadmin.base.JITNonAdminBasePage;
import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import dk.jyskit.waf.wicket.components.panels.modal.ModalContainer;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
public class ExternalSubscriptionConfigurationPage extends JITNonAdminBasePage {
	@Inject
	private MobileContractDao contractDao;
	private EntityModel<MobileContract> contractModel = null;
	private boolean impl;
	protected ModalContainer modalContainer;

	public ExternalSubscriptionConfigurationPage(PageParameters parameters, boolean impl) {
		this(parameters);
		this.impl = impl;
	}
	
	public ExternalSubscriptionConfigurationPage(PageParameters parameters) {
		handleParameters(parameters);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		PageInfo pageInfo = new PageInfo();
		if (impl) {
			pageInfo.setIntroHtml("<h2>Konfigurering af licenser vedr. " + 
					(getContractModel().getObject().getCustomer().getCompanyName() == null ? "*ukendt kundenavn*" : getContractModel().getObject().getCustomer().getCompanyName()) + "</h2>");
		} else {
			pageInfo.setIntroHtml("<h2>Konfigurering af licenser</h2>");
		}
		
		add(new IntroPanelWithDownloadLinks("buttonPanel", pageInfo, getContractModel()));
		add(new SubscriptionConfigurationPanel("mainPanel", getContractModel(), impl));

		modalContainer = new ModalContainer("modalContainer");
		add(modalContainer);
	}
	
	private EntityModel<MobileContract> getContractModel() {
		if (contractModel == null) {
			contractModel = EntityModel.forEntity((MobileContract) CoreSession.get().getContract());
		}
		return contractModel;
	}
	
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
	protected boolean hasCss() {
		return false;
	}

	@Override
	protected SEOInfo getSEOInfo() {
		return new SEOInfo("", "", "");
	}

	@Override
	protected String getAuthor() {
		return "";
	}
}
