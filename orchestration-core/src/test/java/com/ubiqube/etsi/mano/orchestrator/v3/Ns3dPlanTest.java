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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator.v3;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.controllers.VertexResult;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.ManoDexcutorService;
import com.ubiqube.etsi.mano.orchestrator.ManoExecutor;
import com.ubiqube.etsi.mano.orchestrator.NullExecutor;
import com.ubiqube.etsi.mano.orchestrator.Planner;
import com.ubiqube.etsi.mano.orchestrator.PlannerImpl;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.mec.VnfExtractorNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.PortPairNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnfCreateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnfInstantiateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgPostNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMerger;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMultiplier;
import com.ubiqube.etsi.mano.orchestrator.scale.ScalingEngine;
import com.ubiqube.etsi.mano.orchestrator.service.ImplementationService;
import com.ubiqube.etsi.mano.orchestrator.service.SystemManager;
import com.ubiqube.etsi.mano.orchestrator.system.SysA;
import com.ubiqube.etsi.mano.orchestrator.system.SysB;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.graph.GraphListener2d;
import com.ubiqube.etsi.mano.service.sys.SystemV3;

@ExtendWith(MockitoExtension.class)
class Ns3dPlanTest {

	private final ListenableGraph<Vertex2d, Edge2d> g;
	@Mock
	private SystemManager vimManager;
	private ImplementationService implementationService;

	private Planner getPlanner() {
		final List<SystemV3<?>> systems = Arrays.asList(new SysA(), new SysB());
		final ManoDexcutorService<?> service = new ManoDexcutorService<>();
		final ManoExecutor nullExec = new NullExecutor();
		return new PlannerImpl(implementationService, nullExec, List.of());
	}

	public Ns3dPlanTest() throws StreamReadException, DatabindException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final VertexResult r;
		try (InputStream is = this.getClass().getResourceAsStream("/ns-vnffg.json")) {
			r = mapper.readValue(is, VertexResult.class);
		}
		g = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Edge2d.class));
		g.addGraphListener(new GraphListener2d());
		r.getVertices().forEach(x -> g.addVertex(x));
		r.getEdges().forEach(x -> {
			final Vertex2d src = find(g.vertexSet(), x.getSource());
			final Vertex2d tgt = find(g.vertexSet(), x.getTarget());
			Optional.ofNullable(g.addEdge(src, tgt)).ifPresent(y -> y.setRelation(x.getRelation()));
		});
	}

	private static Vertex2d find(final Set<Vertex2d> vertexSet, final String source) {
		final List<Vertex2d> l = vertexSet.stream().filter(x -> x.toString().equals(source)).toList();
		if (l.size() != 1) {
			throw new OrchestrationException("source have " + l);
		}
		return l.get(0);
	}

	@Test
	void testNs() throws Exception {
		final Function<Object, VirtualTaskV3<Object>> func = p -> new TestVirtualTask(VnffgLoadbalancerNode.class, "name", "alias", 0);
		final ScalingEngine se = new ScalingEngine();
		final List<SclableResources<Object>> scales = List.of(
				SclableResources.of(VnffgLoadbalancerNode.class, "nfp_position_1", 0, 1, null),
				SclableResources.of(PortPairNode.class, "element_1", 0, 1, null),
				SclableResources.of(PortPairNode.class, "element_2", 0, 1, null),
				SclableResources.of(PortPairNode.class, "element_3", 0, 1, null),
				SclableResources.of(Network.class, "left_vl", 0, 1, null),
				SclableResources.of(VnffgPostNode.class, "vnffg_1", 0, 1, null),
				SclableResources.of(VnfCreateNode.class, "vnf_left", 0, 1, null),
				SclableResources.of(VnfInstantiateNode.class, "vnf_left", 0, 1, null),
				SclableResources.of(VnfExtractorNode.class, "vnf_left", 0, 1, null),
				SclableResources.of(VnfCreateNode.class, "vnf_middle", 0, 1, null),
				SclableResources.of(VnfInstantiateNode.class, "vnf_middle", 0, 1, null),
				SclableResources.of(VnfExtractorNode.class, "vnf_middle", 0, 1, null),
				SclableResources.of(VnfCreateNode.class, "vnf_right", 0, 1, null),
				SclableResources.of(VnfInstantiateNode.class, "vnf_right", 0, 1, null),
				SclableResources.of(VnfExtractorNode.class, "vnf_right", 0, 1, null));
		final PlanMultiplier pm = new PlanMultiplier(scales, func, List.of());
		final List<ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>>> plans = new ArrayList<>();
		scales.forEach(x -> {
			final ListenableGraph<Vertex2d, Edge2d> s = se.scale(g, x.getType(), x.getName());
			s.edgeSet().forEach(y -> {
				assertNotNull(y.getSource());
				assertNotNull(y.getTarget());
			});
			// exportGraph(s, "test-origin.dot");
			final ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>> np = pm.multiply(s, x);
			np.edgeSet().forEach(y -> {
				assertNotNull(y.getSource());
				assertNotNull(y.getTarget());
			});
			// exportGraph(np, "test-origin.dot");
			plans.add(np);
		});
		final PlanMerger pMerge = new PlanMerger();
		final ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>> ret = pMerge.merge(g, plans);
		final List<ContextUow<?>> global = new ArrayList<>();
		// exportGraph(ret, "test-scale.dot");
	}

	private static void exportGraph(final ListenableGraph np, final String filename) {
		final DOTExporter exporter = new DOTExporter<>(x -> x.toString()
				.replace("-", "_")
				.replace("=", "_")
				.replace("/", "_")
				.replace(" ", "_")
				.replace("\n", "_")
				.replace("(", "_")
				.replace(")", "_")
				.replace(">", "_"));
		exporter.exportGraph(np, new File(filename));
	}

}
