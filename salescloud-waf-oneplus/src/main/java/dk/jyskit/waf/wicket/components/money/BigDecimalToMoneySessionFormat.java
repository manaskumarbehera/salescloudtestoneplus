package dk.jyskit.waf.wicket.components.money;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BigDecimalToMoneySessionFormat {

	public static String format(BigDecimal decimal, Locale locale) {
		if (decimal == null) {
			return "";
		} else {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setGroupingUsed(false);
			decimalFormat.setMinimumFractionDigits(2);
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(locale));
			return decimalFormat.format(decimal);
		}
	}
}