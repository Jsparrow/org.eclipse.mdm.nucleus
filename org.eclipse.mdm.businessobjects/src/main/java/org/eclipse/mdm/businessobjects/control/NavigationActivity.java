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

package org.eclipse.mdm.businessobjects.control;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * NavigationActivity Bean implementation to lookup specified business object
 * children
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class NavigationActivity {

	@EJB
	private ConnectorService connectorService;

	/**
	 * returns the MDM {@link Environment} business objects of all connected MDM
	 * systems
	 * 
	 * @return MDM {@link Environment} business objects
	 */
	public List<Environment> getEnvironments() {
		try {
			List<Environment> envList = new ArrayList<>();
			List<EntityManager> emList = this.connectorService.getEntityManagers();
			for (EntityManager em : emList) {
				envList.add(em.loadEnvironment());
			}
			return envList;
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns all MDM {@link Project} business objects of the connected MDM
	 * system identified by the given name
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @return MDM {@link Project} business objects
	 */
	public List<Project> getProjects(String sourceName) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.loadAll(Project.class);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns all MDM {@link Test} business objects of the connected MDM system
	 * identified by the given name
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @return MDM {@link Test} business objects
	 */
	public List<Test> getTests(String sourceName) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.loadAll(Test.class);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns all MDM {@link Test} business object children for a MDM
	 * {@link Pool} identified by the given source name and {@link Pool} ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param poolID
	 *            The {@code Pool} instance ID
	 * @return MDM {@link Test} business objects
	 */
	public List<Test> getTests(String sourceName, String poolID) {
		return getChildren(sourceName, Pool.class, poolID, Test.class);
	}

	/**
	 * returns all MDM {@link Pool} business object children for a MDM
	 * {@link Project} identified by the given source name and {@link Project}
	 * ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param projectID
	 *            The {@code Project} instance ID
	 * @return MDM {@link Pool} business objects
	 */
	public List<Pool> getPools(String sourceName, String projectID) {
		return getChildren(sourceName, Project.class, projectID, Pool.class);
	}

	/**
	 * returns all MDM {@link TestStep} business object children for a MDM
	 * {@link Test} identified by the given source name and {@link Test} ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param testID
	 *            The {@code Test} instance ID
	 * @return MDM {@link TestStep} business objects
	 */
	public List<TestStep> getTestSteps(String sourceName, String testID) {
		return getChildren(sourceName, Test.class, testID, TestStep.class);
	}

	/**
	 * returns all MDM {@link Measurement} business object children for a MDM
	 * {@link TestStep} identified by the given source name and {@link TestStep}
	 * ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param testStepID
	 *            The {@code TestStep} instance ID
	 * @return MDM {@link Measurement} business objects
	 */
	public List<Measurement> getMeasurements(String sourceName, String testStepID) {
		return getChildren(sourceName, TestStep.class, testStepID, Measurement.class);
	}

	/**
	 * returns all MDM {@link ChannelGroup} business object children for a MDM
	 * {@link Measurement} identified by the given source name and
	 * {@link Measurement} ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param measurementID
	 *            The {@code Measurement} instance ID
	 * @return MDM {@link ChannelGroup} business objects
	 */
	public List<ChannelGroup> getChannelGroups(String sourceName, String measurementID) {
		return getChildren(sourceName, Measurement.class, measurementID, ChannelGroup.class);
	}

	/**
	 * returns all MDM {@link Channel} business object children for a MDM
	 * {@link ChannelGroup} identified by the given source name and
	 * {@link ChannelGroup} ID.
	 * 
	 * @param sourceName
	 *            Name of the MDM system
	 * @param channelGroupID
	 *            The {@code ChannelGroup} instance ID
	 * @return MDM {@link Channel} business objects
	 */
	public List<Channel> getChannels(String sourceName, String channelGroupID) {
		return getChildren(sourceName, ChannelGroup.class, channelGroupID, Channel.class);
	}

	private <T extends Entity> List<T> getChildren(String sourceName, Class<? extends Entity> parentType, String parentID,
			Class<T> childType) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			Entity parent = em.load(parentType, parentID);
			return em.loadChildren(parent, childType);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

}
