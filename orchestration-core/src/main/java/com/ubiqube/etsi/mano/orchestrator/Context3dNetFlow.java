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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author olivier
 *
 */
public class Context3dNetFlow<U> {
	private final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> d;
	private ContextUow<U> root;
	private final List<ContextUow<U>> global;

	public Context3dNetFlow(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final List<ContextUow<U>> global) {
		this.global = global;
		d = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		d.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		final Set<UnitOfWorkV3<U>> cache = new HashSet<>();
		g.vertexSet().forEach(x -> {
			if (!cache.contains(x)) {
				d.addVertex(x);
			}
		});
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
		final Optional<ContextUow<U>> gv = findInCtx(class1, toscaName);
		if (gv.isPresent()) {
			return List.of(gv.get().getResource());
		}
		Objects.requireNonNull(actual, "actual could not be null");
		final AllDirectedPaths<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> path = new AllDirectedPaths<>(d);
		final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths = path.getAllPaths(root, actual, true, 1000);
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
		Set<VirtualTaskV3<U>> tasks;
		if ("VNF_INDICATOR".equals(toscaName)) {
			tasks = paths.stream()
					.flatMap(x -> x.getVertexList().stream())
					.filter(x -> x.getType() == class1)
					.map(UnitOfWorkV3::getTask)
					.collect(Collectors.toSet());
		} else {
			tasks = paths.stream()
					.flatMap(x -> x.getVertexList().stream())
					.filter(x -> x.getType() == class1)
					.filter(x -> x.getTask().getToscaName().equals(toscaName))
					.map(UnitOfWorkV3::getTask)
					.collect(Collectors.toSet());
		}
		return tasks.stream().map(VirtualTaskV3::getVimResourceId).toList();
	}

	private Optional<ContextUow<U>> findInCtx(final Class<? extends Node> class1, final String toscaName) {
		return global.stream()
				.filter(x -> x.getType() == class1)
				.filter(x -> x.getTask().getName().equals(toscaName))
				.findFirst();
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
		final Optional<ContextUow<U>> gv = findInCtx(class1, toscaName);
		if (gv.isPresent()) {
			return gv.get().getResource();
		}
		final List<String> l = getParent(actual, class1, toscaName);
		if (l.size() != 1) {
			throw new OrchestrationException("Could not find correct element number: " + l.size() + " in " + class1.getSimpleName() + "-" + toscaName);
		}
		return l.get(0);
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
}
