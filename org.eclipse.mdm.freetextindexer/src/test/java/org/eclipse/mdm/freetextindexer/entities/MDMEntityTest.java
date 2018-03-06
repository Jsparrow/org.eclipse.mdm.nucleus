/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.mdm.freetextindexer.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.junit.Test;

public class MDMEntityTest {

	@Test
	public void notIndexedFields_NotIndexed() {
		MDMEntity entity = buildEntity("SortIndex", true);

		assertTrue(entity.attributes.isEmpty());
	}

	@Test
	public void invalidFields_NotIndexed() {
		MDMEntity entity = buildEntity("indexed", false);

		assertTrue(entity.attributes.isEmpty());
	}

	@Test
	public void validField_Indexed() {
		MDMEntity entity = buildEntity("indexed", true);

		assertFalse(entity.attributes.isEmpty());
	}

	private MDMEntity buildEntity(String name, boolean valid) {
		Value value = ValueType.INTEGER.create(name, 123);
		value.setValid(valid);
		Map<String, Value> map = new HashMap<>();
		map.put(value.getName(), value);

		MDMEntity entity = new MDMEntity("name", "", "1", map);
		return entity;
	}
}
