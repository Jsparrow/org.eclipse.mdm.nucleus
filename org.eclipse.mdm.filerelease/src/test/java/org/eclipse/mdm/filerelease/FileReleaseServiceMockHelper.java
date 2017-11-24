/*******************************************************************************
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sebastian Dirsch - initial implementation
 *******************************************************************************/

package org.eclipse.mdm.filerelease;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.mdm.api.base.adapter.Core;
import org.eclipse.mdm.api.base.adapter.EntityStore;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.FileConvertJobManager;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.mockito.Mockito;

public class FileReleaseServiceMockHelper {

	public static final String TEST_USERNAME_SELF = "sa";
	public static final String TEST_USERNAME_OTHER = "ot";

	public static final String TESTSTEP = "TestStep";

	public static final int NUM_FILE_REL_IN = 3;
	public static final int NUM_FILE_REL_OUT = 5;

	public static final String ID_IN_PREFIX = "IN";
	public static final String ID_OUT_PREFIX = "OUT";

	public static FileReleaseManager createFileReleaseManagerMock() throws Exception {

		FileReleaseManager frManagerMock = new FileReleaseManager();
		Field releaseMapField = frManagerMock.getClass().getDeclaredField("releaseMap");
		releaseMapField.setAccessible(true);
		releaseMapField.set(frManagerMock, new HashMap<>());
		for (FileRelease fr : createFileReleaseMockList()) {
			frManagerMock.addFileRelease(fr);
		}

		releaseMapField.setAccessible(false);
		return frManagerMock;
	}

	public static FileConvertJobManager createFileConvertJobManagerMock() {
		FileConvertJobManager managerMock = Mockito.mock(FileConvertJobManager.class);
		return managerMock;
	}

	public static ConnectorService createConnectorServiceMock() throws Exception {
		ConnectorService mockedConnectorService = Mockito.mock(ConnectorService.class);
		ApplicationContext mockedContext = createContextMock();
		List<ApplicationContext> contextList = new ArrayList<>();
		contextList.add(mockedContext);
		when(mockedConnectorService.getContexts()).thenReturn(contextList);
		when(mockedConnectorService.getContextByName("MDMENV")).thenReturn(contextList.get(0));
		return mockedConnectorService;
	}

	public static ApplicationContext createContextMock() throws Exception {
		EntityManager em = Mockito.mock(EntityManager.class);

		Environment mockedEnv = createEntityMock(Environment.class, "MDMENV", "MDMENV", "1");
		when(em.loadEnvironment()).thenReturn(mockedEnv);

		User mockedUser = createEntityMock(User.class, TEST_USERNAME_SELF, "MDMENV", "1");
		when(em.loadLoggedOnUser()).thenReturn(Optional.of(mockedUser));

		Test mockedTest = createEntityMock(Test.class, "Test", "MDMENV", "1");
		mockedTest.setResponsiblePerson(mockedUser);
		when(em.loadParent(any(TestStep.class), eq(TestStep.PARENT_TYPE_TEST)))
				.thenReturn(Optional.of(mockedTest));

		TestStep mockedTestStep1 = createEntityMock(TestStep.class, "Teststep", "MDMENV", "123");
		when(em.load(TestStep.class, "123")).thenReturn(mockedTestStep1);

		TestStep mockedTestStep2 = createEntityMock(TestStep.class, "Teststep", "MDMENV", "1234");
		when(em.load(TestStep.class, "1234")).thenReturn(mockedTestStep2);

		ApplicationContext context = Mockito.mock(ApplicationContext.class);
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		return context;
	}

	private static List<FileRelease> createFileReleaseMockList() {
		List<FileRelease> fileReleasMockList = new ArrayList<>();

		for (int i = 0; i < NUM_FILE_REL_IN; i++) {
			FileRelease fileRelease = new FileRelease();
			fileRelease.receiver = TEST_USERNAME_SELF;
			fileRelease.sourceName = "MDMENV";
			fileRelease.typeName = "TestStep";
			fileRelease.id = String.valueOf(i);
			fileRelease.sender = TEST_USERNAME_OTHER;
			fileRelease.identifier = ID_IN_PREFIX + i;
			fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_ORDERED;
			fileReleasMockList.add(fileRelease);
		}

		for (int i = 0; i < NUM_FILE_REL_OUT; i++) {
			FileRelease fileRelease = new FileRelease();
			fileRelease.sourceName = "MDMENV";
			fileRelease.typeName = "TestStep";
			fileRelease.id = String.valueOf(i + NUM_FILE_REL_IN);
			fileRelease.receiver = TEST_USERNAME_OTHER;
			fileRelease.identifier = ID_OUT_PREFIX + i;
			fileRelease.sender = TEST_USERNAME_SELF;
			fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_ORDERED;
			fileReleasMockList.add(fileRelease);
		}
		return fileReleasMockList;

	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, String id)
			throws Exception {

		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));

		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);
		when(core.getValues()).thenReturn(map);

		EntityStore entityStore = new EntityStore();
		when(core.getMutableStore()).thenReturn(entityStore);

		Constructor<T> constructor = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}
}
