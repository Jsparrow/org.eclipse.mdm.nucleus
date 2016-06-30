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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.mdm.filerelease.boundary.FileReleaseService;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.filerelease.entity.FileReleaseRequest;
import org.junit.Test;

public class FileReleaseServiceTest {

	@Test
	public void testGetReleases() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		List<FileRelease> fileReleases = fileReleaseService.getReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		assertNotNull("The fileReleases list should not be null.", fileReleases);
		int expectedSize = FileReleaseServiceMockHelper.NUM_FILE_REL_IN + FileReleaseServiceMockHelper.NUM_FILE_REL_OUT;
		assertEquals("The size of the fileRelease list should be " + expectedSize, expectedSize, fileReleases.size());
		fileReleases = fileReleaseService.getReleases("some unknown state");
		assertEquals("The size of the fileRelease list should be " + 0, 0, fileReleases.size());

	}

	@Test
	public void testGetRelease() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		FileRelease fileRelease = fileReleaseService.getRelease(FileReleaseServiceMockHelper.ID_IN_PREFIX + "1");
		assertNotNull("The file release shoult not be null", fileRelease);
	}

	@Test
	public void testGetIncommingReleases() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		List<FileRelease> fileReleasesIn = fileReleaseService
				.getIncommingReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		assertNotNull("The fileReleases list should not be null.", fileReleasesIn);
		assertEquals("The size of the fileRelease list should be " + FileReleaseServiceMockHelper.NUM_FILE_REL_IN,
				FileReleaseServiceMockHelper.NUM_FILE_REL_IN, fileReleasesIn.size());
	}

	@Test
	public void testGetOutgoingReleases() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		List<FileRelease> fileReleasesOut = fileReleaseService
				.getOutgoingReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		assertNotNull("The fileReleases list should not be null.", fileReleasesOut);
		assertEquals("The size of the fileRelease list should be " + FileReleaseServiceMockHelper.NUM_FILE_REL_OUT,
				FileReleaseServiceMockHelper.NUM_FILE_REL_OUT, fileReleasesOut.size());
	}

	@Test
	public void testCreate() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		
		FileReleaseRequest request1 = new FileReleaseRequest();
		request1.sourceName = "MDMENV";
		request1.typeName = "TestStep";
		request1.id = 123L;
		request1.format = FileReleaseManager.CONVERTER_FORMAT_PAK2RAW;
		request1.message = "new file release";
		request1.validity = 10;
		List<FileRelease> fileReleases = fileReleaseService.getReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		fileReleaseService.create(request1);

		FileReleaseRequest request2 = new FileReleaseRequest();
		request2.sourceName = "MDMENV";
		request2.typeName = "TestStep";
		request2.id = 1234L;
		request2.format = FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX;
		request2.message = "new file release";
		request2.validity = 10;
		fileReleaseService.create(request2);
		fileReleases = fileReleaseService.getReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		assertNotNull("The fileReleases list should not be null.", fileReleases);
		int expectedSize = FileReleaseServiceMockHelper.NUM_FILE_REL_IN + FileReleaseServiceMockHelper.NUM_FILE_REL_OUT
				+ 2;
		assertEquals("The size of the fileRelease list should be " + expectedSize, expectedSize, fileReleases.size());
	}

	@Test(expected = FileReleaseException.class)
	public void testCreateWithInvalidFileRelease() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		FileReleaseRequest request = new FileReleaseRequest();
		request.sourceName = "MDMENV";
		request.typeName = "TestStep";
		request.id = 123L;
		request.format = FileReleaseManager.CONVERTER_FORMAT_PAK2RAW;
		request.message = "new file release";
		request.validity = 0;
		fileReleaseService.create(request);
	}

	@Test(expected = FileReleaseException.class)
	public void testCreateWithUnsuppotedType() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		FileReleaseRequest request = new FileReleaseRequest();
		request.sourceName = "MDMENV";
		request.typeName = "TestStep";
		request.id = 123L;
		request.format = "Some unsupported type";
		request.message = "new file release";
		request.validity = 0;
		fileReleaseService.create(request);
	}

	@Test
	public void testDelete() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		fileReleaseService.delete(FileReleaseServiceMockHelper.ID_OUT_PREFIX + "1");
		List<FileRelease> fileReleases = fileReleaseService.getReleases(FileReleaseManager.FILE_RELEASE_STATE_ORDERED);
		assertNotNull("The fileReleases list should not be null.", fileReleases);
		int expectedSize = FileReleaseServiceMockHelper.NUM_FILE_REL_IN + FileReleaseServiceMockHelper.NUM_FILE_REL_OUT
				- 1;
		assertEquals("The size of the fileRelease list should be " + expectedSize, expectedSize, fileReleases.size());
	}

	@Test
	public void testApprove() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		fileReleaseService.approve(FileReleaseServiceMockHelper.ID_IN_PREFIX + "1");
	}

	@Test
	public void testReject() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		fileReleaseService.reject(FileReleaseServiceMockHelper.ID_IN_PREFIX + "1", "message");
		FileRelease fileRelease = fileReleaseService.getRelease(FileReleaseServiceMockHelper.ID_IN_PREFIX + "1");
		assertNotNull("FileRelease should not be null.", fileRelease);
		assertEquals("FileRelease state should be " + FileReleaseManager.FILE_RELEASE_STATE_REJECTED, fileRelease.state,
				FileReleaseManager.FILE_RELEASE_STATE_REJECTED);
	}

	private FileReleaseService createMockedService() throws Exception {
		FileReleaseService fileReleaseService = new FileReleaseService();

		Field fileReleaseManagerField = fileReleaseService.getClass().getDeclaredField("manager");
		fileReleaseManagerField.setAccessible(true);
		fileReleaseManagerField.set(fileReleaseService, FileReleaseServiceMockHelper.createFileReleaseManagerMock());
		fileReleaseManagerField.setAccessible(false);

		Field connectorServiceField = fileReleaseService.getClass().getDeclaredField("connectorService");
		connectorServiceField.setAccessible(true);
		connectorServiceField.set(fileReleaseService, FileReleaseServiceMockHelper.createConnectorServiceMock());
		connectorServiceField.setAccessible(false);

		Field converterField = fileReleaseService.getClass().getDeclaredField("converter");
		converterField.setAccessible(true);
		converterField.set(fileReleaseService, FileReleaseServiceMockHelper.createFileConvertJobManagerMock());
		converterField.setAccessible(false);


		return fileReleaseService;
	}
}