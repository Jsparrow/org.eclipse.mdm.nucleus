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
