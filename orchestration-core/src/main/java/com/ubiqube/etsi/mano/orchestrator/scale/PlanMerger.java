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
package com.ubiqube.etsi.mano.orchestrator.scale;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.GraphTools;
import com.ubiqube.etsi.mano.orchestrator.Relation;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;

/**
 *
 * @author olivier
 *
 */
public class PlanMerger {
	private static final Logger LOG = LoggerFactory.getLogger(PlanMerger.class);

	public <U> ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> merge(final ListenableGraph<Vertex2d, Edge2d> g, final List<ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>>> plans) {
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d = new DefaultListenableGraph(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
		d.addGraphListener(new VirtualTaskVertexListenerV3<>());
		plans.stream().flatMap(x -> x.vertexSet().stream()).forEach(d::addVertex);
		plans.stream().flatMap(x -> x.edgeSet().stream()).forEach(x -> d.addEdge(x.getSource(), x.getTarget()));
		g.edgeSet().forEach(x -> {
			final List<VirtualTaskV3<U>> srcs = getAll(d, x.getSource().getName(), x.getSource().getType(), List.of());
			final List<VirtualTaskV3<U>> tgts = getAll(d, x.getTarget().getName(), x.getTarget().getType(), srcs);
			if (x.getRelation() == Relation.ONE_TO_MANY) {
				makeOneToMany(d, srcs, tgts);
				return;
			}
			if (x.getRelation() == Relation.ONE_TO_ONE) {
				makeOneToOne(d, x, srcs, tgts);
				return;
			}
			if ((srcs.size() != 1) || (tgts.size() != 1)) {
				LOG.warn("::: {} = {} {} / {} ||| {}", srcs.size(), tgts.size(), x.getRelation(), x.getSource(), x.getTarget());
				LOG.trace("Got {} = {} / {} = {}", x.getSource().getName(), srcs, x.getTarget().getName(), tgts);
				LOG.trace("====================");
				return;
			}
			d.addEdge(srcs.get(0), tgts.get(0));
		});
		if (LOG.isDebugEnabled()) {
			exportPlan(d, "post-plan-merget.dot");
		}
		return d;
	}

	private static <U> void exportPlan(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d, final String filename) {
		final DOTExporter<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> exporter = new DOTExporter<>(GraphTools::toDotName);
		try (final FileOutputStream out = new FileOutputStream(filename)) {
			exporter.exportGraph(d, out);
		} catch (final IOException e) {
			LOG.trace("Error in graph export", e);
		}
	}

	private static <U> void makeOneToOne(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d, final Edge2d edge, final List<VirtualTaskV3<U>> srcs, final List<VirtualTaskV3<U>> tgts) {
		srcs.forEach(x -> {
			final Optional<VirtualTaskV3<U>> res = find(x, edge, tgts);
			if (res.isPresent()) {
				d.addEdge(x, res.get());
			} else {
				LOG.warn("One to one of ({} / {} ) => Could not find: {} in {}", namedVertex(edge.getSource()), namedVertex(edge.getTarget()), x, tgts);
			}
		});
	}

	private static <U> Optional<VirtualTaskV3<U>> find(final VirtualTaskV3<U> src, final Edge2d edge, final List<VirtualTaskV3<U>> tgts) {
		return tgts.stream()
				.filter(x -> x.getType() == edge.getTarget().getType())
				.filter(x -> x.getName().equals(edge.getTarget().getName()))
				.filter(x -> x.getRank() == src.getRank())
				.findFirst();
	}

	private static <U> void makeOneToMany(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d, final List<VirtualTaskV3<U>> srcs, final List<VirtualTaskV3<U>> tgts) {
		if (srcs.size() != 1) {
			LOG.debug("ERROR: ONE TO MANY but src is {}", srcs.size());
			return;
		}
		final VirtualTaskV3<U> src = srcs.get(0);
		tgts.forEach(x -> d.addEdge(src, x));
	}

	private static <U> List<VirtualTaskV3<U>> getAll(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> d, final String name, final Class<?> type, final List<VirtualTaskV3<U>> exclude) {
		return d.vertexSet().stream()
				.filter(x -> !exclude.contains(x))
				.filter(x -> x.getType() == type)
				.filter(x -> x.getName().startsWith(name))
				.toList();
	}

	private static String namedVertex(final Vertex2d v) {
		return v.getType().getSimpleName() + "-" + v.getName();
	}
}
