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

package org.eclipse.mdm.businessobjects.boundary;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class ProjectServiceTest {

	ApplicationContext context = Mockito.mock(ApplicationContext.class);
	EntityManager em = Mockito.mock(EntityManager.class);
	ConnectorService connectorService = Mockito.mock(ConnectorService.class);
	SearchActivity searchActivity = Mockito.mock(SearchActivity.class);
	NavigationActivity navigationActivity = Mockito.mock(NavigationActivity.class);
	I18NActivity i18nActivity = Mockito.mock(I18NActivity.class);

	ProjectService service = new ProjectService(connectorService, searchActivity, i18nActivity);

	@Before
	public void init() {
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		when(connectorService.getContextByName("MDMTEST")).thenReturn(context);
	}

	@Test
	public void testGetProject() {
		service.getProject("MDMTEST", "1");

		verify(em).load(Project.class, "1");
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetProjectsEmptyFilter() {
		service.getProjects("MDMTEST", "");

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetProjectsNullFilter() {
		service.getProjects("MDMTEST", null);

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test(expected = MDMEntityAccessException.class)
	public void testGetProjectsWrongEnvironment() {
		doThrow(MDMEntityAccessException.class).when(connectorService).getContextByName("wrongEnvironment");

		service.getProjects("wrongEnvironment", "Project.Name eq crash");
	}

	@Test
	public void testGetProjects() {
		service.getProjects("MDMTEST", "Project.Name eq crash");

		verify(searchActivity).search(context, Project.class, "Project.Name eq crash");
	}

	@Test
	public void testGetSearchAttributes() {
		service.getSearchAttributes("MDMTEST");

		verify(searchActivity).listAvailableAttributes(context, Project.class);
	}

	@Test
	public void testLocalizeAttributes() {
		service.localizeAttributes("MDMTEST");
		verify(i18nActivity).localizeAttributes("MDMTEST", Project.class);
	}

	@Test
	public void testLocalizeType() {
		service.localizeType("MDMTEST");
		verify(i18nActivity).localizeType("MDMTEST", Project.class);
	}

}
