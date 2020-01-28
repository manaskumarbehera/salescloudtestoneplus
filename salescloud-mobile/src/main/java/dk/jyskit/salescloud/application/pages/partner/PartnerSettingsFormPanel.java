package dk.jyskit.salescloud.application.pages.partner;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

import com.google.inject.Inject;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.SegmentDao;
import dk.jyskit.salescloud.application.utils.MapUtils;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import dk.jyskit.waf.wicket.components.spreadsheets.SpreadsheetLink;
import lombok.extern.slf4j.Slf4j;

import static dk.jyskit.salescloud.application.model.MobileProductBundleEnum.HARDWARE_BUNDLE;

@Slf4j
public class PartnerSettingsFormPanel extends Panel {
	static final int TAB_INDSTILLINGER 	= 0;
//	private static final int TAB_ØVRIG_INST 	= 1;
	static final int TAB_HARDWARE_TIL_RATE = 1;
	static final int TAB_OPGØRELSE = 2;

	@Inject private SegmentDao segmentDao;
	private Label provisionTotal = null;
	private SpreadsheetLink spreadsheetLink = null;
	private Label totalTilRateLabel;
	private Label totalInstallationLabel;

	public PartnerSettingsFormPanel(String id, final PartnerSettingsTabPanel partnerSettingsTabPanel, int index) {
		super(id);
		Map<String, String> labelMap = new HashMap<>();
		
		final MobileContract contract = MobileSession.get().getContract();
		
		if (index == TAB_INDSTILLINGER) {
			// ------------------------
			// Tab 0 - Indstillinger
			
			Jsr303Form<Tab0Bean> form = new Jsr303Form<>("form", partnerSettingsTabPanel.tab0, true);
			add(form);
			form.setLabelSpans(SmallSpanType.SPAN8);
			form.setEditorSpans(SmallSpanType.SPAN4);
			form.setLabelStrategy(new MapLabelStrategy(labelMap, form.getLabelStrategy()));
			
			List<String> paymentOptions = Arrays.<String>asList("PBS", "Email");
			IModel<List<? extends String>> paymentOptionsListModel = Model.ofList(paymentOptions);
			
			List<Segment> segments = ((SegmentDao) Lookup.lookup(SegmentDao.class)).findAll();
			Collections.sort(segments, new Comparator<Segment>() {
				@Override
				public int compare(Segment o1, Segment o2) {
					return Integer.valueOf(o1.getCsvIndex()).compareTo(Integer.valueOf(o2.getCsvIndex()));
				}
			});
			
			partnerSettingsTabPanel.tab0.setSupportNoOfUsers(contract.getNoOfUsers(false));
			partnerSettingsTabPanel.tab0.setSupportPricePrUser(contract.getSupportPricePrUser() / 100);
			partnerSettingsTabPanel.tab0.setSupportRecurringFee(contract.getSupportRecurringFee() / 100);
			partnerSettingsTabPanel.tab0.setSupportMonths(contract.getSupportMonths());
//			partnerSettingsTabPanel.tab0.setRateAgreement(contract.getRateNonRecurringFee() > 0);
			partnerSettingsTabPanel.tab0.setRateNonRecurringFee(contract.getRateNonRecurringFee() / 100);
			partnerSettingsTabPanel.tab0.setRateMonths(contract.getRateMonths());
			partnerSettingsTabPanel.tab0.setUpFrontPayment(contract.getUpFrontPayment() > 0);
			partnerSettingsTabPanel.tab0.setPayment(contract.isPbs() ? paymentOptions.get(0) : paymentOptions.get(1));
			partnerSettingsTabPanel.tab0.setSegment(contract.getSegment());
			
			labelMap.put("supportNoOfUsers", "Antal brugere support aftale");
			labelMap.put("supportPricePrUser", "Pris pr. bruger");
			labelMap.put("supportRecurringFee", "Grundpris for support aftale");
//			labelMap.put("installationFeeDiscount", "Rabat vedr. installation");
//			labelMap.put("oneTimeFeeDiscount", "Rabat vedr. oprettelse");
			labelMap.put("supportMonths", "Løbetid i måneder - supportaftale");
//			labelMap.put("rateAgreement", "Etablering af rate aftale");
//			labelMap.put("rateNonRecurringFee", "Etablering af rate aftale");
//			labelMap.put("rateMonths", "Løbetid i måneder - rate");
			labelMap.put("upFrontPayment", "Forudbetaling");
			labelMap.put("payment", "Fakturering");
			if (!contract.getBusinessArea().isOnePlus()) {
				labelMap.put("segment", "Segment");
			}

			List<Integer> monthOptions = Arrays.<Integer>asList(12, 24, 36);
			IModel<List<? extends Integer>> monthOptionsModel = Model.ofList(monthOptions);

			form.addReadonly("supportNoOfUsers");
			form.addNumberTextField("supportPricePrUser");
			form.addNumberTextField("supportRecurringFee");

//			form.addNumberTextField("installationFeeDiscount");
//			form.addNumberTextField("oneTimeFeeDiscount");

//			Select2Choice supportMonthsField = form.addSelect2Choice("supportMonths", new IntegerListChoiceProvider(monthOptionsModel));
//			supportMonthsField.getSettings().setMinimumResultsForSearch(-1);  // Search field unnecessary

//			form.addCheckBox("rateAgreement");
//			form.addNumberTextField("rateNonRecurringFee");
			
//			Select2Choice rateMonthsField = form.addSelect2Choice("rateMonths", new IntegerListChoiceProvider(monthOptionsModel));
//			rateMonthsField.getSettings().setMinimumResultsForSearch(-1);  // Search field unnecessary



//			Select2Choice supportMonthsField = form.addSelect2Choice("supportMonths", new IntegerListChoiceProvider(monthOptionsModel));
//			supportMonthsField.getSettings().setMinimumResultsForSearch(-1);  // Search field unnecessary




			form.addCheckBox("upFrontPayment");
//			form.addNumberTextField("upFrontPayment");
			
//			IModel<? extends List<String>> paymentOptionsModel = (IModel<? extends List<String>>) Model.ofList(paymentOptions);
			Select2Choice paymentField = form.addSelect2Choice("payment", new StringListChoiceProvider(paymentOptionsListModel));
			paymentField.getSettings().setMinimumResultsForSearch(-1);  // Search field unnecessary

			if (!contract.getBusinessArea().isOnePlus()) {
				List<Segment> segmentOptions = ((SegmentDao) Lookup.lookup(SegmentDao.class)).findAll();
				Select2Choice segmentsField = form.addSelect2Choice("segment", new SegmentsProvider(segmentOptions));
				segmentsField.getSettings().setMinimumResultsForSearch(-1);  // Search field unnecessary

				// Only one segment
				segmentsField.setVisible(false);
				partnerSettingsTabPanel.tab0.setSegment(segmentOptions.get(0));
			}
		}
		
//		if (index == TAB_ØVRIG_INST) {
//			// ------------------------
//			// Tab 1 - Øvrige installationsydelser
//
//			Jsr303Form<ValueMap> form = new Jsr303Form<>("form", partnerSettingsTabPanel.valueMap1, true);
//			add(form);
//			form.setLabelSpans(SmallSpanType.SPAN7);
//			form.setEditorSpans(SmallSpanType.SPAN5);
//			form.setLabelStrategy(new MapLabelStrategy(labelMap, form.getLabelStrategy()));
//
//			contract.initVariableValueMaps();
//
//			for (Product product: contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION.getKey()).getProducts()) {
//				if (!((MobileProduct) product).isExcludeFromConfigurator()) {
//					List<OrderLine> orderLines = contract.getOrderLines(product);
//					Integer count = 0;
//					if (orderLines.size() > 0) {
//						count = orderLines.get(0).getTotalCount();
//					}
//					if (((MobileProduct) product).isVariableInstallationFee()) {
//						labelMap.put(product.getProductId(), product.getPublicName());
//						Amounts amounts = (((MobileProduct) product).getActualPrice());
//
//						// Discounts are negative!
//						int variableInstallationFee = (int) Math.abs(amounts.getInstallationFee()) / 100;
//
//						if (product.getPublicName().contains("Diverse")) {
//							String productName 	= MapUtils.stringToLongStringMap(contract.getVariableProductNames()).get(product.getId());
//							Integer price = MapUtils.stringToLongIntMap(contract.getVariableInstallationFees()).get(product.getId());
//							partnerSettingsTabPanel.valueMap1.put(product.getProductId(), new ProductNameAndPriceRow(product, productName, price));
//							form.addCustomComponent(new ProductNameAndPriceRowPanel(form, product.getProductId()));
//						} else {
//							form.addNumberTextField(product.getProductId());
//						}
//						partnerSettingsTabPanel.valueMap1.put(product.getProductId(), variableInstallationFee);
//					} else {
//						labelMap.put(product.getProductId(), product.getPublicName() + " (" + NumberFormat.getNumberInstance(new Locale("DA", "dk")).format(product.getPrice().getInstallationFee() / 100) + ")");
//						form.addCheckBox(product.getProductId());
//						partnerSettingsTabPanel.valueMap1.put(product.getProductId(), count > 0);
//					}
//				}
//			}
//		}
		
		if (index == TAB_HARDWARE_TIL_RATE) {
			// ------------------------
			// Hardware til rate
			
			Jsr303Form<ValueMap> form = new Jsr303Form<>("form", partnerSettingsTabPanel.valueMap2, true);
			add(form);
			form.setLabelSpans(SmallSpanType.SPAN1);
			form.setEditorSpans(SmallSpanType.SPAN12);
			form.setLabelStrategy(new MapLabelStrategy(labelMap, form.getLabelStrategy()));
			
			contract.initVariableValueMaps(contract.getCampaigns().get(0));

			for (Product product: contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE.getKey()).getProducts()) {
				if (!((MobileProduct) product).isExcludeFromConfigurator() && product.isActive()) {
					if (contract.getVariableRecurringFees() == null) {
						log.warn("contract.getVariableRecurringFees() is null for contract: " + contract.getId());
					} else {
						Integer price = MapUtils.stringToLongIntMap(contract.getVariableRecurringFees()).get(product.getId());
//						if (price == null) {
//							contract.getVariableRecurringFees()
//						}
						if (price == null) {
							log.warn("No variable recurring fee found for product: " + product.getId());
						} else {
							String category 	= MapUtils.stringToLongStringMap(contract.getVariableCategories()).get(product.getId());
							if (category == null) {
								category = product.getPublicName();
							}
							String productName 	= MapUtils.stringToLongStringMap(contract.getVariableProductNames()).get(product.getId());
//							partnerSettingsTabPanel.valueMap2.put(product.getProductId(), new ProductCategoryNamePriceAndCountRow(product, category, productName, price, contract.getCountNewForProduct(product)));
							partnerSettingsTabPanel.valueMap2.put(product.getProductId(), new ProductPriceAndCountRow(product, price, contract.getCountNewForProduct(product)));
							labelMap.put(product.getProductId(), "");
//						labelMap.put(product.getProductId(), product.getInternalName());
						form.addCustomComponent(new ProductPriceAndCountRowPanel(form, product.getProductId(), true));
//							form.addCustomComponent(new ProductCategoryNamePriceAndCountRowPanel(form, product.getProductId()));
						}
					}
				}
			}

			for (ProductBundle bundle: contract.getCampaigns().get(0).getProductBundles()) {
				MobileProductBundle b = (MobileProductBundle) bundle;
				if (HARDWARE_BUNDLE.equals(b.getBundleType()) && b.isActive()) {
					if (contract.getVariableRecurringFees() == null) {
						log.warn("contract.getVariableRecurringFees() is null for contract: " + contract.getId());
					} else {
//						Amounts a = contract.getAmounts(false, contract.getAcceptAllProductFilter());
//						Integer price = Math.toIntExact((a.getRecurringFee() / 100));

						Integer price = MapUtils.stringToLongIntMap(contract.getVariableRecurringFees()).get(b.getId());

						partnerSettingsTabPanel.valueMap2.put(b.getProductId(), new ProductPriceAndCountRow(b, price, contract.getCountNewForBundle(b)));
						labelMap.put(b.getProductId(), "");
						form.addCustomComponent(new ProductPriceAndCountRowPanel(form, b.getProductId(), true, true));
//						form.addCustomComponent(new ProductCategoryNamePriceAndCountRowPanel(form, b.getProductId()));
					}
				}
			}
			// PRODUCT_GROUP_PARTNER_BUNDLE
		}
		
