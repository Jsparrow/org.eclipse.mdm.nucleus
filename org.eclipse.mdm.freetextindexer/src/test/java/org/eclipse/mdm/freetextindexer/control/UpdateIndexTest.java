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

package org.eclipse.mdm.freetextindexer.control;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.junit.Before;
import org.junit.Test;

public class UpdateIndexTest {

	private UpdateIndex update;

	@Before
	public void init() {
		update = new UpdateIndex();
		update.esBoundary = mock(ElasticsearchBoundary.class);
	}

	@Test
	public void nullGiven_notUpdated() {
		update.change(null);

		verify(update.esBoundary, never()).index(any(MDMEntityResponse.class));
	}

	@Test
	public void validDoc_Indexed() {
		MDMEntityResponse response = mock(MDMEntityResponse.class);
		update.change(response);

		verify(update.esBoundary, times(1)).index(eq(response));
	}

	@Test
	public void validDoc_deleted() {
		update.delete("api", "TestStep", "123");

		verify(update.esBoundary, times(1)).delete(eq("api"), eq("TestStep"), eq("123"));
	}
}
