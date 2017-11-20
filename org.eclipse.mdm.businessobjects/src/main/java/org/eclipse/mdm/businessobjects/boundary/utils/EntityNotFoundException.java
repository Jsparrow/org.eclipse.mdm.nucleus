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
package org.eclipse.mdm.businessobjects.boundary.utils;

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
	 * @param e
	 *            the cause of the exception if there is one
	 */
	public EntityNotFoundException(Class<? extends Entity> entityClass, String id, Exception e) {
		super(entityClass.getSimpleName() + " with ID " + id + " not found.", e);
	}

	/**
	 * Default constructor without causing exception
	 * 
	 * @param entityClass
	 *            Class of entity that could not be found
	 * @param id
	 *            id of entity that could not be found
	 * @param e
	 *            the cause of the exception if there is one
	 */
	public EntityNotFoundException(Class<? extends Entity> entityClass, String id) {
		super(entityClass.getSimpleName() + " with ID " + id + " not found.");
	}
}
