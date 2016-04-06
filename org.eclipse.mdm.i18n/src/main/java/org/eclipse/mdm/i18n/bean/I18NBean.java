/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.i18n.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;
import org.eclipse.mdm.i18n.I18NBeanLI;
import org.eclipse.mdm.i18n.I18NException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link I18NBeanLI}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
@LocalBean
public class I18NBean implements I18NBeanLI {

	private static final Logger LOG = LoggerFactory.getLogger(I18NBean.class); 
	
	private String RESOURCE_FILE = "org/eclipse/mdm/i18n/locale/localization";
	private ResourceBundle LOCALIZATION_RESOURCES = ResourceBundle.getBundle(RESOURCE_FILE);

	@EJB
	private ConnectorBeanLI connectorBean;


	@Override
	public Map<String, String> localizeType(String sourceName, Class<? extends Entity> type) throws I18NException {
	
		try {
			
			Map<String, String> localizedMap = new HashMap<>();
			
			EntityManager em = this.connectorBean.getEntityManagerByName(sourceName);			
			List<Attribute> attributeList = lookupAttributes(em, type);			
			
			String typeName = type.getSimpleName();
			
			localizedMap.put(typeName, localize(typeName, typeName));
			
			for(Attribute attribute : attributeList) {
				String attributeName = attribute.getName();
				String localization = localize(typeName + "." + attributeName, attributeName);
				localizedMap.put(attributeName, localization);
			}
			
			return localizedMap;
		
		} catch(ConnectorException e) {
			throw new I18NException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * lookup the attributes for the given MDM business object type
	 * 
	 * @param em {@link EntityManager} to get model access via the {@link ModelManager}
	 * @param type MDM business object type (e.g. TestStep.class)
	 * @return all available attributes for the given type
	 * @throws I18NException if an error occurs during the attribute lookup operation
	 */
	private List<Attribute> lookupAttributes(EntityManager em, Class<? extends Entity> type) throws I18NException {
		Optional<ModelManager> oMM = em.getModelManager();
		if(!oMM.isPresent()) {
			throw new I18NException("neccessary ModelManager is not present!");
		}
		
		ModelManager modelManager = oMM.get();
		EntityType entityType = modelManager.getEntityType(type);
		return entityType.getAttributes();
	}
	
	
	
	/**
	 * localizes the given attribute key
	 * 
	 * @param key attribute key (TYPENAME.ATTRIBUTENAME (e.g. TestStep.Name)
	 * @param defaultLocalization default return value if no localization found for the given key
	 * @return the localized name or the default localization value
	 */
	private String localize(String key, String defaultLocalization) {
		try { 
			return LOCALIZATION_RESOURCES.getString(key);			
		} catch(MissingResourceException e) {
			LOG.debug("unable to locate translation key '" + key + "', no translation possible!");
			return defaultLocalization;
		}
		
	}	
}
