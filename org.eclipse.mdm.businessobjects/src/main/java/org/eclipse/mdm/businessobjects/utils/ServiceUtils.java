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

package org.eclipse.mdm.businessobjects.utils;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;

public final class ServiceUtils {

	/**
	 * converts the given object to a {@link Response} with the given
	 * {@link Status}
	 *
	 * @param responseEntry
	 *            object to convert
	 * @param status
	 *            {@link Status} of the {@link Response}
	 * @return the created {@link Response}
	 */
	public static Response toResponse(Object response, Status status) {
		GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
		return Response.status(status).entity(genEntity).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * returns true if the given filter String is a parent filter of the given
	 * parent type
	 *
	 * @param em
	 *            {@link EntityManager} of the data source
	 * @param filter
	 *            parent filter string to check
	 * @param parentType
	 *            class of the parent entity
	 * @return true if the give filter String is a parent filter
	 */
	public static boolean isParentFilter(ApplicationContext context, String filter, Class<? extends Entity> parentType) {
		ModelManager mm = context.getModelManager().orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
		EntityType et = mm.getEntityType(parentType);

		String idAttributeName = et.getIDAttribute().getName();
		String matcher = workaroundForTypeMapping(et) + "." + idAttributeName + " eq (\\w+)";
		return filter.matches(matcher);
	}

	/**
	 * returns the business object ID from a parent filter
	 *
	 * @param em
	 *            {@link EntityManager} of the data source
	 * @param filter
	 *            parent filter string
	 * @param parentType
	 *            parent type to identify the Id attribute name
	 * @return the extracted business object Id
	 */
	public static String extactIdFromParentFilter(ApplicationContext context, String filter, Class<? extends Entity> parentType) {
		ModelManager mm = context.getModelManager().orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
		EntityType et = mm.getEntityType(parentType);

		String idAttributeName = et.getIDAttribute().getName();
		return filter.replace(workaroundForTypeMapping(et) + "." + idAttributeName + " eq ", "");
	}

	/**
	 * Simple workaround for naming mismatch between Adapter and Business object
	 * names.
	 * 
	 * @param entityType
	 *            entity type
	 * @return MDM business object name
	 */
	public static String workaroundForTypeMapping(EntityType entityType) {
		switch (entityType.getName()) {
		case "StructureLevel":
			return "Pool";
		case "MeaResult":
			return "Measurement";
		case "SubMatrix":
			return "ChannelGroup";
		case "MeaQuantity":
			return "Channel";
		default:
			return entityType.getName();
		}
	}
}
