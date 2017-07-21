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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PoolServiceTest {

	EntityManager em = Mockito.mock(EntityManager.class);
	ModelManager mm = mockModelManager();
	ConnectorService connectorService = Mockito.mock(ConnectorService.class);
	SearchActivity searchActivity = Mockito.mock(SearchActivity.class);
	NavigationActivity navigationActivity = Mockito.mock(NavigationActivity.class);
	I18NActivity i18nActivity = Mockito.mock(I18NActivity.class);

	PoolService service = new PoolService(connectorService, searchActivity, navigationActivity, i18nActivity);

	@Before
	public void init() {
		when(connectorService.getEntityManagerByName("MDMTEST")).thenReturn(em);
		when(em.getModelManager()).thenReturn(Optional.of(mm));
	}

	@Test
	public void testGetPool() throws DataAccessException {
		service.getPool("MDMTEST", "1");

		verify(em).load(Pool.class, "1");
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetPoolsEmptyFilter() throws DataAccessException {
		service.getPools("MDMTEST", "");

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test
	public void testGetPoolsNullFilter() throws DataAccessException {
		service.getPools("MDMTEST", null);

		verify(em).loadAll(Mockito.any());
		verifyNoMoreInteractions(searchActivity);
	}

	@Test(expected = MDMEntityAccessException.class)
	public void testGetPoolsWrongEnvironment() {
		doThrow(MDMEntityAccessException.class).when(connectorService).getEntityManagerByName("wrongEnvironment");

		service.getPools("wrongEnvironment", "Pool.Name eq crash");
	}

	@Test
	public void testGetPoolsParentFilter() {
		service.getPools("MDMTEST", "Project.Id eq 4711");

		verify(navigationActivity).getPools("MDMTEST", "4711");
		verifyZeroInteractions(searchActivity);
	}

	@Test
	public void testGetPools() {
		service.getPools("MDMTEST", "Pool.Name eq crash");

		verify(searchActivity).search(em, Pool.class, "Pool.Name eq crash");
	}

	@Test
	public void testGetSearchAttributes() {
		service.getSearchAttributes("MDMTEST");

		verify(searchActivity).listAvailableAttributes(em, Pool.class);
	}

	@Test
	public void testLocalizeAttributes() {
		service.localizeAttributes("MDMTEST");
		verify(i18nActivity).localizeAttributes("MDMTEST", Pool.class);
	}

	@Test
	public void testLocalizeType() {
		service.localizeType("MDMTEST");
		verify(i18nActivity).localizeType("MDMTEST", Pool.class);
	}

	private ModelManager mockModelManager() {

		Attribute projectId = mock(Attribute.class);
		when(projectId.getName()).thenReturn("Id");

		EntityType project = mock(EntityType.class);
		when(project.getSourceName()).thenReturn("MDMTEST");
		when(project.getName()).thenReturn("Project");
		when(project.getAttributes()).thenReturn(Arrays.asList(projectId));
		when(project.getIDAttribute()).thenReturn(projectId);

		Attribute poolId = mock(Attribute.class);
		when(poolId.getName()).thenReturn("Id");
		when(poolId.getValueType()).thenReturn(ValueType.LONG);

		Attribute poolName = mock(Attribute.class);
		when(poolName.getName()).thenReturn("Name");
		when(poolName.getValueType()).thenReturn(ValueType.STRING);

		EntityType pool = mock(EntityType.class);
		when(pool.getSourceName()).thenReturn("MDMTEST");
		when(pool.getName()).thenReturn("Pool");
		when(pool.getAttributes()).thenReturn(Arrays.asList(poolId, poolName));
		when(pool.getAttribute("Name")).thenReturn(poolName);
		when(pool.getIDAttribute()).thenReturn(poolId);

		when(poolId.getEntityType()).thenReturn(pool);
		when(poolName.getEntityType()).thenReturn(pool);

		ModelManager mm = mock(ModelManager.class);
		when(mm.getEntityType(Project.class)).thenReturn(project);
		when(mm.getEntityType("Project")).thenReturn(project);
		when(mm.getEntityType(Pool.class)).thenReturn(pool);
		when(mm.getEntityType("Pool")).thenReturn(pool);

		return mm;
	}
}
