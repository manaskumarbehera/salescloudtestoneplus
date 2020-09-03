package dk.jyskit.waf.wicket.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.waf.application.model.EntityState;

@Data
public class CrudContext implements Serializable {
	private String panelWicketId = "panel";
	private Breadcrumb breadcrumb;
	private MarkupContainer rootMarkupContainer;
	private String namespace;
	private Map<String, int[]> namespaceToEntityStates = new HashMap<>();
	
	public CrudContext(MarkupContainer rootMarkupContainer, Breadcrumb breadcrumb) {
		this.rootMarkupContainer = rootMarkupContainer;
		this.breadcrumb = breadcrumb;
		if (breadcrumb != null) {
			breadcrumb.setOutputMarkupId(true);
		}
	}

	public void addToBreadCrumb(IBreadCrumbParticipant panel) {
		if ((breadcrumb != null) && (breadcrumb.getActive() == null || !panel.getClass().equals(breadcrumb.getActive().getClass()))) {
			breadcrumb.setActive(panel);
		}
	}
	
	public CrudContext clone() {
		CrudContext newContext = new CrudContext(rootMarkupContainer, breadcrumb);
		// Note: namespace is not included. It must be set after cloning.
		return newContext;
	}
	
	public CrudContext cloneWithIdAndNamespace(final String wicketId, String namespace) {
		CrudContext newContext = new CrudContext(rootMarkupContainer, breadcrumb) {
			@Override
			public String getPanelWicketId() {
				return wicketId;
			}
		};
		newContext.setNamespace(namespace);
		return newContext;
	}
	
	public CrudContext cloneWithNamespace(String namespace) {
		CrudContext newContext = new CrudContext(rootMarkupContainer, breadcrumb) {
			@Override
			public String getPanelWicketId() {
				return CrudContext.this.getPanelWicketId();
			}
		};
		newContext.setNamespace(namespace);
		return newContext;
	}
	
	public void setEntityStates(int ... states) {
		namespaceToEntityStates.put(namespace, states);
	}
	
	public int[] getEntityStates() {
		return namespaceToEntityStates.get(namespace);
	}
	
	public List<EntityState> getEntityStatesAsList() {
		List<EntityState> states = new ArrayList<>();
		int[] statesArr = namespaceToEntityStates.get(namespace);
		if (statesArr != null) {
			for (int state : statesArr) {
				states.add(new EntityState(state));
			}
		}
		return states;
	}
}
