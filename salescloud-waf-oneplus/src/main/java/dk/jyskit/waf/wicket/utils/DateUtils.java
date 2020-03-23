package dk.jyskit.waf.wicket.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.protocol.http.WebSession;

public class DateUtils {

	/**
	 * Check http://sproget.dk/raad-og-regler/artikler-mv/svarbase/SV00000046 for details 
	 * about Danish format.
	 * 
	 * @param style
	 * @return
	 */
	public static SimpleDateFormat getDateFormat(int style) {
		Locale locale = WebSession.get().getLocale();
		if (locale.getLanguage().equals("da")) {
			switch (style) {
			case DateFormat.FULL:
				return new SimpleDateFormat("EEE, dd MMM yyyy");
			case DateFormat.LONG:
				return new SimpleDateFormat("d. MMM yyyy");
			case DateFormat.SHORT:
				return new SimpleDateFormat("d/M");
			default:
				return new SimpleDateFormat("d/M yyyy");
			}
		} else {
			return (SimpleDateFormat) DateFormat.getDateInstance(style, locale);
		}
	}

	/**
	 * @param style
	 * @return
	 */
	public static SimpleDateFormat getTimeFormat(int style) {
		Locale locale = WebSession.get().getLocale();
		if (locale.getLanguage().equals("da")) {
			switch (style) {
			case DateFormat.FULL:
				return new SimpleDateFormat("HH:mm:ss");
			case DateFormat.LONG:
				return new SimpleDateFormat("HH:mm:ss");
			case DateFormat.SHORT:
				return new SimpleDateFormat("HH:mm");
			default:
				return new SimpleDateFormat("HH:mm:ss");
			}
		} else {
			return (SimpleDateFormat) DateFormat.getTimeInstance(style, locale);
		}
	}

	/**
	 * @param style
	 * @return
	 */
	public static SimpleDateFormat getDateTimeFormat(int style) {
		Locale locale = WebSession.get().getLocale();
		if (locale.getLanguage().equals("da")) {
			switch (style) {
			case DateFormat.FULL:
				return new SimpleDateFormat("EEE, dd MMM yyyy - HH:mm:ss");
			case DateFormat.LONG:
				return new SimpleDateFormat("d. MMM yyyy - HH:mm:ss");
			case DateFormat.SHORT:
				return new SimpleDateFormat("d/M - HH:mm");
			default:
				return new SimpleDateFormat("d/MM yyyy - HH:mm:ss");
			}
		} else {
			return (SimpleDateFormat) DateFormat.getDateTimeInstance(style, style, locale);
		}
	}

	public static int getMonthNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static int getYearNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.YEAR);
	}
}
