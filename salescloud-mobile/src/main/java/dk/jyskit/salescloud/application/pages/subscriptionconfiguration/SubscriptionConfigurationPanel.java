package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.BORDERED;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.CONDENSED;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.FILTERTOOLBAR;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.FILTER_SEARCH;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.HEADERTOOLBAR;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.PAGINGBOTTOMTOOLBAR;
import static dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle.STRIPED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Constants;
import dk.jyskit.salescloud.application.model.ContractStatusEnum;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.NumberTransferType;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.SimCardType;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns.DropDownColumn;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns.EmailTextFieldColumn;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns.LabelColumn;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns.PoolColumn;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns.TextFieldColumn;
import dk.jyskit.waf.application.components.login.username.LoginInfo;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.wicket.components.forms.annotations.DefaultFocusBehavior;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTable;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableResponsiveType;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscriptionConfigurationPanel extends Panel {
	private EntityModel<MobileContract> contractModel;
	private LoginInfo loginInfo;
	private boolean impl;

	public SubscriptionConfigurationPanel(String wicketId, final EntityModel<MobileContract> contractModel, boolean impl) {
		super(wicketId);
		this.contractModel = contractModel;
		this.impl = impl;
	}
	
	@Override
	protected void onInitialize() {
		boolean loginVisible = MobileSession.get().isExternalAccessMode() && !MobileSession.get().isCustomerLoggedIn() && !MobileSession.get().isImplementerLoggedIn();
		final WebMarkupContainer loginContainer = new WebMarkupContainer("loginContainer");
		add(loginContainer);
		loginContainer.setVisible(loginVisible);
		loginContainer.setOutputMarkupId(true);
		
		final WebMarkupContainer nonLoginContainer = new WebMarkupContainer("nonLoginContainer");
		add(nonLoginContainer);
		nonLoginContainer.setVisible(!loginVisible);
		nonLoginContainer.setOutputMarkupId(true);
		
		loginInfo = new LoginInfo();
		Form form = new Form("form", new CompoundPropertyModel<LoginInfo>(loginInfo));
		loginContainer.add(form);
		final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		form.add(new RequiredTextField<String>("username").add(new DefaultFocusBehavior()));
		form.add(new PasswordTextField("password"));
		form.add(new AjaxButton("button", new Model("Login"), form) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if ((contractModel.getObject().getConfigurationUsername()).equals(loginInfo.getUsername())) {
					if (contractModel.getObject().getConfigurationPassword().equals(loginInfo.getPassword())) {
						if (impl) {
							MobileSession.get().setImplementerLoggedIn(true);
							MobileSession.get().setCustomerLoggedIn(false);
						} else {
							MobileSession.get().setCustomerLoggedIn(true);
							MobileSession.get().setImplementerLoggedIn(false);
						}
						setResponsePage(new ExternalSubscriptionConfigurationPage(new PageParameters(), impl));
					} else {
						error("Forkert brugernavn eller kodeord");
						target.add(feedbackPanel);
					}
				} else {
					error("Forkert brugernavn eller kodeord");
					target.add(feedbackPanel);
				}
//				loginContainer.setVisible(false);
//				target.add(loginContainer);  
//				table.setVisible(true);
//				target.add(table);  
			}
			
			protected void onError(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
				log.error("Unhandled error!!!");
			}
		});
		
		BootstrapTableStyle[] tableStylesAndOptions;
		tableStylesAndOptions = new BootstrapTableStyle[] { HEADERTOOLBAR, PAGINGBOTTOMTOOLBAR, FILTERTOOLBAR,
				FILTER_SEARCH, BORDERED, CONDENSED, STRIPED };
		// DaoTableDataProvider<Subscription, SubscriptionDao> dataProvider =
		// DaoTableDataProvider.create(subscriptionDao, "id",
		// SortOrder.ASCENDING, getInitialFilter());
		
		final long size = contractModel.getObject().getSubscriptions().size();
		
		SortableDataProvider<Subscription, String> sortableDataProvider = new SortableDataProvider<Subscription, String>() {
			@Override
			public long size() {
				return size;
			}
			
			@Override
			public IModel<Subscription> model(Subscription subscription) {
				return EntityModel.forEntity(subscription);
			}
			
			@Override
			public Iterator<? extends Subscription> iterator(long first, long count) {
				final List<Subscription> sortedSubscriptions = contractModel.getObject().getSubscriptions();
				Collections.sort(sortedSubscriptions, new Comparator<Subscription>() {
					@Override
					public int compare(Subscription s1, Subscription s2) {
						return s1.getSortIndex().compareTo(s2.getSortIndex());
					}
				});
				return sortedSubscriptions.subList((int) first, (int) first + (int) count).iterator();
			}
		};
		// sortableDataProvider.setSort("sortIndex", SortOrder.ASCENDING);
		
		List<IColumn<Subscription, String>> cols = createColumns();
		
		// BootstrapTable<Subscription, String> table = new
		// BootstrapTable<Subscription, String>("table", cols, new int[] {
		// 10 }, dataProvider, tableStylesAndOptions, null);
		final BootstrapTable<Subscription, String> table = new BootstrapTable<Subscription, String>("table", cols,
				new int[] { 10 }, sortableDataProvider, tableStylesAndOptions, null);
		table.setOutputMarkupId(true);
		table.setResponsive(BootstrapTableResponsiveType.SCROLLABLE);
		
		Form tableForm = new Form("tableForm", new CompoundPropertyModel<LoginInfo>(loginInfo));
		nonLoginContainer.add(tableForm);
		tableForm.add(table);
		table.setVisible((!MobileSession.get().isExternalAccessMode() || MobileSession.get().isCustomerLoggedIn() || MobileSession.get().isImplementerLoggedIn()) 
				&& contractModel.getObject().hasOfficeImplementationInfo() && !ContractStatusEnum.IMPLEMENTED.equals(contractModel.getObject().getStatus()));
		
		WebMarkupContainer tableNotEnabled = new WebMarkupContainer("tableNotEnabled");
		nonLoginContainer.add(tableNotEnabled);
		tableNotEnabled.setVisible(!MobileSession.get().isExternalAccessMode() && !contractModel.getObject().hasOfficeImplementationInfo());
		
		WebMarkupContainer alreadyImplemented = new WebMarkupContainer("alreadyImplemented");
		nonLoginContainer.add(alreadyImplemented);
		alreadyImplemented.setVisible(MobileSession.get().isExternalAccessMode() && ContractStatusEnum.IMPLEMENTED.equals(contractModel.getObject().getStatus()));
		
