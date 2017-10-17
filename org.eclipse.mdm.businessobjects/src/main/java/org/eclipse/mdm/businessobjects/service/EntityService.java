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
package org.eclipse.mdm.businessobjects.service;

import java.util.Arrays;
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
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
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
	 * Returns the matching {@link Entity}s using the given filter or returns all
	 * {@link Entity}s if no filter is available
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Entity} result. Can be null.
	 * @return found {@link Entity}
	 */
	// TODO add filter code like in ChannelGroupService.getChannelGroups()…
	public <T extends Entity> List<T> findAll(Class<T> entityClass, String sourceName, String filter) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			List<T> entities = null;
			if (filter == null || filter.trim()
					.length() <= 0) {
				entities = List.ofAll(em.loadAll(entityClass));

			} else {
				entities = List.ofAll(this.searchActivity.search(em, entityClass, filter));
			}

			// return empty list if nothing was found just in case the backend methods would
			// return null
			return entities != null ? entities : List.empty();
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Returns the children of the given {@link Entity}.
	 * 
	 * @param parentEntityClass
	 *            class of the parent that is also an {@link Entity}
	 * @param childrenEntityClass
	 *            class of the {@link Entity}s to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param parentId
	 *            id of the {@link Entity} to find
	 * @return found {@link Entity}s
	 */
	// TODO use method with filter and call with null
	public <T extends Entity> List<T> findChildren(Class<Entity> parentEntityClass, Class<T> childrenEntityClass,
			String sourceName, String parentId) {
		Try<EntityManager> entityManager = Try.of(() -> this.connectorService.getEntityManagerByName(sourceName));
		// TODO handle "Entity not found"
		// TODO is that messy functional style and should I just use a function chain
		Entity parent = entityManager.mapTry(em -> em.load(parentEntityClass, parentId))
				.onFailure(throwException)
				.get();
		List<T> children = entityManager.mapTry(em -> List.ofAll(em.loadChildren(parent, childrenEntityClass)))
				.onFailure(throwException)
				.get();

		return children;
	}

	/**
	 * 
	 * Returns the children of the parent {@link Entity} identified by parentId
	 * using the given filter or returns all {@link Entity}s if no filter is
	 * available
	 * 
	 * @param parentEntityClass
	 *            class of the parent that is also an {@link Entity}
	 * @param childrenEntityClass
	 *            class of the {@link Entity}s to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param parentId
	 *            id of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result. Can be null.
	 * @return found {@link Entity}s
	 */
	public <T extends Entity> List<T> findChildren(Class<? extends Entity> parentEntityClass,
			Class<T> childrenEntityClass, String sourceName, String parentId, String filter) {
		Try<EntityManager> entityManager = Try.of(() -> this.connectorService.getEntityManagerByName(sourceName));
		// TODO handle "Entity not found"
		// TODO is that messy functional style and should I just use a function chain
		Entity parent = entityManager.mapTry(em -> em.load(parentEntityClass, parentId))
				.onFailure(throwException)
				.get();

		Try<List<T>> children;
		if (filter == null || filter.trim()
				.length() <= 0) {
			children = entityManager.mapTry(em -> List.ofAll(em.loadChildren(parent, childrenEntityClass)));

		} else {
			// TODO filter should only filter entities under the given parentId
			children = entityManager
					.mapTry(em -> List.ofAll(this.searchActivity.search(em, childrenEntityClass, filter)));
		}

		return children.onFailure(throwException)
				.get();
	}

	/**
	 * 
	 * Returns the children of the given {@link Entity} for the given
	 * {@link ContextType}.
	 * 
	 * @param parentEntityClass
	 *            class of the parent that is also an {@link Entity}
	 * @param childrenEntityClass
	 *            class of the {@link Entity}s to find
	 * @param contextType
	 *            Context type of the parent and children
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param parentId
	 *            id of the {@link Entity} to find
	 * @return found {@link Entity}s
	 */
	// TODO use method with filter and call with null
	public <T extends Entity> List<T> findChildren(Class<? extends Entity> parentEntityClass,
			Class<T> childrenEntityClass, ContextType contextType, String sourceName, String parentId) {
		Try<EntityManager> entityManager = Try.of(() -> this.connectorService.getEntityManagerByName(sourceName));
		// TODO handle "Entity not found"
		// TODO is that messy functional style and should I just use a function chain
		Entity parent = entityManager.mapTry(em -> em.load(parentEntityClass, contextType, parentId))
				.onFailure(throwException)
				.get();
		List<T> children = entityManager
				.mapTry(em -> List.ofAll(em.loadChildren(parent, childrenEntityClass, contextType)))
				.onFailure(throwException)
				.get();

		return children;
	}

	/**
	 * 
	 * Returns the children of the given {@link Entity} for the given
	 * {@link ContextType}.
	 * 
	 * @param parentEntityClass
	 *            class of the parent that is also an {@link Entity}
	 * @param childrenEntityClass
	 *            class of the {@link Entity}s to find
	 * @param contextType
	 *            Context type of the parent and children
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param parentId
	 *            id of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result. Can be null.
	 * @return found {@link Entity}s
	 */
	public <T extends Entity> List<T> findChildren(Class<? extends Entity> parentEntityClass,
			Class<T> childrenEntityClass, ContextType contextType, String sourceName, String parentId, String filter) {
		Try<EntityManager> entityManager = Try.of(() -> this.connectorService.getEntityManagerByName(sourceName));
		// TODO handle "Entity not found"
		// TODO is that messy functional style and should I just use a function chain
		Entity parent = entityManager.mapTry(em -> em.load(parentEntityClass, contextType, parentId))
				.onFailure(throwException)
				.get();

		Try<List<T>> children;
		if (filter == null || filter.trim()
				.length() <= 0) {
			children = entityManager
					.mapTry(em -> List.ofAll(em.loadChildren(parent, childrenEntityClass, contextType)));

		} else {
			// TODO filter should only filter entities under the given parentId
			// TODO why not use em.loadChildren(..., String pattern)?
			children = entityManager
					.mapTry(em -> List.ofAll(this.searchActivity.search(em, childrenEntityClass, filter)));
		}

		return children.onFailure(throwException)
				.get();
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
				entities = List.ofAll(em.loadAll(entityClass, contextType));

			} else {
				entities = List.ofAll(this.searchActivity.search(em, entityClass, filter));
			}

			// return empty list if nothing was found just in case the backend methods would
			// return null
			return entities != null ? entities : List.empty();
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
		List<Class<? extends Object>> argClasses = List.of(createMethodArgs)
				.map(o -> o.getClass());

		// get corresponding create method for Entity from EntityFactory
		entity = Option.ofOptional(em.getEntityFactory())
				.map(factory -> {
					try {
						return (T) Stream.of(EntityFactory.class.getMethods())
								// find method with the return type matching entityClass
								.filter(m -> m.getReturnType()
										.equals(entityClass))
								.filter(m -> Arrays.asList(m.getParameterTypes())
										.equals(argClasses.toJavaList()))
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
	 * Updates the {@link Entity} with the given identifier with the values in the
	 * given map.
	 * 
	 * @param entityClass
	 *            the class of the entity to update
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	// TODO change method signatures to have sourceName as first param
	public <T extends Entity> Option<T> update(Class<T> entityClass, String sourceName, String id,
			Map<String, Object> values) {
		// get EntityManager
		Try<EntityManager> entityManager = Try.of(() -> connectorService.getEntityManagerByName(sourceName));

		// return updated entity
		return (Option<T>) entityManager.mapTry(em -> em.load(entityClass, id))
				// update entity values
				.map(entity -> ResourceHelper.updateEntityValues(entity, values))
				// persist entity
				.mapTry(entity -> entity.toTry()
						.mapTry(e -> DataAccessHelper.execute()
								.apply(entityManager.get(), entity.get(), DataAccessHelper.UPDATE))
						// TODO try to get rid of inner onFailure
						.onFailure(ResourceHelper.rethrowException))
				.onFailure(ResourceHelper.rethrowException)
				// unwrap Option
				// TODO check if that's the way to handle a potential error and thus null return
				// value of the mapTry. To just call e.get() doesn't seem right also. What about
				// flatMap()?
				.map(e -> e.get())
				.toOption();
	}

	/**
	 * Updates the {@link Entity} of the given contextType with the given identifier
	 * with the values in the given map.
	 * 
	 * @param entityClass
	 *            the class of the entity to update
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the entity to update
	 * @param values
	 *            map of values to update the entity with according to matching
	 *            attribute values by name case sensitive
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	// TODO change method signatures to have sourceName as first param
	public <T extends Entity> Option<T> update(Class<T> entityClass, ContextType contextType, String sourceName,
			String id, Map<String, Object> values) {
		// get EntityManager
		Try<EntityManager> entityManager = Try.of(() -> connectorService.getEntityManagerByName(sourceName));

		// return updated entity
		return (Option<T>) entityManager.mapTry(em -> em.load(entityClass, contextType, id))
				// update entity values
				.map(entity -> ResourceHelper.updateEntityValues(entity, values))
				// persist entity
				// TODO remove toTry()
				.mapTry(entity -> entity.toTry()
						.mapTry(e -> DataAccessHelper.execute()
								.apply(entityManager.get(), entity.get(), DataAccessHelper.UPDATE))
						// TODO try to get rid of inner onFailure
						.onFailure(ResourceHelper.rethrowException))
				.onFailure(ResourceHelper.rethrowException)
				// unwrap Option
				// TODO check if that's the way to handle a potential error and thus null return
				// value of the mapTry. To just call e.get() doesn't seem right also. What about
				// flatMap()?
				.map(e -> e.get())
				.toOption();
	}

	/**
	 * Updates the {@link Entity} of the given contextType with the given identifier
	 * with the values in the given map.
	 * 
	 * @param entityClass
	 *            the class of the entity to update
	 * @param parentClass
	 *            class of the {@link Entity}'s parent. Can be null.
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the entity to update
	 * @param values
	 *            map of values to update the entity with according to matching
	 *            attribute values by name case sensitive
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	// TODO change method signatures to have sourceName as first param
	public <T extends Entity> Option<T> update(Class<T> entityClass, Class<? extends Entity> parentClass,
			ContextType contextType, String sourceName, String id, String parentId, Map<String, Object> values) {
		// get EntityManager
		Try<EntityManager> entityManager = Try.of(() -> connectorService.getEntityManagerByName(sourceName));

		// return updated entity
		return (Option<T>) entityManager.mapTry(em -> em.load(entityClass, contextType, id))
				.mapTry(e -> {
					// reload from parent in case of CatalogAttribute as it can only be deleted
					// if the parent is set
					// TODO rewrite that mess: put in separate method reloadFromParent()
					// TODO or check ODSModelManager for non-declared mandatory relation from
					// CatAttr to CatComp as EntityRequest.load() loads all mandatory and
					// optional related entities
					// UPDATE: currently the mandatory relation from CatAttr to CatComp cannot be
					// defined as this leads to a circular call when loading a CatComp
					if (CatalogAttribute.class.isAssignableFrom(entityClass)
							&& CatalogComponent.class.isAssignableFrom(parentClass)) {
						// TODO verify existence of parent
						return entityManager.mapTry(em -> em.load(CatalogComponent.class, contextType, parentId))
								.map(catComp -> catComp.getCatalogAttributes()
										.stream()
										.filter(attr -> attr.getName()
												.equals(e.getName()))
										.findFirst()
										.get())
								.getOrElse((CatalogAttribute) e);
					} else {
						return e;
					}
				})
				// update entity values
				.map(entity -> ResourceHelper.updateEntityValues(entity, values))
				// persist entity
				// TODO remove toTry()
				.mapTry(entity -> entity.toTry()
						.mapTry(e -> DataAccessHelper.execute()
								.apply(entityManager.get(), entity.get(), DataAccessHelper.UPDATE))
						// TODO try to get rid of inner onFailure
						.onFailure(ResourceHelper.rethrowException))
				.onFailure(ResourceHelper.rethrowException)
				// unwrap Option
				// TODO check if that's the way to handle a potential error and thus null return
				// value of the mapTry. To just call e.get() doesn't seem right also. What about
				// flatMap()?
				.map(e -> e.get())
				.toOption();
	}

	/**
	 * Deletes the {@link Entity} with the given identifier.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param parentClass
	 *            class of the {@link Entity}'s parent. Can be null.
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @param identifier
	 *            The id of the {@link Entity} to delete.
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> delete(Class<T> entityClass, Class<? extends Entity> parentClass,
			String sourceName, ContextType contextType, String id, String parentId) {
		EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
		return Try.of(() -> entityManager.load(entityClass, contextType, id))
				.mapTry(e -> {
					// reload from parent in case of CatalogAttribute as it can only be deleted
					// if the parent is set
					// TODO rewrite that mess: put in separate method reloadFromParent()
					// TODO or check ODSModelManager for non-declared mandatory relation from
					// CatAttr to CatComp as EntityRequest.load() loads all mandatory and
					// optional related entities
					// UPDATE: currently the mandatory relation from CatAttr to CatComp cannot be
					// defined as this leads to a circular call when loading a CatComp
					if (CatalogAttribute.class.isAssignableFrom(entityClass)
							&& CatalogComponent.class.isAssignableFrom(parentClass)) {
						// TODO verify existence of parent
						Entity parent = entityManager.load(CatalogComponent.class, contextType, parentId);
						return ((CatalogComponent) parent).getCatalogAttributes()
								.stream()
								.filter(attr -> attr.getName()
										.equals(e.getName()))
								.findFirst()
								.get();
					} else {
						return e;
					}
				})
				// TODO add null check for parent class
				.onFailure(throwException)
				.mapTry(e ->
				// start transaction and delete the entity
				// TODO this causes the unchecked warning. Why is apply() not returning T?
				(T) DataAccessHelper.execute()
						.apply(entityManager, e, DataAccessHelper.delete()))
				.onFailure(throwException)
				.toOption();
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
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> delete(Class<T> entityClass, String sourceName, ContextType contextType,
			String id) {
		EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
		return Try.of(() -> entityManager.load(entityClass, contextType, id))
				.onFailure(throwException)
				.mapTry(e ->
				// start transaction and delete the entity
				// TODO this causes the unchecked warning. Why is apply() not returning T?
				(T) DataAccessHelper.execute()
						.apply(entityManager, e, DataAccessHelper.delete()))
				.onFailure(throwException)
				.toOption();
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
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> delete(Class<T> entityClass, String sourceName, String id) {
		EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
		return Try.of(() -> entityManager.load(entityClass, id))
				.onFailure(throwException)
				.mapTry(e ->
				// start transaction and delete the entity
				// TODO this causes the unchecked warning. Why is apply() not returning T?
				(T) DataAccessHelper.execute()
						.apply(entityManager, e, DataAccessHelper.delete()))
				.onFailure(throwException)
				.toOption();
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

		searchAttributes = List.ofAll(this.searchActivity.listAvailableAttributes(em, entityClass));

		// return empty list if nothing was found just in case the backend methods would
		// return null
		return searchAttributes != null ? searchAttributes : List.empty();
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
		return HashMap.ofAll(this.i18nActivity.localizeType(sourceName, entityClass));
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
		return HashMap.ofAll(this.i18nActivity.localizeAttributes(sourceName, entityClass));
	}
}