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
import org.eclipse.mdm.api.base.model.Channel;
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
 * ChannelService Bean implementation with available {@link Channel} operations
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class ChannelService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private NavigationActivity navigationActivity;
	@EJB
	private SearchActivity searchActivity;
	
	
	
	/**
	 * returns the matching {@link Channel}s using the given filter or all {@link Channel}s 
	 * if no filter is available
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param filter filter string to filter the Channel result
	 * @return the found {@link Channel}s
	 */
	public List<Channel> getChannels(String sourceName, String filter) {		
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			
			if(filter == null || filter.trim().length() <= 0) {
				return em.loadAll(Channel.class);
			}		
			
			if(ServiceUtils.isParentFilter(em, filter, Channel.PARENT_TYPE_CHANNELGROUP)) {
				long id = ServiceUtils.extactIdFromParentFilter(em, filter, Channel.PARENT_TYPE_CHANNELGROUP);
				URI channelGroupURI = ServiceUtils.createMDMURI(em, sourceName, Channel.PARENT_TYPE_CHANNELGROUP, id);
				return this.navigationActivity.getChannels(channelGroupURI);
			}
			
			return this.searchActivity.search(em, Channel.class, filter);
			
			
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * returns a {@link Channel} identified by the given id.
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param channelId id of the {@link Channel}
	 * @return the matching {@link Channel}
	 */
	public Channel getChannel(String sourceName, long channelId) {
		try {		
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			URI channelURI = ServiceUtils.createMDMURI(em, sourceName, Channel.class, channelId);
			Optional<Channel> optional = em.load(channelURI);
			
			if(!optional.isPresent()) {
				String message = "mdm Channelwith id '" + channelId 
					+ "' does not exist at data source with name '"	+ sourceName + "'";
				throw new MDMEntityAccessException(message);
			}
			
			return optional.get();
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * returns localized {@link Channel} attributes
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Channel} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {		
		return this.i18nActivity.localizeAttributes(sourceName, Channel.class);
	}	
	
	
	
	/**
	 * returns the localized {@link Channel} type name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Channel} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Channel.class);
	}
}