//		AjaxButtonPanel buttonPanel = new AjaxButtonPanel(this, "Login", Buttons.Type.Primary, listener , false);
//		loginContainer.add(buttonPanel);
//		buttonOrLinkPanels.add(buttonPanel);
//		getForm().setDefaultButton(buttonPanel.getButton());
//		AjaxButton prevButton = loginContainer.addSubmitButton("action.prev", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//			}
//		});
		super.onInitialize();
	}

	private Filter getInitialFilter() {
		return new Equal("contract", contractModel.getObject());
	}

	private List<IColumn<Subscription, String>> createColumns() {
		List<IColumn<Subscription, String>> cols = new ArrayList<IColumn<Subscription, String>>();

		boolean isOffice = MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE;

		boolean readonly = false;
		
		if (isOffice) {
			cols.add(new PropertyColumn<Subscription, String>(new ResourceModel("office.header.bundle.name"), "bundle.publicName"));
			if (MobileSession.get().isCustomerLoggedIn()) {
				readonly = (contractModel.getObject().getStatus().getId() > ContractStatusEnum.AWAITING_DATA_FROM_CUSTOMER.getId());
			} else if (!MobileSession.get().isExternalAccessMode()) {
				readonly = (contractModel.getObject().getStatus().getId() >= ContractStatusEnum.SENT_TO_IMPLEMENTATION.getId());
			}
			
			if (readonly) {
				cols.add(new LabelColumn(new ResourceModel("header.firstName"), "firstName"));
				cols.add(new LabelColumn(new ResourceModel("header.lastName"), "lastName"));
				cols.add(new LabelColumn(new ResourceModel("header.email"), "email"));
			} else {
				cols.add(new TextFieldColumn(new ResourceModel("header.firstName"), "firstName"));
				cols.add(new TextFieldColumn(new ResourceModel("header.lastName"), "lastName"));
				cols.add(new EmailTextFieldColumn(new ResourceModel("header.email"), "email"));
			}
		} else {
			cols.add(new DropDownColumn<NumberTransferType>(new ResourceModel("header.numberTransferType"),
					"numberTransferType", NumberTransferType.valuesAsList()));
			cols.add(new TextFieldColumn(new ResourceModel("header.mobileNumber"), "mobileNumber"));
			cols.add(new TextFieldColumn(new ResourceModel("header.name"), "name"));
			cols.add(new TextFieldColumn(new ResourceModel("header.division"), "division"));
			cols.add(new TextFieldColumn(new ResourceModel("header.icc"), "icc"));
			cols.add(new DropDownColumn<SimCardType>(new ResourceModel("header.simCardType"), "simCardType",
					SimCardType.valuesAsList()));
		}

		boolean hasDatadeling = false;
		for (OrderLine orderLine : contractModel.getObject().getOrderLines()) {
			if (orderLine.getTotalCount() > 0) {
				if (orderLine.getProduct() != null) {
					if (orderLine.getProduct().getPublicName().startsWith("Datadeling")) {
						hasDatadeling = true;
						break;
					}
				} else {
					for (BundleProductRelation relation : orderLine.getBundle().getProducts()) {
						if ((relation.getProduct() != null)
								&& relation.getProduct().getPublicName().startsWith("Datadeling")) {
							hasDatadeling = true;
							break;
						}
					}
				}
			}
		}
		if (hasDatadeling) {
			cols.add(new DropDownColumn<SimCardType>(new ResourceModel("header.datadelingSimCardType"),
					"datadelingSimCardType", SimCardType.valuesAsList()));
		}

		// int indexOfAcademy = -1;
		for (OrderLine orderLine : contractModel.getObject().getOrderLines()) {
			if (orderLine.getProduct() != null) {
				MobileProduct product = (MobileProduct) orderLine.getProduct();
				boolean includeProduct = false;
				if (isOffice) {
					includeProduct = product.getProductGroup().getUniqueName()
							.equals(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey())
							&& (product.getMaxCount() == Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT) && (orderLine.getTotalCount() > 0);
				} else {
					includeProduct = (!product.isExtraProduct() && (orderLine.getTotalCount() > 0)
							|| (orderLine.isCustomFlag()));
				}

				if (includeProduct) {
					int used = 0;
					List<Subscription> subscriptions = contractModel.getObject().getSubscriptions();
					for (Subscription subscription : subscriptions) {
						if (subscription.getProducts().contains(orderLine.getProduct())) {
							used++;
						}
					}
					cols.add(new PoolColumn(orderLine, used, readonly));
					// if (product.getPublicName().startsWith("Cloud Academy"))
					// {
					// indexOfAcademy = cols.size();
					// }
				}
			}
		}
		// if (indexOfAcademy > -1) {
		// cols.add(indexOfAcademy++, new TextFieldColumn(new
		// ResourceModel("header.position"), "position"));
		// cols.add(indexOfAcademy++, new TextFieldColumn(new
		// ResourceModel("header.phone"), "phone"));
		// }

		return cols;
	}
}
