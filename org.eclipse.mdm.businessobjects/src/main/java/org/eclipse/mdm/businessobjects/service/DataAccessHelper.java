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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.mdm.api.base.Transaction;
import org.eclipse.mdm.api.base.model.Deletable;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.CheckedFunction2;
import io.vavr.CheckedFunction3;
import io.vavr.collection.Stream;

/**
 * Helper class providing functions to realize transactional data operations
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class DataAccessHelper {

	// TODO use logger from caller to preserve the error context
	private static final Logger LOG = LoggerFactory.getLogger(DataAccessHelper.class);

	/**
	 * Just hide the default constructor
	 */
	private DataAccessHelper() {
	}

	// TODO realize the functions as static to have the () erased from the call

	/**
	 * Returns a function that executes one transactional operation within a
	 * transaction.
	 * 
	 * @return Function that executes a transactional operation on an entity within
	 *         a transaction.
	 */
	// TODO extend this method to handle lists of objects
	public static <T extends Entity> CheckedFunction3<EntityManager, T, CheckedFunction2<Transaction, T, Object>, T> execute() {
		return (em, entity, operation) -> {
			Transaction t = null;
			try {
				// start transaction to persist ValueList
				t = em.startTransaction();
				// perform the transactional operation
				operation.apply(t, entity);
				// commit the transaction
				t.commit();
				// return the processed entity
				return entity;
			} catch (Exception e) {
				if (t != null) {
					t.abort();
				}
				throw new MDMEntityAccessException(e.getMessage(), e);
			}
		};
	}

	/**
	 * Returns a function that performs a delete operation. The operation method
	 * returns the deleted {@link Entity}.
	 * 
	 * @return function that performs a delete operation
	 */
	// TODO make that a constant DELETE
	@SuppressWarnings("unchecked")
	// TODO make the return value of delete() an Option
	// TODO do we really want to return something if deleting an object -> could be
	// some kind to success marker and it's nice for a caller to build a response
	// upon
	public static <T extends Entity> CheckedFunction2<Transaction, T, Object> delete() {
		return (t, entity) -> {
			if (entity instanceof Deletable) {
				t.delete((Collection<Deletable>) Stream.of(entity)
						.collect(Collectors.<T, List<T>>toCollection(LinkedList<T>::new)));
				// TODO did this "return entity" killed some callers like CatComp, CatAttr,
				// ValueList etc.?
				return entity;
			} else {
				throw new MDMEntityAccessException(
						"Entity " + entity.getName() + "[" + entity.getID() + "] is not deletable");
			}
		};
	}

	/**
	 * Returns a function that performs a create operation. The operation method
	 * returns the created {@link Entity}.
	 * 
	 * @return function that performs a create operation
	 */
	// TODO make that a constant CREATE. Replace T by just Entity. Should work.
	@SuppressWarnings("unchecked")
	public static <T extends Entity> CheckedFunction2<Transaction, T, Object> create() {
		return (t, entity) -> {
			t.create((Collection<Deletable>) Stream.of(entity)
					.collect(Collectors.<T, List<T>>toCollection(LinkedList<T>::new)));

			return entity;
		};
	}

	/**
	 * Function that updates the given {@link Entity} it is executed upon and return
	 * the updated entity.
	 */
	// TODO return type of function should be Entity
	public static final CheckedFunction2<Transaction, Entity, Object> UPDATE = (t, entity) -> {
		t.update((Collection<Entity>) Stream.of(entity)
				.collect(Collectors.<Entity, List<Entity>>toCollection(LinkedList<Entity>::new)));

		return entity;
	};

	/**
	 * Handles a {@link Throwable} by loggging the exception message and rethrowing
	 * a {@link MDMEntityAccessException}
	 */
	// TODO should be replaced in Resources by buildErrorResponse()
	public static final Consumer<? super Throwable> rethrowAsMDMEntityAccessException = e -> {
		// TODO anehmer on 2017-11-09: check if logging is necessary depending on how we
		// handle error logging and client response in general
		LOG.error(e.getMessage(), e);
		throw new MDMEntityAccessException(e.getMessage(), e);
	};

}
