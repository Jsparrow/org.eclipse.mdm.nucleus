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


package org.eclipse.mdm.filerelease;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.mdm.filerelease.boundary.FileReleaseService;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
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

		FileRelease request1 = new FileRelease();
		request1.sourceName = "MDMENV";
		request1.typeName = "TestStep";
		request1.id = "123";
		request1.format = FileReleaseManager.CONVERTER_FORMAT_PAK2RAW;
		request1.orderMessage = "new file release";
		request1.validity = 10;
		List<FileRelease> fileReleases = fileReleaseService.getReleases(null);
		fileReleaseService.create(request1);

		FileRelease request2 = new FileRelease();
		request2.sourceName = "MDMENV";
		request2.typeName = "TestStep";
		request2.id = "1234";
		request2.format = FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX;
		request2.orderMessage = "new file release";
		request2.validity = 10;
		fileReleaseService.create(request2);
		fileReleases = fileReleaseService.getReleases(null);
		assertNotNull("The fileReleases list should not be null.", fileReleases);
		int expectedSize = FileReleaseServiceMockHelper.NUM_FILE_REL_IN + FileReleaseServiceMockHelper.NUM_FILE_REL_OUT
				+ 2;
		assertEquals("The size of the fileRelease list should be " + expectedSize, expectedSize, fileReleases.size());
	}

	@Test(expected = FileReleaseException.class)
	public void testCreateWithInvalidFileRelease() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		FileRelease request = new FileRelease();
		request.sourceName = "MDMENV";
		request.typeName = "TestStep";
		request.id = "123";
		request.format = FileReleaseManager.CONVERTER_FORMAT_PAK2RAW;
		request.orderMessage = "new file release";
		request.validity = 0;
		fileReleaseService.create(request);
	}

	@Test(expected = FileReleaseException.class)
	public void testCreateWithUnsuppotedType() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();
		FileRelease request = new FileRelease();
		request.sourceName = "MDMENV";
		request.typeName = "TestStep";
		request.id = "123";
		request.format = "Some unsupported type";
		request.orderMessage = "new file release";
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
		FileRelease release2Approve = new FileRelease();
		release2Approve.identifier = FileReleaseServiceMockHelper.ID_IN_PREFIX + "1";
		release2Approve.state = FileReleaseManager.FILE_RELEASE_STATE_APPROVED;
		fileReleaseService.approve(release2Approve);
	}

	@Test
	public void testReject() throws Exception {
		FileReleaseService fileReleaseService = createMockedService();

		FileRelease release2Reject = new FileRelease();
		release2Reject.identifier = FileReleaseServiceMockHelper.ID_IN_PREFIX + "1";
		release2Reject.state = FileReleaseManager.FILE_RELEASE_STATE_REJECTED;
		release2Reject.rejectMessage = "reject message";

		fileReleaseService.reject(release2Reject);

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

		Field targetDirectoryPathField = fileReleaseService.getClass().getDeclaredField("targetDirectoryPath");
		targetDirectoryPathField.setAccessible(true);
		targetDirectoryPathField.set(fileReleaseService, System.getProperty("java.io.tmpdir"));
		targetDirectoryPathField.setAccessible(false);

		return fileReleaseService;
	}
}
