package org.eclipse.mdm.freetextindexer.entities;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.junit.Test;

public class MDMEntityResponseTest {

	@Test
	public void notContextDescribale_noContext() {
		Quantity q = mock(Quantity.class);
		EntityManager manager = mock(EntityManager.class);

		MDMEntityResponse response = MDMEntityResponse.build(Quantity.class, q, manager);

		assertTrue(response.data.attributes.isEmpty());
	}

	@Test
	public void buildFails_nullIsReturned() throws DataAccessException {
		TestStep ts = mock(TestStep.class);
		EntityManager manager = mock(EntityManager.class);
		when(manager.loadContexts(eq(ts), anyVararg())).thenThrow(new DataAccessException("test"));

		assertNull(MDMEntityResponse.build(TestStep.class, ts, manager));
	}
}