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

import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.service.ImplementationService;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3Impl;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 * This class is responsible for planning and executing orchestration tasks.
 * It implements the Planner interface and provides methods to implement and execute orchestration plans.
 *
 * @param <U> the type of the units of work
 */
@Service
public class PlannerImpl<U> implements Planner<U> {

	private static final Logger LOG = LoggerFactory.getLogger(PlannerImpl.class);

	private final ImplementationService<U> implementationService;
	private final ManoExecutor<U> executorService;
	private final List<PostPlanRunner<U>> postPlanRunner;

	/**
	 * Constructs a new PlannerImpl with the specified services and runners.
	 *
	 * @param implementationService the implementation service
	 * @param executorService the executor service
	 * @param postPlanRunner the list of post-plan runners
	 */
	public PlannerImpl(final ImplementationService<U> implementationService, final ManoExecutor<U> executorService, final List<PostPlanRunner<U>> postPlanRunner) {
		this.implementationService = implementationService;
		this.executorService = executorService;
		this.postPlanRunner = postPlanRunner;
	}

	/**
	 * Implements the given pre-execution graph and returns the resulting execution graph.
	 *
	 * @param g the pre-execution graph
	 * @return the execution graph
	 */
	@Override
	public ExecutionGraph implement(final PreExecutionGraphV3<U> g) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng = createImplementation(((PreExecutionGraphV3Impl) g).getGraph());
		logGraphCreation(ng);
		return new ExecutionGraphImplV3<>(ng);
	}

	/**
	 * Logs the creation of the graph if debug logging is enabled.
	 *
	 * @param ng the graph to log
	 */
	private void logGraphCreation(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Create graph:");
			GraphTools.dumpV3(ng);
		}
	}

	/**
	 * Creates an implementation graph from the given virtual task graph.
	 *
	 * @param gf the virtual task graph
	 * @return the implementation graph
	 */
	private ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createImplementation(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> gf) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng = createEmptyGraph();
		addVerticesToGraph(gf, ng);
		connectGraphEdges(gf, ng);
		return ng;
	}

	/**
	 * Creates an empty implementation graph.
	 *
	 * @return the empty implementation graph
	 */
	private ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createEmptyGraph() {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng = (ListenableGraph) (Object) new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		ng.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		return ng;
	}

	/**
	 * Adds vertices from the virtual task graph to the implementation graph.
	 *
	 * @param gf the virtual task graph
	 * @param ng the implementation graph
	 */
	private void addVerticesToGraph(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> gf, final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng) {
		gf.vertexSet().forEach(x -> {
			final SystemBuilder<U> db = (SystemBuilder<U>) implementationService.getTargetSystem(x);
			x.setSystemBuilder(db);
			db.getVertexV3().forEach(ng::addVertex);
		});
	}

	/**
	 * Connects edges from the virtual task graph to the implementation graph.
	 *
	 * @param gf the virtual task graph
	 * @param ng the implementation graph
	 */
	private void connectGraphEdges(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> gf, final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng) {
		gf.edgeSet().forEach(x -> {
			final VirtualTaskV3<U> src = x.getSource();
			final SystemBuilder<U> vsrc = src.getSystemBuilder();
			final VirtualTaskV3<U> dst = x.getTarget();
			final SystemBuilder<U> vdst = dst.getSystemBuilder();
			vsrc.getVertexV3().forEach(y -> {
				final UnitOfWorkV3<U> newSrc = find(ng, y);
				vdst.getVertexV3().forEach(z -> {
					final UnitOfWorkV3<U> newDst = find(ng, z);
					ng.addEdge(newSrc, newDst);
				});
			});
		});
	}

	/**
	 * Finds a vertex in the implementation graph.
	 *
	 * @param ng the implementation graph
	 * @param y the vertex to find
	 * @return the found vertex
	 */
	private UnitOfWorkV3<U> find(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng, final UnitOfWorkV3<U> y) {
		return ng.vertexSet().stream().filter(x -> x == y).findFirst().orElseThrow();
	}

	/**
	 * Executes the given implementation graph with the specified listener.
	 *
	 * @param implementation the implementation graph
	 * @param listener the execution listener
	 * @return the execution results
	 */
	@Override
	public OrchExecutionResults<U> execute(final ExecutionGraph implementation, final OrchExecutionListener<U> listener) {
		@SuppressWarnings("unchecked")
		final ExecutionGraphImplV3<U> impl = (ExecutionGraphImplV3<U>) implementation;
		final OrchExecutionResults<U> finalDelete = executeDeletePhase(impl, listener);
		if (!finalDelete.getErrored().isEmpty()) {
			return finalDelete;
		}
		return executeCreatePhase(impl, listener, finalDelete);
	}

	/**
	 * Executes the delete phase of the implementation graph.
	 *
	 * @param impl the implementation graph
	 * @param listener the execution listener
	 * @return the execution results of the delete phase
	 */
	private OrchExecutionResults<U> executeDeletePhase(final ExecutionGraphImplV3<U> impl, final OrchExecutionListener<U> listener) {
		postPlanRunner.forEach(x -> x.runDeletePost(impl.getGraph()));
		final ExecutionResults<UnitOfWorkV3<U>, String> deleteRes = execDelete(impl.getGraph(), listener);
		return convertResults(deleteRes);
	}

	/**
	 * Executes the create phase of the implementation graph.
	 *
	 * @param impl the implementation graph
	 * @param listener the execution listener
	 * @param finalDelete the execution results of the delete phase
	 * @return the execution results of the create phase
	 */
	private OrchExecutionResults<U> executeCreatePhase(final ExecutionGraphImplV3<U> impl, final OrchExecutionListener<U> listener, final OrchExecutionResults<U> finalDelete) {
		postPlanRunner.forEach(x -> x.runCreatePost(impl.getGraph()));
		final ExecutionResults<UnitOfWorkV3<U>, String> res = execCreate(impl, listener);
		finalDelete.addAll(convertResults(res));
		return convertResults(res);
	}

	/**
	 * Executes the delete tasks on the given graph with the specified listener.
	 *
	 * @param g the graph
	 * @param listener the execution listener
	 * @return the execution results of the delete tasks
	 */
	private ExecutionResults<UnitOfWorkV3<U>, String> execDelete(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final OrchExecutionListener<U> listener) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> rev = GraphTools.revert(g);
		final Context3dNetFlow<U> context = new Context3dNetFlow<>(g, List.of());
		return executorService.execute(rev, new DeleteTaskProvider<>(context, listener));
	}

	/**
	 * Executes the create tasks on the given implementation graph with the specified listener.
	 *
	 * @param impl the implementation graph
	 * @param listener the execution listener
	 * @return the execution results of the create tasks
	 */
	private ExecutionResults<UnitOfWorkV3<U>, String> execCreate(final ExecutionGraphImplV3<U> impl, final OrchExecutionListener<U> listener) {
		final Context3dNetFlow<U> context = new Context3dNetFlow<>(impl.getGraph(), impl.getGlobal());
		return executorService.execute(impl.getGraph(), new CreateTaskProvider<>(context, listener));
	}

	/**
	 * Converts the execution results to orchestration execution results.
	 *
	 * @param res the execution results
	 * @return the orchestration execution results
	 */
	private OrchExecutionResults<U> convertResults(final ExecutionResults<UnitOfWorkV3<U>, String> res) {
		final List<OrchExecutionResultImpl<U>> all = res.getAll().stream().map(this::convert).toList();
		return new OrchExecutionResultsImpl<>(all);
	}

	/**
	 * Converts an execution result to an orchestration execution result.
	 *
	 * @param res the execution result
	 * @return the orchestration execution result
	 */
	private OrchExecutionResultImpl<U> convert(final ExecutionResult<UnitOfWorkV3<U>, String> res) {
		final VirtualTaskV3<U> t = res.getId().getVirtualTask();
		t.getStatus();
		return new OrchExecutionResultImpl<>(res.getId(), t.getStatus(), res.getResult());
	}

}
