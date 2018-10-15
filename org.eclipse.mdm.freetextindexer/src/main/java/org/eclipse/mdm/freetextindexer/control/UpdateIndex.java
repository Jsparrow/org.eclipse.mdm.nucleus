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

package org.eclipse.mdm.freetextindexer.control;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;

@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Singleton
public class UpdateIndex {

	@EJB
	ElasticsearchBoundary esBoundary;

	public void change(MDMEntityResponse mdmEntityResponse) {
		if (mdmEntityResponse != null) {
			esBoundary.index(mdmEntityResponse);
		}
	}

	public void delete(String apiName, String name, String id) {
		esBoundary.delete(apiName, name, id);
	}
}
