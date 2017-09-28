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
package org.eclipse.mdm.businessobjects.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.mdm.api.base.Transaction;
import org.eclipse.mdm.api.base.model.Deletable;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;

import io.vavr.CheckedFunction2;
import io.vavr.CheckedFunction3;
import io.vavr.collection.Stream;

/**
 * Helper class providing functions to realize transactional data operations
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public class DataAccessHelper {

	/**
	 * Returns a function that executes one transactional operation within a
	 * transaction.
	 * 
	 * @return Function that executes a transactional operation on an entity within
	 *         a transaction.
	 */
	public static <T extends Entity> CheckedFunction3<EntityManager, T, CheckedFunction2<Transaction, T, Object>, Object> execute() {
		return (em, entity, operation) -> {
			Transaction t = null;
			try {
				// start transaction to persist ValueList
				t = em.startTransaction();
				// perform the transactional operation
				operation.apply(t, entity);
				// commit the transaction
				t.commit();
			} catch (Exception e) {
				if (t != null) {
					t.abort();
				}
				throw new MDMEntityAccessException(e.getMessage(), e);
			}
			return null;
		};
	}

	/**
	 * Returns a function that performs a delete operation. The operation method
	 * returns null.
	 * 
	 * @return function that performs a delete operation
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> CheckedFunction2<Transaction, T, Object> delete() {
		return (t, entity) -> {
			if (entity instanceof Deletable) {
				t.delete((Collection<Deletable>) Stream.of(entity)
						.collect(Collectors.<T, List<T>>toCollection(LinkedList<T>::new)));
			} else {
				throw new MDMEntityAccessException(
						"Entity " + entity.getName() + "[" + entity.getID() + "] is not deletable");
			}

			return null;
		};
	}

	/**
	 * Returns a function that performs a create operation. The operation method
	 * returns null.
	 * 
	 * @return function that performs a create operation
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> CheckedFunction2<Transaction, T, Object> create() {
		return (t, entity) -> {
			t.create((Collection<Deletable>) Stream.of(entity)
					.collect(Collectors.<T, List<T>>toCollection(LinkedList<T>::new)));

			return null;
		};
	}
}
