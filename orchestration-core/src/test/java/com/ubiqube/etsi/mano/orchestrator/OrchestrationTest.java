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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Monitoring;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.service.ImplementationService;
import com.ubiqube.etsi.mano.orchestrator.service.SystemManager;
import com.ubiqube.etsi.mano.orchestrator.system.SysA;
import com.ubiqube.etsi.mano.orchestrator.system.SysB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitA;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitB;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3Impl;
import com.ubiqube.etsi.mano.orchestrator.vt.ProvAVt;
import com.ubiqube.etsi.mano.orchestrator.vt.ProvBVt;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

@ExtendWith(MockitoExtension.class)
class OrchestrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(OrchestrationTest.class);

	@Mock
	private SystemManager vimManager;
	private ImplementationService implementationService;

	private Planner getPlanner() {
		final List<SystemV3<?>> systems = Arrays.asList(new SysA(), new SysB());
		final ManoDexcutorService<?> service = new ManoDexcutorService<>();
		final ManoExecutor nullExec = new NullExecutor();
		return new PlannerImpl(implementationService, nullExec, List.of());
	}

	@Test
	void testplanLevel() throws Exception {
		final Planner p = getPlanner();
		final List<Class<? extends Node>> planConstituent = Arrays.asList(Network.class, Compute.class, Monitoring.class);
		final PreExecutionGraphV3<?> planOpaque = new PreExecutionGraphV3Impl<>(null);// p.makePlan(null, planConstituent, null);
		final ListenableGraph<VirtualTaskV3, VirtualTaskConnectivityV3> plan = ((PreExecutionGraphV3Impl) planOpaque).getGraph();
		assertEquals(1, plan.edgeSet().size());
		final VirtualTaskConnectivityV3 o = plan.edgeSet().iterator().next();
		assertNotNull(o.getSource());
		assertNotNull(o.getTarget());
		assertEquals(o.getSource().getClass(), ProvAVt.class);
		assertEquals(o.getTarget().getClass(), ProvBVt.class);
		//
		Mockito.lenient().when(vimManager.findVimByVimIdAndProviderId("PROVA", "")).thenReturn(TestFactory.createVimConnectionA());
		Mockito.lenient().when(vimManager.findVimByVimIdAndProviderId("PROVB", "")).thenReturn(TestFactory.createVimConnectionB());
		final ExecutionGraph rOpaq = p.implement(planOpaque);
		final ListenableGraph<UnitOfWorkV3<?>, ConnectivityEdge> r = ((ExecutionGraphImplV3) rOpaq).getGraph();
		assertEquals(1, r.edgeSet().size());
		final ConnectivityEdge e = r.edgeSet().iterator().next();
		assertNotNull(e.getSource());
		assertNotNull(e.getTarget());
		assertEquals(e.getSource().getClass(), UnitA.class);
		assertEquals(e.getTarget().getClass(), UnitB.class);
	}
}
