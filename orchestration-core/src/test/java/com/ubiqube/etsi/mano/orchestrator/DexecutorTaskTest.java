package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ubiqube.etsi.mano.orchestrator.uow.ANode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

@ExtendWith(MockitoExtension.class)
class DexecutorTaskTest {
	@Mock
	private OrchExecutionListener<Object> listener;
	@Mock
	private Context3dNetFlow<Object> context;

	@Test
	void test() {
		UnitOfWorkV3<Object> uaow = new UnitA("test", ANode.class);
		DexecutorTask<Object> dt = new DexecutorTask<>(listener, uaow, context, false);
		dt.execute();
		assertTrue(true);
	}

	@Test
	void testCreate() {
		UnitOfWorkV3<Object> uaow = new UnitA("test", ANode.class);
		DexecutorTask<Object> dt = new DexecutorTask<>(listener, uaow, context, true);
		dt.execute();
		assertTrue(true);
	}
}
