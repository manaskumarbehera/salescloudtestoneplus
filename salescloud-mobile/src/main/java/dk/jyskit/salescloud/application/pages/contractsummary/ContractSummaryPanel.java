package dk.jyskit.salescloud.application.pages.contractsummary;

import java.math.BigDecimal;
import java.util.*;

import com.google.common.collect.Lists;
import dk.jyskit.salescloud.application.components.ajax.AjaxWrapper;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.utils.SimpleChoiceRenderer;
import dk.jyskit.waf.wicket.components.containers.AjaxContainer;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.panels.WrapperPanel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.col.SmallSpanType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.MobileContractSummaryDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.services.supercontract.SuperContractService;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303FormDialog;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.panels.confirmation.ConfirmDialog;
import dk.jyskit.waf.wicket.components.panels.modal.ModalContainer;
import dk.jyskit.waf.wicket.utils.IAjaxCall;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static dk.jyskit.salescloud.application.pages.contractsummary.ContractDetails.INGEN_RABATAFTALE;

@SuppressWarnings("serial")
@Slf4j
public class ContractSummaryPanel extends Panel {
	public static final String PERIOD_12_MONTHS_NO_DISCOUNT 	= "12 mdr. uden rabat";
	public static final String PERIOD_12_MONTHS 				= "12 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_24_MONTHS 				= "24 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_36_MONTHS 				= "36 mdr. med TDC Erhverv rabataftale";

	@Inject
	private PageNavigator navigator;
	
	@Inject
	private MobileContractDao contractDao;
	
	@Inject
	private MobileContractSummaryDao contractSummaryDao;

	@Inject
	private ProductGroupDao productGroupDao;

	@Inject
	SuperContractService superContractService;
	
	@Inject
	private ContractSaver contractSaver;

	private TextField discountPercentageTextField;
	private TextField contractSumMobileTextField;
	private TextField contractSumFastnetTextField;
	private TextField contractSumBroadbandTextField;
	private MarkupContainer discountPercentageContainer;
	private MarkupContainer contractSumMobileContainer;
	private MarkupContainer contractSumFastnetContainer;
	private MarkupContainer contractSumBroadbandContainer;

	private TextField additionalUserChangesField;

	private TextField additionToKontraktsumTextField;
	private TextField additionToKontraktsumNetworkTextField;
	private MarkupContainer additionToKontraktsumContainer;

	private ContractDetails contractDetails;
	private Panel outputButtonsPanel;
	private Jsr303Form<ContractDetails> contractForm;
	private Panel buttonsDisabledPanel;

	public ContractSummaryPanel(String id) {
		super(id);
		
		final MobileContract contract = (MobileContract) CoreSession.get().getContract();
		
		contractDetails = contract.getContractDetails();

		contractForm = new Jsr303Form<>("form", contractDetails);
		contractForm.setOutputMarkupId(true);
		add(contractForm);
		
		final FinancialSummaryPanel financialSummaryPanel = new FinancialSummaryPanel("panel");
		
		contractForm.setLabelSpans(SmallSpanType.SPAN6);
		contractForm.setEditorSpans(SmallSpanType.SPAN6);
		
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
			BootstrapSelectSingle contractStatusField = 
					contractForm.addSelectSinglePanel("status", ContractStatusEnum.valuesAsList(ContractStatusEnum.BUSINESSAREA_SPECIFIC), 
							new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
			contractStatusField.getParent().getParent().setOutputMarkupId(true);
			contractStatusField.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if (ContractStatusEnum.IMPLEMENTED.equals(contractDetails.getStatus())) {
						new ConfirmDialog(
								Model.of("Data fjernes"), 
								Model.of("Bekræft venligst at sagen er implementeret. Samtidig accepterer du at data vedr. licenser slettes.")).decliner(onNotImplemented).show();
					} else {
						contract.setStatus(contractDetails.getStatus());
						target.add(contractStatusField.getParent().getParent());
						saveContractChanges(target, financialSummaryPanel);
					}
				}
				
				IAjaxCall onNotImplemented = new IAjaxCall() {
					@Override
					public void invoke(AjaxRequestTarget target) {
						contractDetails.setStatus(contract.getStatus());
						target.add(contractStatusField.getParent().getParent());
						saveContractChanges(target, financialSummaryPanel);
					}
				};
			});		
		}
		
		if (contract.getBusinessArea().hasFeature(FeatureType.SHOW_INSTALLATION_DATE)) {
			DateTextField datePicker = contractForm.addDatePicker("installationDate");
			datePicker.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					contract.setInstallationDate(contractDetails.getInstallationDate());
					saveContractChanges(target, financialSummaryPanel);
				}
			});
		}

		if (contract.getBusinessArea().isOnePlus()) {
			contractForm.createGroup(Model.of("Rabataftale - One+"));
		}

		if (contract.getBusinessArea().hasFeature(FeatureType.SHOW_CONTRACT_START_DATE)) {
			DateTextField contractStartDatePicker = null;
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				contractStartDatePicker = contractForm.addDatePicker("contractStartDateFiberErhverv");
			} else {
				contractStartDatePicker = contractForm.addDatePicker("contractStartDate");
			}
				
