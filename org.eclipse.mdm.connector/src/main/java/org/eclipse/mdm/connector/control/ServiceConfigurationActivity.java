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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mdm.connector.boundary.ConnectorServiceException;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ServiceConfigurationReader to read MDM Service configurations from a service.xml file
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class ServiceConfigurationActivity {
	
	private static final String ROOT_ELEMENT_NAME = "services";
	private static final String SERVICE_ELEMENT = "service";
	private static final String SERVICE_ATTR_NS_HOST = "nameServiceHost";
	private static final String SERVICE_ATTR_NS_PORT = "nameServicePort";
	private static final String SERVICE_ATTR_NS_NAME = "nameServiceName";
	private static final String SERVICE_ATTR_SERVICENAME = "serviceName";
	
	private final static String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.connector";
	private final static String SERVICE_XML_FILE_NAME = "service.xml";		
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigurationActivity.class); 
	
	public List<ServiceConfiguration> readServiceConfigurations() {
		
		InputStream is = null; 
		
		try {
			List<ServiceConfiguration> list = new ArrayList<>();
			
			File serviceXML = getServiceFile();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        is = new BufferedInputStream(new FileInputStream(serviceXML));
	        Document doc = db.parse(is);
	       
	        Element root = doc.getDocumentElement();
	        if(!root.getNodeName().equalsIgnoreCase(ROOT_ELEMENT_NAME)) {
	        	String message = "unable to find root element with name '" + ROOT_ELEMENT_NAME + "'";
	        	throw new ConnectorServiceException(message);
	        }
	                
	        Element[] services = getChildElementsByName(root, SERVICE_ELEMENT, true);
	        for(Element service : services) {
	        	list.add(readServiceConfiguration(service));
	        }   
	        
	        return list;	        
		} catch(ParserConfigurationException | SAXException | IOException e) {
			throw new ConnectorServiceException(e.getMessage(), e);
		} finally {
			closeInputStream(is);
		}
	}


	private ServiceConfiguration readServiceConfiguration(Element service) {
		String nameServiceHost = readElementAttribute(SERVICE_ATTR_NS_HOST, "", true, service);
		String nameServicePort = readElementAttribute(SERVICE_ATTR_NS_PORT, "", true, service);
		String nameServiceName = readElementAttribute(SERVICE_ATTR_NS_NAME, "NameService", false, service);
		String serviceName = readElementAttribute(SERVICE_ATTR_SERVICENAME, "", true, service);
		String nameService = "corbaloc::1.2@" + nameServiceHost + ":" + nameServicePort + "/" + nameServiceName;
		return new ServiceConfiguration(nameService, serviceName);
	}
	
	
	private Element[] getChildElementsByName(Element element, String name, boolean mandatory) {
		
		List<Element> elements = new ArrayList<Element>();
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase(name)) {
				elements.add((Element) node);
			}					
		}
		
		if(mandatory && elements.size() <= 0) {
			String errorMessage = "mandatory element '" + name + "' not found!";
			throw new ConnectorServiceException(errorMessage);
		}
		
		return elements.toArray(new Element[elements.size()]);
	}	
	
	
	private String readElementAttribute(String attrName, String defaultValue, boolean mandatory, Element element) {	
		String value = element.getAttribute(attrName);
		if(value.trim().length() <= 0) {
			if(mandatory) {
				String elementName = element.getNodeName();
				String errorMessage = "mandatory attribute '" + attrName + "' at element '" + elementName + "' is missing!";				
				throw new ConnectorServiceException(errorMessage);
			}			
			value = defaultValue;
		}
		return value;
	}
	
	
	private File getServiceFile() {
		File file = new File(COMPONENT_CONFIG_ROOT_FOLDER);
		if(!file.exists() || !file.isDirectory()) {
			String errorMessage = "mandatory configuration folder '" + file.getAbsolutePath() + "' does not exist!";
			throw new ConnectorServiceException(errorMessage);
		}
		File serviceXML = new File(file, SERVICE_XML_FILE_NAME);
		if(!file.exists()) {
			String errorMessage = "mandatory service configuration file at '" + serviceXML.getAbsolutePath() 
			+ "' does not exist!";
			throw new ConnectorServiceException(errorMessage);
		}
		return serviceXML;
	}
	
	
	
	private void closeInputStream(InputStream is) {
		try {
			if(is != null) {
				is.close();
			}
		} catch(IOException e) {
			LOG.error(e.getMessage());
		}
	}
}