		if (index == TAB_OPGØRELSE) {
			// ------------------------
			// Tab 3
			
			Jsr303Form<ValueMap> form = new Jsr303Form<>("form", partnerSettingsTabPanel.valueMap3, true);
			add(form);
			form.setLabelSpans(SmallSpanType.SPAN8);
			form.setEditorSpans(SmallSpanType.SPAN4);
			form.setLabelStrategy(new MapLabelStrategy(labelMap, form.getLabelStrategy()));
			
			int formIndex = 0;
			String formKey = String.valueOf(++formIndex);
			labelMap.put(formKey, "Opgørelse over provision");
			spreadsheetLink = form.addSpreadsheetLink(formKey, "partnerprovision.xls", Model.of("Hent som regneark"), new PartnerProvisionSpreadsheet());

			spreadsheetLink.getParent().getParent().setOutputMarkupId(true);
			spreadsheetLink.getParent().getParent().setOutputMarkupPlaceholderTag(true);
			spreadsheetLink.getParent().getParent().setVisible(contract.isShowProvision());
			
			formKey = String.valueOf(++formIndex);
			labelMap.put(formKey, "Pris support aftale pr. md.");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("support_monthly"));
			form.addReadonly(formKey);

//			formKey = String.valueOf(++formIndex);
//			labelMap.put(formKey, "Rabat vedr. oprettelse");
//			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getOneTimeFeeDiscount() / 100);
//			form.addNumberTextField(formKey);

