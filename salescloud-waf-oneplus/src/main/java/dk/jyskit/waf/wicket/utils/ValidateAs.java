package dk.jyskit.waf.wicket.utils;

/* Copyright (c) 2011-2014 Pushing Inertia
 * All rights reserved.  http://pushinginertia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Provides some common validation logic.
 */
public final class ValidateAs {
	private ValidateAs() {}

	/**
	 * Validates that a variable is not null.
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null
	 * @param <T> type of the value to check
	 * @return the given value
	 * @throws IllegalArgumentException if the value is null
	 */
	public static <T> T notNull(final T value, final String name) throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException("Value for [" + name + "] cannot be null.");
		}
		return value;
	}

	/**
	 * Validates that a string is not null and not empty.
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null or empty
	 * @return the given value
	 * @throws IllegalArgumentException if the value is null or empty
	 */
	public static String notEmpty(final String value, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Value [" + value + "] for [" + name + "] cannot be empty.");
		}
		return value;
	}

	/**
	 * Validates that a string is not null and not empty when leading/trailing whitespace is trimmed from it.
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null or empty
	 * @return the given value with leading and trailing whitespace removed
	 * @throws IllegalArgumentException if the value is null or empty
	 */
	public static String trimmedNotEmpty(final String value, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		final String trimmedValue = value.trim();
		if (trimmedValue.isEmpty()) {
			throw new IllegalArgumentException("Value [" + value + "] for [" + name + "] cannot be empty when leading and trailing whitespace is removed.");
		}
		return trimmedValue;
	}

	/**
	 * Validates that two values are not equal to each other via their {@link Object#equals(Object)} methods.
	 * @param value1 first value to check
	 * @param value2 second value to check
	 * @param value1Name name of the value that will be echoed in the exception if the value is null
	 * @param value2Name name of the value that will be echoed in the exception if the value is null
	 * @param <T> type of the values to check
	 * @throws IllegalArgumentException if one of the values is null or {@link Object#equals(Object)} returns true
	 */
	public static <T> void notEqual(final T value1, final T value2, final String value1Name, final String value2Name)
	throws IllegalArgumentException {
		notNull(value1, value1Name);
		notNull(value2, value2Name);

		if (value1.equals(value2)) {
			throw new IllegalArgumentException("Values for [" + value1Name + "] and [" + value2Name + "] cannot be equal.");
		}
	}

	/**
	 * Validates that two values are equal to each other via their {@link T#equals(Object)} methods.
	 * @param value1 first value to check
	 * @param value2 second value to check
	 * @param value1Name name of the value that will be echoed in the exception if the value is null
	 * @param value2Name name of the value that will be echoed in the exception if the value is null
	 * @param <T> type of the values to check
	 * @throws IllegalArgumentException if one of the values is null or {@link T#equals(Object)} returns false
	 */
	public static <T> void equal(final T value1, final T value2, final String value1Name, final String value2Name)
	throws IllegalArgumentException {
		notNull(value1, value1Name);
		notNull(value2, value2Name);

		if (!value1.equals(value2)) {
			throw new IllegalArgumentException("Values for [" + value1Name + "] and [" + value2Name + "] must be equal.");
		}
	}

	/**
	 * Validates that a variable is a positive integer (i.e., greater than zero).
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null
	 * @return the given value
	 * @throws IllegalArgumentException if the value is less than one
	 */
	public static int positive(final int value, final String name) throws IllegalArgumentException {
		if (value <= 0) {
			throw new IllegalArgumentException("Value for [" + name + "] must be positive: " + value);
		}
		return value;
	}

	/**
	 * Validates that a variable is a positive long (i.e., greater than zero).
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null
	 * @return the given value
	 * @throws IllegalArgumentException if the value is less than one
	 */
	public static long positive(final long value, final String name) throws IllegalArgumentException {
		if (value <= 0) {
			throw new IllegalArgumentException("Value for [" + name + "] must be positive: " + value);
		}
		return value;
	}

	/**
	 * Validates that a variable is non-negative (i.e., zero or greater).
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null
	 * @return the given value
	 * @throws IllegalArgumentException if the value is less than zero
	 */
	public static int nonNegative(final int value, final String name) throws IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException("Value for [" + name + "] must be non-negative: " + value);
		}
		return value;
	}

	/**
	 * Validates that a variable is non-negative (i.e., zero or greater).
	 * @param value value to check
	 * @param name name of the value that will be echoed in the exception if the value is null
	 * @return the given value
	 * @throws IllegalArgumentException if the value is less than zero
	 */
	public static BigDecimal nonNegative(final BigDecimal value, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		if (value.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Value for [" + name + "] must be non-negative: " + value);
		}
		return value;
	}

	/**
	 * Validates that a {@link Map} does not contain a given key.
	 * @param map map to test
	 * @param key key that must not exist in the map
	 * @param <K> type of the key
	 * @throws IllegalArgumentException if the key exists in the map or either argument is null
	 */
	public static <K> void mapDoesNotContainKey(final Map<K, ?> map, final K key) throws IllegalArgumentException {
		ValidateAs.notNull(map, "map");
		ValidateAs.notNull(key, "key");
		if (map.containsKey(key)) {
			throw new IllegalArgumentException("Map already contains key [" + key + "]: " + map);
		}
	}

	/**
	 * Validates that a string is of a specific length.
	 * @param value string to test
	 * @param expectedLength expected length of the string
	 * @param name name of the value that will be echoed in the exception if validation fails
	 * @return the given value
	 * @throws IllegalArgumentException if the length doesn't match or the input is null
	 */
	public static String ofLength(final String value, final int expectedLength, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		if (value.length() != expectedLength) {
			throw new IllegalArgumentException("Value for [" + name + "] must be of length " + expectedLength + ": " + value);
		}
		return value;
	}

	public static String ofMinimumLength(final String value, final int minimumLength, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		if (value.length() < minimumLength) {
			throw new IllegalArgumentException("Value for [" + name + "] must have minimum length " + minimumLength + ": " + value);
		}
		return value;
	}

	/**
	 * Validates that a given string consists only of uppercase characters.
	 * @param value string to test
	 * @param name name of the value that will be echoed in the exception if validation fails
	 * @return the given value
	 * @throws IllegalArgumentException if the validation fails or the input is null
	 */
	public static String allUppercase(final String value, final String name) throws IllegalArgumentException {
		ValidateAs.notNull(value, name);
		for (char c: value.toCharArray()) {
			if (!Character.isUpperCase(c)) {
				throw new IllegalArgumentException("Value for [" + name + "] must be all uppercase: " + value);
			}
		}
		return value;
	}

	/**
	 * Validates that a given index falls within the bounds of a non-empty list.
	 * @param list List to test (cannot be null or empty).
	 * @param index Index to test.
	 * @param listName Name of the list that will be echoed in the exception if validation fails.
	 * @param <T> Type of the items in the list.
	 * @return The given index.
	 * @throws IllegalArgumentException If the validation fails or the list is null or empty.
	 */
	public static <T> int indexInList(final List<T> list, final int index, final String listName)
	throws IllegalArgumentException {
		ValidateAs.notNull(list, listName);
		if (list.size() == 0) {
			throw new IllegalArgumentException("List [" + listName + "] is empty.");
		}
		if (index < 0 || index >= list.size()) {
			throw new IllegalArgumentException(
					"Index " + index + " must be in the range of 0.." + (list.size() - 1) +
					" for lookups from list [" + listName + "].");
		}
		return index;
	}
}
