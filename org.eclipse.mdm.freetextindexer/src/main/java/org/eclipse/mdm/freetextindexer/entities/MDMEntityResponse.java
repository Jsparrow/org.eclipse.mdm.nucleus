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


package org.eclipse.mdm.freetextindexer.entities;

import org.eclipse.mdm.api.base.model.*;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * EntryResponse (Container for {@link MDMEntity}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MDMEntityResponse {

	public String source;

	public String type;

	/** transferable data content */
	public MDMEntity data;

	public String id;

	/**
	 * Constructor (for a list of business objects {@link MDMEntity}s)
	 * 
	 * @param type
	 *            type of all containging {@link MDMEntity}s
	 * @param entries
	 *            list of {@link MDMEntity}
	 * @throws DataAccessException
	 */
	public static <T extends Entity> MDMEntityResponse build(Class<? extends Entity> type, T businessObject,
			EntityManager manager) {
		MDMEntityResponse response = new MDMEntityResponse();

		try {
			response.type = type.getSimpleName();
			response.source = businessObject.getSourceName();
			response.data = toTransferable(businessObject);
			response.id = businessObject.getID();

			response.addContext(businessObject, manager);
		} catch (DataAccessException e) {
			response = null;
		}

		return response;
	}

	private <T extends Entity> void addContext(T businessObject, EntityManager manager) throws DataAccessException {
		if (businessObject instanceof ContextDescribable) {
			Map<ContextType, ContextRoot> contexts = manager.loadContexts((ContextDescribable) businessObject,
					ContextType.UNITUNDERTEST, ContextType.TESTSEQUENCE, ContextType.TESTEQUIPMENT);

			for (ContextRoot root : contexts.values()) {
				MDMEntity entity = toTransferable(root);
				data.components.add(entity);
				for (ContextComponent comp : root.getContextComponents()) {
					MDMEntity compEntity = toTransferable(comp);
					entity.components.add(compEntity);
					for (Entry<String, Value> entry : comp.getValues().entrySet()) {
						Optional<Object> extractedValue = Optional.ofNullable(entry.getValue().extract());
						compEntity.attributes.put(entry.getKey(), extractedValue.map(Object::toString).orElse(""));
					}
				}
			}
		}
	}

	private static <T extends Entity> MDMEntity toTransferable(T businessObject) {
		return new MDMEntity(businessObject.getName(), businessObject.getClass().getSimpleName(),
				businessObject.getID(), businessObject.getValues());
	}
}
