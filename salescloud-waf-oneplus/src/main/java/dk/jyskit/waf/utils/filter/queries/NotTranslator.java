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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.utils.filter.Not;

public class NotTranslator extends AbstractTranslator {

    @Override
    public boolean translatesFilter(Filter filter) {
        return filter instanceof Not;
    }

    @Override
    public Predicate getPredicateForFilter(Filter filter, Root entityRoot, CriteriaBuilder builder) {
        Not not = (Not) filter;
//        if (not.getFilter() instanceof IsNull) {
//            IsNull isNull = (IsNull) not.getFilter();
//            return builder.isNotNull(entityRoot.get(isNull.getPropertyName()));
//        }
        return QueryBuilder.getPredicateForFilter(not.getFilter(), entityRoot, builder).not();
    }
}
