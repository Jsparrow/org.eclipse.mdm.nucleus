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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;

/**
 * {@link io.vavr.collection.HashMap} subclasss providing failure handling if
 * keys or values are not present.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class RequestBody {

	HashMap<String, Object> requestBodyMap;

	/**
	 * Just hide the default constructor
	 */
	private RequestBody() {
	};

	/**
	 * Builds RequestBody from JSON body map
	 * 
	 * @param requestBodyMap
	 *            map with the parsed JSON body
	 */
	private RequestBody(java.util.HashMap<String, Object> requestBodyMap) {
		this.requestBodyMap = HashMap.ofAll(requestBodyMap);
	}

	/**
	 * Creates a RequestBody as a {@link io.vavr.control.Try}
	 * 
	 * @param body
	 *            JSON body to create RequestBody
	 * @return the RequestBody as a {@link io.vavr.control.Try}
	 */
	public static Try<RequestBody> create(String body) {
		return Try.of(() -> new RequestBody(
				new ObjectMapper().readValue(body, new TypeReference<java.util.Map<String, Object>>() {
				})));
	}

	/**
	 * Get the value for the given key as a String wrapped in a Try.
	 * 
	 * @param key
	 *            key to get value for
	 * @return the value as a String wrapped in a
	 *         {@link io.vavr.control.Try.Success}. If the key is not present or no
	 *         value is present, a {@link io.vavr.control.Try.Failure} is returned.
	 */
	public String getString(String key) {
		return requestBodyMap.get(key)
				.toString();
	}

	/**
	 * Returns a complete {@link Map<String, Object>} of the body
	 * 
	 * @return a {@link Map<String, Object> of the body
	 */
	public Map<String, Object> getMap() {
		return requestBodyMap;
	}
}
