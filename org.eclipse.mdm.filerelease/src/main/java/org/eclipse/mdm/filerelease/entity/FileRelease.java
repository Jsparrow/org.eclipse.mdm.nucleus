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

package org.eclipse.mdm.filerelease.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * FileRelease entity
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
public class FileRelease implements Serializable {
	
	private static final long serialVersionUID = -9016111258701009299L;
	
	public String identifier = "";
	public String state = "";
	
	public String name = "";
	public String sourceName = "";
	public String typeName = "";
	public long id = 0L;
		
	public String sender = "";
	public String receiver = "";
	
	
	public String orderMessage = "";
	public String rejectMessage = "";
	public String errorMessage = "";
		
	public String format = "";
	public String fileLink = "";

	public int validity = 0;
	public long expire = 0;	
}
