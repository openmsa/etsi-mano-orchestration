/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.service.ImplementationService;
import com.ubiqube.etsi.mano.orchestrator.service.SystemManager;
import com.ubiqube.etsi.mano.orchestrator.system.SysA;
import com.ubiqube.etsi.mano.orchestrator.system.SysB;
import com.ubiqube.etsi.mano.orchestrator.uow.ANode;
import com.ubiqube.etsi.mano.orchestrator.uow.BNode;
import com.ubiqube.etsi.mano.orchestrator.uow.CNode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitC;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.v3.BlueprintBuilderImpl;
import com.ubiqube.etsi.mano.orchestrator.v3.OrchTestListener;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3Impl;
import com.ubiqube.etsi.mano.orchestrator.vt.ProvAVt;
import com.ubiqube.etsi.mano.orchestrator.vt.ProvBVt;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

@ExtendWith(MockitoExtension.class)
class OrchestrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(OrchestrationTest.class);

	@Mock
	private SystemManager vimManager;
	@Mock
	private ImplementationService implementationService;

	private Planner getPlanner() {
		final List<SystemV3<?>> systems = Arrays.asList(new SysA(), new SysB());
		final ManoDexcutorService<?> service = new ManoDexcutorService<>();
		final ManoExecutor nullExec = new NullExecutor();
		return new PlannerImpl(implementationService, nullExec, List.of());
	}

	@Test
	void testplanLevel() throws Exception {
		final BlueprintBuilderImpl bb = new BlueprintBuilderImpl();
		final ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>> network;
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
		final ExecutionGraph imp = new ExecutionGraphImplV3<>(g);
		final OrchExecutionListener listener = new OrchTestListener();
		final Planner p = getPlanner();
		p.execute(imp, listener);
	}

	@Test
	void testImplementation() {
		final Planner<TestParameters> p = getPlanner();
		final ListenableGraph<VirtualTaskV3<TestParameters>, VirtualTaskConnectivityV3<TestParameters>> g = new DefaultListenableGraph(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
		g.addGraphListener(new VirtualTaskVertexListenerV3<>());
		final ProvAVt a = new ProvAVt();
		final UnitA ua = new UnitA("test", ANode.class);
		g.addVertex(a);
		final ProvBVt b = new ProvBVt();
		final UnitB ub = new UnitB("test", BNode.class);
		g.addVertex(b);
		g.addEdge(a, b);
		final PreExecutionGraphV3 prePlan = new PreExecutionGraphV3Impl<>(g);
		final SystemBuilder<Object> sa = SystemBuilderV3Impl.of(ua);
		final SystemBuilder<Object> sb = SystemBuilderV3Impl.of(ub);
		Mockito.when(implementationService.getTargetSystem(a)).thenReturn(sa);
		Mockito.when(implementationService.getTargetSystem(b)).thenReturn(sb);
		final ExecutionGraph res = p.implement(prePlan);
		assertNotNull(res);
	}
}
