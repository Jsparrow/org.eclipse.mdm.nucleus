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

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Path("/")
public class QueryResource {

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
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
