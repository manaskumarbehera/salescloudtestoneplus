/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package dk.jyskit.waf.utils.filter;


public class Equal extends AbstractPropertyFilter {

    private final Object value;

    /**
     * Construct a filter that accepts items for which the identified
     * property value is equal to <code>value</code>.
     * 
     * For in-memory filters, equals() is used for the comparison. For other
     * containers, the comparison implementation is container dependent and
     * may use e.g. database comparison operations.
     * 
     * @param propertyName
     *            the identifier of the property whose value to compare
     *            against value, not null
     * @param value
     *            the value to compare against - null values may or may not
     *            be supported depending on the container
     */
    public Equal(String propertyName, Object value) {
  		super(propertyName);

        this.value = value;
    }

    @Override
    public boolean passesFilter(Object entity) {
    	Object value = getPropertyValue(entity);
        return compareEquals(value);
    }

    /**
     * Checks if the this value equals the given value. Favors Comparable over
     * equals to better support e.g. BigDecimal where equals is stricter than
     * compareTo.
     * 
     * @param otherValue
     *            The value to compare to
     * @return true if the values are equal, false otherwise
     */
    private boolean compareEquals(Object otherValue) {
        if (value == null || otherValue == null) {
            return (otherValue == value);
        } else if (value == otherValue) {
            return true;
        } else if (value instanceof Comparable
                && otherValue.getClass()
                        .isAssignableFrom(getValue().getClass())) {
            return ((Comparable) value).compareTo(otherValue) == 0;
        } else {
            return value.equals(otherValue);
        }
    }

    @Override
    public boolean equals(Object obj) {
        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final Equal o = (Equal) obj;

        // Checks the properties one by one
        if (getPropertyName() != o.getPropertyName() && null != o.getPropertyName()
                && !o.getPropertyName().equals(getPropertyName())) {
            return false;
        }
        return (null == getValue()) ? null == o.getValue() : getValue().equals(
                o.getValue());
    }

    @Override
    public int hashCode() {
        return (null != getPropertyName() ? getPropertyName().hashCode() : 0)
                ^ (null != getValue() ? getValue().hashCode() : 0);
    }

    /**
     * Returns the value to compare the property against.
     * 
     * @return comparison reference value
     */
    public Object getValue() {
        return value;
    }
}
