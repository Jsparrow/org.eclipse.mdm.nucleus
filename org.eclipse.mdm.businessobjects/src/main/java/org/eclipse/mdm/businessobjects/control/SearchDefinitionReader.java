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

package org.eclipse.mdm.businessobjects.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mdm.businessobjects.entity.SearchDefinition;
import org.eclipse.mdm.businessobjects.entity.XMLFormatException;
import org.eclipse.mdm.businessobjects.entity.XMLParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * SearchDefinitionReader class to read {@link SearchDefinition}s from an XML files
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchDefinitionReader {
	
	private static final String ROOT_ELEMENT_NAME = "mdm_search_definition";
	private static final String ROOT_ELEMENT_ATTR_NAME = "name";
	private static final String ROOT_ELEMENT_ATTR_RESULT = "resultType";
	private static final String ATTRIBUTE_GROUP_ELEMENT = "attributes";
	private static final String ATTRIBUTE_ELEMENT = "attribute";
	private static final String ATTRIBUTE_ELEMENT_ATTR_TYPE = "boType";
	private static final String ATTRIBUTE_ELEMENT_ATTR_VALUENAME = "attrName";
	private static final String ATTRIBUTE_ELEMENT_ATTR_VALUETYPE = "valueType";
	private static final String ATTRIBUTE_ELEMENT_ATTR_CRITERIA = "criteria";
	
	private final static String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.search";
	
	private final Principal principal;
		
	private static final Logger LOG = LoggerFactory.getLogger(SearchDefinitionReader.class); 
	
	
	
	/**
	 * Constructor	
	 * @param principal the current principal to load user specific search definition if available 
	 */
	public SearchDefinitionReader(Principal principal) {
		this.principal = principal;
	}
	
	
	
	/**
	 * reads the {@link SearchDefinition}s from a available {@link SearchDefinition} XML files
	 * @return the read {@link SearchDefinition}s
	 * 
	 * @throws SearchException if an error occurs during reading the {@link SearchException}s from the XML files
	 */
	public List<SearchDefinition> readSearchDefinitions() {	
		
		List<SearchDefinition> searchDefinitionList = new ArrayList<>();		
		List<File> files = listSearchDefinitionFiles();
		
		for(File file : files) {
			searchDefinitionList.add(readSearchDefinitionFile(file));
		}
		
		return searchDefinitionList;	
	}	
	
	
	
	private SearchDefinition readSearchDefinitionFile(File file) {
		
		InputStream is = null;
		
		try {
		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	
	        is = new BufferedInputStream(new FileInputStream(file));
	        Document doc = db.parse(is);
	        Element root = doc.getDocumentElement();
	        if(!root.getNodeName().equalsIgnoreCase(ROOT_ELEMENT_NAME)) {
	        	String message = "unable to find root element with name '" + ROOT_ELEMENT_NAME + "'";
	        	throw new XMLFormatException(message);
	        }
	        
	        String name = readElementAttribute(ROOT_ELEMENT_ATTR_NAME, "", true, root);
	        String resultType = readElementAttribute(ROOT_ELEMENT_ATTR_RESULT, "", true, root);
	        
	        SearchDefinition searchDefinition = new SearchDefinition(name, resultType);
	        
	        Element[] attributeGroups = getChildElementsByName(root, ATTRIBUTE_GROUP_ELEMENT, true);
	        for(Element attributeGroup : attributeGroups) {
	        	Element[] attributes = getChildElementsByName(attributeGroup, ATTRIBUTE_ELEMENT, true);
	        	for(Element attribute : attributes) {
	        		String boType = readElementAttribute(ATTRIBUTE_ELEMENT_ATTR_TYPE, "", true, attribute);
	        		String attrName = readElementAttribute(ATTRIBUTE_ELEMENT_ATTR_VALUENAME, "", true, attribute);
	        		String valueType = readElementAttribute(ATTRIBUTE_ELEMENT_ATTR_VALUETYPE, "", true, attribute);        		
	        		String criteria = readElementAttribute(ATTRIBUTE_ELEMENT_ATTR_CRITERIA, "*", false, attribute);
	        		searchDefinition.addSearchAttribute(boType, attrName, valueType, criteria);
	        	}
	        }	        
	        return searchDefinition;
	        
		} catch(ParserConfigurationException | SAXException | IOException e) {
        	throw new XMLParseException(e.getMessage(), e);
        } finally {
        	closeInputStream(is);
        }
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
			String message = "mandatory element '" + name + "' not found!";
			throw new XMLFormatException(message);
		}
		
		return elements.toArray(new Element[elements.size()]);
	}	
	
	
	
	private String readElementAttribute(String attrName, String defaultValue, boolean mandatory, Element element) {
			
		String value = element.getAttribute(attrName);
		if(value.trim().length() <= 0) {
			if(mandatory) {
				String elementName = element.getNodeName();
				String message = "mandatory attribute '" + attrName + "' at element '" + elementName + "' is missing!";
				throw new XMLFormatException(message);
			}			
			value = defaultValue;
		}
		return value;
	}
	
	
	
	private List<File> listSearchDefinitionFiles() {
		
		List<File> files = new ArrayList<>();
		
		File globalFolder = new File(COMPONENT_CONFIG_ROOT_FOLDER);		
		if(!globalFolder.exists() || !globalFolder.isDirectory()) {
			return Collections.emptyList();
		}		
		files.addAll(listSearchDefinitionFiles(globalFolder));
		
		File userFolder = new File(globalFolder, this.principal.getName());
		if(userFolder.exists() && userFolder.isDirectory()) {
			files.addAll(listSearchDefinitionFiles(userFolder));
		}	
		
		return files;
	}
	
	
	
	private List<File> listSearchDefinitionFiles(File rootFolder) {
		List<File> list = new ArrayList<>();
		File[] files = rootFolder.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				continue;
			}
			if(file.getName().toLowerCase().endsWith(".xml")) {
				list.add(file);
			}
		}
		return list;
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
