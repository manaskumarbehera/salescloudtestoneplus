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


public class Between extends AbstractPropertyFilter {

	private final Number startValue;
	private final Number endValue;

	public Between(String propertyName, Number startValue, Number endValue) {
		super(propertyName);
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public Number getStartValue() {
		return startValue;
	}

	public Number getEndValue() {
		return endValue;
	}

	@Override
	public boolean passesFilter(Object entity) throws UnsupportedOperationException {
		Object value = getPropertyValue(entity);
		if (value instanceof Number) {
			Number cval = (Number) value;
			return Double.valueOf(cval.doubleValue()).compareTo(getStartValue().doubleValue()) >= 0 && Double.valueOf(cval.doubleValue()).compareTo(getEndValue().doubleValue()) <= 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getPropertyName().hashCode() + getStartValue().hashCode() + getEndValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// Only objects of the same class can be equal
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		final Between o = (Between) obj;

		// Checks the properties one by one
		boolean propertyNameEqual = (null != getPropertyName()) ? getPropertyName().equals(o.getPropertyName())
				: null == o.getPropertyName();
		boolean startValueEqual = (null != getStartValue()) ? getStartValue().equals(o.getStartValue()) : null == o
				.getStartValue();
		boolean endValueEqual = (null != getEndValue()) ? getEndValue().equals(o.getEndValue()) : null == o
				.getEndValue();
		return propertyNameEqual && startValueEqual && endValueEqual;

	}
}
