package org.eclipse.mdm.freetextindexer.control;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;

@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
@Startup
@Singleton
public class SetupIndex {

	@EJB
	ElasticsearchBoundary esBoundary;

	@EJB
	MdmApiBoundary apiBoundary;

	@PostConstruct
	public void createIndexIfNeccessary() {
		String source = apiBoundary.getApiName();

		if (!esBoundary.hasIndex(source)) {
			esBoundary.createIndex(source);
			apiBoundary.doForAllEntities(e -> esBoundary.index(e));
		}
	}
}
