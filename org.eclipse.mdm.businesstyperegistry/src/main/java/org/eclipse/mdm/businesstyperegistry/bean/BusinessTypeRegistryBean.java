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

package org.eclipse.mdm.businesstyperegistry.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.businesstyperegistry.ActionBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryException;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;

/**
 * Bean implementation (BusinessTypeRegistryBeanLI)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Singleton
public class BusinessTypeRegistryBean implements BusinessTypeRegistryBeanLI {

	@Inject
	private BeanManager beanManager;
	
	@EJB
	private ConnectorBeanLI connectorBean;
		
	@Override
	public List<ActionBeanLI> getActions(String sourceName, Class<? extends Entity> type)
			throws BusinessTypeRegistryException {
		
		List<ActionBeanLI> actions = lookupActions();
			
		for(ActionBeanLI action : actions) {
			if(!isActionSupported(action, type)) {
				actions.remove(action);
			}
		}
			
		return actions;	
	}
	
	

	@Override
	public URI createURI(String sourceName, Class<? extends Entity> type, long id) throws BusinessTypeRegistryException {
		
		try {
		
			EntityManager em = this.connectorBean.getEntityManagerByName(sourceName);
			Optional<ModelManager> oMM = em.getModelManager();
			if(!oMM.isPresent()) {
				throw new BusinessTypeRegistryException("neccessary ModelManager is not present!");
			}
			ModelManager modelManager = oMM.get();
			String typeName = modelManager.getEntityType(type).getName();
			return new URI(sourceName, typeName, id);
		
		} catch(ConnectorException e) {
			throw new BusinessTypeRegistryException(e.getMessage(), e);
		}
	}

	
	
	/**
	 * using the bean manager to lookup all available actions (ActionsBean implementations)
	 * 
	 * @return all available actions (ActionBeans implementations)
	 */
	private List<ActionBeanLI> lookupActions() {
		List<ActionBeanLI> actionList = new ArrayList<>();
		Set<Bean<?>> set = this.beanManager.getBeans(ActionBeanLI.class);
		Bean<?>[] beans = set.toArray(new Bean<?>[set.size()]);
		for(Bean<?> bean : beans) {
			CreationalContext<?> ctx = this.beanManager.createCreationalContext(bean);
			ActionBeanLI action = (ActionBeanLI) this.beanManager.getReference(bean,
					ActionBeanLI.class, ctx);
			actionList.add(action);
		}
		return actionList;
	}

	
	
	/**
	 * returns true if the action is supported by the given type
	 * 
	 * @param action registered and available ActionBean
	 * @param type MDM business object type (e.g. TestStep.class)
	 * @return true if the action is supported
	 */
	private boolean isActionSupported(ActionBeanLI action, Class<? extends Entity> type) {
		List<Class<? extends Entity>> supportedEntityTypes = action.getSupportedEntityTypes();
		for(Class<? extends Entity> supportedEntityType : supportedEntityTypes) {
			if(type.equals(supportedEntityType)) {
				return true;
			}
		}
		return false;
	}

}
