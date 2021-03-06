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

package org.eclipse.mdm.freetextindexer.boundary;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.eclipse.mdm.property.GlobalProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This Boundary is back-end only to the ElasticSearch Server. It is responsible
 * for the actual indexing work.
 * 
 * @author CWE
 *
 */
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class ElasticsearchBoundary {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchBoundary.class);

	private ObjectMapper jsonMapper;
	private HttpClient client;

	@Inject
	@GlobalProperty(value = "elasticsearch.url")
	String esAddress;

	@Inject
	@GlobalProperty(value = "freetext.active")
	String active;

	/**
	 * Connects to the ElasticSearch Server
	 * 
	 * @throws IOException
	 */
	public ElasticsearchBoundary() {
		jsonMapper = new ObjectMapper();
		jsonMapper.setDateFormat(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
		client = new HttpClient();
	}

	public void index(MDMEntityResponse document) {
		try {
			PutMethod put = new PutMethod(new StringBuilder().append(esAddress).append(getPath(document)).append("?ignore_conflicts=true").toString());

			byte[] json = jsonMapper.writeValueAsBytes(document);
			LOGGER.trace("Document {}: {}", getPath(document), new String(json));

			put.setRequestEntity(new ByteArrayRequestEntity(json, "application/json"));

			execute(put);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private String getPath(MDMEntityResponse document) {
		return new StringBuilder().append(StringUtils.lowerCase(document.source)).append("/").append(document.type).append("/").append(document.id)
				.toString();
	}

	private void execute(HttpMethod put) {
		try {
			int status = client.executeMethod(put);
			
			checkError(status, put);
		} catch (IOException e) {
			throw new IllegalStateException("Problems querying ElasticSearch.", e);
		}
	}

	private void checkError(int status, HttpMethod method) {
		String text = String.format("ElasticSearch answered %d. ", status);

		int httpCategory = status / 100;
		switch (httpCategory) {
		case 4:
			text = text + "This indicates a Client error: ";
			break;
		case 5:
			text = new StringBuilder().append(text).append("This indicates a Server error. The ES instance must be checked (").append(esAddress).append("): ").toString();
			break;
		}

		try {
			if (httpCategory != 2) {
				throw new IllegalStateException(text + method.getResponseBodyAsString());
			}
		} catch (IOException e) {
			throw new IllegalStateException(text + "\nError occured during reading the elastic search response!", e);
		}
	}

	public void delete(String api, String type, String id) {
		String path = new StringBuilder().append(StringUtils.lowerCase(api)).append("/").append(type).append("/").append(id).toString();
		DeleteMethod put = new DeleteMethod(esAddress + path);

		execute(put);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Document '{}' has been deleted!", path);
		}
	}

	public boolean hasIndex(String source) {
		boolean hasIndex = false;

		if (active()) {
			try {
				GetMethod get = new GetMethod(esAddress + StringUtils.lowerCase(source));
				int status = client.executeMethod(get);
				LOGGER.info("Checking index {}: {}", source, status);

				hasIndex = status / 100 == 2;
			} catch (IOException e) {
				LOGGER.warn("Querying ElasticSearch for the Index failed... Assuming no index is there!", e);
				hasIndex = false;
			}
		}

		return hasIndex;
	}

	public void createIndex(String source) {
		if (!Boolean.valueOf(active)) {
			return;
		}
		execute(new PutMethod(esAddress + StringUtils.lowerCase(source)));
		LOGGER.info("New Index created!");
	}

	public void validateConnectionIsPossible() {
		if(active()) {
			try {
				GetMethod get = new GetMethod(esAddress);
				int status = client.executeMethod(get);
				if(status /100 != 2) {
					LOGGER.error("Cannot connect to elasticsearch at {} but free text search is enabled! http status was: {}", esAddress, status);
					throw new RuntimeException("Cannot connect to elasticsearch.");
				} else {
					LOGGER.info("Successfully connected to elasticsearch!");
				}

			} catch (IOException e) {
				LOGGER.error("Cannot connect to elasticsearch at {} but free text search is enabled!", esAddress, e);
				throw new RuntimeException("Cannot connect to elasticsearch.", e);
			}
		}
	}

	public boolean active() {
		return Boolean.valueOf(active);
	}
}
