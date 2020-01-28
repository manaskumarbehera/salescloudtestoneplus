package dk.jyskit.waf.wicket.utils;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.string.Strings;

/**
 * Enum converter based on localized values.
 * Enums are localized by the key [simple class name].[value name]. E.g. an
 * 
 * <pre>
 *  enum SizeType {S, M, L}
 *  SizeType.S=Small
 *  SizeType.M=Medium
 *  SizeType.L=Large
 *  Or in Danish
 *  SizeType.S=Lille
 *  SizeType.M=Mellem
 *  SizeType.L=Stor
 * </pre>
 *
 * @author palfred
 *
 * @param <T>
 */
public class EnumConverter<T extends Enum<T>> implements IConverter<T> {
	private final Class<T> clazz;

	public static <C extends Enum<C>> EnumConverter<C> forEnum(Class<C> clazz) {
		return new EnumConverter<C>(clazz);
	}

	public EnumConverter(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T convertToObject(String value, Locale locale) {
		if (Strings.isEmpty(value)) {
			return null;
		}
		for (T enumValue : clazz.getEnumConstants()) {
			if (value.equalsIgnoreCase(convertToString(enumValue, locale))) {
				return enumValue;
			}
			if (value.equalsIgnoreCase(enumValue.name())) {
				return enumValue;
			}
		}
		// conversion exception ??
		return null;
	}

	@Override
	public String convertToString(T value, Locale locale) {
		if (value == null) {
			return null;
		}
		return WicketUtils.getLocalized(resourceKey(value), value.name());
	}

	protected String resourceKey(T value)	{
		return Classes.simpleName(clazz) + '.' + value.name();
	}
}
