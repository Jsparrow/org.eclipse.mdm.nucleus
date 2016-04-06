/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
 
hints:
- copy all web content file to the document root folder at your web server
- check the following variable definitions at *.service.ts files
	- _host -> configure your rest api host 
	- _port -> configure your rert api port
	- _nodeURL -> configure your rest prefix (default: org.eclipse.mdm.application-1.0.0) 