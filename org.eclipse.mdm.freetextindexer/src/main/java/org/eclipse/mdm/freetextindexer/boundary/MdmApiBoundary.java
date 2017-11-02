package org.eclipse.mdm.freetextindexer.boundary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.notification.NotificationException;
import org.eclipse.mdm.api.base.notification.NotificationFilter;
import org.eclipse.mdm.api.base.notification.NotificationListener;
import org.eclipse.mdm.api.base.notification.NotificationService;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.freetextindexer.control.UpdateIndex;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.eclipse.mdm.property.GlobalProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This boundary is a back-end Boundary to the openMDM Api. It uses the Seach
 * Server to build up {@link MDMDocument}s.
 * 
 * @author CWE
 *
 */
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Stateful
public class MdmApiBoundary {

	private static final Logger LOGGER = LoggerFactory.getLogger(MdmApiBoundary.class);

	public class FreeTextNotificationListener implements NotificationListener {
		@Override
		public void instanceCreated(List<? extends Entity> entities, User arg1) {
			entities.forEach(e -> update.change(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void instanceDeleted(EntityType entityType, List<String> ids, User user) {
			ids.forEach(id -> update.delete(getApiName(), entityType.getName(), id));
		}

		@Override
		public void instanceModified(List<? extends Entity> entities, User arg1) {
			LOGGER.debug(entities.size() + " entities found: " + entities);
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
	@GlobalProperty(value = "freetext.notificationType")
	private String notificationType;

	@Inject
	@GlobalProperty(value = "freetext.notificationUrl")
	private String notificationUrl;

	@Inject
	@GlobalProperty(value = "freetext.notificationMimeType")
	private String notificationMimeType;

	@Inject
	@GlobalProperty(value = "freetext.notificationName")
	private String notificationName;

	@Inject
	@GlobalProperty(value = "freetext.pollingInterval")
	private String pollingInterval;

	@Inject
	@GlobalProperty(value = "freetext.active")
	private String active = "false";

	
	private ApplicationContext context;
	private EntityManager entityManager;
	private NotificationService manager;
	private Map<String, String> properties = new HashMap<>();
	
	@PostConstruct
	public void initalize() {
		properties.put("freetext.active", active);
		properties.put("freetext.notificationType", notificationType);
		properties.put("freetext.notificationUrl", notificationUrl);
		properties.put("freetext.notificationMimeType", notificationMimeType);
		properties.put("freetext.notificationName", notificationName);
		properties.put("freetext.pollingInterval", pollingInterval);
		
		if (isActive()) {
			try {
				List<ApplicationContext> connectionList = service.connectFreetextSearch("freetext.user", "freetext.password");
				if (connectionList.isEmpty()) {
					throw new IllegalArgumentException("Cannot connect to MDM from Freetextindexer. Seems like the technical user/password is not correct.");
				}

				context = connectionList.get(0);
				properties.putAll(context.getParameters());
				
				entityManager = context.getEntityManager()
						.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class));
				manager = context.getNotificationService()
						.orElseThrow(() -> new ConnectionException("Context has no NotificationManager!"));
				manager.register(notificationName, new NotificationFilter(), new FreeTextNotificationListener());

				LOGGER.info("Successfully registered for new Notifications!");
			} catch (ConnectionException | NotificationException e) {
				throw new IllegalArgumentException("The ODS Server and/or the Notification Service cannot be accessed.",
						e);
			}
		} else {
			LOGGER.warn("Freetextsearch is not active!");
		}
	}

	@PreDestroy
	public void deregister() {
		try {
			manager.close(false);
		} catch (NotificationException e) {
			throw new IllegalStateException(
					"The NotificationManager could not be deregistered. In rare cases, this leads to a missed notification. This means the index might not be up-to-date.");
		}
	}

	public void doForAllEntities(Consumer<? super MDMEntityResponse> executor) {
		if (isActive()) {
			try {
				entityManager.loadAll(Test.class).stream().map(this::buildEntity).forEach(executor);
				entityManager.loadAll(TestStep.class).stream().map(this::buildEntity).forEach(executor);
				entityManager.loadAll(Measurement.class).stream().map(this::buildEntity).forEach(executor);
			} catch (DataAccessException e) {
				throw new IllegalStateException("MDM cannot be querried for new elements. Please check the MDM runtime",
						e);
			}
		}
	}

	public String getApiName() {
		String apiName = "";
		if (isActive()) {
			try {
				apiName = entityManager.loadEnvironment().getSourceName();
			} catch (DataAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		return apiName;
	}
	
	private boolean isActive() {
		return Boolean.valueOf(properties.get("freetext.active"));
	}
	
	private MDMEntityResponse buildEntity(Entity e) {
		return MDMEntityResponse.build(e.getClass(), e, entityManager);
	}
}
