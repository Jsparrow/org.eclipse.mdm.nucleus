/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.mdm.freetextindexer.entities;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.junit.Ignore;
import org.junit.Test;

public class MDMEntityResponseTest {

	@Ignore
	@Test
	public void notContextDescribale_noContext() {
		Quantity q = mock(Quantity.class);
		EntityManager manager = mock(EntityManager.class);

		MDMEntityResponse response = MDMEntityResponse.build(Quantity.class, q, manager);

		assertTrue(response.data.attributes.isEmpty());
	}

	@Ignore
	@Test
	public void buildFails_nullIsReturned() throws DataAccessException {
		TestStep ts = mock(TestStep.class);
		EntityManager manager = mock(EntityManager.class);
		when(manager.loadContexts(eq(ts), any())).thenThrow(new DataAccessException("test"));

		assertNull(MDMEntityResponse.build(TestStep.class, ts, manager));
	}
}
