/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.businessobjects.service;

import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.mdm.api.base.Transaction;
import org.eclipse.mdm.api.base.model.Deletable;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.CheckedFunction2;

/**
 * Helper class providing functions to realize transactional data operations
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class DataAccessHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DataAccessHelper.class);

	/**
	 * Just hide the default constructor
	 */
	private DataAccessHelper() {
	}

	/**
	 * Returns a function that executes one transactional operation within a
	 * transaction.
	 * 
	 * @return Function that executes a transactional operation on an entity within
	 *         a transaction.
	 */
	// TODO anehmer on 2017-11-22: extend this method to handle lists of objects
	public static <T extends Entity> T execute(EntityManager em, T entity,
			CheckedFunction2<Transaction, Entity, Entity> operation) {
		Transaction t = null;
		try {
			// start transaction to persist ValueList
			t = em.startTransaction();
			// perform the transactional operation
			@SuppressWarnings("unchecked")
			T processedEntity = (T) operation.apply(t, entity);
			// commit the transaction
			t.commit();
			// return the processed entity
			return processedEntity;
		} catch (Throwable e) {
			if (t != null) {
				t.abort();
			}
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Function that creates an {@link Entity} and returns it.
	 */
	public static final CheckedFunction2<Transaction, Entity, Entity> DELETE = (transaction,
			entity) -> {
		if (entity instanceof Deletable) {
			// TODO anehmer on 2017-11-22: call to delete() should return entity
			transaction.delete(Arrays.asList((Deletable) entity));
		}
		// if entity cannot be deleted
		else {
			throw new MDMEntityAccessException("Entity to delete is no Deletable");
		}
		return entity;
	};

	/**
	 * Function that creates the given {@link Entity} and returns the updated
	 * entity.
	 */
	public static final CheckedFunction2<Transaction, Entity, Entity> CREATE = (transaction,
			entity) -> {
		// TODO anehmer on 2017-11-22: call to create() should return entity
		transaction.create(Arrays.asList(entity));
		return entity;
	};

	/**
	 * Function that updates the given {@link Entity} it is executed upon and
	 * returns the updated entity.
	 */
	public static final CheckedFunction2<Transaction, Entity, Entity> UPDATE = (transaction,
			entity) -> {
		// TODO anehmer on 2017-11-22: call to update() should return entity
		transaction.update(Arrays.asList(entity));
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

	/**
	 * Function that handles an occurred exception without rethrowing it
	 */
	// TODO anehmer on 2017-11-22: remove method and logger
	public static final Consumer<? super Throwable> handleException = e -> LOG.error(e.getMessage(), e);
}
