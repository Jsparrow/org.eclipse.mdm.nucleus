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

package org.eclipse.mdm.businessobjects.boundary;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.base.model.Unit;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.api.dflt.model.TemplateTestStep;
import org.eclipse.mdm.api.dflt.model.TemplateTestStepUsage;

/**
 * Class defining constants used by the specific Jersey resource classes.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class ResourceConstants {
	/**
	 * Parameter name holding the {@link Environment}, i.e. the source name
	 */
	public static final String REQUESTPARAM_SOURCENAME = "SOURCENAME";

	/**
	 * Parameter holding the {@link Entity}s id in the URI path
	 */
	public static final String REQUESTPARAM_ID = "ID";

	/**
	 * Parameter holding an additional {@link Entity}s id in the URI path, e.g. for
	 * a {@link TemplateComponent} when {@link REQUESTPARAM_ID} holds the id of the
	 * {@link TemplateRoot}.
	 */
	public static final String REQUESTPARAM_ID2 = "ID2";

	/**
	 * Parameter holding an additional {@link Entity}s id in the URI path, e.g. for
	 * a {@link TemplateAttribute} when {@link REQUESTPARAM_ID} holds the id of the
	 * {@link TemplateRoot} and {@link REQUESTPARAM_ID2} holds the id of the
	 * {@link TemplateComponent}.
	 */
	public static final String REQUESTPARAM_ID3 = "ID3";

	/**
	 * Parameter holding an additional {@link Entity}s id in the URI path, e.g. for
	 * a {@link TemplateAttribute} when {@link REQUESTPARAM_ID} holds the id of the
	 * {@link TemplateRoot}, {@link REQUESTPARAM_ID2} holds the id of the parent
	 * {@link TemplateComponent} and {@link REQUESTPARAM_ID3} holds the id of the
	 * nested {@link TemplateComponent}.
	 */
	public static final String REQUESTPARAM_ID4 = "ID4";

	/**
	 * Parameter holding the {@link ContextType} of the {@link Entity} in the URI
	 * path
	 */
	public static final String REQUESTPARAM_CONTEXTTYPE = "CONTEXTTYPE";

	/**
	 * Parameter holding the name of the {@link Entity} in the request body
	 */
	public static final String ENTITYATTRIBUTE_NAME = "Name";

	/**
	 * Parameter holding the {@link ValueType} of the {@link Entity} in the request
	 * body
	 */
	public static final String ENTITYATTRIBUTE_DATATYPE = "DataType";

	/**
	 * Parameter holding the id of the {@link CatalogComponent} of e.g. the
	 * {@link TemplateComponent} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGCOMPONENT_ID = "CatalogComponent";

	/**
	 * Parameter holding the id of the {@link CatalogSensor} of e.g. the
	 * {@link TemplateSensor} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGSENSOR_ID = "CatalogSensor";

	/**
	 * Parameter holding the id of the {@link Quantity} of e.g. the {@link Unit} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_QUANTITY_ID = "Quantity";

	/**
	 * Parameter holding the id of the {@link Unit} of e.g. the {@link Quantity} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_UNIT_ID = "Unit";

	/**
	 * Parameter holding the id of the {@link Unit} of e.g. the {@link Quantity} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_PHYSICALDIMENSION_ID = "PhysicalDimension";

	/**
	 * Parameter holding the id of the {@link TemplateTestStep} of e.g. the
	 * {@link TemplateTestStepUsage} in the request body
	 */
	public static final String ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID = "TemplateTestStep";

	/**
	 * Just hide the default constructor
	 */
	private ResourceConstants() {
	}
}
