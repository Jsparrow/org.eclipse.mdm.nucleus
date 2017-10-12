package org.eclipse.mdm.businessobjects.boundary;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;

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
	 * Parameter holding the id of the {@link CatalogAttribute} of e.g. the
	 * {@link TemplateAttribute} in the request body
	 */
	public static final String ENTITYATTRIBUTE_CATALOGATTRIBUTE_ID = "catalogattribute";

	/**
	 * Just hide the default constructor
	 */
	private ResourceConstants() {
	}
}
