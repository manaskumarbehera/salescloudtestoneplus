package dk.jyskit.salescloud.application.pages.sales.existingcontract;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonGroup;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonList;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Orientation;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.utils.filter.And;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.utils.filter.IsNull;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class ExistingContractsPanel extends Panel {
	public ExistingContractsPanel(String id) {
		super(id);
		
		setOutputMarkupId(true);

		final SalespersonRole salespersonRole = CoreSession.get().getSalespersonRole();

		final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb") {
			@Override
			protected void onConfigure() {
				setVisible(allBreadCrumbParticipants().size() > 1);
			}
		};
		breadcrumb.setOutputMarkupId(true);
		breadcrumb.setOutputMarkupPlaceholderTag(true);
		add(breadcrumb);
		
		ListContractPanel contractPanel = new ListContractPanel(new CrudContext(this, breadcrumb)) {
			@Override
			protected Filter getInitialFilter() {
				return new And(
						new Equal("deleted", Boolean.FALSE), 
						new Equal("businessArea", CoreSession.get().getBusinessArea()), 
						new Equal("salesperson", CoreSession.get().getSalespersonRole()));
			}
		};
		add(contractPanel);
		
		ButtonGroup buttons = new ButtonGroup("buttons", Orientation.Horizontal) {
			@Override
			protected List<AbstractLink> newButtons(String buttonMarkupId) {
				List<AbstractLink> buttons = new ArrayList<>();
				
				buttons.add(new BootstrapAjaxLink<String>(ButtonList.getButtonMarkupId(), Buttons.Type.Default) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						ListContractPanel panel = new ListContractPanel(new CrudContext(ExistingContractsPanel.this, breadcrumb)) {
							@Override
							protected Filter getInitialFilter() {
								return new And(
										new Equal("deleted", Boolean.FALSE), 
										new Equal("businessArea", CoreSession.get().getBusinessArea()), 
										new Equal("salesperson", CoreSession.get().getSalespersonRole()));
							}
							
						};
						ExistingContractsPanel.this.addOrReplace(panel);
						target.add(panel);
					}
				}.setLabel(Model.of("Alle")));
				
				for (final ContractCategory contractCategory : salespersonRole.getContractCategories()) {
					buttons.add(new BootstrapAjaxLink<String>(ButtonList.getButtonMarkupId(), Buttons.Type.Default) {
						@Override
						public void onClick(AjaxRequestTarget target) {
							ListContractPanel panel = new ListContractPanel(new CrudContext(ExistingContractsPanel.this, breadcrumb)) {
								@Override
								protected Filter getInitialFilter() {
									return new And(
											new Equal("deleted", Boolean.FALSE), 
											new Equal("businessArea", CoreSession.get().getBusinessArea()), 
											new Equal("salesperson", CoreSession.get().getSalespersonRole()),
											new Equal("category", contractCategory));
								}
								
							};
							ExistingContractsPanel.this.addOrReplace(panel);
							target.add(panel);
						}
					}.setLabel(Model.of(contractCategory.getName())));
				}
				
				buttons.add(new BootstrapAjaxLink<String>(ButtonList.getButtonMarkupId(), Buttons.Type.Default) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						ListContractPanel panel = new ListContractPanel(new CrudContext(ExistingContractsPanel.this, breadcrumb)) {
							@Override
							protected Filter getInitialFilter() {
								return new And(
										new Equal("deleted", Boolean.FALSE), 
										new Equal("businessArea", CoreSession.get().getBusinessArea()), 
										new Equal("salesperson", CoreSession.get().getSalespersonRole()),
										new IsNull("category"));
							}
						};
						ExistingContractsPanel.this.addOrReplace(panel);
						target.add(panel);
					}
				}.setLabel(Model.of("Ikke kategoriseret")));
				
				return buttons;
			}
		};
		add(buttons);
		
	}
}
