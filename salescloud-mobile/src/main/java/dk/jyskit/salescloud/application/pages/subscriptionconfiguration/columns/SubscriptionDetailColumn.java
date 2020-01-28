package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.model.Subscription;

public abstract class SubscriptionDetailColumn<T> implements IExportableColumn<Subscription, String, T> {
	
	@Override
	public String getSortProperty() {
		return null;
	}
	
	@Override
	public boolean isSortable() {
		return false;
	}
	
	@Override
	public void detach() {
		if (getDisplayModel() != null) {
			getDisplayModel().detach();
		}
	}
	
	@Override
	public IModel<T> getDataModel(IModel<Subscription> rowModel) {
		return null;
	}
	
}


