package dk.jyskit.waf.application.model.extradata;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public enum ExtraDataType {
	STRING("regex", "values") {
		@Override
		public boolean isValid(String value, Properties properties) {
			if (value == null) {
				return false;
			}
			String regex = properties.getProperty("regex");
			if (regex != null) {
				if (!value.matches(regex)) {
					return false;
				}
			}
			if (!isInValues(value, properties)) {
				return false;
			}
			return true;
		}
	},
	INTEGER("min", "max", "values") {
		@Override
		public boolean isValid(String value, Properties properties) {
			if (value == null) {
				return false;
			}
			try {
				int nativeValue = Integer.parseInt(value);
				if (!isInValues(value, properties)) {
					return false;
				}
				String min = properties.getProperty("min");
				if (min != null) {
					int nativeMin = Integer.parseInt(min);
					if (nativeMin > nativeValue) {
						return false;
					}
				}
				String max = properties.getProperty("max");
				if (max != null) {
					int nativeMax = Integer.parseInt(max);
					if (nativeMax < nativeValue) {
						return false;
					}
				}
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	},
	FLOAT("min", "max", "values") {
		@Override
		public boolean isValid(String value, Properties properties) {
			if (value == null) {
				return false;
			}
			try {
				double nativeValue = Double.parseDouble(value);
				if (!isInValues(value, properties)) {
					return false;
				}
				String min = properties.getProperty("min");
				if (min != null) {
					double nativeMin = Double.parseDouble(min);
					if (nativeMin > nativeValue) {
						return false;
					}
				}
				String max = properties.getProperty("max");
				if (max != null) {
					double nativeMax = Double.parseDouble(max);
					if (nativeMax < nativeValue) {
						return false;
					}
				}
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	},
	/**
	 * Boolean any valid value is considered true, any other considered false.
	 * The true values can be configured by the "TrueValues" property. If not
	 * defined "true" is the one.
	 */
	BOOLEAN("trueValues") {
		@Override
		public boolean isValid(String value, Properties properties) {
			if (value == null) {
				return false;
			}
			String trueValues = properties.getProperty("trueValues");
			if (trueValues != null) {
				for (String val : trueValues.split(",")) {
					if (value.equalsIgnoreCase(val.trim())) {
						return true;
					}
				}
				return false;
			} else {
				return Boolean.parseBoolean(value);
			}
		}
	};
	private final String[] supportedAttributes;

	private ExtraDataType(String... supportedAttributes) {
		this.supportedAttributes = supportedAttributes;
	}

	public abstract boolean isValid(String value, Properties properties);

	public List<String> getValues(Properties properties) {
		String values = properties.getProperty("values");
		if (values == null) {
			return null;
		}
		return Arrays.asList(values.split(","));
	}

	protected boolean isInValues(String value, Properties properties) {
		String values = properties.getProperty("values");
		if (values == null) {
			// No values constraint.
			return true;
		}
		for (String val : values.split(",")) {
			if (value.equals(val.trim())) {
				return true;
			}
		}
		return false;
	}

}
