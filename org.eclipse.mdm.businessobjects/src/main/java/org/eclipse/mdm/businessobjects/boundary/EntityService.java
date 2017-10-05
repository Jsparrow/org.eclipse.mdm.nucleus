/*******************************************************************************
 * Copyright (c) 2017 science + computing AG Tuebingen (ATOS SE)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Nehmer - initial implementation
 *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.utils.DataAccessHelper;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Class providing basic data access methods to Entities.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Stateless
public class EntityService {

	@EJB
	private ConnectorService connectorService;

	@EJB
	private SearchActivity searchActivity;

	@EJB
	private I18NActivity i18nActivity;

	private static Consumer<? super Throwable> throwException = e -> {
		throw new MDMEntityAccessException(e.getMessage(), e);
	};

	// TODO unify NON-ContextType and ContextType methods

	/**
	 * 
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
	public <T extends Entity> Option<T> find(Class<T> entityClass, String sourceName, String id) {
		// TODO error handling: how to inform caller about what happened? Try?
		// TODO handle "Connector Service not found", "Source not found"
		return Try.of(() -> this.connectorService.getEntityManagerByName(sourceName))
				// TODO handle "Entity not found"
				.mapTry(em -> em.load(entityClass, id))
				.onFailure(throwException)
				.toOption();
	}

	/**
	 * 
	 * Returns a {@link Entity} with the given {@link EntityType} and j * identified
	 * by the given id.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param contextType
	 *            the {@link ContextType} of the entity to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link Entity} to find
	 * @return found {@link Entity}
	 */
	// TODO needed?
	public <T extends Entity> Option<T> find(Class<T> entityClass, ContextType contextType, String sourceName,
			String id) {
		// TODO error handling: how to inform caller about what happened? Try?
		// TODO handle "Connector Service not found", "Source not found"
		return Try.of(() -> this.connectorService.getEntityManagerByName(sourceName))
				// TODO handle "Entity not found"
				.mapTry(em -> em.load(entityClass, contextType, id))
				.onFailure(throwException)
				.toOption();
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
	public <T extends Entity> List<T> findAll(Class<T> entityClass, String sourceName, String filter) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			List<T> entities = null;
			if (filter == null || filter.trim()
					.length() <= 0) {
				entities = em.loadAll(entityClass);

			} else {
				entities = this.searchActivity.search(em, entityClass, filter);
			}

			// return empty list if nothing was found just in case the backend methods would
			// return null
			return entities != null ? entities : new ArrayList<T>();
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the matching {@link Entity}s of the given contextType using the given
	 * filter or all {@link Entity}s of the given contextType if no filter is
	 * available
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Entity} result
	 * @return found {@link Entity}
	 */

	public <T extends Entity> List<T> findAll(Class<T> entityClass, ContextType contextType, String sourceName,
			String filter) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			List<T> entities = null;
			if (filter == null || filter.trim()
					.length() <= 0) {
				entities = em.loadAll(entityClass, contextType);

			} else {
				entities = this.searchActivity.search(em, entityClass, filter);
			}

			// return empty list if nothing was found just in case the backend methods would
			// return null
			return entities != null ? entities : new ArrayList<T>();
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
	public <T extends Entity> Option<T> create(Class<T> entityClass, String sourceName, Object... createMethodArgs) {
		EntityManager em = connectorService.getEntityManagerByName(sourceName);
		Option<T> entity;

		// gather classes of method args
		List<Class<? extends Object>> argClasses = Stream.of(createMethodArgs)
				.map(o -> o.getClass())
				.collect(Collectors.toList());

		// get corresponding create method for Entity from EntityFactory
		entity = Option.ofOptional(em.getEntityFactory())
				.map(factory -> {
					try {
						return (T) Stream.of(EntityFactory.class.getMethods())
								// find method with the return type matching entityClass
								.filter(m -> m.getReturnType()
										.equals(entityClass))
								.filter(m -> Arrays.asList(m.getParameterTypes())
										.equals(argClasses))
								.getOrElseThrow(() -> {
									throw new MDMEntityAccessException(
											"No matching create()-method found for EntityType "
													+ entityClass.getSimpleName() + " taking the parameters "
													+ Stream.of(createMethodArgs)
															.map(o -> o.getClass()
																	.getName())
															.collect(Collectors.toList()));
								})
								.invoke(factory, createMethodArgs);
					} catch (Exception e) {
						throw new MDMEntityAccessException(e.getMessage(), e);
					}
				});

		// start transaction to create the entity
		entity.toTry()
				.mapTry(e -> DataAccessHelper.execute()
						.apply(em, e, DataAccessHelper.create()))
				.onFailure(throwException);

		return entity;
	}

	/**
	 * Deletes the {@link Entity} with the given identifier.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @param identifier
	 *            The id of the {@link Entity} to delete.
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	public <T extends Entity> Optional<T> delete(Class<T> entityClass, String sourceName, ContextType contextType,
			String id) {
		try {
			EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
			T entityObject = entityManager.load(entityClass, contextType, id);

			Optional<T> entity = Optional.ofNullable(entityObject);

			// TODO do it the functional way
			entity.ifPresent(e -> {
				try {
					// start transaction to delete the entity
					// TODO change construct that Throwable has not to be catched here (Sonar
					// issues)
					DataAccessHelper.execute()
							.apply(entityManager, entity.get(), DataAccessHelper.delete());
				} catch (Throwable t) {
					throw new MDMEntityAccessException(t.getMessage(), t);
				}
			});

			return entity;
		} catch (Throwable t) {
			throw new MDMEntityAccessException(t.getMessage(), t);
		}
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
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	public <T extends Entity> Optional<T> delete(Class<T> entityClass, String sourceName, String id) {
		try {
			EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
			T entityObject = entityManager.load(entityClass, id);

			Optional<T> entity = Optional.ofNullable(entityObject);

			// TODO do it the functional way
			entity.ifPresent(e -> {
				try {
					// start transaction to delete the entity
					// TODO change construct that Throwable has not to be catched here (Sonar
					// issues)
					DataAccessHelper.execute()
							.apply(entityManager, entity.get(), DataAccessHelper.delete());
				} catch (Throwable t) {
					throw new MDMEntityAccessException(t.getMessage(), t);
				}
			});

			return entity;
		} catch (Throwable t) {
			throw new MDMEntityAccessException(t.getMessage(), t);
		}
	}

	/**
	 * Returns the {@link SearchAttribute}s for the given entityClass
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to get the {@link SearchAttribute}s
	 *            for
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s
	 */
	public <T extends Entity> List<SearchAttribute> getSearchAttributes(Class<T> entityClass, String sourceName) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		List<SearchAttribute> searchAttributes = null;

		searchAttributes = this.searchActivity.listAvailableAttributes(em, entityClass);

		// return empty list if nothing was found just in case the backend methods would
		// return null
		return searchAttributes != null ? searchAttributes : new ArrayList<SearchAttribute>();
	}

	/**
	 * Returns the localized {@link Entity} type name
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to be localized
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Entity} type name
	 */
	public <T extends Entity> Map<EntityType, String> localizeType(Class<T> entityClass, String sourceName) {
		return this.i18nActivity.localizeType(sourceName, entityClass);
	}

	/**
	 * Returns localized {@link Entity} attributes
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to be localized
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Entity} attributes
	 */
	public <T extends Entity> Map<Attribute, String> localizeAttributes(Class<T> entityClass, String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, entityClass);
	}
}