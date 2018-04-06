/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.mdm.freetextindexer.control;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SetupIndexTest {

	private SetupIndex setup;

	@Before
	public void init() {
		setup = new SetupIndex();
		setup.esBoundary = mock(ElasticsearchBoundary.class);
		setup.apiBoundary = mock(MdmApiBoundary.class);
	}

	@Test
	public void hasAlreadyIndex_doNothing() {
		when(setup.esBoundary.hasIndex(any(String.class))).thenReturn(true);

		setup.createIndexIfNeccessary();

		verify(setup.esBoundary, times(0)).createIndex(any(String.class));
	}

	@Test
	public void noIndex_created() {
		ApplicationContext c = mock(ApplicationContext.class);
		when(setup.esBoundary.hasIndex(any(String.class))).thenReturn(false);
		when(setup.apiBoundary.getContexts()).thenReturn(ImmutableMap.of("MDM", c));
		
		setup.createIndexIfNeccessary();

		verify(setup.esBoundary, times(1)).createIndex(any(String.class));
	}
}
