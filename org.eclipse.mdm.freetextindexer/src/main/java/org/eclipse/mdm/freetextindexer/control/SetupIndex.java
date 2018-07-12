/*
 * Copyright (c) 2017-2018 Peak Solution GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.mdm.freetextindexer.control;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.eclipse.mdm.freetextindexer.events.CreateIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Singleton
public class SetupIndex {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetupIndex.class);

	@EJB
	ElasticsearchBoundary esBoundary;

	@EJB
	MdmApiBoundary apiBoundary;

	@Inject
	Event<CreateIndex> createIndexEvent;

	@PostConstruct
	public void createIndexIfNeccessary() {
		for (Map.Entry<String, ApplicationContext> entry : apiBoundary.getContexts().entrySet()) {
			String source = entry.getKey();

			if (!esBoundary.hasIndex(source)) {
				LOGGER.info("About to create new lucene index!");
				createIndexEvent.fire(new CreateIndex(source, Test.class, TestStep.class, Measurement.class));
			}
		}
	}

	@Asynchronous
	public void handleCreateIndexEvent(@Observes CreateIndex event) {
		String source = event.getSourceName();
		esBoundary.createIndex(source);
		for(Class<? extends Entity> entityType : event.getEntitiesToIndex()) {
			apiBoundary.doForAllEntities(entityType, apiBoundary.getContexts().get(source), e -> esBoundary.index(e));
		}
		LOGGER.info("Index '" + source + "' initialized");
	}
}
