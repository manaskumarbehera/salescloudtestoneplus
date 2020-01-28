package dk.jyskit.salescloud.application.pages.accessnew.locations;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.accessnew.adsl.AdslPanel;
import dk.jyskit.salescloud.application.pages.accessnew.basiclocation.BasicLocationPanel;
import dk.jyskit.salescloud.application.pages.accessnew.fiber.FiberErhvervPanel;
import dk.jyskit.salescloud.application.pages.accessnew.fiber.FiberErhvervPlusPanel;
import dk.jyskit.salescloud.application.pages.accessnew.locationaccess.AbstractLocationAccessPanel;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class LocationsPanel extends Panel {
	@Inject
	private PageNavigator navigator;

	@Inject
	private MobileContractDao contractDao;

	@Inject
	private ContractSaver contractSaver;

//	public Tab0Bean tab0 		= new Tab0Bean();
//	public ValueMap valueMap1 	= new ValueMap();
//	public ValueMap valueMap2 	= new ValueMap();
//	public ValueMap valueMap3 	= new ValueMap();

	private AjaxBootstrapTabbedPanel<ITab> tabbedPanel;
//	private List<AbstractLocationAccessPanel> panels = new ArrayList<>();
	private Map<Integer, AbstractLocationAccessPanel> panels = new HashMap<>();

	private Form<Contract> form;

	public LocationsPanel(String wicketId) {
		super(wicketId);

		List<ITab> tabs = new ArrayList();

		AjaxLink<Void> addNoAccessLink = new AjaxLink<Void>("addNoAccess") {
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = MobileSession.get().getContract();
				contract.addLocation(AccessTypeEnum.NONE);
				save(target);

				form.replace(updateTabs(contract, tabs));

				tabbedPanel.setSelectedTab(contract.getLocationBundles().size()-1);
				target.add(form);
			}
		};
		this.add(addNoAccessLink);
		addNoAccessLink.add(AttributeModifier.append("style", "margin-bottom: 20px"));

		AjaxLink<Void> addFiberLink = new AjaxLink<Void>("addFiber") {
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = MobileSession.get().getContract();
				contract.addLocation(AccessTypeEnum.FIBER);

				List<FiberErhvervBundleData> fiberErhvervBundles = contract.getFiberErhvervBundles();
				fiberErhvervBundles.add(new FiberErhvervBundleData());
				contract.setFiberErhvervBundles(fiberErhvervBundles);

				save(target);

				form.replace(updateTabs(contract, tabs));

				tabbedPanel.setSelectedTab(contract.getLocationBundles().size()-1);
				target.add(form);
			}
		};
		this.add(addFiberLink);
		addFiberLink.setEnabled(false);
		addFiberLink.setVisible(false);
		addFiberLink.add(AttributeModifier.append("style", "margin-bottom: 20px"));

		AjaxLink<Void> addFiberPlusLink = new AjaxLink<Void>("addFiberPlus") {
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = MobileSession.get().getContract();
				contract.addLocation(AccessTypeEnum.FIBER_PLUS);

				List<FiberErhvervPlusBundleData> fiberErhvervPlusBundles = contract.getFiberErhvervPlusBundles();
				fiberErhvervPlusBundles.add(new FiberErhvervPlusBundleData());
				contract.setFiberErhvervPlusBundles(fiberErhvervPlusBundles);

				save(target);

				form.replace(updateTabs(contract, tabs));

				tabbedPanel.setSelectedTab(contract.getLocationBundles().size()-1);
				target.add(form);
			}
		};
		this.add(addFiberPlusLink);
		addFiberPlusLink.setEnabled(false);
		addFiberPlusLink.setVisible(false);
		addFiberPlusLink.add(AttributeModifier.append("style", "margin-bottom: 20px"));

		AjaxLink<Void> addXdslLink = new AjaxLink<Void>("addXdsl") {
			public void onClick(AjaxRequestTarget target) {
				MobileContract contract = MobileSession.get().getContract();
				contract.addLocation(AccessTypeEnum.XDSL);
				save(target);

				form.replace(updateTabs(contract, tabs));

				tabbedPanel.setSelectedTab(contract.getLocationBundles().size()-1);
				target.add(form);
			}
		};
		this.add(addXdslLink);
		addXdslLink.add(AttributeModifier.append("style", "margin-bottom: 20px"));


		form = new Form<>("form");
		form.setOutputMarkupId(true);
		add(form);

//		form.add(new FencedFeedbackPanel("feedback"));

		MobileContract contract = (MobileContract) MobileSession.get().getContract();

