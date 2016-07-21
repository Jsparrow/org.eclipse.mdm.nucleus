package org.eclipse.mdm.freetextindexer.control;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
@ApplicationScoped
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

	public void delete(MDMEntityResponse mdmEntityResponse) {
		if (mdmEntityResponse != null) {
			esBoundary.delete(mdmEntityResponse);
		}
	}
}
