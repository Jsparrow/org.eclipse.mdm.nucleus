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

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I18NActivity Bean implementation to lookup localizations for business objects
 * attributes and types
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class I18NActivity {

	private static final Logger LOG = LoggerFactory.getLogger(I18NActivity.class); 
	
	private String RESOURCE_FILE = "org/eclipse/mdm/businessobjects/control/i18n/locale/localization";
	private ResourceBundle LOCALIZATION_RESOURCES = ResourceBundle.getBundle(RESOURCE_FILE);

	@EJB
	private ConnectorService connectorService;

	private boolean globalLocalization = true;

	
	
	/**
	 * localizes all attributes names from a type (e.g. TestStep.class)
	 *  
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param type MDM business object type (e.g. TestStep.class)
	 * @return a Map with localized attribute names (key is attribute name, value is localized attribute name)
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName, Class<? extends Entity> type) {
		
		Map<Attribute, String> localizedAttributeMap = new HashMap<>();
			
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);			
		EntityType entityType = lookupEntityType(em, type);		
				
		String typeKey = createLocalizationTypeKey(sourceName, entityType);
		
		for(Attribute attribute :entityType.getAttributes()) {	
			String attributeName = attribute.getName();		
			String localizationKey = typeKey + "." + attributeName;
			String localization = localize(localizationKey, attributeName);
			localizedAttributeMap.put(attribute, localization);
		}
		
		return localizedAttributeMap;		
	}
	

	
	/**
	 * localizes the type name of the given type (e.g. TestStep.class)
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param type type to localize its type name
	 * @return the localizes type name (key is the original type name, valu is the localized type name)
	 */
	public Map<EntityType, String> localizeType(String sourceName, Class<? extends Entity> type)  {	
		
		Map<EntityType, String> localizedEntityTypeMap = new HashMap<>();
		
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		EntityType entityType = lookupEntityType(em, type);
		String typeKey = createLocalizationTypeKey(sourceName, entityType);
		String localization = localize(typeKey, type.getSimpleName());
		localizedEntityTypeMap.put(entityType, localization);
		return localizedEntityTypeMap;
		
	}
	
	
	
	private EntityType lookupEntityType(EntityManager em, Class<? extends Entity> type) {
		ModelManager modelManager = ServiceUtils.getModelMananger(em);
		return modelManager.getEntityType(type);
	}
	
	
		
	private String localize(String key, String defaultLocalization) {
		try { 
			return LOCALIZATION_RESOURCES.getString(key);			
		} catch(MissingResourceException e) {
			LOG.debug("unable to locate translation key '" + key + "', no translation possible!");
			return defaultLocalization;
		}
		
	}	
	
	private String createLocalizationTypeKey(String sourceName, EntityType entityType) {
		
		if(this.globalLocalization || sourceName == null || sourceName.trim().length() <= 0) {
			return entityType.getName();
		}
		return sourceName + "." + entityType.getName();
	}
}
