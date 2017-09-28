package org.eclipse.mdm.businessobjects.boundary;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.utils.DataAccessHelper;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.collection.Stream;

@Stateless
public class EntityService {

	@EJB
	private ConnectorService connectorService;

	/**
	 * Creates a new {@link Entity} of type entityClass. The method searches the
	 * {@link EntityFactory} for a suitable create() method by matching the return
	 * parameter and the given entity class. If more than one method is found, the
	 * first one is taken.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to create
	 * @param name
	 *            name of the {@link ValueList} to be created
	 * @return created {@link ValueList}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Optional<T> create(String sourceName, Class<T> entityClass, Object... createMethodArgs) {
		EntityManager em = connectorService.getEntityManagerByName(sourceName);
		Optional<T> entity;

		// TODO test with wrong arguments
		// gather classes of method args
		List<Class<? extends Object>> argClasses = Stream.of(createMethodArgs).map(o -> o.getClass())
				.collect(Collectors.toList());

		// get corresponding create method for Entity from EntityFactory
		entity = em.getEntityFactory().map(factory -> {
			try {
				return (T) Stream.of(EntityFactory.class.getMethods())
						// find method with the return type matching entityClass
						.filter(m -> m.getReturnType().equals(entityClass))
						.filter(m -> Arrays.asList(m.getParameterTypes()).equals(argClasses)).getOrElseThrow(() -> {
							throw new MDMEntityAccessException(
									"No matching create()-method found for EntityType " + entityClass.getSimpleName());
						}).invoke(factory, createMethodArgs);
			} catch (Exception e) {
				throw new MDMEntityAccessException(e.getMessage(), e);
			}
		});

		entity.ifPresent(e -> {
			try {
				// start transaction to create the entity
				DataAccessHelper.execute().apply(em, e, DataAccessHelper.create());
			} catch (Throwable t) {
				throw new MDMEntityAccessException(t.getMessage(), t);
			}
		});

		return entity;
	}

	/**
	 * Deletes the {@link Entity} with the given identifier.
	 *
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param identifier
	 *            The id of the {@link Entity} to delete.
	 */
	public <T extends Entity> Optional<T> delete(String sourceName, Class<T> entityClass, String id) {
		try {
			EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
			T entityObject = entityManager.load(entityClass, id);

			Optional<T> entity = Optional.ofNullable(entityObject);

			entity.ifPresent(e -> {
				try {
					// start transaction to delete the entity
					DataAccessHelper.execute().apply(entityManager, entity.get(), DataAccessHelper.delete());
				} catch (Throwable t) {
					throw new MDMEntityAccessException(t.getMessage(), t);
				}
			});

			return entity;
		} catch (Throwable t) {
			throw new MDMEntityAccessException(t.getMessage(), t);
		}
	}
}