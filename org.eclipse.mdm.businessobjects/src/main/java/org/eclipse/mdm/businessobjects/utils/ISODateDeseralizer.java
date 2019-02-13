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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
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
	 * Deserialize JSON and try to parse every String as an ISO8601 date
	 */
	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		// try to parse every string as a date
		// TODO anehmer on 2018-04-30: this approach could lead to a performance leak as
		// every incoming string is tried to be converted into a date though the
		// appraoch is very generic
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