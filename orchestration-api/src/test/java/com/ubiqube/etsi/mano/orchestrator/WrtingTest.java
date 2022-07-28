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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.model.ComputeScale;
import com.ubiqube.etsi.mano.orchestrator.model.MonitoringScale;
import com.ubiqube.etsi.mano.orchestrator.model.NetworkScale;
import com.ubiqube.etsi.mano.orchestrator.model.PortPairGroupScale;
import com.ubiqube.etsi.mano.orchestrator.model.PortPairScale;
import com.ubiqube.etsi.mano.orchestrator.model.PortScale;
import com.ubiqube.etsi.mano.orchestrator.model.ScaleConnectivity;
import com.ubiqube.etsi.mano.orchestrator.model.ScaleListener;
import com.ubiqube.etsi.mano.orchestrator.model.ScaleModel;
import com.ubiqube.etsi.mano.orchestrator.model.StorageScale;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
class WrtingTest {

	private static final Logger LOG = LoggerFactory.getLogger(WrtingTest.class);

	ListenableGraph<ScaleModel, ScaleConnectivity> g;

	private final Map<ScaleModel, ScaleModel> cache;

	public WrtingTest() {
		g = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ScaleConnectivity.class));
		g.addGraphListener(new ScaleListener());
		final NetworkScale vl1 = new NetworkScale("vl_1");
		g.addVertex(vl1);
		final NetworkScale vl2 = new NetworkScale("vl_2");
		g.addVertex(vl2);

		final PortScale portLbIn1 = new PortScale("port_lb_in_1");
		g.addVertex(portLbIn1);
		final PortScale portLbOut1 = new PortScale("port_lb_out_1");
		g.addVertex(portLbOut1);

		final PortPairScale portPairLb1 = new PortPairScale("port_pair_lb_1");
		g.addVertex(portPairLb1);

		final PortPairGroupScale portPairGroupLb = new PortPairGroupScale("port_pair_group_lb");
		g.addVertex(portPairGroupLb);

		final StorageScale storageLbc1 = new StorageScale("storage_lbc_1");
		g.addVertex(storageLbc1);

		final MonitoringScale monitoringLb1 = new MonitoringScale("mon_lb_1");
		g.addVertex(monitoringLb1);

		final ComputeScale computeLb = new ComputeScale("compute_lb_1");
		g.addVertex(computeLb);

		g.addEdge(vl1, portLbIn1);
		g.addEdge(vl2, portLbOut1);
		g.addEdge(portLbIn1, portPairLb1);
		g.addEdge(portLbOut1, portPairLb1);
		g.addEdge(portPairLb1, portPairGroupLb);

		g.addEdge(portLbIn1, computeLb);
		g.addEdge(portLbOut1, computeLb);
		g.addEdge(computeLb, storageLbc1);
		g.addEdge(computeLb, monitoringLb1);

		cache = new HashMap<>();
	}

	@Test
	void compute01() {
		final Set<ScaleModel> v = g.vertexSet();
		final ScaleModel s = findScaleModel(Compute.class, "compute_lb_1");
		final ScaleModel newObj = s.clone(1);
		assertNotNull(newObj);
		g.addVertex(newObj);
		cache.put(s, newObj);
		handleOut(g, s, newObj);
		handleIn(g, s, newObj);
		exportGraph(g, "test.dot");
	}

	@Test
	void testNamedResources() {
		//
		NamedResource r = NamedResource.of("toscaName");
		assertEquals("toscaName", r.toString());
		r = NamedResource.of("toscaName", "1");
		assertEquals("toscaName_1", r.toString());
		r = NamedResource.of("toscaName", "1", "h");
		assertEquals("toscaName_1_h", r.toString());
		assertEquals("toscaName", r.toBaseName());
	}

	private void handleOut(final ListenableGraph<ScaleModel, ScaleConnectivity> g, final ScaleModel source, final ScaleModel newObj) {
		final Set<ScaleConnectivity> out = g.outgoingEdgesOf(source);
		out.forEach(xx -> {
			final ScaleModel x = xx.getTarget();
			if (x.isSingleNode()) {
				g.addEdge(newObj, x);
				LOG.info("Signle mode {}", x);
				return;
			}
			if (null != cache.get(x)) {
				addIfNeeded(newObj, cache.get(x));
				LOG.info("Cahe out {}", xx);
				return;
			}
			final ScaleModel ns = x.clone(1);
			cache.put(x, ns);
			g.addVertex(ns);
			g.addEdge(newObj, ns);
			handleOut(g, x, ns);
			handleIn(g, x, ns);
		});
	}

	private void handleIn(final ListenableGraph<ScaleModel, ScaleConnectivity> g2, final ScaleModel source, final ScaleModel newObj) {
		final Set<ScaleConnectivity> in = g.incomingEdgesOf(source);
		in.forEach(xx -> {
			final ScaleModel x = xx.getSource();
			if (x.isSingleNode()) {
				g.addEdge(x, newObj);
				LOG.info("Signle mode II {}", x);
				return;
			}
			if (cache.get(x) != null) {
				addIfNeeded(cache.get(x), newObj);
				LOG.info("Cahe in {}", xx);
				return;
			}
			final ScaleModel ns = x.clone(1);
			cache.put(x, ns);
			g.addVertex(ns);
			g.addEdge(ns, newObj);
			handleOut(g, x, ns);
			handleIn(g, x, ns);
		});
	}

	private void addIfNeeded(final ScaleModel source, final ScaleModel target) {
		final boolean edgeExist = g.edgeSet().stream().filter(x -> x.getSource() == source).anyMatch(x -> x.getTarget() == target);
		if (edgeExist) {
			return;
		}
		LOG.debug("Edge doesn't exist {} <-> {}", source, target);
		g.addEdge(source, target);
	}

	private ScaleModel findScaleModel(final Class<? extends Node> class1, final String string) {
		return g.vertexSet().stream().filter(x -> x.getName().equals(string)).findFirst().orElseThrow();
	}

	public static void exportGraph(final ListenableGraph g, final String fileName) {
		final DOTExporter<ScaleModel, ScaleConnectivity> exporter = new DOTExporter<>(x -> x.getName().replace('-', '_'));
		try (final FileOutputStream out = new FileOutputStream(fileName)) {
			exporter.exportGraph(g, out);
		} catch (final IOException e) {
			LOG.trace("Error in graph export", e);
		}
	}
}