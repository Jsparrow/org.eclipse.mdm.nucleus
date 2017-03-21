package org.eclipse.mdm.query.boundary;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.query.entity.QueryRequest;
import org.eclipse.mdm.query.entity.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
