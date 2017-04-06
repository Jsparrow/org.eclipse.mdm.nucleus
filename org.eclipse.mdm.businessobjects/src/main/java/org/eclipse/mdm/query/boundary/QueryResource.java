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
package org.eclipse.mdm.query.boundary;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.query.entity.QueryRequest;
import org.eclipse.mdm.query.entity.QueryResult;
import org.eclipse.mdm.query.entity.SuggestionRequest;
import org.eclipse.mdm.query.entity.SuggestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Path("/")
public class QueryResource {

	private static final Logger LOG = LoggerFactory.getLogger(QueryResource.class); 
	
	@EJB
	private QueryService queryService;
	
	@POST
	@Path("query")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public QueryResult query(QueryRequest request) {	
		return new QueryResult(queryService.queryRows(request));
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("suggestions")
	public Response getSearchAttributes(SuggestionRequest suggestionRequest) {
		try {
			List<String> suggestions = queryService.getSuggestions(suggestionRequest);
			return ServiceUtils.toResponse(new SuggestionResponse(suggestions), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