//			contractStartDatePicker.setRequired(true);
			contractStartDatePicker.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					contract.setContractStartDate(contractDetails.getContractStartDate());
					saveContractChanges(target, financialSummaryPanel);
					if (target != null) {
						target.add(financialSummaryPanel);
						target.add(outputButtonsPanel);
						target.add(buttonsDisabledPanel);
					}
				}
			});
//			if (MobileSession.get().getBusinessArea().getBusinessAreaId() != BusinessAreas.SWITCHBOARD) {
//				contractStartDatePicker.getParent().getParent().setVisible(false);
//			}
		}
		
		if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.FIXED_DISCOUNT_SPECIFIED)) {
			BootstrapSelectSingle contractTypeSelector = contractForm.addSelectSinglePanel("contractType", MobileContractType.valuesAsList(), new IdPropChoiceRenderer("text"), new BootstrapSelectOptions());
			contractTypeSelector.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(discountPercentageContainer);
					contractDetails.setFixedDiscountPercentage(0);
					saveContractChanges(target, financialSummaryPanel);
				}
			});
		}
		if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.FIXED_DISCOUNT_VARIABLE, FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			BootstrapSelectSingle contractLengthSelector = null;
			if (BusinessAreas.match(BusinessAreas.TDC_WORKS, MobileSession.get().getBusinessArea()) ||
					BusinessAreas.match(BusinessAreas.ONE_PLUS, MobileSession.get().getBusinessArea())) {
				contractLengthSelector = contractForm.addSelectSinglePanel("contractLength",
						contract.getBusinessArea().isOnePlus()
						? Arrays.asList(INGEN_RABATAFTALE, "1 år", "2 år", "3 år")
						: Arrays.asList("1 år", "2 år", "3 år")
						, new BootstrapSelectOptions());
				contractLengthSelector.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
//						contract.setContractLength(contractDetails.getContractLength());
						saveContractChanges(target, financialSummaryPanel);
//						if (target != null) {
//							target.add(financialSummaryPanel);
//							target.add(outputButtonsPanel);
//							target.add(buttonsDisabledPanel);
//						}
					}
				});

			} else if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				contractLengthSelector = contractForm.addSelectSinglePanel("contractLength",
						Arrays.asList(new String[] {
								FiberErhvervBundleData.PERIOD_12_MONTHS_NO_DISCOUNT,
								FiberErhvervBundleData.PERIOD_12_MONTHS, 
								FiberErhvervBundleData.PERIOD_24_MONTHS, 
								FiberErhvervBundleData.PERIOD_36_MONTHS})
						, new BootstrapSelectOptions());
