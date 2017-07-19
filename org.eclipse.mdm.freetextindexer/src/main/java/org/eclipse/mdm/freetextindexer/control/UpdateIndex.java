package org.eclipse.mdm.freetextindexer.control;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Singleton
public class UpdateIndex {
	private static final Logger LOGGER = LoggerFactory.getLogger(MdmApiBoundary.class);

	@EJB
	ElasticsearchBoundary esBoundary;

	public void change(MDMEntityResponse mdmEntityResponse) {
		LOGGER.info("Updating Index: " + mdmEntityResponse);

		if (mdmEntityResponse != null) {
			esBoundary.index(mdmEntityResponse);
		}
	}

	public void delete(String apiName, String name, String id) {
		esBoundary.delete(apiName, name, id);
	}
}
