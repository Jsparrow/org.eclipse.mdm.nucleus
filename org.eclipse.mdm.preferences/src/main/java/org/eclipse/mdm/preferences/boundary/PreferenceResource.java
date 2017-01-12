package org.eclipse.mdm.preferences.boundary;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.preferences.controller.PreferenceService;
import org.eclipse.mdm.preferences.entity.Preference;
import org.eclipse.mdm.preferences.entity.PreferenceResponse;
import org.eclipse.mdm.preferences.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/preferences")
public class PreferenceResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(PreferenceResource.class); 
	
	@EJB
	private PreferenceService preferenceService;
	
	@GET
	@Path("/ping")
	public String ping() {
		return "it works";
	}
	
	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param filter TODO
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPreference(@QueryParam("filter") String filter) {
		
		try {			
			List<Preference> config = this.preferenceService.loadAll();
			return ServiceUtils.toResponse(new PreferenceResponse(config), Status.OK);
		
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param preference Configuration to save
	 * @return the result of the delegated request as {@link Response}
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setPreference(Preference preference) {
		
		try {			
			this.preferenceService.save(preference);
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
