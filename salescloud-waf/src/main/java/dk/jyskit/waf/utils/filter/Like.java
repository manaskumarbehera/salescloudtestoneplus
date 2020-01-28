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


public class Like extends AbstractPropertyFilter {
    private final String value;
    private boolean caseSensitive;

    public Like(String propertyName, String value) {
        this(propertyName, value, true);
    }

    public Like(String propertyName, String value, boolean caseSensitive) {
  		super(propertyName);
        this.value = value;
        setCaseSensitive(caseSensitive);
    }

    public String getValue() {
        return value;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    @Override
    public boolean passesFilter(Object entity)
            throws UnsupportedOperationException {
        Object propertyValue = getPropertyValue(entity);
		if (propertyValue != null && !propertyValue.getClass().isAssignableFrom(String.class)) {
            // We can only handle strings
            return false;
        }
        String colValue = "" + propertyValue;

        String pattern = ".*" + getValue().replace("%", ".*") + ".*";
        if (isCaseSensitive()) {
            return colValue.matches(pattern);
        }
        return colValue.toUpperCase().matches(pattern.toUpperCase());
    }

    @Override
    public int hashCode() {
        return getPropertyName().hashCode() + getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final Like o = (Like) obj;

        // Checks the properties one by one
        boolean propertyNameEqual = (null != getPropertyName()) ? getPropertyName()
                .equals(o.getPropertyName()) : null == o.getPropertyName();
        boolean valueEqual = (null != getValue()) ? getValue().equals(
                o.getValue()) : null == o.getValue();
        return propertyNameEqual && valueEqual;
    }
}
