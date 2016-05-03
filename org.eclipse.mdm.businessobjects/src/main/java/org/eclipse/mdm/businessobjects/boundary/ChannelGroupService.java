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
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * ChannelGroupService Bean implementation with available {@link ChannelGroup} operations
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class ChannelGroupService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private I18NActivity I18NActivity;
	@EJB
	private NavigationActivity navigationActivity;
	@EJB
	private SearchActivity searchActivity;
	
	
	
	/**
	 * returns the matching {@link ChannelGroup}s using the given filter or all {@link ChannelGroup}s 
	 * if no filter is available
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param filter filter string to filter the ChannelGroup result
	 * @return the found {@link ChannelGroup}s
	 */
	public List<ChannelGroup> getChannelGroups(String sourceName, String filter) {		
		try {
			
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			
			if(filter == null || filter.trim().length() <= 0) {
				return em.loadAll(ChannelGroup.class);
			}
			
			if(ServiceUtils.isParentFilter(em, filter, ChannelGroup.PARENT_TYPE_MEASUREMENT)) {
				long id = ServiceUtils.extactIdFromParentFilter(em, filter, ChannelGroup.PARENT_TYPE_MEASUREMENT);
				URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, ChannelGroup.PARENT_TYPE_MEASUREMENT, id);
				return this.navigationActivity.getChannelGroups(measurementURI);
			}
			
			return this.searchActivity.search(em, ChannelGroup.class, filter);
	
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		} 
	}
	
	
	
	/**
	 * returns a {@link ChannelGroup identified by the given id
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param channelGroupId id of the {@link ChannelGroup}
	 * @return the matching {@link ChannelGroup}
	 */
	public ChannelGroup getChannelGroup(String sourceName, long channelGroupId) {
		try {		
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			URI channelGroupURI = ServiceUtils.createMDMURI(em, sourceName, ChannelGroup.class, channelGroupId);
			Optional<ChannelGroup> optional = em.load(channelGroupURI);
			
			if(!optional.isPresent()) {
				String message = "mdm ChannelGroup with id '" + channelGroupId 	
					+ "' does not exist at data source with name '"	+ sourceName + "'";
				throw new MDMEntityAccessException(message);
			}
			
			return optional.get();
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * returns localized {@link ChannelGroup} attributes
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ChannelGroup} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {		
		return this.I18NActivity.localizeAttributes(sourceName, ChannelGroup.class);
	}	
	
	
	
	/**
	 * returns the localized {@link ChannelGroup} type name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ChannelGroup} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.I18NActivity.localizeType(sourceName, ChannelGroup.class);
	}
}
