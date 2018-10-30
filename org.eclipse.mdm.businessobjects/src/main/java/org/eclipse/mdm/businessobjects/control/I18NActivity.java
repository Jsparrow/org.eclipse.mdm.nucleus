/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


package org.eclipse.mdm.businessobjects.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.ApplicationContext;
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

	@Inject
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

		ApplicationContext context = this.connectorService.getContextByName(sourceName);
		
		EntityType entityType = lookupEntityType(context, type);
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

		ApplicationContext context = this.connectorService.getContextByName(sourceName);
		EntityType entityType = lookupEntityType(context, type);
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
		ApplicationContext context = this.connectorService.getContextByName(sourceName);
		List<EntityType> list = lookupAllEntityTypes(context);

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
		ApplicationContext context = this.connectorService.getContextByName(sourceName);
		List<EntityType> list = lookupAllEntityTypes(context);

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

	private EntityType lookupEntityType(ApplicationContext context, Class<? extends Entity> type) {
		return context.getModelManager()
			.map(mm -> mm.getEntityType(type))
			.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
	}

	private List<EntityType> lookupAllEntityTypes(ApplicationContext context) {
		return context.getModelManager()
				.map(mm -> mm.listEntityTypes())
				.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
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
