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

package org.eclipse.mdm.businessobjects.utils;

import org.eclipse.mdm.api.base.model.Entity;

/**
 * Exception thrown if entity could not be retrieved from the datastore
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6862157710262117670L;

	/**
	 * Default constructor with causing exception
	 * 
	 * @param entityClass
	 *            Class of entity that could not be found
	 * @param id
	 *            id of entity that could not be found
	 * @param x
	 *            the cause of the exception
	 */
	public EntityNotFoundException(Class<? extends Entity> entityClass, String id, Throwable x) {
		super(entityClass.getSimpleName() + " with ID " + id + " not found.", x);
	}

	/**
	 * Default constructor without causing exception
	 * 
	 * @param entityClass
	 *            Class of entity that could not be found
	 * @param id
	 *            id of entity that could not be found
	 */
	public EntityNotFoundException(Class<? extends Entity> entityClass, String id) {
		super(entityClass.getSimpleName() + " with ID " + id + " not found.");
	}
}
