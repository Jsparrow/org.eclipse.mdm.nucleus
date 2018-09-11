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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * ChannelGroupService Bean implementation with available {@link ChannelGroup}
 * operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class ChannelGroupService {

	@Inject
	private ConnectorService connectorService;

	@EJB
	private I18NActivity I18NActivity;
	@EJB
	private NavigationActivity navigationActivity;
	@EJB
	private SearchActivity searchActivity;

	/**
	 * returns the matching {@link ChannelGroup}s using the given filter or all
	 * {@link ChannelGroup}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the ChannelGroup result
	 * @return the found {@link ChannelGroup}s
	 */
	public List<ChannelGroup> getChannelGroups(String sourceName, String filter) {
		try {

			ApplicationContext context = this.connectorService.getContextByName(sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));

			if (filter == null || filter.trim().length() <= 0) {
				return em.loadAll(ChannelGroup.class);
			}

			if (ServiceUtils.isParentFilter(context, filter, ChannelGroup.PARENT_TYPE_MEASUREMENT)) {
				String id = ServiceUtils.extactIdFromParentFilter(context, filter, ChannelGroup.PARENT_TYPE_MEASUREMENT);
				return this.navigationActivity.getChannelGroups(sourceName, id);
			}

			return this.searchActivity.search(context, ChannelGroup.class, filter);

		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns a {@link ChannelGroup identified by the given id
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param channelGroupId
	 *            id of the {@link ChannelGroup}
	 * @return the matching {@link ChannelGroup}
	 */
	public ChannelGroup getChannelGroup(String sourceName, String channelGroupId) {
		try {
			EntityManager em = this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
			return em.load(ChannelGroup.class, channelGroupId);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns localized {@link ChannelGroup} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ChannelGroup} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.I18NActivity.localizeAttributes(sourceName, ChannelGroup.class);
	}

	/**
	 * returns the localized {@link ChannelGroup} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ChannelGroup} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.I18NActivity.localizeType(sourceName, ChannelGroup.class);
	}
}
