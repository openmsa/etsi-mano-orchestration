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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.dexecutor.core.task.ExecutionResults;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.service.ImplementationService;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWork;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkVertexListenerV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3Impl;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivity;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskConnectivityV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@Service
public class PlannerImpl<P, U, W> implements Planner<P, U, W> {

	private static final Logger LOG = LoggerFactory.getLogger(PlannerImpl.class);

	private final Map<Class<? extends Node>, PlanContributor> contributors;

	private final ImplementationService implementationService;

	private final ManoExecutor<U> executorService;

	private final List<PostPlanRunner> postPlanRunner;

	public PlannerImpl(final List<PlanContributor> contributorRaw, final ImplementationService implementationService, final ManoExecutor<U> executorService,
			final List<PostPlanRunner> postPlanRunner) {
		this.contributors = contributorRaw.stream()
				.collect(Collectors.toMap(
						PlanContributor::getNode,
						Function.identity(),
						(u, v) -> v,
						LinkedHashMap::new));
		this.implementationService = implementationService;
		this.executorService = executorService;
		this.postPlanRunner = postPlanRunner;
	}

	@Override
	public PreExecutionGraphV3<W> makePlan(final Bundle bundle, final List<Class<? extends Node>> planConstituent, final P parameters) {
		return null;
	}

	private void rebuildConnectivity(final ListenableGraph<VirtualTask<U>, VirtualTaskConnectivity<U>> graph) {
		graph.vertexSet().forEach(x -> x.getNameDependencies().forEach(y -> {
			final VirtualTask<U> dep = findProducer(y, graph);
			if (null == dep) {
				LOG.info("Single(dep): {} producer not found {}", x.getName(), y);
				graph.addVertex(x);
			} else {
				LOG.debug("Add edge(dep): {} <-> {}", dep.getName(), x.getName());
				graph.addEdge(dep, x);
			}
		}));
	}

	@Override
	public ExecutionGraph implement(final PreExecutionGraphV3<U> g) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng = createImplementation(((PreExecutionGraphV3Impl) g).getGraph());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Create graph:");
			GraphTools.dumpV3(ng);
		}
		return new ExecutionGraphImplV3(ng, null);
	}

	private ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> createImplementation(final ListenableGraph<VirtualTaskV3<U>, VirtualTaskConnectivityV3<U>> gf) {
		final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng = (ListenableGraph) (Object) new DefaultListenableGraph<>(new DirectedAcyclicGraph<>(ConnectivityEdge.class));
		ng.addGraphListener(new UnitOfWorkVertexListenerV3<>());
		// First resolve implementation.
		gf.vertexSet().forEach(x -> {
			final SystemBuilder<U> db = implementationService.getTargetSystem(x);
			x.setSystemBuilder(db);
			LOG.debug("Implementing {}", db);
			db.getVertexV3().forEach(ng::addVertex);
		});
		// Connect everything.
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
		return ng;
	}

	private UnitOfWorkV3<U> find(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> ng, final UnitOfWorkV3<U> y) {
		return ng.vertexSet().stream().filter(x -> x == y).findFirst().orElseThrow();
	}

	private Optional<UnitOfWork<U>> findOutgoingOf(final NamedDependency y, final ListenableGraph<UnitOfWork<U>, ConnectivityEdge<UnitOfWork<U>>> ng) {
		return ng.vertexSet().stream().filter(x -> x.getNamedProduced().stream().anyMatch(z -> z.match(y))).findFirst();
	}

	private static <U> VirtualTask<U> findProducer(final NamedDependency namedDependency, final ListenableGraph<? extends VirtualTask<U>, VirtualTaskConnectivity<U>> gf) {
		return gf.vertexSet().stream().filter(x -> x.getNamedProduced().stream()
				.anyMatch(namedDependency::match))
				.findAny()
				.orElse(null);
	}

	@Override
	public OrchExecutionResults<U> execute(final ExecutionGraph implementation, final Context3d context, final OrchExecutionListener<U> listener) {
		final ExecutionGraphImplV3<U> impl = (ExecutionGraphImplV3<U>) implementation;
		// Execute delete.

//		postPlanRunner.forEach(x -> x.runDeletePost(impl.getDeleteImplementation()));
//		final ExecutionResults<UnitOfWorkV3<U>, String> deleteRes = execDelete(impl.getDeleteImplementation(), context, listener);
//		final OrchExecutionResults<U> finalDelete = convertResults(deleteRes);
//		if (!deleteRes.getErrored().isEmpty()) {
//			return finalDelete;
//		}
		// Execute create.
		postPlanRunner.forEach(x -> x.runCreatePost(impl.getCreateImplementation()));
		final ExecutionResults<UnitOfWorkV3<U>, String> res = execCreate(impl.getCreateImplementation(), context, listener);
//		finalDelete.addAll(convertResults(res));
		return convertResults(res);
	}

	private ExecutionResults<UnitOfWorkV3<U>, String> execDelete(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> deleteImplementation, final Context3d context, final OrchExecutionListener<U> listener) {
		// TODO Auto-generated method stub
		return null;
	}

	private ExecutionResults<UnitOfWorkV3<U>, String> execCreate(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final Context3d context, final OrchExecutionListener<U> listener) {
		return executorService.execute(g, new CreateTaskProvider<>(context, listener));
	}

	private OrchExecutionResults<U> convertResults(final ExecutionResults<UnitOfWorkV3<U>, String> res) {
		final List<OrchExecutionResultImpl<U>> all = res.getAll().stream().map(x -> new OrchExecutionResultImpl<>(x.getId(), ResultType.valueOf(x.getStatus().toString()), x.getResult())).toList();
		return new OrchExecutionResultsImpl<>(all);
	}
//
//	private ExecutionResults<UnitOfWork<U>, String> execCreate(final ListenableGraph<UnitOfWork<U>, ConnectivityEdge<UnitOfWork<U>>> g, final Context context, final OrchExecutionListener<U> listener) {
//		return executorService.execute(g, new CreateTaskProvider<>(context, listener));
//	}
//
//	private ExecutionResults<UnitOfWork<U>, String> execDelete(final ListenableGraph<UnitOfWork<U>, ConnectivityEdge<UnitOfWork<U>>> g, final Context context, final OrchExecutionListener<U> listener) {
//		final ListenableGraph<UnitOfWork<U>, ConnectivityEdge<UnitOfWork<U>>> rev = GraphTools.revert(g);
//		return executorService.execute(rev, new DeleteTaskProvider<>(context, listener));
//	}

	@Override
	public ExecutionGraph implement(final PreExecutionGraph<U> gf) {
		// TODO Auto-generated method stub
		return null;
	}

}
