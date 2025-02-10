package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.uow.ANode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;

class ExecutionGraphImplV3Test {

	@Test
	void test() {
		final ListenableGraph<UnitOfWorkV3<Object>, ConnectivityEdge<UnitOfWorkV3<Object>>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		g.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		final UnitA ua = new UnitA("test", ANode.class);
		g.addVertex(ua);
		ExecutionGraphImplV3<Object> srv = new ExecutionGraphImplV3<>(g);
		srv.add(Network.class, "test", "test");
		assertDoesNotThrow(() -> srv.dump());
	}

}
