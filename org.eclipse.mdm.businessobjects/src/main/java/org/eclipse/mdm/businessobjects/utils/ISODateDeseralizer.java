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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

/**
 * JSON Deserializer for ISO 8601 compliant dates with format
 * 
 * <pre>
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 * </pre>
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
class ISODateDeseralizer extends UntypedObjectDeserializer {

	private static final long serialVersionUID = 1L;

	transient DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * Overridden constructor as default one is deprecated
	 * 
	 * @param listType
	 * @see #{@link com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer}
	 * @param mapType
	 * @see #{@link com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer}
	 */
	public ISODateDeseralizer(JavaType listType, JavaType mapType) {
		super(listType, mapType);
	}

	/**
	 * Deserialize JSON and try to parse every String as an ISO8601 date
	 */
	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		// try to parse every string as a date
		if (jp.getCurrentTokenId() == JsonTokenId.ID_STRING) {
			try {
				return LocalDateTime.parse(jp.getText(), dateFormatter);
			} catch (Exception e) {
				return super.deserialize(jp, ctxt);
			}
		} else {
			return super.deserialize(jp, ctxt);
		}
	}
}