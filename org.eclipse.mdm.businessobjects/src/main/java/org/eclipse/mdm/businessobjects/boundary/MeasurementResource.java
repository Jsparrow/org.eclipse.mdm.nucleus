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

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextSensor;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
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
 * {@link Measurement} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/environments/{SOURCENAME}/measurements")
public class MeasurementResource {

	private static final Logger LOG = LoggerFactory.getLogger(MeasurementResource.class);

	@EJB
	private MeasurementService measurementService;

	/**
	 * delegates the request to the {@link MeasurementService}
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
			List<SearchAttribute> searchAttributes = this.measurementService.getSearchAttributes(sourceName);
			return ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the Measurement result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMeasurements(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {

		try {
			List<Measurement> measurements = this.measurementService.getMeasurements(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(Measurement.class, measurements), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}")
	public Response getMeasurement(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {

		try {
			Measurement measurement = this.measurementService.getMeasurement(sourceName, measurementId);
			return ServiceUtils.toResponse(new MDMEntityResponse(Measurement.class, measurement), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}/contexts")
	public Response getContext(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.measurementService.getContext(sourceName,
					measurementId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}/contexts/unitundertest")
	public Response getContextUUT(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.measurementService.getContextUUT(sourceName,
					measurementId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}/contexts/testsequence")
	public Response getContextTSQ(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.measurementService.getContextTSQ(sourceName,
					measurementId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}/contexts/testequipment")
	public Response getContextTEQ(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {
		try {
			Map<String, Map<ContextType, ContextRoot>> contextMap = this.measurementService.getContextTEQ(sourceName,
					measurementId);
			return ServiceUtils.toResponse(new ContextResponse(contextMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param MeasurementId
	 *            id of the {@link Measurement}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{MEASUREMENT_ID}/contexts/testequipment/sensors")
	public Response getContextTEQSensors(@PathParam("SOURCENAME") String sourceName,
			@PathParam("MEASUREMENT_ID") String measurementId) {
		try {
			try {
				Map<String, List<ContextSensor>> sensorMap = this.measurementService.getSensors(sourceName,
						measurementId);
				return ServiceUtils.toResponse(new ContextSensorResponse(sensorMap), Status.OK);
			} catch (RuntimeException e) {
				LOG.error(e.getMessage(), e);
				throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
			}

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link MeasurementService}
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
			Map<Attribute, String> localizedAttributeMap = this.measurementService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.measurementService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
