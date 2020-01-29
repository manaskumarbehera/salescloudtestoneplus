package dk.jyskit.waf.wicket.components.money;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.wicket.util.convert.converter.BigDecimalConverter;

public class BigDecimalParser {

	public static BigDecimal parseBigDecimal(String s, Locale locale) {
		BigDecimalConverter converter = new BigDecimalConverter();
		BigDecimal bigDecimal = (BigDecimal) converter.convertToObject(s, locale);
		return bigDecimal;
	}
}
