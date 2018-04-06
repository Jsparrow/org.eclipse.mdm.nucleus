/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
