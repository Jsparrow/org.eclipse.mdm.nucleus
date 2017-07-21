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

package org.eclipse.mdm.connector.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.mdm.connector.boundary.ConnectorServiceException;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ServiceConfigurationReader to read MDM Service configurations from a
 * service.xml file
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 * @author Canoo Engineering AG (support for arbitrary entity manager factories
 *         and connection parameters)
 *
 */
@Stateless
public class ServiceConfigurationActivity {

	private static final String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.connector";
	private static final String SERVICE_XML_FILE_NAME = "service.xml";

	private static final String ROOT_ELEMENT_NAME = "services";
	private static final String SERVICE_ELEMENT_NAME = "service";
	private static final String PARAM_ELEMENT_NAME = "param";
	private static final String EMFACTORYCLASS_ATTRIBUTE_NAME = "entityManagerFactoryClass";
	private static final String NAME_ATTRIBUTE_NAME = "name";

	public List<ServiceConfiguration> readServiceConfigurations() {
		File serviceXML = getServiceFile();

		try (InputStream is = new BufferedInputStream(new FileInputStream(serviceXML))) {

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(is);

			Element root = doc.getDocumentElement();
			if (!ROOT_ELEMENT_NAME.equals(root.getNodeName())) {
				throw new ConnectorServiceException(
						"unable to find root element with name '" + ROOT_ELEMENT_NAME + "'");
			}

			NodeList serviceElements = root.getElementsByTagName(SERVICE_ELEMENT_NAME);
			List<ServiceConfiguration> parsedServiceElements = new ArrayList<>(serviceElements.getLength());
			for (int i = 0, n = serviceElements.getLength(); i < n; i++) {
				parsedServiceElements.add(parseServiceElement((Element) serviceElements.item(i)));
			}

			return parsedServiceElements;

		} catch (ConnectorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ConnectorServiceException(e.toString(), e);
		}
	}

	private static ServiceConfiguration parseServiceElement(Element serviceElement) {
		String entityManagerFactoryClass = readElementAttribute(serviceElement, EMFACTORYCLASS_ATTRIBUTE_NAME);
		NodeList paramElements = serviceElement.getElementsByTagName(PARAM_ELEMENT_NAME);
		Map<String, String> connectionParameters = new LinkedHashMap<>(paramElements.getLength());
		for (int i = 0, n = paramElements.getLength(); i < n; i++) {
			Element paramElement = (Element) paramElements.item(i);
			String paramName = readElementAttribute(paramElement, NAME_ATTRIBUTE_NAME);
			String paramValue = paramElement.getTextContent();
			if (paramValue != null && !(paramValue = paramValue.trim()).isEmpty()) {
				connectionParameters.put(paramName, paramValue);
			}
		}
		return new ServiceConfiguration(entityManagerFactoryClass, connectionParameters);
	}

	private static String readElementAttribute(Element element, String attrName) {
		String value = element.getAttribute(attrName);
		if (value.trim().isEmpty()) {
			throw new ConnectorServiceException(
					"mandatory attribute '" + attrName + "' of element '" + element.getNodeName() + "' is missing!");
		}
		return value;
	}

	private static File getServiceFile() {
		File file = new File(COMPONENT_CONFIG_ROOT_FOLDER);
		if (!file.exists() || !file.isDirectory()) {
			throw new ConnectorServiceException(
					"mandatory configuration folder '" + file.getAbsolutePath() + "' does not exist!");
		}
		File serviceXML = new File(file, SERVICE_XML_FILE_NAME);
		if (!file.exists()) {
			throw new ConnectorServiceException(
					"mandatory service configuration file at '" + serviceXML.getAbsolutePath() + "' does not exist!");
		}
		return serviceXML;
	}

}
