/**
 *     Copyright (C) 2019-2024 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.service.graph;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;

@SuppressWarnings("static-method")
class GraphGeneratorTest {

	@Test
	void testDrawGraph() throws Exception {
		final ListenableGraph<TaskVertex, ConnectivityEdge<TaskVertex>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		final TaskVertex v = new TaskVertex();
		g.addVertex(v);
		final TaskVertex v2 = new TaskVertex();
		v2.setStatus(VertexStatusType.SUCCESS);
		g.addVertex(v2);
		GraphGenerator.drawGraph(g);
		assertTrue(true);
	}

	@Test
	void testName() throws Exception {
		final ListenableGraph<TaskVertex, ConnectivityEdge<TaskVertex>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		final TaskVertex v = new TaskVertex();
		g.addVertex(v);
		GraphGenerator.drawGraph2(g);
		assertTrue(true);
	}
}
