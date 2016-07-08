package org.eclipse.mdm.freetextindexer.boundary;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.ejb.Stateless;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
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
@Stateless
public class ElasticsearchBoundary {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchBoundary.class);

	private ObjectMapper jsonMapper;
	private HttpClient client;

	private String esAddress = "http://localhost:9301/";

	/**
	 * Connects to the ElasticSearch Server
	 * 
	 * @throws IOException
	 */
	public ElasticsearchBoundary() {
		jsonMapper = new ObjectMapper();
		jsonMapper.setDateFormat(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
		client = new HttpClient();

		LOGGER.debug("Connected to ElasticSearch");
	}

	public void index(MDMEntityResponse document) {
		try {
			PutMethod put = new PutMethod(esAddress + getPath(document) + "?ignore_conflicts=true");

			byte[] json = jsonMapper.writeValueAsBytes(document);
			put.setRequestEntity(new ByteArrayRequestEntity(json, "application/json"));

			execute(put);

			LOGGER.info("Document {}: {}", getPath(document), new String(json));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private String getPath(MDMEntityResponse document) {
		return document.source.toLowerCase() + "/" + document.type + "/" + document.id;
	}

	private void execute(HttpMethod put) {
		execute(put, null);
	}

	private void execute(HttpMethod put, Consumer<Void> onSuccess) {
		try {
			int status = client.executeMethod(put);
			checkError(status, onSuccess);
		} catch (IOException e) {
			throw new IllegalStateException("Problems querying ElasticSearch.", e);
		}
	}

	private void checkError(int status, Consumer<Void> onSuccess) {
		String text = String.format("ElasticSearch answered %d. ", status);

		int httpCategory = status / 100;
		switch (httpCategory) {
		case 4:
			text = text + "This indicates a Client error";
			break;
		case 5:
			text = text + "This indicates a Server error. The ES instance must be checked (" + esAddress + ")";
			break;
		}

		if (httpCategory == 2) {
			if (onSuccess != null) {
				onSuccess.accept(null);
			}
		} else {
			throw new IllegalStateException(text);
		}
	}

	public void delete(MDMEntityResponse document) {
		DeleteMethod put = new DeleteMethod(esAddress + getPath(document));
	
		execute(put);
	
		LOGGER.info("Document with Id " + getPath(document) + " has been deleted!");
	}

	public boolean hasIndex(String source) {
		try {
			GetMethod get = new GetMethod(esAddress + source.toLowerCase());
			int status = client.executeMethod(get);
			LOGGER.info("Checking index {}: {}", source, status);
			
			return status / 100 == 2;
		} catch (IOException e) {
			LOGGER.info("Querying ElasticSearch for the Index failed... Assuming no index is there!", e);
			return false;
		}
	}

	public void createIndex(String source) {
		execute(new PutMethod(esAddress + source.toLowerCase()));
	
		LOGGER.info("New Index created!");
	}
}
