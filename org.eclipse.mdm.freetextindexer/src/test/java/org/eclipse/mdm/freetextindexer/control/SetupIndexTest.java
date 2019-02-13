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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.eclipse.mdm.freetextindexer.events.CreateIndex;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SetupIndexTest {

    private SetupIndex setup;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        setup = new SetupIndex();
        setup.esBoundary = mock(ElasticsearchBoundary.class);
        setup.apiBoundary = mock(MdmApiBoundary.class);
        setup.createIndexEvent = mock(Event.class);
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

        verify(setup.createIndexEvent, times(1)).fire(any(CreateIndex.class));
    }
}
