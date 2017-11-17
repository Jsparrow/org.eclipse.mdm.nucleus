package org.eclipse.mdm.businessobjects.boundary;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.base.model.Unit;
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.api.dflt.model.TemplateTest;
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
	 * Parameter holding the {@link ContextType} of the {@link Entity} in the URI
	 * path
	 */
	public static final String REQUESTPARAM_CONTEXTTYPE = "CONTEXTTYPE";

	/**
	 * Parameter holding the name of the {@link Entity} in the request body
	 */
	public static final String ENTITYATTRIBUTE_NAME = "name";

	/**
	 * Parameter holding the {@link ValueType} of the {@link Entity} in the request
	 * body
	 */
	public static final String ENTITYATTRIBUTE_DATATYPE = "datatype";

	/**
	 * Parameter holding the id of the {@link CatalogComponent} of e.g. the
	 * {@link TemplateComponent} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGCOMPONENT_ID = "catalogcomponent";

	/**
	 * Parameter holding the id of the {@link CatalogSensor} of e.g. the
	 * {@link TemplateSensor} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGSENSOR_ID = "catalogsensor";

	/**
	 * Parameter holding the id of the {@link CatalogAttribute} of e.g. the
	 * {@link TemplateAttribute} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGATTRIBUTE_ID = "catalogattribute";
	
	/**
	 * Parameter holding the id of the {@link Quantity} of e.g. the {@link Unit} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_QUANTITY_ID = "quantity";

	/**
	 * Parameter holding the id of the {@link Unit} of e.g. the {@link Quantity} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_UNIT_ID = "unit";
	
	/**
	 * Parameter holding the id of the {@link Unit} of e.g. the
	 * {@link Quantity} in the request body
	 */
	public static final String ENTITYATTRIBUTE_PHYSICALDIMENSION_ID = "physicaldimension";
	
	/**
	 * Parameter holding the id of the {@link TemplateTest} of e.g. the
	 * {@link TemplateTestStepUsage} in the request body
	 */
	public static final String ENTITYATTRIBUTE_TEMPLATETEST_ID = "templatetest";

	/**
	 * Parameter holding the id of the {@link TemplateTestStep} of e.g. the
	 * {@link TemplateTestStepUsage} in the request body
	 */
	public static final String ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID = "templateteststep";

	/**
	 * Parameter holding the id of the {@link TemplateRoot} of
	 * {@link ContextType.UNITUNDERTEST} of e.g. the {@link TemplateTestStep} in the
	 * request body
	 */
	public static final String ENTITYATTRIBUTE_TPLROOTUNITUNDERTEST_ID = "templaterootunitundertest";

	/**
	 * Parameter holding the id of the {@link TemplateRoot} of
	 * {@link ContextType.TESTSEQUENCE} of e.g. the {@link TemplateTestStep} in the
	 * request body
	 */
	public static final String ENTITYATTRIBUTE_TPLROOTTESTSEQUENCE_ID = "templateroottestsequence";

	/**
	 * Parameter holding the id of the {@link TemplateRoot} of
	 * {@link ContextType.TESTEQUIPMMENT} of e.g. the {@link TemplateTestStep} in
	 * the request body
	 */
	public static final String ENTITYATTRIBUTE_TPLROOTTESTEQUIPMENT_ID = "templateroottestequipment";

	/**
	 * Parameter holding the id of the {@link ValueList} of e.g. the
	 * {@link CatalogAttribute} in the request body
	 */
	public static final String ENTITYATTRIBUTE_VALUELISTID = "valuelist";

	/**
	 * Just hide the default constructor
	 */
	private ResourceConstants() {
	}
}
