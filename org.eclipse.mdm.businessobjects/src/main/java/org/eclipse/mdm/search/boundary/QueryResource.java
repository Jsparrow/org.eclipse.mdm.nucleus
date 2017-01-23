package org.eclipse.mdm.search.boundary;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.search.control.QueryService;
import org.eclipse.mdm.search.entity.LoadRequest;
import org.eclipse.mdm.search.entity.QueryRequest;
import org.eclipse.mdm.search.entity.ResultRows;
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
	public ResultRows query(QueryRequest request) {
		return new ResultRows(queryService.queryRows(request));
	}
	
	@POST
	@Path("load")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResultRows load(LoadRequest request) {
		return new ResultRows(queryService.loadRows(request));
	}
}
