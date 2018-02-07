package org.eclipse.mdm.freetextindexer.control;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.eclipse.mdm.api.dflt.ApplicationContext;
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
		for (Map.Entry<String, ApplicationContext> entry : apiBoundary.getContexts().entrySet()) {
			String source = entry.getKey();
			
			if (!esBoundary.hasIndex(source)) {
				esBoundary.createIndex(source);
				
				apiBoundary.doForAllEntities(entry.getValue(), e -> esBoundary.index(e));
			}
		}
	}
}