//				String presentValue = null;
//				if (Integer.valueOf(99).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_12_MONTHS_NO_DISCOUNT;
//				} else if (Integer.valueOf(12).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_12_MONTHS;
//				} else if (Integer.valueOf(24).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_24_MONTHS;
//				} else if (Integer.valueOf(36).equals(fiberBundle.getPeriod())) {
//					presentValue = FiberErhvervBundleData.PERIOD_36_MONTHS;
//				}
//				periodDropDownChoice = addStringDropDownChoice(contract, values, labelMap, presentValue, 
//						Lists.newArrayList(
//								null
//								,FiberErhvervBundleData.PERIOD_12_MONTHS_NO_DISCOUNT
//								,FiberErhvervBundleData.PERIOD_12_MONTHS
//								,FiberErhvervBundleData.PERIOD_24_MONTHS
//								,FiberErhvervBundleData.PERIOD_36_MONTHS),
//						KEY_PERIOD + bi, group, "Bindingsperiode / Rabat", componentIndex++, lastComponentIndex, components,
//						new UpdateListener() {
//							@Override
//							public void onAjaxEvent(AjaxRequestTarget target, @SuppressWarnings("rawtypes") FormComponent component) {
//								component.getParent().getParent()
//										.setVisible(!StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ROAD + bi))
//												&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_ZIPCODE + bi))
//												&& !StringUtils.isEmpty((String) values.get(KEY_ADDRESS_CITY + bi)));
//							}
//						});
			} else {
				contractLengthSelector = contractForm.addSelectSinglePanel("contractLength",
						contract.getBusinessArea().isOnePlus()
								? Arrays.asList(new String[] {INGEN_RABATAFTALE, "1 år", "2 år", "3 år", "4 år"})
								: Arrays.asList(new String[] {"1 år", "2 år", "3 år", "4 år"}),
						new BootstrapSelectOptions());
			}
			if (contractLengthSelector != null) {
				contractLengthSelector.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						saveContractChanges(target, financialSummaryPanel);
					}
				});
			}
		} 

		discountPercentageTextField = contractForm.addTextField("fixedDiscountPercentage");
		discountPercentageTextField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				saveContractChanges(target, financialSummaryPanel);
			}
//			@Override
//		    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//		        super.updateAjaxAttributes(attributes);
//		        attributes.setThrottlingSettings(
//		            new ThrottlingSettings("wait-a-bit", Duration.ONE_SECOND, true)
//		        );
//		    }
		});
		if (!MobileSession.get().getBusinessArea().hasFeature(FeatureType.FIXED_DISCOUNT_SPECIFIED) || !contractDetails.getContractType().equals(MobileContractType.FIXED_DISCOUNT)) {
			discountPercentageTextField.getParent().getParent().setVisible(false);
		}
		discountPercentageContainer = discountPercentageTextField.getParent().getParent();

		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				additionToKontraktsumTextField = contractForm.addTextField("additionToKontraktsumFiberErhverv");
			} else {
				additionToKontraktsumTextField = contractForm.addTextField("additionToKontraktsum");
			}
			additionToKontraktsumTextField.add(new OnChangeAjaxBehavior() {
        		@Override
        		protected void onUpdate(AjaxRequestTarget target) {
        			saveContractChanges(target, financialSummaryPanel);
        		}
        	});
			additionToKontraktsumContainer = additionToKontraktsumTextField.getParent().getParent();
		} else if (contract.getBusinessArea().hasFeature(FeatureType.FORDELSAFTALE)) {
			contractForm.addLabel("contractSumInfo");

			contractSumMobileTextField = contractForm.addTextField("contractSumMobile");
			contractSumMobileTextField.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					saveContractChanges(target, financialSummaryPanel);
				}
//				@Override
//			    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//			        super.updateAjaxAttributes(attributes);
//			        attributes.setThrottlingSettings(
//			            new ThrottlingSettings("wait-a-bit", Duration.ONE_SECOND, true)
//			        );
//			    }
			});
			contractSumMobileContainer = contractSumMobileTextField.getParent().getParent();

			contractSumFastnetTextField = contractForm.addTextField("contractSumFastnet");
			contractSumFastnetTextField.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					saveContractChanges(target, financialSummaryPanel);
				}
//				@Override
//			    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//			        super.updateAjaxAttributes(attributes);
//			        attributes.setThrottlingSettings(
//			            new ThrottlingSettings("wait-a-bit", Duration.ONE_SECOND, true)
//			        );
//			    }
			});
			contractSumFastnetContainer = contractSumFastnetTextField.getParent().getParent();

