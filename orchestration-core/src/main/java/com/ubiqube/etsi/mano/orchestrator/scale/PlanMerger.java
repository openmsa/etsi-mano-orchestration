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
import com.ubiqube.etsi.mano.orchestrator.Vertex2d;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskVertexListenerV3;

/**
 * This class is responsible for merging multiple orchestration plans into a single plan.
 * It provides methods to merge graphs and handle different types of relationships between tasks.
 * 
 * @param <U> the type of the units of work
 */
public class PlanMerger {
	private static final Logger LOG = LoggerFactory.getLogger(PlanMerger.class);

	/**
	 * Merges multiple orchestration plans into a single plan.
	 *
	 * @param g the base graph
	 * @param plans the list of plans to merge
	 * @return the merged plan
	 */
	public <U> ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> merge(final ListenableGraph<Vertex2d, Edge2d> g, final List<ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>>> plans) {
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> mergedGraph = createEmptyGraph();
		addPlansToGraph(plans, mergedGraph);
		connectGraphEdges(g, mergedGraph);
		exportGraphIfDebugEnabled(mergedGraph, "post-plan-merget.dot");
		return mergedGraph;
	}

	/**
	 * Creates an empty graph for merging plans.
	 *
	 * @return the empty graph
	 */
	private <U> ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> createEmptyGraph() {
		final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph = new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(VirtualTaskConnectivityV3.class));
		graph.addGraphListener(new VirtualTaskVertexListenerV3<>());
		return graph;
	}

	/**
	 * Adds the vertices and edges from the given plans to the merged graph.
	 *
	 * @param plans the list of plans
	 * @param mergedGraph the merged graph
	 */
	private <U> void addPlansToGraph(final List<ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>>> plans, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> mergedGraph) {
		plans.stream().flatMap(plan -> plan.vertexSet().stream()).forEach(mergedGraph::addVertex);
		plans.stream().flatMap(plan -> plan.edgeSet().stream()).forEach(edge -> mergedGraph.addEdge(edge.getSource(), edge.getTarget()));
	}

	/**
	 * Connects the edges from the base graph to the merged graph.
	 *
	 * @param baseGraph the base graph
	 * @param mergedGraph the merged graph
	 */
	private <U> void connectGraphEdges(final ListenableGraph<Vertex2d, Edge2d> baseGraph, final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> mergedGraph) {
		baseGraph.edgeSet().forEach(edge -> {
			final List<VirtualTaskV3<U>> sources = getAll(mergedGraph, edge.getSource().getName(), edge.getSource().getType(), List.of());
			final List<VirtualTaskV3<U>> targets = getAll(mergedGraph, edge.getTarget().getName(), edge.getTarget().getType(), sources);
			handleEdgeConnection(mergedGraph, edge, sources, targets);
		});
	}

	/**
	 * Handles the connection of an edge based on its relation type.
	 *
	 * @param mergedGraph the merged graph
	 * @param edge the edge to connect
	 * @param sources the source vertices
	 * @param targets the target vertices
	 */
	private <U> void handleEdgeConnection(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> mergedGraph, final Edge2d edge, final List<VirtualTaskV3<U>> sources, final List<VirtualTaskV3<U>> targets) {
		switch (edge.getRelation()) {
			case ONE_TO_MANY:
				makeOneToMany(mergedGraph, sources, targets);
				break;
			case ONE_TO_ONE:
				makeOneToOne(mergedGraph, edge, sources, targets);
				break;
			default:
				if (sources.size() == 1 && targets.size() == 1) {
					mergedGraph.addEdge(sources.get(0), targets.get(0));
				} else {
					logEdgeConnectionWarning(edge, sources, targets);
				}
				break;
		}
	}

	/**
	 * Logs a warning if the edge connection cannot be made.
	 *
	 * @param edge the edge
	 * @param sources the source vertices
	 * @param targets the target vertices
	 */
	private void logEdgeConnectionWarning(final Edge2d edge, final List<?> sources, final List<?> targets) {
		LOG.warn("::: {} = {} {} / {} ||| {}", sources.size(), targets.size(), edge.getRelation(), edge.getSource(), edge.getTarget());
		LOG.trace("Got {} = {} / {} = {}", edge.getSource().getName(), sources, edge.getTarget().getName(), targets);
		LOG.trace("====================");
	}

	/**
	 * Exports the graph to a DOT file if debug logging is enabled.
	 *
	 * @param graph the graph to export
	 * @param filename the filename to export to
	 */
	private <U> void exportGraphIfDebugEnabled(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final String filename) {
		if (LOG.isDebugEnabled()) {
			exportGraph(graph, filename);
		}
	}

	/**
	 * Exports the graph to a DOT file.
	 *
	 * @param graph the graph to export
	 * @param filename the filename to export to
	 */
	private static <U> void exportGraph(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final String filename) {
		final DOTExporter<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> exporter = new DOTExporter<>(GraphTools::toDotName);
		try (final FileOutputStream out = new FileOutputStream(filename)) {
			exporter.exportGraph(graph, out);
		} catch (final IOException e) {
			LOG.trace("Error in graph export", e);
		}
	}

	/**
	 * Creates a one-to-one relationship between the source and target vertices.
	 *
	 * @param graph the graph
	 * @param edge the edge
	 * @param sources the source vertices
	 * @param targets the target vertices
	 */
	private static <U> void makeOneToOne(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final Edge2d edge, final List<VirtualTaskV3<U>> sources, final List<VirtualTaskV3<U>> targets) {
		sources.forEach(source -> {
			final Optional<VirtualTaskV3<U>> target = findMatchingTarget(source, edge, targets);
			target.ifPresentOrElse(
				t -> graph.addEdge(source, t),
				() -> LOG.warn("One to one of ({} / {} ) => Could not find: {} in {}", namedVertex(edge.getSource()), namedVertex(edge.getTarget()), source, targets)
			);
		});
	}

	/**
	 * Finds a matching target vertex for the given source vertex.
	 *
	 * @param source the source vertex
	 * @param edge the edge
	 * @param targets the target vertices
	 * @return the matching target vertex, if found
	 */
	private static <U> Optional<VirtualTaskV3<U>> findMatchingTarget(final VirtualTaskV3<U> source, final Edge2d edge, final List<VirtualTaskV3<U>> targets) {
		return targets.stream()
				.filter(target -> target.getType() == edge.getTarget().getType())
				.filter(target -> target.getName().equals(edge.getTarget().getName()))
				.filter(target -> target.getRank() == source.getRank())
				.findFirst();
	}

	/**
	 * Creates a one-to-many relationship between the source and target vertices.
	 *
	 * @param graph the graph
	 * @param sources the source vertices
	 * @param targets the target vertices
	 */
	private static <U> void makeOneToMany(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final List<VirtualTaskV3<U>> sources, final List<VirtualTaskV3<U>> targets) {
		if (sources.size() != 1) {
			LOG.debug("ERROR: ONE TO MANY but src is {}", sources.size());
			return;
		}
		final VirtualTaskV3<U> source = sources.get(0);
		targets.forEach(target -> graph.addEdge(source, target));
	}

	/**
	 * Gets all vertices from the graph that match the given name and type, excluding the specified vertices.
	 *
	 * @param graph the graph
	 * @param name the name to match
	 * @param type the type to match
	 * @param exclude the vertices to exclude
	 * @return the matching vertices
	 */
	private static <U> List<VirtualTaskV3<U>> getAll(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> graph, final String name, final Class<?> type, final List<VirtualTaskV3<U>> exclude) {
		return graph.vertexSet().stream()
				.filter(vertex -> !exclude.contains(vertex))
				.filter(vertex -> vertex.getType() == type)
				.filter(vertex -> vertex.getName().startsWith(name))
				.toList();
	}

	/**
	 * Returns a string representation of the named vertex.
	 *
	 * @param vertex the vertex
	 * @return the string representation
	 */
	private static String namedVertex(final Vertex2d vertex) {
		return vertex.getType().getSimpleName() + "-" + vertex.getName();
	}
}
