package com.ubiqube.etsi.mano.orchestrator;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

@ExtendWith(MockitoExtension.class)
class ManoDexcutorServiceTest {
	@Mock
	private UnitOfWorkV3<Object> uaow;
	@Mock
	private OrchExecutionListener<Object> listener;
	@Mock
	private Context3dNetFlow<Object> context;

	@Test
	void test() {
		final ListenableGraph<UnitOfWorkV3<Object>, ConnectivityEdge<UnitOfWorkV3<Object>>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		ManoDexcutorService srv = new ManoDexcutorService();
		TaskProvider<Object, String> tp = id -> new TestTask();
		srv.execute(g, tp);
	}

	class TestTask extends Task<Object, String> {
		@Override
		public String execute() {
			return null;
		}

	}
}
