package org.eclipse.mdm.freetextindexer.control;

import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.Stateless;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;

@Startup
@Stateless
public class SetupIndex {

	@EJB
	ElasticsearchBoundary esBoundary;

	@EJB
	MdmApiBoundary apiBoundary;
	
	public SetupIndex()
	{
		System.out.println("Setup called");
	}

	public void createIndexIfNeccessary() {
		String source = apiBoundary.getApiName();
		
		if (!esBoundary.hasIndex(source)) {
			esBoundary.createIndex(source);
			apiBoundary.doForAllEntities(e -> esBoundary.index(e));
		}
	}

}
