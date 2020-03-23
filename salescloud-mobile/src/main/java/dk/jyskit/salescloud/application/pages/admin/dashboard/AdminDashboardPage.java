package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Provider;
import dk.jyskit.salescloud.application.links.spreadsheets.WorkbookAndFileName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.MobileContractSummaryDao;
import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.links.spreadsheets.SpreadsheetLink;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.ContractLine;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileContractSummary;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.base.AdminBasePage;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.salescloud.application.wafextension.forms.KeyPropChoiceRenderer;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.components.containers.AjaxContainer;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.NoLocalizationLabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapListDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTable;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle;
import lombok.extern.slf4j.Slf4j;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class AdminDashboardPage extends AdminBasePage {
	@Inject
	private ContractSaver contractSaver;
	
	@Inject 
	private ContractDao contractDao;
	
	@Inject 
	private OrganisationDao organisationDao;
	
	@Inject
	private ObjectFactory objectFactory;
	
	private ChartFactory chartFactory;
	
	private ChartTypeEnum chartType = ChartTypeEnum.BUSINESS_AREAS;
	private Organisation organisation;
	private SalespersonRole salesperson;
	
	private BootstrapSelectSingle organisationSelector;

	private MarkupContainer organisationSelectorParent;
	
	public AdminDashboardPage(PageParameters parameters) {
		super(parameters);
		
		chartFactory = new ChartFactory();
		
		WebMarkupContainer adminFeaturesContainer = new WebMarkupContainer("adminFeatures") {
			protected void onConfigure() {
				setVisible(CoreSession.get().getActiveRoleClass().equals(AdminRole.class));
			};
		};
		add(adminFeaturesContainer);
		adminFeaturesContainer.add(new SpreadsheetLink("contractsSpreadsheet",
				new WorkbookAndFileName() {
					@Override
					public Provider<Workbook> getWorkbook() {
						return objectFactory.getContractsSpreadsheet();
					}

					@Override
					public IModel<String> getFileName() {
						return Model.of("salescloud_kontrakter_" + MobileSession.get().getDumpYear() + "_" + MobileSession.get().getDumpMonth() + ".xls");
					}
				}));

//				new AbstractReadOnlyModel<String>() {
//					@Override
//					public String getObject() {
//						return "salescloud_kontrakter_" + MobileSession.get().getDumpYear() + "_" + MobileSession.get().getDumpMonth() + ".xls";
//					}
//				}, objectFactory.getContractsSpreadsheet()));
		adminFeaturesContainer.add(new SpreadsheetLink("productsSpreadsheet", "salescloud_produkter.xls", objectFactory.getProductsSpreadsheet()));
		adminFeaturesContainer.add(new SpreadsheetLink("usersSpreadsheet", "salescloud_brugere.xls", objectFactory.getUsersSpreadsheet()));
		Link fixInconsistenciesLink = new Link("fixInconsistencies") {
			@Override
			public void onClick() {
				for (Contract c : contractDao.findAll()) {
					MobileContract contract = (MobileContract) c;
					if (contract.findInconsistencies()) {
						log.warn ("Fixing inconsistencies in contract: " + contract.getTitle() + "/" + contract.getId());
//						contractDao.save(contract);
						contractSaver.save(contract);
					}
				}
			}
		};
		fixInconsistenciesLink.setVisible(false);   // NOT VISIBLE ATM
		adminFeaturesContainer.add(fixInconsistenciesLink);
		
		{
			final MaintenanceModeBean beanModel = new MaintenanceModeBean();
			beanModel.setActive(CoreSession.isMaintenanceMode());
			beanModel.setText(CoreSession.getMaintenanceText());
			Jsr303Form<MaintenanceModeBean> form = new Jsr303Form<>("maintenance", beanModel);
			Map<String, String> labels = new HashMap<String, String>();
			labels.put("text", "Tekst som vises på startside for sælgere når \"maintenance mode\" er aktiv (Markdown)");
			labels.put("active", "\"Maintenance mode\" er aktiv");
			labels.put("warning", "Kun en advarsel, systemet kan stadig bruges");
			labels.put("submit", "Gem");
			form.setLabelStrategy(new MapLabelStrategy(labels, new NoLocalizationLabelStrategy()));
			
			adminFeaturesContainer.add(form);
			FormGroup<MaintenanceModeBean> formGroup = form.createGroup(Model.of("Maintenance mode"));
			formGroup.addTextArea("text");
			formGroup.addCheckBox("active");
			formGroup.addCheckBox("warning");
			formGroup.addSubmitButton("submit", Type.Default, new AjaxSubmitListener() {
				@Override
				public void onSubmit(AjaxRequestTarget target) {
					CoreSession.setMaintenanceMode(beanModel.isActive());
					CoreSession.setMaintenanceModeWarning(beanModel.isWarning());
					CoreSession.setMaintenanceText(beanModel.getText());
					info("Ændringer er gemt");
				}
			});
		}
		
        
        // one month rolling data
        {
        	List<RollingPeriodBean> rollingPeriodList = new ArrayList<>();
        	
        	Map<Long, RollingPeriodBean> salesPersonToData = new HashMap<>();
        	Map<String, RollingPeriodBean> divisionToData = new HashMap<>();
        	RollingPeriodBean totalRollingPeriodBean = new RollingPeriodBean();
        	totalRollingPeriodBean.setDivision("Alle");
        	totalRollingPeriodBean.setSalespersonName("Alle");
        	totalRollingPeriodBean.setTotal(true);
        	totalRollingPeriodBean.getContractLines().add(new ContractLine());
        	totalRollingPeriodBean.getContractLines().get(0).setCustomerName("Alle");
        	totalRollingPeriodBean.getContractLines().get(0).setDate("-");
        	
            boolean allDivisions = DivisionHelper.includeAllDivisions();
//    	    MobileContract contract = null;
    	    MobileContract contractInSession = MobileSession.get().getContract();
    	    
    	    Long contractId = null;
    	    try {
    		    for (MobileContractSummary mcs : Lookup.lookup(MobileContractSummaryDao.class).findAll()) {
    		    	contractId = mcs.getContractId();
    		    	
    		    	if (!Boolean.TRUE.equals(mcs.isDeleted()) && mcs.getContractCreationDate().after(DateUtils.addDays(new Date(), -31))) {
    		    		if (!allDivisions) {
        	            	if (DivisionHelper.skipDivision(CoreSession.get().getSalesmanagerRole(), mcs.getDivision())) {
        	            		continue; // skip contract
        	            	}
    		    		}
//        			    MobileSession.get().setContract(contract);
//        		    	ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo();
        		    	
        		    	// Salesperson
        		    	{
            		    	RollingPeriodBean dataForSalesPerson = salesPersonToData.get(mcs.getSalespersonId());
            		    	if (dataForSalesPerson == null) {
            		    		dataForSalesPerson = new RollingPeriodBean();
            		    		if (StringUtils.isEmpty(mcs.getDivision())) {
                		    		dataForSalesPerson.setDivision("?");
            		    		} else {
                		    		dataForSalesPerson.setDivision(mcs.getDivision());
            		    		}
//            		    		if (dataForSalesPerson == null) {
//            		    			System.out.println("dataForSalesPerson == null");
//            		    		} else if (contract == null) {
//            		    			System.out.println("contract == null");
//            		    		} else if (contract.getSalesperson() == null) {
//            		    			System.out.println("contract.getSalesperson() == null");
//            		    		} else if (contract.getSalesperson().getUser() == null) {
//            		    			System.out.println("contract.getSalesperson().getUser() == null");
//            		    		} else {
            		    			dataForSalesPerson.setSalespersonName(mcs.getSalespersonFullName());
            		    			salesPersonToData.put(mcs.getSalespersonId(), dataForSalesPerson);
//            		    		}
            		    	}
//            		    	dataForSalesPerson.getContractLines().add(new ContractLine(contract, contractFinansialInfo));
            		    	dataForSalesPerson.getContractLines().add(mcs.toContractLine());
        		    	}
        		    	
        		    	// Division + Total
        		    	{
	        		    	String division = mcs.getDivision();
	        		    	if (StringUtils.isEmpty(division)) {
	        		    		division = "?";
	        		    	}
	        		    	RollingPeriodBean dataForDivision = divisionToData.get(division);
	        		    	if (dataForDivision == null) {
	        		    		dataForDivision = new RollingPeriodBean();
	        		    		dataForDivision.setDivision(division);
	        		    		divisionToData.put(division, dataForDivision);
	        		    		dataForDivision.setTotal(true);
	            		    	dataForDivision.getContractLines().add(new ContractLine());
	            		    	dataForDivision.setSalespersonName("Alle");
	            		    	dataForDivision.getContractLines().get(0).setCustomerName("Alle");
	            		    	dataForDivision.getContractLines().get(0).setDate("-");
	        		    	}
	        		    	
	        		    	ContractLine[] contractLines = new ContractLine[] { 
	        		    			dataForDivision.getContractLines().get(0), 
	        		    			totalRollingPeriodBean.getContractLines().get(0) 
	        		    	};
	        		    	
	        		    	for (ContractLine contractLine : contractLines) {
	        		    		if (mcs.getBusinessAreaId() == BusinessAreas.MOBILE_VOICE) {
		        					contractLine.setMobileVoice(contractLine.getMobileVoice() + 1);
			        				contractLine.setMobileVoiceSubscribers(contractLine.getMobileVoiceSubscribers() + mcs.getSubscriberCount());
			        				contractLine.setMobileVoiceTotalRecurring(contractLine.getMobileVoiceTotalRecurring() + mcs.getTotalRecurring());
	        		    		} else if ((mcs.getBusinessAreaId() == BusinessAreas.SWITCHBOARD) ||
										BusinessAreas.match(BusinessAreas.TDC_WORKS, mcs.getBusinessAreaId()) ||
										BusinessAreas.match(BusinessAreas.ONE_PLUS, mcs.getBusinessAreaId())) {
		        					contractLine.setSwitchboard(contractLine.getSwitchboard() + 1);
			        				contractLine.setSwitchboardSubscribers(contractLine.getSwitchboardSubscribers() + mcs.getSubscriberCount());
			        				contractLine.setSwitchboardTotalRecurring(contractLine.getSwitchboardTotalRecurring() + mcs.getTotalRecurring());
	        		    		} else if (mcs.getBusinessAreaId() == BusinessAreas.WIFI) {
		        					contractLine.setWifi(contractLine.getWifi() + 1);
			        				contractLine.setWifiBundles(contractLine.getWifiBundles() + mcs.getWifiBundles());
			        				contractLine.setWifiTotalRecurring(contractLine.getWifiTotalRecurring() + mcs.getTotalRecurring());
	        		    		}
							}
        		    	}
    		    	}
    			}
    	    } catch (Exception e) {
    	    	log.error("Some problem with contract " + contractId, e);
    	    } finally {
    	    	MobileSession.get().setContract(contractInSession);
    	    }
    	    
    	    for (RollingPeriodBean data: salesPersonToData.values()) {
    	    	rollingPeriodList.add(data);
    	    }

			Collections.sort(rollingPeriodList, new Comparator<RollingPeriodBean>() {
				@Override
				public int compare(RollingPeriodBean o1, RollingPeriodBean o2) {
					return o1.getDivision().compareTo(o2.getDivision());
				}
			});
				    	
	    	// Insert divisions
	    	for (RollingPeriodBean divisionRollingPeriodBean : divisionToData.values()) {
	    		boolean foundDivision = false;
	    		boolean inserted = false;
		    	for (int i = 0; i < rollingPeriodList.size(); i++) {
		    		RollingPeriodBean rp = rollingPeriodList.get(i);
	    	    	if (rp.getDivision().equals(divisionRollingPeriodBean.getDivision())) {
	    	    		foundDivision = true;
	    	    	} else {
		    	    	if (foundDivision && !rp.getDivision().equals(divisionRollingPeriodBean.getDivision())) {
		    	    		rollingPeriodList.add(i, divisionRollingPeriodBean);
		    	    		inserted = true;
		    	    		break;
		    	    	}
	    	    	}
	    	    }
	    	    if (!inserted) {
    	    		rollingPeriodList.add(divisionRollingPeriodBean);
	    	    }
	    	}
    	    
    	    // Insert total
	    	rollingPeriodList.add(0, totalRollingPeriodBean);
        	
	    	// Set sorting
	    	for (int i = 0; i < rollingPeriodList.size(); i++) {
    	    	rollingPeriodList.get(i).setSortIndex(i);
    	    }
    	    
    		List<IColumn<RollingPeriodBean, String>> cols = new ArrayList<IColumn<RollingPeriodBean, String>>();
    		
			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Afdeling")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					RollingPeriodBean bean = rowModel.getObject();
					Label label = new Label(componentId, bean.getDivision());
					if (bean.isTotal()) {
						cellItem.getParent().getParent().add(AttributeModifier.replace("class", "total-row"));
					}
					cellItem.add(label);
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Sælger")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					cellItem.add(new Label(componentId, rowModel.getObject().getSalespersonName()));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Dato")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						s += contractLine.getDate();
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Kunde")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						s += contractLine.getCustomerName() == null ? "-" : "<span style='white-space:nowrap;'>" + StringUtils.left(contractLine.getCustomerName(), 40) + "</span>";
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("MV")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine()) {
							s += "" + contractLine.getMobileVoice();
						} else {
							s += contractLine.getMobileVoice() == 1 ? "X" : "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("MV abb.")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getMobileVoice() > 0) {
							s += "" + contractLine.getMobileVoiceSubscribers();
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("MV ACV (kr/år)")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getMobileVoice() > 0) {
							s += "" + Amounts.getFormattedNoDecimals(100 * contractLine.getMobileVoiceTotalRecurring());
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Omst.")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine()) {
							s += "" + contractLine.getSwitchboard();
						} else {
							s += contractLine.getSwitchboard() == 1 ? "X" : "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Omst. abb.")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getSwitchboard() > 0) {
							s += "" + contractLine.getSwitchboardSubscribers();
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("Omst. ACV (kr/år)")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getSwitchboard() > 0) {
							s += "" + Amounts.getFormattedNoDecimals(100 * contractLine.getSwitchboardTotalRecurring());
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("WF")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine()) {
							s += "" + contractLine.getWifi();
						} else {
							s += contractLine.getWifi() == 1 ? "X" : "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("WF addr")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getWifi() > 0) {
							s += "" + contractLine.getWifiBundles();
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});

			cols.add(new AbstractColumn<RollingPeriodBean, String>(new Model("WF ACV (kr/år)")) {
				@Override
				public void populateItem(Item<ICellPopulator<RollingPeriodBean>> cellItem, String componentId, IModel<RollingPeriodBean> rowModel) {
					String s = "";
					for (int i = 0; i < rowModel.getObject().getContractLines().size(); i++) {
						ContractLine contractLine = rowModel.getObject().getContractLines().get(i);
						if (contractLine.isTotalLine() || contractLine.getWifi() > 0) {
							s += "" + Amounts.getFormattedNoDecimals(100 * contractLine.getWifiTotalRecurring());
						} else {
							s += "";
						}
						if (i < rowModel.getObject().getContractLines().size()-1) {
							s += "<br/>";
						}
					}
					cellItem.add(new Label(componentId, s).setEscapeModelStrings(false));
				}
			});
			
    		BootstrapTableStyle[] styles = new BootstrapTableStyle[] {
    				BootstrapTableStyle.HEADERTOOLBAR, BootstrapTableStyle.PAGINGBOTTOMTOOLBAR, BootstrapTableStyle.FILTERTOOLBAR, 
    				BootstrapTableStyle.FILTER_SEARCH, BootstrapTableStyle.BORDERED, BootstrapTableStyle.CONDENSED 
    		};
    		
    		BootstrapTable<RollingPeriodBean, String> table = 
    				new BootstrapTable<RollingPeriodBean, String>("rollingData", cols, new int[] { 10, 20, 50 }, getRollingDataProvider(rollingPeriodList), styles, null); 
    		add(table);
        }
        
        final AjaxContainer chartContainer = new AjaxContainer("chartContainer", true);
        add(chartContainer);
        
        chartContainer.add(chartFactory.build());
        
		Jsr303Form<AdminDashboardPage> settingsForm = new Jsr303Form<AdminDashboardPage>("settingsForm", this);
		add(settingsForm);
		
		BootstrapSelectSingle chartTypeSelector = settingsForm.addSelectSinglePanel("chartType", ChartTypeEnum.valuesAsList(), new KeyPropChoiceRenderer("displayText"), new BootstrapSelectOptions());
		chartTypeSelector.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				organisationSelectorParent.setVisible(ChartTypeEnum.CONTRACT_ACTIVITY_ORGANISATION.equals(chartType));
				target.add(organisationSelectorParent);
			}
		});
		
		organisationSelector = settingsForm.addSelectSinglePanel("organisation", organisationDao.findAll(), new IdPropChoiceRenderer("companyName"), new BootstrapSelectOptions());
		organisationSelectorParent = organisationSelector.getParent().getParent();
		organisationSelectorParent.setOutputMarkupId(true);
		organisationSelectorParent.setOutputMarkupPlaceholderTag(true);
		organisationSelectorParent.setVisible(ChartTypeEnum.CONTRACT_ACTIVITY_ORGANISATION.equals(chartType));
		
		settingsForm.addSubmitButton("submit", Type.Default, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				chartFactory.setChartType(chartType);
				chartFactory.setOrganisation(organisation);
				chartContainer.replace(chartFactory.build());
				target.add(chartContainer);
			}
		});
	}
	
	private BootstrapTableDataProvider<RollingPeriodBean, String> getRollingDataProvider(final List<RollingPeriodBean> rollingPeriodList) {
		IModel<List<RollingPeriodBean>> listModel = new AbstractReadOnlyModel<List<RollingPeriodBean>>() {
			@Override
			public List<RollingPeriodBean> getObject() {
				return rollingPeriodList;
			}
		};
		
		BootstrapListDataProvider<RollingPeriodBean> provider = new BootstrapListDataProvider<RollingPeriodBean>(listModel, "salespersonName", "division") {
			@Override
			protected void sort(List<RollingPeriodBean> filteredList, SortParam<String> sort) {
				super.sort(filteredList, sort);
			}
		};
		provider.setSort("sortIndex", SortOrder.ASCENDING);
		return provider;
	}
}
