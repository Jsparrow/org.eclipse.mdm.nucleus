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
			PutMethod put = new PutMethod(esAddress + getPath(document) + "?ignore_conflicts=true");

			byte[] json = jsonMapper.writeValueAsBytes(document);
			put.setRequestEntity(new ByteArrayRequestEntity(json, "application/json"));

			execute(put);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Document {} indexed: {}", getPath(document), new String(json));
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private String getPath(MDMEntityResponse document) {
		return document.source.toLowerCase() + "/" + document.type + "/" + document.id;
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
			text = text + "This indicates a Server error. The ES instance must be checked (" + esAddress + "): ";
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
		String path = api.toLowerCase() + "/" + type + "/" + id;
		DeleteMethod put = new DeleteMethod(esAddress + path);

		execute(put);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Document '{}' has been deleted!", path);
		}
	}

	public boolean hasIndex(String source) {
		boolean hasIndex = false;

		if (Boolean.valueOf(active)) {
			try {
				GetMethod get = new GetMethod(esAddress + source.toLowerCase());
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
		if (Boolean.valueOf(active)) {
			execute(new PutMethod(esAddress + source.toLowerCase()));
			LOGGER.info("New Index created!");
		}
	}
}
