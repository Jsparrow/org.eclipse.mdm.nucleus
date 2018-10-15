/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


package org.eclipse.mdm.businessobjects.control;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.mdm.api.base.query.Condition;
import org.eclipse.mdm.api.base.query.Filter;

/**
 * Filter builder.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchFilterBuilder {

	private static final String AND = "and";
	private static final String OR = "or";

	private List<Object> filterElements = new ArrayList<>();
	private List<Filter> filterList = new ArrayList<>();
	private String currentFilterType = null;
	private Filter currentFilter = null;

	/**
	 * Adds a condition
	 * 
	 * @param condition
	 */
	public void addCondition(Condition condition) {
		filterElements.add(condition);
	}

	/**
	 * Adds a and operator.
	 */
	public void addAnd() {
		filterElements.add(AND);
	}

	/**
	 * Adds an or operator.
	 */
	public void addOr() {
		filterElements.add(OR);
	}

	/**
	 * Creates the filter.
	 * 
	 * @return The created filter.
	 */
	public Filter build() {
		for (int i = 0; i < filterElements.size(); i++) {
			if (filterElements.get(i) instanceof Condition) {
				if (currentFilter == null) {
					createNextFilter(i);
				}
				if (filterElements.size() > (i + 1) && AND.equals(currentFilterType)
						&& OR.equals(filterElements.get(i + 1))) {
					filterList.add(currentFilter);
					currentFilter = Filter.or();
					currentFilterType = OR;
				}
				currentFilter.add((Condition) filterElements.get(i));
			} else {
				if (currentFilterType != filterElements.get(i)) {
					filterList.add(currentFilter);
					currentFilterType = (String) filterElements.get(i);
					currentFilter = null;
				}
			}
		}
		if (currentFilter != null) {
			filterList.add(currentFilter);
		}
		return Filter.and().merge(filterList);
	}

	/**
	 * Create the next filter.
	 * 
	 * @param index
	 *            The current filter element index.
	 * @return The created filter.
	 */
	private Filter createNextFilter(int index) {
		if (filterElements.size() > (index + 1)) {
			currentFilter = AND.equals((String) filterElements.get(index + 1)) ? Filter.and() : Filter.or();
			currentFilterType = (String) filterElements.get(index + 1);
		} else if (AND.equals(currentFilterType)) {
			currentFilter = Filter.and();
		} else {
			currentFilter = Filter.or();
		}
		return null;
	}

}
