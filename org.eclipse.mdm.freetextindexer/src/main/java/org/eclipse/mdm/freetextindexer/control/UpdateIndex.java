package org.eclipse.mdm.freetextindexer.control;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;

@Stateless
public class UpdateIndex {

	@EJB
	ElasticsearchBoundary esBoundary;

	public void change(MDMEntityResponse mdmEntityResponse) {
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
