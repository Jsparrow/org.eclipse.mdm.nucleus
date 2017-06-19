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
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I18NActivity Bean implementation to lookup localizations for business objects
 * attributes and types
 * 
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

	/**
	 * localizes all attributes names from a type (e.g. TestStep.class)
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param type
	 *            MDM business object type (e.g. TestStep.class)
	 * @return a {@link Map} with localized {@link Attribute} names (key is
	 *         attribute name, value is localized attribute name)
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName, Class<? extends Entity> type) {

		Map<Attribute, String> map = new HashMap<>();

		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		EntityType entityType = lookupEntityType(em, type);
		localizeAttributes(entityType, map);

		return map;
	}

	/**
	 * localizes the type name of the given type (e.g. TestStep.class)
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param type
	 *            type to localize its type name
	 * @return the localizes type name (key is the original type name, value is
	 *         the localized type name)
	 */
	public Map<EntityType, String> localizeType(String sourceName, Class<? extends Entity> type) {

		Map<EntityType, String> map = new HashMap<>();

		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		EntityType entityType = lookupEntityType(em, type);
		localizeType(entityType, map);

		return map;
	}

	/**
	 * localizes all {@link Attribute}s of all types from the source identified
	 * by the given soureName
	 * 
	 * @param sourceName
	 *            source name to identify the MDM data source
	 * @return a {@link Map} with localized {@link Attribute} names (key is
	 *         attribute name, value is localized attribute name)
	 */
	public Map<Attribute, String> localizeAllAttributes(String sourceName) {

		Map<Attribute, String> map = new HashMap<>();
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		List<EntityType> list = lookupAllEntityTypes(em);

		for (EntityType entityType : list) {
			localizeAttributes(entityType, map);
		}

		return map;
	}

	/**
	 * localizes all types form the source identified by the given sourceName
	 * 
	 * @param sourceName
	 *            source name to identify the MDM data source
	 * @return the localizes type names (key is the original type name, value is
	 *         the localized type name)
	 */
	public Map<EntityType, String> localizeAllTypes(String sourceName) {

		Map<EntityType, String> map = new HashMap<>();
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		List<EntityType> list = lookupAllEntityTypes(em);

		for (EntityType entityType : list) {
			localizeType(entityType, map);
		}

		return map;
	}

	private void localizeType(EntityType entityType, Map<EntityType, String> map) {
		String localization = localize(entityType.getName(), entityType.getName());
		map.put(entityType, localization);
	}

	private void localizeAttributes(EntityType entityType, Map<Attribute, String> map) {

		for (Attribute attribute : entityType.getAttributes()) {
			String key = entityType.getName() + "." + attribute.getName();
			String localization = localize(key, attribute.getName());
			map.put(attribute, localization);
		}

	}

	private EntityType lookupEntityType(EntityManager em, Class<? extends Entity> type) {
		ModelManager modelManager = ServiceUtils.getModelMananger(em);
		return modelManager.getEntityType(type);
	}

	private List<EntityType> lookupAllEntityTypes(EntityManager em) {
		ModelManager modelManager = ServiceUtils.getModelMananger(em);
		return modelManager.listEntityTypes();
	}

	private String localize(String key, String defaultValue) {
		try {
			return LOCALIZATION_RESOURCES.getString(key);
		} catch (MissingResourceException e) {
			LOG.debug("unable to localize key '" + key + "', no translation possible!");
			return defaultValue;
		}
	}

}
