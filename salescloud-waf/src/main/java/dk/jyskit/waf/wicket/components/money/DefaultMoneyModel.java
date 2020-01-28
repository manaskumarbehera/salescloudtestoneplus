package dk.jyskit.waf.wicket.components.money;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;

public class DefaultMoneyModel implements IModel {

	private static final long serialVersionUID = 1479000955482442517L;
	private final IModel<BigDecimal> bigDecimalModel;

	public DefaultMoneyModel(IModel nestedModel) {
		bigDecimalModel = nestedModel;
	}

	public Object getObject() {
		// convert BigDecimal to String
		return BigDecimalToMoneySessionFormat.format(bigDecimalModel.getObject(), getSessionLocale());
	}

	public void setObject(Object object) {
		// convert String to Bigdecimal
		bigDecimalModel.setObject(BigDecimalParser.parseBigDecimal((String) object, getSessionLocale()));
	}

	public void detach() {
		bigDecimalModel.detach();
	}

	private Locale getSessionLocale() {
		return Session.get().getLocale();
	}
}