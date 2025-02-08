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
import org.jspecify.annotations.Nullable;

import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.scale.ContextVt;
import com.ubiqube.etsi.mano.orchestrator.uow.ContextUow;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 * Context3dNetFlow is responsible for managing the orchestration context in a directed acyclic graph.
 * It provides methods to add, retrieve, and manage units of work within the graph.
 *
 * @param <U> the type of the unit of work
 */
public class Context3dNetFlow<U> {
	private final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> d;
	@Nullable
	private ContextUow<U> root;
	private final List<ContextUow<U>> global;

	/**
	 * Constructs a Context3dNetFlow with the given graph and global context.
	 *
	 * @param g the initial graph
	 * @param global the global context
	 */
	public Context3dNetFlow(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final List<ContextUow<U>> global) {
		this.global = global;
		d = new DefaultListenableGraph(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		d.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		final Set<UnitOfWorkV3<U>> cache = new HashSet<>();
		initializeGraph(g, cache);
		makeStartNode();
	}

	/**
	 * Initializes the graph by adding vertices and edges from the given graph.
	 *
	 * @param g the initial graph
	 * @param cache the cache to store processed vertices
	 */
	private void initializeGraph(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final Set<UnitOfWorkV3<U>> cache) {
		g.vertexSet().forEach(x -> addVertexToGraph(x, cache));
		g.edgeSet().forEach(x -> addEdgeToGraph(x, cache));
	}

	/**
	 * Adds a vertex to the graph if it is not already present in the cache.
	 *
	 * @param vertex the vertex to add
	 * @param cache the cache to store processed vertices
	 */
	private void addVertexToGraph(final UnitOfWorkV3<U> vertex, final Set<UnitOfWorkV3<U>> cache) {
		if (!cache.contains(vertex)) {
			d.addVertex(vertex);
			cache.add(vertex);
		}
	}

	/**
	 * Adds an edge to the graph, ensuring both source and target vertices are present.
	 *
	 * @param edge the edge to add
	 * @param cache the cache to store processed vertices
	 */
	private void addEdgeToGraph(final ConnectivityEdge<UnitOfWorkV3<U>> edge, final Set<UnitOfWorkV3<U>> cache) {
		final UnitOfWorkV3<U> src = edge.getSource();
		final UnitOfWorkV3<U> dst = edge.getTarget();
		final UnitOfWorkV3<U> vc = getOrAddVertex(src, cache);
		final UnitOfWorkV3<U> vcTgt = getOrAddVertex(dst, cache);
		d.addEdge(vc, vcTgt);
	}

	/**
	 * Retrieves or adds a vertex to the graph.
	 *
	 * @param vertex the vertex to retrieve or add
	 * @param cache the cache to store processed vertices
	 * @return the vertex from the graph
	 */
	private UnitOfWorkV3<U> getOrAddVertex(final UnitOfWorkV3<U> vertex, final Set<UnitOfWorkV3<U>> cache) {
		if (!cache.contains(vertex)) {
			cache.add(vertex);
			d.addVertex(vertex);
			return vertex;
		} else {
			return findVertex(vertex);
		}
	}

	/**
	 * Creates the start node and connects it to all vertices with no incoming edges.
	 */
	private void makeStartNode() {
		final List<UnitOfWorkV3<U>> startNodes = d.vertexSet().stream().filter(x -> d.incomingEdgesOf(x).isEmpty()).toList();
		root = new ContextUow<>(new ContextVt<>(Node.class, "start", null));
		d.addVertex(root);
		startNodes.forEach(x -> d.addEdge(root, x));
	}

	/**
	 * Finds a vertex in the graph.
	 *
	 * @param src the vertex to find
	 * @return the vertex from the graph
	 */
	private UnitOfWorkV3<U> findVertex(final UnitOfWorkV3<U> src) {
		return d.vertexSet().stream().filter(x -> x == src).findFirst().orElseThrow();
	}

	/**
	 * Retrieves the parent resources of the given unit of work.
	 *
	 * @param actual the unit of work
	 * @param class1 the class of the node
	 * @param toscaName the TOSCA name
	 * @return the list of parent resources
	 */
	public List<String> getParent(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String toscaName) {
		final Optional<ContextUow<U>> gv = findInCtx(class1, toscaName);
		if (gv.isPresent()) {
			return List.of(gv.get().getResource());
		}
		Objects.requireNonNull(actual, "actual could not be null");
		final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths = getAllPaths(actual);
		final Set<VirtualTaskV3<U>> tasks = filterTasks(paths, class1, toscaName);
		return tasks.stream().map(VirtualTaskV3::getVimResourceId).toList();
	}

	/**
	 * Retrieves all paths from the root to the given unit of work.
	 *
	 * @param actual the unit of work
	 * @return the list of graph paths
	 */
	private List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> getAllPaths(final UnitOfWorkV3<U> actual) {
		final AllDirectedPaths<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> path = new AllDirectedPaths<>(d);
		return path.getAllPaths(root, actual, true, 1000);
	}

	/**
	 * Filters tasks based on the given class and TOSCA name.
	 *
	 * @param paths the list of graph paths
	 * @param class1 the class of the node
	 * @param toscaName the TOSCA name
	 * @return the set of filtered tasks
	 */
	private Set<VirtualTaskV3<U>> filterTasks(final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths, final Class<? extends Node> class1, final String toscaName) {
		if ("VNF_INDICATOR".equals(toscaName)) {
			return paths.stream()
					.flatMap(x -> x.getVertexList().stream())
					.filter(x -> x.getType() == class1)
					.map(UnitOfWorkV3::getVirtualTask)
					.collect(Collectors.toSet());
		} else {
			return paths.stream()
					.flatMap(x -> x.getVertexList().stream())
					.filter(x -> x.getType() == class1)
					.filter(x -> x.getVirtualTask().getToscaName().equals(toscaName))
					.map(UnitOfWorkV3::getVirtualTask)
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Finds a context unit of work in the global context.
	 *
	 * @param class1 the class of the node
	 * @param toscaName the TOSCA name
	 * @return the optional context unit of work
	 */
	private Optional<ContextUow<U>> findInCtx(final Class<? extends Node> class1, final String toscaName) {
		return global.stream()
				.filter(x -> x.getType() == class1)
				.filter(x -> x.getVirtualTask().getName().equals(toscaName))
				.findFirst();
	}

	/**
	 * Adds a new unit of work to the graph.
	 *
	 * @param actual the existing unit of work
	 * @param class1 the class of the node
	 * @param name the name of the new unit of work
	 * @param resourceId the resource ID of the new unit of work
	 */
	public void add(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1, final String name, final String resourceId) {
		Objects.requireNonNull(actual, "Actual must not be null");
		final ContextUow<U> uow = createContextUow(class1, name, resourceId);
		d.addVertex(uow);
		addEdgesForNewVertex(actual, uow);
	}

	/**
	 * Creates a new context unit of work.
	 *
	 * @param class1 the class of the node
	 * @param name the name of the new unit of work
	 * @param resourceId the resource ID of the new unit of work
	 * @return the new context unit of work
	 */
	private ContextUow<U> createContextUow(final Class<? extends Node> class1, final String name, final String resourceId) {
		final ContextVt<U> v = new ContextVt<>(class1, name, resourceId);
		return new ContextUow<>(v);
	}

	/**
	 * Adds edges for the new unit of work.
	 *
	 * @param actual the existing unit of work
	 * @param uow the new unit of work
	 */
	private void addEdgesForNewVertex(final UnitOfWorkV3<U> actual, final ContextUow<U> uow) {
		d.incomingEdgesOf(actual).forEach(x -> d.addEdge(x.getSource(), uow));
		d.outgoingEdgesOf(actual).forEach(x -> d.addEdge(uow, x.getTarget()));
	}

	/**
	 * Retrieves the resource ID of the given unit of work.
	 *
	 * @param actual the unit of work
	 * @param class1 the class of the node
	 * @param toscaName the TOSCA name
	 * @return the resource ID
	 */
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

	/**
	 * Retrieves the parent resources of the given unit of work.
	 *
	 * @param actual the unit of work
	 * @param class1 the class of the node
	 * @return the list of parent resources
	 */
	public List<String> getParent(final UnitOfWorkV3<U> actual, final Class<? extends Node> class1) {
		Objects.requireNonNull(actual, "actual could not be null");
		final List<GraphPath<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>>> paths = getAllPaths(actual);
		return paths.stream()
				.flatMap(x -> x.getVertexList().stream())
				.filter(x -> x.getType() == class1)
				.map(UnitOfWorkV3::getVirtualTask)
				.map(VirtualTaskV3::getVimResourceId)
				.collect(Collectors.toSet())
				.stream()
				.toList();
	}

	/**
	 * Retrieves the optional resource ID of the given unit of work.
	 *
	 * @param actual the unit of work
	 * @param class1 the class of the node
	 * @param toscaName the TOSCA name
	 * @return the optional resource ID
	 */
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
