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
package com.ubiqube.etsi.mano.orchestrator.scale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.Edge2d;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.exceptions.OrchestrationException;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;

/**
 * The PlanMultiplier class is responsible for multiplying a given plan by the specified scalable resources.
 * It creates a new graph based on the input plan and scalable resources.
 *
 * @param <U> The type of the scalable resources.
 */
public class PlanMultiplier<U> {
	private static final String ADD_VT = "Add VT {}";
	private static final Logger LOG = LoggerFactory.getLogger(PlanMultiplier.class);

	@NonNull
	private final List<SclableResources<U>> scaleResources;
	private final Function<U, VirtualTaskV3<U>> converter;
	@NonNull
	private final List<ContextHolder> liveItems;

	/**
	 * Constructs a new PlanMultiplier.
	 *
	 * @param scaleResources The list of scalable resources.
	 * @param converter      The function to convert scalable resources to virtual tasks.
	 * @param liveItems      The list of live items.
	 */
	public PlanMultiplier(final List<SclableResources<U>> scaleResources, final Function<U, VirtualTaskV3<U>> converter, final List<ContextHolder> liveItems) {
		this.scaleResources = scaleResources;
		this.converter = converter;
		this.liveItems = liveItems;
	}

	/**
	 * Multiplies the given plan by the specified scalable resources.
	 *
	 * @param plan The 2D plan of the connected resources.
	 * @param sr   The scalable resources.
	 * @return The new graph.
	 */
	public ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> multiply(final ListenableGraph<Vertex2d, Edge2d> plan, final SclableResources<U> sr) {
		final Set<ContextHolder> cache = new HashSet<>();
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph = new DefaultListenableGraph(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
		graph.addGraphListener(new VirtualTaskVertexListenerV3<>());
		final Map<String, VirtualTaskV3<U>> vertexMap = new HashMap<>();
		final int maxIterations = Math.max(sr.getHave(), sr.getWant());
		LOG.debug("SR: {}", sr);

		for (int i = 0; i < maxIterations; i++) {
			final boolean delete = i >= sr.getWant();
			final int iteration = i;
			processEdges(plan, graph, vertexMap, delete, iteration, cache);
			processVertices(plan, graph, vertexMap, delete, iteration, cache);
		}

		collectOrphan(graph, plan, maxIterations, cache);
		return graph;
	}

	/**
	 * Processes the edges of the plan and adds them to the graph.
	 *
	 * @param plan       The 2D plan of the connected resources.
	 * @param graph      The graph to add the edges to.
	 * @param vertexMap  The map of vertex IDs to virtual tasks.
	 * @param delete     Whether to delete the vertices.
	 * @param iteration  The current iteration.
	 * @param cache      The cache of context holders.
	 */
	private void processEdges(final ListenableGraph<Vertex2d, Edge2d> plan, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph,
			final Map<String, VirtualTaskV3<U>> vertexMap, final boolean delete, final int iteration, final Set<ContextHolder> cache) {
		plan.edgeSet().forEach(edge -> {
			final String srcId = getUniqId(edge.getSource(), iteration);
			final VirtualTaskV3<U> srcVertex = vertexMap.computeIfAbsent(srcId, id -> createVertex(graph, delete, iteration, edge.getSource(), srcId, cache));
			final String dstId = getUniqId(edge.getTarget(), iteration);
			final VirtualTaskV3<U> dstVertex = vertexMap.computeIfAbsent(dstId, id -> createVertex(graph, delete, iteration, edge.getTarget(), dstId, cache));
			Optional.ofNullable(graph.addEdge(srcVertex, dstVertex)).ifPresent(e -> e.setRelation(edge.getRelation()));
		});
	}

	/**
	 * Processes the vertices of the plan and adds them to the graph.
	 *
	 * @param plan       The 2D plan of the connected resources.
	 * @param graph      The graph to add the vertices to.
	 * @param vertexMap  The map of vertex IDs to virtual tasks.
	 * @param delete     Whether to delete the vertices.
	 * @param iteration  The current iteration.
	 * @param cache      The cache of context holders.
	 */
	private void processVertices(final ListenableGraph<Vertex2d, Edge2d> plan, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph,
			final Map<String, VirtualTaskV3<U>> vertexMap, final boolean delete, final int iteration, final Set<ContextHolder> cache) {
		plan.vertexSet().forEach(vertex -> {
			final String vertexId = getUniqId(vertex, iteration);
			vertexMap.computeIfAbsent(vertexId, id -> createVertex(graph, delete, iteration, vertex, vertexId, cache));
		});
	}

	/**
	 * Collects orphan vertices and adds them to the graph.
	 *
	 * @param graph         The graph to add the orphan vertices to.
	 * @param plan          The 2D plan of the connected resources.
	 * @param maxIterations The maximum number of iterations.
	 * @param cache         The cache of context holders.
	 */
	private void collectOrphan(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final ListenableGraph<Vertex2d, Edge2d> plan, final int maxIterations, final Set<ContextHolder> cache) {
		final List<Vertex2d> liveVertices = plan.vertexSet().stream()
				.filter(vertex -> match(vertex, liveItems))
				.toList();
		final List<Vertex2d> orphanVertices = liveVertices.stream().filter(vertex -> isNotIn(vertex, graph)).toList();
		orphanVertices.forEach(vertex -> createVertex(graph, true, maxIterations, vertex, UUID.randomUUID().toString(), cache));
		LOG.debug("{}", orphanVertices);
	}

	/**
	 * Checks if a vertex is not in the graph.
	 *
	 * @param vertex The vertex to check.
	 * @param graph  The graph to check against.
	 * @return True if the vertex is not in the graph, false otherwise.
	 */
	private static <U> boolean isNotIn(final Vertex2d vertex, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph) {
		return graph.vertexSet().stream()
				.filter(v -> v.getType() == vertex.getType())
				.noneMatch(v -> v.getName().equals(vertex.getName()));
	}

	/**
	 * Matches a vertex against a list of live items.
	 *
	 * @param vertex    The vertex to match.
	 * @param liveItems The list of live items.
	 * @return True if the vertex matches any live item, false otherwise.
	 */
	private static boolean match(final Vertex2d vertex, final List<ContextHolder> liveItems) {
		return liveItems.stream()
				.filter(item -> item.getType() == vertex.getType())
				.anyMatch(item -> item.getName().equals(vertex.getName()));
	}

	/**
	 * Creates a vertex and adds it to the graph.
	 *
	 * @param graph     The graph to add the vertex to.
	 * @param delete    Whether to delete the vertex.
	 * @param iteration The current iteration.
	 * @param vertex    The vertex to create.
	 * @param vertexId  The ID of the vertex.
	 * @param cache     The cache of context holders.
	 * @return The created virtual task.
	 */
	private VirtualTaskV3<U> createVertex(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph,
			final boolean delete, final int iteration, final Vertex2d vertex, final String vertexId, final Set<ContextHolder> cache) {
		final SclableResources<U> templateResource = findTemplate(scaleResources, vertex);
		final VirtualTaskV3<U> task = createTask(templateResource, vertexId, vertex, iteration, delete, cache);
		LOG.debug(ADD_VT, task.getAlias());
		graph.addVertex(task);
		return task;
	}

	/**
	 * Finds the template resource for a given vertex.
	 *
	 * @param resources The list of scalable resources.
	 * @param target    The target vertex.
	 * @return The matching scalable resource.
	 */
	private static <U> SclableResources<U> findTemplate(final List<SclableResources<U>> resources, final Vertex2d target) {
		final List<SclableResources<U>> matchingResources = resources.stream()
				.filter(resource -> resource.getType() == target.getType())
				.filter(resource -> resource.getName().equals(target.getName()))
				.toList();
		if (matchingResources.size() > 1) {
			throw new OrchestrationException("Multiple match for vertex " + target);
		}
		if (matchingResources.isEmpty()) {
			throw new OrchestrationException("No match for vertex " + target);
		}
		LOG.trace("Found {}", matchingResources.get(0));
		return matchingResources.get(0);
	}

	/**
	 * Creates a task for a given vertex.
	 *
	 * @param resource  The scalable resource.
	 * @param vertexId  The ID of the vertex.
	 * @param vertex    The vertex to create the task for.
	 * @param iteration The current iteration.
	 * @param delete    Whether to delete the task.
	 * @param cache     The cache of context holders.
	 * @return The created virtual task.
	 */
	private VirtualTaskV3<U> createTask(final SclableResources<U> resource, final String vertexId, final Vertex2d vertex, final int iteration, final boolean delete, final Set<ContextHolder> cache) {
		final Optional<ContextHolder> context = findInContext(liveItems, vertex, iteration);
		VirtualTaskV3<U> task = createTask(vertexId, vertex, iteration, delete, resource.getTemplateParameter());
		if (context.isPresent()) {
			final ContextHolder liveInstance = context.get();
			cache.add(liveInstance);
			if (!delete) {
				return createContext(vertexId, vertex, iteration, delete, task.getTemplateParameters(), liveInstance.getResourceId(), task.getType(), liveInstance.getVimConnectionId());
			}
			task.setVimResourceId(liveInstance.getResourceId());
			task.setVimConnectionId(liveInstance.getVimConnectionId());
			task.setRemovedLiveInstanceId(Objects.requireNonNull(liveInstance.getLiveInstanceId()));
		} else {
			if (delete) {
				if ((resource.getHave() != 0) || (resource.getWant() != 0)) {
					LOG.warn("Deleting {} but not found in context.", vertexId);
				}
				task = createContext(vertexId, vertex, iteration, delete, task.getTemplateParameters(), null, task.getType(), null);
			}
			LOG.trace("creating task: {}/{}", vertex.getType().getSimpleName(), vertex.getName());
		}
		return task;
	}

	/**
	 * Creates a context for a given vertex.
	 *
	 * @param vertexId       The ID of the vertex.
	 * @param vertex         The vertex to create the context for.
	 * @param iteration      The current iteration.
	 * @param delete         Whether to delete the context.
	 * @param parameters     The template parameters.
	 * @param resourceId     The resource ID.
	 * @param type           The type of the node.
	 * @param vimConnectionId The VIM connection ID.
	 * @return The created virtual task.
	 */
	private static <U> VirtualTaskV3<U> createContext(final String vertexId, final Vertex2d vertex, final int iteration, final boolean delete,
			final U parameters, @Nullable final String resourceId, final Class<? extends Node> type, @Nullable final String vimConnectionId) {
		return (VirtualTaskV3<U>) ContextVt.builder()
				.alias(vertexId)
				.delete(delete)
				.name(vertex.getName())
				.rank(iteration)
				.templateParameters(parameters)
				.vimResourceId(resourceId)
				.vimConnectionId(vimConnectionId)
				.parent(type)
				.build();
	}

	/**
	 * Finds a context holder for a given vertex and iteration.
	 *
	 * @param liveItems The list of live items.
	 * @param vertex    The vertex to find the context holder for.
	 * @param iteration The current iteration.
	 * @return The matching context holder.
	 */
	private static Optional<ContextHolder> findInContext(final List<ContextHolder> liveItems, final Vertex2d vertex, final int iteration) {
		final List<ContextHolder> matchingItems = liveItems.stream()
				.filter(item -> item.getType() == vertex.getType())
				.filter(item -> item.getName().equals(vertex.getName()))
				.filter(item -> item.getRank() == iteration)
				.toList();
		if (matchingItems.size() > 1) {
			LOG.warn("List is more than 1 for {}-{}", vertex.getType().getSimpleName(), vertex.getName());
		}
		return Optional.ofNullable(matchingItems).filter(list -> !list.isEmpty()).map(list -> list.get(0));
	}

	/**
	 * Generates a unique ID for a given vertex and iteration.
	 *
	 * @param vertex    The vertex to generate the ID for.
	 * @param iteration The current iteration.
	 * @return The generated unique ID.
	 */
	private static String getUniqId(final Vertex2d vertex, final int iteration) {
		final StringBuilder sb = new StringBuilder(vertex.getName());
		if (vertex.getParent() != null) {
			sb.append("-").append(vertex.getParent());
		}
		sb.append("-").append(vertex.getType().getSimpleName());
		sb.append("-").append(String.format("%04d", iteration));
		return sb.toString();
	}

	/**
	 * Creates a task for a given vertex.
	 *
	 * @param vertexId   The ID of the vertex.
	 * @param vertex     The vertex to create the task for.
	 * @param iteration  The current iteration.
	 * @param delete     Whether to delete the task.
	 * @param parameters The template parameters.
	 * @return The created virtual task.
	 */
	private VirtualTaskV3<U> createTask(final String vertexId, final Vertex2d vertex, final int iteration, final boolean delete, final U parameters) {
		final VirtualTaskV3<U> task = converter.apply(parameters);
		task.setRank(iteration);
		task.setName(vertex.getName());
		task.setAlias(vertexId);
		task.setDelete(delete);
		return task;
	}

}
