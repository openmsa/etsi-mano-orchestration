/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.orchestrator.v3;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.controllers.VertexResult;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.DnsHost;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.DnsZone;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Monitoring;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.SecurityGroupNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Storage;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.SubNetwork;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.VnfPortNode;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMerger;
import com.ubiqube.etsi.mano.orchestrator.scale.PlanMultiplier;
import com.ubiqube.etsi.mano.orchestrator.scale.ScalingEngine;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.graph.GraphListener2d;

class Plan3dTest {

	private final ListenableGraph<Vertex2d, Edge2d> g;
	private final List<SclableResources<Object>> sr;

	public Plan3dTest() throws StreamReadException, DatabindException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final VertexResult r;
		try (InputStream is = this.getClass().getResourceAsStream("/vnf-full.json")) {
			r = mapper.readValue(is, VertexResult.class);
		}
		g = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(Edge2d.class));
		g.addGraphListener(new GraphListener2d());
		r.getVertices().forEach(x -> g.addVertex(x));
		sr = r.getVertices().stream().map(x -> SclableResources.of(x.getType(), x.getName(), 0, 1, null)).toList();
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
	void dummyTest() {
		assertTrue(true);
	}

	/**
	 * Looks like a test problem.
	 *
	 * @throws Exception
	 */
	void testVnf() throws Exception {
		final ScalingEngine se = new ScalingEngine();
		final Function<Object, VirtualTaskV3<Object>> func = p -> new TestVirtualTask(null, null, null, 0);
		final List<SclableResources<Object>> scales = new ArrayList<>();
		scales.add(SclableResources.of(SecurityGroupNode.class, "security", 0, 1, null));
		scales.add(SclableResources.of(Monitoring.class, "name", 0, 1, null));
		scales.add(SclableResources.of(VnfPortNode.class, "cpLc01", 0, 1, null));
		scales.add(SclableResources.of(DnsHost.class, "leftVdu01-cpLc01", 0, 1, null));
		scales.add(SclableResources.of(VnfPortNode.class, "cpRc01", 0, 1, null));
		scales.add(SclableResources.of(DnsHost.class, "rightVdu01-cpRc01", 0, 1, null));
		scales.add(SclableResources.of(VnfPortNode.class, "cpLc02", 0, 1, null));
		scales.add(SclableResources.of(DnsHost.class, "leftVdu01-cpLc02", 0, 1, null));
		scales.add(SclableResources.of(VnfPortNode.class, "cpRc02", 0, 1, null));
		scales.add(SclableResources.of(DnsHost.class, "rightVdu01-cpRc02", 0, 1, null));
		scales.add(SclableResources.of(Compute.class, "leftVdu01", 0, 1, null));
		scales.add(SclableResources.of(Compute.class, "rightVdu01", 0, 1, null));
		scales.add(SclableResources.of(Network.class, "middleVl01", 0, 1, null));
		scales.add(SclableResources.of(DnsZone.class, "middleVl01", 0, 1, null));
		scales.add(SclableResources.of(SubNetwork.class, "middleVl01-vl01L2", 0, 1, null));
		scales.add(SclableResources.of(SubNetwork.class, "middleVl01-vl01L2-bis", 0, 1, null));
		scales.add(SclableResources.of(Network.class, "leftVl01", 0, 1, null));
		scales.add(SclableResources.of(SubNetwork.class, "leftVl01-vl01L2", 0, 1, null));
		scales.add(SclableResources.of(DnsZone.class, "leftVl01", 0, 1, null));
		scales.add(SclableResources.of(Storage.class, "block01", 0, 1, null));
		final List<ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>>> plans = new ArrayList<>();
		final PlanMultiplier pm = new PlanMultiplier<>(scales, func, List.of());
		scales.forEach(x -> {
			final ListenableGraph<Vertex2d, Edge2d> s = se.scale(g, x.getType(), x.getName());
			s.edgeSet().forEach(y -> {
				assertNotNull(y.getSource());
				assertNotNull(y.getTarget());
			});
			// exportGraph(g, "test-origin.dot");
			final ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>> np = pm.multiply(s, x);
			np.edgeSet().forEach(y -> {
				assertNotNull(y.getSource());
				assertNotNull(y.getTarget());
			});
			plans.add(np);
		});
		final PlanMerger pMerge = new PlanMerger();
		final ListenableGraph<VirtualTaskV3<Object>, VirtualTaskConnectivityV3<Object>> ret = pMerge.merge(g, plans);
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
