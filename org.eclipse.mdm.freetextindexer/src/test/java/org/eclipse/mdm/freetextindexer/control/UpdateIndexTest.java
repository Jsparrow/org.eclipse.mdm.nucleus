package org.eclipse.mdm.freetextindexer.control;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.mdm.freetextindexer.boundary.ElasticsearchBoundary;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.junit.Before;
import org.junit.Test;

public class UpdateIndexTest {

	private UpdateIndex update;

	@Before
	public void init() {
		update = new UpdateIndex();
		update.esBoundary = mock(ElasticsearchBoundary.class);
	}

	@Test
	public void nullGiven_notUpdated() {
		update.change(null);

		verify(update.esBoundary, never()).index(any(MDMEntityResponse.class));
	}

	@Test
	public void validDoc_Indexed() {
		MDMEntityResponse response = mock(MDMEntityResponse.class);
		update.change(response);

		verify(update.esBoundary, times(1)).index(eq(response));
	}

	@Test
	public void nullGiven_notDeleted() {
		update.delete(null);

		verify(update.esBoundary, never()).delete(any(MDMEntityResponse.class));
	}

	@Test
	public void validDoc_deleted() {
		MDMEntityResponse response = mock(MDMEntityResponse.class);
		update.delete(response);

		verify(update.esBoundary, times(1)).delete(eq(response));
	}
}