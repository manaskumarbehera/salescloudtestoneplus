package dk.jyskit.salescloud.application.pages.productselection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.components.campaigns.CampaignInfoPanel;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class ProductSelectionPage extends ContentPage {
	@Inject
	private ProductGroupDao productGroupDao;
	
	private List<Long> productGroupIds = new ArrayList<Long>();
	private Long selectedProductGroupId;
	
	public ProductSelectionPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_PRODUCT_SELECTION));
		
		for (final ProductGroup productGroup : CoreSession.get().getBusinessArea().getProductGroups()) {
			if (CoreSession.get().getBusinessArea().hasFeature(FeatureType.TEM5_PRODUCTS)) {
				if (MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON.getKey().equals(productGroup.getUniqueName())) {
					selectedProductGroupId = productGroup.getChildProductGroups().get(0).getId();
					break;
				}
			} else if (CoreSession.get().getBusinessArea().hasFeature(FeatureType.TDC_OFFICE)) {
				if (MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey().equals(productGroup.getUniqueName())) {
					selectedProductGroupId = productGroup.getChildProductGroups().get(0).getId();
					break;
				}
			} else {
				if (MobileProductGroupEnum.PRODUCT_GROUP_ADDON.getKey().equals(productGroup.getUniqueName())) {
					selectedProductGroupId = productGroup.getChildProductGroups().get(0).getId();
					break;
				}
			}
		}
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new ConfiguratorTabPanel(wicketId, productGroupIds);
	}

	@Override
	protected HelpPanel getHelpPanel(String wicketId) {
		HelpPanel panel = new HelpPanel(wicketId, new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				return productGroupDao.findById(selectedProductGroupId).getHelpHtml();
			}
		}) {
			protected Component getPreContentPanel(String wicketId) {
				String campaignHelp = ((MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0)).getProductSelectionHelpText();
				if (StringUtils.isEmpty(campaignHelp)) {
					return new EmptyPanel(wicketId);
				}
				campaignHelp = Processor.process(campaignHelp);
				return new CampaignInfoPanel(wicketId, new Model<String>(campaignHelp));
			}
		};
		panel.setOutputMarkupId(true);
		return panel;
	}

	public void updateHelp(AjaxRequestTarget target, Long productGroupId) {
		selectedProductGroupId = productGroupId;
		target.add(getHelpPanel());
	}
}
