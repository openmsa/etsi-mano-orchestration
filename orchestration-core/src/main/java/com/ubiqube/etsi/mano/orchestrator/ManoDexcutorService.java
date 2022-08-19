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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.ListenableGraph;
import org.springframework.stereotype.Service;

import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.DexecutorConfig;
import com.github.dexecutor.core.ExecutionConfig;
import com.github.dexecutor.core.task.ExecutionResults;
import com.github.dexecutor.core.task.TaskProvider;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

/**
 *
 * @author olivier
 *
 */
@Service
public class ManoDexcutorService<U> implements ManoExecutor<U> {

	@Override
	public ExecutionResults<UnitOfWorkV3<U>, String> execute(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final TaskProvider<UnitOfWorkV3<U>, String> uowTaskProvider) {
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final DexecutorConfig<UnitOfWorkV3<U>, String> config = new DexecutorConfig<>(executorService, uowTaskProvider);
		// What about config setExecutionListener.
		final DefaultDexecutor<UnitOfWorkV3<U>, String> executor = new DefaultDexecutor<>(config);
		addRoot(g, executor);
		g.edgeSet().forEach(x -> executor.addDependency(x.getSource(), x.getTarget()));

		final ExecutionResults<UnitOfWorkV3<U>, String> res = executor.execute(ExecutionConfig.TERMINATING);
		executorService.shutdown();
		return res;
	}

	private static <U> void addRoot(final ListenableGraph<UnitOfWorkV3<U>, ConnectivityEdge<UnitOfWorkV3<U>>> g, final DefaultDexecutor<UnitOfWorkV3<U>, String> executor) {
		g.vertexSet().forEach(x -> {
			if (g.incomingEdgesOf(x).isEmpty() && g.outgoingEdgesOf(x).isEmpty()) {
				executor.addIndependent(x);
			}
		});
	}
}