//			contractSumBroadbandTextField = contractForm.addTextField("contractSumBroadband");
//			contractSumBroadbandTextField.add(new OnChangeAjaxBehavior() {
//				@Override
//				protected void onUpdate(AjaxRequestTarget target) {
//					saveContractChanges(target, financialSummaryPanel);
//				}
//				@Override
//			    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//			        super.updateAjaxAttributes(attributes);
//			        attributes.setThrottlingSettings(
//			            new ThrottlingSettings("wait-a-bit", Duration.ONE_SECOND, true)
//			        );
//			    }
//			});
//			contractSumBroadbandContainer = contractSumBroadbandTextField.getParent().getParent();
		}

		if (contract.getBusinessArea().isOnePlus()) {
			contractForm.createGroup(Model.of("Rabataftale - Netværk"));

			DateTextField contractStartDatePicker = null;
			contractStartDatePicker = contractForm.addDatePicker("contractStartDateNetwork");
//			contractStartDatePicker.setRequired(true);
			contractStartDatePicker.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					contract.setContractStartDateNetwork(contractDetails.getContractStartDateNetwork());
					saveContractChanges(target, financialSummaryPanel);
					if (target != null) {
						target.add(financialSummaryPanel);
						target.add(outputButtonsPanel);
						target.add(buttonsDisabledPanel);
					}
				}
			});

			if ((contract.getContractLengthNetwork() == null) || (contract.getContractLengthNetwork().intValue() == 0)) {
				if (contract.getBusinessArea().isOnePlus()) {
					contractDetails.setContractLengthNetwork(INGEN_RABATAFTALE);
				} else {
					contractDetails.setContractLengthNetwork("1 år");
				}
			} else {
				contractDetails.setContractLengthNetwork(contract.getContractLengthNetwork() + " år");
			}
			BootstrapSelectSingle contractLengthSelector = null;
			contractLengthSelector = contractForm.addSelectSinglePanel("contractLengthNetwork",
					contract.getBusinessArea().isOnePlus()
							? Arrays.asList(new String[] {INGEN_RABATAFTALE, "1 år", "2 år", "3 år"})
							: Arrays.asList(new String[] {"1 år", "2 år", "3 år"}),
					new BootstrapSelectOptions());
			contractLengthSelector.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					saveContractChanges(target, financialSummaryPanel);
				}
			});

			additionToKontraktsumNetworkTextField = contractForm.addTextField("additionToKontraktsumNetwork");
			additionToKontraktsumNetworkTextField.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					saveContractChanges(target, financialSummaryPanel);
				}
			});
			additionToKontraktsumContainer = additionToKontraktsumNetworkTextField.getParent().getParent();

			contractForm.createGroup(Model.of("Andet"));

			{
				// FlexConnect?
				contractDetails.setExistingFlexConnectSubscriptions(contract.getExistingFlexConnectSubscriptions() == null ? null : (contract.getExistingFlexConnectSubscriptions() ? "Ja" : "Nej"));
				BootstrapSelectSingle selectSingle = contractForm.addSelectSinglePanel("existingFlexConnectSubscriptions", Lists.newArrayList("Ja", "Nej"),
						new SimpleChoiceRenderer(), new BootstrapSelectOptions());
				selectSingle.setRequired(true);
				selectSingle.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						saveContractChanges(target, financialSummaryPanel);
						setResponsePage(new ContractSummaryPage(new PageParameters()));
					}
				});
			}

			if (MobileContractMode.CONVERSION.equals(contract.getContractMode())) {
				// Midlertidigt løsning (Rettelser 190905 side 75):
				contractDetails.setAdditionalUserChanges(null);
				contract.setAdditionalUserChanges(null);
				contract.setCallFlowChanges(null);

				// Midlertidigt disabled (Rettelser 190905 side 75):
				/*
				if (contract.hasDDI()) {
					// Ja, der er nysalg af DDI
					contractDetails.setAdditionalUserChanges(null);
					contract.setAdditionalUserChanges(null);
					contract.setCallFlowChanges(null);
				} else {
					{
						// ændringer i kaldsflow?
						contractDetails.setCallFlowChanges(contract.getCallFlowChanges() == null ? null : (contract.getCallFlowChanges() ? "Ja" : "Nej"));
						BootstrapSelectSingle selectSingle = contractForm.addSelectSinglePanel("callFlowChanges", Lists.newArrayList("Ja", "Nej"),
								new SimpleChoiceRenderer(), new BootstrapSelectOptions());
						selectSingle.setRequired(true);
						selectSingle.add(new OnChangeAjaxBehavior() {
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								if ("Ja".equals(contractDetails.getCallFlowChanges())) {
									additionalUserChangesField.getParent().getParent().setVisible(false);
									contractDetails.setAdditionalUserChanges(null);
								} else if ("Nej".equals(contractDetails.getCallFlowChanges())) {
									additionalUserChangesField.getParent().getParent().setVisible(true);
									contractDetails.setAdditionalUserChanges(0);
								} else {
									contractDetails.setAdditionalUserChanges(null);
									additionalUserChangesField.getParent().getParent().setVisible(false);
								}
								target.add(additionalUserChangesField.getParent().getParent());
								saveContractChanges(target, financialSummaryPanel);
							}
						});
					}

					contractDetails.setAdditionalUserChanges(contract.getAdditionalUserChanges());
					additionalUserChangesField = contractForm.addNumberTextField("additionalUserChanges");
					additionalUserChangesField.add(new OnChangeAjaxBehavior() {
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							saveContractChanges(target, financialSummaryPanel);
						}
					});
					if (Boolean.TRUE.equals(contract.getCallFlowChanges())) {
						additionalUserChangesField.getParent().getParent().setVisible(false);
					} else {
						if (contractDetails.getAdditionalUserChanges() == null) {
							contractDetails.setAdditionalUserChanges(0);
						}
					}
					additionalUserChangesField.getParent().getParent().setOutputMarkupPlaceholderTag(true);
					additionalUserChangesField.getParent().getParent().setOutputMarkupId(true);
				}
				 */
			}
		}

