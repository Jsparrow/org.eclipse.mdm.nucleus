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
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * ProjectService Bean implementation with available {@link Project} operations
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Stateless
public class ProjectService {

	@EJB
	private ConnectorService connectorService;	
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private SearchActivity searchActivity;
	
	/**
	 * returns the matching {@link Project}s using the given filter or all {@link Project}s 
	 * if no filter is available
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param filter filter string to filter the {@link Project} result
	 * @return the found {@link Project}s
	 */
	public List<Project> getProjects(String sourceName, String filter) {
		
		try {		
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			
			if(filter == null || filter.trim().length() <= 0) {
				return em.loadAll(Project.class);
			}		
			
			return this.searchActivity.search(em, Project.class, filter);
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the {@link SearchAttribute} for the entity type {@link Project} in the given data source.
	 * @param sourceName The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		return this.searchActivity.listAvailableAttributes(em, Project.class);
	}
	
	/**
	 * returns a {@link Project} identified by the given id.
	 * @param projectId id of the {@link Project}
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param testStepId id of the {@link Project}
	 * @return the matching {@link Project}
	 */
	public Project getProject(String sourceName, long projectId) {
		try {		
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.load(Project.class, projectId);
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	/**
	 * returns localized {@link Project} attributes
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Project} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {		
		return this.i18nActivity.localizeAttributes(sourceName, Project.class);
	}	
	
	/**
	 * returns the localized {@link Project} type name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Project} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Project.class);
	}
}
