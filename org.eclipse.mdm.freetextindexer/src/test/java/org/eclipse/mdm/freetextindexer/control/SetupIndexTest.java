package org.eclipse.mdm.freetextindexer.control;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary;
import org.junit.Before;
import org.junit.Test;

public class SetupIndexTest {

	private SetupIndex setup;

	@Before
	public void init() {
		setup = new SetupIndex();
		setup.esBoundary = mock(ElasticsearchBoundary.class);
		setup.apiBoundary = mock(MdmApiBoundary.class);
	}

	@Test
	public void hasAlreadyIndex_doNothing() {
		when(setup.esBoundary.hasIndex(any(String.class))).thenReturn(true);

		setup.createIndexIfNeccessary();

		verify(setup.esBoundary, times(0)).createIndex(any(String.class));
	}

	@Test
	public void noIndex_created() {
		when(setup.esBoundary.hasIndex(any(String.class))).thenReturn(false);

		setup.createIndexIfNeccessary();

		verify(setup.esBoundary, times(1)).createIndex(any(String.class));
	}
}
