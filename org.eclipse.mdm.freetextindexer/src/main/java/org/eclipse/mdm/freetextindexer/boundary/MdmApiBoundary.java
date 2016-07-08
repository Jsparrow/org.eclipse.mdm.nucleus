package org.eclipse.mdm.freetextindexer.boundary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.BaseEntityManager;
import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.notification.NotificationException;
import org.eclipse.mdm.api.base.notification.NotificationFilter;
import org.eclipse.mdm.api.base.notification.NotificationListener;
import org.eclipse.mdm.api.base.notification.NotificationManager;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory;
import org.eclipse.mdm.api.odsadapter.ODSNotificationManagerFactory;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.freetextindexer.control.UpdateIndex;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This boundary is a back-end Boundary to the openMDM Api. It uses the Seach
 * Server to build up {@link MDMDocument}s.
 * 
 * @author CWE
 *
 */
@Singleton
@Startup
public class MdmApiBoundary {

	private static final Logger LOGGER = LoggerFactory.getLogger(MdmApiBoundary.class);

	public class FreeTextNotificationListener implements NotificationListener {
		@Override
		public void instanceCreated(List<? extends Entity> entities, User arg1) {
			entities.forEach(e -> update.change(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void instanceDeleted(List<? extends Entity> entities, User arg1) {
			entities.forEach(e -> update.delete(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void instanceModified(List<? extends Entity> entities, User arg1) {
			entities.forEach(e -> update.change(MDMEntityResponse.build(e.getClass(), e, entityManager)));
		}

		@Override
		public void modelModified(EntityType arg0, User arg1) {
		}

		@Override
		public void securityModified(List<? extends Entity> arg0, User arg1) {
		}
	}

	@Inject
	ConnectorService service;

	@Inject
	ODSNotificationManagerFactory factory;

	@Inject
	UpdateIndex update;

	private EntityManager entityManager;

	@PostConstruct
	public void initalize() {
		try {
			entityManager = service.connect("sa", "sa").get(0);

			Map<String, String> map = new HashMap<>();
			map.put(ODSNotificationManagerFactory.PARAM_SERVER_TYPE, ODSNotificationManagerFactory.SERVER_TYPE_PEAK);
			map.put(ODSNotificationManagerFactory.PARAM_URL, "http://localhost:8089/api");
			map.put(ODSNotificationManagerFactory.PARAM_EVENT_MEDIATYPE, "application/json");
			map.put(ODSEntityManagerFactory.PARAM_USER, "sa");
			map.put(ODSEntityManagerFactory.PARAM_PASSWORD, "sa");

			NotificationManager notificationManager = factory.create(entityManager, map);
			notificationManager.register("eindeutigerName", new NotificationFilter(),
					new FreeTextNotificationListener());
			
			LOGGER.info("Successfully registered for new Notifications!");
		} catch (ConnectionException | NotificationException e) {
			throw new IllegalArgumentException("The ODS Server and/or the Notification Service cannot be accessed.", e);
		}
	}

	public void doForAllEntities(Consumer<? super MDMEntityResponse> executor) {
		try {
			entityManager.loadAll(Test.class).stream().map(this::buildEntity).forEach(executor);
			entityManager.loadAll(TestStep.class).stream().map(this::buildEntity).forEach(executor);
			entityManager.loadAll(Measurement.class).stream().map(this::buildEntity).forEach(executor);
		} catch (DataAccessException e) {
			throw new IllegalStateException("MDM cannot be querried for new elements. Please check the MDM runtime", e);
		}
	}

	public String getApiName() {
		try {
			return entityManager.loadEnvironment().getName();
		} catch (DataAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private MDMEntityResponse buildEntity(Entity e) {
		return MDMEntityResponse.build(e.getClass(), e, entityManager);
	}
}