//		String[] titles = new String[] {"Indstillinger", "Øvrige installationsydelser", "Hardware til rate", "Sammenfatning partner"};

		form.add(updateTabs(contract, tabs));

		AjaxButton prevButton = new AjaxButton("prevButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (save(target)) {
					if (tabbedPanel.getSelectedTab() == 0) {
						navigate(false);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()-1);
//						((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
						target.add(form);
					}
				} else {
					target.add(form);
				}
			}
		};
		prevButton.setOutputMarkupId(true);
		form.add(prevButton);

		AjaxButton nextButton = new AjaxButton("nextButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (save(target)) {
					if (tabbedPanel.getSelectedTab() == tabbedPanel.getTabs().size()-1) {
						navigate(true);
					} else {
						tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab()+1);
//						((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(tabbedPanel.getSelectedTab()));
						target.add(form);
					}
				} else {
					target.add(form);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				log.warn("Error - validation (min/max)? - set breakpoint in anyFormComponentError");
			}
		};
		nextButton.setOutputMarkupId(true);
		form.add(nextButton);
	}

	private AjaxBootstrapTabbedPanel<ITab> updateTabs(MobileContract contract, List<ITab> tabs) {
		int i = 0;
		tabs.clear();
		for (LocationBundleData bundle : contract.getLocationBundles()) {
			final int tabIndex = i++;
			tabs.add(new AbstractTab(new Model<String>(StringUtils.isEmpty(bundle.getAddressRoad()) ? "<ny>" : bundle.getAddressRoad())) {
				public Panel getPanel(String panelId) {
					return new AjaxLazyLoadPanel(panelId) {
						@Override
						public Component getLazyLoadComponent(String markupId) {
							if (bundle.getAccessType() == AccessTypeEnum.FIBER.getId()) {
								Panel panel = new FiberErhvervPanel(markupId, tabIndex);
								panels.put(tabIndex, (AbstractLocationAccessPanel) panel);
								return panel;
							} else if (bundle.getAccessType() == AccessTypeEnum.FIBER_PLUS.getId()) {
								Panel panel = new FiberErhvervPlusPanel(markupId, tabIndex);
								panels.put(tabIndex, (AbstractLocationAccessPanel) panel);
								return panel;
							} else if (bundle.getAccessType() == AccessTypeEnum.NONE.getId()) {
								Panel panel = new BasicLocationPanel(markupId, tabIndex);
								panels.put(tabIndex, (AbstractLocationAccessPanel) panel);
								return panel;
							} else if (bundle.getAccessType() == AccessTypeEnum.XDSL.getId()) {
								Panel panel = new AdslPanel(markupId, tabIndex);
								panels.put(tabIndex, (AbstractLocationAccessPanel) panel);
								return panel;
							}
							return new FiberErhvervPanel(markupId, tabIndex);
						}
					};

				}
			});
		}
		tabbedPanel = new AjaxBootstrapTabbedPanel<ITab>("tabs", tabs) {
			@Override
			protected WebMarkupContainer newLink(String linkId, final int index) {
				return new AjaxSubmitLink(linkId, form) {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						if (save(target)) {
							setSelectedTab(index);
							//	((ProductSelectionPage) getPage()).updateHelp(target, tabIndexToEntityId.get(getSelectedTab()));
							if (target != null) {
								target.add(form);
							}
						} else {
							if (target != null) {
								target.add(form);
							}
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						log.error("There is a problem");
					}
				};
			}

			@Override
			protected void onAjaxUpdate(AjaxRequestTarget target) {
				super.onAjaxUpdate(target);
			}
		};
		return tabbedPanel;
	}

	private boolean save(AjaxRequestTarget target) {
		MobileContract contract = (MobileContract) CoreSession.get().getContract();

		if (tabbedPanel.getSelectedTab() > -1) {
			Panel panel = panels.get(tabbedPanel.getSelectedTab());
			if ((panel != null) && (panel instanceof AbstractLocationAccessPanel)) {
				if (!((AbstractLocationAccessPanel) panel).saveAndNavigate(contract, null, tabbedPanel.getSelectedTab(), target)) {
					return false;
				}
			}
		}

		contractSaver.save(contract);
		target.add(form);
		return true;
	}

	private void navigate(boolean goToNext) {
		if (goToNext) {
			setResponsePage(navigator.next(getWebPage()));
		} else {
			setResponsePage(navigator.prev(getWebPage()));
		}
	}
}
