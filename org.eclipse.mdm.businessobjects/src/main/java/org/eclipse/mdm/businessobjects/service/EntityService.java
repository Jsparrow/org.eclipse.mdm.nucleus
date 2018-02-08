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

import static io.vavr.API.Tuple;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.Core;
import org.eclipse.mdm.api.base.adapter.EntityStore;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.BaseEntity;
import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.EnumRegistry;
import org.eclipse.mdm.api.base.model.EnumerationValue;
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
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.CheckedFunction0;
import io.vavr.Function0;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Class providing basic data access methods to {@link Entity}s.
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

	Logger LOGGER = LoggerFactory.getLogger(EntityService.class);

	/**
	 * Converts a {@code value} into a {@link Value}. If {@code value} is
	 * {@code null}, {@code Value.isEmpty() == true}.
	 * 
	 * @param value
	 *            the value to wrap in a {@link Value}
	 * @return the created {@link Value}
	 */
	// TODO anehmer on 2017-11-26: rename to toValue()?
	public static <T> Value<T> V(T value) {
		return Option.of(value);
	}

	/**
	 * Converts the given string values into a {@link Seq} of {@link Value}s
	 * wrapping the value.
	 * 
	 * @param value
	 *            the {@link Value}s to put in a {@link Seq}
	 * @return the created {@link Seq} of {@link Value}s
	 */
	public static Seq<Value<String>> SL(String... values) {
		return List.of(values).map(s -> Option.of(s));
	}

	/**
	 * Converts the given string {@link Value}s into a {@link Seq} of string
	 * {@link Value}s.
	 * 
	 * @param value
	 *            the {@link Value}s to put in a {@link Seq}
	 * @return the created {@link Seq} of {@link Value}s
	 */
	@SafeVarargs
	public static Seq<Value<String>> SL(Value<String>... values) {
		return List.of(values);
	}

	/**
	 * Converts the given {@link Value}s into a {@link Seq} of {@link Value}s.
	 * 
	 * @param value
	 *            the {@link Value}s to put in a {@link Seq}
	 * @return the created {@link Seq} of {@link Value}s
	 */
	@SafeVarargs
	public static Seq<Value<?>> L(Value<?>... values) {
		return List.of(values);
	}

	/**
	 * @see #find(Value, Class, Value, Value, Value...)
	 */
	public <T extends Entity> Try<T> find(Value<String> sourceNameSupplier, Class<T> entityClass,
			Value<String> idSupplier) {
		return find(sourceNameSupplier, entityClass, idSupplier, (Value<ContextType>) null, null);
	}

	/**
	 * @see #find(Value, Class, Value, Value, Value...)
	 */
	public <T extends Entity> Try<T> find(Value<String> sourceNameSupplier, Class<T> entityClass,
			Value<String> idSupplier, Seq<Value<String>> parentIdSuppliers) {
		return find(sourceNameSupplier, entityClass, idSupplier, (Value<ContextType>) null, parentIdSuppliers);
	}

	/**
	 * @see #find(Value, Class, Value, Value, Value...)
	 */
	public <T extends Entity> Try<T> find(Value<String> sourceNameSupplier, Class<T> entityClass,
			Value<String> idSupplier, Value<ContextType> contextTypeSupplier) {
		return find(sourceNameSupplier, entityClass, idSupplier, contextTypeSupplier, null);
	}

	/**
	 * Returns the specified entity by given {@code entityClass} and given
	 * {@code id}. If the {@code entityClass} is either {@link CatalogAttribute},
	 * {@link TemplateCompont}, {@link TemplateAttributeAttribute} or
	 * {@link ContextComponent} the respective root entities
	 * {@link CatalogComponent}, {@link TemplateRoot} or {@link ContextRoot} are
	 * used to get the entity to find.
	 * 
	 * @param sourceNameSupplier
	 *            {@link Value} with the name of the source (MDM {@link Environment}
	 *            name)
	 * @param entityClass
	 *            entityType
	 * @param idSupplier
	 *            {@link Value} with id of entity to find
	 * @param contextTypeSupplier
	 *            {@link Value} with the contextType of entity to find. Can be
	 *            {@code null} if {@code EntityType} has no {@code ContextType}.
	 * @param parentIdSuppliers
	 *            {@link Value}s with the id(s) of parent(s). For
	 *            {@link CatalogAttribute} the parentId must be the id of the
	 *            {@link CatalogComponent}, for {@link TemplateComponent} it must be
	 *            the id of the {@link TemplateRoot}, for {@link TemplateAttribute}
	 *            it must be the id of the {@link TemplateRoot} first and also the
	 *            {@link TemplateComponent}, for a nested {@link TemplateComponent}
	 *            it must be the id of the {@link TemplateRoot} first and the id of
	 *            the parent {@link TemplateComponent} second, for a
	 *            {@link TemplateAttribute} within a nested
	 *            {@link TemplateComponent} it must be the id of the
	 *            {@link TemplateRoot} first, the id of the parent
	 *            {@link TemplateComponent} second and the id of the
	 *            {@link TemplateComponent} last and for {@link ContextComponent} it
	 *            must be the id of the {@link ContextRoot}.
	 * @return {@link Try} with the found entity
	 */
	// TODO anehmer on 2017-11-22: complete javadoc for ValueListValue and
	// TemplateTestStepUsage
	// TODO anehmer on 2017-11-22: add comment for parentIds for TplSensors and
	// nested TplSensors as well as for TplSensorAttrs and nested TplSensorAttrs
	@SuppressWarnings("unchecked")
	public <T extends Entity> Try<T> find(Value<String> sourceNameSupplier, Class<T> entityClass,
			Value<String> idSupplier, Value<ContextType> contextTypeSupplier, Seq<Value<String>> parentIdSuppliers) {

		// validate parentIds count
		Map<Class<?>, Integer> minParentsForEntity = HashMap.empty();
		minParentsForEntity = minParentsForEntity.put(Tuple(CatalogAttribute.class, 1))
				.put(Tuple(CatalogSensor.class, 1)).put(Tuple(TemplateComponent.class, 1))
				.put(Tuple(TemplateAttribute.class, 2)).put(Tuple(TemplateSensor.class, 2))
				.put(Tuple(ValueListValue.class, 1)).put(Tuple(TemplateTestStepUsage.class, 1));

		// return failure if number of parentIds do not correspond with the minimu
		// required by the entity type
		Option<Integer> minParents = minParentsForEntity.get(entityClass);
		// TODO anehmer on 2017-11-25: add entity types to message
		if (minParents.isDefined() && (parentIdSuppliers == null || minParents.get() > parentIdSuppliers.size())) {
			return Try.failure(new IllegalArgumentException("ParentId(s) of " + entityClass.getSimpleName()
					+ " not set appropriately. Expected minimum: " + minParents.get()));
		}

		// if the find is contextType specific
		if (contextTypeSupplier != null && !contextTypeSupplier.isEmpty()) {
			if (entityClass.equals(CatalogAttribute.class)) {
				// get CatalogAttribute from CatalogComponent
				if (parentIdSuppliers.size() == 1) {
					return find(sourceNameSupplier, CatalogComponent.class, parentIdSuppliers.get(0),
							contextTypeSupplier)
									.map(catComp -> (T) getChild(CatalogAttribute.class, idSupplier,
											catComp::getCatalogAttributes));
				}
				// get the CatalogAttribute from a CatalogSensor
				else if (parentIdSuppliers.size() == 2) {
					return find(sourceNameSupplier, CatalogSensor.class, parentIdSuppliers.get(1), contextTypeSupplier,
							parentIdSuppliers.dropRight(1))
									.map(catComp -> (T) getChild(CatalogAttribute.class, idSupplier,
											catComp::getCatalogAttributes));
				}
			}

			// get CatalogSensor from CatalogComponent
			else if (entityClass.equals(CatalogSensor.class)) {
				return find(sourceNameSupplier, CatalogComponent.class, parentIdSuppliers.get(0), contextTypeSupplier)
						.map(catComp -> (T) getChild(CatalogSensor.class, idSupplier, catComp::getCatalogSensors));
			}

			// get TemplateComponent from TemplateRoot or parent TemplateComponent(s)
			else if (entityClass.equals(TemplateComponent.class)) {
				// if nested TplComp has to be found
				if (parentIdSuppliers.size() > 1) {
					return find(sourceNameSupplier, TemplateComponent.class,
							parentIdSuppliers.get(parentIdSuppliers.size() - 1), contextTypeSupplier,
							parentIdSuppliers.dropRight(1))
									.map(tplComp -> (T) getChild(TemplateComponent.class, idSupplier,
											tplComp::getTemplateComponents));
				}
				// if non-nested TplComp has to be found: exit condition of recursive call
				return find(sourceNameSupplier, TemplateRoot.class, parentIdSuppliers.get(0), contextTypeSupplier).map(
						tplRoot -> (T) getChild(TemplateComponent.class, idSupplier, tplRoot::getTemplateComponents));
			}

			// get TemplateAttributes from TemplateComponent
			else if (entityClass.equals(TemplateAttribute.class)) {
				Try<TemplateComponent> tplCompTry = find(sourceNameSupplier, TemplateComponent.class,
						parentIdSuppliers.get(parentIdSuppliers.size() - 1), contextTypeSupplier,
						parentIdSuppliers.dropRight(1));
				// if TemplateSensorAttribute has to be found
				if (!tplCompTry.isFailure()) {
					return tplCompTry.map(tplComp -> (T) getChild(TemplateAttribute.class, idSupplier,
							tplComp::getTemplateAttributes));
				} else {
					return find(sourceNameSupplier, TemplateSensor.class,
							parentIdSuppliers.get(parentIdSuppliers.size() - 1), contextTypeSupplier,
							parentIdSuppliers.dropRight(1))
									.map(tplComp -> (T) getChild(TemplateAttribute.class, idSupplier,
											tplComp::getTemplateAttributes));
				}
			}

			// get TemplateSensor from TemplateComponent
			else if (entityClass.equals(TemplateSensor.class)) {
				return find(sourceNameSupplier, TemplateComponent.class,
						parentIdSuppliers.get(parentIdSuppliers.size() - 1), contextTypeSupplier,
						parentIdSuppliers.dropRight(1)).map(
								tplComp -> (T) getChild(TemplateSensor.class, idSupplier, tplComp::getTemplateSensors));
			}

			// get ContextComponent from ContextRoot
			else if (entityClass.equals(ContextComponent.class)) {
				// TODO anehmer on 2017-11-09: implement (also for nested ContextComponents)
				throw new NotImplementedException("NOT IMPLEMENTED YET");
			}

			// get root nested entities (CatalogComponent, TemplateRoot, ContextRoot)
			return getEntityManager(sourceNameSupplier)
					.mapTry(em -> em.load(entityClass, contextTypeSupplier.get(), idSupplier.get()));
		}

		// get ValueListValue from ValueList
		else if (entityClass.equals(ValueListValue.class)) {
			return find(sourceNameSupplier, ValueList.class, parentIdSuppliers.get(0))
					.map(valueList -> (T) getChild(ValueListValue.class, idSupplier, valueList::getValueListValues));
		}

		// get TemplateTestStepUsage from TemplateTest
		else if (entityClass.equals(TemplateTestStepUsage.class)) {
			return find(sourceNameSupplier, TemplateTest.class, parentIdSuppliers.get(0))
					.map(tplTest -> (T) getChild(TemplateTestStepUsage.class, idSupplier,
							tplTest::getTemplateTestStepUsages));
		}

		// for all other cases
		return getEntityManager(sourceNameSupplier).map(em -> em.load(entityClass, idSupplier.get()));
	}

	/**
	 * Gets the child with the given {@code childId} or an EntityNotFoundException
	 * if the child was not found
	 * 
	 * @param childClass
	 *            class of child to construct exception on failure
	 * @param childIdSupplier
	 *            supplier of the id of child to find
	 * @param childSupplier
	 *            function that gets all children
	 * @return the found child
	 */
	private <T extends Entity> T getChild(Class<T> childClass, Value<String> childIdSupplier,
			Function0<java.util.List<T>> childSupplier) {
		return Stream.ofAll(childSupplier.apply())
				.find(childEntity -> childEntity.getID().equals(childIdSupplier.get()))
				.getOrElseThrow(() -> new EntityNotFoundException(childClass, childIdSupplier.get()));
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
	// TODO anehmer on 2017-11-26: make filter Value<String>
	public <T extends Entity> Try<List<T>> findAll(Value<String> sourceNameSupplier, Class<T> entityClass,
			String filter) {
		return findAll(sourceNameSupplier, entityClass, filter, null);
	}

	/**
	 * Returns a {@link Try} of the matching {@link Entity}s of the given
	 * contextType using the given filter or all {@link Entity}s of the given
	 * contextType provided by the {@code contextTypeSupplier} if no filter is
	 * available.
	 * 
	 * @param sourceNameSupplier
	 *            {@link Value} with the name of the source (MDM {@link Environment}
	 *            name)
	 * @param entityClass
	 *            class of the {@link Entity} to find
	 * @param filter
	 *            filter string to filter the {@link Entity} result
	 * @param contextTypeSupplier
	 *            a {@link Value} with the contextType of entity to find. Can be
	 *            {@code null} if {@code EntityType} has no {@code ContextType}.
	 * @return a {@link Try} of the list of found {@link Entity}s
	 */
	// TODO anehmer on 2017-11-26: make filter Value<String>
	public <T extends Entity> Try<List<T>> findAll(Value<String> sourceNameSupplier, Class<T> entityClass,
			String filter, Value<ContextType> contextTypeSupplier) {
		// TODO anehmer on 2017-11-22: do we need to implement the navigationActivity
		// filter shortcut like in ChannelGroupService.getChannelGroups()
		if (filter == null || filter.trim().length() <= 0) {
			return Try.of(getLoadAllEntitiesMethod(getEntityManager(sourceNameSupplier).get(), entityClass,
					contextTypeSupplier)).map(javaList -> List.ofAll(javaList));
		} else {
			// TODO anehmer on 2017-11-15: not tested
			return Try.of(() -> this.searchActivity.search(connectorService.getContextByName(sourceNameSupplier.get()),
					entityClass, filter)).map(javaList -> List.ofAll(javaList));
		}
	}

	/**
	 * Returns the method to load all entities of type {@code entityClass}. If a
	 * {@code ContextType} is given the appropriate method in
	 * {@link org.eclipse.mdm.api.dflt.EntityManager} is used
	 * 
	 * @param entityManager
	 *            entityManager to load entities with
	 * @param entityClass
	 *            class of entites to load
	 * @param contextType
	 *            {@link ContextType} of entities of null if none
	 * @return the appropriate loadAllEntities() method
	 */
	private <T extends Entity> CheckedFunction0<java.util.List<T>> getLoadAllEntitiesMethod(EntityManager entityManager,
			Class<T> entityClass, Value<ContextType> contextType) {
		// if contextType is specified
		if (contextType != null && !contextType.isEmpty()) {
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
	public <T extends Entity> Try<T> create(Value<String> sourceNameSupplier, Class<T> entityClass,
			Seq<Value<?>> argumentSuppliers) {

		// get corresponding create method for entityClass from EntityFactory
		return Try.of(() -> connectorService.getContextByName(sourceNameSupplier.get()).getEntityFactory())
				.mapTry(factory -> (T) Stream.of(EntityFactory.class.getMethods())
						// find method with the return type matching entityClass
						.filter(m -> m.getReturnType().equals(entityClass))
						.filter(m -> Arrays.asList(m.getParameterTypes())
								// compare argument types
								.equals(argumentSuppliers.map(s -> s.get().getClass()).toJavaList()))
						.getOrElseThrow(
								() -> new NoSuchMethodException("No matching create()-method found for EntityType "
										+ entityClass.getSimpleName() + " taking the parameters "
										+ argumentSuppliers.map(s -> s.get().getClass().getName())
												.collect(Collectors.joining(", "))))
						// invoke with given arguments
						.invoke(factory.get(), argumentSuppliers.map(s -> s.get()).toJavaArray()))

				// start transaction to create the entity
				.map(e -> DataAccessHelper.execute(getEntityManager(sourceNameSupplier).get(), e,
						DataAccessHelper.CREATE));
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
	public <T extends Entity> Try<T> update(Value<String> sourceNameSupplier, Try<T> entity,
			Value<Map<String, Object>> valueMapSupplier) {
		// return updated entity
		return
		// update entity values
		entity.map(e -> updateEntityValues(e, valueMapSupplier.get(), sourceNameSupplier))
				// persist entity
				.map(e -> DataAccessHelper.execute(getEntityManager(sourceNameSupplier).get(), e.get(),
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
	public <T extends Entity> Try<T> delete(Value<String> sourceNameSupplier, Try<T> entity) {
		return entity.map(
				e -> DataAccessHelper.execute(getEntityManager(sourceNameSupplier).get(), e, DataAccessHelper.DELETE));
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
	public <T extends Entity> Try<List<SearchAttribute>> getSearchAttributesSupplier(Value<String> sourceNameSupplier,
			Class<T> entityClass) {
		return Try.of(() -> List.ofAll(this.searchActivity
				.listAvailableAttributes(connectorService.getContextByName(sourceNameSupplier.get()), entityClass)));
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
	public <T extends Entity> Try<Map<EntityType, String>> getLocalizeTypeSupplier(Value<String> sourceNameSupplier,
			Class<T> entityClass) {
		return Try.of(() -> HashMap.ofAll(this.i18nActivity.localizeType(sourceNameSupplier.get(), entityClass)));
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
	public <T extends Entity> Try<Map<Attribute, String>> getLocalizeAttributesSupplier(
			Value<String> sourceNameSupplier, Class<T> entityClass) {
		return Try.of(() -> HashMap.ofAll(this.i18nActivity.localizeAttributes(sourceNameSupplier.get(), entityClass)));
	}

	/**
	 * Returns a {@link Try} of an {@link EnumerationValue} for the name supplied by
	 * the {@code enumValueNameSupplier}
	 * 
	 * @param enumValueNameSupplier
	 *            supplies the name of the {@link EnumerationValue} to get
	 * @return a {@link Try} with the resolved {@link EnumerationValue}
	 */
	public Try<EnumerationValue> getEnumerationValueSupplier(Try<?> enumValueNameSupplier) {
		return Try.of(() -> EnumRegistry.getInstance().get(EnumRegistry.VALUE_TYPE)
				.valueOf(enumValueNameSupplier.get().toString()));
	}

	/**
	 * Gets the EntityManager from the ConnectorService with the given source name
	 * provided by the {@code sourceNameSupplier}.
	 * 
	 * @param sourceNameSupplier
	 *            {@link Value} with the name of the datasource to get EntityManager
	 *            for
	 * @return the found EntityManager. Throws {@link MDMEntityAccessException} if
	 *         not found.
	 */
	private Try<EntityManager> getEntityManager(Value<String> sourceNameSupplier) {
		return Try.of(() -> this.connectorService.getContextByName(sourceNameSupplier.get()).getEntityManager()
				.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present")));
	}

	/**
	 * Updates the given {@link Entity} with the values from the given valueMap. All
	 * matching attributes (case sensitive) are updated as well as the referenced
	 * relations by the id of the given
	 * {@link org.eclipse.mdm.api.base.model.Entity} and the simple class name as
	 * the key (the data model attribute name is the reference, case sensitive).
	 * 
	 * @param entity
	 *            the entity to update
	 * @param valueMap
	 *            values to update the entity with according to matching attribute
	 *            names. The keys are compared case sensitive.
	 * @return a {@link Try} with the the updated entity
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Try<T> updateEntityValues(T entity, Map<String, Object> valueMap,
			Value<String> sourceNameSupplier) {

		HashMap<String, org.eclipse.mdm.api.base.model.Value> entityValues = HashMap.ofAll(entity.getValues());

		// update values where the key from the valueMap has a matching entity value
		// and collect the updated keys
		Set<String> updatedValues = valueMap
				.filter((valueMapEntryKey, valueMapEntryValue) -> entityValues.containsKey(valueMapEntryKey))
				.map((entityValueEntryKey, entityValueEntryValue) -> {
					entityValues.get(entityValueEntryKey).forEach(value -> value.set(entityValueEntryValue));
					return new Tuple2<String, Object>(entityValueEntryKey, entityValueEntryValue);
				}).keySet();

		// update the relations and gather the updated keys
		// use only those keys that have not been updated yet and can be resolved as
		// class names. If so, try to update accordingly named relation with the entity
		// found by its id given as the value
		Set<String> updatedRelations = valueMap
				.filter((valueMapEntryKey, valueMapEntryValue) -> !updatedValues.contains(valueMapEntryKey))
				.filter((relatedEntityClassName, relatedEntityId) -> {
					EntityStore store = getMutableStore(entity);

					// load class from model packages
					Try<Class<Entity>> updateTry = Try
							.of(() -> (Class<Entity>) Class
									.forName("org.eclipse.mdm.api.base.model." + relatedEntityClassName))
							.orElse(Try.of(() -> (Class<Entity>) Class
									.forName("org.eclipse.mdm.api.dflt.model." + relatedEntityClassName)))
							// update related entity by first finding the related entity by its id
							.andThenTry(entityClass -> store
									.set(find(sourceNameSupplier, entityClass, V(relatedEntityId.toString()))
											.onFailure(e -> LOGGER.error(e.getMessage())).get()))
							.onFailure(e -> LOGGER.error("Entity of type [" + relatedEntityClassName + "] and ID "
									+ relatedEntityId + " not found", e));

					return updateTry.isSuccess() ? true : false;
				}).keySet();

		// return Try.Failure if there are keys that are not present in the entity and
		// thus are not updated
		String unmappedKeys = valueMap
				.filterKeys(key -> !updatedValues.contains(key) && !updatedRelations.contains(key)).map(Tuple::toString)
				.collect(Collectors.joining(", "));

		if (unmappedKeys != null && !unmappedKeys.isEmpty()) {
			return Try.failure(
					new IllegalArgumentException("ValueMap to update entity contains the following keys that either "
							+ "have no match in the entity values or relations to update "
							+ "or an error occurred while finding the related entity: " + unmappedKeys));
		} else {
			return Try.of(() -> entity);
		}
	}

	/**
	 * Get the mutableStore from {@link org.eclipse.mdm.api.base.adapter.Core} of
	 * given {@link org.eclipse.mdm.api.base.model.Entity}
	 * 
	 * @param e
	 *            Entity to get Core of
	 * @return Core of given Entity
	 */
	private static EntityStore getMutableStore(Entity e) {
		return Try.of(() -> {
			Method GET_CORE_METHOD;
			try {
				GET_CORE_METHOD = BaseEntity.class.getDeclaredMethod("getCore");
				GET_CORE_METHOD.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException x) {
				throw new IllegalStateException(
						"Unable to load 'getCore()' in class '" + BaseEntity.class.getSimpleName() + "'.", x);
			}
			Core core = (Core) GET_CORE_METHOD.invoke(e);
			EntityStore store = core.getMutableStore();

			return store;
		}).get();
	}

}