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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.mdm.api.base.model.BaseEntity;
import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.CheckedFunction0;
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
	private NavigationActivity navigationActivity;

	@EJB
	private I18NActivity i18nActivity;

	private static Consumer<? super Throwable> throwException = e -> {
		throw new MDMEntityAccessException(e.getMessage(), e);
	};

	/**
	 * 
	 * Returns a {@link Entity} identified by the given id.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param id
	 *            id of the {@link Entity} to find
	 * @return found {@link Entity}
	 */
	public <T extends Entity> Option<T> find(String sourceName, Class<T> entityClass, String id) {
		return find(sourceName, entityClass, id, null);
	}

	/**
	 * Finds the specified entity by given {@code entityClass} and given {@code id}.
	 * If the {@code entityClass} is either {@link CatalogAttribute},
	 * {@link TemplateCompont}, {@link TemplateAttributeAttribute} or
	 * {@link ContextComponent} the respective root entities
	 * {@link CatalogComponent}, {@link TemplateRoot} or {@link ContextRoot} are
	 * used to get the entity to find.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            entityType
	 * @param id
	 *            id of entity to find
	 * @param contextType
	 *            contextType of entity to find. Can be {@code null} if
	 *            {@code EntityType} has no {@code ContextType}.
	 * @param parentIds
	 *            id(s) of parent(s). For {@link CatalogAttribute} the parentId must
	 *            be the id of the {@link CatalogComponent}, for
	 *            {@link TemplateComponent} it must be the id of the
	 *            {@link TemplateRoot}, for {@link TemplateAttribute} it must be the
	 *            id of the {@link TemplateRoot} first and also the
	 *            {@link TemplateComponent}, for a nested {@link TemplateComponent}
	 *            it must be the id of the {@link TemplateRoot} first and the id of
	 *            the parent {@link TemplateComponent} second, for a
	 *            {@link TemplateAttribute} within a nested
	 *            {@link TemplateComponent} it must be the id of the
	 *            {@link TemplateRoot} first, the id of the parent
	 *            {@link TemplateComponent} second and the id of the
	 *            {@link TemplateComponent} last and for {@link ContextComponent} it
	 *            must be the id of the {@link ContextRoot}.
	 * @return the found entity
	 */
	// TODO add comment for parentIds for TplSensors and nested TplSensors as well
	// as for TplSensorAttrs and nested TplSensorAttrs
	// TODO anehmer on 2017-11-09: parentIds and entity specific code should be
	// removed from the signature if the ODSAdapter is able to handle load on
	// CatalogAttributes etc. without using the root/parent entity
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> find(String sourceName, Class<T> entityClass, String id,
			ContextType contextType, String... parentIds) {
		// if the find is contextType specific
		if (contextType != null) {
			// TODO anehmer on 2017-11-09: test if all code paths are used
			if (entityClass.equals(CatalogAttribute.class)) {
				// check existence of parentId
				if (parentIds.length != 1) {
					throw new IllegalArgumentException("Id of CatalogComponent not set as parentId");
				}
				return (Option<T>) find(sourceName, CatalogComponent.class, parentIds[0], contextType)
						.map(catComp -> Stream.ofAll(catComp.getCatalogAttributes())
								.find(catAttr -> catAttr.getID()
										.equals(id))
								.getOrElseThrow(() -> new NoSuchElementException()));
			} else if (entityClass.equals(TemplateComponent.class)) {
				// nested TemplateComponents do not need to be retrieved via their
				// parentTemplateComponent as getting TemplateComponents from a TemplateRoot
				// does also return the nested TemplateComponents
				if (parentIds.length != 1 && parentIds.length != 2) {
					throw new IllegalArgumentException(
							"Id of TemplateRoot (and ParentTemplateComponent) not set as parentId");
				}

				// if non-nested TemplateComponent has to be found
				Option<TemplateComponent> templateComponent = find(sourceName, TemplateRoot.class, parentIds[0],
						contextType).map(
								tplRoot -> Stream.ofAll(tplRoot.getTemplateComponents())
										.find(tplComp -> tplComp.getID()
												// if a nested TemplateComponent has to be found, the
												// ParentTemplateComponent has to be retrieved
												.equals(parentIds.length == 1 ? id : parentIds[1]))
										.getOrElseThrow(() -> new NoSuchElementException(
												"TemplateComponent with ID " + id + " not found")));
				if (parentIds.length == 1) {
					return (Option<T>) templateComponent;
				}

				// if nested TemplateComponent has to be found
				if (parentIds.length == 2) {
					return (Option<T>) templateComponent.map(tplComp -> Stream.ofAll(tplComp.getTemplateComponents())
							.find(nestedTplComp -> nestedTplComp.getID()
									.equals(id))
							.getOrElseThrow(() -> new NoSuchElementException(
									"TemplateComponent with ID " + id + " not found")));
				}
			} else if (entityClass.equals(TemplateAttribute.class)) {
				if (parentIds.length != 2) {
					throw new IllegalArgumentException(
							"Id of TemplateComponent and id of TemplateRoot not set as parentIds");
				}
				return (Option<T>)
				// get TemplateRoot
				find(sourceName, TemplateRoot.class, parentIds[0], contextType).onEmpty(() -> {
					throw new NoSuchElementException("TemplateComponent with ID " + parentIds[0] + " not found");
				})
						// get TemplateComponents from TemplateRoot
						.map(tplRoot -> Stream.ofAll(tplRoot.getTemplateComponents())
								.find(tplComp -> tplComp.getID()
										.equals(parentIds[1]))
								.onEmpty(() -> {
									throw new NoSuchElementException(
											"TemplateComponent with ID " + parentIds[1] + " not found");
								})
								.get())
						// get TemplateAttributes from TemplateComponent
						.map(tplComp -> Stream.ofAll(tplComp.getTemplateAttributes())
								.find(tplAttr -> tplAttr.getID()
										.equals(id))
								.onEmpty(() -> {
									throw new NoSuchElementException("TemplateAttribute with ID " + id + " not found");
								})
								.get());
			} else if (entityClass.equals(ContextComponent.class)) {
				if (parentIds.length != 1) {
					throw new IllegalArgumentException("Id of ContextRoot not set as parentId");
				}
				// TODO anehmer on 2017-11-09: implement
				throw new NotImplementedException("NOT IMPLEMENTED YET");
			} else {

			}
			// TODO anehmer on 2017-11-09: error handling: how to inform caller about what
			// happened? Try?
			// TODO anehmer on 2017-11-09: handle "Connector Service not found", "Source not
			// found"
			return Try.of(() -> this.connectorService.getEntityManagerByName(sourceName))
					// TODO handle "Entity not found"
					.mapTry(em -> em.load(entityClass, contextType, id))
					.onFailure(throwException)
					.toOption();
		}

		// if the find is not contextType specific
		// TODO anehmer on 2017-11-09: error handling: how to inform caller about what
		// happened? Try?
		// TODO anehmer on 2017-11-09: handle "Connector Service not found", "Source not
		// found"
		return Try.of(() -> this.connectorService.getEntityManagerByName(sourceName))
				// TODO handle "Entity not found"
				.mapTry(em -> em.load(entityClass, id))
				.onFailure(throwException)
				.toOption();
	}

	/**
	 * {@link Entity}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result. Can be null.
	 * @return a list of found {@link Entity}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> List<T> findAll(String sourceName, Class<T> entityClass, String filter) {
		return findAll(sourceName, entityClass, filter, null);
	}

	/**
	 * Returns the matching {@link Entity}s of the given contextType using the given
	 * filter or all {@link Entity}s of the given contextType if no filter is
	 * available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result
	 * @param contextType
	 *            the {@link ContextType) of the entities to find
	 * @return a list of found {@link Entity}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> List<T> findAll(String sourceName, Class<T> entityClass, String filter,
			ContextType contextType) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

		// TODO do we need to implement the navigationActivity filter shortcut like in
		// ChannelGroupService.getChannelGroups()…
		if (filter == null || filter.trim()
				.length() <= 0) {
			return (List<T>) Try.of(getLoadAllEntitiesMethod(em, entityClass, contextType))
					.onFailure(DataAccessHelper.rethrowAsMDMEntityAccessException)
					.map(list -> List.of(list.toArray()))
					.getOrElse(List.empty());
		} else {
			return (List<T>) Try.of(() -> this.searchActivity.search(em, entityClass, filter))
					.onFailure(DataAccessHelper.rethrowAsMDMEntityAccessException)
					.map(list -> List.of(list.toArray()))
					.getOrElse(List.empty());
		}
	}

	/**
	 * Returns the method to load all entities of type {@code entityClass}. If a
	 * {@code ContextType} is given the appropriate method in
	 * {@link org.eclipse.mdm.api.dflt.EntityManager} is used
	 */
	private <T extends Entity> CheckedFunction0<java.util.List<T>> getLoadAllEntitiesMethod(EntityManager entityManager,
			Class<T> entityClass, ContextType... contextType) {
		// if contextType is specified
		if (contextType != null && contextType.length > 0) {
			return (() -> entityManager.loadAll(entityClass, contextType[0]));

		}
		return (() -> entityManager.loadAll(entityClass));
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
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            the class of the entity to update
	 * @param id
	 *            id of the {@link Entity} to update
	 * @param values
	 *            map of values to update the entity with according to matching
	 *            attribute values by name case sensitive
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> update(String sourceName, Class<T> entityClass, String id,
			Map<String, Object> values) {

		return update(sourceName, entityClass, id, values, null);
	}

	/**
	 * Updates the {@link Entity} of the given contextType with the given identifier
	 * with the values in the given map.
	 * 
	 * @param entityClass
	 *            the class of the entity to update
	 * @param contextType
	 *            the {@link ContextType) of the entities to update
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the entity to update
	 * @param values
	 *            map of values to update the entity with according to matching
	 *            attribute values by name case sensitive
	 * @param contextType
	 *            the {@link ContextType) of the entities to update
	 * @param parentIds
	 *            see
	 *            {@link EntityService#find(String, Class, String, ContextType, String...)}
	 * 
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> update(String sourceName, Class<T> entityClass, String id,
			Map<String, Object> values, ContextType contextType, String... parentIds) {
		// get EntityManager
		Try<EntityManager> entityManager = Try.of(() -> connectorService.getEntityManagerByName(sourceName));

		// return updated entity
		return (Option<T>) find(sourceName, entityClass, id, contextType, parentIds)
				// update entity values
				.map(entity -> ResourceHelper.updateEntityValues(entity, values))
				// persist entity
				.map(entity -> entity.toTry()
						.mapTry(e -> DataAccessHelper.execute()
								.apply(entityManager.get(), entity.get(), DataAccessHelper.UPDATE))
						.onFailure(ResourceHelper.rethrowAsWebApplicationException)
						.get());
	}

	/**
	 * Deletes the {@link Entity} with the given identifier.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param id
	 *            id of the entity to delete
	 */
	public <T extends Entity> Option<T> delete(String sourceName, Class<T> entityClass, String id) {
		return find(sourceName, entityClass, id, null);
	}

	/**
	 * Deletes the {@link Entity} with the given identifier.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to delete
	 * @param id
	 *            id of the entity to delete
	 * @param contextType
	 *            the {@link ContextType) of the entities to delete
	 * @param parentIds
	 *            see
	 *            {@link EntityService#find(String, Class, String, ContextType, String...)}
	 * 
	 */
	// TODO handle erroneous call to delete on complete lists of ValueList etc.
	@SuppressWarnings("unchecked")
	public <T extends Entity> Option<T> delete(String sourceName, Class<T> entityClass, String id,
			ContextType contextType, String... parentIds) {
		EntityManager entityManager = connectorService.getEntityManagerByName(sourceName);
		return (Option<T>) find(sourceName, entityClass, id, contextType, parentIds).toTry()
				.mapTry(entity ->
				// start transaction and delete the entity
				// TODO this causes the unchecked warning. Why is apply() not returning T?
				(T) DataAccessHelper.execute()
						.apply(entityManager, entity, DataAccessHelper.delete()))
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

	/**
	 * Sets a related {@link Entity} for a given {@link Entity}. A
	 * {@link ContextType} can be specified for the relatedEntity.
	 * 
	 * @param entity
	 *            entity to set related entity for
	 * @param relatedEntity
	 *            related entity to set
	 * @param contextType
	 *            contextType of related entity or null if not applicable
	 */
	private static void setRelatedEntity(Entity entity, Entity relatedEntity, ContextType contextType) {
		Method GET_CORE_METHOD;
		try {
			GET_CORE_METHOD = BaseEntity.class.getDeclaredMethod("getCore");
			GET_CORE_METHOD.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(
					"Unable to load 'getCore()' in class '" + BaseEntity.class.getSimpleName() + "'.", e);
		}
		// use appropriate set-method
		if (contextType != null) {
			Try.of(() -> {
				((Core) GET_CORE_METHOD.invoke(entity)).getMutableStore()
						.set(relatedEntity, contextType);
				return null;
			})
					.onFailure(ResourceHelper.rethrowAsWebApplicationException);
		} else {
			Try.of(() -> {
				((Core) GET_CORE_METHOD.invoke(entity)).getMutableStore()
						.set(relatedEntity);
				return null;
			})
					.onFailure(ResourceHelper.rethrowAsWebApplicationException);

		}
	}

	/**
	 * Sets the parent {@link Entity} for a given {@link Entity}. A
	 * {@link ContextType} can be specified for the parent entity.
	 * 
	 * @param entity
	 *            entity to set as parent
	 * @param parent
	 *            parent entity to set
	 * @param contextType
	 *            contextType of parent entity or null if not applicable
	 */
	public static void setParentEntity(Entity entity, Entity parent, ContextType contextType) {
		Method GET_CORE_METHOD;
		try {
			GET_CORE_METHOD = BaseEntity.class.getDeclaredMethod("getCore");
			GET_CORE_METHOD.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(
					"Unable to load 'getCore()' in class '" + BaseEntity.class.getSimpleName() + "'.", e);
		}
		// use appropriate set-method
		if (contextType != null) {
			Try.of(() -> {
				((Core) GET_CORE_METHOD.invoke(entity)).getPermanentStore()
						.set(parent, contextType);
				return null;
			})
					// TODO onFailure not triggering
					.onFailure(ResourceHelper.rethrowAsWebApplicationException);
		} else {
			Try.of(() -> {
				((Core) GET_CORE_METHOD.invoke(entity)).getPermanentStore()
						.set(parent);
				return null;
			})
					.onFailure(ResourceHelper.rethrowAsWebApplicationException);

		}
	}
}