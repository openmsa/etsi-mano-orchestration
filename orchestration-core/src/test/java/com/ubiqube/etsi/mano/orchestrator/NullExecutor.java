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

import org.jgrapht.ListenableGraph;

import com.github.dexecutor.core.task.ExecutionResults;
import com.github.dexecutor.core.task.TaskProvider;
import com.ubiqube.etsi.mano.orchestrator.nodes.ConnectivityEdge;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;

public class NullExecutor implements ManoExecutor<Object> {

	@Override
	public ExecutionResults<UnitOfWorkV3<Object>, String> execute(final ListenableGraph<UnitOfWorkV3<Object>, ConnectivityEdge<UnitOfWorkV3<Object>>> g, final TaskProvider<UnitOfWorkV3<Object>, String> uowTaskProvider) {
		return new ExecutionResults<>();
	}

}