//		TextArea offerIntroTextField = contractForm.addTextArea("offerIntroText");
//		offerIntroTextField.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				saveContractChanges(target, financialSummaryPanel);
//			}
//		});

//		discountPercentageTextField.add(new OnChangeAjaxBehavior() {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				updateFixedDiscount(target);
//				target.add(finansialSummaryPanel);
//			}
//		});

//		updateFixedDiscount(null);

		contractForm.addPanel(financialSummaryPanel, true);
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			outputButtonsPanel = new OutputButtonsOnePlusPanel("panel") {
				@Override
				protected void onConfigure() {
					super.onConfigure();
					setVisible(!missingRequiredData(contract));
				}
			};
		} else {
			outputButtonsPanel = new OutputButtonsPanel("panel") {
				@Override
				protected void onConfigure() {
					super.onConfigure();
					setVisible(!missingRequiredData(contract));
				}
			};
		}

		contractForm.addPanel(outputButtonsPanel, true);

		buttonsDisabledPanel = new ButtonsDisabledPanel("panel") {
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(missingRequiredData(contract));
			}
		};
		contractForm.addPanel(buttonsDisabledPanel, true);

// Ikke nødvendig mere, vel?
//		AjaxButton updateButton = contractForm.addSubmitButton("update", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				saveContractChanges(target, financialSummaryPanel);
//			}
//		});

		// Navigation

		AjaxButton prevButton = contractForm.addSubmitButton("prev", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(target, financialSummaryPanel, false);
			}
		});
		AjaxButton nextButton = contractForm.addSubmitButton("next", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveAndNavigate(target, financialSummaryPanel, true);
			}
		});
		nextButton.setVisible(
				MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE ||
				MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER ||
				MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV);

		AjaxButton extraFormButton = contractForm.addSubmitButton("extraForm", Buttons.Type.Info, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				saveContractChanges(target, financialSummaryPanel);

				MobileContract contract = (MobileContract) CoreSession.get().getContract();
				final IModel<MobileContract> contractModel = new Model<MobileContract>(contract);
				final Jsr303FormDialog<MobileContract> dialog = new Jsr303FormDialog<MobileContract>(ModalContainer.MODAL_ID, contractModel, false) {
					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						super.onSubmit(target);
						MobileContract contract = contractDao.save(contractModel.getObject());
						contract.adjustOrderLineForExtraProducts();
						setResponsePage(ContractSummaryPage.class);
					}
				};
				dialog.headerKey("modalform.header");

				FormGroup<MobileContract> offerGroup = dialog.createGroup("offer");

				TextArea offerIntroTextField = offerGroup.addTextArea("offerIntroText");

				FormGroup<MobileContract> productionOutputTextGroup = dialog.createGroup("production");

				TextArea productionOutputTextField = productionOutputTextGroup.addTextArea("productionOutputText");

				FormGroup<MobileContract> extra1Group = dialog.createGroup("extra1");

				extra1Group.addTextField("extraProduct1Text");
				extra1Group.addTextField("extraProduct1OneTimeFee");
				if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.RECURRING_FEE_SPLIT)) {
					extra1Group.addTextField("extraProduct1InstallationFee");
				}
				extra1Group.addTextField("extraProduct1RecurringFee");

				FormGroup<MobileContract> extra2Group = dialog.createGroup("extra2");

				extra2Group.addTextField("extraProduct2Text");
				extra2Group.addTextField("extraProduct2OneTimeFee");
				if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.RECURRING_FEE_SPLIT)) {
					extra2Group.addTextField("extraProduct2InstallationFee");
				}
				extra2Group.addTextField("extraProduct2RecurringFee");

				FormGroup<MobileContract> extra3Group = dialog.createGroup("extra3");

				extra3Group.addTextField("extraProduct3Text");
				extra3Group.addTextField("extraProduct3OneTimeFee");
				if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.RECURRING_FEE_SPLIT)) {
					extra3Group.addTextField("extraProduct3InstallationFee");
				}
				extra3Group.addTextField("extraProduct3RecurringFee");

				dialog.addStandardButtons();
				ModalContainer.showModal(target, dialog);
			}
		});
		extraFormButton.setVisible(
				(MobileSession.get().getBusinessArea().getBusinessAreaId() != BusinessAreas.TDC_OFFICE) &&
				(MobileSession.get().getBusinessArea().getBusinessAreaId() != BusinessAreas.FIBER_ERHVERV)
				);
	}
	
	private boolean missingRequiredData(MobileContract contract) {
		BusinessArea businessArea = contract.getBusinessArea();
		if (businessArea.isOnePlus()) {
			// Midlertidigt disabled (Rettelser 190905 side 75):
			/*
			if (MobileContractMode.CONVERSION.equals(MobileSession.get().getContract().getContractMode())) {
				if (!contract.hasDDI() && MobileSession.get().getContract().getCallFlowChanges() == null) {
					return true;
				}
			}
			 */
			return false;
		}

		if (businessArea.hasFeature(FeatureType.SHOW_CONTRACT_START_DATE) && (contractDetails.getContractStartDate() == null)) {
			return true;
		}
//		if (businessArea.isOnePlus() && (contractDetails.getContractStartDateNetwork() == null)) {
//			return true;
//		}
		if (businessArea.hasFeature(FeatureType.FORDELSAFTALE) && (contractDetails.getContractSumMobile() == null || contractDetails.getContractSumMobile() == 0)) {
			return true;
		}
		return false;
	}
	
	private void saveContractChanges(AjaxRequestTarget target, FinancialSummaryPanel financialSummaryPanel) {
		MobileContract contract = (MobileContract) CoreSession.get().getContract();
		if (contractDetails.getContractLength() != null) {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				switch (contractDetails.getContractLength()) {
				case PERIOD_12_MONTHS_NO_DISCOUNT:
					contract.setContractLength(0);
					break;
				case PERIOD_12_MONTHS:
					contract.setContractLength(1);
					break;
				case PERIOD_24_MONTHS:
					contract.setContractLength(2);
					break;
				case PERIOD_36_MONTHS:
					contract.setContractLength(3);
					break;
				}
			} else {
				try {
					contract.setContractLength((contractDetails.getContractLength() == null) ? 0 :
							Integer.valueOf(contractDetails.getContractLength().substring(0, 1)));
				} catch (Exception e) {
					contract.setContractLength(0);
				}
				if (MobileSession.get().getBusinessArea().isOnePlus()) {
					try {
						contract.setContractLengthNetwork((contractDetails.getContractLengthNetwork() == null) ? 0 :
								Integer.valueOf(contractDetails.getContractLengthNetwork().substring(0, 1)));
					} catch (Exception e) {
						contract.setContractLengthNetwork(0);
					}
				}
			}
		}

		contract.setContractType(contractDetails.getContractType());
		try {
			contract.setInstallationDate(contractDetails.getInstallationDate());
		} catch (Exception e) {}

		contract.setAdditionalUserChanges(contractDetails.getAdditionalUserChanges() == null ? null : Integer.valueOf(contractDetails.getAdditionalUserChanges()));
		contract.setCallFlowChanges("Ja".equals(contractDetails.getCallFlowChanges()));
		contract.setExistingFlexConnectSubscriptions("Ja".equals(contractDetails.getExistingFlexConnectSubscriptions()));

		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			contract.setContractStartDate(contractDetails.getContractStartDateFiberErhverv());
		} else {
			contract.setContractStartDate(contractDetails.getContractStartDate());
		}

		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
			if (!ContractStatusEnum.IMPLEMENTED.equals(contract.getStatus()) && ContractStatusEnum.IMPLEMENTED.equals(contractDetails.getStatus())) {
				contract.setSubscriptions(new ArrayList<Subscription>());
			}
			if (ContractStatusEnum.IMPLEMENTED.equals(contract.getStatus()) && ContractStatusEnum.SENT_TO_IMPLEMENTATION.equals(contractDetails.getStatus())) {
				// Not allowed. It's considered a race-condition
			} else {
				contract.setStatus(contractDetails.getStatus());
			}
		}
		
		try {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				contract.setAdditionToKontraktsum(contractDetails.getAdditionToKontraktsumFiberErhverv());
			} else {
				contract.setAdditionToKontraktsum(contractDetails.getAdditionToKontraktsum());
			}
		} catch (Exception e) {
			contract.setAdditionToKontraktsum(0);
		}

		try {
			if (MobileSession.get().getBusinessArea().isOnePlus()) {
				contract.setContractStartDateNetwork(contractDetails.getContractStartDateNetwork());
				contract.setAdditionToKontraktsumNetwork(contractDetails.getAdditionToKontraktsumNetwork());

				contract.adjustOrderLinesForRemoteInstallation();
			}
		} catch (Exception e) {
			contract.setAdditionToKontraktsumNetwork(0);
		}

		contract.setContractSumMobile(contractDetails.getContractSumMobile()  == null ? 0 : contractDetails.getContractSumMobile());
		contract.setContractSumFastnet(contractDetails.getContractSumFastnet() == null ? 0 : contractDetails.getContractSumFastnet());
		contract.setContractSumBroadband(contractDetails.getContractSumBroadband() == null ? 0 : contractDetails.getContractSumBroadband());
		
