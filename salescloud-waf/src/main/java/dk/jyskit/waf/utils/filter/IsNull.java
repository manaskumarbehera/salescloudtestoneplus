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


/**
 * Simple container filter checking whether an item property value is null.
 * 
 * This filter also directly supports in-memory filtering.
 * 
 * @since 6.6
 */
public final class IsNull extends AbstractPropertyFilter {

    /**
     * Constructor for a filter that compares the value of an item property with
     * null.
     * 
     * For in-memory filtering, a simple == check is performed. For other
     * containers, the comparison implementation is container dependent but
     * should correspond to the in-memory null check.
     * 
     * @param propertyName
     *            the identifier (not null) of the property whose value to check
     */
    public IsNull(String propertyName) {
  		super(propertyName);

    }

    @Override
    public boolean passesFilter(Object entity)
            throws UnsupportedOperationException {
        return null == getPropertyValue(entity);
    }

    @Override
    public boolean equals(Object obj) {
        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final IsNull o = (IsNull) obj;

        // Checks the properties one by one
        return (null != getPropertyName()) ? getPropertyName().equals(
                o.getPropertyName()) : null == o.getPropertyName();
    }

    @Override
    public int hashCode() {
        return (null != getPropertyName() ? getPropertyName().hashCode() : 0);
    }

}