			formKey = "totalTilRate";
			labelMap.put(formKey, "Samlet totalbeløb til rate");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("total_til_rate_betaling"));
			totalTilRateLabel = form.addReadonly(formKey);
			totalTilRateLabel.setOutputMarkupId(true);

			formKey = String.valueOf(++formIndex);
			labelMap.put(formKey, "Pris rate pr. md.");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("rate_monthly"));
			form.addReadonly(formKey);

			formKey = "totalInstallation";
			labelMap.put(formKey, "Samlet kontant pris installation");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("partnerInstallationAfterDiscountKontant"));
			totalInstallationLabel = form.addReadonly(formKey);
			totalInstallationLabel.setOutputMarkupId(true);

			{
				formKey = "installationFeeDiscount";
				labelMap.put(formKey, "Rabat vedr. installation");
				partnerSettingsTabPanel.valueMap3.put(formKey, contract.getInstallationFeeDiscount() / 100);
				TextField textField = form.addTextField(formKey);
				final String finalFormKey = formKey;
				textField.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						try {
							contract.setInstallationFeeDiscount(100 * Integer.valueOf((String) partnerSettingsTabPanel.valueMap3.get(finalFormKey)));
							partnerSettingsTabPanel.valueMap3.put("totalTilRate", contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("total_til_rate_betaling"));
							partnerSettingsTabPanel.valueMap3.put("totalInstallation", contract.getPartnerData(PartnerData.VARIANT_GENERELT).getValues().get("partnerInstallationAfterDiscountKontant"));
							target.add(totalTilRateLabel);
							target.add(totalInstallationLabel);
						} catch (Exception e) {
						}
					}
				});
			}

			formKey = "TM-nummer";
			labelMap.put(formKey, "TM nummer");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.getTmNumber());
			TextField tmNummerField = form.addTextField(formKey);
			if (contract.getSegment() == null) {
				tmNummerField.setEnabled(false);
			}
			
			formKey = String.valueOf(++formIndex);
			final String showProvisionFormKey = formKey;
			labelMap.put(formKey, "Vis provision");
			partnerSettingsTabPanel.valueMap3.put(formKey, contract.isShowProvision());
			form.addCheckBox(formKey, new AjaxEventListener() {
				@Override
				public void onAjaxEvent(AjaxRequestTarget target) {
					provisionTotal.getParent().getParent().setVisible(partnerSettingsTabPanel.valueMap3.getBoolean(showProvisionFormKey));
					target.add(provisionTotal.getParent().getParent());
					spreadsheetLink.getParent().getParent().setVisible(partnerSettingsTabPanel.valueMap3.getBoolean(showProvisionFormKey));
					target.add(spreadsheetLink.getParent().getParent());
				}
			});

			formKey = String.valueOf(++formIndex);
			labelMap.put(formKey, "Provision");
			
		    MutableLong stykProvisions	= new MutableLong();
		    MutableLong satsProvisions	= new MutableLong();
		    MutableLong totalProvisions	= new MutableLong();

		    contract.calculatePartnerProvision(stykProvisions, satsProvisions, totalProvisions);
		    float factor = contract.calculatePartnerProvisionFactor();

		    partnerSettingsTabPanel.valueMap3.put(formKey, NumberFormat.getNumberInstance(new Locale("DA", "dk")).format(totalProvisions.longValue() * factor));
			provisionTotal = form.addReadonly(formKey);
			provisionTotal.getParent().getParent().setOutputMarkupId(true);
			provisionTotal.getParent().getParent().setOutputMarkupPlaceholderTag(true);
			provisionTotal.getParent().getParent().setVisible(contract.isShowProvision());

		}
	}
	
	public class SegmentsProvider extends TextChoiceProvider<Segment> {
		private List<Segment> segments;

		public SegmentsProvider(List<Segment> segments) {
			this.segments = segments;
//			Collections.sort(this.segments, new Comparator<Segment>() {
//				@Override
//				public int compare(Segment o1, Segment o2) {
//					return SegmentsProvider.this.toString(o1).compareTo(SegmentsProvider.this.toString(o2));
//				}
//			});
		}

		@Override
		protected String getDisplayText(Segment choice) {
			return toString(choice);
		}

		@Override
		protected Object getId(Segment choice) {
			return choice.getId();
		}

		@Override
		public void query(String term, int page, Response<Segment> response) {
//			List<Segment> pageOfSegments = new ArrayList<>();
//			if (!StringUtils.isEmpty(term)) {
//				term = term.toUpperCase();
//				for (Segment segment : segments) {
//					if (segment.getName().toUpperCase().contains(term)) {
//						pageOfSegments.add(segment);
//					}
//				}
//			}
			response.addAll(segments);
		}

		@Override
		public Collection<Segment> toChoices(Collection<String> ids) {
			ArrayList<Segment> result = new ArrayList<Segment>();
			for (String id : ids) {
				result.add(segmentDao.findById(Long.valueOf(id)));
			}
			return result;
		}
		
		private String toString(Segment segment) {
			return segment.getName();
		}
	}

}