//		contract.setOfferIntroText(contractDetails.getOfferIntroText() == null ? "" : contractDetails.getOfferIntroText());
		
//		contract.adjustOrderLineForExtraProducts();

		if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			RabatAftaleDiscountScheme contractDiscountScheme = (RabatAftaleDiscountScheme) contract.getDiscountScheme(RabatAftaleDiscountScheme.class);
			if (contractDiscountScheme == null) {
				contractDiscountScheme = new RabatAftaleDiscountScheme();
				contractDiscountScheme.setName("TDC Erhverv Rabataftale");
				contract.addDiscountScheme(contractDiscountScheme);
			}
		} else if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.FIXED_DISCOUNT_SPECIFIED)) {
			FixedDiscount fixedDiscount = (FixedDiscount) contract.getDiscountScheme(FixedDiscount.class);
			if (contractDetails.getContractType().equals(MobileContractType.FIXED_DISCOUNT)) {
				discountPercentageContainer.setVisible(true);
				if (fixedDiscount == null) {
					fixedDiscount = new FixedDiscount();
					fixedDiscount.setName("Fast rabat");
					contract.addDiscountScheme(fixedDiscount);
				}
				fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 100 * contractDetails.getFixedDiscountPercentage()));
			} else {
				discountPercentageContainer.setVisible(false);
				contract.removeDiscountScheme(fixedDiscount);
			}
		} else if (MobileSession.get().getBusinessArea().hasFeature(FeatureType.FIXED_DISCOUNT_VARIABLE)) {
			FixedDiscount fixedDiscount = (FixedDiscount) contract.getDiscountScheme(FixedDiscount.class);
			if (fixedDiscount == null) {
				fixedDiscount = new FixedDiscount();
				fixedDiscount.setName("Fast rabat");
				contract.addDiscountScheme(fixedDiscount);
			}
			switch (contract.getAdjustedContractLength()) {
			case 2:
				fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 2500));
				break;
			case 3:
				fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 2700));
				break;
			case 4:
				fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 2900));
				break;
			default:
				fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 0));
				break;
			}
		}
		
		ContractFinansialInfo info = null;
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			info = contract.getContractFinansialInfo(true, false, false);
			CoreSession.get().setDiscountPointNonNetwork(Lookup.lookup(SuperContractService.class).getDiscountPoint(contract.getBusinessArea(),
					new BigDecimal(info.getRabataftaleKontraktsum()), contract, false));
			if (contract.getBusinessArea().isOnePlus()) {
				info = contract.getContractFinansialInfo(false, true, false);
				CoreSession.get().setDiscountPointNetwork(Lookup.lookup(SuperContractService.class).getDiscountPoint(contract.getBusinessArea(),
						new BigDecimal(info.getRabataftaleKontraktsum()), contract, true));
			}
		}
		
		contractSaver.save(contract);
		
