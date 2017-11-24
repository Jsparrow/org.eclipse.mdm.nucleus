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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.api.dflt.model.TemplateTest;
import org.eclipse.mdm.api.dflt.model.TemplateTestStepUsage;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.api.dflt.model.ValueListValue;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.utils.EntityNotFoundException;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import io.vavr.CheckedFunction0;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
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
	 * @see #find(String, Class, String, ContextType, String...)
	 */
	public <T extends Entity> Try<T> find(String sourceName, Class<T> entityClass, String id) {
		return find(sourceName, entityClass, id, null, (String[]) null);
	}

	/**
	 * 
	 * Returns a {@link Entity} identified by the given id with the given parent(s).
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param id
	 *            id of the {@link Entity} to find
	 * @param parentIds
	 * @return found {@link Entity}
	 * @see #find(String, Class, String, ContextType, String...)
	 */
	public <T extends Entity> Try<T> find(String sourceName, Class<T> entityClass, String id, String... parentIds) {
		return find(sourceName, entityClass, id, null, parentIds);
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
	 * @param contextTypeSupplier
	 *            a {@link Try} with the contextType of entity to find. Can be
	 *            {@code null} if {@code EntityType} has no {@code ContextType}.
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
	// TODO anehmer on 2017-11-22: complete javadoc for ValueListValue and
	// TemplateTestStepUsage
	// TODO anehmer on 2017-11-22: add comment for parentIds for TplSensors and
	// nested TplSensors as well as for TplSensorAttrs and nested TplSensorAttrs
	@SuppressWarnings("unchecked")
	public <T extends Entity> Try<T> find(String sourceName, Class<T> entityClass, String id,
			Try<ContextType> contextTypeSupplier, String... parentIds) {

		// if the find is contextType specific
		if (contextTypeSupplier != null && contextTypeSupplier.isSuccess()) {
			if (entityClass.equals(CatalogAttribute.class)) {
				// check existence of parentId
				if (parentIds.length != 1) {
					throw new IllegalArgumentException("Id of CatalogComponent not set as parentId");
				}

				String catCompId = parentIds[0];

				return (Try<T>)
				// get CatalogComponent
				find(sourceName, CatalogComponent.class, catCompId, contextTypeSupplier)
						.onFailure(x -> new EntityNotFoundException(CatalogComponent.class, catCompId, x))
						// get CatalogAttribute from CatalogComponent
						.map(catComp -> Stream.ofAll(catComp.getCatalogAttributes())
								.find(catAttr -> catAttr.getID()
										.equals(id)));
				// .getOrElseThrow(() -> new EntityNotFoundException(CatalogAttribute.class,
				// id)));

			} else if (entityClass.equals(TemplateComponent.class)) {
				// does also return the nested TemplateComponents
				if (parentIds.length != 1 && parentIds.length != 2) {
					throw new IllegalArgumentException(
							"Id of TemplateRoot (and ParentTemplateComponent) not set as parentId");
				}

				String tplRootId = parentIds[0];
				// if a nested TemplateComponent has to be found, the
				// ParentTemplateComponent has to be retrieved
				String tplCompId = parentIds.length == 1 ? id : parentIds[1];

				// if non-nested TemplateComponent has to be found
				Try<TemplateComponent> templateComponent =
						// get TemplateRoot
						find(sourceName, TemplateRoot.class, tplRootId, contextTypeSupplier)
								.onFailure(x -> new EntityNotFoundException(TemplateRoot.class, tplRootId, x))
								// get TemplateComponent from TemplateRoot
								.map(tplRoot -> Stream.ofAll(tplRoot.getTemplateComponents())
										.find(tplComp -> tplComp.getID()
												.equals(tplCompId))
										.getOrElseThrow(
												() -> new EntityNotFoundException(TemplateRoot.class, tplCompId)));

				// if non-nested TemplateComponent is found
				if (parentIds.length == 1) {
					return (Try<T>) templateComponent;
				}

				// if nested TemplateComponent has to be found
				if (parentIds.length == 2) {
					return (Try<T>)
					// get nested TemplateComponent from parent TemplateComponent
					templateComponent.map(tplComp -> Stream.ofAll(tplComp.getTemplateComponents())
							.find(nestedTplComp -> nestedTplComp.getID()
									.equals(id))
							.getOrElseThrow(() -> new NoSuchElementException(
									"TemplateComponent with ID " + id + " not found")));
				}

			} else if (entityClass.equals(CatalogSensor.class)) {
				// check existence of parentId
				if (parentIds.length != 1) {
					throw new IllegalArgumentException("Id of CatalogComponent not set as parentId");
				}

				String catCompid = parentIds[0];

				return (Try<T>)
				// get CatalogComponent
				find(sourceName, CatalogComponent.class, catCompid, contextTypeSupplier)
						// get CatalogSensor from CatalogComponent
						.map(catComp -> Stream.ofAll(catComp.getCatalogSensors())
								.find(catSensor -> catSensor.getID()
										.equals(id))
								.getOrElseThrow(() -> new EntityNotFoundException(CatalogComponent.class, catCompid)));

			} else if (entityClass.equals(TemplateAttribute.class)) {
				if (parentIds.length != 2 && parentIds.length != 3) {
					throw new IllegalArgumentException(
							"Id of TemplateComponent, (ParentTemplateComponent) and TemplateRoot not set as parentIds");
				}

				String tplRootId = parentIds[0];
				String tplCompId = parentIds[1];

				// if non-nested TemplateAttribute has to be found
				Try<TemplateComponent> templateComponent =
						// get TemplateRoot
						find(sourceName, TemplateRoot.class, tplRootId, contextTypeSupplier)
								// get TemplateComponent from TemplateRoot
								.map(tplRoot -> Stream.ofAll(tplRoot.getTemplateComponents())
										.find(tplComp -> tplComp.getID()
												// if a nested TemplateComponent has to be found, the
												// ParentTemplateComponent has to be retrieved
												.equals(tplCompId))
										.getOrElseThrow(
												() -> new EntityNotFoundException(TemplateComponent.class, tplCompId)));

				// if nested TemplateAttribute has to be found
				if (parentIds.length == 3) {
					String nestedTplCompId = parentIds[2];

					// get nested TemplateComponent
					templateComponent =
							// get nested TemplateComponent from TemplateComponent
							templateComponent.map(tplComp -> Stream.ofAll(tplComp.getTemplateComponents())
									.find(nestedTplComp -> nestedTplComp.getID()
											.equals(nestedTplCompId))
									.getOrElseThrow(
											() -> new EntityNotFoundException(TemplateComponent.class, tplCompId)));
				}

				// get TemplateAttributes from TemplateComponent
				return (Try<T>) templateComponent.map(tplComp -> Stream.ofAll(tplComp.getTemplateAttributes())
						.find(tplAttr -> tplAttr.getID()
								.equals(id))
						.onEmpty(() -> new EntityNotFoundException(TemplateAttribute.class, id)));

			} else if (entityClass.equals(TemplateSensor.class)) {
				// TODO anehmer on 2017-11-22: implement for nested TplSensor
				if (parentIds.length != 2) {
					throw new IllegalArgumentException("Id of TemplateComponent and TemplateRoot not set as parentIds");
				}
				String tplRootId = parentIds[0];
				String tplCompId = parentIds[1];

				return (Try<T>)
				// get TemplateRoot
				find(sourceName, TemplateRoot.class, tplRootId, Try.of(() -> ContextType.TESTEQUIPMENT))
						.onFailure(x -> new EntityNotFoundException(TemplateRoot.class, tplRootId, x))
						// get TemplateComponents from TemplateRoot
						.map(tplRoot -> Stream.ofAll(tplRoot.getTemplateComponents())
								.find(tplComp -> tplComp.getID()
										.equals(tplCompId))
								.getOrElseThrow(() -> new EntityNotFoundException(TemplateComponent.class, tplCompId)))
						// // get TemplateSensors from TemplateComponent
						.map(tplComp -> Stream.ofAll(tplComp.getTemplateSensors())
								.find(tplSensor -> tplSensor.getID()
										.equals(id))
								.getOrElseThrow(() -> new EntityNotFoundException(TemplateSensor.class, id)));

			} else if (entityClass.equals(ContextComponent.class)) {
				// TODO anehmer on 2017-11-09: implement
				throw new NotImplementedException("NOT IMPLEMENTED YET");
			}

			return getEntityManager(sourceName).mapTry(em -> em.load(entityClass, contextTypeSupplier.get(), id));
		}

		// if a ValueListValue has to be found
		else if (entityClass.equals(ValueListValue.class)) {
			if (parentIds.length != 1) {
				throw new IllegalArgumentException("Id of ValueList not set as parentId");
			}

			String valueListId = parentIds[0];

			return (Try<T>)
			// get ValueList
			find(sourceName, ValueList.class, valueListId)
					// get ValueListValues from ValueList
					.map(valueList -> Stream.ofAll(valueList.getValueListValues())
							.find(valueListValue -> valueListValue.getID()
									.equals(id))
							.getOrElseThrow(() -> new EntityNotFoundException(TemplateSensor.class, id)));
		}

		// if a TemplateTestStepUsage has to be found
		else if (entityClass.equals(TemplateTestStepUsage.class)) {
			if (parentIds.length != 1) {
				throw new IllegalArgumentException("Id of TemplateTest not set as parentId");
			}

			String tplTestId = parentIds[0];

			return (Try<T>)
			// get TplTest
			find(sourceName, TemplateTest.class, tplTestId)
					.onFailure(x -> new EntityNotFoundException(TemplateTest.class, tplTestId, x))
					// get TemplateComponents from TemplateRoot
					.map(templateTest -> Stream.ofAll(templateTest.getTemplateTestStepUsages())
							.find(templateTestStepUsage -> templateTestStepUsage.getID()
									.equals(id)))
					.get();
			// .getOrElseThrow(() -> new
			// EntityNotFoundException(TemplateTestStepUsage.class, id));
		}

		// for all other cases
		// TODO anehmer on 2017-11-24: test if onFailure() can enrich Failure with
		// EntityNotFoundException
		// if so, remove onFailure() from find() to roots
		return getEntityManager(sourceName).map(em -> em.load(entityClass, id));
	}

	/**
	 * Returns a {@link Try} of all {@link Entity}s if no filter is available.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result. Can be null.
	 * @return a {@link Try} of the list of found {@link Entity}s
	 */
	public <T extends Entity> Try<List<T>> findAll(String sourceName, Class<T> entityClass, String filter) {
		return findAll(sourceName, entityClass, filter, null);
	}

	/**
	 * Returns a {@link Try} of the matching {@link Entity}s of the given
	 * contextType using the given filter or all {@link Entity}s of the given
	 * contextType provided by the {@code contextTypeSupplier} if no filter is
	 * available.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result
	 * @param contextTypeSupplier
	 *            a {@link Try} with the contextType of entity to find. Can be
	 *            {@code null} if {@code EntityType} has no {@code ContextType}.
	 * @return a {@link Try} of the list of found {@link Entity}s
	 */
	public <T extends Entity> Try<List<T>> findAll(String sourceName, Class<T> entityClass, String filter,
			Try<ContextType> contextTypeSupplier) {
		// TODO anehmer on 2017-11-22: do we need to implement the navigationActivity
		// filter shortcut like in
		// ChannelGroupService.getChannelGroups()
		if (filter == null || filter.trim()
				.length() <= 0) {
			return Try
					.of(getLoadAllEntitiesMethod(getEntityManager(sourceName).get(), entityClass, contextTypeSupplier))
					.map(javaList -> List.ofAll(javaList));
		} else {
			// TODO anehmer on 2017-11-15: not tested
			return Try
					.of(() -> this.searchActivity.search(connectorService.getContextByName(sourceName), entityClass,
							filter))
					.map(javaList -> List.ofAll(javaList));
		}
	}

	/**
	 * Returns the method to load all entities of type {@code entityClass}. If a
	 * {@code ContextType} is given the appropriate method in
	 * {@link org.eclipse.mdm.api.dflt.EntityManager} is used
	 */
	private <T extends Entity> CheckedFunction0<java.util.List<T>> getLoadAllEntitiesMethod(EntityManager entityManager,
			Class<T> entityClass, Try<ContextType> contextType) {
		// if contextType is specified
		if (contextType != null && contextType.isSuccess()) {
			return (() -> entityManager.loadAll(entityClass, contextType.get()));
		}
		return (() -> entityManager.loadAll(entityClass));
	}

	/**
	 * Creates a new {@link Entity} of type entityClass. The method searches the
	 * {@link EntityFactory} for a suitable create() method by matching the return
	 * parameter and the given entity class. If more than one method is found, the
	 * first one is taken. The argument are provided by {@link Try<Object>}s so that
	 * any exceptions thrown throughout retrieval will be wrapped in the returned
	 * {@link Try}.
	 * 
	 * @param entityClass
	 *            class of the {@link Entity} to create
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param argumentSuppliers
	 *            varargs of {@link Try<?>s that supply the create() method
	 * arguments
	 * @return a {@link Try} with the created {@link Entity}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Try<T> create(String sourceName, Class<T> entityClass, Try<?>... argumentSuppliers) {
		List<Try<?>> argSuppliers = List.of(argumentSuppliers);

		// get corresponding create method for entityClass from EntityFactory
		return Try.of(() -> connectorService.getContextByName(sourceName)
				.getEntityFactory())
				.mapTry(factory -> (T) Stream.of(EntityFactory.class.getMethods())
						// find method with the return type matching entityClass
						.filter(m -> m.getReturnType()
								.equals(entityClass))
						.filter(m -> Arrays.asList(m.getParameterTypes())
								// compare argument types
								.equals(argSuppliers.map(s -> s.get()
										.getClass())
										.toJavaList()))
						.getOrElseThrow(() -> new NoSuchMethodException(
								"No matching create()-method found for EntityType " + entityClass.getSimpleName()
										+ " taking the parameters " + argSuppliers.map(s -> s.get()
												.getClass()
												.getName())
												.collect(Collectors.joining(", "))))
						// invoke with given arguments
						.invoke(factory.get(), argSuppliers.map(s -> s.get())
								.toJavaArray()))

				// start transaction to create the entity
				.map(e -> DataAccessHelper.execute(getEntityManager(sourceName).get(), e, DataAccessHelper.CREATE));
	}

	/**
	 * Updates the given {@link Entity} with the values of the given map provided by
	 * the {@code valueMapSupplier}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entity
	 *            the entity to update
	 * @param valueMapSupplier
	 *            {@link Supplier<Map<String, Object>> of a map of values to update
	 * the entity with according to matching attribute values by name case sensitive
	 * @return a {@link Try} of the updated entity
	 */
	public <T extends Entity> Try<T> update(String sourceName, Try<T> entity,
			Try<Map<String, Object>> valueMapSupplier) {
		// return updated entity
		return
		// update entity values
		entity.map(e -> ServiceUtils.updateEntityValues(e, valueMapSupplier.get()))
				// persist entity
				.map(e -> DataAccessHelper.execute(getEntityManager(sourceName).get(), e.get(),
						DataAccessHelper.UPDATE));
	}

	/**
	 * Deletes the given {@link Entity} {@code valueMapSupplier}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entity
	 *            the entity to delete
	 * @return a {@link Try} of the deleted entity
	 */
	public <T extends Entity> Try<T> delete(String sourceName, Try<T> entity) {
		return entity
				.map(e -> DataAccessHelper.execute(getEntityManager(sourceName).get(), e, DataAccessHelper.DELETE));
	}

	/**
	 * Returns a {@link Try} of the the {@link SearchAttribute}s for the given
	 * entityClass
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to get the {@link SearchAttribute}s
	 *            for
	 * 
	 * @return a {@link Try} with the {@link SearchAttribute}s
	 */
	public <T extends Entity> Try<List<SearchAttribute>> getSearchAttributesSupplier(String sourceName, Class<T> entityClass) {
		return Try.of(() -> List.ofAll(this.searchActivity
				.listAvailableAttributes(connectorService.getContextByName(sourceName), entityClass)));
	}

	/**
	 * Returns a {@link Try} of the localized {@link Entity} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to be localized
	 * 
	 * @return a {@link Try} with the localized {@link Entity} type name
	 */
	public <T extends Entity> Try<Map<EntityType, String>> getLocalizeTypeSupplier(String sourceName, Class<T> entityClass) {
		return Try.of(() -> HashMap.ofAll(this.i18nActivity.localizeType(sourceName, entityClass)));
	}

	/**
	 * Returns a {@link Try} of the localized {@link Entity} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param entityClass
	 *            class of the {@link Entity} to be localized
	 * @return a {@link Try} with the the localized {@link Entity} attributes
	 */
	public <T extends Entity> Try<Map<Attribute, String>> getLocalizeAttributesSupplier(String sourceName, Class<T> entityClass) {
		return Try.of(() -> HashMap.ofAll(this.i18nActivity.localizeAttributes(sourceName, entityClass)));
	}

	/**
	 * Gets the EntityManager from the ConnectorService with the given source name.
	 * 
	 * @param sourceName
	 *            name of the datasource to get EntityManager for
	 * @return the found EntityManager. Throws {@link MDMEntityAccessException} if
	 *         not found.
	 */
	private Try<EntityManager> getEntityManager(String sourceName) {
		return Try.of(() -> this.connectorService.getContextByName(sourceName)
				.getEntityManager()
				.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present")));
	}
}