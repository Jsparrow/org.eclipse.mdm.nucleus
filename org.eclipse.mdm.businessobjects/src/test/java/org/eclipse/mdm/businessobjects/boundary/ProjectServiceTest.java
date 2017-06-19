/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectServiceTest {

	EntityManager em = Mockito.mock(EntityManager.class);
	ConnectorService connectorService = Mockito.mock(ConnectorService.class);
	SearchActivity searchActivity = Mockito.mock(SearchActivity.class);
	I18NActivity i18nActivity = Mockito.mock(I18NActivity.class);

	ProjectService service = new ProjectService(connectorService, searchActivity, i18nActivity);

	@Before
	public void init() {
		when(connectorService.getEntityManagerByName("MDMTEST")).thenReturn(em);
	}

	@Test
	public void testGetProject() throws DataAccessException {
		service.getProject("MDMTEST", 1L);

		verify(em).load(Project.class, 1L);
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetProjectsEmptyFilter() throws DataAccessException {
		service.getProjects("MDMTEST", "");

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetProjectsNullFilter() throws DataAccessException {
		service.getProjects("MDMTEST", null);

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test(expected = MDMEntityAccessException.class)
	public void testGetProjectsWrongEnvironment() {
		doThrow(MDMEntityAccessException.class).when(connectorService).getEntityManagerByName("wrongEnvironment");

		service.getProjects("wrongEnvironment", "Project.Name eq crash");
	}

	@Test
	public void testGetProjects() {
		service.getProjects("MDMTEST", "Project.Name eq crash");

		verify(searchActivity).search(em, Project.class, "Project.Name eq crash");
	}

	@Test
	public void testGetSearchAttributes() {
		service.getSearchAttributes("MDMTEST");

		verify(searchActivity).listAvailableAttributes(em, Project.class);
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