//		Long contractId = contractDao.save(contract).getId();
//		
//		if (contractId != null) {
//			contractSummaryDao.deleteByContractId(contractId);
//		}
//		contractSummaryDao.save(MobileContractSummary.create(contract));
		
		if (target != null) {
			target.add(financialSummaryPanel);
			target.add(outputButtonsPanel);
			target.add(buttonsDisabledPanel);
		}
//		System.out.println("setContractSumMobile: " + contractDetails.getContractSumMobile());
//		System.out.println("setContractSumFastnet: " + contractDetails.getContractSumFastnet());
//		System.out.println("setContractSumBroadband: " + contractDetails.getContractSumBroadband());
	}
	
	private void saveAndNavigate(AjaxRequestTarget target, FinancialSummaryPanel financialSummaryPanel, boolean goToNext) {
		saveContractChanges(target, financialSummaryPanel);
		if (goToNext) {
			setResponsePage(navigator.next(getWebPage()));
		} else {
			setResponsePage(navigator.prev(getWebPage()));
		}
	}
	
//	private void updateFixedDiscount(AjaxRequestTarget target) {
//		MarkupContainer discountPercentageContainer = discountPercentageTextField.getParent().getParent();
//		
//		MobileContract contract = (MobileContract) CoreSession.get().getContract();
//		
//		FixedDiscount fixedDiscount = (FixedDiscount) contract.getDiscountScheme(FixedDiscount.class);
//		
//		if (contractDetails.getContractType().equals(MobileContractType.FIXED_DISCOUNT)) {
//			discountPercentageContainer.setVisible(true);
//			if (fixedDiscount == null) {
//				fixedDiscount = new FixedDiscount();
//				fixedDiscount.setName("Fast rabat");
////				fixedDiscount.setApplyAutomatically(false);
//				contract.addDiscountScheme(fixedDiscount);
//			}
//			fixedDiscount.setDiscountPercentages(new Amounts(0, 0, 100 * contractDetails.getFixedDiscountPercentage()));
//		} else {
//			discountPercentageContainer.setVisible(false);
//			contract.removeDiscountScheme(fixedDiscount);
//		}
//		contractDao.save(contract);
//		if (target != null) {
//			target.add(discountPercentageContainer);
//		}
//	}
}
