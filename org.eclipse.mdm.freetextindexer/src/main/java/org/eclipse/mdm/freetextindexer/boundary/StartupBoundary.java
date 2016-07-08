package org.eclipse.mdm.freetextindexer.boundary;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.eclipse.mdm.freetextindexer.control.SetupIndex;

@Startup
@Singleton
public class StartupBoundary {

	@Inject
	private SetupIndex setupIndex;

	public StartupBoundary() {
		this.setupIndex = null;
		System.out.println("Startup called!");
	}

	@PostConstruct
	public void setup() {
		setupIndex.createIndexIfNeccessary();
	}
	
}
