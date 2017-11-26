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

import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.API;
import io.vavr.CheckedFunction0;
import io.vavr.Lazy;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;

/**
 * Class representing the JSON request body as a accessible map of values. The
 * parsing occurs lazily on first get() on a value and as the {@link Lazy}
 * result is memoized, the body is only parsed once. Any exceptions due to
 * parsing or non-existing keys are thrown on get() call.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class RequestBody {

	Lazy<HashMap<String, Object>> requestBodyMap;

	/**
	 * Just hide the default constructor
	 */
	private RequestBody() {
	};

	/**
	 * Builds RequestBody by using the given function that returns the request body
	 * as a parsed {@link Map<String, Object>.
	 * 
	 * @param requestBodyMapSupplier
	 *            function that return a {@link Map<String, Object> with the parsed
	 *            JSON body
	 */
	private RequestBody(CheckedFunction0<HashMap<String, Object>> requestBodyMapSupplier) {
		// make the function lazily evaluated, so that any parsing exception occur on
		// first get() on a value
		// unchecked() makes it possible to trick the compiler to not force us to deal
		// with the checked exceptions here
		this.requestBodyMap = Lazy.of(API.unchecked(requestBodyMapSupplier));
	}

	/**
	 * Creates a RequestBody
	 * 
	 * @param body
	 *            JSON request body to create RequestBody
	 * @return the RequestBody
	 */
	public static RequestBody create(String requestBody) {
		// create requestbody with a function that parses the json request and
		// transforms it to a {@link map<string, object>
		return new RequestBody(() -> {
			return HashMap.ofAll(
					new ObjectMapper().readValue(requestBody, new TypeReference<java.util.Map<String, Object>>() {
					}));
		});
	}

	/**
	 * Returns a {@link Try> that holds the string value for the given key. If the
	 * underlying request body map can be parsed, the appropriate JSON exceptions
	 * are thrown if get() is called on the {@link Try>. If the key was not found, a
	 * {@link NoSuchElementException} is thrown correspondingly.
	 * 
	 * @param key
	 *            key to get value for
	 * @return the string value for the given key
	 */
	public Try<String> getStringValueSupplier(String key) {
		return Try.of(() -> Lazy.of(() -> requestBodyMap.get()
				.get(key)
				.map(value -> value.toString())
				.getOrElseThrow(() -> new NoSuchElementException("Key [" + key + "] not found in request body.")))
				.get());
	}

	/**
	 * Returns a {@link Try> that holds the value for the given key. If the
	 * underlying request body map can be parsed, the appropriate JSON exceptions
	 * are thrown if get() is called on the {@link Try>. If the key was not found, a
	 * {@link NoSuchElementException} is thrown correspondingly.
	 * 
	 * @param key
	 *            key to get value for
	 * @return the value for the given key
	 */
	public Try<?> getValueSupplier(String key) {
		return Try.of(() -> Lazy.of(() -> requestBodyMap.get()
				.get(key)
				.map(value -> value.toString())
				.getOrElseThrow(() -> new NoSuchElementException("Key [" + key + "] not found in request body.")))
				.get());
	}

	/**
	 * Returns a {@link Try} of the complete {@link Map<String, Object>} of the
	 * request body
	 * 
	 * @return a {@link Try} of {@link Map<String, Object>} of the request body
	 */
	public Try<Map<String, Object>> getValueMapSupplier() {
		return Try.of(() -> requestBodyMap.get());
	}
}
