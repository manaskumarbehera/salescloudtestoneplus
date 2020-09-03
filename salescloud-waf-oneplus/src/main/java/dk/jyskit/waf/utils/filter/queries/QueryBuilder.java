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

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dk.jyskit.waf.utils.filter.Filter;

public class QueryBuilder implements Serializable {

	private static ArrayList<FilterTranslator> filterTranslators = new ArrayList<FilterTranslator>();

	static {
		/* Register all default filter translators */
		addFilterTranslator(new AndTranslator());
		addFilterTranslator(new OrTranslator());
		addFilterTranslator(new LikeTranslator());
		addFilterTranslator(new BetweenTranslator());
		addFilterTranslator(new CompareTranslator());
		addFilterTranslator(new EqualTranslator());
		addFilterTranslator(new NotTranslator());
		addFilterTranslator(new IsNullTranslator());
//		addFilterTranslator(new SimpleStringTranslator());
	}

	public synchronized static void addFilterTranslator(FilterTranslator translator) {
		filterTranslators.add(translator);
	}

	public synchronized static Predicate getPredicateForFilter(Filter filter, Root entityRoot, CriteriaBuilder builder) {
		for (FilterTranslator ft : filterTranslators) {
			if (ft.translatesFilter(filter)) {
				return ft.getPredicateForFilter(filter, entityRoot, builder);
			}
		}
		return null;
	}
}
