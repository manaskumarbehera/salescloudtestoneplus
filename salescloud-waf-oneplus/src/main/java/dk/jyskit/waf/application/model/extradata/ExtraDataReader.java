package dk.jyskit.waf.application.model.extradata;

import java.util.*;

import org.apache.wicket.util.string.Strings;

import dk.jyskit.waf.application.dao.ExtraDataDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.guice.Lookup;

/**
 * Helps reading extra data values for an entity.
 * @author palfred
 *
 */
public class ExtraDataReader {
	private Map<String, ExtraData> data;
	private ExtraDataDefinitions dataDefinitions;

	public ExtraDataReader(BaseEntity entity) {
		data = Lookup.lookup(ExtraDataDao.class).findForEntity(entity);
		dataDefinitions = new ExtraDataDefinitions(entity.getClass());
	}

	public Set<String> keySet() {
		Set<String> keys = new TreeSet<>(data.keySet());
		keys.addAll(dataDefinitions.getDefinitions().keySet());
		return keys;
	}

	public String getString(String name, String defaultValue) {
		String result = null;
		ExtraData extraData = data.get(name);
		if (extraData != null) {
			result = extraData.getValue();
		}
		if (result == null) {
			ExtraDataDefinition def = dataDefinitions.getDefinitions().get(name);
			if (def != null) {
				result = def.getDefaultValue();
			}
		}
		return result;
	}

	public String[] getStringArray(String name,  String... defaultValue) {
		return toStringArray(getString(name, fromStringArray(defaultValue)));
	}

	public int getInt(String name,  int defaultValue) {
		return toInt(getString(name, fromInt(defaultValue)));
	}

	public int[] getIntArray(String name,  int... defaultValue) {
		return toIntArray(getString(name, fromIntArray(defaultValue)));
	}

	public double getDouble(String name,  double defaultValue) {
		return toDouble(getString(name, fromDouble(defaultValue)));
	}


	// converter helpers
	public static <T> T nvl(T value, T valueWhenNull) {
		return value == null ? valueWhenNull : value;
	}

	public static int toInt(String val) {
		return Integer.parseInt(val);
	}

	public static String fromInt(int val) {
		return Integer.toString(val);
	}

	public static double toDouble(String val) {
		return Double.parseDouble(val);
	}

	public static String fromDouble(double val) {
		return Double.toString(val);
	}


	public static int[] toIntArray(String val) {
		String[] split = toStringArray(val);
		int[] result = new int[split.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}
		return result;
	}

	public static String fromIntArray(int[] val) {
		List<String> vals = new ArrayList<>();
		for (int i : val) {
			vals.add(fromInt(i));
		}
		return Strings.join(",", vals);
	}

	public static String[] toStringArray(String val) {
		String[] split = Strings.split(val, ',');
		return split;
	}

	public static String fromStringArray(String[] val) {
		return Strings.join(",", val);
	}

}
