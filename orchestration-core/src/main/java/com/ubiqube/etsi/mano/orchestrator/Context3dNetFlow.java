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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 *
 * @author olivier
 *
 */
public class Context3dNetFlow<U> {
	private static final Logger LOG = LoggerFactory.getLogger(Context3dNetFlow.class);
	private final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> d;
	private ContextUow<U> root;

	public Context3dNetFlow(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g) {
		d = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		d.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		final Set<UnitOfWorkV3<U>> cache = new HashSet<>();
		g.edgeSet().forEach(x -> {
			final UnitOfWorkV3<U> src = x.getSource();
			UnitOfWorkV3<U> vc;
			if (!cache.contains(src)) {
				cache.add(src);
				d.addVertex(src);
				vc = src;
			} else {
				vc = findVertex(src);
			}
			final UnitOfWorkV3<U> dst = x.getTarget();
			UnitOfWorkV3<U> vcTgt;
			if (!cache.contains(dst)) {
				cache.add(dst);
				d.addVertex(dst);
				vcTgt = dst;
			} else {
				vcTgt = findVertex(dst);
			}
			d.addEdge(vc, vcTgt);
		});
		makeStartNode();
	}

	private void makeStartNode() {
		final List<UnitOfWorkV3<U>> lv = d.vertexSet().stream().filter(x -> d.incomingEdgesOf(x).isEmpty()).toList();
		root = new ContextUow<>(new ContextVt<>(Node.class, "start", null));
		d.addVertex(root);
		lv.forEach(x -> d.addEdge(root, x));
	}

	private UnitOfWorkV3<U> findVertex(final UnitOfWorkV3<U> src) {
		return d.vertexSet().stream().filter(x -> x == src).findFirst().orElseThrow();
	}

	public List<String> getParent(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String toscaName) {
		Objects.requireNonNull(actual, "actual could not be null");
		final AllDirectedPaths<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> path = new AllDirectedPaths<>(d);
		final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths = path.getAllPaths(root, actual, true, 1000);
		final DOTExporter<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> exporter = new DOTExporter<>(this::toDotName);
		try (final FileOutputStream out = new FileOutputStream("context.dot")) {
			exporter.exportGraph(d, out);
		} catch (final IOException e) {
			LOG.trace("Error in graph export", e);
		}
		/*-
		 * Here we can have a schema like that:
		 * A
		 *  \
		 *   B - D
		 *  /
		 * C
		 *
		 * In this case B will be duplicated.
		 */
		final Set<VirtualTaskV3<U>> tasks = paths.stream()
				.flatMap(x -> x.getVertexList().stream())
				.filter(x -> x.getType() == class1)
				.filter(x -> x.getTask().getToscaName().equals(toscaName))
				.map(UnitOfWorkV3::getTask)
				.collect(Collectors.toSet());
		return tasks.stream().map(VirtualTaskV3::getVimResourceId).toList();
	}

	public void add(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String name, final String resourceId) {
		Objects.requireNonNull(actual, "Actual must not be null");
		final ContextVt<U> v = new ContextVt<>(class1, name, resourceId);
		final ContextUow<U> uow = new ContextUow<>(v);
		d.addVertex(uow);
		d.incomingEdgesOf(actual).forEach(x -> d.addEdge(x.getSource(), uow));
		d.outgoingEdgesOf(actual).forEach(x -> d.addEdge(uow, x.getTarget()));
	}

	@NotNull
	public String get(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String toscaName) {
		final List<String> l = getParent(actual, class1, toscaName);
		if (l.size() != 1) {
			throw new OrchestrationException("Could not find correct element number: " + l.size() + " in " + class1.getSimpleName() + "-" + toscaName);
		}
		return l.get(0);
	}

	public void add(final UnitOfWorkV3<U> uaow, final String res) {
		throw new IllegalAccessError();
	}

	public List<String> getParent(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1) {
		Objects.requireNonNull(actual, "actual could not be null");
		final AllDirectedPaths<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> path = new AllDirectedPaths<>(d);
		final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths = path.getAllPaths(root, actual, true, 1000);
		return paths.stream()
				.flatMap(x -> x.getVertexList().stream())
				.filter(x -> x.getType() == class1)
				.map(UnitOfWorkV3::getTask)
				.map(VirtualTaskV3::getVimResourceId)
				.collect(Collectors.toSet())
				.stream()
				.toList();
	}

	@Nullable
	public String getOptional(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String toscaName) {
		final List<String> l = getParent(actual, class1, toscaName);
		if (l.size() > 1) {
			throw new OrchestrationException("Get Optional failed for " + toscaName + ", found " + l.size() + " elements.");
		}
		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	private String toDotName(final UnitOfWorkV3<U> task) {
		final String base = task.getType().getSimpleName() + "_" + task.getTask().getName();
		return base.replace("/", "_").replace("-", "_").replace("\n", "_").replace(",", "_").replace("(", "_").replace(")", "_");
	}
}
