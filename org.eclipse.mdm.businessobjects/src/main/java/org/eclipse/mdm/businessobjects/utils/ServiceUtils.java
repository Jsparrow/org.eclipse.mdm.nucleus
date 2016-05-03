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

import java.util.Optional;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.businessobjects.entity.MDMEntityAccessException;

import com.google.gson.Gson;

public final class ServiceUtils {

	/**
	 * creates a valid {@link URI} with a valid source type
	 * 
	 * @param em {@link EntityManager} of the data source
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param type business object type e.g. TestStep.class
	 * @param id business object id
	 * @return the created valid MDM business object {@link URI}
	 */
	public static URI createMDMURI(EntityManager em, String sourceName, Class<? extends Entity> type, long id)  {
		
		String typeName = getModelMananger(em).getEntityType(type).getName();
		return new URI(sourceName, typeName, id);		
	}
	
	
	
	/**
	 * converts the given object to a {@link Response} with the given {@link Status} 
	 * 
	 * @param responseEntry object to convert
	 * @param status {@link Status} of the {@link Response}
	 * @return the created {@link Response}
	 */
	public static Response toResponse(Object responseEntry, Status status) {
		String response = new Gson().toJson(responseEntry);
		return Response.status(status).entity(response).type(MediaType.APPLICATION_JSON).build();
	}
	
	
	
	/**
	 * returns true if the given filter String is a parent filter of the given parent type
	 * 
	 * @param em {@link EntityManager} of the data source
	 * @param filter parent filter string to check
	 * @param parentType class of the parent entity 
	 * @return true if the give filter String is a parent filter
	 */
	public static boolean isParentFilter(EntityManager em, String filter, Class<? extends Entity> parentType) {
		ModelManager mm = getModelMananger(em);
		EntityType et = mm.getEntityType(parentType);
	
		String idAttributeName = et.getIDAttribute().getName();
		String matcher = parentType.getSimpleName() + "." + idAttributeName + " eq (\\d+)";
		return filter.matches(matcher);
	}
	
	
	
	/**
	 * returns the business object ID from a parent filter
	 * 
	 * @param em {@link EntityManager} of the data source
	 * @param filter parent filter string 
	 * @param parentType parent type to identify the Id attribute name
	 * @return the extracted business object Id
	 */
	public static long extactIdFromParentFilter(EntityManager em, String filter, Class<? extends Entity> parentType) {
		ModelManager mm = getModelMananger(em);
		EntityType et = mm.getEntityType(parentType);
	
		String idAttributeName = et.getIDAttribute().getName();
		return Long.valueOf(filter.replace(parentType.getSimpleName() + "." + idAttributeName + " eq ", ""));
	}
	
	
	
	/**
	 * returns the {@link ModelManager} service form the given {@link EntityManager} if it is available
	 * @param em {@link EntityManager} which provides the {@link ModelManager} service
	 * @return the {@link ModelManager} service form the given {@link EntityManager} if it is available
	 */
	public static ModelManager getModelMananger(EntityManager em) {
		Optional<ModelManager> optional = em.getModelManager();
		if(!optional.isPresent()) {
			throw new IllegalStateException("neccessary ModelManager service is not available");
		}
		return optional.get();
	}
	
	

	/**
	 * returns the {@link SearchService} from the given {@link EntityManager} if it is available
	 * @param em {@link EntityManager} which provides the {@link SearchService}
	 * @return the {@link SearchService} from the given {@link EntityManager} if it is available
	 */
	public static SearchService getSearchService(EntityManager em) {
		Optional<SearchService> oSS = em.getSearchService();
		if(!oSS.isPresent()) {
			throw new IllegalStateException("neccessary Search service is not available");
		}
		return oSS.get();
	}
	
	
	/**
	 * returns the a business object form a data source identified by the given {@link URI}
	 * 
	 * @param type MDM business object type
	 * @param em {@link EntityManager}
	 * @param uri {@link URI} of the MDM business object
	 * @return the found business object
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T lookupEntityByURI(Class<T> type, EntityManager em, URI uri) throws DataAccessException  {

		Optional<? extends Entity> optinal = em.load(uri);
		if(!optinal.isPresent()) {
			throw new MDMEntityAccessException("mdm entity with uri '" + uri.toString() + " not found");
		}
		return (T)optinal.get();		
	}
}
