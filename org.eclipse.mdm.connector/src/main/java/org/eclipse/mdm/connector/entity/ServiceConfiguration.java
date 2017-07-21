/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.connector.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ServiceConfiguration
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 * @author Canoo Engineering AG (support for arbitrary entity manager factories
 *         and connection parameters)
 *
 */
public class ServiceConfiguration {

	/**
	 * The fully qualified class name of the entity manager factory for this
	 * backend, never null.
	 */
	private final String entityManagerFactoryClass;

	/**
	 * An unmodifiable map holding the connection parameters for this backend;
	 * never null, but possibly empty.
	 */
	private final Map<String, String> connectionParameters;

	/**
	 * Constructs a new instance with the specified properties. The specified
	 * parameter map is copied by this constructor and no reference to the
	 * original is retained.
	 * 
	 * @param entityManagerFactoryClass
	 *            the fully qualified class name of the entity manager factory
	 *            for this backend, must not be null
	 * @param connectionParameters
	 *            a map holding the connection parameters for this backend, or
	 *            null to use an empty map instead
	 */
	public ServiceConfiguration(String entityManagerFactoryClass, Map<String, String> connectionParameters) {
		this.entityManagerFactoryClass = Objects.requireNonNull(entityManagerFactoryClass,
				"Null \"entityManagerFactoryClass\" argument passed to ServiceConfiguration constructor");
		this.connectionParameters = (connectionParameters == null ? Collections.emptyMap()
				: Collections.unmodifiableMap(new LinkedHashMap<>(connectionParameters)));
	}

	/**
	 * Returns the fully qualified class name of the entity manager factory for
	 * this backend. The result is never null.
	 * 
	 * @return the entity manager factory class name passed to the constructor,
	 *         never null
	 */
	public String getEntityManagerFactoryClass() {
		return entityManagerFactoryClass;
	}

	/**
	 * Returns an unmodifiable map holding the connection parameters for this
	 * backend. The result is never null, but may be empty.
	 * 
	 * @return an unmodifiable copy of the connection parameter map passed to
	 *         the constructor; never null, but possibly empty
	 */
	public Map<String, String> getConnectionParameters() {
		return connectionParameters;
	}

	@Override
	public String toString() {
		return this.entityManagerFactoryClass + "#" + connectionParameters;
	}

}
