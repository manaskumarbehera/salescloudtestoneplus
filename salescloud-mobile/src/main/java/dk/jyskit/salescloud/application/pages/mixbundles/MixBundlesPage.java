package dk.jyskit.salescloud.application.pages.mixbundles;

import java.util.HashMap;
import java.util.Map;

import dk.jyskit.salescloud.application.model.*;
import lombok.Data;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
@Data
public class MixBundlesPage extends ContentPage {
	private MobileProductBundle selectedBundle;
	
	public MixBundlesPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_MIX_BUNDLES));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		if (MobileSession.get().getSelectedMixBundleId() == null) {
			setSelectedBundle((MobileProductBundle) MobileSession.get().getContract().getProductBundles().get(0));
			MobileSession.get().setSelectedMixBundle(MobileSession.get().getSelectedMixBundle());
		} else {
			setSelectedBundle(MobileSession.get().getSelectedMixBundle());
		}
		
		final Map<Long, Product> oldSpeechTimes = new HashMap<>();
		final Map<Long, Product> oldDataAmounts = new HashMap<>();
		
		for (ProductBundle mixBundle : MobileSession.get().getContract().getProductBundles()) {
			for (BundleProductRelation productRelation : mixBundle.getProducts()) {
				if ((productRelation.getProduct() != null) && ((MobileProduct) productRelation.getProduct()).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME)) {
					oldSpeechTimes.put(mixBundle.getId(), productRelation.getProduct());
				} 
				if ((productRelation.getProduct() != null) && ((MobileProduct) productRelation.getProduct()).isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT)) {
					oldDataAmounts.put(mixBundle.getId(), productRelation.getProduct());
				} 
			}
		}
		
		return new MixBundlesPanel(wicketId, getNotificationPanel(), new AbstractReadOnlyModel<MobileProductBundle>() {
			@Override
			public MobileProductBundle getObject() {
				return selectedBundle;
			}
		}, oldSpeechTimes, oldDataAmounts);
	}
}
