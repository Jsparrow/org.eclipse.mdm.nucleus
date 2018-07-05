/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.mdm.freetextindexer.boundary;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.notification.NotificationException;
import org.eclipse.mdm.api.base.notification.NotificationFilter;
import org.eclipse.mdm.api.base.notification.NotificationListener;
import org.eclipse.mdm.api.base.notification.NotificationService;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.freetextindexer.control.UpdateIndex;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.eclipse.mdm.property.GlobalProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This boundary is a back-end Boundary to the openMDM Api. It uses the Seach
 * Server to build up MDDocuments.
 * 
 * @author CWE
 *
 */
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Stateful
public class MdmApiBoundary {

	private static final Logger LOGGER = LoggerFactory.getLogger(MdmApiBoundary.class);

	private static final String FREETEXT_NOTIFICATION_NAME = "freetext.notificationName";

	public class FreeTextNotificationListener implements NotificationListener {

		private EntityManager entityManager;

		public FreeTextNotificationListener(EntityManager entityManager) {
			this.entityManager = entityManager;
		}

		@Override
		public void instanceCreated(List<? extends Entity> entities, User arg1) {
			LOGGER.debug("{} entities created: {}", entities.size(), entities);
			entities.forEach(e -> update.change(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void instanceDeleted(EntityType entityType, List<String> ids, User user) {
			LOGGER.debug("{} entities deleted: {}", ids.size(), ids);
			ids.forEach(id -> update.delete(getApiName(entityManager), workaroundForTypeMapping(entityType), id));
		}

		@Override
		public void instanceModified(List<? extends Entity> entities, User arg1) {
			LOGGER.debug("{} entities modified: {}", entities.size(), entities);
			entities.forEach(e -> update.change(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void modelModified(EntityType arg0, User arg1) {
			// not needed
		}

		@Override
		public void securityModified(EntityType entityType, List<String> ids, User user) {
			// not needed
		}
	}

	@Inject
	ConnectorService service;

	@Inject
	UpdateIndex update;

	@Inject
	@GlobalProperty(value = "freetext.active")
	private String active = "false";

	ConnectorService connectorService; 

	@Inject
	@GlobalProperty
	private Map<String, String> globalProperties = Collections.emptyMap();

	@PostConstruct
	public void initalize() {
		Principal principal = new Principal() {
			
			@Override
			public String getName() {
				return null;
			}
		};
		
		connectorService = new ConnectorService(principal, globalProperties);
		connectorService.connect();
		connectorService.getContexts().forEach(this::initializeContext);
	}

	private void initializeContext(ApplicationContext context) {

		String source = getName(context);

		try {
			EntityManager entityManager = context.getEntityManager()
					.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class));

			NotificationService manager = context.getNotificationService()
					.orElseThrow(() -> new ConnectionException("Context has no NotificationManager!"));

			String notificationName = context.getParameters().getOrDefault(FREETEXT_NOTIFICATION_NAME, "mdm5");
			LOGGER.debug("Registering with name '{}' at source '{}'", notificationName, source);
			
			manager.register(notificationName, new NotificationFilter(), new FreeTextNotificationListener(entityManager));
			LOGGER.info("Successfully registered for new notifications with name '{}' at source '{}!", notificationName, source);
		} catch (ConnectionException | NotificationException e) {
			throw new IllegalArgumentException("The ODS Server and/or the Notification Service cannot be accessed for source '" + source + "'!",
					e);
		}
	}

	@PreDestroy
	public void deregister() {
		for (ApplicationContext context : getContexts().values()) {
			try {
				context.getNotificationService()
					.orElseThrow(() -> new ConnectionException("Context has no NotificationManager!"))
					.close(false);

			} catch (ConnectionException | NotificationException e) {
				throw new IllegalStateException(
						"The NotificationManager could not be deregistered. In rare cases, this leads to a missed notification. This means the index might not be up-to-date.");
			}
		}
		
		connectorService.disconnect();
	}

	public void doForAllEntities(Class<? extends Entity> entityClass, ApplicationContext context, Consumer<? super MDMEntityResponse> executor) {
		if (isActive()) {
			try {
				EntityManager entityManager = context.getEntityManager()
						.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class));

				entityManager.loadAll(entityClass).stream().map(e -> this.buildEntity(e, entityManager)).forEach(executor);

			} catch (DataAccessException e) {
				throw new IllegalStateException("MDM cannot be querried for new elements. Please check the MDM runtime", e);
			}
		}
	}

	public Map<String, ApplicationContext> getContexts() {
		return connectorService.getContexts().stream().collect(Collectors.toMap(this::getName, Function.identity()));
	}

	public String getName(ApplicationContext context) {
		return context.getEntityManager()
				.map(this::getApiName)
				.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class));
	}

	private String getApiName(EntityManager entityManager) {
		return entityManager.loadEnvironment().getSourceName();
	}

	private boolean isActive() {
		return Boolean.parseBoolean(active) && !getContexts().isEmpty();
	}

	private MDMEntityResponse buildEntity(Entity e, EntityManager entityManager) {
		return MDMEntityResponse.build(e.getClass(), e, entityManager);
	}

	/**
	 * Simple workaround for naming mismatch between Adapter and Business object
	 * names.
	 *
	 * @param entityType
	 *            entity type
	 * @return MDM business object name
	 */
	private static String workaroundForTypeMapping(EntityType entityType) {
		switch (entityType.getName()) {
		case "StructureLevel":
			return "Pool";
		case "MeaResult":
			return "Measurement";
		case "SubMatrix":
			return "ChannelGroup";
		case "MeaQuantity":
			return "Channel";
		default:
			return entityType.getName();
		}
	}
}