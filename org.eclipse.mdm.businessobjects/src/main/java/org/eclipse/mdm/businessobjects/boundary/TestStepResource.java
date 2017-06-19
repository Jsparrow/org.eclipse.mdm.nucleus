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
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextSensor;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.businessobjects.entity.ContextResponse;
import org.eclipse.mdm.businessobjects.entity.ContextSensorResponse;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TestStep} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/environments/{SOURCENAME}/teststeps")
public class TestStepResource {

	private static final Logger LOG = LoggerFactory.getLogger(TestStepResource.class);

	@EJB
	private TestStepService testStepService;

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the TestStep result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTestSteps(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {

		try {
			List<TestStep> testSteps = this.testStepService.getTestSteps(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(TestStep.class, testSteps), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam("SOURCENAME") String sourceName) {
		try {
			List<SearchAttribute> searchAttributes = this.testStepService.getSearchAttributes(sourceName);
			return ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}")
	public Response getTestStep(@PathParam("SOURCENAME") String sourceName, @PathParam("TESTSTEP_ID") long testStepId) {

		try {
			TestStep testStep = this.testStepService.getTestStep(sourceName, testStepId);
			return ServiceUtils.toResponse(new MDMEntityResponse(TestStep.class, testStep), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}/contexts")
	public Response getContext(@PathParam("SOURCENAME") String sourceName, @PathParam("TESTSTEP_ID") long testStepId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.testStepService.getContext(sourceName,
					testStepId);

			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}/contexts/unitundertest")
	public Response getContextUUT(@PathParam("SOURCENAME") String sourceName,
			@PathParam("TESTSTEP_ID") long testStepId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.testStepService.getContextUUT(sourceName,
					testStepId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}/contexts/testsequence")
	public Response getContextTSQ(@PathParam("SOURCENAME") String sourceName,
			@PathParam("TESTSTEP_ID") long testStepId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.testStepService.getContextTSQ(sourceName,
					testStepId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}/contexts/testequipment")
	public Response getContextTEQ(@PathParam("SOURCENAME") String sourceName,
			@PathParam("TESTSTEP_ID") long testStepId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.testStepService.getContextTEQ(sourceName,
					testStepId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{TESTSTEP_ID}/contexts/testequipment/sensors")
	public Response getContextTEQSensors(@PathParam("SOURCENAME") String sourceName,
			@PathParam("TESTSTEP_ID") long testStepId) {
		try {
			Map<String, List<ContextSensor>> sensorMap = this.testStepService.getSensors(sourceName, testStepId);
			return ServiceUtils.toResponse(new ContextSensorResponse(sensorMap), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link TestStepService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam("SOURCENAME") String sourceName) {

		try {
			Map<Attribute, String> localizedAttributeMap = this.testStepService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.testStepService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
