package org.eclipse.mdm.businessobjects.boundary;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.utils.DataAccessHelper;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.collection.Stream;

@Stateless
public class EntityService {

	@EJB
	private ConnectorService connectorService;

	@EJB
	private SearchActivity searchActivity;

	/**
	 * Returns a {@link Entity} identified by the given id.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link Entity} to find
	 * @return found {@link Entity}
	 */
	public <T extends Entity> Optional<T> find(Class<T> entityClass, String sourceName, String id) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return Optional.ofNullable(em.load(entityClass, id));
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the matching {@link Entity}s using the given filter or all
	 * {@link Entity}s if no filter is available
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Entity} result
	 * @return found {@link Entity}
	 */
	public <T extends Entity> Optional<List<T>> findAll(Class<T> entityClass, String sourceName, String filter) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			if (filter == null || filter.trim().length() <= 0) {
				return Optional.ofNullable(em.loadAll(entityClass));
			}

			return Optional.ofNullable(this.searchActivity.search(em, entityClass, filter));
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new {@link Entity} of type entityClass. The method searches the
	 * {@link EntityFactory} for a suitable create() method by matching the return
	 * parameter and the given entity class. If more than one method is found, the
	 * first one is taken.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to create
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param name
	 *            name of the {@link ValueList} to be created
	 * @return created {@link Entity}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Optional<T> create(Class<T> entityClass, String sourceName, Object... createMethodArgs) {
		EntityManager em = connectorService.getEntityManagerByName(sourceName);
		Optional<T> entity;

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
									"No matching create()-method found for EntityType " + entityClass.getSimpleName()
											+ " taking the parameters " + Stream.of(createMethodArgs)
													.map(o -> o.getClass().getName()).collect(Collectors.toList()));
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
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param identifier
	 *            The id of the {@link Entity} to delete.
	 */
	public <T extends Entity> Optional<T> delete(Class<T> entityClass, String sourceName, String id) {
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