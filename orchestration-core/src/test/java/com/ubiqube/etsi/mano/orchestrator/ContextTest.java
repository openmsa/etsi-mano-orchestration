/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ANode;
import com.ubiqube.etsi.mano.orchestrator.uow.BNode;
import com.ubiqube.etsi.mano.orchestrator.uow.CNode;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitC;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;

class ContextTest {

	@Test
	void testName() throws Exception {
		final List<ContextUow<?>> global = new ArrayList<>();
		global.add(new ContextUow<>(new ContextVt<>(BNode.class, "global", "grid")));
		final ListenableGraph<UnitOfWorkV3<Object>, ConnectivityEdge<UnitOfWorkV3<Object>>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		g.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		final UnitA ua = new UnitA("test", ANode.class);
		g.addVertex(ua);
		final UnitB ub = new UnitB("test", BNode.class);
		g.addVertex(ub);
		final UnitC uc = new UnitC("test", CNode.class);
		g.addVertex(uc);
		g.addEdge(ua, ub);
		g.addEdge(ub, uc);
		final Context3dNetFlow<Object> ctx = new Context3dNetFlow(g, global);
		final Object res = ctx.get(uc, BNode.class, "test");
		assertNotNull(res);
		final String ro = ctx.getOptional(uc, BNode.class, "notHere");
		assertNull(ro);
		ctx.add(ub, BNode.class, "notHere", "rid");
		final String end = ctx.get(uc, BNode.class, "notHere");
		assertNotNull(end);
		final List<String> an = ctx.getParent(uc, ANode.class);
		assertEquals(1, an.size());
	}
}
