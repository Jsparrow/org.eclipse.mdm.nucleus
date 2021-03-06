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

package org.eclipse.mdm.query.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.query.entity.Column;
import org.eclipse.mdm.query.entity.Row;

import com.google.common.base.Strings;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
public final class Util {

	private Util() {

	}

	public static List<Row> convertResultList(Collection<Result> results, Class<? extends Entity> resultEntityClass,
			EntityType type) {
		List<Row> rows = new ArrayList<>();
		results.forEach(row -> rows.add(convertResult(row, resultEntityClass, type)));
		return rows;
	}

	public static Row convertResult(Result result, Class<? extends Entity> resultEntityClass, EntityType type) {
		Row row = new Row();
		row.setSource(type.getSourceName());
		row.setType(resultEntityClass.getSimpleName());
		row.setId(result.getRecord(type).getID());
		result.forEach(record -> row.addColumns(convertRecord(record)));
		return row;
	}

	public static List<Column> convertRecord(Record record) {
		List<Column> columns = new ArrayList<>();
		record.getValues().values().forEach(value -> columns.add(convertColumn(record, value)));
		return columns;
	}

	public static Column convertColumn(Record record, Value value) {
		return new Column(ServiceUtils.workaroundForTypeMapping(record.getEntityType()), value.getName(),
				Strings.emptyToNull(Objects.toString(value.extract())), Strings.emptyToNull(value.getUnit()));
	}

}
