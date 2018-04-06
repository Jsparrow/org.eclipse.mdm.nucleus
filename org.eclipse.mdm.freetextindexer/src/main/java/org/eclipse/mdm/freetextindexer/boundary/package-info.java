/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 * This package contains all communication to external systems. Those systems
 * are:
 * <ul>
 * <li>ElasticSearch for Indexing via REST
 * {@link org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary
 * ElasticsearchBoundary}</li>
 * <li>MDM for querying the dataitems and getting notifications
 * {@link org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary
 * MDMBoundary}</li>
 * <li>JEE Container for creating the initial index
 * {@link org.eclipse.mdm.freetextindexer.boundary.StartupBoundary
 * StartupBoundary}</li>
 * </ul>
 * 
 */
package org.eclipse.mdm.freetextindexer.boundary;
