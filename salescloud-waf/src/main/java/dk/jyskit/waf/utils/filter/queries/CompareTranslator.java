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
package dk.jyskit.waf.utils.filter.queries;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dk.jyskit.waf.utils.filter.Compare;
import dk.jyskit.waf.utils.filter.Filter;

public class CompareTranslator extends AbstractTranslator {

    @Override
    public boolean translatesFilter(Filter filter) {
        return filter instanceof Compare;
    }

    @Override
    public Predicate getPredicateForFilter(Filter filter, Root entityRoot, CriteriaBuilder builder) {
        Compare compare = (Compare) filter;
        Path path = getPath(entityRoot, compare.getPropertyName());
        switch (compare.getOperation()) {
        case GREATER:
            return builder.gt(path, compare.getValue());
        case GREATER_OR_EQUAL:
            return builder.ge(path, compare.getValue());
        case LESS:
            return builder.lt(path, compare.getValue());
        case LESS_OR_EQUAL:
            return builder.le(path, compare.getValue());
        default:
            return null;
        }
    }
}
